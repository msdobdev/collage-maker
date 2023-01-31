package com.ms.dob.photocollage.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ms.dob.photocollage.Callback;
import com.ms.dob.photocollage.CollageUtils;
import com.ms.dob.photocollage.DegreeSeekBar;
import com.ms.dob.photocollage.FileUtils;
import com.ms.dob.photocollage.R;
import com.ms.dob.photocollage.photopicker.Define;
import com.ms.dob.photocollage.photopicker.PhotoPicker;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import com.ms.dob.collage.CollageLayout;
import com.ms.dob.collage.CollageView;
import com.ms.dob.collage.CollagePiece;


import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class CollageActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int FLAG_CONTROL_LINE_SIZE = 1;
    private static final int FLAG_CONTROL_CORNER = 1 << 1;
    private static final String TAG = "CollageActivity";

    private CollageLayout collageLayout;
    private List<String> bitmapPaint;
    private CollageView collageView;
    private DegreeSeekBar degreeSeekBar;

    private List<Target> targets = new ArrayList<>();
    private int deviceWidth = 0;
    ProgressDialog progressdialog;
    private int controlFlag;
    View ad_default_layout;
    LinearLayout ly_adcontainer;
    private boolean isShown = true;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collage);
        ad_default_layout = findViewById(R.id.ad_default_layout);
        ly_adcontainer = findViewById(R.id.ly_adcontainer);
        deviceWidth = getResources().getDisplayMetrics().widthPixels;

        int type = getIntent().getIntExtra("type", 0);
        int pieceSize = getIntent().getIntExtra("piece_size", 0);
        int themeId = getIntent().getIntExtra("theme_id", 0);
        bitmapPaint = getIntent().getStringArrayListExtra("photo_path");
        collageLayout = CollageUtils.getPuzzleLayout(type, pieceSize, themeId);
        progressdialog = new ProgressDialog(CollageActivity.this);
        progressdialog.setMessage("Saving....");
        loadInterstitialAd();
        initView();
        loadBanner(ly_adcontainer,ad_default_layout);

        collageView.post(new Runnable() {
            @Override
            public void run() {
                loadPhoto();
            }
        });
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
                Intent intent = new Intent(CollageActivity.this, ShareActivity.class);
                intent.putExtra("shared_file", path);
                startActivity(intent);

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


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void loadPhoto() {
        if (bitmapPaint == null) {
            loadPhotoFromRes();
            return;
        }

        final List<Bitmap> pieces = new ArrayList<>();

        final int count = bitmapPaint.size() > collageLayout.getAreaCount() ? collageLayout.getAreaCount()
                : bitmapPaint.size();

        for (int i = 0; i < count; i++) {
            final Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    pieces.add(bitmap);
                    if (pieces.size() == count) {
                        if (bitmapPaint.size() < collageLayout.getAreaCount()) {
                            for (int i = 0; i < collageLayout.getAreaCount(); i++) {
                                collageView.addPiece(pieces.get(i % count));
                            }
                        } else {
                            collageView.addPieces(pieces);
                        }
                    }
                    targets.remove(this);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };

            Picasso.with(this)
                    .load("file:///" + bitmapPaint.get(i))
                    .resize(deviceWidth, deviceWidth)
                    .centerInside()
                    .config(Bitmap.Config.RGB_565)
                    .into(target);

            targets.add(target);
        }
    }

    private void loadPhotoFromRes() {
        final List<Bitmap> pieces = new ArrayList<>();

        final int[] resIds = new int[]{
                R.drawable.demo1, R.drawable.demo2, R.drawable.demo3, R.drawable.demo4, R.drawable.demo5,
                R.drawable.demo6, R.drawable.demo7, R.drawable.demo8, R.drawable.demo9,
        };

        final int count =
                resIds.length > collageLayout.getAreaCount() ? collageLayout.getAreaCount() : resIds.length;

        for (int i = 0; i < count; i++) {
            final Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    pieces.add(bitmap);
                    if (pieces.size() == count) {
                        if (resIds.length < collageLayout.getAreaCount()) {
                            for (int i = 0; i < collageLayout.getAreaCount(); i++) {
                                collageView.addPiece(pieces.get(i % count));
                            }
                        } else {
                            collageView.addPieces(pieces);
                        }
                    }
                    targets.remove(this);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };

            Picasso.with(this).load(resIds[i]).config(Bitmap.Config.RGB_565).into(target);

            targets.add(target);
        }
    }

    private void initView() {
        ImageView btnBack = (ImageView) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

//    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//    fab.setOnClickListener(new View.OnClickListener() {
//      @Override public void onClick(View view) {
//        share();
//      }
//    });

        collageView = (CollageView) findViewById(R.id.puzzle_view);
        degreeSeekBar = (DegreeSeekBar) findViewById(R.id.degree_seek_bar);

        //TODO the method we can use to change the puzzle view's properties
        collageView.setPuzzleLayout(collageLayout);
        collageView.setTouchEnable(true);
        collageView.setNeedDrawLine(false);
        collageView.setNeedDrawOuterLine(false);
        collageView.setLineSize(4);
        collageView.setLineColor(Color.BLACK);
        collageView.setSelectedLineColor(Color.BLACK);
        collageView.setHandleBarColor(Color.BLACK);
        collageView.setAnimateDuration(300);
        collageView.setOnPieceSelectedListener(new CollageView.OnPieceSelectedListener() {
            @Override
            public void onPieceSelected(CollagePiece piece, int position) {
//                Snackbar.make(collageView, "Piece " + position + " selected", Snackbar.LENGTH_SHORT).show();
            }
        });

        // currently the SlantPuzzleLayout do not support padding
        collageView.setPiecePadding(10);

        ImageView btnReplace = (ImageView) findViewById(R.id.btn_replace);
        ImageView btnRotate = (ImageView) findViewById(R.id.btn_rotate);
        ImageView btnFlipHorizontal = (ImageView) findViewById(R.id.btn_flip_horizontal);
        ImageView btnFlipVertical = (ImageView) findViewById(R.id.btn_flip_vertical);
        ImageView btnBorder = (ImageView) findViewById(R.id.btn_border);
        ImageView btnCorner = (ImageView) findViewById(R.id.btn_corner);

        btnReplace.setOnClickListener(this);
        btnRotate.setOnClickListener(this);
        btnFlipHorizontal.setOnClickListener(this);
        btnFlipVertical.setOnClickListener(this);
        btnBorder.setOnClickListener(this);
        btnCorner.setOnClickListener(this);

        TextView btnSave = (TextView) findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressdialog.show();
                    }
                });
                File file = FileUtils.getNewFile(CollageActivity.this, getResources().getString(R.string.folder_name));
                FileUtils.savePuzzle(collageView, file, 100, new Callback() {
                    @Override
                    public void onSuccess() {
                        //TODO show loader
                        if (mInterstitialAd != null) {
                            progressdialog.dismiss();
                            setInterListener( file.getAbsolutePath());
                            mInterstitialAd.show(CollageActivity.this);
                        }else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //TODO dismiss loader
                                    progressdialog.dismiss();
                                    //TODO open saved screen
                                    Intent intent = new Intent(CollageActivity.this, ShareActivity.class);
                                    intent.putExtra("shared_file", file.getAbsolutePath());
                                    startActivity(intent);
                                }
                            }, 3000);
                        }

