package fc.flexremote;

import java.util.concurrent.LinkedBlockingQueue;
import fc.flexremote.common.Message;

public class ConnectionResource {
    private static LinkedBlockingQueue<Message> MessageQueue = new LinkedBlockingQueue<>();

    public static LinkedBlockingQueue<Message> getMessageQueue() {
        return MessageQueue;
    }

    public static void reset() {
        MessageQueue = new LinkedBlockingQueue<>();
    }
}
