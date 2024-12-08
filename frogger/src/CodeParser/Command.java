package CodeParser;

import Constants.Constants;

import Frog.*;
import javax.management.StringValueExp;
import java.util.ArrayList;
import java.util.Comparator;

public class Command {
    private String command;
    private int count;
    private ArrayList<Command> commands;
    private ArrayList<Command> backup;
    private int currentCommand;

    private String ifCord;  // can be either x or y
    private String ifSign;  // can be either =, <, >
    private float ifVal;    // any float val

    Command(String command, int count) {
        this.command = command;
        this.count = count;
        this.currentCommand = 0;
        if(count != Constants.SINGLE_COMMAND) {
            this.commands = CodeParser.parseCode(command);
        }
        else {
            this.commands = null;
        }

        this.backup = this.createBackup();
    }

    Command(String command, int count, String ifCord, String ifSign, float ifVal) {
        this.command = command;
        this.count = count;
        this.ifCord = ifCord;
        this.ifSign = ifSign;
        this.ifVal = ifVal;
        this.currentCommand = 0;
        if(count != Constants.SINGLE_COMMAND) {
            this.commands = CodeParser.parseCode(command);
        }
        else {
            this.commands = null;
        }

        this.backup = this.createBackup();
    }

    public String getCommand() {
        return command;
    }

    public String execute(Frog frog) {
        // loop ended
        if(this.count == 0) {
            return null;
        }

        if(this.count == Constants.IGNORE_IFFALSE) {
            return null;
        }

        if(this.count != Constants.IGNORE_IFTRUE && this.count == Constants.IF_STATEMENT) {
            if(!this.evalIf(frog)) {
                return null;
            }
            this.count = Constants.IGNORE_IFTRUE;
        }

        // single command was already fetched
        if(this.count == Constants.NO_MORE_COMMANDS) {
            return null;
        }

        // if single command return it and set it to no more commands
        if(this.count == Constants.SINGLE_COMMAND) {
            this.count = Constants.NO_MORE_COMMANDS;
            return this.command;
        }

        String c = this.commands.get(this.currentCommand).execute(frog);;
        while(c == null) {
            ++this.currentCommand;

            if(this.currentCommand >= this.commands.size()) {
                this.currentCommand = 0;
                this.commands = this.getBackup();
                --this.count;
            }

            if(this.count == 0 || this.count == Constants.NO_MORE_COMMANDS) {
                return null;
            }

            // better safe than sorry
            assert (this.commands != null);

            c = this.commands.get(this.currentCommand).execute(frog);
        }

        if(this.currentCommand >= this.commands.size()) {
            this.currentCommand = 0;
            this.commands = this.getBackup();
            --this.count;
        }

        return c;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }



    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(" ( ").append(this.count).append(" )");
        if(commands != null) {
            for (Command command : commands) {
                builder.append(" + ").append(command.toString()).append(" + ");
            }
        }
        else {
            builder.append(this.command);
        }
        return  builder.toString();
    }

    // create a deep copy for backup
    private ArrayList<Command> createBackup() {
        ArrayList<Command> backup = new ArrayList<>();
        if(commands == null) {
            return null;
        }

        for(Command command : this.commands) {
            backup.add(new Command(command.command, command.count, command.ifCord, command.ifSign, command.ifVal));
        }

        return backup;
    }

    private ArrayList<Command> getBackup() {
        if(this.backup == null) {
            return null;
        }

        ArrayList<Command> newBackup = new ArrayList<>();
        for(Command command : this.backup) {

            newBackup.add(new Command(command.command, command.count, command.ifCord, command.ifSign, command.ifVal));
        }

        return newBackup;
    }


    public void setIf(String cord, String sign, float val) {
        this.ifCord = cord;
        this.ifSign = sign;
        this.ifVal = val;
    }

    private boolean evalIf(Frog frog) {
        if(frog == null || this.ifCord == null || this.ifSign == null) {
            return false;
        }

        System.out.println(this.ifCord + " " + this.ifSign + " " + this.ifVal);

        float valFrog = 0;
        switch (this.ifCord) {
            case "x":
                valFrog = frog.getX();
                break;
            case "y":
                valFrog = frog.getY();
                break;
            case "r" :
                valFrog = (float)frog.getAngleDeg();
            default:
                return false;
        }

        return switch (this.ifSign) {
            case ">" -> valFrog > this.ifVal;
            case "<" -> valFrog < this.ifVal;
            case "=" -> valFrog == this.ifVal;
            default -> false;
        };
    }
}
