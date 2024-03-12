package dk.dtu.compute.se.pisd.roborally.model;


class TurnGear implements SpecialSpace{
    private String direction;

    public TurnGear(){
        this.direction = "left";    
    }

    public TurnGear(String direction){
        if (direction.toLowerCase() == "left" || direction.toLowerCase() == "right"){
            this.direction = direction.toLowerCase();
        }
        else {
            this.direction = "left";
        }
    }

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
