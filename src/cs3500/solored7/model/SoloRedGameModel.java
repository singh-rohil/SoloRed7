package cs3500.solored.model.hw02;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of the RedGameModel for the SoloRed game.
 * Handles the game state and operations.
 */
public class SoloRedGameModel implements RedGameModel<CardImpl> {
  protected final Random random;
  protected List<CardImpl> deck;
  protected List<List<CardImpl>> palettes;
  protected List<CardImpl> hand;
  protected CardImpl canvas;
  protected boolean gameStarted;
  protected boolean gameOver;
  protected boolean gameWon;
  protected boolean canPlayToCanvas;
  protected int maxHandSize;

  /**
   * Default constructor initializes the model in a state ready to start the game.
   */
  public SoloRedGameModel() {
    this(new Random());
  }

  /**
   * Constructor that initializes the model with a specific Random object for shuffling.
   *
   * @param rand the Random object used for shuffling
   * @throws IllegalArgumentException if rand is null
   */
  public SoloRedGameModel(Random rand) {
    if (rand == null) {
      throw new IllegalArgumentException("Random object cannot be null.");
    }
    this.gameStarted = false;
    this.gameOver = false;
    this.gameWon = false;
    this.canPlayToCanvas = true;
    this.random = rand;
    this.maxHandSize = 0;
  }

  @Override
  public void startGame(List<CardImpl> deck, boolean shuffle, int numPalettes, int handSize) {
    if (deck == null) {
      throw new IllegalArgumentException("Deck cannot be null.");
    }
    if (gameStarted) {
      throw new IllegalArgumentException("The game has already started.");
    }
    if (numPalettes < 2 || handSize <= 0) {
      throw new IllegalArgumentException("Invalid number of palettes or hand size.");
    }

    int requiredCards = numPalettes + handSize;
    if (deck.size() < requiredCards) {
      throw new IllegalArgumentException("Not enough cards in the deck to start the game.");
    }

    Set<CardImpl> cardSet = new HashSet<>(deck);
    if (deck.size() != cardSet.size() || deck.contains(null)) {
      throw new IllegalArgumentException("Deck contains duplicate or null cards.");
    }

    this.maxHandSize = handSize;

    this.deck = new ArrayList<>(deck);
    if (shuffle) {
      Collections.shuffle(this.deck, this.random);
    }

    this.palettes = new ArrayList<>();
    for (int i = 0; i < numPalettes; i++) {
      List<CardImpl> palette = new ArrayList<>();
      palette.add(this.deck.remove(0));
      this.palettes.add(palette);
    }

    this.hand = new ArrayList<>();
    for (int i = 0; i < handSize && !this.deck.isEmpty(); i++) {
      this.hand.add(this.deck.remove(0));
    }

    this.canvas = new CardImpl("R", 0);

    this.gameStarted = true;
    this.gameOver = false;
    this.gameWon = false;
    this.canPlayToCanvas = true;
  }

  @Override
  public void drawForHand() {
    if (!gameStarted || gameOver) {
      throw new IllegalStateException("The game has not started or is over.");
    }

    int cardsToDraw = maxHandSize - hand.size();

    for (int i = 0; i < cardsToDraw && !deck.isEmpty(); i++) {
      hand.add(deck.remove(0));
    }

    canPlayToCanvas = true;
  }

  @Override
  public void playToPalette(int paletteIdx, int cardIdxInHand) {
    if (!gameStarted || gameOver) {
      throw new IllegalStateException("The game has not started or is over.");
    }
    if (paletteIdx < 0 || paletteIdx >= palettes.size()) {
      throw new IllegalArgumentException("Invalid palette index.");
    }
    if (cardIdxInHand < 0 || cardIdxInHand >= hand.size()) {
      throw new IllegalArgumentException("Invalid card index in hand.");
    }
    if (paletteIdx == winningPaletteIndex()) {
      throw new IllegalStateException("Cannot play to a winning palette.");
    }

    CardImpl cardToPlay = hand.remove(cardIdxInHand);
    palettes.get(paletteIdx).add(cardToPlay);

    if (winningPaletteIndex() != paletteIdx) {
      gameOver = true;
      gameWon = false;
      return;
    }

    canPlayToCanvas = true;

    updateGameOverStatus();
  }

  @Override
  public void playToCanvas(int cardIdxInHand) {
    if (!gameStarted || gameOver) {
      throw new IllegalStateException("The game has not started or is over.");
    }
    if (cardIdxInHand < 0 || cardIdxInHand >= hand.size()) {
      throw new IllegalArgumentException("Invalid card index in hand.");
    }
    if (!canPlayToCanvas) {
      throw new IllegalStateException("Cannot play to the canvas at this time.");
    }
    if (hand.size() == 1) {
      throw new IllegalStateException("Cannot play to the canvas when only one card remains in "
              + "hand.");
    }

    canvas = hand.remove(cardIdxInHand);
    canPlayToCanvas = false;

    updateGameOverStatus();
  }

