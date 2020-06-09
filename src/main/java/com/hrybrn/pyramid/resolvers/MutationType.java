package com.hrybrn.pyramid.resolvers;

import com.hrybrn.pyramid.UserService;
import com.hrybrn.pyramid.data.Card;
import com.hrybrn.pyramid.data.CurrentUser;
import com.hrybrn.pyramid.data.Game;
import com.hrybrn.pyramid.game.GameService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MutationType implements GraphQLMutationResolver {
  private final GameService gameService;
  private final UserService userService;

  public CurrentUser registerUser(String name) {
    return userService.registerUser(name);
  }

  public CurrentUser updateUser(String id, String name, String secret) {
    if (!userService.authenticate(id, secret)) {
      return null;
    }
    return userService.updateUser(id, name);
  }

  public Game registerGame(String name, String hostId, String hostSecret) {
    if (!userService.authenticate(hostId, hostSecret)) {
      return null;
    }
    return gameService.newGame(name, hostId);
  }

  public Game joinGame(String gameId, String userId, String userSecret) {
    if (!userService.authenticate(userId, userSecret)) {
      return null;
    }

    return gameService.joinGame(gameId, userId);
  }

  public Card flipCard(String gameId, String hostId, String hostSecret) {
    if (!userService.authenticate(hostId, hostSecret)) {
      return null;
    }

    return gameService.flipCard(gameId, hostId);
  }
}
