package fc.flexremote.common;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

public class Symbols {
    public static final String SPACE = "Space";
    public static final String EXCLAMATION = "!";
    public static final String DOUBLE_QUOTE = "\"";
    public static final String SHARP = "#";
    public static final String DOLLAR = "$";
    public static final String PERCENT = "%";
    public static final String AMPERSAND = "&";
    public static final String SINGLE_QUOTE = "'";
    public static final String OPEN_PARENTHESIS = "(";
    public static final String CLOSE_PARENTHESIS = ")";
    public static final String ASTERISK = "*";
    public static final String PLUS = "+";
    public static final String COMMA = ",";
    public static final String HYPHEN = "-";
    public static final String FULL_STOP = ".";
    public static final String FORWARD_SLASH = "/";
    public static final String COLON = ":";
    public static final String SEMICOLON = ";";
    public static final String LESS_THAN = "<";
    public static final String EQUAL = "=";
    public static final String GREATER_THAN = ">";
    public static final String QUESTION_MARK = "?";
    public static final String AT = "@";
    public static final String OPEN_BRACKET = "[";
    public static final String BACKSLASH = "\\";
    public static final String CLOSE_BRACKET = "]";
    public static final String CIRCUMFLEX = "^";
    public static final String UNDERSCORE = "_";
    public static final String BACK_QUOTE = "`";
    public static final String OPEN_BRACE = "{";
    public static final String PIPE = "|";
    public static final String CLOSE_BRACE = "}";
    public static final String TILDE = "~";

    public static ArrayList<String> getSymbolList() {
        ArrayList<String> symbolList = new ArrayList<>();

        for (Field f : Symbols.class.getFields()) {
            if (f.getType().equals(String.class)) {
                try {
                    symbolList.add(String.valueOf(f.get(null)));
                } catch (IllegalAccessException e) {
                    // Do nothing
                }
            }
        }

        Collections.sort(symbolList);
        return symbolList;
    }
}
