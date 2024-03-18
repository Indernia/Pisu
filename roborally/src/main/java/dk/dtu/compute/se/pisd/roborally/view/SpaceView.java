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
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.Checkpoint;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.TurnGear;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.model.Wall;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class SpaceView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 40; // 60; // 75;
    final public static int SPACE_WIDTH = 40; // 60; // 75;

    public final Space space;

    public SpaceView(@NotNull Space space) {
        this.space = space;

        // XXX the following styling should better be done with styles
        this.setPrefWidth(SPACE_WIDTH);
        this.setMinWidth(SPACE_WIDTH);
        this.setMaxWidth(SPACE_WIDTH);

        this.setPrefHeight(SPACE_HEIGHT);
        this.setMinHeight(SPACE_HEIGHT);
        this.setMaxHeight(SPACE_HEIGHT);

        if ((space.x + space.y) % 2 == 0) {
            this.setStyle("-fx-background-color: white;");
        } else {
            this.setStyle("-fx-background-color: black;");
        }

        //TODO remove
        if (space.getActions().size() != 0){
            FieldAction action = space.getActions().get(0);
            drawGear();
            if (action.getType() == "gear"){
                this.setStyle("-fx-background-color: blue;");
            }
        }

        //TODO End

        // updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    private void updatePlayer() {
        this.getChildren().clear();

        if (space.getActions().size() != 0){
            FieldAction action = space.getActions().get(0);
            if (action.getType().equals("Belt")){
                Polygon arrow = new Polygon(0.0, 0.0,
                        15.0, 30.0,
                        30.0, 0.0);

                arrow.setFill(Color.DARKGREY);


                arrow.setRotate((90 * ((ConveyorBelt)action).getHeading().ordinal()) % 360);
                this.getChildren().add(arrow);
            }

        }

        Player player = space.getPlayer();
        if (player != null) {
            Polygon arrow = new Polygon(0.0, 0.0,
                    10.0, 20.0,
                    20.0, 0.0);
            try {
                arrow.setFill(Color.valueOf(player.getColor()));
            } catch (Exception e) {
                arrow.setFill(Color.MEDIUMPURPLE);
            }

            arrow.setRotate((90 * player.getHeading().ordinal()) % 360);
            this.getChildren().add(arrow);
        }

                     if (space.getActions().size() != 0){
            FieldAction action = space.getActions().get(0);
            if (action.getType() == "gear"){
                drawGear();
            }
        }

    
    }

    /**
     * @param subject
     */
    @Override
    public void updateView(Subject subject) {
        if (subject == this.space) {
            updatePlayer();
            updateCheckpoint();
        }
        if (space.getWalls().size() != 0){
            for(Heading wall: space.getWalls()){
                drawWall(wall);
            }
        }

    }

    public void drawWall(Heading heading) {
    Canvas canvas = new Canvas(SPACE_WIDTH, SPACE_HEIGHT);
    GraphicsContext gc =
    canvas.getGraphicsContext2D();
    gc.setStroke(Color.RED);
    gc.setLineWidth(5);
    gc.setLineCap(StrokeLineCap.ROUND);

    switch (heading) {
        case SOUTH:
            gc.strokeLine(2, SPACE_HEIGHT - 2, SPACE_WIDTH - 2, SPACE_HEIGHT - 2);
            break;
        case NORTH:
            gc.strokeLine(2, 2, SPACE_WIDTH - 2, 2);
            break;
        case WEST:
            gc.strokeLine(2, 2, 2, SPACE_HEIGHT - 2);
            break;
        case EAST:
            gc.strokeLine(SPACE_WIDTH - 2, 2, SPACE_WIDTH - 2, SPACE_HEIGHT - 2);
            break;
    }
            
    this.getChildren().add(canvas);

    }

    public void drawGear() {
                // TODO redo as svg path, but this is placeholder
        Pane pane = new Pane();
        Rectangle rectangle = new Rectangle(0.0, 0.0, SPACE_WIDTH, SPACE_HEIGHT);
        rectangle.setFill(Color.TRANSPARENT);
        pane.getChildren().add(rectangle);
        
        SVGPath arrow = new SVGPath();
        String path = "M0.0,324.059c3.21,3.926,8.409,3.926,11.619,0l69.162-84.621c3.21-3.926,1.698-7.108-3.372-7.108h-36.723 c-5.07,0-8.516-4.061-7.427-9.012c18.883-85.995,95.625-150.564,187.207-150.564c105.708,0,191.706,85.999,191.706,191.706 c0,105.709-85.998,191.707-191.706,191.707c-12.674,0-22.95,10.275-22.95,22.949s10.276,22.949,22.95,22.949 c131.018,0,237.606-106.588,237.606-237.605c0-131.017-106.589-237.605-237.606-237.605 c-116.961,0-214.395,84.967-233.961,196.409c-0.878,4.994-5.52,9.067-10.59,9.067H5.057c-5.071,0-6.579,3.182-3.373,7.108 L70.846,324.059z";
        arrow.setContent(path);
        arrow.setFill(Color.GREEN);
        arrow.setScaleY(0.05);
        arrow.setScaleX(0.05);
        pane.getChildren().add(arrow);

        // Add the pane to the children of the SpaceView (StackPane)
        this.getChildren().add(pane); 
    }

    /**
     * ...
     *
     * @author Julius Sondergaard, s234096
     *
     */
    private void updateCheckpoint() {
        for (FieldAction action : space.getActions()) {
            if (action instanceof Checkpoint) {
                Checkpoint checkpoint = (Checkpoint) action;
                Canvas canvas = new Canvas(SPACE_WIDTH, SPACE_HEIGHT);
                GraphicsContext gc = canvas.getGraphicsContext2D();
                drawCheckpointCircleAndNumber(gc, checkpoint.getCheckpointNumber());
                this.getChildren().add(canvas);
                break;
            }
        }
    }

    /**
     * ...
     *
     * @author Julius Sondergaard, s234096
     *
     */
    private void drawCheckpointCircleAndNumber(GraphicsContext gc, int checkpointNumber) {
        double circleDiameter = Math.min(SPACE_WIDTH, SPACE_HEIGHT) * 0.8;
        double circleX = (SPACE_WIDTH - circleDiameter) / 2;
        double circleY = (SPACE_HEIGHT - circleDiameter) / 2;

        gc.setFill(Color.LIGHTGREEN);
        gc.fillOval(circleX, circleY, circleDiameter, circleDiameter);

        gc.setFill(Color.BLACK);

        gc.setFont(new Font("Arial", 20));

        String text = String.valueOf(checkpointNumber);

        Text tempText = new Text(text);
        tempText.setFont(gc.getFont());
        double textWidth = tempText.getBoundsInLocal().getWidth();
        double textHeight = tempText.getBoundsInLocal().getHeight();

        double textX = circleX + (circleDiameter - textWidth) / 2;
        double textY = circleY + (circleDiameter + textHeight) / 2;

        gc.fillText(text, textX, textY);
    }

    }


