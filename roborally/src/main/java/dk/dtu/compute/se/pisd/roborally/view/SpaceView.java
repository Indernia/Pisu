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


        // updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    /**
     * Draws the belt polygon on any action field that is a belt
     *
     * @author Noah Nissen
     */
    private void drawBelt(){
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
    }

    private void updatePlayer() {

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
            // TODO Unsafe type cast, secure later
                TurnGear gear = (TurnGear) action;
                drawGear(gear);
            }
        }

    
    }

    /**
     * @param subject
     */
    @Override
    public void updateView(Subject subject) {
        this.getChildren().clear();
        drawBelt();
        if (subject == this.space) {
            updateCheckpoint();
        }
        if (space.getWalls().size() != 0){
            for(Heading wall: space.getWalls()){
                drawWall(wall);
            }
        }
        updatePlayer();

    }
    
    // @author Alex Lundberg
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
    // @author Alex Lundberg
    public void drawGear(TurnGear gear) {
                // TODO redo as svg path, but this is placeholder
        Pane pane = new Pane();
        Rectangle rectangle = new Rectangle(0.0, 0.0, SPACE_WIDTH, SPACE_HEIGHT);
        rectangle.setFill(Color.TRANSPARENT);
        pane.getChildren().add(rectangle);
        
        SVGPath arrow = new SVGPath();
        String path;
        if (gear.getDirection() == "left"){
            path = "M4.959,22.684c0.225,0.275,0.589,0.275,0.813,0.0l4.841-5.923c0.225-0.275,0.119-0.498-0.236-0.498h-2.571 c-0.355,0.0-0.596-0.284-0.52-0.631c1.322-6.02,6.694-10.539,13.104-10.539c7.4,0.0,13.419,6.02,13.419,13.419 c0.0,7.4-6.02,13.419-13.419,13.419c-0.887,0.0-1.607,0.719-1.607,1.606s0.719,1.606,1.607,1.606 c9.171,0.0,16.632-7.461,16.632-16.632c0.0-9.171-7.461-16.632-16.632-16.632 c-8.187,0.0-15.008,5.948-16.377,13.749c-0.061,0.35-0.386,0.635-0.741,0.635H0.354c-0.355,0.0-0.461,0.223-0.236,0.498 L4.959,22.684z";
        } else{
            path = "M36.670,16.263h-2.919c-0.355,0.000-0.680-0.285-0.741-0.635c-1.370-7.801-8.190-13.749-16.377-13.749 C7.461,1.880,0.000,9.341,0.000,18.512c0.000,9.171,7.461,16.632,16.632,16.632c0.887,0.000,1.607-0.719,1.607-1.606 s-0.719-1.606-1.607-1.606c-7.400,0.000-13.419-6.020-13.419-13.419c0.000-7.399,6.020-13.419,13.419-13.419 c6.411,0.000,11.783,4.520,13.105,10.539c0.076,0.347-0.165,0.631-0.520,0.631H26.646c-0.355,0.000-0.460,0.223-0.236,0.498 l4.841,5.923c0.225,0.275,0.589,0.275,0.813,0.000l4.841-5.923C37.131,16.486,37.025,16.263,36.670,16.263z";
        }
        arrow.setContent(path);
        arrow.setFill(Color.GREEN);
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


