package fc.flexremote.common;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

public class Control {

  public static final String ARROW_LEFT = "\u2190";
  public static final String ARROW_UP = "\u2191";
  public static final String ARROW_RIGHT = "\u2192";
  public static final String ARROW_DOWN = "\u2193";
  public static final String ESC = "Esc";
  public static final String TAB = "Tab";
  public static final String CAPS_LOCK ="CapsLock";
  public static final String ENTER = "Enter";
  public static final String BACKSPACE = "\u232B";
  public static final String SHIFT = "Shift";
  public static final String CTRL = "Ctrl";
  public static final String ALT = "Alt";
  public static final String DELETE = "Delete";
  public static final String HOME = "Home";
  public static final String END = "End";
  public static final String PRINTSCREEN = "PrintScreen";
  public static final String PAGE_UP = "PageUp";
  public static final String PAGE_DOWN = "PageDown";
  public static final String F1 = "F1";
  public static final String F2 = "F2";
  public static final String F3 = "F3";
  public static final String F4 = "F4";
  public static final String F5 = "F5";
  public static final String F6 = "F6";
  public static final String F7 = "F7";
  public static final String F8 = "F8";
  public static final String F9 = "F9";
  public static final String F10 = "F10";
  public static final String F11 = "F11";
  public static final String F12 = "F12";
  public static final String MOUSE_LEFT = "M1";
  public static final String MOUSE_MID = "M2";
  public static final String MOUSE_RIGHT = "M3";
  public static final String COPY = "Copy";
  public static final String PASTE = "Paste";
  public static final String UNDO = "Undo";
  public static final String SHOW_DESKTOP = "ShowDesktop";
  public static final String SHUT_DOWN = "Shutdown";
  public static final String RESTART = "Restart";

  public static ArrayList<String> getControlList() {
    ArrayList<String> controlList = new ArrayList<>();

    for (Field f : Control.class.getFields()) {
      if (f.getType().equals(String.class)) {
        try {
          controlList.add(String.valueOf(f.get(null)));
        } catch (IllegalAccessException e) {
          // Do nothing
        }
      }
    }

    Collections.sort(controlList);
    return controlList;
  }
}
