package dk.dtu.compute.se.pisd.roborally.controller;
import org.jetbrains.annotations.NotNull;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.model.Player;


public class Pit extends FieldAction {

    public Pit(){

    }
    /**
     * Implementation of the action of a pit.
     * takes a player and "kills" them.
     *  @param gameController the given controller for the current instance
     * @param space a given space that will have the pit action on it
     * @author Noah Nissen
     */
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        Player player = space.getPlayer();
        gameController.die(player, space);
        return true;
    }

    /**
     *  gets the special space
     *  @return type as String
     */
    @Override
    public String getType(){
        return "Pit";
    }

}
