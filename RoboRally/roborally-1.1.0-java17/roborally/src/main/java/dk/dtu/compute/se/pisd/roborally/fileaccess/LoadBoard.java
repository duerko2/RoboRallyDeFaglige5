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
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.BoardTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.CardTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.PlayerTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.SpaceTemplate;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

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
		// FileReader fileReader = null;
        JsonReader reader = null;
		try {
			// fileReader = new FileReader(filename);
			reader = gson.newJsonReader(new InputStreamReader(inputStream));
			BoardTemplate template = gson.fromJson(reader, BoardTemplate.class);

			result = new Board(template.width, template.height);
			for (SpaceTemplate spaceTemplate: template.spaces) {
			    Space space = result.getSpace(spaceTemplate.x, spaceTemplate.y);
			    if (space != null) {
                    for (int i = 0;i<spaceTemplate.walls.length;i++) {
                        space.setWall(spaceTemplate.walls);
                    }
                }
            }
			reader.close();
			return result;
		} catch (IOException e1) {
            if (reader != null) {
                try {
                    reader.close();
                    inputStream = null;
                } catch (IOException e2) {}
            }
            if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e2) {}
			}
		}
		return null;
    }

    public static void saveBoard(Board board, String name) {
        BoardTemplate template = new BoardTemplate();
        template.width = board.width;
        template.height = board.height;

        for (int i=0; i<board.width; i++) {
            for (int j=0; j<board.height; j++) {
                Space space = board.getSpace(i,j);

                if (space.getWalls()[0]!=null) {
                    SpaceTemplate spaceTemplate = new SpaceTemplate();
                    spaceTemplate.x = space.x;
                    spaceTemplate.y = space.y;
                    spaceTemplate.walls=space.getWalls();
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





}
