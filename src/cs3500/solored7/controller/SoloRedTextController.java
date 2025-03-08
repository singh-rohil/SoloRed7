package cs3500.solored.controller;

import cs3500.solored.model.hw02.Card;
import cs3500.solored.model.hw02.RedGameModel;
import cs3500.solored.view.hw02.RedGameView;
import cs3500.solored.view.hw02.SoloRedGameTextView;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * A text-based controller for running a SoloRed game.
 */
public class SoloRedTextController implements RedGameController {
  private final Readable rd;
  private final Appendable ap;
  private boolean gameQuit;

  /**
   * Constructs a controller with the given input and output streams.
   *
   * @param rd the source of input (Readable)
   * @param ap the target for output (Appendable)
   * @throws IllegalArgumentException if rd or ap is null
   */
  public SoloRedTextController(Readable rd, Appendable ap) throws IllegalArgumentException {
    if (rd == null || ap == null) {
      throw new IllegalArgumentException("Readable and Appendable cannot be null.");
    }
    this.rd = rd;
    this.ap = ap;
    this.gameQuit = false;
  }

  /**
   * Starts and plays the game using the given model, deck, and game settings.
   *
   * @param model      the game model to control
   * @param deck       the deck of cards for the game
   * @param shuffle    whether to shuffle the deck before starting
   * @param numPalettes number of palettes in the game
   * @param handSize   the size of each player's hand
   * @param <C>        the type of card being used in the game
   * @throws IllegalArgumentException if the model is null, or if there are errors starting the game
   */
  @Override
  public <C extends Card> void playGame(RedGameModel<C> model, List<C> deck, boolean shuffle,
                                        int numPalettes, int handSize) {
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null.");
    }

    try {
      model.startGame(deck, shuffle, numPalettes, handSize);
    } catch (IllegalArgumentException e) {
      throw e;
    } catch (Exception e) {
      throw new IllegalArgumentException("An error occurred while starting the game.", e);
    }

