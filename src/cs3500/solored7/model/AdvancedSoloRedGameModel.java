package cs3500.solored.model.hw04;

import cs3500.solored.model.hw02.SoloRedGameModel;

/**
 * Advanced version of the SoloRed game model, implementing additional game logic.
 */
public class AdvancedSoloRedGameModel extends SoloRedGameModel {

  private int lastCanvasCardNumber;
  private boolean playedToCanvasSinceLastDraw;

  /**
   * Constructor for the advanced game model. Initializes the additional game state variables.
   */
  public AdvancedSoloRedGameModel() {
    super();
    this.lastCanvasCardNumber = -1;
    this.playedToCanvasSinceLastDraw = false;
  }

  /**
   * Plays a card to the canvas and updates the game state for advanced rules.
   *
   * @param cardIdxInHand the index of the card in the hand to play.
   */
  @Override
  public void playToCanvas(int cardIdxInHand) {
    super.playToCanvas(cardIdxInHand);

    lastCanvasCardNumber = canvas.getNumber();
    playedToCanvasSinceLastDraw = true;
  }

  /**
   * Draws cards for the player's hand, with additional rules for the advanced version.
   */
  @Override
  public void drawForHand() {
    if (!gameStarted || gameOver) {
      throw new IllegalStateException("The game has not started or is over.");
    }

    int winningPaletteSize = palettes.get(winningPaletteIndex()).size();
    int cardsToDraw;
    if (playedToCanvasSinceLastDraw && lastCanvasCardNumber > winningPaletteSize) {
      cardsToDraw = 2; // Draw two cards
    } else {
      cardsToDraw = 1; // Draw one card
    }

    for (int i = 0; i < cardsToDraw && hand.size() < this.maxHandSize && !deck.isEmpty(); i++) {
      hand.add(deck.remove(0));
    }

    playedToCanvasSinceLastDraw = false;
    lastCanvasCardNumber = -1;

    canPlayToCanvas = true;
  }

  /**
   * Plays a card to a player's palette in the advanced game.
   *
   * @param paletteIdx the index of the palette.
   * @param cardIdxInHand the index of the card in the hand.
   */
  @Override
  public void playToPalette(int paletteIdx, int cardIdxInHand) {
    super.playToPalette(paletteIdx, cardIdxInHand);
  }
}