package dk.dtu.compute.se.pisd.roborally.dal;

import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborally.model.CommandCard;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Arrays;
import java.util.ArrayList;

public class DeckTranscoder {

    private Map<String, String> encodingMap = new HashMap<>();
    private Map<String, Command> decodingMap = new HashMap<>();
    
    public DeckTranscoder(){
        //String values are found in Command enum
        // F for forward
        encodingMap.put("Fwd", "F");
        // R for right
        encodingMap.put("Turn Right", "R");
        // L for left
        encodingMap.put("Turn Left", "L");
        // D for double
        encodingMap.put("Fast Fwd", "D");
        // C for choice
        encodingMap.put("Left OR Right", "C");


        decodingMap.put("F", Command.FORWARD);
        decodingMap.put("R", Command.RIGHT);
        decodingMap.put("L", Command.LEFT);
        decodingMap.put("D", Command.FAST_FORWARD);
        decodingMap.put("C", Command.OPTION_LEFT_RIGHT);
    }



    public Map<String, String> getCardMap(){
        return this.encodingMap;
    }

    public String encode(List<CommandCard> deck) {
        String string = deck.stream()
            .map((CommandCard card) -> this.getCardMap().get(card.getName().toString()))
            .collect(Collectors.joining());

        return string;
    }


   public List<CommandCard> decode(String string){
       List<String> list = new ArrayList<>(Arrays.asList(string.split("")));
        return list.stream()
           .filter((String encodedCard) -> encodedCard != "")
           .map((String encodedCard) -> new CommandCard(decodingMap.get(encodedCard)))
           .toList();
   }
}
