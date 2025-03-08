package cs3500.solored.view.hw02;

import cs3500.solored.model.hw02.Card;
import cs3500.solored.model.hw02.RedGameModel;

import java.io.IOException;
import java.util.List;

/**
 * Concrete view implementation for displaying the state of the SoloRed game.
 */
public class SoloRedGameTextView implements RedGameView {
  private final RedGameModel<?> model;
  private final Appendable appendable;

  /**
   * Constructs a SoloRedGameTextView with the given model and appendable.
   *
   * @param model      the model of the SoloRed game
   * @param appendable the appendable to transmit output
   * @throws IllegalArgumentException if the model or appendable is null
   */
  public SoloRedGameTextView(RedGameModel<?> model, Appendable appendable) {
    if (model == null || appendable == null) {
      throw new IllegalArgumentException("Model and Appendable cannot be null.");
    }
    this.model = model;
    this.appendable = appendable;
  }

  /**
   * Constructs a SoloRedGameTextView with the given model, using System.out as the default output.
   *
   * @param model the model of the SoloRed game
   * @throws IllegalArgumentException if the model is null
   */
  public SoloRedGameTextView(RedGameModel<?> model) {
    this(model, System.out);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    Card canvasCard = model.getCanvas();
    String canvasRepresentation = canvasCard.toString();
    String color = canvasRepresentation.substring(0, 1);
    sb.append("Canvas: ").append(color).append("\n");

    for (int i = 0; i < model.numPalettes(); i++) {
      if (i == model.winningPaletteIndex()) {
        sb.append("> P").append(i + 1).append(": ");
      } else {
        sb.append("P").append(i + 1).append(": ");
      }

      List<?> palette = model.getPalette(i);
      if (palette.isEmpty()) {
        sb.append("\n");
      } else {
        for (int j = 0; j < palette.size(); j++) {
          sb.append(palette.get(j).toString());
          if (j < palette.size() - 1) {
            sb.append(" ");
          }
        }
        sb.append("\n");
      }
    }

    // Display the hand
    sb.append("Hand: ");
    List<?> hand = model.getHand();
    if (hand.isEmpty()) {
      sb.append("");
    } else {
      for (int i = 0; i < hand.size(); i++) {
        sb.append(hand.get(i).toString());
        if (i < hand.size() - 1) {
          sb.append(" ");
        }
      }
    }
    return sb.toString();
  }

  @Override
  public void render() throws IOException {
    appendable.append(this.toString());
  }
}