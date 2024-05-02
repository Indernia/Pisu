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
package dk.dtu.compute.se.pisd.roborally.controller;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.NORTH;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborally.model.CommandCard;
import dk.dtu.compute.se.pisd.roborally.model.CommandCardField;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Phase;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    final public Board board;

    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * This is just some dummy controller operation to make a simple move to see
     * something
     * happening on the board. This method should eventually be deleted!
     *
     * @param space the space to which the current player should move
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space) {
        Player current = board.getCurrentPlayer();
        if (current != null && space.getPlayer() == null) {
            current.setSpace(space);
        }
        board.setCounter(board.getCounter() + 1);
        board.setCurrentPlayer(board.getPlayerTurn((board.getPlayerNumber(current) + 1) % board.getPlayersNumber()));

        // TODO Assignment V1: method should be implemented by the students:
        // - the current player should be moved to the given space
        // (if it is free()
        // - and the current player should be set to the player
        // following the current player
        // - the counter of moves in the game should be increased by one
        // if and when the player is moved (the counter and the status line
        // message needs to be implemented at another place)

    }

    /**
     * Is called at the start of the program
     * Starts the programming phase
     */
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayerTurn(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayerTurn(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    //TODO change here to use the player deck.
                    if(field.getCard() == null){
                        field.setCard(player.drawCard());
                        field.setVisible(true);

                    }

                }
            }
        }
    }

    /**
     * Makes random command cards
     * 
     * @return the random command card
     */
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    /** Activated when everyone is done programming */
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayerTurn(0));
        board.setStep(0);
    }

    /**
     * Makes things visible
     * 
     * @param register
     */
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayerTurn(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    /** Makes things invisible */
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayerTurn(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    /**
     * Sets stepmode false, and continues to do the whole program
     */
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    /** Sets the mode of stepmode to true, and continues the program */
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    /** Continues the program */
    private void continuePrograms() {
        do {
            executeNextStep(null);
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    /**
     * A public method that can execute an interractive card command, and then
     * continue the program.
     * 
     * @param option
     */
    public void executeOptionAndContinue(@NotNull Command option) {
        assert board.getPhase() == Phase.PLAYER_INTERACTION;
        board.setPhase(Phase.ACTIVATION);
        executeNextStep(option);
        while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode()) {
            executeNextStep(null);
        }
    }

    /**
     * 
     * Executes the next step, activated by the press of a button in the gui and checks for special actions on current players space
     */
    private void executeNextStep(Command option) {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    if (option == null) {
                        if (!command.isInteractive()) {
                            executeCommand(currentPlayer, command);
                            currentPlayer.discardCard(card);
                        }
                        else {
                            board.setPhase(Phase.PLAYER_INTERACTION);
                            return;
                        }
                    } else {
                        executeCommand(currentPlayer, option);
                        currentPlayer.discardCard(card);
                    }
                    ActivateFieldActions();
                }
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                int numberOfPlayers = board.getPlayersNumber();
                if (nextPlayerNumber < numberOfPlayers) {
                    if(board.getPlayerTurn(nextPlayerNumber).getSpace() != null){
                    board.setCurrentPlayer(board.getPlayerTurn(nextPlayerNumber));
                    } else {
                        skipPlayer(nextPlayerNumber, numberOfPlayers);
                    }
                } else {
                    nextStep();
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
        checkForGameEnd();
    }

    
    private void nextStep(){
        int step = board.getStep();
        step++;
        if (step < Player.NO_REGISTERS) {
            makeProgramFieldsVisible(step);
            board.setStep(step);
            if(board.getPlayerTurn(0).getSpace() != null){
            board.setCurrentPlayer(board.getPlayerTurn(0));
            } else{
                skipPlayer(board.getPlayerNumber(board.getPlayerTurn(0))+1, board.getPlayersNumber());
            }
        } else {
            for(int i = 0; i < board.getPlayersNumber(); i++){
                Player player = board.getPlayerTurn(i);
                if(player.getSpace() == null){
                    reboot(player);
                }
            }
            if(board.getSpaceByActionSubClass(Antenna.class).size() > 0){
            Antenna.makeTurnOrder(this, board.getSpaceByActionSubClass(Antenna.class).get(0));
            }
            startProgrammingPhase();   
        }
    }

    private void skipPlayer(int nextPlayerNumber, int numberOfPlayers){
        for(int i = nextPlayerNumber; i < board.getPlayersNumber(); i++){
            Player iPlayer = board.getPlayerTurn(i);
            int iPlayerNumber = board.getPlayerNumber(iPlayer);
            if(iPlayer.getSpace() != null){
                board.setCurrentPlayer(iPlayer);
                break;
            } else if(iPlayerNumber == numberOfPlayers-1){
                nextStep();
            }
        }
    }
    /**
     * Checks if the game should end if a player reaches the last checkpoint.
     * Sets the game phase to FINISHED if it's meets the above criteria by calling on endGame method
     * This method is called after executeNextStep
     *
     * @author Julius Sondergaard, s234096
     */
    public void checkForGameEnd() {
        int lastCheckpoint = board.getMaxCheckpointNumber();

        for (Player player : board.getPlayers()) {
            if (player.getCurrentCheckpoint() == lastCheckpoint) {
                endGame(player);
                break;
            }
        }
    }

    /**
     * Sets the game phase to FINISHED
     * Calls winner pop up message splitting them up in 3 pieces to make them more testable
     *
     * @author Julius Sondergaard, s234096
     */
    public void endGame(Player winner) {
        updateGameStateToFinished();
        showWinnerPopup(winner);
    }

    public void updateGameStateToFinished() {
        board.setPhase(Phase.FINISHED);
    }

    public void showWinnerPopup(Player winner) {
        AppController.showWinnerPopup(winner.getName());
    }


    /**
     * @param player
     * @param command
     */
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            // their execution. This should eventually be done in a more elegant way
            // (this concerns the way cards are modelled as well as the way they are
            // executed).

            switch (command) {
                case FORWARD:
                    moveForward(player);
                    break;
                case RIGHT:
                    turnRight(player);
                    break;
                case LEFT:
                    turnLeft(player);
                    break;
                case FAST_FORWARD:
                    fastForward(player);
                    break;
                case SPAM:
                    spamDamage(player);
                    break;
                default:
                    // DO NOTHING (for now)
            }
        }
    }

    /**
     * Moves the player forward, if another player is on the new space, that player
     * is pushed in the direction that the original player is facing
     * 
     * @param player
     */

    public void moveForward(@NotNull Player player) {
        Space space = player.getSpace();
        if (space != null) {
            Heading heading = player.getHeading();
            Space newSpace = board.getNeighbour(space, heading);
            if (!wallObstructs(player.getSpace(), player.getHeading())) {
                if (newSpace != null) {
                    try {
                        moveToSpace(player, newSpace, heading);
                        player.setSpace(newSpace);
                    } catch (ImpossibleMoveException e) {
                    }

                }
            }

        }

    }


    /**
     * Checks if there is a wall obstructing the player
     * 
     * @param start
     * @param heading
     * @return true if there is a wall obstructing the player
     */
    private boolean wallObstructs(Space start, Heading heading) {
        if (start.getWalls().contains(heading)) {
            return true;
        }
        if (board.getNeighbour(start, heading).getWalls().contains(heading.getOpposite())) {
            return true;
        }
        if(board.getNeighbour(start, heading).getActions().size() > 0 ){
            if(board.getNeighbour(start, heading).getActions().get(0) instanceof Antenna){
                return true;
            }
        }
        return false;
    }

    public void moveToSpace(
            @NotNull Player player,
            @NotNull Space space,
            @NotNull Heading heading) throws ImpossibleMoveException {
        Player other = space.getPlayer();
        if (other != null) {
            Space newspace = board.getNeighbour(space, heading);

            if (newspace != null && !wallObstructs(other.getSpace(), player.getHeading())) {
                moveToSpace(other, newspace, heading);
            } else
                throw new ImpossibleMoveException(player, newspace, heading);
        }
        player.setSpace(space);

    }

    /**
     * Moves the player forward twice. Implements moveforward.
     * 
     * @see moveForward
     * 
     * @param player
     */
    public void fastForward(@NotNull Player player) {
        moveForward(player);
        moveForward(player);
    }

    /**
     * Turns the heading towards the right from the perspective of the robot
     * 
     * @param player
     */
    public void turnRight(@NotNull Player player) {
        Heading heading = player.getHeading();
        Heading nextHeading = heading.next();
        player.setHeading(nextHeading);
    }

    /**
     * Turns the heading towards the left from the perspective of the robot
     * 
     * @param player
     */
    public void turnLeft(@NotNull Player player) {
        Heading heading = player.getHeading();
        Heading nextHeading = heading.prev();
        player.setHeading(nextHeading);
    }

    public void spamDamage(@NotNull Player player){
        int currentReg = board.getStep();
        CommandCard topCard = player.drawCard();
        player.setProgramField(currentReg, topCard);
        if(topCard.command != Command.OPTION_LEFT_RIGHT){
        executeCommand(player, topCard.command);
        } else {
            double random = Math.random();
            if(random < 0.5){
                executeCommand(player,Command.RIGHT);
            } else {
                executeCommand(player, Command.LEFT);
            }
        }
    }

    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * A method called when no corresponding controller operation is implemented
     * yet. This
     * should eventually be removed.
     */

    public void notImplemented() {
        // XXX just for now to indicate that the actual method is not yet implemented
        assert false;
    }

    public void die(Player player, Space space){
        for(int i = 0; i < Player.NO_CARDS; i++){
            player.discardCard(player.getCardField(i).getCard());
            player.getCardField(i).setCard(null);
        }
        player.setDeathSpace(space);
        player.setSpace(null);
    }

    public void reboot(Player player){
        Space playerspace = player.getDeathSpace();
        ArrayList<Space> actionSpaces = board.getSpaceByActionSubClass(Reboot.class);
        Space rebootSpace = actionSpaces.get(0);
        double prevdistance = 99999.99999;
            for(Space actionSpace : actionSpaces){
                double py = (double) playerspace.y;
                double px = (double) playerspace.x;
                double ay = (double) actionSpace.y;
                double ax = (double) actionSpace.x;
                double distance = Math.sqrt((Math.pow(py-ay,2)) + (Math.pow(px-ax,2)));


                if(distance < prevdistance){
                rebootSpace = board.getSpace(actionSpace.x, actionSpace.y);
                prevdistance = distance;
                }

            }
        player.setHeading(NORTH);
        player.getCardField(0).setCard(new CommandCard(Command.SPAM));
        player.getCardField(1).setCard(new CommandCard(Command.SPAM));
        try {
            moveToSpace(player, rebootSpace, player.getHeading());
        } catch (ImpossibleMoveException e) {
            e.printStackTrace();
        }
    }


    /**
     * Method activates all fields with players on them, in the order given in the roborally rules.
     *
     * @return void
     */
    public void ActivateFieldActions(){
        ArrayList<FieldAction> actions = new ArrayList<FieldAction> ();
        //Adding all fieldactions in the order of activation.
        actions.add(new ConveyorBelt());
        actions.add(new TurnGear());
        actions.add(new Checkpoint());
        actions.add(new Pit());

        Player player;
        for (FieldAction action : actions){
            for (int j = 0; j< board.getPlayersNumber(); j++){
                player = board.getPlayerTurn(j);
                if(player.getSpace() != null){
                    for (FieldAction PAction : player.getSpace().getActions()){
                        if (action.getClass().isInstance(PAction)){
                            PAction.doAction(this, player.getSpace());
                        }
                    }
                }
            }

        }
    }




    public void setPlayerDeck(Player player, int size){
        ArrayList<CommandCard> deck = new ArrayList<>();
        for (int i = 0; i < size;){
            CommandCard randomCard = generateRandomCommandCard();
            if(randomCard.command != Command.SPAM){
            deck.add(randomCard);
            i++;
            } 
        }
        player.setDeck(deck);

    }
        

    public boolean isSorted(List<Player> list){
        for(int i = 0; i+1 < list.size(); i++){
            Player iPlayer = list.get(i);
            Player nextPlayer = list.get(i+1);
            if(iPlayer.getDistanceToAntenna() > nextPlayer.getDistanceToAntenna()){
                return false;
            }
        }
        return true;
    }

}
