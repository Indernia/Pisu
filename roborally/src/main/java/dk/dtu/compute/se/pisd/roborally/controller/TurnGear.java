package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.model.Player;

// @author Alex Lundberg
public class TurnGear extends FieldAction {


    private String direction;

    public TurnGear(String direction){
        setDirection(direction);
    }

    public void setDirection(String direction){
        if (direction.toLowerCase() == "left" || direction.toLowerCase() == "right"){
            this.direction = direction.toLowerCase();
        } else {
            this.direction = "left";
        }
    }

    @Override
    public boolean doAction(GameController gameController, Space space){
        Player player;
        if (space.getPlayer() != null){
            player = space.getPlayer();
        } else{
            player = gameController.board.getCurrentPlayer();
        }

        if (this.direction == "left"){
            gameController.turnLeft(player);
            return true;
        }
        else if (this.direction == "right"){
            gameController.turnRight(player);
            return true;
        }
        else{
            return false;
        }

    }

    public String getDirection(){
        return this.direction;
    }

    @Override
    public String getType(){
        return "gear";
    }

}


