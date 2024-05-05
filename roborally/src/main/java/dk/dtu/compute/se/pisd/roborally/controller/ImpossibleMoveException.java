package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

/**
 * This class represents an impossible move exception.
 * This exception is thrown when a player tries to move to a space that is not
 * possible to move to.
 * 
 * @author Andreas (s235455)
 */
public class ImpossibleMoveException extends Exception {
  private Player player;
  private Space space;
  private Heading heading;

  /**
   * Constructor
   * 
   * @param player  the player that is trying to move
   * @param space   the space that the player is trying to move to
   * @param heading the heading that the player is trying to move in
   */
  public ImpossibleMoveException(Player player, Space space, Heading heading) {
    super("Impossible move");
    this.space = space;
    this.player = player;
    this.heading = heading;
  }
}
