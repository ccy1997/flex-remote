package fc.flexremote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.preference.PreferenceManager;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import fc.flexremote.common.Message;

/**
 * This class represents the client-end on a mobile device that connects to the server-end on a PC
 *
 * @author ccy
 * @version 2019.0723
 * @since 1.0
 */
public class Client implements Runnable {
    private MainActivity mainActivity;
    private RemoteControlConfig remoteControlConfig;
    private String hostIPString;
    private Socket server;
    private final int PORT = 9090;
    private ObjectOutputStream toServer;
    private boolean run = false;

    public Client(MainActivity mainActivity, String hostIPString, RemoteControlConfig remoteControlConfig) {
        this.mainActivity = mainActivity;
        this.hostIPString = hostIPString;
        this.remoteControlConfig = remoteControlConfig;
    }

    @Override
    public void run() {
        try {
            // Reset all resources used for client-server communication
            ConnectionResource.reset();

            // Attempt to connect to the PC server
            server = new Socket();
            server.connect(new InetSocketAddress(hostIPString, PORT), 3000);
            toServer = new ObjectOutputStream(server.getOutputStream());

            // Connection successful if this line is reached
            // Allow this thread to send message to the PC server
            run = true;

            // Prepare relevant info and start RemoteControlActivity
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mainActivity);
            int touchPadSensitivity = sharedPref.getInt("set_touch_pad_sensitivity",5);
            boolean touchPadMode = sharedPref.getBoolean("set_touch_pad_mode", false);
            Intent startRemoteControlActivity = new Intent(mainActivity, RemoteControlActivity.class);
            startRemoteControlActivity.putExtra("remote_control_config", remoteControlConfig);
            startRemoteControlActivity.putExtra("touch_pad_mode", touchPadMode);
            startRemoteControlActivity.putExtra("touch_pad_sensitivity", touchPadSensitivity);
            mainActivity.startActivity(startRemoteControlActivity);

        } catch (UnknownHostException uhe) {
            showErrorMessage(mainActivity, "Invalid server IP");

        } catch (IOException ioe) {
            showErrorMessage(mainActivity,"Fail to connect to remote server\n(Have you set the server IP?)");
        }

        // Send mobile device's info to the PC server
        if (run) {
            try {
                toServer.writeObject(new Message(Message.MESSAGE_DEVICE, Build.MANUFACTURER + " " + Build.MODEL));
            } catch (IOException ioe) {
                run = false;
                showErrorMessage(mainActivity, "Disconnected from remote server");
            }
        }

        // Keep listening to the message queue for any commands to be sent to the PC server
        while (run) {
            try {
                Message message = ConnectionResource.getMessageQueue().take();
                toServer.writeObject(message);

                if (message.getMessageType() == Message.MESSAGE_DISCONNECT) {
                    run = false;
                }

            } catch (InterruptedException e) {
                // Do nothing
            } catch (IOException e) {
                run = false;
                showErrorMessage(mainActivity, "Disconnected from remote server");
            }
        }


        try {
            server.close();
        } catch (IOException e) {
            // Do nothing
        }

    }

    /**
     * Show the provided error message
     *
     * @param c The context in which the error message is displayed
     * @param message The error message to be displayed
     */
    private void showErrorMessage (final Context c, final String message) {
        mainActivity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
