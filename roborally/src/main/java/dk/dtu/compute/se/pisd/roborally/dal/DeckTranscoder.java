package dk.dtu.compute.se.pisd.roborally.dal;

import dk.dtu.compute.se.pisd.roborally.model.CommandCard;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;

public class DeckTranscoder {

    private Map<String, String> cardMap = new HashMap<>();
    
    public DeckTranscoder(){
        //String values are found in Command enum
        // F for forward
        cardMap.put("Fwd", "F");
        // R for right
        cardMap.put("Turn Right", "R");
        // L for left
        cardMap.put("Turn Left", "L");
        // D for double
        cardMap.put("Fast Fwd", "D");
        // C for choice
        cardMap.put("Left OR Right", "C");
    }

    public Map<String, String> getCardMap(){
        return this.cardMap;
    }

    public String encode(List<CommandCard> deck) {
        String string = deck.stream().map(
                (CommandCard card) -> this.getCardMap().get(card.getName().toString()))
                .collect(Collectors.joining());

        return string;
    }
}
