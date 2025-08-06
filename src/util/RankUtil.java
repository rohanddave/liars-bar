package util;
import java.util.Random;

import model.game.Rank;

public class RankUtil {
  private static final Random RANDOM = new Random();

  public static Rank getRandomRank() {
    Rank[] ranks = Rank.values();
    return ranks[RANDOM.nextInt(ranks.length)];
  }
}