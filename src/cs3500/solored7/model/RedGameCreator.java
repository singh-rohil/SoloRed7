package cs3500.solored.model.hw04;

import cs3500.solored.model.hw02.SoloRedGameModel;
import cs3500.solored.model.hw02.CardImpl;
import cs3500.solored.model.hw02.RedGameModel;

/**
 * Factory class for creating different game models in the SoloRed game.
 */
public class RedGameCreator {

  /**
   * Creates a game model based on the provided game type.
   *
   * @param type the type of game to create (BASIC or ADVANCED).
   * @return the created game model.
   * @throws IllegalArgumentException if the game type is unknown.
   */
  public static RedGameModel<CardImpl> createGame(GameType type) {
    switch (type) {
      case BASIC:
        return new SoloRedGameModel();
      case ADVANCED:
        return new AdvancedSoloRedGameModel();
      default:
        throw new IllegalArgumentException("Unknown game type");
    }
  }

  /**
   * Enum representing the different types of games that can be created.
   */
  public enum GameType {
    BASIC, ADVANCED
  }
}