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

    public Checkpoint(int checkpointNumber) {
        this.checkpointNumber = checkpointNumber;
    }

    public Checkpoint() {

    }

    public int getCheckpointNumber() {
        return checkpointNumber;
    }

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

    @Override
    public String getType() {
        return "Checkpoint";
    }

    public void setCheckpointNumber(int number) {
        this.checkpointNumber = number;
    }
}
