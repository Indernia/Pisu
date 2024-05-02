package dk.dtu.compute.se.pisd.roborally.controller;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

/**
 * Represents checkpoint action on the board.
 * When a player lands on a checkpoint space, their current checkpoint is updated if this checkpoint
 * is the next in sequence.
 *
 * @author Julius Sondergaard, s234096
 */
public class Checkpoint extends FieldAction {

    public int checkpointNumber;

    /**
     * Constructor
     * @param checkpointNumber the number of the checkpoint
     */
    public Checkpoint(int checkpointNumber) {
        this.checkpointNumber = checkpointNumber;
    }

    /**
     * Default constructor
     */
    public Checkpoint() {

    }
    
    /**
     * Returns the checkpoint number
     * @return the checkpoint number
     */
    public int getCheckpointNumber() {
        return checkpointNumber;
    }

    /**
     * Implementation of the checkpoint
     * determines if the player has landed on the correct checkpoint
     *  @param gameController the given controller for the current instance
     * @param space a given space that will have the belt action on it
     * @return true if the player has landed on the correct checkpoint
     * @return false if the player has not landed on the correct checkpoint
     */
    @Override
    public boolean doAction(GameController gameController, Space space) {
        Player player = space.getPlayer();
        if (player != null) {
            if (player.getCurrentCheckpoint() + 1 == this.checkpointNumber) {
                player.setCurrentCheckpoint(this.checkpointNumber);
                return true;
            }
        }
        return false;
    }

    /**
     *  gets the special space
     *  @return type as String
     */
    @Override
    public String getType() {
        return "Checkpoint";
    }

    /**
     * Sets the checkpoint number
     * @param number the number of the checkpoint
     */
    public void setCheckpointNumber(int number) {
        this.checkpointNumber = number;
    }
}
