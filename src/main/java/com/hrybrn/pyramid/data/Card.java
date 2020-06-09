package com.hrybrn.pyramid.data;

import com.jmethods.catatumbo.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class Card {
  private String suit;
  private String rank;
  private Boolean revealed;
}
