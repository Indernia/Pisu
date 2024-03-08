package dk.dtu.compute.se.pisd.roborally.model;


class TurnGear implements SpecialSpace{
    
    private String direction = "left";

    public void triggerEffect(Player player){
        if (direction == "left"){
            player.setHeading(player.getHeading().prev());
        }
        else {
            player.setHeading(player.getHeading().next());
        }
    }

    public String getType(){
        return "turnGear" + direction;
    }
}
