package dk.dtu.compute.se.pisd.roborally.controller;

import java.util.Arrays;

import org.jetbrains.annotations.NotNull;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;


public class Antenna extends FieldAction {

/**
 * Calculates the distance plus angle between the antenna and all players and sets the turn order list in order of players from least distance to most distance
 * @param gameController the gamecontroller of the game
 * @param space the space on which the antenna is
 */
public static void makeTurnOrder(@NotNull GameController gameController,@NotNull Space space){
Board board = gameController.board;
int totPlayers = board.getPlayersNumber();
        for(int i = 0; i < totPlayers; i++){
            Space playerspace = board.getPlayerTurn(i).getSpace();
            if(playerspace == null){
                playerspace = board.getPlayerTurn(i).getDeathSpace();
            }
            double py = (double) playerspace.y;
            double px = (double) playerspace.x;
            double ay = (double) space.y;
            double ax = (double) space.x;
            double distance = Math.abs(py-ay) + Math.abs(px-ax);
            distance+=(Math.atan2(py-ay,px-ax))/(Math.PI*2)+0.5;

            board.getPlayerTurn(i).setDistanceToAntenna(distance);
        }

        while(!gameController.isSorted(board.getPlayerTurnList())){
            for(int i = 0; i+1 < totPlayers; i++){
                Player iPlayer = board.getPlayerTurn(i);
                Player nextPlayer = board.getPlayerTurn(i+1);
                if(iPlayer.getDistanceToAntenna() > nextPlayer.getDistanceToAntenna()){
                    if(iPlayer.getDistanceToAntenna() > nextPlayer.getDistanceToAntenna()){                 
                        board.setPlayerTurnOrder(i, nextPlayer);
                        board.setPlayerTurnOrder(i+1, iPlayer);
                    }
                }
            }
        }
    }

    public Antenna(){

    }
    /**
     * Implementation of the antenna
     * determines the order in which the players play based on distance to the antenna at the start of the programming phase
     *  @param gameController the given controller for the current instance
     * @param space a given space that will have the belt action on it
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
        return "Antenna";
    }

}
