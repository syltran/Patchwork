package fr.umlv.patchwork;
import java.util.Objects;
import java.util.Scanner;

public class Player {
  private final String name;
  private final int money;
  private final TimeToken timeToken;
  private final QuiltBoard board;
  
  public Player(String name, int money, TimeToken timeToken, QuiltBoard board) {
    Objects.requireNonNull(name, "name is null");
    Objects.requireNonNull(timeToken, "timeToken is null");
    Objects.requireNonNull(board, "board is null");
    this.name = name;
    this.money = money;
    this.timeToken = timeToken;
    this.board = board;
  }
  
  /**
   * Give the timeToken of the player.
   * @return timeToken.
   */
  public TimeToken timeToken() {
    return timeToken;
  }
   
  /**
   * Tests if the player can buy one of the three patches in front of the neutral token
   * @param circle, the circle of patches
   * @return true if it is
   */
  public boolean canBuyOneOfTheThreePatches(CircleOfPatches circle) {
    Objects.requireNonNull(circle, "circle is null");
    var list = circle.threePatchesFrontOfNeutralToken();
    return list.stream().anyMatch(patch -> patch.price() <= money);
  }
  
  /**
   * Return the code corresponding to the selected action.
   * @param circle, the circle of patches.
   * @return a code (int).
   */
  public int chooseActions(CircleOfPatches circle) {
    Objects.requireNonNull(circle, "circle is null");
    System.out.println("press key 'a' : advance and receive buttons");
    System.out.println("press key 'b' : take and place a patch");
    
    Scanner scanner = new Scanner(System.in);
    while (true) {
      var action = switch(scanner.nextLine()) {
        case "a" -> 1; 
        case "b" -> 2;
        default -> -1;
      };
      if (action == -1) {
        System.out.println("error : you can only press 'a' or 'b'");
      }
      else if (action == 2 && !canBuyOneOfTheThreePatches(circle)) {
        System.out.println("you don't have enough money. You have no choose, press 'a'");
      }
      else {
        return action;
      }
    }
  }
  
  /**
   * To know if the player is first
   * @return true if the player is first, else false.
   */
  public boolean isFirst() {
    var first = timeToken.first() == true ? true : false;
    return first;
  }
  
  /**
   * Update the value first of the player
   * @return Player with the new value first
   */
  public Player udpateFirst(Player opponent) {
    Objects.requireNonNull(opponent, "opponent is null");
    return new Player(name, money, timeToken.changeFirst(!opponent.isFirst()), board);
  }
  
  /**
   * Allows us to do the action "a" : advance and receive buttons
   * @param the timeToken of the opponent
   * @param the central time board
   * @return Player with the new value first
   */
  public Player advanceAndReceiveButtons(TimeToken opponent, CentralTimeBoard timeBoard) {
    Objects.requireNonNull(opponent, "opponent is null");
    Objects.requireNonNull(timeBoard, "timeBoard is null");
    var first = true;
    var newPosition = opponent.position() + 1;
    var newMoney = money + opponent.position() - timeToken.position() + 1;
    if (timeBoard.onTheLastSpace(opponent)) {
      newPosition -= 1;
      newMoney -= 1;
      first = false;
    }
    
    var newTimeToken = new TimeToken(timeToken.color(), newPosition, first);
    var player = new Player(name, newMoney, newTimeToken, board);
    player = player.resolveEventsIfMarkedSpacesHaveBeenPast(timeToken.position(), newTimeToken.position(), timeBoard);
    timeBoard.changeToken(player.timeToken);
    return player;
  }
  
  /**
   * Tests if the player has enough money to buy the patch
   * @param the patch that the player wants to buy
   * @return true if it is, else false
   */
  public boolean enoughMoneyToBuy(RegularPatch patch) {
    Objects.requireNonNull(patch, "patch is null");
    return money >= patch.price();
  }
  
  /**
   * Allows us to choose a patch in the circle
   * @param the circle of patches 
   * @return the regular patch chosen 
   */
  public RegularPatch choosePatch(CircleOfPatches circle) {
    Objects.requireNonNull(circle, "circle is null");
    Scanner scanner = new Scanner(System.in);
    
    System.out.println("choose a patch -> press key '1' or '2' or '3'");
    while (true) {
      var pick = switch(scanner.nextLine()) {
        case "1" -> 1;
        case "2" -> 2;
        case "3" -> 3;
        default -> -1;
      };
      if (pick == -1) {
        System.out.println("error : yon can only press key '1' or '2' or '3'");
      }
      else {
        var index = (circle.neutralToken() + pick) % circle.nbPatches();
        var patch = circle.getPatch(index);
        if (enoughMoneyToBuy(patch)) {
          return patch;
        }
        System.out.println("you don't have enough money to buy the patch " + pick);
      }
    }
  }
  
