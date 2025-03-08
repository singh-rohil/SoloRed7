import cs3500.solored.controller.SoloRedTextController;
import cs3500.solored.model.hw02.CardImpl;
import cs3500.solored.model.hw02.SoloRedGameModel;
import cs3500.solored.view.hw02.SoloRedGameTextView;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;


/**
 * Comprehensive test class for the SoloRedGameModel, SoloRedGameTextView, and
 * SoloRedTextController.
 * Tests cover various scenarios to ensure the correctness of the game model, view rendering,
 * and controller interactions.
 */
public class SoloRedGameModelTest {

  private SoloRedGameModel model;
  private List<CardImpl> deck;

  @Before
  public void setup() {
    model = new SoloRedGameModel();
    deck = new ArrayList<>();
    for (String color : new String[]{"R", "O", "B", "I", "V"}) {
      for (int num = 1; num <= 7; num++) {
        deck.add(new CardImpl(color, num));
      }
    }
  }

  // --------------------- CardImpl Tests ---------------------

  /**
   * Tests the toString method of CardImpl.
   */
  @Test
  public void testCardImplToString() {
    CardImpl card = new CardImpl("R", 5);
    assertEquals("R5", card.toString());

    CardImpl card2 = new CardImpl("O", 1);
    assertEquals("O1", card2.toString());

    CardImpl card3 = new CardImpl("B", 7);
    assertEquals("B7", card3.toString());
  }

  /**
   * Tests the equals method of CardImpl.
   */
  @Test
  public void testCardImplEquals() {
    CardImpl card1 = new CardImpl("R", 5);
    CardImpl card2 = new CardImpl("R", 5);
    CardImpl card3 = new CardImpl("O", 5);
    CardImpl card4 = new CardImpl("R", 6);

    assertEquals(card1, card2);
    assertNotEquals(card1, card3);
    assertNotEquals(card1, card4);
    assertNotEquals(card3, card4);
    assertNotEquals(card1, null);
    assertNotEquals(card1, "R5");
  }

  /**
   * Tests the hashCode method of CardImpl.
   */
  @Test
  public void testCardImplHashCode() {
    CardImpl card1 = new CardImpl("R", 5);
    CardImpl card2 = new CardImpl("R", 5);
    CardImpl card3 = new CardImpl("O", 5);

    assertEquals(card1.hashCode(), card2.hashCode());
    assertNotEquals(card1.hashCode(), card3.hashCode());
  }

  // --------------------- SoloRedGameModel Tests ---------------------

  /**
   * Tests that the game starts correctly with valid parameters.
   */
  @Test
  public void testStartGame() {
    model.startGame(deck, false, 4, 7);
    assertEquals(4, model.numPalettes());
    assertEquals(24, model.numOfCardsInDeck());
    assertEquals(7, model.getHand().size());
    assertFalse(model.isGameOver());
  }

  /**
   * Tests that the game starts correctly with shuffling enabled.
   */
  @Test
  public void testStartGameWithShuffling() {
    model.startGame(deck, true, 4, 7);
    assertEquals(4, model.numPalettes());
    assertEquals(24, model.numOfCardsInDeck());
    assertEquals(7, model.getHand().size());
    assertFalse(model.isGameOver());
    // Additional checks can be added to verify shuffling if determinism is managed
  }

  /**
   * Tests that drawing cards fills the hand up to the maximum hand size.
   */
  @Test
  public void testDrawForHand() {
    model.startGame(deck, false, 4, 7);
    model.playToPalette(0, 0); // Play a card to palette to allow drawing
    model.drawForHand();
    assertEquals(7, model.getHand().size());
  }

  /**
   * Tests that drawing when hand is already full does not change the hand.
   */
  @Test
  public void testDrawForHandWhenHandIsFull() {
    model.startGame(deck, false, 4, 7);
    List<CardImpl> handBeforeDraw = new ArrayList<>(model.getHand());
    model.drawForHand();
    assertEquals(7, model.getHand().size());
    assertEquals(handBeforeDraw, model.getHand());
  }

  /**
   * Tests that after drawing, the canPlayToCanvas flag is reset appropriately.
   */
  @Test
  public void testCanPlayToCanvasAfterDraw() {
    model.startGame(deck, false, 4, 2);
    model.playToCanvas(0);
    assertEquals(1, model.getHand().size());
    // At this point, cannot play to canvas again
    try {
      model.playToCanvas(0);
      fail("Expected IllegalStateException when playing to canvas twice in a turn.");
    } catch (IllegalStateException e) {
      // Expected exception
    }
    model.drawForHand();
    // After drawing, can play to canvas again
    model.playToCanvas(0);
    assertEquals(1, model.getHand().size());
  }

  /**
   * Tests that playing a card to the palette updates the game state correctly.
   */
  @Test
  public void testPlayToPalette() {
    model.startGame(deck, false, 4, 7);
    int initialHandSize = model.getHand().size();
    CardImpl cardToPlay = model.getHand().get(0);
    // Find a palette that is not the winning palette
    int winningIndex = model.winningPaletteIndex();
    int paletteToPlay = (winningIndex + 1) % model.numPalettes();
    model.playToPalette(paletteToPlay, 0);
    assertEquals(initialHandSize - 1, model.getHand().size());
    assertEquals(2, model.getPalette(paletteToPlay).size());
    assertEquals(cardToPlay, model.getPalette(paletteToPlay).get(1));
  }

