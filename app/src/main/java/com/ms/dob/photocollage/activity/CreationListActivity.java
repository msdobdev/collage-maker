package com.ms.dob.photocollage.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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
import com.ms.dob.photocollage.adapter.CreationAdapter;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CreationListActivity extends AppCompatActivity {
    private static final String TAG = "CreationListActivity";
    RecyclerView ry_creationlist;
    LinearLayout ly_adcontainer_creationlist;
    View ad_default_layout;
    private InterstitialAd mInterstitialAd;
    private boolean isShown = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_list);
        ly_adcontainer_creationlist = findViewById(R.id.ly_adcontainer_creationlist);
        ad_default_layout = findViewById(R.id.ad_default_layout);
        View tv_empty = findViewById(R.id.tv_empty);
        View iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        File mediaStorageDir =
                new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        getResources().getString(R.string.folder_name));
        if (mediaStorageDir != null && mediaStorageDir.listFiles() != null) {
            if (mediaStorageDir.listFiles().length != 0) {
                tv_empty.setVisibility(View.GONE);
            } else {
                tv_empty.setVisibility(View.VISIBLE);
            }
            List<File> listedCreation = Arrays.asList(mediaStorageDir.listFiles());

            Collections.sort(listedCreation, new Comparator<File>() {
                public int compare(File f1, File f2) {
                    return Long.compare(f1.lastModified(), f2.lastModified());
                }
            });
            CreationAdapter creationAdapter = new CreationAdapter(listedCreation, CreationListActivity.this, new CreationAdapter.onItemClickListener() {
                @Override
                public void onCreationItemClick(File file) {
                    //TODO open share screen
                    if (mInterstitialAd != null) {
                        setInterListener(file.getAbsolutePath());
                        mInterstitialAd.show(CreationListActivity.this);
                    }else {
                        Intent intent = new Intent(CreationListActivity.this, ShareActivity.class);
                        intent.putExtra("shared_file", file.getAbsolutePath());
                        startActivity(intent);
                        loadInterstitialAd();

                    }

                }
            });
            ry_creationlist = findViewById(R.id.ry_creationlist);
            ry_creationlist.setAdapter(creationAdapter);
        } else {
            tv_empty.setVisibility(View.VISIBLE);
            //TODO show empty view
        }
        loadBanner(ly_adcontainer_creationlist, ad_default_layout);
//        sorted.sort(Comparator.comparing(File::lastModified));
//        mediaStorageDir.listFiles();
//        Arrays.sort(mediaStorageDir.listFiles(), Comparator.comparingLong(File::lastModified).reversed());
        //TODO get all created files

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

    private void setInterListener(String path) {
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
                Intent intent = new Intent(CreationListActivity.this, ShareActivity.class);
                intent.putExtra("shared_file", path);
                startActivity(intent);
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
        AdView adView = new AdView(CreationListActivity.this);
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

}