  @Override
  public int numOfCardsInDeck() {
    if (!gameStarted) {
      throw new IllegalStateException("The game has not started.");
    }
    return deck.size();
  }

  @Override
  public int numPalettes() {
    if (!gameStarted) {
      throw new IllegalStateException("The game has not started.");
    }
    return palettes.size();
  }

  @Override
  public int winningPaletteIndex() {
    if (!gameStarted) {
      throw new IllegalStateException("The game has not started.");
    }

    List<PaletteScore> scores = new ArrayList<>();

    for (List<CardImpl> palette : palettes) {
      PaletteScore score = computePaletteScore(palette);
      scores.add(score);
    }

    PaletteScore highestScore = null;
    int winningIndex = -1;
    for (int i = 0; i < scores.size(); i++) {
      if (highestScore == null || scores.get(i).compareTo(highestScore) > 0) {
        highestScore = scores.get(i);
        winningIndex = i;
      }
    }

    return winningIndex;
  }

  @Override
  public boolean isGameOver() {
    if (!gameStarted) {
      throw new IllegalStateException("The game has not started.");
    }
    return gameOver;
  }

  @Override
  public boolean isGameWon() {
    if (!gameStarted) {
      throw new IllegalStateException("The game has not started.");
    }
    if (!gameOver) {
      throw new IllegalStateException("The game is not over yet.");
    }
    return gameWon;
  }

  @Override
  public List<CardImpl> getHand() {
    if (!gameStarted) {
      throw new IllegalStateException("The game has not started.");
    }
    return new ArrayList<>(hand);
  }

  @Override
  public List<CardImpl> getPalette(int paletteNum) {
    if (!gameStarted) {
      throw new IllegalStateException("The game has not started.");
    }
    if (paletteNum < 0 || paletteNum >= palettes.size()) {
      throw new IllegalArgumentException("Invalid palette number.");
    }
    return new ArrayList<>(palettes.get(paletteNum));
  }

  @Override
  public CardImpl getCanvas() {
    if (!gameStarted) {
      throw new IllegalStateException("The game has not started.");
    }
    return canvas;
  }

  @Override
  public List<CardImpl> getAllCards() {
    List<CardImpl> allCards = new ArrayList<>();
    for (String color : new String[]{"R", "O", "B", "I", "V"}) {
      for (int num = 1; num <= 7; num++) {
        allCards.add(new CardImpl(color, num));
      }
    }
    return allCards;
  }

  /**
   * Updates the game state to check if the game is over.
   * The game ends when both the hand and deck are empty, and the game is won if player 0 is the
   * winner.
   */
  protected void updateGameOverStatus() {
    if (hand.isEmpty() && deck.isEmpty()) {
      gameOver = true;
      gameWon = winningPaletteIndex() == 0;
    }
  }

  /**
   * Computes the score of a palette based on the current canvas rule.
   *
   * @param palette the palette to compute the score for
   * @return the computed PaletteScore
   */
  private PaletteScore computePaletteScore(List<CardImpl> palette) {
    switch (canvas.getColor()) {
      case "R":
        return computeRedScore(palette);
      case "O":
        return computeOrangeScore(palette);
      case "B":
        return computeBlueScore(palette);
      case "I":
        return computeIndigoScore(palette);
      case "V":
        return computeVioletScore(palette);
      default:
        throw new IllegalStateException("Unknown canvas rule.");
    }
  }

  /**
   * Determines if card c1 is higher than card c2 according to the Red rule.
   *
   * @param c1 the first card
   * @param c2 the second card
   * @return true if c1 is higher than c2, false otherwise
   */
  private boolean isHigherCard(CardImpl c1, CardImpl c2) {
    if (c1.getNumber() > c2.getNumber()) {
      return true;
    }
    if (c1.getNumber() < c2.getNumber()) {
      return false;
    }
    // If numbers are equal, compare colors based on rainbow order
    return getColorRank(c1.getColor()) > getColorRank(c2.getColor());
  }

  /**
   * Assigns a rank to each color based on the rainbow order.
   *
   * @param color the color of the card
   * @return the rank of the color
   */
  private int getColorRank(String color) {
    switch (color) {
      case "V":
        return 1;
      case "I":
        return 2;
      case "B":
        return 3;
      case "O":
        return 4;
      case "R":
        return 5;
      default:
        throw new IllegalArgumentException("Unknown card color: " + color);
    }
  }

  /**
   * Computes the score of a palette under the Red rule (highest card wins).
   *
   * @param palette the palette to compute the score for
   * @return the computed PaletteScore
   */
  private PaletteScore computeRedScore(List<CardImpl> palette) {
    CardImpl highestCard = null;
    for (CardImpl card : palette) {
      if (highestCard == null || isHigherCard(card, highestCard)) {
        highestCard = card;
      }
    }
    int score = highestCard != null ?
            highestCard.getNumber() * 100 + getColorRank(highestCard.getColor()) : 0;
    return new PaletteScore(score, highestCard);
  }

