package CodeParser;

import Constants.Constants;

import java.util.ArrayList;

public class CodeParser {

    public static ArrayList<Command> parseCode(String code) {
        ArrayList<Command> commands = new ArrayList<Command>();
        int i = 0; // Starting index

        while (i < code.length()) {
            i = extractCommand(code, i, commands) + 1;

            if (i == -1) {
                break;
            } // No more valid commands to extract

            i = skipWhitespace(code, i);
        }

        return commands;
    }

    public static int extractCommand(String code, int startIndex, ArrayList<Command> commands) {
        int length = code.length();
        startIndex = skipWhitespace(code, startIndex);

        if (startIndex >= length) {
            return -1; // End of string
        }

        // Check if it starts with 'for'
        if (code.startsWith("for", startIndex) && !code.startsWith("forw", startIndex)) {
            int openValBrace = code.indexOf('(', startIndex);
            int closeValBrace = code.indexOf(')', startIndex);

            int count = Integer.parseInt(code.substring(openValBrace + 1, closeValBrace).trim());

            int openBrace = code.indexOf('{', startIndex);
            int closeBrace = findMatchingBrace(code, openBrace);
            if (openBrace != -1 && closeBrace != -1) {

                commands.add(new Command(code.substring(openBrace + 1, closeBrace).trim(), count));
                return closeBrace;
            }
        }

        // x > = <,  y > = <
        if (code.startsWith("if", startIndex)) {
            int openValBrace = code.indexOf('(', startIndex);
            int closeValBrace = code.indexOf(')', startIndex);

            // statement should contain x/y >/</= float val
            String statement = code.substring(openValBrace + 1, closeValBrace).trim();

            // look for the cord
            int cordIndx = statement.indexOf(' ');

            if (cordIndx == -1) {
                return -1;
            }

            String cord = statement.substring(0, cordIndx).trim();

            // look for the sign
            statement = statement.substring(cordIndx + 1).trim();
            int signIndx = statement.indexOf(' ');
            if (signIndx == -1) {
                return -1;
            }
            String sign = statement.substring(0, signIndx).trim();

            // look for the val
            statement = statement.substring(signIndx + 1).trim();
            float value = Float.parseFloat(statement.trim());

            // find the opening and closing braces
            int openBrace = code.indexOf('{', startIndex);
            int closeBrace = findMatchingBrace(code, openBrace);
            if (openBrace != -1 && closeBrace != -1) {
                Command command = new Command(code.substring(openBrace + 1, closeBrace), Constants.IF_STATEMENT);
                command.setIf(cord, sign, value);
                commands.add(command);
                return closeBrace;
            }
        }

        // Otherwise, find the first semicolon
        int semicolonIndex = code.indexOf(';', startIndex);
        if (semicolonIndex != -1) {
            commands.add(new Command(code.substring(startIndex, semicolonIndex), Constants.SINGLE_COMMAND));
            return semicolonIndex;
        }

        return -1; // No valid command found
    }

    public static int findMatchingBrace(String code, int openIndex) {
        if (openIndex == -1 || openIndex >= code.length() || code.charAt(openIndex) != '{') {
            return -1; // Not a valid open brace
        }

        int depth = 0;
        for (int i = openIndex; i < code.length(); i++) {
            char c = code.charAt(i);
            if (c == '{') depth++;
            else if (c == '}') depth--;
            if (depth == 0) return i; // Found the matching brace
        }

        return -1; // No matching brace found
    }

    public static int skipWhitespace(String code, int index) {
        while (index < code.length() && Character.isWhitespace(code.charAt(index))) {
            index++;
        }
        return index;
    }
}
