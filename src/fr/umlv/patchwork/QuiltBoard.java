package fr.umlv.patchwork;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiPredicate;

public class QuiltBoard {
  private final int [][] board;
  private final int nbLines;
  private final int nbColumns;
  private final ArrayList<Patch> patches;
 
  public QuiltBoard(int nbLines, int nbColumns) {
    this.nbLines = nbLines;
    this.nbColumns = nbColumns;
    board = new int[nbLines][nbColumns];
    patches = new ArrayList<>();
  }
  
  /**
   * Initialize the board.
   */
  public void initialize() {
    for (var i = 0; i < nbLines; i++) {
      for (var j = 0; j < nbColumns; j++) {
        board[i][j] = 0;
      }
    }
  }
  
  /**
   * Add the patch to the list of patches.
   * @param the patch to add.
   */
  public void add(Patch patch) {
    Objects.requireNonNull(patch, "patch is null");
    patches.add(patch);
  }
 
  /**
   * Tests if the patch overlaps with another one.
   * @param the patch to be tested.
   * @return true if there is a superposition, else false.
   */
  public boolean superpositionBetweenPatches(Patch patch) {
    Objects.requireNonNull(patch, "patch is null");
    var shape = patch.copyShape();
    return Arrays.stream(shape).anyMatch(square -> board[square[1]][square[0]] == 1);
  }
  
  /**
   * Tests if the patch is inside the board.
   * @param the patch to be tested.
   * @return true if the patch is inside, else false.
   */
  public boolean patchInsideBoard(Patch patch) {
    Objects.requireNonNull(patch, "patch is null");
    BiPredicate<Integer, Integer> predicate = (x, y) -> (x >= 0 && x < nbColumns) && (y >= 0 && y < nbLines);
    var shape = patch.copyShape();
    return Arrays.stream(shape).allMatch(square -> predicate.test(square[1], square[0]));
  }
  
  /**
   * Put a path in the board.
   * @param the patch to put.
   */
  public void putPatch(Patch patch) {
    Objects.requireNonNull(patch, "patch is null"); 
    for (var i = 0; i < patch.nbSquares(); i++) {
      board[patch.getValue(i, 1)][patch.getValue(i, 0)] = 1;
    }
  }
  
  /**
   * Actualize the board.
   */
  public void actualizeBoard() {
    for (var patch : patches) {
      putPatch(patch);
    }
  }
  
  /**
   * Give the number of empty spaces in the board.
   * @return the number of empty spaces.
   */
  public int numberOfEmptySpace() {
    var cmpt = 0;
    for (var i = 0; i < nbLines; i++) {
      for (var j = 0; j < nbColumns; j++) {
        if (board[i][j] == 0) {
          cmpt++;
        }
      }
    }
    return cmpt;
  }
  
  /**
   * Give the number of buttons in the board.
   * @return the number of buttons.
   */
  public int numberOfButtons() {
    return patches.stream().mapToInt(Patch::buttons).sum();
  }
  
  
  /**
   * The method toString.
   * @return a string representing the object QuiltBoard.
   */
  @Override
  public String toString() {
    var builder = new StringBuilder();
    
    for (var i = 0; i < nbLines; i++) {
      for (var j = 0; j < nbColumns; j++) {
        if (board[i][j] == 0) {
          builder.append(" . ");
        }
        else {
          builder.append(" o ");
        }
      }
      builder.append("\n");
    }
    return builder.toString();
  }
}