  /**
   * Pay a patch
   * @param the patch to buy
   * @return Player with the new value money
   */
  public Player payPatch(RegularPatch patch) {
    Objects.requireNonNull(patch, "patch is null");
    return new Player(name, money - patch.price(), timeToken, board);
  } 
  
  /**
   * Allows to put a patch in the quilt board
   * @param the patch to put
   */
  public void putPatchOnBoard(Patch patch) {
    Objects.requireNonNull(patch, "patch is null");
    Scanner scanner = new Scanner(System.in);
    
    while (true) {
      System.out.println("press 'z' 'q' 's' 'd' to move the patch, 't' to turn the patch and 'e' to confirm the place!\n");
      var superposition = false;
      var copy = patch.copyShape();
      board.initialize(); // reset the board
      board.actualizeBoard();
      if (board.superpositionBetweenPatches(patch)) {
        System.out.println("superposition !\n");
        superposition = true;
      }
      board.putPatch(patch);
      System.out.println(board);
      
      switch(scanner.nextLine()) {
        case "q" -> patch.move(-1, 0); // left
        case "d" -> patch.move(1, 0); // right
        case "z" -> patch.move(0, -1); // up
        case "s" -> patch.move(0, 1); // down
        case "t" -> patch.turn(); // turn
        case "e" -> { // confirm 
          if (!superposition) {
            board.add(patch);
            return;
          }
        }
        default -> patch.move(0, 0); // nothing : the patch doesn't move
      }
      if (!board.patchInsideBoard(patch)) {
        patch = patch.changeShape(copy); // if patch is not inside the board, it returns to its previous position
      }  
    }
  }
  
  /**
   * Allows to move the timeToken in the central time board
   * @param the patch purchased for his value time
   * @param the timeToken of the opponent
   * @param the timeBoard
   * @return Player with a new value TimeToken and money
   */
  public Player moveTimeToken(RegularPatch patch, TimeToken opponent, CentralTimeBoard timeBoard) {
    Objects.requireNonNull(patch, "patch is null");
    Objects.requireNonNull(opponent, "opponent is null");
    Objects.requireNonNull(timeBoard, "timeBoard is null");
    var first = false;
    var newPosition = timeToken.position() + patch.time();
    var lastSpace = timeBoard.nbSpaces() - 1;
    if (newPosition >= lastSpace) {
      newPosition = lastSpace;
    }
    if (newPosition > opponent.position()) {
      first = true;
    }
    
    var newTimeToken = new TimeToken(timeToken.color(), newPosition, first);
    var player = new Player(name, money, newTimeToken, board);
    player = player.resolveEventsIfMarkedSpacesHaveBeenPast(timeToken.position(), newTimeToken.position(), timeBoard);
    timeBoard.changeToken(player.timeToken);
    return player;
  }
  
   
  /**
   * Resolve events if marked spaces have been past 
   * @param the old position of the timeToken
   * @param the new position of the timeToken
   * @param the timeBoard
   * @return Player with a new value TimeToken and/or money
   */
  public Player resolveEventsIfMarkedSpacesHaveBeenPast(int oldPosition, int newPosition, CentralTimeBoard timeBoard) {
    Objects.requireNonNull(timeBoard, "timeBoard is null");
    var player = new Player(name, money, timeToken, board);
    
    for (var i = oldPosition; i <= newPosition; i++) {
      var event = timeBoard.getEvent(i);
      if (event != null) {
        player = player.resolveEvent(event, i, timeBoard);
      }
    }
    return player;
  }
  
  /**
   * Resolve the event.
   * @param the event.
   * @param the spaces where is the event (index)
   * @param the timeBoard.
   * @return Player with a new value money or a new special on his board.
   */
  public Player resolveEvent(Event event, int index, CentralTimeBoard timeBoard) {
    Objects.requireNonNull(event, "event is null");
    Objects.requireNonNull(timeBoard, "timeBoard is null");
    
    return switch(event) {
      case BUTTON -> buttonIncome();
      case SPECIALPATCH -> {
        var shape = new int[][] {{0, 0}};
        var patch = new SpecialPatch(shape);
        timeBoard.removeEvent(index);
        putPatchOnBoard(patch);
        yield new Player(name, money, timeToken, board);
      }
      default -> throw new IllegalArgumentException("the event : " + event + " doesn't exist");
    };
  }
  
  /**
   * Generate a button income.
   * @return Player with a new value money;
   */
  public Player buttonIncome() {
    var income = board.numberOfButtons();
    return new Player(name, money + income, timeToken, board);
  }
  
  /**
   * Get the final score of the player.
   * @return the final score.
   */
  public int finalScore() {
    var nbEmptySpaces = board.numberOfEmptySpace();
    return money - nbEmptySpaces * 2;
  }
  
  /**
   * The method toString
   * @return a string representing the object Player
   */
  @Override
  public String toString() {
    return "name:" + name + " money:" + money + "$ " + timeToken + "\n" + board;
  }
}
