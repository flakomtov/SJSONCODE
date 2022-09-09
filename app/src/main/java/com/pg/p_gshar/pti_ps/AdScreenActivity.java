package com.pg.p_gshar.pti_ps;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.pg.p_gshar.pti_ps.data.DataManager;
import com.pg.p_gshar.pti_ps.data.model.AdScreen;
import com.pg.p_gshar.pti_ps.data.model.AdScreenData;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AdScreenActivity extends AppCompatActivity implements MaxRewardedAdListener {
    private DataManager dataManager;
    private int currentIndex;
    private int maxScreens;
    int savedColor;
    private InterstitialAd mInterstitialAd;
    private AdScreen currentAdScreen;
    private MaxRewardedAd rewardedAd;
    private int retryAttempt;
    private int adsProcessed;
    private Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ad_screen);
        dataManager = DataManager.getInstance(this);

        maxScreens = Integer.parseInt(getResources().getString(R.string.max_screens));

        savedColor = dataManager.getSharedPrefs().getInt(DataManager.COLOR_PICKER, -1);
        String displayName = dataManager.getSharedPrefs().getString(DataManager.USER_DISPLAY_NAME, null);

        // bind data with UI
        TextView tv = findViewById(R.id.userName);
        tv.setText(displayName);
        ImageView imageView = findViewById(R.id.selectedAvatar);
        setAvatar(imageView);
        // color set
        LinearLayout gridLayout = findViewById(R.id.navContainer);
        gridLayout.setBackgroundColor(savedColor);

        Button customizeBtn = findViewById(R.id.customizeBtn);
        customizeBtn.setBackgroundColor(savedColor);
        customizeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        Button homeBtn = findViewById(R.id.homeBtn);
        homeBtn.setBackgroundColor(savedColor);
        homeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        Button settingsBtn = findViewById(R.id.settingsBtn);
        settingsBtn.setBackgroundColor(savedColor);
        settingsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, ParametresActivity.class);
            startActivity(intent);
            finish();
        });

        // handle navigation
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentIndex = extras.getInt("currentIndex");
        }

        if (dataManager.getAdsData() != null
                && dataManager.getAdsData().getAdScreens().size() >= currentIndex) {
            for (AdScreen adScreen : dataManager.getAdsData().getAdScreens()) {
                if (adScreen.getScreenIndex() == currentIndex) {
                    currentAdScreen = adScreen;
                }
            }
        }

        // bind adScreen data with UI
        if (currentAdScreen != null) {
            LinearLayout contentContainer = findViewById(R.id.contentContainer);

            TextView description = new TextView(this, null, R.style.Theme_AppUnlocker);
            description.setText(currentAdScreen.getDescription());
            description.setTextSize(16);
            contentContainer.addView(description);

            // to replace with AdView
            LinearLayout adView = new LinearLayout(this);
            LinearLayout.LayoutParams adViewParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            adView.setGravity(Gravity.CENTER);
            adView.setOrientation(LinearLayout.VERTICAL);
            //adViewParams.setMargins(150, 100, 150, 100);
            adView.setLayoutParams(adViewParams);

            // handle ads loading
            switch (dataManager.getAdsData().getDefaultProvider()) {
                case "admob":
                    handleNativeAd(currentAdScreen, this, adView);
                    handleInterstitialAd(currentAdScreen, this);
                    break;
                case "applovin":
                    createRewardedAd(currentAdScreen);
                    break;
            }

            nextBtn = new Button(this);
            nextBtn.setTextColor(getResources().getColor(R.color.white));
            nextBtn.setTextSize(20);
            nextBtn.setText("Next");
            nextBtn.setEnabled(false);
            nextBtn.setBackgroundColor(getResources().getColor(R.color.lightGray));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(30, 50, 30, 50);
            nextBtn.setLayoutParams(params);//.setWidth(contentContainer.getWidth()-50);

            nextBtn.setOnClickListener(view -> {
                if (currentIndex == maxScreens) {
                    switch (dataManager.getAdsData().getDefaultProvider()) {
                        case "admob" :
                            if (mInterstitialAd != null) {
                                mInterstitialAd.show(AdScreenActivity.this);
                                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                    @Override
                                    public void onAdClicked() {
                                    }

                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        mInterstitialAd = null;
                                        Intent intent = new Intent(AdScreenActivity.this, AppOpenerActivity.class);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        mInterstitialAd = null;
                                        Intent intent = new Intent(AdScreenActivity.this, AppOpenerActivity.class);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onAdImpression() {
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                    }
                                });
                            } else {
                                Intent intent = new Intent(AdScreenActivity.this, AppOpenerActivity.class);
                                startActivity(intent);
                            }
                            break;
                        case "applovin" :
                            if (rewardedAd.isReady()) {
                                rewardedAd.showAd();
                            }else {
                                Intent intent = new Intent(AdScreenActivity.this, AppOpenerActivity.class);
                                startActivity(intent);
                            }
                            break;
                    }
                } else {
                    switch (dataManager.getAdsData().getDefaultProvider()) {
                        case "admob" :
                            if (mInterstitialAd != null) {
                                mInterstitialAd.show(AdScreenActivity.this);
                                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                    @Override
                                    public void onAdClicked() {
                                    }

                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        mInterstitialAd = null;
                                        Intent intent = new Intent(AdScreenActivity.this, AdScreenActivity.class);
                                        intent.putExtra("currentIndex", currentIndex + 1);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        mInterstitialAd = null;
                                        Intent intent = new Intent(AdScreenActivity.this, AdScreenActivity.class);
                                        intent.putExtra("currentIndex", currentIndex + 1);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onAdImpression() {
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                    }
                                });
                            } else {
                                Intent intent = new Intent(AdScreenActivity.this, AdScreenActivity.class);
                                intent.putExtra("currentIndex", currentIndex + 1);
                                startActivity(intent);
                            }
                            break;
                        case "applovin" :
                            if (rewardedAd.isReady()) {
                                rewardedAd.showAd();
                            }else {
                                Intent intent = new Intent(AdScreenActivity.this, AdScreenActivity.class);
                                intent.putExtra("currentIndex", currentIndex + 1);
                                startActivity(intent);
                            }
                            break;
                    }
                }
            });

            if (currentIndex % 2 == 0) {
                contentContainer.addView(adView);
                contentContainer.addView(nextBtn);
            } else {
                contentContainer.addView(nextBtn);
                contentContainer.addView(adView);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentAdScreen != null) {
            if (dataManager.getAdsData().getDefaultProvider().equals("admob")){
                handleInterstitialAd(currentAdScreen, this);
            }else if (dataManager.getAdsData().getDefaultProvider().equals("applovin")) {
                createRewardedAd(currentAdScreen);
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void handleNativeAd(AdScreen currentAdScreen, Context context, LinearLayout contentContainer) {
        String nativeAdId = null;
        for (AdScreenData adScreenDatum : currentAdScreen.getAdScreenData()) {
            if (adScreenDatum.getProvider().equals("admob") && adScreenDatum.getType().equals("native")) {
                nativeAdId = adScreenDatum.getId();
            }
        }
        if (nativeAdId == null) {
            return;
        }
        AdLoader adLoader = new AdLoader.Builder(context, nativeAdId)
                .forNativeAd(nativeAd -> {
                    NativeTemplateStyle styles = new
                            NativeTemplateStyle.Builder()
                            .withCallToActionBackgroundColor(
                                    new ColorDrawable(savedColor))
                            .build();
                    LayoutInflater layoutInflater = (LayoutInflater) AdScreenActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
                    View view = layoutInflater.inflate(com.google.android.ads.nativetemplates.R.layout.medium_template_view, contentContainer);
                    TemplateView template = view.findViewById(com.google.android.ads.nativetemplates.R.id.my_template);
                    template.setStyles(styles);
                    template.setNativeAd(nativeAd);
                    // Show the ad.
                    if (isDestroyed()) {
                        nativeAd.destroy();
                    }
                    enableNextBtn();
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError adError) {
                        // Handle the failure by logging, altering the UI, and so on.
                        System.out.println(adError);
                        enableNextBtn();
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private void handleInterstitialAd(AdScreen currentAdScreen, Context context) {
        String interstitialAdId = null;
        for (AdScreenData adScreenDatum : currentAdScreen.getAdScreenData()) {
            if (adScreenDatum.getProvider().equals("admob") && adScreenDatum.getType().equals("interstitial")) {
                interstitialAdId = adScreenDatum.getId();
            }
        }
        if (interstitialAdId == null) {
            return;
        }
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, interstitialAdId, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        enableNextBtn();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        System.out.println(loadAdError);
                        enableNextBtn();
                    }
                });
    }

    private void enableNextBtn() {
        adsProcessed++;
        if (adsProcessed > 1) {
            nextBtn.setEnabled(true);
            nextBtn.setBackgroundColor(savedColor);
        }
    }

    private void setAvatar(ImageView imageView) {
        int avRsc = dataManager.getSharedPrefs().getInt(DataManager.USER_AVATAR, -1);
        List<Integer> imageViews = Arrays.asList(
                R.drawable.av1,
                R.drawable.av2,
                R.drawable.av3,
                R.drawable.av4,
                R.drawable.av5,
                R.drawable.av6);
        if (avRsc != -1) {
            imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), imageViews.get(avRsc), null));
        }
    }

    void createRewardedAd(AdScreen currentAdScreen) {
        String rewardedAdId = null;
        for (AdScreenData adScreenDatum : currentAdScreen.getAdScreenData()) {
            if (adScreenDatum.getProvider().equals("applovin") && adScreenDatum.getType().equals("rewarded")) {
                rewardedAdId = adScreenDatum.getId();
            }
        }
        if (rewardedAdId == null) {
            return;
        }
        rewardedAd = MaxRewardedAd.getInstance(rewardedAdId, AdScreenActivity.this);
        rewardedAd.setListener(this);
        rewardedAd.loadAd();
    }

    // MAX Ad Listener
    @Override
    public void onAdLoaded(final MaxAd maxAd) {
        // Rewarded ad is ready to be shown. rewardedAd.isReady() will now return 'true'

        // Reset retry attempt
        retryAttempt = 0;
    }

    @Override
    public void onAdLoadFailed(final String adUnitId, final MaxError error) {
        // Rewarded ad failed to load
        // We recommend retrying with exponentially higher delays up to a maximum delay (in this case 64 seconds)

        retryAttempt++;
        long delayMillis = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retryAttempt)));

        new Handler().postDelayed(() -> rewardedAd.loadAd(), delayMillis);
    }

    @Override
    public void onAdDisplayFailed(final MaxAd maxAd, final MaxError error) {
        // Rewarded ad failed to display. We recommend loading the next ad
        rewardedAd.loadAd();
    }

    @Override
    public void onAdDisplayed(final MaxAd maxAd) {
    }

    @Override
    public void onAdClicked(final MaxAd maxAd) {
    }

    @Override
    public void onAdHidden(final MaxAd maxAd) {
        // rewarded ad is hidden. Pre-load the next ad
        rewardedAd.loadAd();
    }

    @Override
    public void onRewardedVideoStarted(final MaxAd maxAd) {
    }

    @Override
    public void onRewardedVideoCompleted(final MaxAd maxAd) {
    }

    @Override
    public void onUserRewarded(final MaxAd maxAd, final MaxReward maxReward) {
        if (currentIndex == maxScreens) {
            Intent intent = new Intent(AdScreenActivity.this, AppOpenerActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(AdScreenActivity.this, AdScreenActivity.class);
            intent.putExtra("currentIndex", currentIndex + 1);
            startActivity(intent);
        }
    }
}