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
            ConnectionResource.reset();
            server = new Socket();
            server.connect(new InetSocketAddress(hostIPString, PORT), 3000);
            toServer = new ObjectOutputStream(server.getOutputStream());
            run = true;
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

        if (run) {
            try {
                toServer.writeObject(new Message(Message.MESSAGE_DEVICE, Build.MANUFACTURER + " " + Build.MODEL));
            } catch (IOException ioe) {
                run = false;
                showErrorMessage(mainActivity, "Disconnected from remote server");
            }
        }

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

    private void showErrorMessage (final Context c, final String message) {
        mainActivity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
