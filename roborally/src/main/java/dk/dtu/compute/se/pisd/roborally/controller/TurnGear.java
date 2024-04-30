package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.model.Player;

/**
 * Represents the turn gear action on the board.
 * When a player lands on a turn gear space, they are turned left or right.
 *
 * @author Alex Lundberg
 */
public class TurnGear extends FieldAction {

    private String direction;

    /**
     * Default constructor
     */
    public TurnGear() {
        this.direction = "left";
    }

    /**
     * Constructor
     * @param direction the direction to turn
     */
    public TurnGear(String direction) {
        setDirection(direction);
    }

    /**
     * Sets the direction to turn
     * @param direction the direction to turn
     */
    public void setDirection(String direction) {
        if (direction.toLowerCase() == "left" || direction.toLowerCase() == "right") {
            this.direction = direction.toLowerCase();
        } else {
            this.direction = "left";
        }
    }

    @Override
    /**
     * Implementation of the action of a turn gear.
     * takes a player and turns them left or right
     *  @param gameController the given controller for the current instance
     * @param space a given space that will have the turn gear action on it
     * @return true if the player has been turned
     * @return false if the player has not been turned
     */
    public boolean doAction(GameController gameController, Space space) {
        Player player;
        if (space.getPlayer() != null) {
            player = space.getPlayer();
        } else {
            player = gameController.board.getCurrentPlayer();
        }

        if (this.direction == "left") {
            gameController.turnLeft(player);
            return true;
        } else if (this.direction == "right") {
            gameController.turnRight(player);
            return true;
        } else {
            return false;
        }

    }

    /**
     *  gets the special space
     *  @return type as String
     */
    public String getDirection() {
        return this.direction;
    }

    @Override
    /**
     *  gets the special space
     *  @return type as String
     */
    public String getType() {
        return "gear";
    }

}