    try {
      RedGameView view = new SoloRedGameTextView(model, ap);
      Scanner scanner = new Scanner(this.rd);

      while (!model.isGameOver() && !gameQuit) {
        transmitGameState(view);
        transmit("Number of cards in deck: " + model.numOfCardsInDeck() + "\n");

        String command = readNextCommand(scanner);

        if (command.equalsIgnoreCase("q")) {
          handleQuitCommand(model, view);
          break;
        }

        if (command.equalsIgnoreCase("palette")) {
          handlePaletteCommand(scanner, model, view);
          if (gameQuit) {
            break;
          }
        } else if (command.equalsIgnoreCase("canvas")) {
          handleCanvasCommand(scanner, model, view);
          if (gameQuit) {
            break;
          }
        } else {
          transmit("Invalid command. Try again.\n");
        }
      }

      if (model.isGameOver()) {
        if (model.isGameWon()) {
          transmit("Game won.\n");
        } else {
          transmit("Game lost.\n");
        }
        transmitGameState(view);
        transmit("Number of cards in deck: " + model.numOfCardsInDeck() + "\n");
      }

    } catch (IOException e) {
      throw new IllegalArgumentException("Unable to successfully receive input or transmit output.",
              e);
    }
  }

  /**
   * Reads the next command from the input scanner.
   *
   * @param scanner the scanner to read commands from
   * @return the next command string
   * @throws IllegalStateException if there is no more input available
   */
  private String readNextCommand(Scanner scanner) {
    if (!scanner.hasNext()) {
      throw new IllegalStateException("No more input available.");
    }
    return scanner.next();
  }

  /**
   * Transmits the current game state using the given view.
   *
   * @param view the game view to render the game state
   * @throws IOException if there is an error transmitting the game state
   */
  private void transmitGameState(RedGameView view) throws IOException {
    view.render();
    ap.append("\n");
  }

  /**
   * Transmits a message to the output.
   *
   * @param message the message to transmit
   * @throws IOException if there is an error transmitting the message
   */
  private void transmit(String message) throws IOException {
    ap.append(message);
  }

  /**
   * Transmits an invalid move message, appending the exception message if available.
   *
   * @param e the exception representing the invalid move
   * @throws IOException if there is an error transmitting the message
   */
  private void transmitInvalidMove(Exception e) throws IOException {
    String exceptionMessage = (e.getMessage() != null) ? e.getMessage().trim() : "";
    transmit("Invalid move. Try again. " + exceptionMessage + "\n");
  }

  /**
   * Handles the "quit" command, ending the game and showing the final game state.
   *
   * @param model the game model to quit
   * @param view  the view to display the final game state
   * @param <C>   the type of card being used in the game
   * @throws IOException if there is an error transmitting the quit state
   */
  private <C extends Card> void handleQuitCommand(RedGameModel<C> model, RedGameView view)
          throws IOException {
    transmit("Game quit!\n");
    transmit("State of game when quit:\n");
    transmitGameState(view);
    transmit("Number of cards in deck: " + model.numOfCardsInDeck() + "\n");
    this.gameQuit = true;
  }

  /**
   * Handles the "palette" command, which allows a card to be played to a palette.
   *
   * @param scanner the scanner to read input
   * @param model   the game model to play to the palette
   * @param view    the view to update after the move
   * @param <C>     the type of card being used in the game
   * @throws IOException if there is an error transmitting the game state
   */
  private <C extends Card> void handlePaletteCommand(Scanner scanner, RedGameModel<C> model,
                                                     RedGameView view) throws IOException {
    Integer paletteIdx = readNextNaturalNumber(scanner);
    if (gameQuit) {
      handleQuitCommand(model, view);
      return;
    }

    if (paletteIdx == null || paletteIdx <= 0) {
      transmit("Invalid move. Try again.\n");
      return;
    }

    Integer cardIdx = readNextNaturalNumber(scanner);
    if (gameQuit) {
      handleQuitCommand(model, view);
      return;
    }

    if (cardIdx == null || cardIdx <= 0) {
      transmit("Invalid move. Try again.\n");
      return;
    }

    int paletteIndex = paletteIdx - 1;
    int cardIndex = cardIdx - 1;

    try {
      model.playToPalette(paletteIndex, cardIndex);
    } catch (IllegalArgumentException | IllegalStateException e) {
      transmitInvalidMove(e);
      return;
    }

    try {
      if (!model.isGameOver()) {
        model.drawForHand();
      }
    } catch (IllegalStateException e) {
      transmit("Error during draw. Continuing the game.\n");
    }
  }

  /**
   * Handles the "canvas" command, which allows a card to be played to the canvas.
   *
   * @param scanner the scanner to read input
   * @param model   the game model to play to the canvas
   * @param view    the view to update after the move
   * @param <C>     the type of card being used in the game
   * @throws IOException if there is an error transmitting the game state
   */
  private <C extends Card> void handleCanvasCommand(Scanner scanner, RedGameModel<C> model,
                                                    RedGameView view) throws IOException {
    Integer cardIdx = readNextNaturalNumber(scanner);
    if (gameQuit) {
      handleQuitCommand(model, view);
      return;
    }

    if (cardIdx == null || cardIdx <= 0) {
      transmit("Invalid move. Try again.\n");
      return;
    }

    int cardIndex = cardIdx - 1;

    try {
      model.playToCanvas(cardIndex);
    } catch (IllegalArgumentException | IllegalStateException e) {
      transmitInvalidMove(e);
      return;
    }
  }

  /**
   * Reads the next natural number from the input scanner.
   * A natural number is a positive integer.
   *
   * @param scanner the scanner to read input from
   * @return the next natural number or null if the user wants to quit
   * @throws IOException if there is an error reading input
   */
  private Integer readNextNaturalNumber(Scanner scanner) throws IOException {
    while (true) {
      if (!scanner.hasNext()) {
        throw new IllegalStateException("No more input available.");
      }
      String input = scanner.next();
      if (input.equalsIgnoreCase("q")) {
        gameQuit = true;
        return null;
      }
      try {
        int value = Integer.parseInt(input);
        if (value > 0) {
          return value;
        } else {
          continue;
        }
      } catch (NumberFormatException e) {
        continue;
      }
    }
  }
}