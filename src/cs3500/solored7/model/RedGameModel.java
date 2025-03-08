package cs3500.solored.model.hw02;

import java.util.List;

/**
 * Behaviors for a solo game of RedSeven.
 * The game consists of four structures:
 * <ul>
 *   <li>A deck of cards to draw from</li>
 *   <li>A hand to play from</li>
 *   <li>Palettes to play to (number varies based on the game)</li>
 *   <li>A canvas that dictates the winning rule</li>
 * </ul>
 * The goal of the game is to use all the cards in the deck
 * while ensuring exactly one palette is winning each round.
 *
 * @param <C> the type of cards used
 */
public interface RedGameModel<C extends Card> {

  // Operations

  /**
   * Plays the given card from the hand to the specified palette.
   * The card is removed from the hand and placed at the far right
   * end of the palette.
   *
   * @param paletteIdx    a 0-index number representing which palette to play to
   * @param cardIdxInHand a 0-index number representing the card to play from the hand
   * @throws IllegalStateException    if the game has not started or the game is over
   * @throws IllegalArgumentException if paletteIdx < 0 or more than the number of palettes
   * @throws IllegalArgumentException if cardIdxInHand < 0 or greater/equal to the number of
   *                                  cards in hand
   * @throws IllegalStateException    if the palette referred to by paletteIdx is winning
   */
  void playToPalette(int paletteIdx, int cardIdxInHand);

  /**
   * Plays the given card from the hand to the canvas.
   * This changes the rules of the game for all palettes.
   * The method can only be called once per turn.
   *
   * @param cardIdxInHand a 0-index number representing the card to play from the hand
   * @throws IllegalStateException    if the game has not started or the game is over
   * @throws IllegalArgumentException if cardIdxInHand < 0 or greater/equal to the number of
   *                                  cards in hand
   * @throws IllegalStateException    if this method was already called once in a given turn
   * @throws IllegalStateException    if there is exactly one card in hand
   */
  void playToCanvas(int cardIdxInHand);

  /**
   * Draws cards from the deck until the hand is full
   * OR until the deck is empty, whichever occurs first. Newly drawn cards
   * are added to the end of the hand (far-right conventionally).
   * SIDE-EFFECT: Allows the player to play to the canvas again.
   *
   * @throws IllegalStateException if the game has not started or the game is over
   */
  void drawForHand();

  /**
   * Starts the game with the given options. The deck given is used
   * to set up the palettes and hand. Modifying the deck given to this method
   * will not modify the game state in any way.
   *
   * @param deck        the cards used to set up and play the game
   * @param shuffle     whether the deck should be shuffled prior to setting up the game
   * @param numPalettes number of palettes in the game
   * @param handSize    the maximum number of cards allowed in the hand
   * @throws IllegalStateException    if the game has already started or is over
   * @throws IllegalArgumentException if numPalettes < 2 or handSize <= 0
   * @throws IllegalArgumentException if deck's size is not large enough to set up the game
   * @throws IllegalArgumentException if deck has non-unique cards or contains null cards
   */
  void startGame(List<C> deck, boolean shuffle, int numPalettes, int handSize);

  // Observations

  /**
   * Returns the number of cards remaining in the deck used in the game.
   *
   * @return the number of cards in the deck
   * @throws IllegalStateException if the game has not started
   */
  int numOfCardsInDeck();

  /**
   * Returns the number of palettes in the running game.
   *
   * @return the number of palettes in the game
   * @throws IllegalStateException if the game has not started
   */
  int numPalettes();

  /**
   * Returns the index of the winning palette in the game.
   *
   * @return the 0-based index of the winning palette
   * @throws IllegalStateException if the game has not started
   */
  int winningPaletteIndex();

  /**
   * Returns if the game is over as specified by the implementation.
   *
   * @return true if the game has ended and false otherwise
   * @throws IllegalStateException if the game has not started
   */
  boolean isGameOver();

  /**
   * Returns if the game is won by the player as specified by the implementation.
   *
   * @return true if the game has been won, false if it has not
   * @throws IllegalStateException if the game has not started or the game is not over
   */
  boolean isGameWon();

  /**
   * Returns a copy of the hand in the game. This means modifying the returned list
   * or the cards in the list has no effect on the game.
   *
   * @return a new list containing the cards in the player's hand in the same order as in the
   *         current state of the game.
   * @throws IllegalStateException if the game has not started
   */
  List<C> getHand();

  /**
   * Returns a copy of the specified palette. This means modifying the returned list
   * or the cards in the list has no effect on the game.
   *
   * @param paletteNum 0-based index of a particular palette
   * @return a new list containing the cards in the specified palette in the same order
   *         as in the current state of the game.
   * @throws IllegalStateException    if the game has not started
   * @throws IllegalArgumentException if paletteNum < 0 or greater than or equal to the number of
   *                                  palettes
   */
  List<C> getPalette(int paletteNum);

  /**
   * Returns the top card of the canvas. Modifying this card has no effect on the game.
   *
   * @return the top card of the canvas
   * @throws IllegalStateException if the game has not started
   */
  C getCanvas();

  /**
   * Gets a new list of all cards that can be used to play the game.
   * Editing this list should have no effect on the game itself.
   * Repeated calls to this method should produce a list of cards in the same order.
   * Modifying the cards in this list should have no effect on any returned list
   * or the game itself.
   *
   * @return a new list of all possible cards that can be used for the game
   */
  List<C> getAllCards();
}