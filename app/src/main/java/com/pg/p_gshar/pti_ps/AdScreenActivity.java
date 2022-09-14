package com.pg.p_gshar.pti_ps;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
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
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;
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
import com.pg.p_gshar.pti_ps.utils.BlurBuilder;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselGravity;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.imaginativeworld.whynotimagecarousel.model.CarouselType;
import org.imaginativeworld.whynotimagecarousel.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AdScreenActivity extends AppCompatActivity implements MaxRewardedAdListener {
    private DataManager dataManager;
    private int currentIndex;
    private int maxScreens;
    private int masterColor;
    private InterstitialAd mInterstitialAd;
    private AdScreen currentAdScreen;
    private MaxRewardedAd rewardedAd;
    private int retryAttempt;
    private int adsProcessed;

    private Button settingsBtn;
    private Button homeBtn;
    private Button customizeBtn;
    private Button nextBtn;

    private int masterTextColor;

    private ImageCarousel carousel;
    private StyledPlayerView playerView;
    private ExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ad_screen);
        dataManager = DataManager.getInstance(this);

        maxScreens = dataManager.getAdsData().getAdScreensCount();

        masterColor = getResources().getColor(R.color.masterColor);
        int masterColorAccent = ContextCompat.getColor(AdScreenActivity.this, R.color.masterColorAccent);
        masterTextColor = ContextCompat.getColor(AdScreenActivity.this, R.color.masterTextColor);

        String displayName = dataManager.getSharedPrefs().getString(DataManager.USER_DISPLAY_NAME, null);

        // bind data with UI
        TextView tv = findViewById(R.id.userName);
        tv.setText(displayName);
        ImageView imageView = findViewById(R.id.selectedAvatar);
        setAvatar(imageView);
        // color set
        LinearLayout gridLayout = findViewById(R.id.navContainer);
        gridLayout.setBackgroundColor(masterColor);

        customizeBtn = findViewById(R.id.customizeBtn);
        customizeBtn.setBackgroundColor(masterColor);
        customizeBtn.setEnabled(false);
        customizeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            handleStateAndStartActivity(intent);
            finish();
        });
        homeBtn = findViewById(R.id.homeBtn);
        homeBtn.setBackgroundColor(masterColor);
        homeBtn.setEnabled(false);
        homeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            handleStateAndStartActivity(intent);
            finish();
        });
        settingsBtn = findViewById(R.id.settingsBtn);
        settingsBtn.setBackgroundColor(masterColor);
        settingsBtn.setEnabled(false);
        settingsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            handleStateAndStartActivity(intent);
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

            // to replace with AdView
            LinearLayout adView = new LinearLayout(this);
            LinearLayout.LayoutParams adViewParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            adView.setGravity(Gravity.CENTER);
            adView.setOrientation(LinearLayout.VERTICAL);
            adView.setLayoutParams(adViewParams);

            // add carousel
            if (currentAdScreen.getCarousel() != null) {
                carousel = findViewById(R.id.carousel);
                carousel.registerLifecycle(getLifecycle());
                List<CarouselItem> list = new ArrayList<>();
                for (String ci : currentAdScreen.getCarousel().getItems()) {
                    list.add(new CarouselItem(ci));
                }
                carousel.setData(list);
                carousel.setAutoWidthFixing(true);
                carousel.setShowNavigationButtons(false);
                carousel.setShowBottomShadow(false);
                carousel.setShowTopShadow(false);
                carousel.setImagePlaceholder(
                        ResourcesCompat.getDrawable(getResources(), R.drawable.logo, null));

                if (currentAdScreen.getCarousel().isAdvancedView()) {
                    carousel.setCarouselType(CarouselType.SHOWCASE);
                    carousel.setScalingFactor(0.2f);
                    carousel.setScaleOnScroll(true);
                    carousel.setShowIndicator(false);
                    LinearLayout.LayoutParams carouselParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            Utils.dpToPx(300, this)
                    );
                    carouselParams.setMargins(-35, 0, -35, 50);
                    carousel.setLayoutParams(carouselParams);
                    carousel.setCarouselPaddingStart(Utils.dpToPx(70, this));
                    carousel.setCarouselPaddingEnd(Utils.dpToPx(70, this));
                    carousel.setCarouselGravity(CarouselGravity.CENTER);
                    carousel.next();
                } else {
                    carousel.setCarouselType(CarouselType.BLOCK);
                    carousel.setScaleOnScroll(false);
                    carousel.setShowIndicator(true);
                    carousel.setPadding(
                            Utils.dpToPx(20, this),
                            Utils.dpToPx(0, this),
                            Utils.dpToPx(20, this),
                            Utils.dpToPx(0, this)
                    );
                }
                carousel.setVisibility(View.VISIBLE);
            }

            // add video player
            if (currentAdScreen.isVideo()) {
                player = new ExoPlayer.Builder(this).build();
                playerView = new StyledPlayerView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        350
                );
                params.setMargins(0, 30, 0, 30);
                playerView.setLayoutParams(params);
                // Bind the player to the view.
                playerView.setPlayer(player);

                player.addMediaItem(MediaItem.fromUri("file:///android_asset/video.mp4"));
                player.prepare();
                player.setPlayWhenReady(true);
            }

            // set blurry background
            new BlurBackground(findViewById(R.id.appBackground)).execute(currentAdScreen.getBackground());

            nextBtn = new Button(this);
            nextBtn.setText(R.string.btnNextLabel);
            nextBtn.setVisibility(View.INVISIBLE);
            nextBtn.setBackgroundColor(masterColor);
            nextBtn.setTextColor(masterColorAccent);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(20, 30, 20, 30);
            nextBtn.setLayoutParams(params);
            nextBtn.setOnClickListener(new BtnClickListener());

            // handle ads loading
            switch (dataManager.getAdsData().getDefaultProvider()) {
                case "admob":
                    handleNativeAd(currentAdScreen, this, adView);
                    handleInterstitialAd(currentAdScreen);
                    break;
                case "applovin":
                    createRewardedAd(currentAdScreen);
                    break;
            }

            // handle dispositions
            if (currentAdScreen.getCarousel()!=null && !currentAdScreen.getCarousel().isAdvancedView()) {
                // page 1 in Sketch
                contentContainer.addView(generateRandomDescription(false));
                contentContainer.bringChildToFront(carousel);
                contentContainer.addView(nextBtn);
            } else if (currentAdScreen.getCarousel()==null && !currentAdScreen.isVideo()) {
                if (currentIndex<=2) {
                    // page 2 in Sketch
                    contentContainer.addView(generateRandomDescription(false));
                    contentContainer.addView(adView);
                    contentContainer.addView(nextBtn);
                }else {
                    // pages 4 & 6 in Sketch
                    contentContainer.addView(generateRandomDescription(true));
                    contentContainer.addView(nextBtn);
                    contentContainer.addView(adView);
                }
            } else if (currentAdScreen.getCarousel()==null && currentAdScreen.isVideo()) {
                // page 3 in Sketch
                contentContainer.addView(playerView);
                contentContainer.addView(adView);
                contentContainer.addView(nextBtn);
                contentContainer.addView(generateRandomDescription(false));
            } else if (currentAdScreen.getCarousel()!=null && currentAdScreen.getCarousel().isAdvancedView()) {
                // page 5 in Sketch
                contentContainer.bringChildToFront(carousel);
                contentContainer.addView(generateRandomDescription(false));
                contentContainer.addView(adView);
                contentContainer.addView(nextBtn);
                contentContainer.addView(generateRandomDescription(true));
            }
        }
    }

    private TextView generateRandomDescription(boolean useSecond) {
        TextView description = new TextView(this, null, R.style.Theme_AppUnlocker);
        description.setText(
                useSecond ?
                        getResources().getString(R.string.genericDescription2)
                        : getResources().getString(R.string.genericDescription));
        description.setTextSize(16);
        description.setTextColor(masterTextColor);
        description.setPadding(0, 30, 0, 30);
        return description;
    }

    void handleState() {
        if (player != null) {
            player.release();
        }
    }

    void handleStateAndStartActivity(Intent intent) {
        handleState();
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        finish();
        handleState();
    }

    class BtnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (player != null) {
                player.stop();
            }
            if (currentIndex == maxScreens) {
                switch (dataManager.getAdsData().getDefaultProvider()) {
                    case "admob":
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
                                    handleStateAndStartActivity(intent);
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                    mInterstitialAd = null;
                                    Intent intent = new Intent(AdScreenActivity.this, AppOpenerActivity.class);
                                    handleStateAndStartActivity(intent);
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
                            handleStateAndStartActivity(intent);
                        }
                        break;
                    case "applovin":
                        if (rewardedAd.isReady()) {
                            rewardedAd.showAd();
                        } else {
                            Intent intent = new Intent(AdScreenActivity.this, AppOpenerActivity.class);
                            handleStateAndStartActivity(intent);
                        }
                        break;
                }
            } else {
                switch (dataManager.getAdsData().getDefaultProvider()) {
                    case "admob":
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
                                    handleStateAndStartActivity(intent);
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                    mInterstitialAd = null;
                                    Intent intent = new Intent(AdScreenActivity.this, AdScreenActivity.class);
                                    intent.putExtra("currentIndex", currentIndex + 1);
                                    handleStateAndStartActivity(intent);
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
                            handleStateAndStartActivity(intent);
                        }
                        break;
                    case "applovin":
                        if (rewardedAd.isReady()) {
                            rewardedAd.showAd();
                        } else {
                            Intent intent = new Intent(AdScreenActivity.this, AdScreenActivity.class);
                            intent.putExtra("currentIndex", currentIndex + 1);
                            handleStateAndStartActivity(intent);
                        }
                        break;
                }
            }
        }
    }

    private void handleNativeAd(AdScreen currentAdScreen, Context context, LinearLayout contentContainer) {
        String nativeAdId = null;
        for (AdScreenData adScreenDatum : currentAdScreen.getAdScreenData()) {
            if (adScreenDatum.getProvider().equals("admob") && adScreenDatum.getType().equals("native")) {
                nativeAdId = adScreenDatum.getId();
            }
        }
        if (nativeAdId == null) {
            enableNavigationBtns();
            return;
        }
        AdLoader adLoader = new AdLoader.Builder(context, nativeAdId)
                .forNativeAd(nativeAd -> {
                    NativeTemplateStyle styles = new
                            NativeTemplateStyle.Builder()
                            .withCallToActionBackgroundColor(new ColorDrawable(masterColor))
                            .withSecondaryTextTypefaceColor(masterTextColor)
                            .withTertiaryTextTypefaceColor(masterTextColor)
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
                    enableNavigationBtns();
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        // Handle the failure by logging, altering the UI, and so on.
                        System.out.println(adError);
                        enableNavigationBtns();
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private void handleInterstitialAd(AdScreen currentAdScreen) {
        String interstitialAdId = null;
        for (AdScreenData adScreenDatum : currentAdScreen.getAdScreenData()) {
            if (adScreenDatum.getProvider().equals("admob") && adScreenDatum.getType().equals("interstitial")) {
                interstitialAdId = adScreenDatum.getId();
            }
        }
        if (interstitialAdId == null) {
            enableNavigationBtns();
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
                        enableNavigationBtns();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        System.out.println(loadAdError);
                        enableNavigationBtns();
                    }
                });
    }

    private void enableNavigationBtns() {
        adsProcessed++;
        if (adsProcessed > 1) {
            findViewById(R.id.progressBar).setVisibility(View.GONE);

            nextBtn.setVisibility(View.VISIBLE);
            nextBtn.setBackgroundColor(masterColor);

            for (Button btn : Arrays.asList(settingsBtn, homeBtn, customizeBtn)) {
                btn.setEnabled(true);
            }
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

    public class BlurBackground extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        public BlurBackground(ImageView img){
            this.imageView = img;
        }
        @Override
        protected Bitmap doInBackground(String... url) {
            String stringUrl = url[0];
            Bitmap bitmap = null;
            InputStream inputStream;
            try {
                inputStream = new java.net.URL(stringUrl).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap){
            super.onPostExecute(bitmap);
            Bitmap blurredBitmap = BlurBuilder.blur(AdScreenActivity.this, bitmap);
            imageView.setImageDrawable(new BitmapDrawable(getResources(), blurredBitmap));
            /* // Generate palette asynchronously and use it on a different
            // thread using onGenerated()
            Palette.from(blurredBitmap).generate(p -> {
                // Use generated instance
                if(p != null && p.getMutedSwatch() != null){
                    Palette.Swatch mutedSwatch = p.getMutedSwatch();
                    if (mutedSwatch != null) {
                        masterColorAccent = mutedSwatch.getTitleTextColor();
                        nextBtn.setTextColor(masterColorAccent);
                    }
                }
            });*/
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
            handleStateAndStartActivity(intent);
        }else {
            Intent intent = new Intent(AdScreenActivity.this, AdScreenActivity.class);
            intent.putExtra("currentIndex", currentIndex + 1);
            handleStateAndStartActivity(intent);
        }
    }
}