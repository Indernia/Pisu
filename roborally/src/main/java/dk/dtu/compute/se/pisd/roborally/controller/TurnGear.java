package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Space;

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
        return false;

    }

    @Override
    public String getType(){
        return "gear";
    }

}


