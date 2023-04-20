package fr.umlv.patchwork;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class CentralTimeBoard {
  private final int nbSpaces;
  private final HashMap<Integer, Event> markedSpaces;
  private final ArrayList<TimeToken> tokens;
  
  public CentralTimeBoard(int nbSpaces) {
    this.nbSpaces = nbSpaces;
    markedSpaces = new HashMap<>();
    tokens = new ArrayList<>();
  }
  
  /**
   * Give the numbers of spaces in the central time board
   * @return nbSpaces
   */
  public int nbSpaces() {
    return nbSpaces;
  }
  
  /**
   * Add a event
   */
  public void addEvent(int index, Event event) {
    Objects.requireNonNull(event, "event is null");
    markedSpaces.put(index, event);
  }
  
  /**
   * Loads all the events from a path (file)
   */
  public void loadMarkedSpaces(Path path) throws IOException {
    Objects.requireNonNull(path, "path is null");
    try(var reader = Files.newBufferedReader(path)) {
      String line;
      while ((line = reader.readLine()) != null) {
        var array = line.split(":");
        var index = Integer.parseInt(array[0]);
        var event = array[1] == "button" ? Event.BUTTON : Event.SPECIALPATCH;
        addEvent(index, event);
      }
    }
  }
  
  /**
   * Get the event at the space index if it exists, else null if it doesn't
   * @param the index of the event
   * @return true or false;
   */
  public Event getEvent(int index) {
    return markedSpaces.getOrDefault(index, null);
  }
  
  /**
   * Remove the event at the space index
   * @param the index of the event
   */
  public void removeEvent(int index) {
    var value = markedSpaces.remove(index);
    if (value == null) {
      throw new IllegalArgumentException("there is no event in space " + index);
    }
  }
  
  /**
   * Add time token in the list tokens.
   * @param the token to add.
   */
  public void addToken(TimeToken token) {
    Objects.requireNonNull(token, "token is null");
    tokens.add(token);
  }
  
  /**
   * Change a timetoken in the list by the new one
   * @rparam the newToken
   */
  public void changeToken(TimeToken newToken) {
    if (!tokens.contains(newToken)) {
      throw new IllegalArgumentException("this token doesn't exist in the list tokens");
    }
    for (var i = 0; i < tokens.size(); i++) {
      if (tokens.get(i).equals(newToken)) {
        tokens.set(i, newToken);
      }
    }
  }
  
  /**
   * Tests if the token is on the last space of the central time board.
   * @return true if is it, else false.
   */
  public boolean onTheLastSpace(TimeToken token) {
    Objects.requireNonNull(token, "token is null");
    return token.position() == nbSpaces - 1;
  }
  
  /**
   * Tests if is it the end of the Game.
   * @return true if is it, else false.
   */
  public boolean endOfTheGame() {
    return tokens.stream().allMatch(timeToken -> onTheLastSpace(timeToken));
  }

  /**
   * The method toString
   * @return a string representing the object CentralTimeBoard
   */
  @Override
  public String toString() {
    var builder = new StringBuilder();
    builder.append("| ");
    for (var i = 0; i < nbSpaces; i++) {
      if (i == nbSpaces - 1) {
        builder.append("END ");
      }
      
      var event = getEvent(i);
      if (event != null) {
        switch(event) {
          case BUTTON -> builder.append("o ");
          case SPECIALPATCH -> builder.append("[p] ");
          default -> builder.append("");
        }
      }
      
      for (var timeToken :tokens) {
        if (timeToken.position() == i) {
          builder.append(timeToken.color()).append(" ");
        }
      }
      builder.append(" | ");
    }
    return builder.toString();
  } 
}
