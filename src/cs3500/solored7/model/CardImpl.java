package cs3500.solored.model.hw02;

/**
 * Concrete implementation of a card in the SoloRed game.
 * Each card has a color and a number.
 */
public class CardImpl implements Card {
  private final String color;
  private final int number;

  /**
   * Constructor to initialize a Card with a specific color and number.
   * @param color the color of the card
   * @param number the number of the card
   * @throws IllegalArgumentException if the color or number are invalid
   */
  public CardImpl(String color, int number) {
    if (!isValidColor(color) || !isValidNumber(number)) {
      throw new IllegalArgumentException("Invalid card color or number.");
    }
    this.color = color;
    this.number = number;
  }

  /**
   * Returns the color of the card.
   * @return the color of the card
   */
  public String getColor() {
    return color;
  }

  /**
   * Returns the number of the card.
   * @return the number of the card
   */
  public int getNumber() {
    return number;
  }

  /**
   * Checks if the given color is a valid color in the game.
   * @param color the color to check
   * @return true if the color is valid, false otherwise
   */
  private boolean isValidColor(String color) {
    return color.equals("R") || color.equals("O") || color.equals("B")
            || color.equals("I") || color.equals("V");
  }

  /**
   * Checks if the given number is a valid number in the game.
   * @param number the number to check
   * @return true if the number is between 0 and 7, false otherwise
   */
  private boolean isValidNumber(int number) {
    return number >= 0 && number <= 7; // Allows 0 for special "non-playable" cards.
  }

  @Override
  public String toString() {
    if (number == 0) {
      return color;
    }
    return color + number;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    CardImpl card = (CardImpl) obj;
    return number == card.number && color.equals(card.color);
  }

  @Override
  public int hashCode() {
    int result = color.hashCode();
    result = 31 * result + number;
    return result;
  }
}