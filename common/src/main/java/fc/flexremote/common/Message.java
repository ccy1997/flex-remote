package fc.flexremote.common;

import java.io.Serializable;

/**
 * This class represents a message being sent between client and server
 *
 * @author ccy
 * @version 2019.0723
 * @since 1.0
 */
public class Message implements Serializable {
    public static final int MESSAGE_DISCONNECT = 0;
    public static final int MESSAGE_KEY_EVENT = 1;
    public static final int MESSAGE_ABSOLUTE_TOUCHPAD_EVENT = 2;
    public static final int MESSAGE_RELATIVE_TOUCHPAD_EVENT = 3;
    public static final int MESSAGE_DEVICE = 4;

    public static final int TOUCH_ACTION_KEY_UP = 0;
    public static final int TOUCH_ACTION_KEY_DOWN = 1;

    private int messageType;
    private String keyAction;
    private int touchEvent;
    private float xNormalized;
    private float yNormalized;
    private int dX;
    private int dY;
    private String device;

    public Message(int messageType, String keyAction, int touchEvent) {
        this.messageType = messageType;
        this.keyAction = keyAction;
        this.touchEvent = touchEvent;
    }

    public Message(int messageType, float xNormalized, float yNormalized) {
        this.messageType = messageType;
        this.xNormalized = xNormalized;
        this.yNormalized = yNormalized;
    }

    public Message(int messageType, int dX, int dY) {
        this.messageType = messageType;
        this.dX = dX;
        this.dY = dY;
    }

    public Message(int messageType, String device) {
        this.messageType = messageType;
        this.device = device;
    }

    public int getMessageType() {
        return messageType;
    }

    public String getKeyAction() {
        return keyAction;
    }

    public int getTouchEvent() {
        return touchEvent;
    }

    public float getXNormalized() {
        return xNormalized;
    }

    public float getYNormalized() {
        return yNormalized;
    }

    public int getdX() {
        return dX;
    }

    public int getdY() {
        return dY;
    }

    public String getDevice() {
        return device;
    }

}