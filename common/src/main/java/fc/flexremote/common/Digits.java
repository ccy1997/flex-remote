package fc.flexremote.common;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

public class Digits {
    public static final String ZERO = "0";
    public static final String ONE = "1";
    public static final String TWO = "2";
    public static final String THREE = "3";
    public static final String FOUR = "4";
    public static final String FIVE = "5";
    public static final String SIX = "6";
    public static final String SEVEN = "7";
    public static final String EIGHT = "8";
    public static final String NINE = "9";

    public static ArrayList<String> getNumberList() {
        ArrayList<String> numberList = new ArrayList<>();

        for (Field f : Digits.class.getFields()) {
            if (f.getType().equals(String.class)) {
                try {
                    numberList.add(String.valueOf(f.get(null)));
                } catch (IllegalAccessException e) {
                    // Do nothing
                }
            }
        }

        Collections.sort(numberList);
        return numberList;
    }
}
