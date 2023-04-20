package fr.umlv.patchwork;
import java.util.Objects;

public record TimeToken(String color, int position, boolean first) {
  public TimeToken {
    Objects.requireNonNull(color, "color is null");
    Objects.requireNonNull(first, "first is null");
    if (position < 0) {
      throw new IllegalArgumentException("position < 0");
    }
  }
  
  /**
   * Change the position of the TimeToken.
   * @param newPosition, the new position.
   * @return TimeToken with the new position.
   */
  public TimeToken changePosition(int newPosition) {
    return new TimeToken(color, newPosition, first);
  }
  
  /**
   * Change the value first of the TimeToken.
   * @param newFirst, the new value first.
   * @return TimeToken with the new value first.
   */
  public TimeToken changeFirst(boolean newFirst) {
    Objects.requireNonNull(newFirst, "newFirst is null");
    return new TimeToken(color, position, newFirst);
  }
  
  /**
   * The method equals
   * @param Object o 
   * @return true if the object o equals to the object thats call the method
   */
  @Override
  public boolean equals(Object o) {
    return o instanceof TimeToken token
        && color.equals(token.color);
  }
  
  /**
   * The method toString
   * @return a string representing the object TimeToken
   */
  @Override
  public String toString() {
    return "TimeToken:" + color;
  }
}
