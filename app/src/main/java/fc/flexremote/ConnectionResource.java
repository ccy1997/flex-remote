package fc.flexremote;

import java.util.concurrent.LinkedBlockingQueue;
import fc.flexremote.common.Message;

/**
 * This class contains resources used during connection between client and server
 *
 * @author ccy
 * @version 2019.0723
 * @since 1.0
 */
public class ConnectionResource {
    private static LinkedBlockingQueue<Message> MessageQueue = new LinkedBlockingQueue<>();

    public static LinkedBlockingQueue<Message> getMessageQueue() {
        return MessageQueue;
    }

    public static void reset() {
        MessageQueue = new LinkedBlockingQueue<>();
    }
}
