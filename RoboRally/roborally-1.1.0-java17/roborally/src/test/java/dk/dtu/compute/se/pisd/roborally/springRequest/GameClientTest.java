package dk.dtu.compute.se.pisd.roborally.springRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GameClientTest {

    public String expected = "added";
    public static String serialNumber = "444";
    public String jsonfile = "{\n" +
            "  \"serialNumber\": \"325722\",\n" +
            "  \"maxAmountOfPlayers\": 2,\n" +
            "  \"readyToReceivePlayers\": true,\n" +
            "  \"board\": {\n" +
            "    \"width\": 13,\n" +
            "    \"height\": 10,\n" +
            "    \"step\": 0,\n" +
            "    \"currentPhase\": \"INITIALISATION\",\n" +
            "    \"spaces\": [\n" +
            "      {\n" +
            "        \"walls\": [\n" +
            "          \"NORTH\"\n" +
            "        ],\n" +
            "        \"x\": 1,\n" +
            "        \"y\": 2\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [\n" +
            "          \"SOUTH\"\n" +
            "        ],\n" +
            "        \"x\": 1,\n" +
            "        \"y\": 7\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"EAST\",\n" +
            "          \"isDouble\": false\n" +
            "        },\n" +
            "        \"x\": 2,\n" +
            "        \"y\": 0\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [\n" +
            "          \"EAST\"\n" +
            "        ],\n" +
            "        \"x\": 2,\n" +
            "        \"y\": 4\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [\n" +
            "          \"EAST\"\n" +
            "        ],\n" +
            "        \"x\": 2,\n" +
            "        \"y\": 5\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"EAST\",\n" +
            "          \"isDouble\": false\n" +
            "        },\n" +
            "        \"x\": 2,\n" +
            "        \"y\": 9\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"EAST\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 3,\n" +
            "        \"y\": 7\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"EAST\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 3,\n" +
            "        \"y\": 8\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"SOUTH\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 4,\n" +
            "        \"y\": 0\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"SOUTH\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 4,\n" +
            "        \"y\": 1\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"SOUTH\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 4,\n" +
            "        \"y\": 2\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"SOUTH\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 4,\n" +
            "        \"y\": 3\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"SOUTH\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 4,\n" +
            "        \"y\": 4\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"SOUTH\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 4,\n" +
            "        \"y\": 5\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"SOUTH\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 4,\n" +
            "        \"y\": 6\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"SOUTH\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 4,\n" +
            "        \"y\": 7\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"EAST\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 4,\n" +
            "        \"y\": 8\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"SOUTH\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 5,\n" +
            "        \"y\": 0\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"WEST\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 5,\n" +
            "        \"y\": 1\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"EAST\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 5,\n" +
            "        \"y\": 8\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"WEST\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 6,\n" +
            "        \"y\": 1\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [\n" +
            "          \"NORTH\"\n" +
            "        ],\n" +
            "        \"x\": 6,\n" +
            "        \"y\": 3\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [\n" +
            "          \"SOUTH\"\n" +
            "        ],\n" +
            "        \"x\": 6,\n" +
            "        \"y\": 4\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [\n" +
            "          \"WEST\"\n" +
            "        ],\n" +
            "        \"x\": 6,\n" +
            "        \"y\": 6\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"EAST\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 6,\n" +
            "        \"y\": 8\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"WEST\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 7,\n" +
            "        \"y\": 1\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [\n" +
            "          \"EAST\"\n" +
            "        ],\n" +
            "        \"x\": 7,\n" +
            "        \"y\": 6\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"EAST\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 7,\n" +
            "        \"y\": 8\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"WEST\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 8,\n" +
            "        \"y\": 1\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [\n" +
            "          \"WEST\"\n" +
            "        ],\n" +
            "        \"x\": 8,\n" +
            "        \"y\": 3\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"EAST\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 8,\n" +
            "        \"y\": 8\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"WEST\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 9,\n" +
            "        \"y\": 1\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [\n" +
            "          \"EAST\"\n" +
            "        ],\n" +
            "        \"x\": 9,\n" +
            "        \"y\": 3\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [\n" +
            "          \"NORTH\"\n" +
            "        ],\n" +
            "        \"x\": 9,\n" +
            "        \"y\": 5\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [\n" +
            "          \"SOUTH\"\n" +
            "        ],\n" +
            "        \"x\": 9,\n" +
            "        \"y\": 6\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"EAST\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 9,\n" +
            "        \"y\": 8\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"WEST\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 10,\n" +
            "        \"y\": 1\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"EAST\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 10,\n" +
            "        \"y\": 8\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"NORTH\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 10,\n" +
            "        \"y\": 9\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"WEST\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 11,\n" +
            "        \"y\": 1\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"NORTH\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 11,\n" +
            "        \"y\": 2\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"NORTH\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 11,\n" +
            "        \"y\": 3\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"NORTH\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 11,\n" +
            "        \"y\": 4\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"NORTH\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 11,\n" +
            "        \"y\": 5\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"NORTH\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 11,\n" +
            "        \"y\": 6\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"NORTH\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 11,\n" +
            "        \"y\": 7\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"NORTH\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 11,\n" +
            "        \"y\": 8\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"NORTH\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 11,\n" +
            "        \"y\": 9\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"WEST\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 12,\n" +
            "        \"y\": 1\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"conveyorBelt\": {\n" +
            "          \"heading\": \"WEST\",\n" +
            "          \"isDouble\": true\n" +
            "        },\n" +
            "        \"x\": 12,\n" +
            "        \"y\": 2\n" +
            "      },\n" +
            "      {\n" +
            "        \"walls\": [],\n" +
            "        \"checkPoint\": {\n" +
            "          \"number\": 1\n" +
            "        },\n" +
            "        \"x\": 12,\n" +
            "        \"y\": 3\n" +
            "      }\n" +
            "    ],\n" +
            "    \"players\": [\n" +
            "      {\n" +
            "        \"name\": \"battenmanden\",\n" +
            "        \"color\": \"red\",\n" +
            "        \"x\": 0,\n" +
            "        \"y\": 3,\n" +
            "        \"heading\": \"SOUTH\",\n" +
            "        \"checkPoint\": 0,\n" +
            "        \"currentPlayer\": true,\n" +
            "        \"program\": [\n" +
            "          {\n" +
            "            \"visible\": true\n" +
            "          },\n" +
            "          {\n" +
            "            \"visible\": true\n" +
            "          },\n" +
            "          {\n" +
            "            \"visible\": true\n" +
            "          },\n" +
            "          {\n" +
            "            \"visible\": true\n" +
            "          },\n" +
            "          {\n" +
            "            \"visible\": true\n" +
            "          }\n" +
            "        ],\n" +
            "        \"cards\": [\n" +
            "          {\n" +
            "            \"visible\": true\n" +
            "          },\n" +
            "          {\n" +
            "            \"visible\": true\n" +
            "          },\n" +
            "          {\n" +
            "            \"visible\": true\n" +
            "          },\n" +
            "          {\n" +
            "            \"visible\": true\n" +
            "          },\n" +
            "          {\n" +
            "            \"visible\": true\n" +
            "          },\n" +
            "          {\n" +
            "            \"visible\": true\n" +
            "          },\n" +
            "          {\n" +
            "            \"visible\": true\n" +
            "          },\n" +
            "          {\n" +
            "            \"visible\": true\n" +
            "          },\n" +
            "          {\n" +
            "            \"visible\": true\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      {\n" +
            "        \"color\": \"green\",\n" +
            "        \"x\": 1,\n" +
            "        \"y\": 4,\n" +
            "        \"heading\": \"SOUTH\",\n" +
            "        \"checkPoint\": 0,\n" +
            "        \"currentPlayer\": false,\n" +
            "        \"program\": [\n" +
            "          {\n" +
            "            \"visible\": true\n" +
            "          },\n" +
            "          {\n" +
            "            \"visible\": true\n" +
            "          },\n" +
            "          {\n" +
            "            \"visible\": true\n" +
            "          },\n" +
            "          {\n" +
            "            \"visible\": true\n" +
            "          },\n" +
            "          {\n" +
            "            \"visible\": true\n" +
            "          }\n" +
            "        ],\n" +
            "        \"cards\": [\n" +
            "          {\n" +
            "            \"visible\": true\n" +
            "          },\n" +
            "          {\n" +
            "            \"visible\": true\n" +
            "          },\n" +
            "          {\n" +
            "            \"visible\": true\n" +
            "          },\n" +
            "          {\n" +
            "            \"visible\": true\n" +
            "          },\n" +
            "          {\n" +
            "            \"visible\": true\n" +
            "          },\n" +
            "          {\n" +
            "            \"visible\": true\n" +
            "          },\n" +
            "          {\n" +
            "            \"visible\": true\n" +
            "          },\n" +
            "          {\n" +
            "            \"visible\": true\n" +
            "          },\n" +
            "          {\n" +
            "            \"visible\": true\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}";

/**
 * This test checks if it's possible to upload a game to a server.
 */

@Test

    void someTest(){
    String result = null;
    try {
        result = GameClient.putGame(serialNumber, jsonfile);
    } catch (Exception e) {
        e.printStackTrace();
    }

    Assertions.assertEquals(result, expected, "Result: " + result + ". " + "Expected: " + expected + ". ");

    }
}