  /**
   * Tests that playing a card to the canvas updates the canvas correctly.
   */
  @Test
  public void testPlayToCanvas() {
    model.startGame(deck, false, 4, 7);
    int initialHandSize = model.getHand().size();
    CardImpl cardToPlay = model.getHand().get(0);
    model.playToCanvas(0);
    assertEquals(initialHandSize - 1, model.getHand().size());
    assertEquals(cardToPlay, model.getCanvas());
  }

  /**
   * Tests that the number of cards in the deck is correct after starting the game.
   */
  @Test
  public void testNumOfCardsInDeck() {
    model.startGame(deck, false, 4, 7);
    int expectedDeckSize = deck.size() - (4 + 7);
    assertEquals(expectedDeckSize, model.numOfCardsInDeck());
  }

  /**
   * Tests that the number of palettes is correct after starting the game.
   */
  @Test
  public void testNumPalettes() {
    model.startGame(deck, false, 4, 7);
    assertEquals(4, model.numPalettes());
  }

  /**
   * Tests that the winning palette index is determined correctly.
   */
  @Test
  public void testWinningPaletteIndex() {
    model.startGame(deck, false, 4, 7);
    int winningIndex = model.winningPaletteIndex();
    assertTrue(winningIndex >= 0 && winningIndex < model.numPalettes());
  }

  /**
   * Tests that the game over condition is correctly identified when the game is lost.
   */
  @Test
  public void testGameOverWhenNoMoreMoves() {
    List<CardImpl> smallDeck = Arrays.asList(new CardImpl("R", 6), // Palette 0
            new CardImpl("R", 7), // Palette 1 (Opponent's palette)
            new CardImpl("O", 5), // Player's hand
            new CardImpl("O", 6)  // Additional card in hand
    );
    model.startGame(smallDeck, false, 2, 2);

    int playerPaletteIndex = 0; // Player's own palette

    // Confirm that the player's palette is not the winning palette before the move
    assertNotEquals(playerPaletteIndex, model.winningPaletteIndex());

    // Player plays O5 to their own palette
    model.playToPalette(playerPaletteIndex, 0);

    // After the move, the player's palette remains losing
    assertNotEquals(playerPaletteIndex, model.winningPaletteIndex());

    // According to your implementation, the game should be over
    assertTrue(model.isGameOver());
    assertFalse(model.isGameWon());

    // Player still has one card left
    assertEquals(1, model.getHand().size());
  }

  /**
   * Tests that the game over condition is correctly identified when the game is won.
   */
  @Test
  public void testIsGameOverWhenWon() {
    List<CardImpl> smallDeck = Arrays.asList(new CardImpl("O", 6), // Palette 0
            new CardImpl("R", 6), // Palette 1
            new CardImpl("R", 7)  // Player's hand
    );
    model.startGame(smallDeck, false, 2, 1);

    int playerPaletteIndex = 0; // Player's own palette

    // Confirm that the player's palette is not the winning palette before the move
    assertNotEquals(playerPaletteIndex, model.winningPaletteIndex());

    // Player plays R7 to their own palette
    model.playToPalette(playerPaletteIndex, 0);

    // After the move, the player's palette should be winning
    assertEquals(playerPaletteIndex, model.winningPaletteIndex());

    // Game should be over and won
    assertTrue(model.isGameOver());
    assertTrue(model.isGameWon());
  }

  /**
   * Tests that getting the hand returns the correct cards.
   * Also tests that modifying the returned hand does not affect the model's hand.
   */
  @Test
  public void testGetHand() {
    model.startGame(deck, false, 4, 7);
    List<CardImpl> hand = model.getHand();
    assertEquals(7, hand.size());
    assertNotSame(hand, model.getHand());

    // Modify the returned hand
    hand.remove(0);
    assertEquals(7, model.getHand().size());
  }

  /**
   * Tests that getting a palette returns the correct cards.
   * Also tests that modifying the returned palette does not affect the model's palette.
   */
  @Test
  public void testGetPalette() {
    model.startGame(deck, false, 4, 7);
    List<CardImpl> palette = model.getPalette(0);
    assertEquals(1, palette.size());
    assertNotSame(palette, model.getPalette(0));

    // Modify the returned palette
    palette.remove(0);
    assertEquals(1, model.getPalette(0).size());
  }

  /**
   * Tests that getting the canvas returns the correct card.
   * Also tests that modifying the returned canvas card does not affect the model's canvas.
   */
  @Test
  public void testGetCanvas() {
    model.startGame(deck, false, 4, 7);
    CardImpl canvas = model.getCanvas();
    assertEquals(new CardImpl("R", 0), canvas);

    // Modify the returned canvas card (impossible since CardImpl is immutable)
    // But we can check that a new CardImpl with same values is equal
    assertEquals(new CardImpl("R", 0), model.getCanvas());
  }

  /**
   * Tests that getAllCards returns all the unique cards.
   * Also tests that modifying the returned list does not affect the model.
   */
  @Test
  public void testGetAllCards() {
    List<CardImpl> allCards = model.getAllCards();
    assertEquals(35, allCards.size());
    assertEquals(35, new HashSet<>(allCards).size());

    // Modify the returned list
    allCards.clear();
    // Call getAllCards again and check size
    assertEquals(35, model.getAllCards().size());
  }

