package com.hrybrn.pyramid.game;

import com.hrybrn.pyramid.DatastoreService;
import com.hrybrn.pyramid.UserService;
import com.hrybrn.pyramid.data.Card;
import com.hrybrn.pyramid.data.Game;
import com.hrybrn.pyramid.data.GameState;
import com.hrybrn.pyramid.data.Hand;
import com.jmethods.catatumbo.DatastoreTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GameService {
  private final DatastoreService datastoreService;
  private final UserService userService;

  public Game newGame(String name, String hostID) {
    Game game = new Game();
    game.setId(UUID.randomUUID().toString());
    game.setName(name);
    game.setHostId(hostID);
    game.setDeck(shuffledDeck());
    game.setPlayerIds(List.of(hostID));

    List<Card> cards = game.getDeck();
    int starting = cards.size() - 5;
    int ending = starting + 4;

    List<Card> cardsInHand = cards.subList(starting, ending);

    Hand hand = new Hand();
    hand.setCards(cardsInHand);

    Map<String, Hand> hands = Map.of(hostID, hand);

    game.setHands(hands);

    datastoreService.getEntityManager().insert(game);

    game.setPlayers(userService.getUsers(game.getPlayerIds()));
    return game;
  }

  public Game joinGame(String gameId, String userId) {
    DatastoreTransaction transaction = datastoreService.getEntityManager().newTransaction();
    Game game = transaction.load(Game.class, gameId);

    List<String> playerIds = game.getPlayerIds();

    // we can't support more than 8 players
    if (playerIds.size() == 8) {
      return null;
    }

    // player can't be in the same game twice
    if (game.getHands().get(userId) != null) {
      return null;
    }

    playerIds.add(userId);
    game.setPlayerIds(playerIds);

    List<Card> cards = game.getDeck();
    int starting = cards.size() - 4 * playerIds.size() - 1;
    int ending = starting + 4;

    List<Card> cardsInHand = cards.subList(starting, ending);

    Hand hand = new Hand();
    hand.setCards(cardsInHand);

    game.getHands().put(userId, hand);

    transaction.update(game);
    transaction.commit();

    game.setPlayers(userService.getUsers(game.getPlayerIds()));
    return game;
  }

  public Hand getHand(String gameId, String userId) {
    Game game = datastoreService.getEntityManager().load(Game.class, gameId);

    if (game == null) {
      return null;
    }

    return game.getHands().get(userId);
  }

  public Card flipCard(String gameId, String hostId) {
    DatastoreTransaction transaction = datastoreService.getEntityManager().newTransaction();
    Game game = transaction.load(Game.class, gameId);

    // only host can flip cards
    if (!hostId.equals(game.getHostId())) {
      return null;
    }

    // check game isn't finished
    List<Card> deck = game.getDeck();
    long amountOfCardsRevealed = deck.stream()
        .filter(Card::getRevealed)
        .count();

    if (amountOfCardsRevealed >= 21) {
      return null;
    }

    Optional<Card> optionalCard = game.getDeck().stream()
        .filter(card -> !card.getRevealed())
        .findFirst();

    // we will always find a card
    if (optionalCard.isEmpty()) {
      return null;
    }

    Card card = optionalCard.get();
    card.setRevealed(true);

    transaction.update(game);
    transaction.commit();

    return card;
  }

  public GameState getState(String gameId) {
    Game game = datastoreService.getEntityManager().load(Game.class, gameId);
    List<Card> flippedCards = game.getDeck().stream()
        .filter(Card::getRevealed)
        .collect(Collectors.toList());

    return new GameState(flippedCards);
  }

  private List<Card> shuffledDeck() {
    String[] SUITS = {
        "Clubs", "Diamonds", "Hearts", "Spades"
    };

    String[] RANKS = {
        "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"
    };

    // initialize deck
    int n = SUITS.length * RANKS.length;
    Card[] deck = new Card[n];
    for (int i = 0; i < RANKS.length; i++) {
      for (int j = 0; j < SUITS.length; j++) {
        Card card = new Card();
        card.setRevealed(false);
        card.setRank(RANKS[i]);
        card.setSuit(SUITS[j]);

        deck[SUITS.length*i + j] = card;
      }
    }

    // shuffle
    for (int i = 0; i < n; i++) {
      int r = i + (int) (Math.random() * (n-i));
      Card temp = deck[r];
      deck[r] = deck[i];
      deck[i] = temp;
    }

    return List.of(deck);
  }
}
