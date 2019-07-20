package fc.flexremote;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

public class ConfigureRemoteControlActivity extends AppCompatActivity {
    private final int RESIZE_BOTTOM_RIGHT = 0;
    private final int RESIZE_TOP_RIGHT = 1;
    private final int RESIZE_BOTTOM_LEFT = 2;
    private final int RESIZE_TOP_LEFT = 3;

    private Activity activity = this;
    private ArrayList<Button> keyList = new ArrayList<>();
    private ArrayList<Button> touchPadList = new ArrayList<>();
    private ArrayList<Button> resizeButtonList = new ArrayList<>();
    private ConstraintLayout parentLayout;
    private Button resizeOnOff;
    private Button addKey;
    private Button addTouchPad;
    private ImageView done;
    private ImageView bin;
    private int elevationCount = 0;
    private boolean isResizeButtonVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_remote_control);
        setEditorOptionButtonsInitialAttributes();
        setupEditorByPurposeAndOrientation();
    }

    @Override
    public void onBackPressed() {
        showSaveDialog();
    }

    private void setEditorOptionButtonsInitialAttributes() {
        parentLayout = findViewById(R.id.cl_configure_keyboard_activity);
        resizeOnOff = findViewById(R.id.resize_on_off_button);
        addKey = findViewById(R.id.add_key);
        addTouchPad = findViewById(R.id.add_touch_pad);
        done = findViewById(R.id.done);
        bin = findViewById(R.id.delete_view);
        resizeOnOff.setStateListAnimator(null);
        addKey.setStateListAnimator(null);
        addTouchPad.setStateListAnimator(null);
        done.setStateListAnimator(null);
        bin.setStateListAnimator(null);
        bin.setVisibility(View.GONE);
        resizeOnOff.setOnClickListener(new ResizeOnOffButtonListener(resizeButtonList));
        addKey.setOnClickListener(new CreateButtonClickListener(keyList, Parameters.BUTTON_TYPE_KEY));
        addTouchPad.setOnClickListener(new CreateButtonClickListener(touchPadList, Parameters.BUTTON_TYPE_TOUCHPAD));
    }

    private void setupEditorByPurposeAndOrientation() {
        Intent intent = getIntent();
        String purpose = intent.getStringExtra("purpose");
        String orientation = intent.getStringExtra("orientation");

        if (purpose.equals("create")) {
            done.setOnClickListener(new DoneCreateClickListener());

            if (orientation.equals("portrait"))
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            else if (orientation.equals("landscape"))
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        }
        else if (purpose.equals("edit")) {
            String remoteName = intent.getStringExtra("remote_name");
            done.setOnClickListener(new DoneEditClickListener());
            Utils.drawRemoteSetupFromFile(this, remoteName, parentLayout, keyList, touchPadList);

            for (Button key : keyList) {
                addRepositionAndResizeFunctionForButton(key, keyList, Parameters.BUTTON_TYPE_KEY, elevationCount);
                elevationCount++;
            }

            for (Button touchPad : touchPadList) {
                addRepositionAndResizeFunctionForButton(touchPad, touchPadList, Parameters.BUTTON_TYPE_TOUCHPAD, elevationCount);
                elevationCount++;
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addRepositionAndResizeFunctionForButton(final Button mainButton, final ArrayList<Button> mainButtonList,
                                                         final int buttonType, final int elevationCount) {
        mainButton.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                mainButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int[] mainButtonPos = new int[2];
                mainButton.getLocationOnScreen(mainButtonPos);
                int mainButtonLeft = mainButtonPos[0];
                int mainButtonTop = mainButtonPos[1];
                int mainButtonRight = mainButtonPos[0] + mainButton.getWidth();
                int mainButtonBottom = mainButtonPos[1] + mainButton.getHeight();

                ArrayList<Button> resizeButtons = prepareResizeButtons(mainButtonLeft, mainButtonTop, mainButtonRight, mainButtonBottom);

                Button resizeTopLeft = resizeButtons.get(0);
                Button resizeTopRight = resizeButtons.get(1);
                Button resizeBottomRight = resizeButtons.get(2);
                Button resizeBottomLeft = resizeButtons.get(3);

                mainButton.setElevation(elevationCount);
                resizeTopLeft.setElevation(elevationCount);
                resizeTopRight.setElevation(elevationCount);
                resizeBottomRight.setElevation(elevationCount);
                resizeBottomLeft.setElevation(elevationCount);

                mainButton.setOnTouchListener(new MainButtonTouchListener(mainButtonList, buttonType, resizeTopLeft, resizeTopRight, resizeBottomRight, resizeBottomLeft));
                setResizeButtonsListener(mainButton, resizeTopLeft, resizeTopRight, resizeBottomRight, resizeBottomLeft);

                parentLayout.addView(resizeTopLeft);
                parentLayout.addView(resizeTopRight);
                parentLayout.addView(resizeBottomRight);
                parentLayout.addView(resizeBottomLeft);

                resizeButtonList.add(resizeTopLeft);
                resizeButtonList.add(resizeTopRight);
                resizeButtonList.add(resizeBottomRight);
                resizeButtonList.add(resizeBottomLeft);
            }

        });
    }

    private ArrayList<Button> prepareResizeButtons(float mainButtonLeft, float mainButtonTop, float mainButtonRight, float mainButtonBottom) {
        ArrayList<Button> resizeButtons = new ArrayList<>();

        Button topLeftResize = new Button(this);
        topLeftResize.setX(mainButtonLeft - Utils.dpToPx(Parameters.RESIZE_BOX_WIDTH_DP / 2, this));
        topLeftResize.setY(mainButtonTop - Utils.dpToPx(Parameters.RESIZE_BOX_HEIGHT_DP / 2, this));
        setResizeButtonCommonAttributes(topLeftResize);

        Button topRightResize = new Button(this);
        topRightResize.setX(mainButtonRight - Utils.dpToPx(Parameters.RESIZE_BOX_WIDTH_DP / 2, this));
        topRightResize.setY(mainButtonTop - Utils.dpToPx(Parameters.RESIZE_BOX_HEIGHT_DP / 2, this));
        setResizeButtonCommonAttributes(topRightResize);

        Button bottomRightResize = new Button(this);
        bottomRightResize.setX(mainButtonRight - Utils.dpToPx(Parameters.RESIZE_BOX_WIDTH_DP / 2, this));
        bottomRightResize.setY(mainButtonBottom - Utils.dpToPx(Parameters.RESIZE_BOX_HEIGHT_DP / 2, this));
        setResizeButtonCommonAttributes(bottomRightResize);

        Button bottomLeftResize = new Button(activity);
        bottomLeftResize.setX(mainButtonLeft - Utils.dpToPx(Parameters.RESIZE_BOX_WIDTH_DP / 2, this));
        bottomLeftResize.setY(mainButtonBottom - Utils.dpToPx(Parameters.RESIZE_BOX_HEIGHT_DP / 2, this));
        setResizeButtonCommonAttributes(bottomLeftResize);

        resizeButtons.add(topLeftResize);
        resizeButtons.add(topRightResize);
        resizeButtons.add(bottomRightResize);
        resizeButtons.add(bottomLeftResize);

        return resizeButtons;
    }

    private void setResizeButtonCommonAttributes(Button resizeButton) {
        resizeButton.setLayoutParams(
                new ConstraintLayout.LayoutParams(Utils.dpToPx(Parameters.RESIZE_BOX_WIDTH_DP, this), Utils.dpToPx(Parameters.RESIZE_BOX_HEIGHT_DP, this)));
        resizeButton.setStateListAnimator(null);
        resizeButton.setBackgroundResource(R.drawable.resize_button);
        if (isResizeButtonVisible)
            resizeButton.setVisibility(View.VISIBLE);
        else
            resizeButton.setVisibility(View.GONE);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setResizeButtonsListener(Button mainButton, Button resizeTopLeft, Button resizeTopRight, Button resizeBottomRight, Button resizeBottomLeft) {
        resizeTopLeft.setOnTouchListener(new ResizeButtonTouchListener(RESIZE_TOP_LEFT, mainButton, resizeBottomLeft, resizeTopRight));
        resizeTopRight.setOnTouchListener(new ResizeButtonTouchListener(RESIZE_TOP_RIGHT, mainButton, resizeBottomRight, resizeTopLeft));
        resizeBottomRight.setOnTouchListener(new ResizeButtonTouchListener(RESIZE_BOTTOM_RIGHT, mainButton, resizeTopRight, resizeBottomLeft));
        resizeBottomLeft.setOnTouchListener(new ResizeButtonTouchListener(RESIZE_BOTTOM_LEFT, mainButton, resizeTopLeft, resizeBottomRight));
    }

    private void setOptionsVisibilityOnMainButtonRepositionStart() {
        resizeOnOff.setVisibility(View.GONE);
        addKey.setVisibility(View.GONE);
        addTouchPad.setVisibility(View.GONE);
        done.setVisibility(View.GONE);
        bin.setVisibility(View.VISIBLE);
    }

    private void setOptionsVisibilityOnMainButtonRepositionEnd() {
        resizeOnOff.setVisibility(View.VISIBLE);
        addKey.setVisibility(View.VISIBLE);
        addTouchPad.setVisibility(View.VISIBLE);
        done.setVisibility(View.VISIBLE);
        bin.setVisibility(View.GONE);
        bin.setBackgroundResource(R.drawable.ic_delete_forever_black_50dp);
    }

    private void removeMainButtonAndAssociatedResizeButtons(Button mainButton, Button topLeftResize, Button topRightResize,
                                                            Button bottomRightResize, Button bottomLeftResize) {
        parentLayout.removeView(mainButton);
        parentLayout.removeView(topLeftResize);
        parentLayout.removeView(topRightResize);
        parentLayout.removeView(bottomRightResize);
        parentLayout.removeView(bottomLeftResize);
    }

    private float calculateResizeButtonXWhenMainButtonMin(int resizeButtonType, float resizeButtonLeft, int mainButtonWidth) {
        if (resizeButtonType == RESIZE_TOP_LEFT || resizeButtonType == RESIZE_BOTTOM_LEFT)
            return resizeButtonLeft + (mainButtonWidth - Utils.dpToPx(Parameters.BUTTON_MIN_WIDTH_DP, activity));
        else
            return resizeButtonLeft - (mainButtonWidth - Utils.dpToPx(Parameters.BUTTON_MIN_WIDTH_DP, activity));
    }

    private float calculateResizeButtonYWhenMainButtonMin(int resizeButtonType, float resizeButtonTop, int mainButtonHeight) {
        if (resizeButtonType == RESIZE_TOP_LEFT || resizeButtonType == RESIZE_TOP_RIGHT)
            return resizeButtonTop + (mainButtonHeight - Utils.dpToPx(Parameters.BUTTON_MIN_HEIGHT_DP, activity));
        else
            return resizeButtonTop - (mainButtonHeight - Utils.dpToPx(Parameters.BUTTON_MIN_HEIGHT_DP, activity));
    }

    private void setOptionsVisibilityOnMainButtonResizeStart() {
        resizeOnOff.setVisibility(View.GONE);
        addKey.setVisibility(View.GONE);
        addTouchPad.setVisibility(View.GONE);
        done.setVisibility(View.GONE);
    }

    private void setOptionsVisibilityOnMainButtonResizeEnd() {
        resizeOnOff.setVisibility(View.VISIBLE);
        addKey.setVisibility(View.VISIBLE);
        addTouchPad.setVisibility(View.VISIBLE);
        done.setVisibility(View.VISIBLE);
    }

    private void performMainButtonReposition(View v, float xPositionFinger, float yPositionFinger, float dX, float dY, ImageView bin) {
        float unRestrictedButtonX = xPositionFinger - dX;
        float unRestrictedButtonY = yPositionFinger - dY;

        if (unRestrictedButtonX <= 0)
            v.setX(0);
        else if (unRestrictedButtonX >= Utils.getDisplayMetrics(activity).widthPixels - v.getWidth())
            v.setX(Utils.getDisplayMetrics(activity).widthPixels - v.getWidth());
        else
            v.setX(unRestrictedButtonX);

        if (unRestrictedButtonY <= 0)
            v.setY(0);
        else if (unRestrictedButtonY >= Utils.getDisplayMetrics(activity).heightPixels - v.getHeight())
            v.setY(Utils.getDisplayMetrics(activity).heightPixels - v.getHeight());
        else
            v.setY(unRestrictedButtonY);

        if (Utils.isCoordinateInViewRect(bin, (int) xPositionFinger, (int) yPositionFinger))
            bin.setBackgroundResource(R.drawable.ic_delete_forever_red_50dp);
        else
            bin.setBackgroundResource(R.drawable.ic_delete_forever_black_50dp);
    }

    private void performResizeButtonsReposition(Button mainButton, Button topLeftResize, Button topRightResize, Button bottomRightResize, Button bottomLeftResize) {
        topLeftResize.setX(mainButton.getX() - Utils.dpToPx(Parameters.RESIZE_BOX_WIDTH_DP / 2, activity));
        topLeftResize.setY(mainButton.getY() - Utils.dpToPx(Parameters.RESIZE_BOX_HEIGHT_DP / 2, activity));

        topRightResize.setX(mainButton.getX() + mainButton.getWidth() - Utils.dpToPx(Parameters.RESIZE_BOX_WIDTH_DP / 2, activity));
        topRightResize.setY(mainButton.getY() - Utils.dpToPx(Parameters.RESIZE_BOX_HEIGHT_DP / 2, activity));

        bottomRightResize.setX(mainButton.getX() + mainButton.getWidth() - Utils.dpToPx(Parameters.RESIZE_BOX_WIDTH_DP / 2, activity));
        bottomRightResize.setY(mainButton.getY() + mainButton.getHeight() - Utils.dpToPx(Parameters.RESIZE_BOX_HEIGHT_DP / 2, activity));

        bottomLeftResize.setX(mainButton.getX() - Utils.dpToPx(Parameters.RESIZE_BOX_WIDTH_DP / 2, activity));
        bottomLeftResize.setY(mainButton.getY() + mainButton.getHeight() - Utils.dpToPx(Parameters.RESIZE_BOX_HEIGHT_DP / 2, activity));
    }

    private void performResizeButtonXReposition(Button resizeButton, int resizeButtonType, Button adjacentResizeButtonSameX,
                                                float resizeButtonXWhenMainButtonMin, float xPositionFinger, float dX) {
        float unRestrictedButtonX = (int) (xPositionFinger - dX);

        if (resizeButtonType == RESIZE_TOP_LEFT || resizeButtonType == RESIZE_BOTTOM_LEFT) {
            if (unRestrictedButtonX > resizeButtonXWhenMainButtonMin) {
                resizeButton.setX(resizeButtonXWhenMainButtonMin);
                adjacentResizeButtonSameX.setX(resizeButtonXWhenMainButtonMin);
            }
            else if (unRestrictedButtonX < 0 - Utils.dpToPx(Parameters.RESIZE_BOX_WIDTH_DP / 2, this)) {
                resizeButton.setX(0 - Utils.dpToPx(Parameters.RESIZE_BOX_WIDTH_DP / 2, this));
                adjacentResizeButtonSameX.setX(0 - Utils.dpToPx(Parameters.RESIZE_BOX_WIDTH_DP / 2, this));
            }
            else {
                resizeButton.setX(unRestrictedButtonX);
                adjacentResizeButtonSameX.setX(unRestrictedButtonX);
            }
        }
        else if (resizeButtonType == RESIZE_TOP_RIGHT || resizeButtonType == RESIZE_BOTTOM_RIGHT) {
            if (unRestrictedButtonX < resizeButtonXWhenMainButtonMin) {
                resizeButton.setX(resizeButtonXWhenMainButtonMin);
                adjacentResizeButtonSameX.setX(resizeButtonXWhenMainButtonMin);
            }
            else if (unRestrictedButtonX > parentLayout.getWidth() - Utils.dpToPx(Parameters.RESIZE_BOX_WIDTH_DP / 2, this)) {
                resizeButton.setX(parentLayout.getWidth() - Utils.dpToPx(Parameters.RESIZE_BOX_WIDTH_DP / 2, this));
                adjacentResizeButtonSameX.setX(parentLayout.getWidth() - Utils.dpToPx(Parameters.RESIZE_BOX_WIDTH_DP / 2, this));
            }
            else {
                resizeButton.setX(unRestrictedButtonX);
                adjacentResizeButtonSameX.setX(unRestrictedButtonX);
            }
        }
    }

    private void performResizeButtonYReposition(Button resizeButton, int resizeButtonType, Button adjacentResizeButtonSameY,
                                                float resizeButtonYWhenMainButtonMin, float yPositionFinger, float dY) {
        float unRestrictedButtonY = (int) (yPositionFinger - dY);

        if (resizeButtonType == RESIZE_TOP_LEFT || resizeButtonType == RESIZE_TOP_RIGHT) {
            if (unRestrictedButtonY > resizeButtonYWhenMainButtonMin) {
                resizeButton.setY(resizeButtonYWhenMainButtonMin);
                adjacentResizeButtonSameY.setY(resizeButtonYWhenMainButtonMin);
            }
            else if (unRestrictedButtonY < 0 - Utils.dpToPx(Parameters.RESIZE_BOX_HEIGHT_DP / 2, this)) {
                resizeButton.setY(0 - Utils.dpToPx(Parameters.RESIZE_BOX_HEIGHT_DP / 2, this));
                adjacentResizeButtonSameY.setY(0 - Utils.dpToPx(Parameters.RESIZE_BOX_HEIGHT_DP / 2, this));
            }
            else {
                resizeButton.setY(unRestrictedButtonY);
                adjacentResizeButtonSameY.setY(unRestrictedButtonY);
            }
        }
        else if (resizeButtonType == RESIZE_BOTTOM_LEFT || resizeButtonType == RESIZE_BOTTOM_RIGHT) {
            if (unRestrictedButtonY < resizeButtonYWhenMainButtonMin) {
                resizeButton.setY(resizeButtonYWhenMainButtonMin);
                adjacentResizeButtonSameY.setY(resizeButtonYWhenMainButtonMin);
            }
            else if (unRestrictedButtonY > parentLayout.getHeight() - Utils.dpToPx(Parameters.RESIZE_BOX_HEIGHT_DP / 2, this)) {
                resizeButton.setY(parentLayout.getHeight() - Utils.dpToPx(Parameters.RESIZE_BOX_WIDTH_DP / 2, this));
                adjacentResizeButtonSameY.setY(parentLayout.getHeight() - Utils.dpToPx(Parameters.RESIZE_BOX_HEIGHT_DP / 2, this));
            }
            else {
                resizeButton.setY(unRestrictedButtonY);
                adjacentResizeButtonSameY.setY(unRestrictedButtonY);
            }
        }
    }

    private void performMainButtonResize(Button mainButton, Button resizeButton, int resizeButtonType) {
        float newMainButtonX, newMainButtonY;
        int newMainButtonWidth = 0, newMainButtonHeight = 0;

        if (resizeButtonType == RESIZE_TOP_LEFT) {
            newMainButtonX = resizeButton.getX() + Utils.dpToPx(Parameters.RESIZE_BOX_WIDTH_DP / 2, this);
            newMainButtonY = resizeButton.getY() + Utils.dpToPx(Parameters.RESIZE_BOX_HEIGHT_DP / 2, this);
            newMainButtonWidth = (int) (mainButton.getWidth() + (mainButton.getX() - newMainButtonX));
            newMainButtonHeight = (int) (mainButton.getHeight() + (mainButton.getY() - newMainButtonY));
            mainButton.setX(newMainButtonX);
            mainButton.setY(newMainButtonY);
        }
        else if (resizeButtonType == RESIZE_TOP_RIGHT) {
            newMainButtonY = resizeButton.getY() + Utils.dpToPx(Parameters.RESIZE_BOX_HEIGHT_DP / 2, this);
            newMainButtonWidth = (int) (resizeButton.getX() - mainButton.getX() + Utils.dpToPx(Parameters.RESIZE_BOX_WIDTH_DP / 2, this));
            newMainButtonHeight = (int) (mainButton.getHeight() + (mainButton.getY() - newMainButtonY));
            mainButton.setY(newMainButtonY);
        }
        else if (resizeButtonType == RESIZE_BOTTOM_RIGHT) {
            newMainButtonWidth = (int) (resizeButton.getX() - mainButton.getX() + Utils.dpToPx(Parameters.RESIZE_BOX_WIDTH_DP / 2, this));
            newMainButtonHeight = (int) (resizeButton.getY() - mainButton.getY()  + Utils.dpToPx(Parameters.RESIZE_BOX_HEIGHT_DP / 2, this));
        }
        else if (resizeButtonType == RESIZE_BOTTOM_LEFT) {
            newMainButtonX = resizeButton.getX() + Utils.dpToPx(Parameters.RESIZE_BOX_WIDTH_DP / 2, this);
            newMainButtonWidth = (int) (mainButton.getWidth() + (mainButton.getX() - newMainButtonX));
            newMainButtonHeight = (int) (resizeButton.getY() - mainButton.getY() + Utils.dpToPx(Parameters.RESIZE_BOX_HEIGHT_DP / 2, this));
            mainButton.setX(newMainButtonX);
        }

        mainButton.setLayoutParams(new ConstraintLayout.LayoutParams(newMainButtonWidth, newMainButtonHeight));
        Utils.setTextSizeBasedOnButtonSize(this, mainButton, mainButton.getWidth(), mainButton.getHeight());
    }

    private void showKeyActionOptionDialog(final Button button) {
        GridView gridView = new GridView(this);
        ArrayList<String> keyActions = Preload.getKeyActions();
        final ArrayAdapter<String> Adapter = new ArrayAdapter<>(this, R.layout.key_action_grid_item, keyActions);
        gridView.setAdapter(new ArrayAdapter<>(this, R.layout.key_action_grid_item, keyActions));
        gridView.setNumColumns(3);
        gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        gridView.setGravity(Gravity.CENTER);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        builder.setTitle("Key Action");
        builder.setView(gridView);
        final AlertDialog dialog = builder.create();

        dialog.show();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedAction = Adapter.getItem(position);
                button.setTag(selectedAction);
                button.setText(selectedAction);
                Utils.setTextSizeBasedOnButtonSize(activity, button, button.getWidth(), button.getHeight());
                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            lp.width = Utils.dpToPx(Parameters.KEY_ACTION_DIALOG_PORTRAIT_WIDTH_DP, this);
            lp.height = Utils.dpToPx(Parameters.KEY_ACTION_DIALOG_PORTRAIT_HEIGHT_DP, this);
        }
        else {
            lp.width = Utils.dpToPx(Parameters.KEY_ACTION_DIALOG_LANDSCAPE_WIDTH_DP, this);
            lp.height = Utils.dpToPx(Parameters.KEY_ACTION_DIALOG_LANDSCAPE_HEIGHT_DP, this);
        }

        window.setAttributes(lp);
    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        builder.setMessage("Save this remote control config?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = getIntent();
                String purpose = intent.getStringExtra("purpose");
                if (purpose.equals("create")) {
                    showEnterRemoteControlNameDialog();
                }
                else if (purpose.equals("edit")){
                    overwriteRemoteControlConfigFile();
                    backToMainActivityAfterEdit();
                }
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                backToMainActivityAfterCancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showEnterRemoteControlNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        builder.setMessage("Remote control name:");

        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String enteredName = input.getText().toString();
                backToMainActivityOnValidNameAfterCreate(enteredName);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog, do nothing and let the dialog goes
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void createRemoteControlConfigFile(String remoteControlName, ArrayList<Button> keyList, ArrayList<Button> touchPadList) {
        PrintWriter printWriter = null;
        int[] buttonPosition = new int[2];

        try {
            FileOutputStream fileOutputStream = openFileOutput(remoteControlName, Context.MODE_PRIVATE);
            printWriter = new PrintWriter(fileOutputStream);
            printWriter.println(getResources().getConfiguration().orientation);
            printWriter.println(keyList.size());

            for (Button key : keyList) {
                key.getLocationOnScreen(buttonPosition);
                printWriter.print(key.getTag().toString() + " ");
                printWriter.print((buttonPosition[0] / (float) Utils.getDisplayMetrics(activity).widthPixels)  + " ");
                printWriter.print((buttonPosition[1] / (float) Utils.getDisplayMetrics(activity).heightPixels) + " ");
                printWriter.print(Utils.pxToDp(key.getWidth(), activity) + " ");
                printWriter.print(Utils.pxToDp(key.getHeight(), activity) + "\n");
            }

            printWriter.println(touchPadList.size());

            for (Button touchPad : touchPadList) {
                touchPad.getLocationOnScreen(buttonPosition);
                printWriter.print((buttonPosition[0] / (float) Utils.getDisplayMetrics(activity).widthPixels)  + " ");
                printWriter.print((buttonPosition[1] / (float) Utils.getDisplayMetrics(activity).heightPixels) + " ");
                printWriter.print(Utils.pxToDp(touchPad.getWidth(), activity) + " ");
                printWriter.print(Utils.pxToDp(touchPad.getHeight(), activity) + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        printWriter.flush();
        printWriter.close();
    }

    private void overwriteRemoteControlConfigFile() {
        Intent intent = getIntent();
        String remoteName = intent.getStringExtra("remote_name");

        try {
            File f = new File(activity.getFilesDir(), remoteName);
            PrintWriter writer = new PrintWriter(f);
            writer.print("");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        createRemoteControlConfigFile(remoteName, keyList, touchPadList);
    }

    private void backToMainActivityAfterCancel() {
        Intent startMainActivity = new Intent(activity, MainActivity.class);
        startMainActivity.putExtra("intent_source", "ConfigureRemoteControlActivity");
        startMainActivity.putExtra("purpose", "cancel");
        startMainActivity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(startMainActivity);
    }

    private void backToMainActivityAfterEdit() {
        Intent startMainActivity = new Intent(activity, MainActivity.class);
        startMainActivity.putExtra("intent_source", "ConfigureRemoteControlActivity");
        startMainActivity.putExtra("purpose", "edit");
        startMainActivity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(startMainActivity);
    }

    private void backToMainActivityOnValidNameAfterCreate(String remoteControlName) {
        if (remoteControlName.equals("")) {
            Toast.makeText(activity, "Name cannot be empty", Toast.LENGTH_SHORT).show();
        }
        else if (isNameExist(remoteControlName)) {
            Toast.makeText(activity, "Name already exist", Toast.LENGTH_SHORT).show();
        }
        else if (!remoteControlName.matches("[a-zA-Z0-9]+")) {
            Toast.makeText(activity, "Name can only consist of alphabets and digits", Toast.LENGTH_SHORT).show();
        }
        else {
            createRemoteControlConfigFile(remoteControlName, keyList, touchPadList);
            Intent startMainActivity = new Intent(activity, MainActivity.class);
            startMainActivity.putExtra("intent_source", "ConfigureRemoteControlActivity");
            startMainActivity.putExtra("purpose", "create");
            startMainActivity.putExtra("remote_control_config", new RemoteControlConfig(remoteControlName, getResources().getConfiguration().orientation));
            startMainActivity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(startMainActivity);
        }
    }

    private boolean isNameExist(String name) {
        File f = new File(getFilesDir(), name);
        return f.exists();
    }

    private void setMainButtonCommonAttributes(Button mainButton) {
        mainButton.setId(View.generateViewId());
        mainButton.setStateListAnimator(null);
        mainButton.setAllCaps(false);
        mainButton.setPadding(0,0,0,0);
    }

    private void setMainButtonUniqueAttributesByType(Button mainButton, int mainButtonType) {
        if (mainButtonType == Parameters.BUTTON_TYPE_KEY) {
            setKeyAttributes(mainButton);
        }
        else if (mainButtonType == Parameters.BUTTON_TYPE_TOUCHPAD) {
            setTouchPadAttributes(mainButton);
        }
    }

    private void setKeyAttributes(Button mainButton) {
        float mainButtonX = Parameters.KEY_DEFAULT_X_NORMALIZED * Utils.getDisplayMetrics(activity).widthPixels;
        float mainButtonY = Parameters.KEY_DEFAULT_Y_NORMALIZED * Utils.getDisplayMetrics(activity).heightPixels;
        int mainButtonDefaultWidth = Utils.dpToPx(Parameters.KEY_DEFAULT_WIDTH_DP, activity);
        int mainButtonDefaultHeight = Utils.dpToPx(Parameters.KEY_DEFAULT_HEIGHT_DP, activity);
        mainButton.setX(mainButtonX);
        mainButton.setY(mainButtonY);
        mainButton.setBackgroundResource(R.drawable.key_button);
        mainButton.setLayoutParams(new ConstraintLayout.LayoutParams(mainButtonDefaultWidth, mainButtonDefaultHeight));
        mainButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        mainButton.setTag("No_Action");
    }

    private void setTouchPadAttributes(Button mainButton) {
        float mainButtonX = Parameters.TOUCHPAD_DEFAULT_X_NORMALIZED * Utils.getDisplayMetrics(activity).widthPixels;
        float mainButtonY = Parameters.TOUCHPAD_DEFAULT_Y_NORMALIZED * Utils.getDisplayMetrics(activity).heightPixels;
        int mainButtonDefaultWidth = Utils.dpToPx(Parameters.TOUCHPAD_DEFAULT_WIDTH_DP, activity);
        int mainButtonDefaultHeight = Utils.dpToPx(Parameters.TOUCHPAD_DEFAULT_HEIGHT_DP, activity);
        mainButton.setX(mainButtonX);
        mainButton.setY(mainButtonY);
        mainButton.setBackgroundResource(R.drawable.touchpad_button);
        mainButton.setLayoutParams(new ConstraintLayout.LayoutParams(mainButtonDefaultWidth, mainButtonDefaultHeight));
    }

    private final class CreateButtonClickListener implements View.OnClickListener {
        ArrayList<Button> mainButtonList;
        int mainButtonType;

        CreateButtonClickListener(ArrayList<Button> mainButtonList, int mainButtonType) {
            this.mainButtonList = mainButtonList;
            this.mainButtonType = mainButtonType;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onClick(View view) {
            Button mainButton = new Button(activity);
            setMainButtonCommonAttributes(mainButton);
            setMainButtonUniqueAttributesByType(mainButton, mainButtonType);
            addRepositionAndResizeFunctionForButton(mainButton, mainButtonList, mainButtonType, elevationCount);
            elevationCount++;
            parentLayout.addView(mainButton);
            mainButtonList.add(mainButton);
        }
    }

    private final class ResizeOnOffButtonListener implements View.OnClickListener {
        private ArrayList<Button> resizeButtonList;

        ResizeOnOffButtonListener(ArrayList<Button> resizeButtonList) {
            this.resizeButtonList = resizeButtonList;
        }

        @Override
        public void onClick(View v) {
            if (isResizeButtonVisible) {
                Button resizeOnOffButton = (Button) v;
                resizeOnOffButton.setText("Resize off");
                for (Button resizeButton : resizeButtonList) {
                    resizeButton.setVisibility(View.GONE);
                }
                isResizeButtonVisible = false;
            }
            else {
                Button resizeOnOffButton = (Button) v;
                resizeOnOffButton.setText("Resize on");
                for (Button resizeButton : resizeButtonList) {
                    resizeButton.setVisibility(View.VISIBLE);
                }
                isResizeButtonVisible = true;
            }
        }
    }

    private final class MainButtonTouchListener implements View.OnTouchListener {
        ArrayList<Button> mainButtonList;
        Button topLeftResize, topRightResize, bottomRightResize, bottomLeftResize;
        int mainButtonType;
        int actionMoveCount = 0;
        float xPositionFinger, yPositionFinger, dX, dY, mainButtonLeftPos, mainButtonTopPos;
        int[] buttonPos = new int[2];

        MainButtonTouchListener(ArrayList<Button> mainButtonList, int mainButtonType,
                                Button topLeftResize, Button topRightResize, Button bottomRightResize, Button bottomLeftResize) {
            this.mainButtonList = mainButtonList;
            this.mainButtonType = mainButtonType;
            this.topLeftResize = topLeftResize;
            this.topRightResize = topRightResize;
            this.bottomRightResize = bottomRightResize;
            this.bottomLeftResize = bottomLeftResize;
        }

        @SuppressLint("ClickableViewAccessibility")
        public boolean onTouch(final View v, MotionEvent motionEvent) {
            xPositionFinger = motionEvent.getRawX();
            yPositionFinger = motionEvent.getRawY();
            v.getLocationOnScreen(buttonPos);
            mainButtonLeftPos = buttonPos[0];
            mainButtonTopPos = buttonPos[1];

            switch (motionEvent.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    dX = xPositionFinger - mainButtonLeftPos;
                    dY = yPositionFinger - mainButtonTopPos;
                    break;

                case MotionEvent.ACTION_MOVE:
                    setOptionsVisibilityOnMainButtonRepositionStart();
                    performMainButtonReposition(v, xPositionFinger, yPositionFinger, dX, dY, bin);
                    performResizeButtonsReposition((Button) v, topLeftResize, topRightResize, bottomRightResize, bottomLeftResize);
                    actionMoveCount++;
                    break;

                case MotionEvent.ACTION_UP:
                    if (mainButtonType == Parameters.BUTTON_TYPE_KEY && actionMoveCount < 3) { // Regarded as button clicked
                        showKeyActionOptionDialog((Button) v);
                    }
                    else if (Utils.isCoordinateInViewRect(bin, (int) xPositionFinger, (int) yPositionFinger) && bin.getVisibility() == View.VISIBLE) {
                        removeMainButtonAndAssociatedResizeButtons((Button) v, topLeftResize, topRightResize, bottomRightResize, bottomLeftResize);
                        mainButtonList.remove((Button) v);
                    }

                    setOptionsVisibilityOnMainButtonRepositionEnd();
                    actionMoveCount = 0;
                    break;

                default:
                    break;
            }

            return true;
        }
    }

    private final class ResizeButtonTouchListener implements View.OnTouchListener {
        int resizeButtonType;
        Button mainButton;
        Button adjacentResizeButtonSameX;
        Button adjacentResizeButtonSameY;
        int[] resizeButtonPos = new int[2];
        float xPositionFinger, yPositionFinger, resizeButtonLeft, resizeButtonTop, dX, dY, resizeButtonXWhenMainButtonMin, resizeButtonYWhenMainButtonMin;

        ResizeButtonTouchListener(int resizeButtonType, Button mainButton, Button adjacentResizeButtonSameX, Button adjacentResizeButtonSameY) {
            this.resizeButtonType = resizeButtonType;
            this.mainButton = mainButton;
            this.adjacentResizeButtonSameX = adjacentResizeButtonSameX;
            this.adjacentResizeButtonSameY = adjacentResizeButtonSameY;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            xPositionFinger = event.getRawX();
            yPositionFinger = event.getRawY();
            v.getLocationOnScreen(resizeButtonPos);
            resizeButtonLeft = resizeButtonPos[0];
            resizeButtonTop = resizeButtonPos[1];

            switch (event.getActionMasked()) {
                case (MotionEvent.ACTION_DOWN):
                    dX = xPositionFinger - resizeButtonLeft;
                    dY = yPositionFinger - resizeButtonTop;
                    resizeButtonXWhenMainButtonMin = calculateResizeButtonXWhenMainButtonMin(resizeButtonType, resizeButtonLeft, mainButton.getWidth());
                    resizeButtonYWhenMainButtonMin = calculateResizeButtonYWhenMainButtonMin(resizeButtonType, resizeButtonTop, mainButton.getHeight());

                    break;
                case (MotionEvent.ACTION_MOVE):
                    setOptionsVisibilityOnMainButtonResizeStart();
                    performResizeButtonXReposition((Button) v, resizeButtonType, adjacentResizeButtonSameX, resizeButtonXWhenMainButtonMin,xPositionFinger,dX);
                    performResizeButtonYReposition((Button) v, resizeButtonType, adjacentResizeButtonSameY, resizeButtonYWhenMainButtonMin, yPositionFinger, dY);
                    performMainButtonResize(mainButton, (Button) v, resizeButtonType);
                    break;
                case (MotionEvent.ACTION_UP):
                    setOptionsVisibilityOnMainButtonResizeEnd();
                    break;
            }

            return true;
        }
    }

    private final class DoneCreateClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            showEnterRemoteControlNameDialog();
        }

    }

    private final class DoneEditClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            overwriteRemoteControlConfigFile();
            backToMainActivityAfterEdit();
        }

    }

}
