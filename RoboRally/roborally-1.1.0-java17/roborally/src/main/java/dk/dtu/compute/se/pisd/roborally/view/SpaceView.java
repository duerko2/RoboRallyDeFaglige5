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
import dk.dtu.compute.se.pisd.roborally.model.CheckPoint;
import dk.dtu.compute.se.pisd.roborally.model.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.model.FieldAction;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class SpaceView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 60; // 60; // 75;
    final public static int SPACE_WIDTH = 60;  // 60; // 75;

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

    private void updatePlayer() {
        this.getChildren().clear();

        Player player = space.getPlayer();
        if (player != null) {
            try {
                Image image = new Image("File:RoboRally/roborally-1.1.0-java17/roborally/src/main/resources/players/" + player.getColor() + ".png", 60, 60, false, false);
                ImageView imageView = new ImageView(image);
                imageView.setRotate((90*player.getHeading().ordinal())%360);
                this.getChildren().add(imageView);

            }catch(Exception e){
                Polygon arrow = new Polygon(0.0, 0.0,
                    10.0, 20.0,
                    20.0, 0.0 );
                    arrow.setFill(Color.valueOf(player.getColor()));


                arrow.setRotate((90*player.getHeading().ordinal())%360);
                this.getChildren().add(arrow);
            }

        }
    }

    @Override
    public void updateView(Subject subject) {
        if (subject == this.space) {
            updatePlayer();
        }

        //Sets the background tile
        Image backgroundTile = new Image("File:RoboRally/roborally-1.1.0-java17/roborally/src/main/resources/Tile.png", 60, 60, false, false);
        ImageView bgtileView = new ImageView(backgroundTile);
        this.getChildren().add(bgtileView);


        for(int i = 0; i <space.getActions().size();i++) {
            FieldAction fieldaction = space.getActions().get(i);

            //Visualizes CheckPoints
            if (fieldaction instanceof CheckPoint) {
                CheckPoint checkpoint = (CheckPoint) space.getActions().get(i);
                Image image = new Image("File:RoboRally/roborally-1.1.0-java17/roborally/src/main/resources/placeholder.png",60, 60, false, false);
                switch(checkpoint.getNumber()){
                    case 1:
                        image = new Image("File:RoboRally/roborally-1.1.0-java17/roborally/src/main/resources/checkpoints/checkpoint1.png",60, 60, false, false);
                        break;
                    case 2:
                        image = new Image("File:RoboRally/roborally-1.1.0-java17/roborally/src/main/resources/checkpoints/checkpoint2.png",60, 60, false, false);
                        break;
                    case 3:
                        image = new Image("File:RoboRally/roborally-1.1.0-java17/roborally/src/main/resources/checkpoints/checkpoint3.png",60, 60, false, false);
                        break;
                    case 4:
                        image = new Image("File:RoboRally/roborally-1.1.0-java17/roborally/src/main/resources/checkpoints/checkpoint4.png",60, 60, false, false);
                        break;
                    case 5:
                        image = new Image("File:RoboRally/roborally-1.1.0-java17/roborally/src/main/resources/checkpoints/checkpoint5.png",60, 60, false, false);
                        break;
                    case 6:
                        image = new Image("File:RoboRally/roborally-1.1.0-java17/roborally/src/main/resources/checkpoints/checkpoint6.png",60, 60, false, false);
                        break;
                }
                ImageView imageView = new ImageView(image);
                this.getChildren().add(imageView);
                imageView.toBack();

            }

            //Visualizes ConveyorBelts
            if(fieldaction instanceof ConveyorBelt) {
                ConveyorBelt conveyorBelt = (ConveyorBelt) space.getActions().get(i);
                Image image;
                if (((ConveyorBelt) space.getActions().get(i)).getIsDouble()) {
                    image = new Image("File:RoboRally/roborally-1.1.0-java17/roborally/src/main/resources/doubleconbelt.png", 60, 60, false, false);
                } else {
                    image = new Image("File:RoboRally/roborally-1.1.0-java17/roborally/src/main/resources/conbelt.png", 60, 60, false, false);
                }
                ImageView imageView = new ImageView(image);
                switch (conveyorBelt.getHeading()) {
                    case SOUTH:
                        imageView.setRotate(180);
                        break;
                    case WEST:
                        imageView.setRotate(270);
                        break;
                    case NORTH:
                        break;
                    case EAST:
                        imageView.setRotate(90);
                        break;
                }

                this.getChildren().add(imageView);
                imageView.toBack();
            }
        }


        if(!space.getWalls().isEmpty()){
            int startY=SPACE_HEIGHT-2;
            int startX=2;
            int endX=SPACE_WIDTH-2;
            int endY=SPACE_HEIGHT-2;


            for(int i=0;i<this.space.getWalls().size();i++){
                if(this.space.getWalls()!=null){
                Pane pane = new Pane();
                Rectangle rectangle =
                        new Rectangle(0.0, 0.0, SPACE_WIDTH, SPACE_HEIGHT);
                rectangle.setFill(Color.TRANSPARENT);
                pane.getChildren().add(rectangle);



                switch(this.space.getWalls().get(i)){
                    case EAST:
                        startY=2;
                        startX=SPACE_WIDTH-2;
                        endX=SPACE_WIDTH-2;
                        endY=SPACE_HEIGHT-2;
                        break;
                    case WEST:
                        startY=SPACE_HEIGHT-2;
                        startX=2;
                        endX=2;
                        endY=2;
                        break;
                    case NORTH:
                        startY=2;
                        startX=2;
                        endX=SPACE_WIDTH-2;
                        endY=2;
                        break;
                    case SOUTH:
                        startY=SPACE_HEIGHT-2;
                        startX=2;
                        endX=SPACE_WIDTH-2;
                        endY=SPACE_HEIGHT-2;
                        break;
                }

                Line line =
                        new Line(startX, startY, endX,
                                endY);
                line.setStroke(Color.RED);
                line.setStrokeWidth(5);
                pane.getChildren().add(line);
                this.getChildren().add(pane);
            }
            }
        }
        //Keeps the background tile behind everything else that is shown on the space.
        bgtileView.toBack();
    }


}
