package cs3500.solored.model.hw02;

/**
 * Behaviors for a Card in the Game of RedSeven.
 * Any additional behaviors for cards must be made
 * creating a new interface that extends this one.
 */
public interface Card {

  /**
   * Prints the color and number of the card.
   * The colors are printed R, O, B, I, or V.
   * The numbers are printed as 1-7.
   * As an example, a blue 5 is printed as B5.
   *
   * @return a two character representation of the card
   */
  String toString();
}