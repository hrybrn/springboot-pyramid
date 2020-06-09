package com.hrybrn.pyramid.data;

import com.jmethods.catatumbo.Embeddable;
import lombok.Data;

import java.util.List;

@Data
@Embeddable
public class Hand {
  private List<Card> cards;
}