  /**
   * Tests that an exception is thrown when starting the game with a null deck.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testStartGameWithNullDeck() {
    model.startGame(null, false, 4, 7);
  }

  /**
   * Tests that an exception is thrown when starting the game with invalid parameters.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testStartGameInvalidParameters() {
    model.startGame(deck, false, 1, 0);
  }

  /**
   * Tests that an exception is thrown when playing to an invalid palette index.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testPlayToPaletteInvalidPaletteIndex() {
    model.startGame(deck, false, 4, 7);
    model.playToPalette(5, 0);
  }

  /**
   * Tests that an exception is thrown when playing to an invalid hand index.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testPlayToPaletteInvalidHandIndex() {
    model.startGame(deck, false, 4, 7);
    model.playToPalette(0, 10);
  }

  /**
   * Tests that an exception is thrown when playing to the canvas with invalid hand index.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testPlayToCanvasInvalidHandIndex() {
    model.startGame(deck, false, 4, 7);
    model.playToCanvas(10);
  }

  /**
   * Tests that an exception is thrown when drawing for hand after the game is over.
   */
  @Test(expected = IllegalStateException.class)
  public void testDrawForHandAfterGameOver() {
    List<CardImpl> smallDeck = Arrays.asList(new CardImpl("R", 7), // Palette 0
            new CardImpl("R", 6), // Palette 1
            new CardImpl("O", 5)  // Player's hand
    );

    model.startGame(smallDeck, false, 2, 1);

    model.playToPalette(0, 0);

    model.drawForHand();
  }

  /**
   * Tests that an exception is thrown when getting the canvas before the game starts.
   */
  @Test(expected = IllegalStateException.class)
  public void testGetCanvasBeforeGameStarts() {
    model.getCanvas();
  }

  /**
   * Tests that an exception is thrown when playing to the canvas when not allowed.
   */
  @Test(expected = IllegalStateException.class)
  public void testPlayToCanvasWhenNotAllowed() {
    model.startGame(deck, false, 4, 2);
    model.playToCanvas(0);
    model.playToCanvas(0);
  }

  /**
   * Tests that playing to a winning palette throws an exception.
   */
  @Test(expected = IllegalStateException.class)
  public void testPlayToWinningPalette() {
    model.startGame(deck, false, 4, 7);
    int winningIndex = model.winningPaletteIndex();
    model.playToPalette(winningIndex, 0);
  }

  /**
   * Tests that an exception is thrown when the game is not started.
   */
  @Test(expected = IllegalStateException.class)
  public void testPlayToPaletteBeforeGameStarts() {
    model.playToPalette(0, 0);
  }

  /**
   * Tests that an exception is thrown when checking if the game is over before starting.
   */
  @Test(expected = IllegalStateException.class)
  public void testIsGameOverBeforeGameStarts() {
    model.isGameOver();
  }

  /**
   * Tests that an exception is thrown when checking if the game is won before starting.
   */
  @Test(expected = IllegalStateException.class)
  public void testIsGameWonBeforeGameStarts() {
    model.isGameWon();
  }

  /**
   * Tests that an exception is thrown when checking if the game is won before it's over.
   */
  @Test(expected = IllegalStateException.class)
  public void testIsGameWonBeforeGameOver() {
    model.startGame(deck, false, 4, 7);
    model.isGameWon();
  }

  /**
   * Tests that an exception is thrown when getting the hand before the game starts.
   */
  @Test(expected = IllegalStateException.class)
  public void testGetHandBeforeGameStarts() {
    model.getHand();
  }

  /**
   * Tests that an exception is thrown when getting a palette before the game starts.
   */
  @Test(expected = IllegalStateException.class)
  public void testGetPaletteBeforeGameStarts() {
    model.getPalette(0);
  }

  /**
   * Tests that an exception is thrown when starting the game after it has already started.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testStartGameAfterGameStarted() {
    model.startGame(deck, false, 4, 7);
    model.startGame(deck, false, 4, 7);
  }

  /**
   * Tests that an exception is thrown when attempting
   * to play to the canvas with only one card in hand.
   */
  @Test(expected = IllegalStateException.class)
  public void testPlayToCanvasWithOneCardInHand() {
    model.startGame(deck, false, 4, 1);
    model.playToCanvas(0);
  }

  /**
   * Tests that playing to the canvas resets the ability to play to the canvas after drawing.
   */
  @Test
  public void testPlayToCanvasAfterDrawing() {
    model.startGame(deck, false, 4, 2);
    model.playToCanvas(0);
    model.drawForHand();
    model.playToCanvas(0);
    assertEquals(1, model.getHand().size());
  }

  /**
   * Tests that drawing from an empty deck does not add new cards to the hand.
   */
  @Test
  public void testDrawFromEmptyDeck() {
    List<CardImpl> smallDeck = new ArrayList<>();
    // Create 11 unique cards
    smallDeck.add(new CardImpl("R", 1));
    smallDeck.add(new CardImpl("R", 2));
    smallDeck.add(new CardImpl("R", 3));
    smallDeck.add(new CardImpl("R", 4));
    smallDeck.add(new CardImpl("R", 5));
    smallDeck.add(new CardImpl("R", 6));
    smallDeck.add(new CardImpl("R", 7));
    smallDeck.add(new CardImpl("O", 1));
    smallDeck.add(new CardImpl("O", 2));
    smallDeck.add(new CardImpl("O", 3));
    smallDeck.add(new CardImpl("O", 4));

    model.startGame(smallDeck, false, 4, 7);

    // At this point, the deck should be empty
    assertEquals(0, model.numOfCardsInDeck());

    // Attempt to draw for hand
    model.drawForHand();

    // Hand size should remain the same (7), as the deck is empty
    assertEquals(7, model.getHand().size());
  }

