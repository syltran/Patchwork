package fr.umlv.patchwork;
import java.util.ArrayList;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CircleOfPatches {
  private int neutralToken;
  private final ArrayList<RegularPatch> patches;
  
  public CircleOfPatches(int neutralToken) {
    this.neutralToken = neutralToken;
    patches = new ArrayList<>();
  }

  /**
   * Give the neutralToken of the circle.
   * @return neutralToken.
   */
  public int neutralToken() {
    return neutralToken;
  }
  
  /**
   * Give the number of patches in the circle
   * @return the size of the list patches.
   */
  public int nbPatches() {
    return patches.size();
  }
  
  /**
   * Load the patches from a path (or file)
   */
  public void loadPatches(Path path) throws IOException {
    Objects.requireNonNull(path, "path is null");
    try(var reader = Files.newBufferedReader(path)) {
      String line;
      while ((line = reader.readLine()) != null) {
        var patch = RegularPatch.fromText(line);
        patches.add(patch);
      }
    }
  }
  
  /**
   * Shuffle the list of patches
   */
  public void shufflePatches() {
    Collections.shuffle(patches);
  }
  
  /**
   * Move the neutral token front of the smallest patch
   */
  public void frontOfSmallestPatch() {
    Optional<RegularPatch> smallest = patches.stream().min(Comparator.comparingInt(RegularPatch::nbSquares));
    var index = patches.indexOf(smallest.orElseThrow());
    neutralToken = index;
  }
  
  /**
   * Move the neutral token next to the chosen patch
   * @param the patch chosen
   */
  public void moveNeutralTokenNextToChosenPatch(RegularPatch patch) {
    Objects.requireNonNull(patch, "patch is null");
    var index = patches.indexOf(patch);
    if (index == -1) {
      throw new IllegalArgumentException("this patch doesn't exist");
    }
    neutralToken = index - 1; // - 1 because the patch will be remove from the circle
  }
  
  /**
   * Get a patch from the list
   * @param the index of the patch in the list.
   */
  public RegularPatch getPatch(int index) {
    if (index < 0 || index >= patches.size()) {
      throw new IllegalArgumentException("index ouf of range");
    }
    return patches.get(index);
  }
  
  /**
   * Remove a path from the list
   * @param the patch to remove
   */
  public void removePatch(RegularPatch patch) {
    Objects.requireNonNull(patch, "patch is null");
    patches.remove(patch);
  }
  
  /**
   * Give a list that contains the three patches in front of the neutral token
   */
  public List<RegularPatch> threePatchesFrontOfNeutralToken() {
    var list = new ArrayList<RegularPatch>();
    
    for (var i = 1; i <= 3; i++) {
      var index = (neutralToken + i) % patches.size();
      var patch = patches.get(index);
      if (!list.contains(patch)) {
        list.add(patch);
      }
    }
    return list;
  }
  
  /**
   * The method toString
   * @return a string representing the object CircleOfPatch
   */
  @Override
  public String toString() {
    var builder = new StringBuilder();
    var list = new ArrayList<Integer>();
    
    for (var i = 1; i <= 9; i++) {
      var index = (neutralToken + i) % patches.size();
      if (!list.contains(index)) {
        builder.append(patches.get(index));
      }
      list.add(index);
    }
    return builder.toString();
  } 
}
