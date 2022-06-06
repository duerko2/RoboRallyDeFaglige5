package dk.dtu.compute.se.pisd.roborally.springRequest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dk.dtu.compute.se.pisd.roborally.controller.CheckPoint;
import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.*;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Space;

public class JsonConverter {

    public static String gameToJson(Board board) {

        //Creates board template based on board size.
        BoardTemplate template = new BoardTemplate();
        template.width = board.width;
        template.height = board.height;


        // Iterates through the boards spaces
        for (int i = 0; i < board.width; i++) {
            for (int j = 0; j < board.height; j++) {
                Space space = board.getSpace(i, j);


                //Checks if the current space has walls or fieldactions.
                if (!space.getWalls().isEmpty() || !space.getActions().isEmpty()) {

                    //Creates a spacetemplate object to be written to JSON file. Only writes it if it contains element.
                    SpaceTemplate spaceTemplate = new SpaceTemplate();
                    spaceTemplate.x = space.x;
                    spaceTemplate.y = space.y;

                    //Adds walls to spaceTemplate if it has walls.
                    if (!space.getWalls().isEmpty()) {

                        spaceTemplate.walls.addAll(space.getWalls());
                    }

                    //Adds fieldactions to spacetemplate if it has any of those.
                    if (!space.getActions().isEmpty()) {
                        for (int l = 0; l < space.getActions().size(); l++) {
                            FieldAction fieldAction = space.getActions().get(l);
                            if (fieldAction instanceof CheckPoint) {
                                spaceTemplate.checkPoint = new CheckPointTemplate();
                                spaceTemplate.checkPoint.number = ((CheckPoint) fieldAction).getNumber();
                            }
                            if (fieldAction instanceof ConveyorBelt) {
                                spaceTemplate.conveyorBelt = new ConveyorBeltTemplate();
                                spaceTemplate.conveyorBelt.heading = ((ConveyorBelt) fieldAction).getHeading();
                                spaceTemplate.conveyorBelt.isDouble = ((ConveyorBelt) fieldAction).getIsDouble();
                            }

                        }
                    }

                    // Adds the spacetemplate to the BoardTemplate.
                    template.spaces.add(spaceTemplate);
                }


            }
        }

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            PlayerTemplate playerTemplate = new PlayerTemplate();
            playerTemplate.x = board.getPlayer(i).getSpace().x;
            playerTemplate.y = board.getPlayer(i).getSpace().y;
            playerTemplate.color = board.getPlayer(i).getColor();
            playerTemplate.name = board.getPlayer(i).getName();
            playerTemplate.checkPoint = board.getPlayer(i).getCurrentCheckPoint();
            playerTemplate.heading = board.getPlayer(i).getHeading();

            if (board.getPlayer(i) == board.getCurrentPlayer()) {
                playerTemplate.currentPlayer = true;
            }


            for (int j = 0; j < PlayerTemplate.NO_CARDS; j++) {
                CardTemplate cardTemplate = new CardTemplate();
                cardTemplate.card = board.getPlayer(i).getCardField(j).getCard();
                cardTemplate.visible = board.getPlayer(i).getCardField(j).isVisible();
                playerTemplate.cards[j] = cardTemplate;
            }
            for (int j = 0; j < PlayerTemplate.NO_REGISTERS; j++) {
                CardTemplate cardTemplate = new CardTemplate();
                cardTemplate.card = board.getPlayer(i).getProgramField(j).getCard();
                cardTemplate.visible = board.getPlayer(i).getProgramField(j).isVisible();
                playerTemplate.program[j] = cardTemplate;
            }


            template.players.add(playerTemplate);
        }

        GsonBuilder simpleBuilder = new GsonBuilder().
                setPrettyPrinting();
        Gson gson = simpleBuilder.create();

        return gson.toJson(template,template.getClass());
    }

    public static Board jsonToBoard(String game) {

        return null;
    }
}