  /**
   * Tests that the game handles playing to the canvas correctly when allowed.
   */
  @Test
  public void testPlayToCanvasWhenAllowed() {
    model.startGame(deck, false, 4, 2);
    CardImpl cardToPlay = model.getHand().get(0);
    model.playToCanvas(0);
    assertEquals(1, model.getHand().size());
    assertEquals(cardToPlay, model.getCanvas());
  }

  /**
   * Tests that an exception is thrown when the game has not started and methods are called.
   */
  @Test(expected = IllegalStateException.class)
  public void testMethodCallBeforeGameStarts() {
    model.numOfCardsInDeck();
  }

  /**
   * Tests that getPalette with invalid paletteNum throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testGetPaletteWithInvalidIndex() {
    model.startGame(deck, false, 4, 7);
    model.getPalette(-1);
  }

  /**
   * Tests that the game handles ties correctly in scoring.
   */
  @Test
  public void testTieBreakerInScoring() {
    List<CardImpl> customDeck = Arrays.asList(new CardImpl("R", 7),
            new CardImpl("O", 7), // Palette 1
            new CardImpl("B", 7)  // Player's hand
    );
    model.startGame(customDeck, false, 2, 1);

    // Canvas rule is Red: Highest card wins

    // Initially, Palette 0 is winning because R7 > O7
    assertEquals(0, model.winningPaletteIndex());

    // Player plays B7 to Palette 1
    model.playToPalette(1, 0);

    // Now, Palette 1 has O7 and B7
    // The highest card is still 7, but tie-breaker depends on color rank
    // R > O > B, so Palette 0 should still be winning

    assertEquals(0, model.winningPaletteIndex());
  }

  // --------------------- SoloRedGameTextView Tests ---------------------

  /**
   * Tests that the render method outputs the correct game state.
   */
  @Test
  public void testRender() throws IOException {
    SoloRedGameModel modelForView = new SoloRedGameModel();
    modelForView.startGame(Arrays.asList(new CardImpl("R", 5), // Canvas
            new CardImpl("R", 7), // Palette 1
            new CardImpl("O", 6), // Palette 2
            new CardImpl("B", 4), // Hand
            new CardImpl("I", 3)), false, 2, 2);

    StringWriter output = new StringWriter();
    SoloRedGameTextView view = new SoloRedGameTextView(modelForView, output);

    // Render the game state
    view.render();

    // Expected output
    String expected = "Canvas: R\n" + "P1: R5\n" + "> P2: R7\n" + "Hand: O6 B4";

    assertEquals(expected, output.toString());
  }

  /**
   * Tests that the render method correctly handles empty hand and palettes.
   */
  @Test
  public void testRenderEmptyHandAndPalettes() throws IOException {
    SoloRedGameModel modelForView = new SoloRedGameModel();
    modelForView.startGame(Arrays.asList(new CardImpl("R", 0) // Canvas
    ), false, 1, 1); // Use at least 1 palette and 1 hand card

    StringWriter output = new StringWriter();
    SoloRedGameTextView view = new SoloRedGameTextView(modelForView, output);

    // Render the game state
    view.render();

    // Expected output
    String expected = "Canvas: R0\n" + "> P1: \n" + "Hand: \n";

    assertEquals(expected, output.toString());
  }

  /**
   * Tests that the render method outputs the correct winning palette indicator.
   */
  @Test
  public void testRenderWinningPaletteIndicator() throws IOException {
    // Prepare a game state with multiple palettes
    SoloRedGameModel modelForView = new SoloRedGameModel();
    modelForView.startGame(Arrays.asList(new CardImpl("R", 5), // Canvas
            new CardImpl("R", 7), // Palette 1
            new CardImpl("O", 6), // Palette 2
            new CardImpl("V", 2), // Palette 3
            new CardImpl("B", 4), // Hand
            new CardImpl("I", 3)), false, 3, 2);

    StringWriter output = new StringWriter();
    SoloRedGameTextView view = new SoloRedGameTextView(modelForView, output);

    // Render the game state
    view.render();

    // Expected output
    String expected = "Canvas: R\n" + "P1: R5\n" + "> P2: R7\n" + "P3: O6\n" + "Hand: V2 B4";

    String actualOutput = output.toString();

    assertEquals(expected, actualOutput);
  }

  /**
   * Tests the toString method of SoloRedGameTextView.
   */
  @Test
  public void testViewToString() throws IOException {
    SoloRedGameModel modelForView = new SoloRedGameModel();
    modelForView.startGame(Arrays.asList(new CardImpl("R", 0), // Canvas
            new CardImpl("R", 1), // Palette 1
            new CardImpl("O", 2), // Palette 2
            new CardImpl("B", 3), // Hand
            new CardImpl("I", 4)), false, 2, 2);

    SoloRedGameTextView view = new SoloRedGameTextView(modelForView);

    // Get the string representation
    String viewString = view.toString();

    // Expected output
    String expected = "Canvas: R\n" + "P1: R\n" + "> P2: R1\n" + "Hand: O2 B3";

    assertEquals(expected, viewString);
  }

  // --------------------- SoloRedTextController Tests ---------------------

  /**
   * Tests that the controller processes a simple game where the player quits immediately.
   */
  @Test
  public void testControllerImmediateQuit() {
    StringReader input = new StringReader("q");
    StringWriter output = new StringWriter();

    SoloRedTextController controller = new SoloRedTextController(input, output);

    // Do NOT manually start the game; let the controller handle it
    controller.playGame(model, deck, false, 2, 1);

    String expectedOutput =
            "Canvas: R\n" + "P1: R1\n" + "> P2: R2\n" + "Hand: R3\n" + "Number " + "of" + " cards"
                    + " in deck: 32\n" + "Game quit!\n" + "State of game when quit:\n" + "Canvas:" +
                    " " + "R\n" + "P1: R1\n" + "> P2: R2\n" + "Hand: R3\n" + "Number of cards in "
                    + "deck: 32\n";

    assertEquals(expectedOutput, output.toString());
  }

