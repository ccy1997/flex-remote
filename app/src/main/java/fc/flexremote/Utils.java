package fc.flexremote;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Utils {

    public static int pxToDp(float px, Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float dp = px / metrics.density;
        return (int) dp;
    }

    public static int dpToPx(float dp, Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float px = dp * metrics.density;
        return (int) px;
    }

    public static boolean isCoordinateInViewRect(View v, int x, int y) {
        Rect rect = new Rect();
        v.getDrawingRect(rect);
        rect.offset((int) v.getX(), (int) v.getY());
        return rect.contains(x, y);
    }

    public static DisplayMetrics getDisplayMetrics(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    public static void setTextSizeBasedOnButtonSize(Activity activity, Button button, int buttonWidth, int buttonHeight) {
        int textCount = button.getText().toString().length();

        if (buttonWidth < buttonHeight)
            button.setTextSize(Utils.pxToDp(buttonWidth / (textCount / 2f + 1f), activity));
        else
            button.setTextSize(Utils.pxToDp(buttonHeight / (textCount / 2f + 1f), activity));
    }

    private static void setKeyAttribute(Button key, float keyXNormalized, float keyYNormalized, int keyWidthDp, int keyHeightDp,
                                        String action, Activity a) {
        key.setId(View.generateViewId());
        key.setStateListAnimator(null);
        key.setAllCaps(false);
        key.setPadding(0,0,0,0);
        key.setX(keyXNormalized * Utils.getDisplayMetrics(a).widthPixels);
        key.setY(keyYNormalized * Utils.getDisplayMetrics(a).heightPixels);
        key.setBackgroundResource(R.drawable.key_button);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(dpToPx(keyWidthDp, a), dpToPx(keyHeightDp, a));
        key.setLayoutParams(layoutParams);
        key.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        key.setTag(action);
        if (!action.equals("No_Action"))
            key.setText(action);
        Utils.setTextSizeBasedOnButtonSize(a, key, dpToPx(keyWidthDp, a), dpToPx(keyHeightDp, a));
    }

    private static void setTouchPadAttribute(Button touchPad, float touchPadXNormalized, float touchPadYNormalized,
                                             int touchPadWidthDp, int touchPadHeightDp, Activity a) {
        touchPad.setId(View.generateViewId());
        touchPad.setStateListAnimator(null);
        touchPad.setX(touchPadXNormalized * Utils.getDisplayMetrics(a).widthPixels);
        touchPad.setY(touchPadYNormalized * Utils.getDisplayMetrics(a).heightPixels);
        ConstraintLayout.LayoutParams layoutParams =
                new ConstraintLayout.LayoutParams(Utils.dpToPx(touchPadWidthDp, a), Utils.dpToPx(touchPadHeightDp, a));
        touchPad.setLayoutParams(layoutParams);
        touchPad.setBackgroundResource(R.drawable.touchpad_button);
    }

    public static void drawRemoteSetupFromFile(Activity a, String filename, ConstraintLayout parentLayout, ArrayList<Button> keyList, ArrayList<Button> touchPadList) {
        try {
            FileInputStream fis = a.openFileInput(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            int orientation = Integer.parseInt(br.readLine());

            if (orientation == Configuration.ORIENTATION_PORTRAIT)
                a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            else
                a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            int keyCount = Integer.parseInt(br.readLine());

            for (int i = 0; i < keyCount; i++) {
                String[] keyInfo = br.readLine().split(" ");
                String action = keyInfo[0];
                float keyXNormalized = Float.parseFloat(keyInfo[1]);
                float keyYNormalized = Float.parseFloat(keyInfo[2]);
                int keyWidthDp = Integer.parseInt(keyInfo[3]);
                int keyHeightDp = Integer.parseInt(keyInfo[4]);

                Button key = new Button(a);
                setKeyAttribute(key, keyXNormalized, keyYNormalized, keyWidthDp, keyHeightDp, action, a);
                parentLayout.addView(key);
                keyList.add(key);
            }

            int touchPadCount = Integer.parseInt(br.readLine());

            for (int i = 0; i < touchPadCount; i++) {
                String[] touchPadInfo = br.readLine().split(" ");
                float touchPadXNormalized = Float.parseFloat(touchPadInfo[0]);
                float touchPadYNormalized = Float.parseFloat(touchPadInfo[1]);
                int touchPadWidthDp = Integer.parseInt(touchPadInfo[2]);
                int touchPadHeightDp = Integer.parseInt(touchPadInfo[3]);

                Button touchPad = new Button(a);
                setTouchPadAttribute(touchPad, touchPadXNormalized, touchPadYNormalized, touchPadWidthDp, touchPadHeightDp, a);
                parentLayout.addView(touchPad);
                touchPadList.add(touchPad);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
