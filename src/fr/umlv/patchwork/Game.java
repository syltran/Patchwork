package fr.umlv.patchwork;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

public class Game {
  public static int chooseVersion() {
    Scanner scanner = new Scanner(System.in);
    System.out.println("choose version -> press '1' for the simplified version or '2' for the complete one");
    while (true) {
      var version = switch(scanner.nextLine()) {
        case "1" -> {
          System.out.println("simplified version has been chosen");
          yield 1;
        }
        case "2" -> {
          System.out.println("complete version has been chosen");
          yield 2;
        }
        default -> {
          System.out.println("error : you can only press '1' or '2'");
          yield -1;
        }
      };
      if (version != -1) {
        return version;
      }
    }
  }
  
  public static void main(String[] args) {
    Path path1;
    Path path2;
    var version = Game.chooseVersion();
    if (version == 1) {
      path1 = Path.of("txt/baseMarkedSpaces.txt");
      path2 = Path.of("txt/basePatches.txt");
    }
    else {
      path1 = Path.of("txt/completeMarkedSpaces.txt");
      path2 = Path.of("txt/completePatches.txt");
    }
    
    
    var player1 = new Player("player1", 5, new TimeToken("green", 0, false), new QuiltBoard(9, 9));
    var player2 = new Player("player2", 5, new TimeToken("yellow", 0, true), new QuiltBoard(9, 9));
    var centralTimeBoard = new CentralTimeBoard(54);
    var circleOfPatches = new CircleOfPatches(0);
  
    centralTimeBoard.addToken(player1.timeToken());
    centralTimeBoard.addToken(player2.timeToken());
    try {
      centralTimeBoard.loadMarkedSpaces(path1);
    } catch (IOException e) {
      System.err.println(e.getMessage());
      System.exit(1);
      return;
    }
    
    try {
      circleOfPatches.loadPatches(path2);
    } catch (IOException e) {
      System.err.println(e.getMessage());
      System.exit(1);
      return;
    }
    circleOfPatches.shufflePatches();
    circleOfPatches.frontOfSmallestPatch();
    
    while (!centralTimeBoard.endOfTheGame()) {
      System.out.println(centralTimeBoard);
      System.out.println(circleOfPatches);
      if (!player1.isFirst()) {
        System.out.println(player1);
        var action = player1.chooseActions(circleOfPatches);
        if (action == 1) {
          player1 = player1.advanceAndReceiveButtons(player2.timeToken(), centralTimeBoard);
          player2 = player2.udpateFirst(player1);
        }
        else {
          var patch = player1.choosePatch(circleOfPatches);
          circleOfPatches.moveNeutralTokenNextToChosenPatch(patch);
          player1 = player1.payPatch(patch);
          player1.putPatchOnBoard(patch);
          circleOfPatches.removePatch(patch);
          player1 = player1.moveTimeToken(patch, player2.timeToken(), centralTimeBoard);
          player2 = player2.udpateFirst(player1);
        }
      }
      else {
        System.out.println(player2);
        var action = player2.chooseActions(circleOfPatches);
        if (action == 1) {
          player2 = player2.advanceAndReceiveButtons(player1.timeToken(), centralTimeBoard);
          player1 = player1.udpateFirst(player2);
        }
        else {
          var patch = player2.choosePatch(circleOfPatches);
          circleOfPatches.moveNeutralTokenNextToChosenPatch(patch);
          player2 = player2.payPatch(patch);
          player2.putPatchOnBoard(patch);
          circleOfPatches.removePatch(patch);
          player2 = player2.moveTimeToken(patch, player1.timeToken(), centralTimeBoard);
          player1 = player1.udpateFirst(player2);
        }
      }
    }
    
    var scorePlayer1 = player1.finalScore();
    var scorePlayer2 = player2.finalScore();
    if (scorePlayer1 > scorePlayer2) {
      System.out.println("player1 wins");
    }
    else if (scorePlayer1 < scorePlayer2) {
      System.out.println("player2 wins");
    }
    else {
      if (player1.isFirst()) {
        System.out.println("player1 wins");
      }
      else {
        System.out.println("player2 wins");
      }
    }
  }
}
