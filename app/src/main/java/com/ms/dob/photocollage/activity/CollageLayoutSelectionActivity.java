package com.ms.dob.photocollage.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.ms.dob.photocollage.CollageSelectionAdapter;
import com.ms.dob.photocollage.CollageSelectionAdapter;
import com.ms.dob.photocollage.CollageUtils;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.ms.dob.collage.CollageLayout;
import com.ms.dob.collage.skew.SkewCollageLayout;
import com.ms.dob.photocollage.R;

public class CollageLayoutSelectionActivity extends AppCompatActivity {
    private static final String TAG = "collageselecactivity";
    View ad_default_layout;
    LinearLayout ly_adcontainer_collageselection;
    private boolean isShown = true;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collageselection);
        ad_default_layout = findViewById(R.id.ad_default_layout);
        ly_adcontainer_collageselection = findViewById(R.id.ly_adcontainer_collageselection);
        initView();
        loadInterstitialAd();
        loadBanner(ly_adcontainer_collageselection, ad_default_layout);
        prefetchResPhoto();
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

    private void loadBanner(LinearLayout adcontainer, View ad_default_layout) {
        AdView adView = new AdView(CollageLayoutSelectionActivity.this);
        adView.setAdSize(AdSize.BANNER);
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


    private void prefetchResPhoto() {
        final int[] resIds = new int[]{
                R.drawable.demo1, R.drawable.demo2, R.drawable.demo3, R.drawable.demo4, R.drawable.demo5,
                R.drawable.demo6, R.drawable.demo7, R.drawable.demo8, R.drawable.demo9,
        };

        for (int resId : resIds) {
            Picasso.with(this)
                    .load(resId)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .config(Bitmap.Config.RGB_565)
                    .fetch();
        }
    }

    private void initView() {
        final RecyclerView puzzleList = (RecyclerView) findViewById(R.id.puzzle_list);
        puzzleList.setLayoutManager(new GridLayoutManager(this, 2));

        CollageSelectionAdapter collageAdapter = new CollageSelectionAdapter();

        puzzleList.setAdapter(collageAdapter);

        collageAdapter.refreshData(CollageUtils.getAllPuzzleLayouts(), null);

        collageAdapter.setOnItemClickListener(new CollageSelectionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CollageLayout collageLayout, int themeId) {
                if (mInterstitialAd != null) {
                    setInterListener(collageLayout,themeId);
                    mInterstitialAd.show(CollageLayoutSelectionActivity.this);
                }else {
                    Intent intent = new Intent(CollageLayoutSelectionActivity.this, CollageActivity.class);
                    if (collageLayout instanceof SkewCollageLayout) {
                        intent.putExtra("type", 0);
                    } else {
                        intent.putExtra("type", 1);
                    }
                    intent.putExtra("piece_size", collageLayout.getAreaCount());
                    intent.putExtra("theme_id", themeId);

                    startActivity(intent);
                }

            }
        });

        ImageView btnClose = (ImageView) findViewById(R.id.btn_cancel);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setInterListener(CollageLayout collageLayout, int themeId) {
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
                Intent intent = new Intent(CollageLayoutSelectionActivity.this, CollageActivity.class);
                if (collageLayout instanceof SkewCollageLayout) {
                    intent.putExtra("type", 0);
                } else {
                    intent.putExtra("type", 1);
                }
                intent.putExtra("piece_size", collageLayout.getAreaCount());
                intent.putExtra("theme_id", themeId);

                startActivity(intent);

            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when ad fails to show.
                Log.e(TAG, "Ad failed to show fullscreen content.");
                mInterstitialAd = null;
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

}
