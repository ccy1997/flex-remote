package fc.flexremote;

import android.os.SystemClock;

public class AdsFlag {
    public static boolean showAds = true;
    public static long interstitialAdLastShownTime = SystemClock.elapsedRealtime();
}