  /**
   * Computes the score of a palette under the Orange rule (most of one number).
   *
   * @param palette the palette to compute the score for
   * @return the computed PaletteScore
   */
  private PaletteScore computeOrangeScore(List<CardImpl> palette) {
    Map<Integer, Integer> numberCounts = new HashMap<>();
    for (CardImpl card : palette) {
      int num = card.getNumber();
      numberCounts.put(num, numberCounts.getOrDefault(num, 0) + 1);
    }
    int maxCount = 0;
    int numberWithMaxCount = 0;
    for (Map.Entry<Integer, Integer> entry : numberCounts.entrySet()) {
      int num = entry.getKey();
      int count = entry.getValue();
      if (count > maxCount || (count == maxCount && num > numberWithMaxCount)) {
        maxCount = count;
        numberWithMaxCount = num;
      }
    }
    CardImpl tieBreakerCard = null;
    for (CardImpl card : palette) {
      if (card.getNumber() == numberWithMaxCount) {
        if (tieBreakerCard == null || isHigherCard(card, tieBreakerCard)) {
          tieBreakerCard = card;
        }
      }
    }
    int score = maxCount * 100 + numberWithMaxCount;
    return new PaletteScore(score, tieBreakerCard);
  }

  /**
   * Computes the score of a palette under the Blue rule (most unique colors).
   *
   * @param palette the palette to compute the score for
   * @return the computed PaletteScore
   */
  private PaletteScore computeBlueScore(List<CardImpl> palette) {
    Set<String> uniqueColors = new HashSet<>();
    for (CardImpl card : palette) {
      uniqueColors.add(card.getColor());
    }
    int score = uniqueColors.size() * 100;
    CardImpl tieBreakerCard = null;
    for (CardImpl card : palette) {
      if (tieBreakerCard == null || isHigherCard(card, tieBreakerCard)) {
        tieBreakerCard = card;
      }
    }
    return new PaletteScore(score, tieBreakerCard);
  }

  /**
   * Computes the score of a palette under the Indigo rule (longest run).
   *
   * @param palette the palette to compute the score for
   * @return the computed PaletteScore
   */
  private PaletteScore computeIndigoScore(List<CardImpl> palette) {
    List<Integer> numbers =
            palette.stream().map(CardImpl::getNumber).distinct()
                    .sorted().collect(Collectors.toList());
    int longestRun = 1;
    int currentRun = 1;
    for (int i = 1; i < numbers.size(); i++) {
      if (numbers.get(i) == numbers.get(i - 1) + 1) {
        currentRun++;
        longestRun = Math.max(longestRun, currentRun);
      } else {
        currentRun = 1;
      }
    }
    int score = longestRun * 100;
    CardImpl tieBreakerCard = null;
    for (CardImpl card : palette) {
      if (tieBreakerCard == null || isHigherCard(card, tieBreakerCard)) {
        tieBreakerCard = card;
      }
    }
    return new PaletteScore(score, tieBreakerCard);
  }

  /**
   * Computes the score of a palette under the Violet rule (most cards below 4).
   *
   * @param palette the palette to compute the score for
   * @return the computed PaletteScore
   */
  private PaletteScore computeVioletScore(List<CardImpl> palette) {
    List<CardImpl> belowFourCards = new ArrayList<>();
    for (CardImpl card : palette) {
      if (card.getNumber() < 4) {
        belowFourCards.add(card);
      }
    }
    int score = belowFourCards.size() * 100;
    CardImpl tieBreakerCard = null;
    for (CardImpl card : belowFourCards) {
      if (tieBreakerCard == null || isHigherCard(card, tieBreakerCard)) {
        tieBreakerCard = card;
      }
    }
    return new PaletteScore(score, tieBreakerCard);
  }

  /**
   * Private class to hold the score of a palette according to the current rule.
   */
  private class PaletteScore implements Comparable<PaletteScore> {
    int mainScore;
    CardImpl tieBreakerCard;

    /**
     * Constructs a PaletteScore with the given main score and tie-breaker card.
     *
     * @param mainScore      the main score of the palette
     * @param tieBreakerCard the card used for tie-breaking
     */
    public PaletteScore(int mainScore, CardImpl tieBreakerCard) {
      this.mainScore = mainScore;
      this.tieBreakerCard = tieBreakerCard;
    }

    @Override
    public int compareTo(PaletteScore o) {
      if (this.mainScore != o.mainScore) {
        return Integer.compare(this.mainScore, o.mainScore);
      }
      if (this.tieBreakerCard != null && o.tieBreakerCard != null) {
        if (isHigherCard(this.tieBreakerCard, o.tieBreakerCard)) {
          return 1;
        } else if (isHigherCard(o.tieBreakerCard, this.tieBreakerCard)) {
          return -1;
        }
      }
      return 0;
    }
  }
}