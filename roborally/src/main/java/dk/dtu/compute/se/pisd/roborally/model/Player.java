/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import org.jetbrains.annotations.NotNull;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.SOUTH;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk, Alex Lundberg (s235442)
 *
 */
public class Player extends Subject{

    final public static int NO_REGISTERS = 5;
    final public static int NO_CARDS = 8;

    final public Board board;

    private String name;
    private String color;

    private Space space;
    private Space deathSpace;
    private Heading heading = SOUTH;

    private CommandCardField[] program;
    private CommandCardField[] cards;

    private List<CommandCard> deck;
    private List<CommandCard> discardDeck;
  
    public int priority = 0;
    public double distanceToAntenna = 0;

    
    private int currentCheckpoint;

    /**
     * gets the current checkpoint of the player
     *
     * @return int of the current checkpoint
     */
    public int getCurrentCheckpoint() {
        return currentCheckpoint;
    }

    /**
     * sets the current checkpoint of the player
     *
     * @param currentCheckpoint as int
     */
    public void setCurrentCheckpoint(int currentCheckpoint) {
        this.currentCheckpoint = currentCheckpoint;
    }

    /**
     * constructor for player, needs board, color and name
     * 
     * @param board board the player is on
     * @param color color of the player 
     * @param name  name of the player
     */
    public Player(@NotNull Board board, String color, @NotNull String name) {
        this.board = board;
        this.name = name;
        this.color = color;

        this.space = null;
        this.currentCheckpoint = 0;

        program = new CommandCardField[NO_REGISTERS];
        for (int i = 0; i < program.length; i++) {
            program[i] = new CommandCardField(this);
        }

        cards = new CommandCardField[NO_CARDS];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new CommandCardField(this);
        }
        this.discardDeck = new ArrayList<CommandCard>();
    }

    /**
     * Returns the name of the player
     * 
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the player
     * 
     * @param name the name of the player
     */
    public void setName(String name) {
        if (name != null && !name.equals(this.name)) {
            this.name = name;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    /**
     * Returns the color of the player
     * Re
     * 
     * @return
     */
    public String getColor() {
        return color;
    }

    /**
     * Sets the color of the player
     * 
     * @param color the color of the player
     */
    public void setColor(String color) {
        this.color = color;
        notifyChange();
        if (space != null) {
            space.playerChanged();
        }
    }

    /**
     * Gets the space the player is standing on
     * 
     * @return
     */
    public Space getSpace() {
        return space;
    }

    /**
     * Sets the space the player is on
     * 
     * @param space the space the player is on
     */
    public void setSpace(Space space) {
        Space oldSpace = this.space;
        if (space != oldSpace &&
                (space == null || space.board == this.board)) {
            this.space = space;
            if (oldSpace != null) {
                oldSpace.setPlayer(null);
            }
            if (space != null) {
                space.setPlayer(this);
            }
            notifyChange();
        }
    }

    /**
     * Returns the heading of the player, so which way it is facing
     * 
     * @return
     */
    public Heading getHeading() {
        return heading;
    }

    /**
     * Sets the heading of the player
     * 
     * @param heading the heading of the player
     */
    public void setHeading(@NotNull Heading heading) {
        if (heading != this.heading) {
            this.heading = heading;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    /**
     * Returns the program of the player at a given index
     *
     * @param i index of the program field
     * @return {@link CommandCardField} 
     */
    public CommandCardField getProgramField(int i) {
        return program[i];
    }

    /**
     * sets the program field to the given card
     *
     * @param i index of the program field
     * @param card the card to be set
     */
    public void setProgramField(int i,CommandCard card) {
        program[i].setCard(card);
    }

    /**
     * Returns the hand of the player at a given index
     *
     * @param i index of the hand field
     * @return {@link CommandCardField}
     */
    public CommandCardField getCardField(int i) {
        return cards[i];
    }

    /**
     * sets the deck for the player to the given deck
     *
     * @param deck a list of {@link CommandCard}s to use as the player deck
     */
    public void setDeck (List<CommandCard> deck){
        this.deck = deck;
    }


    /**
     * gets the deck of the player
     * @return the deck of the player
     */
    public List<CommandCard> getDeck(){
        return deck;
    }


    /**
     * puts a card into a players discardDeck 
     *
     * @param card a {@link CommandCard} to be discarded.
     *
     */
    public void discardCard(CommandCard card){
        this.discardDeck.add(card);
    }


    /**
     * gets the discardDeck Array.
     *
     * @return a list of {@link CommandCard}
     */
    public List<CommandCard> getDiscardDeck(){
            return this.discardDeck;
    }

    /**
     *Returns the top card of the player deck and removes it
     *
     * return {@link CommandCard} 
     */
    //deck should be implented as a stack instead of a list 🤷‍♂️
    public CommandCard drawCard(){
        if(deck.size() == 0){
            shuffleDiscardAndDeck();
        }
        CommandCard output = this.deck.get(this.deck.size()-1);
        this.deck.remove(this.deck.size()-1);
        return output;
        
    }


    /**
     * shuffles the deck and discard deck into one
     *
     */
    public void shuffleDiscardAndDeck(){
        for(CommandCard card : discardDeck){
            deck.add(card);
        }
        Collections.shuffle(deck);
        discardDeck.clear();
    }

    /**
     * setter for discard deck
     *
     * @param deck a list of {@link CommandCard}
     */
    public void setDiscardDeck(List<CommandCard> deck){
        this.discardDeck = deck;
    }

    /**
     * a method to get the program as {@link CommandCard}
     *
     * @return List of commandCards of ArrayList
     */
    public List<CommandCard> getProgramAsCommandCards(){
        ArrayList<CommandCard> output = new ArrayList<>();
        for(CommandCardField field : program){
            if(field.getCard() != null){
                output.add(field.getCard());
            }
        }
        return output;
    }
    /**
     * a method to get the hand as {@link CommandCard}
     *
     * @return List of commandCards of ArrayList
     */
    public List<CommandCard> getHandAsCommandCards(){
        ArrayList<CommandCard> output = new ArrayList<>();
        for(CommandCardField field : cards){
            output.add(field.getCard());
        }
        return output;
    }



    /**
     * sets the program of the player to the given array
     *
     * @param list  list  of {@link CommandCardField} to be used as the program
     */
    public void setProgram(List<CommandCardField> list){

        if (list.size() != NO_REGISTERS){
            while (NO_REGISTERS - list.size() > 0){
                list.add(new CommandCardField(this));
            }
        }
        program = list.toArray(new CommandCardField[NO_REGISTERS]);         
        
    }

    /**
     * sets the hand of the player to the given array
     *
     * @param list an array of {@link CommandCardField} to be used as the hand
     */
    public void setHand(List<CommandCardField> list){
        if (list.size() != NO_CARDS){
            while (NO_CARDS - list.size() > 0){
                list.add(new CommandCardField(this));
            }
        }
        cards = list.toArray(new CommandCardField[NO_CARDS]);
    }

    /**
     * sets the space the player dies on
     *
     * @param deathSpace the space the player dies on
     */
    public void setDeathSpace(Space deathSpace){
        this.deathSpace = deathSpace;
    }

    /**
     * gets the space the player dies on
     *
     * @return the space the player dies on
     */
    public Space getDeathSpace(){
        return deathSpace;

    }

    /**
     * sets the distance to the antenna
     *
     * @param distanceToAntenna the distance to the antenna
     */
    public void setDistanceToAntenna(double distanceToAntenna){
        this.distanceToAntenna = distanceToAntenna;
    }

    /**
     * gets the distance to the antenna
     *
     * @return the distance to the antenna
     */
    public double getDistanceToAntenna(){
        return distanceToAntenna;
    }

    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
}

