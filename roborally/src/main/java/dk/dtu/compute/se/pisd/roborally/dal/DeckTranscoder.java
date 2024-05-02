package dk.dtu.compute.se.pisd.roborally.dal;


import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborally.model.CommandCard;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;

import java.util.Arrays;
import java.util.ArrayList;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.CommandCardField;

/**
 * This class is responsible for encoding and decoding the deck of command cards.
 * It is used to convert the deck of command cards to a string and vice versa.
 *
 * @author Alex Lundberg, s235442
 */
public class DeckTranscoder {

    private Map<String, String> encodingMap = new HashMap<>();
    private Map<String, Command> decodingMap = new HashMap<>();
    
    /**
     * Default constructor
     */
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
        //S for SPAM
        encodingMap.put("SPAM", "S");



        decodingMap.put("F", Command.FORWARD);
        decodingMap.put("R", Command.RIGHT);
        decodingMap.put("L", Command.LEFT);
        decodingMap.put("D", Command.FAST_FORWARD);
        decodingMap.put("C", Command.OPTION_LEFT_RIGHT);
        decodingMap.put("S", Command.SPAM);
    }



    /**
     * Returns the encoding map
     * @return encodingMap
     */
    public Map<String, String> getCardMap(){
        return this.encodingMap;
    }

    /**
     * Encodes a list of command cards to a string
     * @param deck   the list of command cards to encode
     * @return the encoded string
     */
    public String encode(List<CommandCard> deck) {
        if (deck.size() == 0){
            return ""; 
        }
        String string = deck.stream()
            .filter((CommandCard card) -> card != null)
            .filter((CommandCard card) -> card.getName() != null)
            .map((CommandCard card) -> this.getCardMap().getOrDefault(card.getName().toString(), "n"))
            .collect(Collectors.joining());

        return string;
    }


    /**
     * Encodes a list of command cards to a string
     * @param string the string to encode
     * @param player the player to assign the command cards to
     * @return a list of {@link CommandCardField}
     */
   public List<CommandCardField> decodeAsField(String string, Player player){
       if (string == "" || string.isEmpty()){
           return new ArrayList<CommandCardField>();
       }
       List<String> list = new ArrayList<>(Arrays.asList(string.toUpperCase().split("")));
        return list.stream()
           .filter((String encodedCard) -> encodedCard != "")
           .map((String encodedCard) -> new CommandCard(decodingMap.get(encodedCard)))
           .map((CommandCard card) -> new CommandCardField(player, card))
           .toList();
   }

   /**
    * Decodes a string to a list of command cards
    * @param string the string to decode
    * @return the list of {@link CommandCard}s
    */
   public List<CommandCard> decode(String string){
       if (string == "" || string.isEmpty()){
           return new ArrayList<CommandCard>();
       }
       List<String> list = new ArrayList<>(Arrays.asList(string.split("")));
        return list.stream()
           .filter((String encodedCard) -> encodedCard != "")
           .map((String encodedCard) -> new CommandCard(decodingMap.get(encodedCard)))
           .toList();
   }

}
