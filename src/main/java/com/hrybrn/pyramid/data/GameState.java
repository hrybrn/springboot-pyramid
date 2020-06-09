package com.hrybrn.pyramid.data;

import lombok.Data;

import java.util.List;

@Data
public class GameState {
  private final List<Card> flippedCards;
}
