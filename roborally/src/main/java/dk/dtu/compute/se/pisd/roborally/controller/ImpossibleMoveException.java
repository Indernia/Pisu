package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

/**
 * This is an exception class made to handle impossible moves
 * 
 * @author Andreas Jensen
 */
public class ImpossibleMoveException extends Exception {
    private Player player;
    private Space space;
    private Heading heading;

    public ImpossibleMoveException(Player player, Space space, Heading heading) {
        super("Impossible move");
        this.space = space;
        this.player = player;
        this.heading = heading;
    }
}
