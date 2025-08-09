package com.tfc.liarsbar.model.game;

/**
 * This class represents a revolver with n bullet capacity and m bullets.
 * Can shoot a bullet.
 * Can reset the revolver.
 */
public interface Revolver {
  /**
   * Shoots revolver.
   * @return true if bullet false otherwise.
   */
  boolean shoot();

  /**
   * Resets the revolver.
   */
  void reset();

  int getCurrentIndex();
}
