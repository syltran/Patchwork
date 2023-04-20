package fr.umlv.patchwork;

public sealed interface Patch permits RegularPatch, SpecialPatch{
  int buttons();
  int nbSquares();
  int getValue(int i, int j);
  int[][] copyShape();
  Patch changeShape(int[][] newShape);
  void move(int x, int y);
  public void turn();
}
