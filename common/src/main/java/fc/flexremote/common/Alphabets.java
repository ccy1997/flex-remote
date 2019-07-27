package fc.flexremote.common;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class contains a set of alphabets
 *
 * @author ccy
 * @version 2019.0723
 * @since 1.0
 */
public class Alphabets {
    public static final String SMALL_A = "a";
    public static final String SMALL_B = "b";
    public static final String SMALL_C = "c";
    public static final String SMALL_D = "d";
    public static final String SMALL_E = "e";
    public static final String SMALL_F = "f";
    public static final String SMALL_G = "g";
    public static final String SMALL_H = "h";
    public static final String SMALL_I = "i";
    public static final String SMALL_J = "j";
    public static final String SMALL_K = "k";
    public static final String SMALL_L = "l";
    public static final String SMALL_M = "m";
    public static final String SMALL_N = "n";
    public static final String SMALL_O = "o";
    public static final String SMALL_P = "p";
    public static final String SMALL_Q = "q";
    public static final String SMALL_R = "r";
    public static final String SMALL_S = "s";
    public static final String SMALL_T = "t";
    public static final String SMALL_U = "u";
    public static final String SMALL_V = "v";
    public static final String SMALL_W = "w";
    public static final String SMALL_X = "x";
    public static final String SMALL_Y = "y";
    public static final String SMALL_Z = "z";
    public static final String BIG_A = "A";
    public static final String BIG_B = "B";
    public static final String BIG_C = "C";
    public static final String BIG_D = "D";
    public static final String BIG_E = "E";
    public static final String BIG_F = "F";
    public static final String BIG_G = "G";
    public static final String BIG_H = "H";
    public static final String BIG_I = "I";
    public static final String BIG_J = "J";
    public static final String BIG_K = "K";
    public static final String BIG_L = "L";
    public static final String BIG_M = "M";
    public static final String BIG_N = "N";
    public static final String BIG_O = "O";
    public static final String BIG_P = "P";
    public static final String BIG_Q = "Q";
    public static final String BIG_R = "R";
    public static final String BIG_S = "S";
    public static final String BIG_T = "T";
    public static final String BIG_U = "U";
    public static final String BIG_V = "V";
    public static final String BIG_W = "W";
    public static final String BIG_X = "X";
    public static final String BIG_Y = "Y";
    public static final String BIG_Z = "Z";

    public static ArrayList<String> getAlphabetList() {
        ArrayList<String> alphabetList = new ArrayList<>();

        for (Field f : Alphabets.class.getFields()) {
            if (f.getType().equals(String.class)) {
                try {
                    alphabetList.add(String.valueOf(f.get(null)));
                } catch (IllegalAccessException e) {
                    // Do nothing
                }
            }
        }

        Collections.sort(alphabetList);
        return alphabetList;
    }
}
