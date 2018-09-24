package fc.flexremote;

import java.util.ArrayList;

import fc.flexremote.common.Alphabets;
import fc.flexremote.common.Control;
import fc.flexremote.common.Digits;
import fc.flexremote.common.Symbols;

public class Preload {

    private static ArrayList<String> keyActionList = new ArrayList<>();

    public static void initializeKeyActionList() {
        keyActionList.addAll(Control.getControlList());
        keyActionList.addAll(Alphabets.getAlphabetList());
        keyActionList.addAll(Digits.getNumberList());
        keyActionList.addAll(Symbols.getSymbolList());
    }

    public static ArrayList<String> getKeyActions() {
        return keyActionList;
    }
}