  /**
   * Tests that the controller processes valid moves correctly.
   */
  @Test
  public void testControllerValidMoves() {
    StringReader input = new StringReader("palette 1 1 q");
    StringWriter output = new StringWriter();

    SoloRedTextController controller = new SoloRedTextController(input, output);

    // Do NOT manually start the game; let the controller handle it
    controller.playGame(model, deck, false, 2, 1);

    String expectedOutput =
            "Canvas: R\n" + "P1: R1\n" + "> P2: R2\n" + "Hand: R3\n" + "Number " + "of" + " cards"
                    + " in deck: 32\n" + "Canvas: R\n" + "> P1: R1 R3\n" + "P2: R2\n" + "Hand:" +
                    " R4\n" + "Number of cards in deck: 31\n" + "Game quit!\n" + "State of game " +
                    "when " + "quit:\n" + "Canvas: R\n" + "> P1: R1 R3\n" + "P2: R2\n" + "Hand: " +
                    "R4\n" + "Number of " + "cards in deck: 31\n";

    assertEquals(expectedOutput, output.toString());
  }

  /**
   * Tests that the controller handles invalid commands gracefully.
   */
  @Test
  public void testControllerInvalidCommand() {
    StringReader input = new StringReader("invalid q");
    StringWriter output = new StringWriter();

    SoloRedTextController controller = new SoloRedTextController(input, output);

    // Do NOT manually start the game; let the controller handle it
    controller.playGame(model, deck, false, 2, 1);

    String expectedOutput =
            "Canvas: R\n" + "P1: R1\n" + "> P2: R2\n" + "Hand: R3\n" + "Number " + "of" + " cards"
                    + " in deck: 32\n" + "Invalid command. Try again.\n" + "Canvas: R\n" + "P1" +
                    ": R1\n" + "> P2: R2\n" + "Hand: R3\n" + "Number of cards in deck: 32\n" +
                    "Game " + "quit!\n" + "State of game when quit:\n" + "Canvas: R\n" + "P1: " +
                    "R1\n" + "> P2: R2\n" + "Hand: R3\n" + "Number of cards in deck: 32\n";

    assertEquals(expectedOutput, output.toString());
  }

  /**
   * Tests that the controller handles invalid move inputs gracefully.
   */
  @Test
  public void testControllerInvalidMoveInputs() {
    StringReader input = new StringReader("palette q q");
    StringWriter output = new StringWriter();

    SoloRedTextController controller = new SoloRedTextController(input, output);

    // Do NOT manually start the game; let the controller handle it
    controller.playGame(model, deck, false, 2, 1);

    String expectedOutput =
            "Canvas: R\n" + "P1: R1\n" + "> P2: R2\n" + "Hand: R3\n" + "Number " + "of" + " cards"
                    + " in deck: 32\n" + "Game quit!\n" + "State of game when quit:\n" + "Canvas:" +
                    " " + "R\n" + "P1: R1\n" + "> P2: R2\n" + "Hand: R3\n" + "Number of cards in "
                    + "deck: 32\n";

    assertEquals(expectedOutput, output.toString());
  }

  /**
   * Tests that the controller correctly handles the game over condition.
   */
  @Test
  public void testControllerGameOver() {
    // Create a scenario where the game will end
    List<CardImpl> smallDeck = Arrays.asList(new CardImpl("R", 7),
            new CardImpl("O", 6), // Palette 1
            new CardImpl("R", 5)  // Player's hand
    );

    // Include a quit command 'q' after the game-ending move
    StringReader input = new StringReader("palette 1 1 q");
    StringWriter output = new StringWriter();

    SoloRedTextController controller = new SoloRedTextController(input, output);

    controller.playGame(model, smallDeck, false, 2, 1);

    String expectedOutput =
            "Canvas: R\n" + "> P1: R7\n" + "P2: O6\n" + "Hand: R5\n" + "Number " + "of" + " cards"
                    + " in deck: 0\n" + "Invalid move. Try again. Cannot play to a winning " +
                    "palette" + ".\n" + "Canvas: R\n" + "> P1: R7\n" + "P2: O6\n" + "Hand: R5\n" +
                    "Number of cards " + "in deck: 0\n" + "Game quit!\n" +
                    "State of game when quit:\n" + "Canvas: R\n" + "> " + "P1: R7\n" + "P2: O6\n" +
                    "Hand: R5\n" + "Number of cards in deck: 0\n";

    assertEquals(expectedOutput, output.toString());
  }

  /**
   * Tests that the controller handles input with extra whitespace correctly.
   */
  @Test
  public void testControllerInputWithExtraWhitespace() {
    StringReader input = new StringReader("   palette    1   1   q   ");
    StringWriter output = new StringWriter();

    SoloRedTextController controller = new SoloRedTextController(input, output);

    model.startGame(deck, false, 2, 1);
    controller.playGame(model, deck, false, 2, 1);

    String expectedOutput = "Canvas: R0\n" + "> P1: R1\n" + "P2: R2\n" + "Hand: R3\n" + "\n" +
            "Number of cards in deck: 32\n" + "Game quit!\n" + "State of game when quit:\n" +
            "Canvas: R0\n" + "> P1: R1\n" + "P2: R2\n" + "Hand: R3\n" + "\n" + "Number of cards " +
            "in deck: 32\n";

    assertEquals(expectedOutput, output.toString());
  }

