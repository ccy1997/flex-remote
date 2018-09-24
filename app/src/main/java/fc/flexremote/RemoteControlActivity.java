package fc.flexremote;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;

import java.util.ArrayList;

import fc.flexremote.common.Control;
import fc.flexremote.common.Message;

public class RemoteControlActivity extends AppCompatActivity {
    private ArrayList<Button> keyList = new ArrayList<>();
    private ArrayList<Button> touchPadList = new ArrayList<>();
    private int touchPadSensitivity;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_control);
        Intent intent = getIntent();
        RemoteControlConfig remoteControlConfig = (RemoteControlConfig) intent.getSerializableExtra("remote_control_config");
        boolean touchPadMode = intent.getBooleanExtra("touch_pad_mode", false);
        touchPadSensitivity = intent.getIntExtra("touch_pad_sensitivity", 3);

        ConstraintLayout parentLayout = findViewById(R.id.cl_keyboard_activity);
        Utils.drawRemoteSetupFromFile(this, remoteControlConfig.getName(), parentLayout, keyList, touchPadList);
        setKeyTouchListener(keyList);
        setTouchPadTouchListener(touchPadList, touchPadMode);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setKeyTouchListener (ArrayList<Button> keyList) {
        for (Button key : keyList) {
            key.setOnTouchListener(new KeyTouchListener());
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchPadTouchListener (ArrayList<Button> touchPadList, final boolean touchPadMode) {
        for (final Button touchPad : touchPadList) {
            touchPad.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    touchPad.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int[] touchPadPositions = new int[2];
                    touchPad.getLocationOnScreen(touchPadPositions);
                    int touchPadLeftX = touchPadPositions[0];
                    int touchPadTopY = touchPadPositions[1];
                    int touchPadRightX = touchPadLeftX + touchPad.getWidth();
                    int touchPadBottomY = touchPadTopY + touchPad.getHeight();
                    Rect touchPadRect = new Rect(touchPadLeftX, touchPadTopY, touchPadRightX, touchPadBottomY);

                    if (touchPadMode)
                        touchPad.setOnTouchListener(new AbsoluteTouchListener(touchPadLeftX, touchPadTopY, touchPadRightX, touchPadBottomY, touchPadRect));
                    else
                        touchPad.setOnTouchListener(new RelativeTouchListener(touchPadLeftX, touchPadTopY, touchPadRightX, touchPadBottomY, touchPadRect));
                }

            });
        }
    }

    @Override
    public void onBackPressed() {
        try {
            ConnectionResource.getMessageQueue().put(new Message(Message.MESSAGE_DISCONNECT, null, -1));
        } catch (InterruptedException e) {
            // Do nothing
        }

        Intent startMainActivity = new Intent(this, MainActivity.class);
        startMainActivity.putExtra("intent_source", "RemoteControlActivity");
        startMainActivity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(startMainActivity);
    }

    private final class KeyTouchListener implements View.OnTouchListener {

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent motionEvent) {
            try {
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP && !v.getTag().equals("No_Action")) {
                    ConnectionResource.getMessageQueue().put( new Message(Message.MESSAGE_KEY_EVENT, v.getTag().toString(), Message.TOUCH_ACTION_KEY_UP) );
                    v.setBackgroundResource(R.drawable.key_button);
                }
                else if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN && !v.getTag().equals("No_Action")) {
                    ConnectionResource.getMessageQueue().put( new Message(Message.MESSAGE_KEY_EVENT, v.getTag().toString(), Message.TOUCH_ACTION_KEY_DOWN) );
                    v.setBackgroundResource(R.drawable.key_button_down);
                }
            } catch (InterruptedException e) {
                // Do nothing
            }

            return true;
        }

    }

    private class RelativeTouchListener implements View.OnTouchListener {
        int touchPadLeftX, touchPadTopY, touchPadRightX, touchPadBottomY, actionMoveCount = 0;
        Rect touchPadRect;
        int lastFingerX, lastFingerY, dX, dY;

        RelativeTouchListener (int touchPadLeftX, int touchPadTopY, int touchPadRightX, int touchPadBottomY, Rect touchPadRect) {
            this.touchPadLeftX = touchPadLeftX;
            this.touchPadTopY = touchPadTopY;
            this.touchPadRightX = touchPadRightX;
            this.touchPadBottomY = touchPadBottomY;
            this.touchPadRect = touchPadRect;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getActionMasked()) {
                case (MotionEvent.ACTION_DOWN):
                    lastFingerX = (int) event.getRawX();
                    lastFingerY = (int) event.getRawY();
                    break;

                case (MotionEvent.ACTION_MOVE):
                    if (touchPadRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                        if (lastFingerX == -1 || lastFingerY == -1) {
                            lastFingerX = (int) event.getRawX();
                            lastFingerY = (int) event.getRawY();
                        }
                        else {
                            dX = (int) ( ((int) event.getRawX() - lastFingerX) * (touchPadSensitivity / 3f) );
                            dY = (int) ( ((int) event.getRawY() - lastFingerY) * (touchPadSensitivity / 3f) );

                            try {
                                ConnectionResource.getMessageQueue().put( new Message(Message.MESSAGE_RELATIVE_TOUCHPAD_EVENT, dX, dY) );
                            } catch (InterruptedException e) {
                                // Do nothing
                            }

                            lastFingerX = (int) event.getRawX();
                            lastFingerY = (int) event.getRawY();
                            actionMoveCount++;
                        }
                    }
                    else {
                        lastFingerX = -1;
                        lastFingerY = -1;
                    }
                    break;

                case (MotionEvent.ACTION_UP):
                    if (actionMoveCount < 3) {
                        try {
                            ConnectionResource.getMessageQueue().put( new Message(Message.MESSAGE_KEY_EVENT, Control.MOUSE_LEFT, Message.TOUCH_ACTION_KEY_DOWN) );
                            ConnectionResource.getMessageQueue().put( new Message(Message.MESSAGE_KEY_EVENT, Control.MOUSE_LEFT, Message.TOUCH_ACTION_KEY_UP) );
                        } catch (InterruptedException e) {
                            // Do nothing
                        }
                    }

                    actionMoveCount = 0;
                    break;

                default:
                    break;
            }

            return true;
        }

    }

    private class AbsoluteTouchListener implements View.OnTouchListener {
        int touchPadLeftX, touchPadTopY, touchPadRightX, touchPadBottomY, actionMoveCount = 0;
        float xNormalized, yNormalized;
        Rect touchPadRect;

        AbsoluteTouchListener (int touchPadLeftX, int touchPadTopY, int touchPadRightX, int touchPadBottomY, Rect touchPadRect) {
            this.touchPadLeftX = touchPadLeftX;
            this.touchPadTopY = touchPadTopY;
            this.touchPadRightX = touchPadRightX;
            this.touchPadBottomY = touchPadBottomY;
            this.touchPadRect = touchPadRect;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getActionMasked()) {
                case (MotionEvent.ACTION_DOWN):
                    xNormalized = event.getX() / v.getWidth();
                    yNormalized = event.getY() / v.getHeight();

                    if (touchPadRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                        try {
                            ConnectionResource.getMessageQueue().put( new Message(Message.MESSAGE_ABSOLUTE_TOUCHPAD_EVENT, xNormalized, yNormalized) );
                        } catch (InterruptedException e) {
                            // Do nothing
                        }
                    }
                    break;

                case (MotionEvent.ACTION_MOVE):
                    xNormalized = event.getX() / v.getWidth();
                    yNormalized = event.getY() / v.getHeight();

                    if (touchPadRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                        try {
                            ConnectionResource.getMessageQueue().put( new Message(Message.MESSAGE_ABSOLUTE_TOUCHPAD_EVENT, xNormalized, yNormalized) );
                        } catch (InterruptedException e) {
                            // Do nothing
                        }
                    }

                    actionMoveCount++;
                    break;

                case (MotionEvent.ACTION_UP):
                    if (actionMoveCount < 3) {
                        try {
                            ConnectionResource.getMessageQueue().put( new Message(Message.MESSAGE_KEY_EVENT, Control.MOUSE_LEFT, Message.TOUCH_ACTION_KEY_DOWN) );
                            ConnectionResource.getMessageQueue().put( new Message(Message.MESSAGE_KEY_EVENT, Control.MOUSE_LEFT, Message.TOUCH_ACTION_KEY_UP) );
                        } catch (InterruptedException e) {
                            // Do nothing
                        }
                    }

                    actionMoveCount = 0;
                    break;

                default:
                    break;
            }

            return true;
        }

    }

}
