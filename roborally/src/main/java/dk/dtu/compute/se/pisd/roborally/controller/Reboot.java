package dk.dtu.compute.se.pisd.roborally.controller;
import org.jetbrains.annotations.NotNull;
import dk.dtu.compute.se.pisd.roborally.model.Space;


public class Reboot extends FieldAction {

    public Reboot(){

    }
    /**
     * Implementation of the action field reboot
     *  @param gameController the given controller for the current instance
     * @param space a given space that will have the pit action on it
     * @author Noah Nissen
     */
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        return true;
    }

    /**
     *  gets the special space
     *  @return type as String
     */
    @Override
    public String getType(){
        return "Reboot";
    }

}
