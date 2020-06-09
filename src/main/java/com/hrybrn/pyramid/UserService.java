package com.hrybrn.pyramid;

import com.hrybrn.pyramid.data.CurrentUser;
import com.hrybrn.pyramid.data.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserService {
  private final DatastoreService datastoreService;

  public CurrentUser registerUser(String name) {
    CurrentUser currentUser = new CurrentUser();
    currentUser.setName(name);
    currentUser.setId(UUID.randomUUID().toString());
    currentUser.setSecret(UUID.randomUUID().toString());
    datastoreService.getEntityManager().insert(currentUser);
    return currentUser;
  }

  public CurrentUser updateUser(String id, String name) {
    CurrentUser currentUser = datastoreService.getEntityManager().load(CurrentUser.class, id);
    currentUser.setName(name);
    datastoreService.getEntityManager().update(currentUser);
    return currentUser;
  }

  public User getUser(String id) {
    return datastoreService.getEntityManager().load(User.class, id);
  }

  public List<User> getUsers(List<String> ids) {
    return datastoreService.getEntityManager().loadByName(User.class, ids);
  }

  public Boolean authenticate(String id, String secret) {
    CurrentUser currentUser = datastoreService.getEntityManager().load(CurrentUser.class, id);
    return authenticate(currentUser, secret);
  }

  private Boolean authenticate(CurrentUser currentUser, String secret) {
    return secret != null && currentUser != null && secret.equals(currentUser.getSecret());
  }
}
