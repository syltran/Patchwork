package fr.umlv.patchwork;
import java.util.Arrays;
import java.util.Objects;

public final class RegularPatch implements Patch {
  private final int price; // price of the patch
  private final int time; // number of spaces we’ll have to move forward
  private final int buttons; // number of buttons won
  private final int[][] shape; // geometric shape of the patch
  
  public RegularPatch(int price, int time, int buttons, int[][] shape) {
    Objects.requireNonNull(shape, "shape is null");
    this.price = price;
    this.time = time;
    this.buttons = buttons;
    this.shape = shape;
  }
  
  /**
   * Give the price of the patch.
   * @return price.
   */
  public int price() {
    return price;
  }
  
  /**
   * Give the time of the patch.
   * @return time.
   */
  public int time() {
    return time;
  }
  
  /**
   * Give the number of buttons that the patch contains.
   * @return buttons.
   */
  public int buttons() {
    return buttons;
  }
  
  /**
   * Give the number of squares that the patch contains.
   * @return the length of the shape.
   */
  public int nbSquares() {
    return shape.length;
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
    var copy = new int[shape.length][2];
    for (var i = 0; i < shape.length; i++) {
      copy[i][0] = shape[i][0];
      copy[i][1] = shape[i][1];
    }
    return copy;
  }
  
  /**
   * Change the shape of the patch.
   * @param newShape, the new shape of the patch.
   * @return RegularPatch with the new shape.
   */
  public RegularPatch changeShape(int[][] newShape) {
    Objects.requireNonNull(newShape, "newShape is null");
    return new RegularPatch(price, time, buttons, newShape);
  }
  
  /**
   * Get the maximal value x of the shape.
   * @return the maximal value x.
   */
  public int getMaxValueX() {
    return Arrays.stream(shape).mapToInt(square -> square[0]).max().orElseThrow();
  }
  
  /**
   * Get the maximal value y of the shape.
   * @return the maximal value y.
   */
  public int getMaxValueY() {
    return Arrays.stream(shape).mapToInt(square -> square[1]).max().orElseThrow();
  }
  
  
  /**
   * Move the shape of the patch.
   * @param x, the value x to add to the x coordinates of the shape.
   * @param y, the value y to add to the y coordinates of the shape.
   */
  public void move(int x, int y) {
    for (var i = 0; i < shape.length; i++) {
      shape[i][0] += x;
      shape[i][1] += y;
    }
  }
  
  /**
   * Turn the shape of the patch.
   */
  public void turn(){
    var start = 0;
    var end = shape.length;
    while (start < end) {
      var tmp1 = shape[start][0];
      var tmp2 = shape[end - 1][0];
      
      var i = start;
      for (; i < end; i++) {
        if (shape[i][0] != tmp1) {
          break;
        }
        shape[i][0] = tmp2;
      }
      start = i;
      
      var j = end - 1;
      for (; j >= start; j--) {
        if (shape[j][0] != tmp2) {
          break;
        }
        shape[j][0] = tmp1;
      }
      end = j + 1;
    }
  }
  
  /**
   * Convert a text to a RegularPatch
   * @return RegularPatch with the informations from the text
   */
  public static RegularPatch fromText(String text) {
    Objects.requireNonNull(text, "text is null");
    var array = text.split(":");   
    var squares = array[array.length - 1].split(",");
    var shape = new int[squares.length][2];
 
    for (var i = 0; i < shape.length; i++) {
      shape[i][0] = Integer.parseInt(squares[i].substring(0, 1));
      shape[i][1] = Integer.parseInt(squares[i].substring(1, 2));
    }
    
    var price = Integer.parseInt(array[0]);
    var time = Integer.parseInt(array[1]);
    var buttons = Integer.parseInt(array[2]);
    return new RegularPatch(price, time, buttons, shape);
  }
  
  /**
   * The method toString
   * @return a string representing the object RegularPatch
   */
  @Override
  public String toString() {
    var builder = new StringBuilder();
    var board = new int[getMaxValueY() + 1][getMaxValueX() + 1];
    Arrays.stream(shape).forEach(square -> board[square[1]][square[0]] = 1);
    
    builder.append("price:" + price + "$ time:" + time + " buttons:" + buttons + "o\n");
    for (var i = 0; i < board.length; i++) {
      for (var j = 0; j < board[i].length; j++) {
        if (board[i][j] == 1) {
          builder.append("o");
        }
        else {
          builder.append(" ");
        }
      }
      builder.append("\n");
    }
    return builder.toString();
  }
  
  /**
   * The method equals
   * @param Object o 
   * @return true if the object o equals to the object thats call the method
   */
  @Override
  public boolean equals(Object o) {
    return o instanceof RegularPatch patch 
      && price == patch.price
      && time == patch.time
      && buttons == patch.buttons
      && Arrays.deepEquals(shape, patch.shape);
  }
}
