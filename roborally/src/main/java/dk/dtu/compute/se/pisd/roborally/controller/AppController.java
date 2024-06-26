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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import dk.dtu.compute.se.pisd.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.dal.GameInDB;
import dk.dtu.compute.se.pisd.roborally.dal.RepositoryAccess;
import dk.dtu.compute.se.pisd.roborally.fileaccess.ResourceFileLister;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk, Alex Lundberg (s235442), Andreas
 *         (s235455)
 *
 */
public class AppController implements Observer {

    private ResourceFileLister lister = new ResourceFileLister();

    private List<String> boardList = lister.getFiles();

    final private List<String> BOARD_CHOICES = boardList;
    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
    final private List<String> PLAYER_COLORS = Arrays.asList("crimson", "chocolate", "royalblue", "aqua", "purple", "magenta");

    final private RoboRally roboRally;

    private GameController gameController;

    /**
     * Constructor for the AppController
     *
     * @param roboRally the RoboRally application
     */
    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

    /**
     * Start a new game with a given number of players. The user is asked
     * to select the number of players and the board to play on. The game
     * is then started with the selected number of players and the selected
     * board.
     */
    public void newGame() {
        ChoiceDialog<String> boardDialog = new ChoiceDialog<>(BOARD_CHOICES.get(0), BOARD_CHOICES);
        boardDialog.setTitle("Board selection");
        boardDialog.setHeaderText("Select the board the game should be played on");
        Optional<String> boardChoice = boardDialog.showAndWait();
        if (!boardChoice.isPresent()) {
            return;
        }
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");
        Optional<Integer> result = dialog.showAndWait();

        if (result.isPresent()) {
            if (gameController != null) {
                // The UI should not allow this, but in case this happens anyway.
                // give the user the option to save the game or abort this operation!
                if (!stopGame()) {
                    return;
                }
            }
            Board board = BoardFactory.getInstance().createBoard(boardChoice.get());

            gameController = new GameController(board);
            int no = result.get();
            for (int i = 0; i < no; i++) {
                Player player = new Player(board, PLAYER_COLORS.get(i), "Player " + (i + 1));
                board.addPlayer(player);
                // Initializing player deck
                gameController.setPlayerDeck(player, 20);
                setPlayerSpawn(player, board, i);
            }
            board.setCurrentPlayer(board.getPlayer(0));
            gameController.startProgrammingPhase();
            if (board.getSpaceByActionSubClass(Antenna.class).size() > 0) {
                Antenna.makeTurnOrder(gameController, board.getSpaceByActionSubClass(Antenna.class).get(0));
            }

            RepositoryAccess.getRepository().createGameInDB(board);

            roboRally.createBoardView(gameController);
        }
    }

    /**
     * sets the player spawn to a random space
     * 
     * @param player player to be set
     * @param board  the board
     * @param i      index of player
     */
    private void setPlayerSpawn(Player player, Board board, int i) {
        int randomY = 0;
        int randomX = 0;
        int count = 0;
        Space randomSpace = board.getSpace(randomX, randomY);
        boolean playerIsAssigned = false;
        while (!playerIsAssigned) {
            randomY = (int) ((int) board.height * Math.random());
            randomX = (int) ((int) board.width * Math.random());
            randomSpace = board.getSpace(randomX, randomY);
            count++;
            if (count > 20) {
                Space defaultspace = board.getSpace(i % board.width, i);
                if (defaultspace.getPlayer() != null) {
                    player.setSpace(defaultspace);
                } else {
                    throw new IllegalStateException();
                }
                playerIsAssigned = true;
            }
            if (randomSpace.getPlayer() != null) {
                continue;
            }
            if (board.getSpaceByActionSubClass(Antenna.class).contains(randomSpace)) {
                continue;
            }
            if (board.getSpaceByActionSubClass(Pit.class).contains(randomSpace)) {
                continue;
            }
            if (board.getSpaceByActionSubClass(Checkpoint.class).contains(randomSpace)) {
                continue;
            }
            player.setSpace(randomSpace);
            playerIsAssigned = true;
        }
    }

    public void saveGame() {

        if (gameController != null && gameController.board.getGameId() != null) {
            RepositoryAccess.getRepository().updateGameInDB(gameController.board);
        }

    }

    /**
     * Load a game from the database. The user is asked to select the game
     * to load from the database. The game is then loaded and the game
     * controller is set up to continue the game.
     */
    public void loadGame() {

        List<GameInDB> gameIds = RepositoryAccess.getRepository().getGames();
        List<String> games = new ArrayList<>();
        int gameID = 1;
        for (GameInDB game : gameIds) {
            games.add(game.name);
        }
        ChoiceDialog<String> dialog = new ChoiceDialog<String>(gameIds.get(0).name, games);
        dialog.setTitle("Game");
        dialog.setHeaderText("Select game to load");
        Optional<String> result = dialog.showAndWait();
        for (GameInDB game : gameIds) {
            if (game.name.equals(result.get())) {
                gameID = game.id;
            }
        }

        Board board = RepositoryAccess.getRepository().loadGameFromDB(gameID);
        if (board != null) {
            gameController = new GameController(board);
            roboRally.createBoardView(gameController);
        }
    }

    /**
     * Stop playing the current game, giving the user the option to save
     * the game or to cancel stopping the game. The method returns true
     * if the game was successfully stopped (with or without saving the
     * game); returns false, if the current game was not stopped. In case
     * there is no current game, false is returned.
     *
     * @return true if the current game was stopped, false otherwise
     */
    public boolean stopGame() {
        if (gameController != null) {

            // here we save the game (without asking the user).
            saveGame();

            gameController = null;
            roboRally.createBoardView(null);
            return true;
        }
        return false;
    }

    /**
     * Exit the RoboRally application. If there is a game running, the user
     * is asked whether the game should be saved before exiting the
     * application.
     */
    public void exit() {
        if (gameController != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Exit RoboRally?");
            alert.setContentText("Are you sure you want to exit RoboRally?");
            Optional<ButtonType> result = alert.showAndWait();

            if (!result.isPresent() || result.get() != ButtonType.OK) {
                return; // return without exiting the application
            }
        }

        // If the user did not cancel, the RoboRally application will exit
        // after the option to save the game
        if (gameController == null || stopGame()) {
            Platform.exit();
        }
    }

    /**
     * Returns whether a game is currently running or not.
     * 
     * @return boolean
     */
    public boolean isGameRunning() {
        return gameController != null;
    }

    /**
     * Update the observer, does nothing for now
     */
    public void update(Subject subject) {
    }

    /**
     * Announces winner
     *
     * @param winnerName name of the winning player
     */
    static void showWinnerPopup(String winnerName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game WINNER!!!!!");
        alert.setHeaderText(null);
        alert.setContentText("Congratulations! " + winnerName + " has won the game!");
        alert.showAndWait();
    }

    /**
     * Makes a new alert that pops up on the players screen telling them to fill all
     * registers with cards.
     * 
     * @param player the player that has empty registers
     */
    public static void missingCard(String player) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Fill all registers");
        alert.setHeaderText(null);
        alert.setContentText(player + " has empty registers fill all before continuing");
        alert.showAndWait();
    }

}
