package com.tfc.liarsbar.util;
import com.tfc.liarsbar.model.game.Rank;

import java.util.Random;

public class RankUtil {
  private static final Random RANDOM = new Random();

  public static Rank getRandomRank() {
    Rank[] ranks = Rank.values();
    return ranks[RANDOM.nextInt(ranks.length)];
  }
}