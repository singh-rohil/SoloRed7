package cs3500.solored;

import cs3500.solored.model.hw02.RedGameModel;
import cs3500.solored.model.hw02.CardImpl;
import cs3500.solored.model.hw04.RedGameCreator;
import cs3500.solored.controller.SoloRedTextController;

import java.io.InputStreamReader;
import java.util.List;

/**
 * Main class for the SoloRed game. Starts the game based on provided arguments.
 */
public final class SoloRed {

  /**
   * Main method to run the SoloRed game.
   *
   * @param args command-line arguments.
   *             First argument is the game type ('basic' or 'advanced').
   *             Optional second and third arguments are the number of palettes and hand size.
   * @throws IllegalArgumentException if the game type is invalid or missing.
   */
  public static void main(String[] args) {
    if (args.length < 1) {
      throw new IllegalArgumentException("Game type required (basic or advanced)");
    }

    String gameTypeStr = args[0];
    RedGameCreator.GameType gameType;
    switch (gameTypeStr.toLowerCase()) {
      case "basic":
        gameType = RedGameCreator.GameType.BASIC;
        break;
      case "advanced":
        gameType = RedGameCreator.GameType.ADVANCED;
        break;
      default:
        throw new IllegalArgumentException("Invalid game type. Choose 'basic' or 'advanced'");
    }

    int numPalettes = (args.length > 1) ? Integer.parseInt(args[1]) : 4;
    int maxHandSize = (args.length > 2) ? Integer.parseInt(args[2]) : 7;

    RedGameModel<CardImpl> game = RedGameCreator.createGame(gameType);

    List<CardImpl> deck = game.getAllCards();

    SoloRedTextController controller = new SoloRedTextController(
            new InputStreamReader(System.in), System.out);
    try {
      controller.playGame(game, deck, true, numPalettes, maxHandSize);
    } catch (IllegalArgumentException ignored) {
    }
  }
}