//                        Snackbar.make(view, R.string.prompt_save_success, Snackbar.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onFailed() {
                        Snackbar.make(view, R.string.prompt_save_failed, Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        });

        degreeSeekBar.setCurrentDegrees(collageView.getLineSize());
        degreeSeekBar.setDegreeRange(0, 30);
        degreeSeekBar.setScrollingListener(new DegreeSeekBar.ScrollingListener() {
            @Override
            public void onScrollStart() {

            }

            @Override
            public void onScroll(int currentDegrees) {
                switch (controlFlag) {
                    case FLAG_CONTROL_LINE_SIZE:
                        collageView.setLineSize(currentDegrees);
                        break;
                    case FLAG_CONTROL_CORNER:
                        collageView.setPieceRadian(currentDegrees);
                        break;
                }
            }

            @Override
            public void onScrollEnd() {

            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_replace:
                showSelectedPhotoDialog();
                break;
            case R.id.btn_rotate:
                collageView.rotate(90f);
                break;
            case R.id.btn_flip_horizontal:
                collageView.flipHorizontally();
                break;
            case R.id.btn_flip_vertical:
                collageView.flipVertically();
                break;
            case R.id.btn_border:
                controlFlag = FLAG_CONTROL_LINE_SIZE;
                collageView.setNeedDrawLine(!collageView.isNeedDrawLine());
                if (collageView.isNeedDrawLine()) {
                    degreeSeekBar.setVisibility(View.VISIBLE);
                    degreeSeekBar.setCurrentDegrees(collageView.getLineSize());
                    degreeSeekBar.setDegreeRange(0, 30);
                } else {
                    degreeSeekBar.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.btn_corner:
                if (controlFlag == FLAG_CONTROL_CORNER && degreeSeekBar.getVisibility() == View.VISIBLE) {
                    degreeSeekBar.setVisibility(View.INVISIBLE);
                    return;
                }
                degreeSeekBar.setCurrentDegrees((int) collageView.getPieceRadian());
                controlFlag = FLAG_CONTROL_CORNER;
                degreeSeekBar.setVisibility(View.VISIBLE);
                degreeSeekBar.setDegreeRange(0, 100);
                break;
        }
    }

    private void showSelectedPhotoDialog() {
        PhotoPicker.newInstance().setMaxCount(1).pick(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Define.DEFAULT_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> paths = data.getStringArrayListExtra(Define.PATHS);
            String path = paths.get(0);

            final Target target = new Target() {
                @Override
                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                    collageView.replace(bitmap, "");
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    Snackbar.make(collageView, "Replace Failed!", Snackbar.LENGTH_SHORT).show();
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };

            //noinspection SuspiciousNameCombination
            Picasso.with(this)
                    .load("file:///" + path)
                    .resize(deviceWidth, deviceWidth)
                    .centerInside()
                    .config(Bitmap.Config.RGB_565)
                    .into(target);
        }
    }

    private void loadBanner(LinearLayout adcontainer,View ad_default_layout) {
        AdView adView = new AdView(CollageActivity.this);
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
/*
    private void loadNative(LinearLayout adcontainer) {
        AdLoader adLoader = new AdLoader.Builder(CollageActivity.this, "ca-app-pub-3940256099942544/2247696110")
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd NativeAd) {
                        // Show the ad.
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError adError) {
                        // Handle the failure by logging, altering the UI, and so on.
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();
        adLoader.loadAd(new AdRequest.Builder().build());
        AdLoader.Builder builder = new AdLoader.Builder(this, "<your ad unit ID>")
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        // Assumes you have a placeholder FrameLayout in your View layout
                        // (with id fl_adplaceholder) where the ad is to be placed.
                        FrameLayout frameLayout =
                                findViewById(R.id.fl_adplaceholder);
                        // Assumes that your ad layout is in a file call native_ad_layout.xml
                        // in the res/layout folder
                        NativeAdView adView = (NativeAdView) getLayoutInflater()
                                .inflate(R.layout.native_ad_layout, null);
                        // This method sets the text, images and the native ad, etc into the ad
                        // view.
                        populateNativeAdView(nativeAd, adView);
                        frameLayout.removeAllViews();
                        frameLayout.addView(adView);
                    }
                });




    }
*/
}
