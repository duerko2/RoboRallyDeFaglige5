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
package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.controller.CheckPoint;
import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.*;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class LoadBoard {

    private static final String BOARDSFOLDER = "boards";
    private static final String SAVESFOLDER = "saves";
    private static final String DEFAULTBOARD = "defaultboard";
    private static final String JSON_EXT = "json";
    private static final String RESOURCEFOLDER = "resources";

    public static Board loadBoard(String boardname) {
        if (boardname == null) {
            boardname = DEFAULTBOARD;
        }


/*

        ClassLoader classLoader = LoadBoard.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("RoboRally/roborally-1.1.0-java17/roborally/src/main/resources/boards" + "/" + boardname);
        if (inputStream == null) {
            // TODO these constants should be defined somewhere
            return new Board(8,8);
        }

 */

		// In simple cases, we can create a Gson object with new Gson():
        GsonBuilder simpleBuilder = new GsonBuilder();
        Gson gson = simpleBuilder.create();

		Board result;
		// FileReader fileReader = null;
        //JsonReader reader = null;
        Reader reader = null;
		try {
			// fileReader = new FileReader(filename);
			//reader = gson.newJsonReader(new InputStreamReader(inputStream));

            reader = Files.newBufferedReader(Paths.get("RoboRally/roborally-1.1.0-java17/roborally/src/main/resources/boards" + "/" + boardname));

            // Creates boardtemplate based on JSON file
			BoardTemplate template = gson.fromJson(reader, BoardTemplate.class);

            //Creates a new board with size from boardtemplate
			result = new Board(template.width, template.height);

            //Iterates through boardtemplate's spaces and adds information to the board's spaces if available.
			for (SpaceTemplate spaceTemplate: template.spaces) {
			    Space space = result.getSpace(spaceTemplate.x, spaceTemplate.y);
			    if (space != null) {

                    // Adds walls if available.
                    for (int i = 0;i<spaceTemplate.walls.size();i++) {
                        space.getWalls().addAll(spaceTemplate.walls);
                    }

                    // Adds checkpoint if available.
                    if(spaceTemplate.checkPoint!=null){
                        space.getActions().add(new CheckPoint(space,spaceTemplate.checkPoint.number));
                    }

                    // Adds conveyorbelt if available.
                    if(spaceTemplate.conveyorBelt!=null){
                        space.getActions().add(new ConveyorBelt(space,spaceTemplate.conveyorBelt.heading, spaceTemplate.conveyorBelt.isDouble));
                    }
                }
            }
			reader.close();
			return result;
		} catch (IOException e1) {
            if (reader != null) {
                try {
                    reader.close();
                    reader = null;
                } catch (IOException e2) {}
            }
            if (reader != null) {
				try {
					reader.close();
				} catch (IOException e2) {}
			}
		} catch (NullPointerException e3){
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e2) {
                }
            }
            return new Board(8,8);
        }
		return null;
    }

    public static void saveGame(Board board, String name) {

        //Creates board template based on board size.
        BoardTemplate template = new BoardTemplate();
        template.width = board.width;
        template.height = board.height;


        // Iterates through the boards spaces
        for (int i=0; i<board.width; i++) {
            for (int j=0; j<board.height; j++) {
                Space space = board.getSpace(i,j);


                //Checks if the current space has walls or fieldactions.
                if (!space.getWalls().isEmpty()||!space.getActions().isEmpty()) {

                    //Creates a spacetemplate object to be written to JSON file. Only writes it if it contains element.
                    SpaceTemplate spaceTemplate = new SpaceTemplate();
                    spaceTemplate.x = space.x;
                    spaceTemplate.y = space.y;

                    //Adds walls to spaceTemplate if it has walls.
                    if(!space.getWalls().isEmpty()) {

                        spaceTemplate.walls.addAll(space.getWalls());
                    }

                    //Adds fieldactions to spacetemplate if it has any of those.
                    if(!space.getActions().isEmpty()){
                        for(int l=0;l<space.getActions().size();l++){
                            FieldAction fieldAction = space.getActions().get(l);
                            if(fieldAction instanceof CheckPoint){
                                spaceTemplate.checkPoint = new CheckPointTemplate();
                                spaceTemplate.checkPoint.number=((CheckPoint) fieldAction).getNumber();
                            }
                            if(fieldAction instanceof ConveyorBelt){
                                spaceTemplate.conveyorBelt = new ConveyorBeltTemplate();
                                spaceTemplate.conveyorBelt.heading= ((ConveyorBelt) fieldAction).getHeading();
                                spaceTemplate.conveyorBelt.isDouble= ((ConveyorBelt) fieldAction).getIsDouble();
                            }

                        }
                    }

                    // Adds the spacetemplate to the BoardTemplate.
                    template.spaces.add(spaceTemplate);
                }



            }
        }

        for(int i=0;i<board.getPlayersNumber();i++){
            PlayerTemplate playerTemplate = new PlayerTemplate();
            playerTemplate.x=board.getPlayer(i).getSpace().x;
            playerTemplate.y=board.getPlayer(i).getSpace().y;
            playerTemplate.color=board.getPlayer(i).getColor();
            playerTemplate.name=board.getPlayer(i).getName();
            playerTemplate.checkPoint=board.getPlayer(i).getCurrentCheckPoint();
            playerTemplate.heading=board.getPlayer(i).getHeading();

            if(board.getPlayer(i)==board.getCurrentPlayer()){
                playerTemplate.currentPlayer=true;
            }



            for(int j=0;j<PlayerTemplate.NO_CARDS;j++){
                CardTemplate cardTemplate = new CardTemplate();
                cardTemplate.card=board.getPlayer(i).getCardField(j).getCard();
                cardTemplate.visible=board.getPlayer(i).getCardField(j).isVisible();
                playerTemplate.cards[j]=cardTemplate;
            }
            for(int j=0;j<PlayerTemplate.NO_REGISTERS;j++){
                CardTemplate cardTemplate = new CardTemplate();
                cardTemplate.card=board.getPlayer(i).getProgramField(j).getCard();
                cardTemplate.visible=board.getPlayer(i).getProgramField(j).isVisible();
                playerTemplate.program[j]=cardTemplate;
            }



            template.players.add(playerTemplate);


        }



        ClassLoader classLoader = LoadBoard.class.getClassLoader();
        // TODO: this is not very defensive, and will result in a NullPointerException
        //       when the folder "resources" does not exist! But, it does not need
        //       the file "simpleCards.json" to exist!
        String filename =
                classLoader.getResource(BOARDSFOLDER).getPath() + "/" + name + "." + JSON_EXT;

        // In simple cases, we can create a Gson object with new:
        //
        //   Gson gson = new Gson();
        //
        // But, if you need to configure it, it is better to create it from
        // a builder (here, we want to configure the JSON serialisation with
        // a pretty printer):
        GsonBuilder simpleBuilder = new GsonBuilder().
                setPrettyPrinting();
        Gson gson = simpleBuilder.create();


        FileWriter fileWriter = null;
        JsonWriter writer = null;
        try {
            fileWriter = new FileWriter(filename);
            writer = gson.newJsonWriter(fileWriter);
            gson.toJson(template, template.getClass(), writer);
            writer.close();

        } catch (IOException e1) {
            if (writer != null) {
                try {
                    writer.close();
                    fileWriter = null;
                } catch (IOException e2) {}
            }
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e2) {}
            }
        }
    }

    public static Board loadGame(String boardname) {
        if (boardname == null) {
            boardname = DEFAULTBOARD;
        }

        ClassLoader classLoader = LoadBoard.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(BOARDSFOLDER + "/" + boardname + "." + JSON_EXT);
        if (inputStream == null) {
            // TODO these constants should be defined somewhere
            return new Board(8,8);
        }

        // In simple cases, we can create a Gson object with new Gson():
        GsonBuilder simpleBuilder = new GsonBuilder();
        Gson gson = simpleBuilder.create();

        Board result;
        JsonReader reader = null;
        try {
            reader = gson.newJsonReader(new InputStreamReader(inputStream));

            // Creates boardtemplate based on JSON file
            BoardTemplate template = gson.fromJson(reader, BoardTemplate.class);

            //Creates a new board with size from boardtemplate
            result = new Board(template.width, template.height);

            //Iterates through boardtemplate's spaces and adds information to the board's spaces if available.
            for (SpaceTemplate spaceTemplate: template.spaces) {
                Space space = result.getSpace(spaceTemplate.x, spaceTemplate.y);
                if (space != null) {

                    // Adds walls if available.
                    for (int i = 0;i<spaceTemplate.walls.size();i++) {
                        space.getWalls().addAll(spaceTemplate.walls);
                    }

                    // Adds checkpoint if available.
                    if(spaceTemplate.checkPoint!=null){
                        space.getActions().add(new CheckPoint(space,spaceTemplate.checkPoint.number));
                    }

                    // Adds conveyorbelt if available.
                    if(spaceTemplate.conveyorBelt!=null){
                        space.getActions().add(new ConveyorBelt(space,spaceTemplate.conveyorBelt.heading, spaceTemplate.conveyorBelt.isDouble));
                    }
                }
            }


            // Adds players to the Board
            for(int i=0;i<template.players.size();i++){

                // Creates playertemplate object
                PlayerTemplate playerTemplate = template.players.get(i);



                //Creates player object from playertemplate info
                Player player = new Player(result,playerTemplate.color,playerTemplate.name,playerTemplate.checkPoint);

                // Checks if this player is the current player
                if(playerTemplate.currentPlayer){
                    result.setCurrentPlayer(player);
                }

                // Informs the player object of the location
                player.setSpace(result.getSpace(playerTemplate.x,playerTemplate.y));

                //Informs the player object of the heading.
                player.setHeading(playerTemplate.heading);

                // Adds players cards
                for(int j=0;j<PlayerTemplate.NO_CARDS;j++){

                    // Creates card template instance
                    CardTemplate cardTemplate = new CardTemplate();

                    // Loads information into template from playerTemplate
                    cardTemplate.card=playerTemplate.cards[j].card;
                    cardTemplate.visible=playerTemplate.cards[j].visible;

                    // Loads information into player object from card template.
                    player.getCardField(j).setCard(cardTemplate.card);
                    player.getCardField(j).setVisible(cardTemplate.visible);
                }

                // Adds players register
                for(int j=0;j<PlayerTemplate.NO_REGISTERS;j++){
                    // Creates card template instance
                    CardTemplate cardTemplate = new CardTemplate();

                    // Loads information into template from playerTemplate
                    cardTemplate.card=playerTemplate.program[j].card;
                    cardTemplate.visible=playerTemplate.program[j].visible;

                    // Loads information into player object from card template.
                    player.getProgramField(j).setCard(cardTemplate.card);
                    player.getProgramField(j).setVisible(cardTemplate.visible);
                }


                result.getPlayers().add(player);
            }


            reader.close();
            return result;
        } catch (IOException e1) {
            if (reader != null) {
                try {
                    reader.close();
                    reader = null;
                } catch (IOException e2) {}
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e2) {}
            }
        } catch (NullPointerException e3){
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e2) {
                }
            }
            return new Board(8,8);
        }
        return null;
    }
}
