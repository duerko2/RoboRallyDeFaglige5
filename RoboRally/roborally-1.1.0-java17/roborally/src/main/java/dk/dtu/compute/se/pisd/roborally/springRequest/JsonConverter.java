package dk.dtu.compute.se.pisd.roborally.springRequest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dk.dtu.compute.se.pisd.roborally.model.CheckPoint;
import dk.dtu.compute.se.pisd.roborally.model.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.model.FieldAction;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.*;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Game;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

public class JsonConverter {

    /**
     * Converts a game to a JSON file string
     */

    public static String gameToJson(Game game) {
        Board board = game.getBoard();

        //Creates board template based on board size.
        BoardTemplate template = new BoardTemplate();
        template.currentPhase = board.getPhase();
        template.width = board.width;
        template.height = board.height;
        template.step=board.getStep();


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
        GameTemplate gameTemplate = new GameTemplate();
        gameTemplate.board=template;
        gameTemplate.serialNumber=game.getSerialNumber();
        gameTemplate.maxAmountOfPlayers=game.getMaxAmountOfPlayers();
        gameTemplate.readyToReceivePlayers=game.getReadyToReceivePlayers();
        gameTemplate.gameWinner=game.getWinner();

        GsonBuilder simpleBuilder = new GsonBuilder().
                setPrettyPrinting();
        Gson gson = simpleBuilder.create();

        return gson.toJson(gameTemplate,gameTemplate.getClass());
    }

    /**
     * Converts a json file in string format to a game
     */

    public static Game jsonToGame(String game) {

        // In simple cases, we can create a Gson object with new Gson():
        GsonBuilder simpleBuilder = new GsonBuilder();
        Gson gson = simpleBuilder.create();

            GameTemplate gameTemplate = gson.fromJson(game, GameTemplate.class);
            //Creates a new board with size from boardtemplate
            Board board = new Board(gameTemplate.board.width, gameTemplate.board.height,gameTemplate.board.amountOfCheckPoints);



            //Iterates through boardtemplate's spaces and adds information to the board's spaces if available.
            for (SpaceTemplate spaceTemplate : gameTemplate.board.spaces) {
                Space space = board.getSpace(spaceTemplate.x, spaceTemplate.y);
                if (space != null) {

                    // Adds walls if available.
                    for (int i = 0; i < spaceTemplate.walls.size(); i++) {
                        space.getWalls().addAll(spaceTemplate.walls);
                    }

                    // Adds checkpoint if available.
                    if (spaceTemplate.checkPoint != null) {
                        space.getActions().add(new CheckPoint(space, spaceTemplate.checkPoint.number));
                    }

                    // Adds conveyorbelt if available.
                    if (spaceTemplate.conveyorBelt != null) {
                        space.getActions().add(new ConveyorBelt(space, spaceTemplate.conveyorBelt.heading, spaceTemplate.conveyorBelt.isDouble));
                    }
                }
            }


            // Adds players to the Board
            for (int i = 0; i < gameTemplate.board.players.size(); i++) {

                // Creates playertemplate object
                PlayerTemplate playerTemplate = gameTemplate.board.players.get(i);


                //Creates player object from playertemplate info
                Player player = new Player(board, playerTemplate.color, playerTemplate.name, playerTemplate.checkPoint);


                // Informs the player object of the location
                player.setSpace(board.getSpace(playerTemplate.x, playerTemplate.y));
                board.getSpace(player.getSpace().x,player.getSpace().y).setPlayer(player);

                //Informs the player object of the heading.
                player.setHeading(playerTemplate.heading);

                // Adds players cards
                for (int j = 0; j < PlayerTemplate.NO_CARDS; j++) {

                    // Creates card template instance
                    CardTemplate cardTemplate = new CardTemplate();

                    // Loads information into template from playerTemplate
                    cardTemplate.card = playerTemplate.cards[j].card;
                    cardTemplate.visible = playerTemplate.cards[j].visible;

                    // Loads information into player object from card template.
                    player.getCardField(j).setCard(cardTemplate.card);
                    player.getCardField(j).setVisible(cardTemplate.visible);
                }

                // Adds players register
                for (int j = 0; j < PlayerTemplate.NO_REGISTERS; j++) {
                    // Creates card template instance
                    CardTemplate cardTemplate = new CardTemplate();

                    // Loads information into template from playerTemplate
                    cardTemplate.card = playerTemplate.program[j].card;
                    cardTemplate.visible = playerTemplate.program[j].visible;

                    // Loads information into player object from card template.
                    player.getProgramField(j).setCard(cardTemplate.card);
                    player.getProgramField(j).setVisible(cardTemplate.visible);
                }


                board.getPlayers().add(player);
                board.setPhase(gameTemplate.board.currentPhase);
                board.setStep(gameTemplate.board.step);

                // Checks if this player is the current player
                if (playerTemplate.currentPlayer) {
                    board.setCurrentPlayer(player);
                }
            }



            Game result = new Game(board,gameTemplate.serialNumber,gameTemplate.maxAmountOfPlayers,gameTemplate.readyToReceivePlayers);
            result.setWinner(gameTemplate.gameWinner);
        return result;
    }
}