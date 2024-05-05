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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class SpaceView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 40; // 60; // 75; changing the height and width are not currently copatible with the current images used, and may cause issues
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

        //though this is overridden by the drawing of space and fieldActions, if something becomes strange this is still here to make sure the spaces can be differentiated

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
                ImageView belt = new ImageView(new Image(getClass().getResourceAsStream("/images/belt.png")));
                belt.setRotate((90 * ((ConveyorBelt)action).getHeading().ordinal()) % 360);
                this.getChildren().add(belt);
            }

        }
    }

    private void drawPit(){
        if (space.getActions().size() != 0){
            FieldAction action = space.getActions().get(0);
            if (action.getType().equals("Pit")){
                ImageView circle = new ImageView(new Image(getClass().getResourceAsStream("/images/pit.png")));
                this.getChildren().add(circle);
            }

        }
    }
    private void drawReboot(){
        if (space.getActions().size() != 0){
            FieldAction action = space.getActions().get(0);
            if (action.getType().equals("Reboot")){
                ImageView circle = new ImageView(new Image(getClass().getResourceAsStream("/images/reboot.png")));
                this.getChildren().add(circle);
            }

        }
    }

    private void drawAntenna(){
        if (space.getActions().size() != 0){
            FieldAction action = space.getActions().get(0);
            if (action.getType().equals("Antenna")){
                ImageView circle = new ImageView(new Image(getClass().getResourceAsStream("/images/antenna.png")));
                this.getChildren().add(circle);
            }

        }
    }


    private void drawSpace(){
        ImageView image = new ImageView(new Image(getClass().getResourceAsStream("/images/space.png")));
        this.getChildren().add(image);
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

    }

    /**
     * @param subject
     */
    @Override
    public void updateView(Subject subject) {
        this.getChildren().clear();
        if (space.getActions().size() != 0){
            switch (space.getActions().get(0).getType()){
                case "gear":
                    drawGear();
                    break;
                case "Belt":
                    drawBelt();
                    break;
                case "Checkpoint":
                    updateCheckpoint();
                    break;
                case "Pit":
                    drawPit();
                    break;
                case "Reboot":
                    drawReboot();
                    break;
                case "Antenna":
                    drawAntenna();
                    break;
            } 
        } else{
            drawSpace();
        }

        if (space.getWalls().size() != 0){
            for(Heading wall: space.getWalls()){
                drawWall(wall);
            }
        }
        updatePlayer();
    }
    
    /**
     * draws a wall on the space
     *
     * @param heading the direction of the wall
     * @author Alex Lundberg
     */
    public void drawWall(Heading heading) {
        ImageView wall = new ImageView(new Image(getClass().getResourceAsStream("/images/wall.png")));
        wall.setRotate(90 * (heading.ordinal()) % 360);
        this.getChildren().add(wall);


    }
    /**
     * draws a gear on the space
     *
     * author Alex Lundberg
     */
    public void drawGear() {
        TurnGear gear = (TurnGear) space.getActions().get(0);
        ImageView gearImage;

        if (gear.getDirection().equals("left")){
            gearImage = new ImageView(new Image(getClass().getResourceAsStream("/images/gearleft.png")));
        } else{
            gearImage = new ImageView(new Image(getClass().getResourceAsStream("/images/gearright.png")));
        }

        this.getChildren().add(gearImage); 
    }

    /**
     *The visuals of a checkpoint on a space.
     * Draws a circle with the checkpoint number.
     *
     * author Julius Sondergaard, s234096
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
     * Draws circle with the checkpoint number.
     *
     * @param gc GraphicsContext for drawing on the canvas.
     * @param checkpointNumber The checkpoint number to draw.
     * author Julius Sondergaard, s234096
     */
    private void drawCheckpointCircleAndNumber(GraphicsContext gc, int checkpointNumber) {
        ImageView star = new ImageView(new Image(getClass().getResourceAsStream("/images/checkpoint.png")));

        gc.setFill(Color.BLACK);

        gc.setFont(new Font("Arial", 15));

        String text = String.valueOf(checkpointNumber);

        Text tempText = new Text(text);
        tempText.setFont(gc.getFont());
        double textWidth = tempText.getLayoutBounds().getWidth();
        double textHeight = tempText.getLayoutBounds().getHeight();

        gc.fillText(text, 20 - textWidth/2, 20 + textHeight/3);
        this.getChildren().add(star);
    }

    }


