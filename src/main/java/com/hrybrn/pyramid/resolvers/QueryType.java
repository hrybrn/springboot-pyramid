package com.hrybrn.pyramid.resolvers;

import com.hrybrn.pyramid.UserService;
import com.hrybrn.pyramid.data.GameState;
import com.hrybrn.pyramid.data.Hand;
import com.hrybrn.pyramid.data.User;

import com.hrybrn.pyramid.game.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLQueryResolver;

import java.util.List;

@Component
@RequiredArgsConstructor
public class QueryType implements GraphQLQueryResolver {
  private final UserService userService;
  private final GameService gameService;

  public User user(String id) {
    return userService.getUser(id);
  }

  public List<User> players(List<String> ids) {
    return userService.getUsers(ids);
  }

  public Hand hand(String userId, String userSecret, String gameId) {
    if (!userService.authenticate(userId, userSecret)) {
      return null;
    }

    return gameService.getHand(gameId, userId);
  }

  public GameState state(String gameId) {
    return gameService.getState(gameId);
  }
}