  /**
   * Tests that the controller throws an IllegalArgumentException when the model is null.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testControllerPlayGameWithNullModel() {
    StringReader input = new StringReader("");
    StringWriter output = new StringWriter();

    SoloRedTextController controller = new SoloRedTextController(input, output);

    controller.playGame(null, deck, false, 2, 1);
  }

  /**
   * Tests that the controller throws an IllegalStateException when input is exhausted.
   */
  @Test(expected = IllegalStateException.class)
  public void testControllerInputExhausted() {
    StringReader input = new StringReader("");
    StringWriter output = new StringWriter();

    SoloRedTextController controller = new SoloRedTextController(input, output);

    controller.playGame(model, deck, false, 2, 1);
  }

  // --------------------- Additional Predicted Tests ---------------------

  /**
   * Tests that the controller handles multiple valid moves correctly.
   */
  @Test
  public void testControllerMultipleValidMoves() {
    // Assuming 'playToCanvas' is a valid command. If not, replace it with the correct command
    // syntax.
    StringReader input = new StringReader("palette 1 1 playToCanvas 1 q");
    StringWriter output = new StringWriter();

    SoloRedTextController controller = new SoloRedTextController(input, output);

    // Do NOT manually start the game; let the controller handle it
    controller.playGame(model, deck, false, 2, 2);

    // Define the expected output based on your game logic
    String expectedOutput = "Canvas: R\n" + "P1: R1\n" + "> P2: R2\n" + "Hand: R3 R4\n" + "Number"
            + " of cards in deck: 31\n" + "Canvas: R\n" + "> P1: R1 R3\n" + "P2: R2\n" + "Hand: " +
            "R4 " + "R5\n" + "Number of cards in deck: 30\n" + "Invalid command. Try again.\n" +
            "Canvas:" + " R\n" + "> P1: R1 R3\n" + "P2: R2\n" + "Hand: R4 R5\n" + "Number of " +
            "cards in deck: " + "30\n" + "Invalid command. Try again.\n" + "Canvas: R\n" + "> P1:" +
            " R1 R3\n" + "P2: " + "R2\n" + "Hand: R4 R5\n" + "Number of cards in deck: 30\n" +
            "Game quit!\n" + "State " + "of game when quit:\n" + "Canvas: R\n" + "> P1: R1 R3\n" +
            "P2: R2\n" + "Hand: R4 " + "R5\n" + "Number of cards in deck: 30\n";

    assertEquals(expectedOutput, output.toString());
  }

  /**
   * Tests that the controller initializes correctly with null Readable and Appendable.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testControllerConstructorWithNullReadable() {
    new SoloRedTextController(null, new StringWriter());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testControllerConstructorWithNullAppendable() {
    new SoloRedTextController(new StringReader(""), null);
  }

  /**
   * Tests that the controller initializes correctly with valid Readable and Appendable.
   */
  @Test
  public void testControllerConstructorValid() {
    StringReader input = new StringReader("q");
    StringWriter output = new StringWriter();
    SoloRedTextController controller = new SoloRedTextController(input, output);
    assertNotNull(controller);
  }

  /**
   * Tests that the controller handles shuffle correctly.
   */
  @Test
  public void testControllerWithShuffledDeck() {

    StringReader input = new StringReader("palette 1 1 q");
    StringWriter output = new StringWriter();

    SoloRedTextController controller = new SoloRedTextController(input, output);

    controller.playGame(model, deck, true, 2, 1);

    String expectedOutputStart =
            "Canvas: R0\n" + "> P1: R1\n" + "P2: R2\n" + "Hand: R3\n" + "\n" + "Number of cards " +
                    "in deck: 32\n" + "Canvas: R0\n" + "> P1: R1 R3\n" + "P2: R2\n" + "Hand: " +
                    "R4\n" + "\n" + "Number of cards in deck: 31\n" + "Game quit!\n" + "State of " +
                    "game when quit:\n" + "Canvas: R0\n" + "> P1: R1 R3\n" + "P2: R2\n" + "Hand: " +
                    "R4\n" + "\n" + "Number of cards in deck: 31\n";

    assertTrue(output.toString().contains("Number of cards in deck: 31"));
  }

  /**
   * Tests that the controller correctly handles advanced rules, specifically performing a
   * special move when three cards of the same color are present in a palette.
   */
  @Test
  public void testControllerAdvancedRules() {
    StringReader input = new StringReader("palette 1 1 palette 1 1 palette 1 1 specialMove 1 q");
    StringWriter output = new StringWriter();

    SoloRedTextController controller = new SoloRedTextController(input, output);

    controller.playGame(model, deck, false, 2, 3); // 2 palettes, hand size 3

    String expectedOutput = "Canvas: R\n" + "P1: R1\n" + "> P2: R2\n" + "Hand: R3 R4 R5\n" +
            "Number of cards in deck: 30\n" + "Canvas: R\n" + "> P1: R1 R3\n" + "P2: R2\n" +
            "Hand: R4 R5 R6\n" + "Number of cards in deck: 29\n" + "Invalid move. Try again. " +
            "Cannot play to a winning palette.\n" + "Canvas: R\n" + "> P1: R1 R3\n" + "P2: R2\n" +
            "Hand: R4 R5 R6\n" + "Number of cards in deck: 29\n" +
            "Invalid move. Try again. Cannot play to a winning palette.\n" +
            "Canvas: R\n" + "> P1: R1 R3\n" + "P2: R2\n" + "Hand: R4 R5 R6\n" +
            "Number of cards in deck: 29\n" + "Invalid command. Try again.\n" +
            "Canvas: R\n" + "> P1: R1 R3\n" + "P2: R2\n" + "Hand: R4 R5 R6\n" +
            "Number of cards in deck: 29\n" + "Invalid command. Try again.\n" +
            "Canvas: R\n" + "> P1: R1 R3\n" + "P2: R2\n" + "Hand: R4 R5 R6\n" +
            "Number of cards in deck: 29\n" + "Game quit!\n" + "State of game when quit:\n" +
            "Canvas: R\n" + "> P1: R1 R3\n" + "P2: R2\n" + "Hand: R4 R5 R6\n" +
            "Number of cards in deck: 29\n";

    assertEquals(expectedOutput, output.toString());
  }

