package dk.dtu.compute.se.pisd.roborally.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public enum Command {

    // This is a very simplistic way of realizing different commands.

    FORWARD("Move 1"),
    RIGHT("Turn Right"),
    LEFT("Turn Left"),
    FAST_FORWARD("Move 2"),
    U_TURN("U-Turn"),
    MOVE_THREE("Move 3"),
    BACK_UP("Move 1 Back"),

    // XXX Assignment P3
    OPTION_LEFT_RIGHT("Left OR Right", LEFT, RIGHT),

    //2 Special programming cards
    WEASEL_ROUTINE("Weasel", LEFT,RIGHT,U_TURN),
    SANDBOX("Sandbox",LEFT,RIGHT,FORWARD,FAST_FORWARD,U_TURN,MOVE_THREE,BACK_UP);

    final public String displayName;

    // XXX Assignment P3
    // Command(String displayName) {
    //     this.displayName = displayName;
    // }
    //
    // replaced by the code below:

    final private List<Command> options;

    Command(String displayName, Command... options) {
        this.displayName = displayName;
        this.options = Collections.unmodifiableList(Arrays.asList(options));
    }

    public boolean isInteractive() {
        return !options.isEmpty();
    }

    public List<Command> getOptions() {
        return options;
    }

}
