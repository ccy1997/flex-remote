package fc.flexremote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyInterstitial;
import com.adcolony.sdk.AdColonyInterstitialListener;
import com.adcolony.sdk.AdColonyReward;
import com.adcolony.sdk.AdColonyRewardListener;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.jirbo.adcolony.AdColonyAdapter;
import com.jirbo.adcolony.AdColonyBundleBuilder;

public class SettingsActivity extends AppCompatActivity implements RewardedVideoAdListener {
    private final int AD_NETWORK_ADMOB = 0;
    private final int AD_NETWORK_ADCOLONY = 1;
    private AdView mAdView;
    private RewardedVideoAd mRewardedVideoAd;
    private int adNetworkIndex = AD_NETWORK_ADMOB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_activity_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        PreferenceFragmentCompat settingsFragmentCompat =
                (PreferenceFragmentCompat) getSupportFragmentManager().findFragmentById(R.id.settings_fragment);

        Preference disableAdsTemporarily = settingsFragmentCompat.findPreference("disable_ads_temporarily");

        disableAdsTemporarily.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (mRewardedVideoAd.isLoaded())
                    mRewardedVideoAd.show();
                else
                    Toast.makeText(SettingsActivity.this, "Reward video not yet loaded", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        if (AdsFlag.showAds) {
            mAdView = findViewById(R.id.banner_ad_view_settings);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(SettingsActivity.this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAds(adNetworkIndex);
    }

    private void loadRewardedVideoAds(int adNetworkIndex) {
        if (adNetworkIndex == AD_NETWORK_ADMOB) {
            adNetworkIndex = AD_NETWORK_ADCOLONY;
            mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().build());
        }
        else if (adNetworkIndex == AD_NETWORK_ADCOLONY) {
            adNetworkIndex = AD_NETWORK_ADMOB;
            AdRequest adRequest = new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdColonyAdapter.class, AdColonyBundleBuilder.build())
                    .build();

            mRewardedVideoAd.loadAd("ca-app-pub-3009958898657292/5097391890", adRequest);
        }
    }

    @Override
    public void onRewarded(RewardItem reward) {
        AdsFlag.showAds = false;
        if (mAdView != null)
            mAdView.destroy();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        // Do nothing
    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAds(adNetworkIndex);
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        loadRewardedVideoAds(adNetworkIndex);
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        // Do nothing
    }

    @Override
    public void onRewardedVideoAdOpened() {
        // Do nothing
    }

    @Override
    public void onRewardedVideoStarted() {
        // Do nothing
    }

    @Override
    public void onRewardedVideoCompleted() {
        // Do nothing
    }
}
