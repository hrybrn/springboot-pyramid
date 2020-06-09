package com.hrybrn.pyramid.data;

import com.jmethods.catatumbo.Entity;
import com.jmethods.catatumbo.Identifier;
import com.jmethods.catatumbo.Ignore;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Entity
public class Game {
  @Identifier
  private String id;

  private String name;
  private String hostId;
  private List<Card> deck;
  private List<String> playerIds;
  private Map<String, Hand> hands;

  @Ignore
  private List<User> players;
}