  /**
   * Tests that the controller properly handles exception scenarios without propagating exceptions.
   *
  @Test
  public void testControllerExceptionHandling() {
    // Simulate Appendable throwing an IOException
    // This requires creating a mock Appendable that throws an exception but I did not have time
    /*
    Appendable faultyAppendable = new Appendable() {
      @Override
      public Appendable append(CharSequence csq) throws IOException {
        throw new IOException("Appendable failure");
      }

      @Override
      public Appendable append(CharSequence csq, int start, int end) throws IOException {
        throw new IOException("Appendable failure");
      }

      @Override
      public Appendable append(char c) throws IOException {
        throw new IOException("Appendable failure");
      }
    };

    StringReader input = new StringReader("q");
    SoloRedTextController controller = new SoloRedTextController(input, faultyAppendable);

    try {
      controller.playGame(model, deck, false, 2, 1);
      fail("Expected IllegalStateException due to Appendable failure.");
    } catch (IllegalStateException e) {
      assertEquals("Failed to append to output.", e.getMessage());
    }
  }
    */

  /**
   * Tests that the controller correctly handles playing all cards leading to a win.
   */
  @Test
  public void testControllerWinCondition() {
    List<CardImpl> winningDeck = Arrays.asList(new CardImpl("R", 0), // Canvas
            new CardImpl("R", 7), // Palette 1
            new CardImpl("R", 6)  // Player's hand
    );

    StringReader input = new StringReader("palette 1 1");
    StringWriter output = new StringWriter();

    SoloRedTextController controller = new SoloRedTextController(input, output);

    controller.playGame(model, winningDeck, false, 2, 1);

    String expectedOutput = "Canvas: R\n" + "P1: R\n" + "> P2: R7\n" + "Hand: R6\n" + "Number of "
            + "cards in deck: 0\n" + "Game lost.\n" + "Canvas: R\n" + "P1: R R6\n" + "> P2: R7\n" +
            "Hand: \n" + "Number of cards in deck: 0\n";

    assertEquals(expectedOutput, output.toString());
  }

  /**
   * Tests that the controller correctly handles playing invalid commands repeatedly.
   *
  @Test
  public void testControllerRepeatedInvalidCommands() {
    StringReader input = new StringReader("foo bar palette 3 2 q");
    StringWriter output = new StringWriter();

    SoloRedTextController controller = new SoloRedTextController(input, output);

    model.startGame(deck, false, 3, 2);
    controller.playGame(model, deck, false, 3, 2);
  }
  */

  // --------------------- Advanced Predicted Tests ---------------------

  /**
   * Tests that the model correctly identifies the winning palette in complex scenarios.
   */
  @Test
  public void testModelWinningPaletteComplexScenario() {
    // Create a complex scenario with multiple palettes and varied card numbers
    List<CardImpl> complexDeck = Arrays.asList(new CardImpl("R", 5), // Canvas
            new CardImpl("B", 3), // Palette 1
            new CardImpl("O", 4), // Palette 2
            new CardImpl("V", 2), // Palette 3
            new CardImpl("I", 6), // Hand
            new CardImpl("R", 7));

    model.startGame(complexDeck, false, 3, 2);

    // Play R7 to Palette 1
    model.playToPalette(0, 1); // Palette 1 now has B3, R7

    // Winning palette should now be Palette 1
    assertEquals(0, model.winningPaletteIndex());

    // Play I6 to Palette 2
    model.playToPalette(1, 0); // Palette 2 now has O4, I6

    // Winning palette should still be Palette 1
    assertEquals(0, model.winningPaletteIndex());
  }

  /**
   * Tests that the model does not allow starting a game without calling startGame.
   */
  @Test(expected = IllegalStateException.class)
  public void testModelWithoutStartingGame() {
    model.playToCanvas(0);
  }

  /**
   * Tests that the model correctly handles maximum hand size limits.
   */
  @Test
  public void testModelMaxHandSize() {
    model.startGame(deck, false, 4, 3); // Max hand size is 3

    model.playToPalette(0, 0);
    model.drawForHand();
    model.playToPalette(1, 0);
    model.drawForHand();
    model.playToPalette(2, 0);
    model.drawForHand();

    assertEquals(3, model.getHand().size());
  }

  /**
   * Tests that the model handles playing to a full palette.
   */
  @Test
  public void testModelPlayToFullPalette() {
    model.startGame(deck, false, 2, 3); // Palette 1 and 2

    model.playToPalette(0, 0);
    model.playToPalette(0, 1);
    model.playToPalette(0, 2);

    assertEquals(4, model.getPalette(0).size());
  }

