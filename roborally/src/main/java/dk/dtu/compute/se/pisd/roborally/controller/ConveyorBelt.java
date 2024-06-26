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

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents a conveyor belt on a space.
 *
 * @author Ekkart Kindler, ekki@dtu.dk , Noah (s235441)
 *
 */
public class ConveyorBelt extends FieldAction {

    private Heading heading;

    /**
     * Returns the heading of the conveyor belt
     * 
     * @return the heading of the conveyor belt
     */
    public Heading getHeading() {
        return heading;
    }

    /**
     * Sets the heading of the conveyor belt
     * 
     * @param heading the heading of the conveyor belt
     */
    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    /**
     * Constructor
     * 
     * @param heading the heading of the conveyor belt
     */
    public ConveyorBelt(Heading heading) {
        setHeading(Heading.SOUTH);
        setHeading(heading);
    }

    /**
     * Default constructor
     */
    public ConveyorBelt() {

    }

    /**
     * Implementation of the action of a conveyor belt.
     * takes a player and moves them to the space in the direction of the heading of
     * the belt
     * 
     * @param gameController the given controller for the current instance
     * @param space          a given space that will have the belt action on it
     */
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        Board board = gameController.board;
        Space newspace = board.getNeighbour(space, heading);
        try {
            gameController.moveToSpace(space.getPlayer(), newspace, heading, 0);
            return true;
        } catch (ImpossibleMoveException e) {
        }
        return false;
    }

    /**
     * gets the special space
     * 
     * @return type as String
     */
    @Override
    public String getType() {
        return "Belt";
    }

}
