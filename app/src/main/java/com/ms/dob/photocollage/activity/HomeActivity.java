package com.ms.dob.photocollage.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.ms.dob.photocollage.R;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    View btn_create, btn_mycreation, btn_playground;
    LinearLayout ly_adcontainer_home;
    View ad_default_layout;
    private InterstitialAd mInterstitialAd;
    private Clicked_item selectedItem;
    private boolean isShown = true;

    private enum Clicked_item {
        CREATE,PLAYGROUND,CREATION
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ly_adcontainer_home = findViewById(R.id.ly_adcontainer_home);
        ad_default_layout = findViewById(R.id.ad_default_layout);
        btn_create = findViewById(R.id.btn_create);
        btn_mycreation = findViewById(R.id.btn_mycreation);
        btn_playground = findViewById(R.id.btn_playground);
        loadInterstitialAd();

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mInterstitialAd != null) {
                    selectedItem = Clicked_item.CREATE;
                    setInterListener();
                    mInterstitialAd.show(HomeActivity.this);
                }else {
                    startActivity(new Intent(HomeActivity.this, MainActivity.class));
                    loadInterstitialAd();

                }
//                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
//                startActivity(intent);
            }
        });
        btn_mycreation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mInterstitialAd != null) {
                    selectedItem = Clicked_item.CREATION;
                    setInterListener();
                    mInterstitialAd.show(HomeActivity.this);
                }else {
                    startActivity(new Intent(HomeActivity.this, CreationListActivity.class));
                    loadInterstitialAd();

                }
//                Intent intent = new Intent(HomeActivity.this, CreationListActivity.class);
//                startActivity(intent);
            }
        });

        btn_playground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mInterstitialAd != null) {
                    selectedItem = Clicked_item.PLAYGROUND;
                    setInterListener();
                    mInterstitialAd.show(HomeActivity.this);
                }else {
                    startActivity(new Intent(HomeActivity.this, CollageLayoutSelectionActivity.class));
                    loadInterstitialAd();

                }
//                Intent intent = new Intent(HomeActivity.this, CollageLayoutSelectionActivity.class);
//                startActivity(intent);
            }
        });
        loadBanner(ly_adcontainer_home, ad_default_layout);


    }

    private void loadInterstitialAd() {

        if (isShown && mInterstitialAd == null) {
            AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load(this, getResources().getString(R.string.interstitial_id), adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.
                            mInterstitialAd = interstitialAd;
                            isShown = false;
                            //                        Log.i(TAG, "onAdLoaded");
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            //                        Log.d(TAG, loadAdError.toString());
                            mInterstitialAd = null;
                            isShown = true;

                        }
                    });
        }


    }
    private void setInterListener() {
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.");
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d(TAG, "Ad dismissed fullscreen content.");
                mInterstitialAd = null;
                isShown = true;
                switch (selectedItem) {
                    case CREATE:
                        startActivity(new Intent(HomeActivity.this, MainActivity.class));

                        break;
                    case PLAYGROUND:
                        startActivity(new Intent(HomeActivity.this, CollageLayoutSelectionActivity.class));

                        break;
                    case CREATION:
                        startActivity(new Intent(HomeActivity.this, CreationListActivity.class));
                        break;

                }
                loadInterstitialAd();

            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when ad fails to show.
                Log.e(TAG, "Ad failed to show fullscreen content.");
                mInterstitialAd = null;
                isShown = true;

            }

            @Override
            public void onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.");
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad showed fullscreen content.");
            }
        });
    }

    private void loadBanner(LinearLayout adcontainer, View ad_default_layout) {
        AdView adView = new AdView(HomeActivity.this);
        adView.setAdSize(AdSize.MEDIUM_RECTANGLE);
        adView.setAdUnitId(getResources().getString(R.string.banner_id));
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                adcontainer.removeAllViews();
                adcontainer.addView(adView);
                ad_default_layout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                ad_default_layout.setVisibility(View.GONE);

                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });


    }

}