  /**
   * Tests that the model correctly identifies game over due to deck exhaustion.
   */
  @Test
  public void testModelGameOverDueToDeckExhaustion() {
    // Create a deck that will exhaust after a few moves
    List<CardImpl> limitedDeck = Arrays.asList(new CardImpl("R", 0), // Canvas
            new CardImpl("R", 1), // Palette 1
            new CardImpl("O", 2), // Palette 2
            new CardImpl("B", 3)  // Hand
    );

    model.startGame(limitedDeck, false, 2, 1);

    // Play to palette and draw
    model.playToPalette(0, 0);
    model.drawForHand(); // Deck is now empty

    // Attempt to play remaining hand
    model.playToCanvas(0);

    // Game should now be over
    assertTrue(model.isGameOver());
    assertTrue(model.isGameWon());
  }

  /**
   * Tests that the model enforces unique cards in the deck.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testModelEnforceUniqueCards() {
    List<CardImpl> duplicateDeck = Arrays.asList(new CardImpl("R", 1), new CardImpl("R", 1),
            new CardImpl("O", 2));

    model.startGame(duplicateDeck, false, 2, 1);
  }

  /**
   * Tests that the model handles multiple game starts correctly.
   */
  @Test
  public void testModelMultipleGameStarts() {
    model.startGame(deck, false, 4, 7);
    assertFalse(model.isGameOver());

    // Reset the game with a new deck
    List<CardImpl> newDeck = new ArrayList<>(deck);
    model = new SoloRedGameModel();
    model.startGame(newDeck, false, 3, 5);
    assertEquals(3, model.numPalettes());
    assertEquals(27, model.numOfCardsInDeck());
    assertEquals(5, model.getHand().size());
    assertFalse(model.isGameOver());
  }

  /**
   * Tests that the model correctly handles starting a game with the minimum number of palettes
   * and hand size.
   */
  @Test
  public void testModelMinimumPalettesAndHandSize() {
    model.startGame(deck, false, 1, 1);
    assertEquals(1, model.numPalettes());
    assertEquals(33, model.numOfCardsInDeck());
    assertEquals(1, model.getHand().size());
    assertFalse(model.isGameOver());
  }

  /**
   * Tests that the model correctly handles starting a game with a large number of palettes and
   * hand size.
   */
  @Test
  public void testModelLargePalettesAndHandSize() {
    model.startGame(deck, false, 10, 20);
    assertEquals(10, model.numPalettes());
    assertEquals(35 - (10 + 20), model.numOfCardsInDeck());
    assertEquals(20, model.getHand().size());
    assertFalse(model.isGameOver());
  }

  /**
   * Tests that the model throws an exception when starting a game with more palettes or hand
   * size than the deck allows.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testModelStartGameWithTooManyPalettesOrHandSize() {
    // Total required cards = palettes + hand size > deck size
    model.startGame(deck, false, 20, 20);
  }

  // --------------------- SoloRedTextController Additional Tests ---------------------

  /**
   * Tests that the controller correctly handles a sequence of valid palette and canvas plays.
   */
  @Test
  public void testControllerSequenceOfValidMoves() {
    // Define the sequence of commands: play to palette 1, play to canvas 1, then quit
    StringReader input = new StringReader("palette 1 1 playToCanvas 1 q");
    StringWriter output = new StringWriter();

    SoloRedTextController controller = new SoloRedTextController(input, output);

    // Do NOT manually start the game; let the controller handle it
    controller.playGame(model, deck, false, 2, 2);

    // Define the expected output based on the sequence of commands
    String expectedOutput = "Canvas: R\n" + "P1: R1\n" + "> P2: R2\n" + "Hand: R3 R4\n" + "Number"
            + " of cards in deck: 31\n" + "Canvas: R\n" + "> P1: R1 R3\n" + "P2: R2\n" + "Hand: " +
            "R4 " + "R5\n" + "Number of cards in deck: 30\n" + "Invalid command. Try again.\n" +
            "Canvas:" + " R\n" + "> P1: R1 R3\n" + "P2: R2\n" + "Hand: R4 R5\n" + "Number of " +
            "cards in deck: " + "30\n" + "Invalid command. Try again.\n" + "Canvas: R\n" + "> P1:" +
            " R1 R3\n" + "P2: " + "R2\n" + "Hand: R4 R5\n" + "Number of cards in deck: 30\n" +
            "Game quit!\n" + "State " + "of game when quit:\n" + "Canvas: R\n" + "> P1: R1 R3\n" +
            "P2: R2\n" + "Hand: R4 " + "R5\n" + "Number of cards in deck: 30\n";

    // Assert that the actual output matches the expected output
    assertEquals(expectedOutput, output.toString());
  }

  /**
   * Tests that the controller properly handles quitting during the game.
   */
  @Test
  public void testControllerQuitDuringGame() {
    StringReader input = new StringReader("palette 1 1 q");
    StringWriter output = new StringWriter();

    SoloRedTextController controller = new SoloRedTextController(input, output);

    controller.playGame(model, deck, false, 2, 2);

    String expectedOutput = "Canvas: R\n" + "P1: R1\n" + "> P2: R2\n" + "Hand: R3 R4\n" + "Number"
            + " of cards in deck: 31\n" + "Canvas: R\n" + "> P1: R1 R3\n" + "P2: R2\n" + "Hand: " +
            "R4 " + "R5\n" + "Number of cards in deck: 30\n" + "Game quit!\n" + "State of game " +
            "when " + "quit:\n" + "Canvas: R\n" + "> P1: R1 R3\n" + "P2: R2\n" + "Hand: R4 R5\n" +
            "Number " + "of cards in deck: 30\n";

    assertEquals(expectedOutput, output.toString());
  }
}