package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

public class Checkpoint extends FieldAction {

    private int checkpointNumber;

    public Checkpoint(int checkpointNumber) {
        this.checkpointNumber = checkpointNumber;
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
}
