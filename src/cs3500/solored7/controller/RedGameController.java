package cs3500.solored.controller;

import cs3500.solored.model.hw02.Card;
import cs3500.solored.model.hw02.RedGameModel;
import java.util.List;

/**
 * Interface for the SoloRed game controller.
 */
public interface RedGameController {

  /**
   * Plays a new game of Solo Red using the provided model and settings.
   *
   * @param model the model for the SoloRed game
   * @param deck the deck of cards to use
   * @param shuffle whether to shuffle the deck
   * @param numPalettes number of palettes in the game
   * @param handSize size of the player's hand
   * @param <C> the type of cards used in the game
   * @throws IllegalArgumentException if the model is null
   * @throws IllegalStateException if input or output fails
   */
  <C extends Card> void playGame(RedGameModel<C> model, List<C> deck, boolean shuffle,
                                 int numPalettes, int handSize);
}