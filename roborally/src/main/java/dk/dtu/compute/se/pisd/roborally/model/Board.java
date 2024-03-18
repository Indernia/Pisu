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
import dk.dtu.compute.se.pisd.roborally.controller.Checkpoint;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static dk.dtu.compute.se.pisd.roborally.model.Phase.INITIALISATION;

import  dk.dtu.compute.se.pisd.roborally.controller.ConveyorBelt;
import  dk.dtu.compute.se.pisd.roborally.controller.TurnGear;

 /* ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Board extends Subject {

    public final int width;

    public final int height;

    public final String boardName;

    private Integer gameId;

    private final Space[][] spaces;

    private final List<Player> players = new ArrayList<>();

    private Player current;

    private Phase phase = INITIALISATION;

    private int step = 0;

    private boolean stepMode;

    private int counter = 0;

    /**
     * Constructor for board, requires all 3 inputs
     * 
     * @param width
     * @param height
     * @param boardName
     */
    public Board(int width, int height, @NotNull String boardName) {
        this.boardName = boardName;
        this.width = width;
        this.height = height;
        spaces = new Space[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Space space = new Space(this, x, y);
                spaces[x][y] = space;
            }
        }
        this.stepMode = false;


        //TODO remove later
        ConveyorBelt action  = new ConveyorBelt();
        action.setHeading(Heading.WEST);
        spaces[2][2].getActions().add(action);
        TurnGear gearLeft = new TurnGear("left");
        spaces[3][3].getActions().add(gearLeft);
        ConveyorBelt action2  = new ConveyorBelt();
        action2.setHeading(Heading.SOUTH);
        spaces[1][2].getActions().add(action2);

        spaces[6][6].getWalls().add(Heading.SOUTH);
        spaces[6][6].getWalls().add(Heading.WEST);
        spaces[5][5].getWalls().add(Heading.NORTH);
        spaces[5][5].getWalls().add(Heading.EAST);


        //TODO End

        intializeCheckpoints();
    }

     /**
      * ...
      *
      * @author Julius Sondergaard, s234096
      *
      */
    private void intializeCheckpoints() {
        addCheckpointToSpace(4, 1, 1);
        addCheckpointToSpace(5, 6, 2);
        addCheckpointToSpace(7, 1, 3);
    }

     /**
      * ...
      *
      * @author Julius Sondergaard, s234096
      *
      */
     private void addCheckpointToSpace(int x, int y, int checkpointNumber) {
         Space checkpointSpace = getSpace(x, y);
         if (checkpointSpace != null) {
             checkpointSpace.getActions().add(new Checkpoint(checkpointNumber));
         }
     }


     /**
     * Constructor for board, to change its width and height, its an overflow
     * constructor, in case a name is not given for the board
     * 
     * @param width
     * @param height
     */
    public Board(int width, int height) {
        this(width, height, "defaultboard");
    }

    /**
     * Returns the game id
     * 
     * @return game id
     */
    public Integer getGameId() {
        return gameId;
    }

    /**
     * Sets the id of the game
     * 
     * @param gameId what the id should be
     */
    public void setGameId(int gameId) {
        if (this.gameId == null) {
            this.gameId = gameId;
        } else {
            if (!this.gameId.equals(gameId)) {
                throw new IllegalStateException("A game with a set id may not be assigned a new id!");
            }
        }
    }

    /**
     * Returns the space from two coordinates
     * 
     * @param x , X coordinate
     * @param y , Y coordinate
     * @return The space at the coordinates
     */
    public Space getSpace(int x, int y) {
        if (x >= 0 && x < width &&
                y >= 0 && y < height) {
            return spaces[x][y];
        } else {
            return null;
        }
    }

    /**
     * Returns the counter at whatever value it is currently at.
     * 
     * @return counter value
     */
    public int getCounter() {
        return counter;
    }

    /**
     * Sets the counter of moves
     * 
     * @param i what the counter should be
     */
    public void setCounter(int i) {
        counter = i;
        notifyChange();
    }

    /**
     * Returns the number of players in the game
     * 
     * @return number of players
     */
    public int getPlayersNumber() {
        return players.size();
    }

    /**
     * Function to add another player
     * 
     * @param player the player to be added
     */
    public void addPlayer(@NotNull Player player) {
        if (player.board == this && !players.contains(player)) {
            players.add(player);
            notifyChange();
        }
    }

    /**
     * Gets the player of the index given
     * 
     * @param i the player that is sought after
     * @return the player of index i
     */
    public Player getPlayer(int i) {
        if (i >= 0 && i < players.size()) {
            return players.get(i);
        } else {
            return null;
        }
    }

    /**
     * returns the current player of the game.
     * 
     * @return current player
     */
    public Player getCurrentPlayer() {
        return current;
    }

    /**
     * Sets the current player to be the player given
     * 
     * @param player
     */
    public void setCurrentPlayer(Player player) {
        if (player != this.current && players.contains(player)) {
            this.current = player;
            notifyChange();
        }
    }

    /**
     * Gets the current phase/stage of the game
     * 
     * @return phase
     */
    public Phase getPhase() {
        return phase;
    }

    /**
     * Sets the phase
     * 
     * @param phase
     */
    public void setPhase(Phase phase) {
        if (phase != this.phase) {
            this.phase = phase;
            notifyChange();
        }
    }

    /**
     * Gets the step
     * 
     * @return step
     */
    public int getStep() {
        return step;
    }

    /**
     * Sets the step counter to the specified value
     * 
     * @param step
     */
    public void setStep(int step) {
        if (step != this.step) {
            this.step = step;
            notifyChange();
        }
    }

    /**
     * Returns the current stepmode, if that value is true or false
     * 
     * @return stepmode
     */
    public boolean isStepMode() {
        return stepMode;
    }

    /**
     * Changes the stepmode to be the new stepmode, if its not already that mode. If
     * its a new mode, then it will notify change, and change the mode.
     * 
     * @param stepMode the new mode for stepmode
     */
    public void setStepMode(boolean stepMode) {
        if (stepMode != this.stepMode) {
            this.stepMode = stepMode;
            notifyChange();
        }
    }

    /**
     * Returns the index of the player given
     * 
     * @param player the player's index that is sought after
     * @return the index of the player given
     */
    public int getPlayerNumber(@NotNull Player player) {
        if (player.board == this) {
            return players.indexOf(player);
        } else {
            return -1;
        }
    }

    /**
     * Returns the neighbour of the given space of the board in the given heading.
     * The neighbour is returned only, if it can be reached from the given space
     * (no walls or obstacles in either of the involved spaces); otherwise,
     * null will be returned.
     *
     * @param space   the space for which the neighbour should be computed
     * @param heading the heading of the neighbour
     * @return the space in the given direction; null if there is no (reachable)
     *         neighbour
     */
    public Space getNeighbour(@NotNull Space space, @NotNull Heading heading) {
        int x = space.x;
        int y = space.y;
        switch (heading) {
            case SOUTH:
                y = (y + 1) % height;
                break;
            case WEST:
                x = (x + width - 1) % width;
                break;
            case NORTH:
                y = (y + height - 1) % height;
                break;
            case EAST:
                x = (x + 1) % width;
                break;
        }

        return getSpace(x, y);
    }

    /**
     * @return String
     */

     /**
      * ...
      *
      * @author Julius Sondergaard, s234096
      *
      */
    public String getStatusMessage() {
        String baseMessage = "Phase = " + getPhase() + "Player = " + getCurrentPlayer().getName()
                + ", moves = " + getCounter();

        String checkpointMessage = ", Checkpoint = " + getCurrentPlayer().getCurrentCheckpoint();

        return baseMessage + checkpointMessage;
    }


 }
