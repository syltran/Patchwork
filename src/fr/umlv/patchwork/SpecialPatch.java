package fr.umlv.patchwork;
import java.util.Objects;

public final class SpecialPatch implements Patch {
  private final int[][] shape;
  
  public SpecialPatch(int[][] shape){
    Objects.requireNonNull(shape, "shape is null");
    if (shape.length != 1) {
      throw new IllegalArgumentException("the length of the shape is not 1");
    }
    this.shape = shape;
  }
  
  /**
   * Give the number of buttons the patch contains
   * @return 0.
   */
  public int buttons() {
    return 0;
  }
  
  /**
   * Give the number of squares the patch contains
   * @return 1.
   */
  public int nbSquares() {
    return 1;
  }
  
  /**
   * Give the value shape[i][j].
   * @param i, the first index (line).
   * @param j, the second index (column).
   * @return the value shape[i][j].
   */
  public int getValue(int i, int j) {
    return shape[i][j];
  }
  
  /**
   * Give a copy of the shape.
   * @return the copy of the shape.
   */
  public int[][] copyShape() {
    var copy = new int[1][2];
    copy[0][0] = shape[0][0];
    copy[0][1] = shape[0][1];
    return copy;
  }
  
  /**
   * Change the shape of the patch.
   * @param newShape, the new shape of the patch.
   * @return SpecialPatch with the new shape.
   */
  public SpecialPatch changeShape(int[][] newShape) {
    return new SpecialPatch(newShape);
  }
  
  /**
   * Move the shape of the patch.
   * @param x, the value x to add to the x coordinates of the shape.
   * @param y, the value y to add to the y coordinates of the shape.
   */
  public void move(int x, int y) {
    shape[0][0] += x;
    shape[0][1] += y;
  }
  
  /**
   * Turn the shape of the patch.
   * @return nothing.
   */
  public void turn() {
    return;
  }
}