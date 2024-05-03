package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Julius Sondergaard, s234096
 */
class GameControllerTest {

    private final int TEST_WIDTH = 8;
    private final int TEST_HEIGHT = 8;

    private GameController gameController;

    @BeforeEach
    void setUp() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT);
        gameController = new GameController(board);
        for (int i = 0; i < 6; i++) {
            Player player = new Player(board, null, "Player " + i);
            board.addPlayer(player);
            player.setSpace(board.getSpace(i, i));
            player.setHeading(Heading.values()[i % Heading.values().length]);
        }
        board.setCurrentPlayer(board.getPlayer(0));
    }

    @AfterEach
    void tearDown() {
        gameController = null;
    }

    /**
     * Test for Assignment V1 (can be delete later once V1 was shown to the teacher)
     */
    @Test
    void testV1() {
        Board board = gameController.board;

        Player player = board.getCurrentPlayer();
        gameController.moveCurrentPlayerToSpace(board.getSpace(0, 4));

        assertEquals(player, board.getSpace(0, 4).getPlayer(),
                "Player " + player.getName() + " should beSpace (0,4)");
    }

    @Test
    void moveCurrentPlayerToSpace() {
        Board board = gameController.board;
        Player player1 = board.getPlayer(0);
        Player player2 = board.getPlayer(1);

        gameController.moveCurrentPlayerToSpace(board.getSpace(0, 4));

        assertEquals(player1, board.getSpace(0, 4).getPlayer(),
                "Player " + player1.getName() + " should beSpace (0,4)");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty");
        assertEquals(player2, board.getCurrentPlayer(),
                "Current player should be " + player2.getName() + ".");
    }

    @Test
    void moveForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.moveForward(current);

        assertEquals(current, board.getSpace(0, 1).getPlayer(),
                "Player " + current.getName() + " should beSpace (0,1)");
        assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty");
    }

    @Test
    void fastForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.fastForward(current);

        assertEquals(current, board.getSpace(0, 2).getPlayer(),
                "Player " + current.getName() + " should beSpace (0,2)");
        assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty");
    }

    @Test
    void turnLeft() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        gameController.turnLeft(current);

        assertEquals(current.getHeading(), board.getSpace(0, 0).getPlayer().getHeading(),
                "Player " + current.getName() + " should beSpace (0,0)");
        assertEquals(Heading.EAST, current.getHeading(), "Player 0 should be heading EAST");
        assertNotNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should not be empty");
    }

    @Test
    void turnRight() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        gameController.turnRight(current);

        assertEquals(current.getHeading(), board.getSpace(0, 0).getPlayer().getHeading(),
                "Player " + current.getName() + " should beSpace (0,0)");
        assertEquals(Heading.WEST, current.getHeading(), "Player 0 should be heading WEST");
        assertNotNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should not be empty");
    }


    /**
     * Verifies that landing on a checkpoint correctly updates the player's checkpoint counter.
     *
     *
     */
    @Test
    void testPlayerReachesCheckpoint() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        int checkpointNumber = 3;
        player.setCurrentCheckpoint(checkpointNumber - 1);

        Space checkpointSpace = board.getSpace(1, 1);
        Checkpoint checkpoint = new Checkpoint(checkpointNumber);
        checkpointSpace.getActions().add(checkpoint);

        player.setSpace(checkpointSpace);

        boolean actionResult = checkpoint.doAction(gameController, checkpointSpace);

        Assertions.assertTrue(actionResult, "Checkpoint action should return true indicating it was successful.");
        assertEquals(checkpointNumber, player.getCurrentCheckpoint(),
                "Player's checkpoint should be updated to " + checkpointNumber);
    }

    /**
     * Negative test with player trying to skip to a non-sequential checkpoint
     *
     *
     */
    @Test
    void testPlayerDoesNotSkipCheckpoints() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        int currentCheckpoint = 1;
        int wrongCheckpointNumber = 3;

        player.setCurrentCheckpoint(currentCheckpoint);
        Space checkpointSpace = board.getSpace(2, 2);
        Checkpoint wrongCheckpoint = new Checkpoint(wrongCheckpointNumber);
        checkpointSpace.getActions().add(wrongCheckpoint);

        player.setSpace(checkpointSpace);

        boolean actionResult = wrongCheckpoint.doAction(gameController, checkpointSpace);

        Assertions.assertFalse(actionResult, "Checkpoint action should return false indicating failure.");
        assertEquals(currentCheckpoint, player.getCurrentCheckpoint(),
                "Player's checkpoint should not be updated.");
    }

    /**
     * Negative test moving player to a checkpoint the player already owns / revisiting checkpoint
     *
     *
     */
    @Test
    void testPlayerRevisitsCheckpoint() {
        Board board = gameController.board;
        Player player = board.getCurrentPlayer();
        int currentCheckpoint = 2;

        player.setCurrentCheckpoint(currentCheckpoint);

        Space checkpointSpace = board.getSpace(1, 1);
        Checkpoint sameCheckpoint = new Checkpoint(currentCheckpoint);
        checkpointSpace.getActions().add(sameCheckpoint);

        player.setSpace(checkpointSpace);

        boolean actionResult = sameCheckpoint.doAction(gameController, checkpointSpace);

        Assertions.assertFalse(actionResult, "Checkpoint action should return false indicating no need to update.");
        assertEquals(currentCheckpoint, player.getCurrentCheckpoint(),
                "Player's checkpoint should remain unchanged as it's a revisit.");
    }

    /**
     * Test for game ends overriding showWinnerPopup to avoid testing GUI
     *
     *
     */
    @Test
    void testEndGame() {
        Board board = gameController.board;
        GameController gameController = new GameController(board) {
            @Override
            public void showWinnerPopup(Player winner) {
                // Avoid potential GUI conflict with unit test hence we override this
            }
        };
        Player player = new Player(board, null, "Player 1");
        board.addPlayer(player);
        board.setCurrentPlayer(player);

        int finalCheckpoint = board.getMaxCheckpointNumber();
        player.setCurrentCheckpoint(finalCheckpoint - 1);

        Space finalCheckpointSpace = new Space(board, 0, 0);
        Checkpoint checkpoint = new Checkpoint(finalCheckpoint);
        finalCheckpointSpace.getActions().add(checkpoint);
        player.setSpace(finalCheckpointSpace);

        checkpoint.doAction(gameController, finalCheckpointSpace);

        gameController.checkForGameEnd();

        assertEquals(Phase.FINISHED, board.getPhase(), "Game phase should be set to FINISHED after player reaches the final checkpoint.");
    }

    /**
     * Secure player is moved with 1 on Forward card assuming no walls or obstacles
     *
     *
     */
    @Test
    void testPlayerCollisionForward() {
        Board board = gameController.board;
        Player player1 = board.getPlayer(0);
        Player player2 = board.getPlayer(1);

        player1.setHeading(Heading.EAST);
        player1.setSpace(board.getSpace(1, 0));
        player2.setSpace(board.getSpace(2, 0));

        gameController.moveForward(player1);

        assertEquals(player1, board.getSpace(2, 0).getPlayer(), "Player1 should be at (2,0)");

        assertEquals(player2, board.getSpace(3, 0).getPlayer(), "Player2 should be pushed to (3,0)");
    }

    /**
     * Secure player is moved with 2 on fast forward assuming no walls or obstacles
     *
     *
     */
    @Test
        void testPlayerCollisionFastForward() {
            Board board = gameController.board;
            Player player1 = board.getPlayer(0);
            Player player2 = board.getPlayer(1);

            player1.setHeading(Heading.EAST);
            player1.setSpace(board.getSpace(1, 0));
            player2.setSpace(board.getSpace(2,0));

            gameController.fastForward(player1);

            assertEquals(player1, board.getSpace(3,0).getPlayer(), "Player 1 should be at (3,0)");
            assertEquals(player2, board.getSpace(4,0).getPlayer(), "Player 2 should be at (4,0)");
        }

    /**
     * Test player movement through wall
     *
     *
     */
    @Test
    void testMovementBlockedByWall() {
        Board board = gameController.board;
        Player player = board.getPlayer(0);

        player.setSpace(board.getSpace(0, 0));
        player.setHeading(Heading.EAST); //Here we assume the wall is at the east direction

        // Add a wall to the east of (0,0)
        board.getSpace(0, 0).getWalls().add(Heading.EAST);

        gameController.moveForward(player);

        assertEquals(player, board.getSpace(0, 0).getPlayer(), "Player should remain at the initial position due to a wall");
        assertNull(board.getSpace(1, 0).getPlayer(), "The space beyond the wall should remain empty as player can't move through walls.");
    }

    /**
     * Test player can't push another player through wall
     *
     *
     */
    @Test
    void testPlayerCantPushPlayerThroughWall() {
        Board board = gameController.board;
        Player pushingPlayer = board.getPlayer(0);
        Player pushedPlayer = board.getPlayer(1);

        pushingPlayer.setSpace(board.getSpace(0,0));
        pushedPlayer.setSpace(board.getSpace(1,0));

        pushingPlayer.setHeading((Heading.EAST));

        board.getSpace(1, 0).getWalls().add(Heading.EAST);

        gameController.moveForward(pushingPlayer);

        assertEquals(pushingPlayer, board.getSpace(0, 0).getPlayer(), "Pushing player should remain at the initial position due to the wall blocking the pushed player!");
        assertEquals(pushedPlayer, board.getSpace(1, 0).getPlayer(), "Pushed player should remain at the initial position as the wall prevents movement!");
        assertNull(board.getSpace(2, 0).getPlayer(), "The space beyond the wall should remain empty as no movement is possible through walls.");
    }

    /**
     * Test that the gear function works
     *
     *
     */
    @Test
    void testTurnGearAction() {
        Board board = gameController.board;
        Player player = board.getPlayer(0);

        player.setSpace(board.getSpace(1, 1));
        player.setHeading(Heading.NORTH);

        //Gear turn left test
        TurnGear turnLeftGear = new TurnGear("left");
        board.getSpace(1, 1).getActions().add(turnLeftGear);

        boolean actionResult = turnLeftGear.doAction(gameController, board.getSpace(1, 1));

        assertTrue(actionResult, "TurnGear action should return true indicating it was successful.");

        assertEquals(Heading.WEST, player.getHeading(), "After turning left, player should be facing WEST.");

        //Gear turn right test
        TurnGear turnRightGear = new TurnGear("right");
        board.getSpace(1, 1).getActions().set(0, turnRightGear);

        actionResult = turnRightGear.doAction(gameController, board.getSpace(1, 1));

        assertTrue(actionResult, "TurnGear action should return true indicating it was successful.");

        assertEquals(Heading.NORTH, player.getHeading(), "After turning right from WEST, player should be facing NORTH again.");
    }

    /**
     * Test Antenna function if it's determining correct players to start and if distances are correctly calculated
     *
     *
     */
    @Test
    void testAntennaAdjustsPlayerTurnOrder() {
        Board board = gameController.board;
        Player player1 = board.getPlayer(0); // Closest to the antenna
        Player player2 = board.getPlayer(1); // Second closest
        Player player3 = board.getPlayer(2); // Most far away

        player1.setSpace(board.getSpace(0, 0));
        player2.setSpace(board.getSpace(1, 1));
        player3.setSpace(board.getSpace(2, 2));

        //We put the antenna at the board at (0,0) thus player1 is closest followed by player2 and player3
        Space antennaSpace = board.getSpace(0, 0);
        Antenna antenna = new Antenna();
        antennaSpace.getActions().add(antenna);

        Antenna.makeTurnOrder(gameController, antennaSpace);

        assertTrue(board.getPlayerTurn(0) == player1, "Player 1 should be first due to being closest to the antenna.");
        assertTrue(board.getPlayerTurn(1) == player2, "Player 2 should be second.");
        assertTrue(board.getPlayerTurn(2) == player3, "Player 3 should be last.");

        //Check if distances are calculated correctly
        assertTrue(player1.getDistanceToAntenna() < player2.getDistanceToAntenna() && player2.getDistanceToAntenna() < player3.getDistanceToAntenna(),
                "Player distances to antenna should increase from player1 to player3.");
    }

    /**
     * Players are same distance away from Antenna in a 2 player scenario
     *
     *
     */
    @Test
    void testAntennaIfPlayersAreSameDistanceAway() {
        Board board = gameController.board;
        Player player1 = board.getPlayer(0);
        Player player2 = board.getPlayer(1);

        player1.setSpace(board.getSpace(1, 0));
        player2.setSpace(board.getSpace(0, 1));

        Space antennaSpace = board.getSpace(0, 0);
        Antenna antenna = new Antenna();
        antennaSpace.getActions().add(antenna);

        Antenna.makeTurnOrder(gameController, antennaSpace);

        assertTrue(board.getPlayerTurn(0) == player1, "Player 1 should be first due to being closest to the antenna.");
        assertTrue(board.getPlayerTurn(1) == player2, "Player 2 should be second.");
    }
    /**
     * Test conveyor belt if it moves the player
     *
     *
     */
    @Test
    void testConveyorBeltMovesPlayer() {
        Board board = gameController.board;
        Player player = board.getPlayer(0);

        player.setSpace(board.getSpace(1, 1));
        player.setHeading(Heading.NORTH);

        ConveyorBelt conveyorBelt = new ConveyorBelt(Heading.EAST);
        board.getSpace(1, 1).getActions().add(conveyorBelt);

        boolean actionResult = conveyorBelt.doAction(gameController, board.getSpace(1, 1));

        assertTrue(actionResult, "Conveyor belt action should successfully move the player.");

        assertNull(board.getSpace(1, 1).getPlayer(), "Original space should now be empty.");
        assertEquals(player, board.getSpace(2, 1).getPlayer(), "Player should be moved to the east by the conveyor belt.");
    }

    /**
     * Test conveyor belt if it moves the player to another conveyorbelt - it should only be moved once
     *
     *
     */
    @Test
    void testConveyorBeltPushToAnotherConveyor() {
        Board board = gameController.board;
        Player player = board.getPlayer(0);

        player.setSpace(board.getSpace(1, 1));
        player.setHeading(Heading.NORTH);

        ConveyorBelt conveyorBelt1 = new ConveyorBelt(Heading.EAST);
        board.getSpace(1, 1).getActions().add(conveyorBelt1);
        ConveyorBelt conveyorBelt2 = new ConveyorBelt(Heading.NORTH);
        board.getSpace(2,1).getActions().add(conveyorBelt2);

        boolean actionResult = conveyorBelt1.doAction(gameController, board.getSpace(1, 1));

        assertTrue(actionResult, "Conveyor belt action should successfully move the player.");

        assertNull(board.getSpace(1, 1).getPlayer(), "Original space should now be empty.");
        assertEquals(player, board.getSpace(2, 1).getPlayer(), "Player should be moved to the east by the conveyor belt.");
    }


    /**
     * Test to check if deck works assuming no actions fields on the board purely checking if logic is valid in
     * 1 specific scenario
     *
     */
    @Test
    void testDeckMove() {
        Board board = gameController.board;
        Player player = board.getPlayer(0);

        //We define players position
        player.setSpace(board.getSpace(1,1));
        player.setHeading(Heading.SOUTH);

        //Define cards on hand
        player.getProgramField(0).setCard(new CommandCard(Command.FORWARD));
        player.getProgramField(1).setCard(new CommandCard(Command.FAST_FORWARD));
        player.getProgramField(2).setCard(new CommandCard(Command.LEFT));
        player.getProgramField(3).setCard(new CommandCard(Command.RIGHT));
        player.getProgramField(4).setCard(new CommandCard(Command.FORWARD));

        //We iterate until all cards have been executed
        for (int i = 0; i < 5; i++) {
            CommandCard card = player.getProgramField(i).getCard();
            if (card != null) {
                gameController.executeCommand(player, card.command);
            }
        }
        //We define final position
        int expectedX = 1;
        int expectedY = 5;
        Heading expectedFinalHeading = Heading.SOUTH;

        //Field should be (1,5) with a SOUTH heading
        assertEquals(board.getSpace(expectedX, expectedY), player.getSpace(), "Player should be at the correct final space.");
        assertEquals(expectedFinalHeading, player.getHeading(), "Player should have the correct final heading.");
    }
}
