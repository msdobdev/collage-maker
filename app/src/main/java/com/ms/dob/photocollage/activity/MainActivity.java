package com.ms.dob.photocollage.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.collection.ArrayMap;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.ms.dob.photocollage.CollageAdapter;
import com.ms.dob.photocollage.CollageUtils;
import com.ms.dob.photocollage.photopicker.GetAllPhotoTask;
import com.ms.dob.photocollage.photopicker.PhotoManager;
import com.ms.dob.photocollage.photopicker.datatype.Photo;
import com.ms.dob.photocollage.photopicker.ui.adapter.PhotoAdapter;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.ms.dob.photocollage.layout.custom.StraightLayoutHelper;
import com.ms.dob.photocollage.R;

import com.ms.dob.collage.CollageLayout;
import com.ms.dob.collage.skew.SkewCollageLayout;
//import com.xiaopo.flying.poiphoto.GetAllPhotoTask;
//import com.xiaopo.flying.poiphoto.datatype.Photo;
//import com.xiaopo.flying.poiphoto.ui.adapter.PhotoAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "MainActivity";

  private RecyclerView photoList;
  private RecyclerView puzzleList;

  private CollageAdapter collageAdapter;
  private PhotoAdapter photoAdapter;

  private List<Bitmap> bitmaps = new ArrayList<>();
  private ArrayMap<String, Bitmap> arrayBitmaps = new ArrayMap<>();
  private ArrayList<String> selectedPath = new ArrayList<>();

  private PuzzleHandler puzzleHandler;

  private List<Target> targets = new ArrayList<>();

  private int deviceWidth;
  View ad_default_layout;
  private LinearLayout ly_adcontainer_main;
  private InterstitialAd mInterstitialAd;
  private boolean isShown = true;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    puzzleHandler = new PuzzleHandler(this);

    deviceWidth = getResources().getDisplayMetrics().widthPixels;
    loadInterstitialAd();

    initView();
    ad_default_layout = findViewById(R.id.ad_default_layout);
    ly_adcontainer_main = findViewById(R.id.ly_adcontainer_main);
    loadBanner(ly_adcontainer_main,ad_default_layout);
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED
        || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this, new String[] {
          Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
      }, 110);
    } else {
      loadPhoto();
    }
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
    AdView adView = new AdView(MainActivity.this);
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


  private void loadPhoto() {

    new GetAllPhotoTask() {
      @Override protected void onPostExecute(List<Photo> photos) {
        super.onPostExecute(photos);
        photoAdapter.refreshData(photos);
      }
    }.execute(new PhotoManager(this));
  }

  private void initView() {
    photoList = (RecyclerView) findViewById(R.id.photo_list);
    puzzleList = (RecyclerView) findViewById(R.id.puzzle_list);

    photoAdapter = new PhotoAdapter();
    photoAdapter.setMaxCount(9);
    photoAdapter.setSelectedResId(R.drawable.photo_selected);

    photoList.setAdapter(photoAdapter);
    photoList.setLayoutManager(new GridLayoutManager(this, 4));

    collageAdapter = new CollageAdapter();
    puzzleList.setAdapter(collageAdapter);
    puzzleList.setLayoutManager(
        new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    puzzleList.setHasFixedSize(true);

    collageAdapter.setOnItemClickListener(new CollageAdapter.OnItemClickListener() {
      @Override public void onItemClick(CollageLayout collageLayout, int themeId) {

        if (mInterstitialAd != null) {
          setInterListener(collageLayout, themeId);
          mInterstitialAd.show(MainActivity.this);
        } else {
          Intent intent = new Intent(MainActivity.this, CollageActivity.class);
          intent.putStringArrayListExtra("photo_path", selectedPath);
          if (collageLayout instanceof SkewCollageLayout) {
            intent.putExtra("type", 0);
          } else {
            intent.putExtra("type", 1);
          }
          intent.putExtra("piece_size", selectedPath.size());
          intent.putExtra("theme_id", themeId);

          startActivity(intent);
        }
      }
    });

    photoAdapter.setOnPhotoSelectedListener(new PhotoAdapter.OnPhotoSelectedListener() {
      @Override
      public void onPhotoSelected(final Photo photo, int position) {
        Message message = Message.obtain();
        message.what = 120;
        message.obj = photo.getPath();
        puzzleHandler.sendMessage(message);

        //prefetch the photo
        Picasso.with(MainActivity.this)
            .load("file:///" + photo.getPath())
            .resize(deviceWidth, deviceWidth)
            .centerInside()
            .memoryPolicy(MemoryPolicy.NO_CACHE)
            .fetch();
      }
    });

    photoAdapter.setOnPhotoUnSelectedListener(new PhotoAdapter.OnPhotoUnSelectedListener() {
      @Override public void onPhotoUnSelected(Photo photo, int position) {
        Bitmap bitmap = arrayBitmaps.remove(photo.getPath());
        bitmaps.remove(bitmap);
        selectedPath.remove(photo.getPath());

        collageAdapter.refreshData(StraightLayoutHelper.getAllThemeLayout(bitmaps.size()), bitmaps);
      }
    });

    photoAdapter.setOnSelectedMaxListener(new PhotoAdapter.OnSelectedMaxListener() {
      @Override public void onSelectedMax() {
        Toast.makeText(MainActivity.this, "You have reached to maximum limit!!", Toast.LENGTH_SHORT).show();
      }
    });

    ImageView btnCancel = (ImageView) findViewById(R.id.btn_cancel);
    btnCancel.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (bitmaps == null || bitmaps.size() == 0) {
          onBackPressed();
          return;
        }

        arrayBitmaps.clear();
        bitmaps.clear();
        selectedPath.clear();

        photoAdapter.reset();
        puzzleHandler.sendEmptyMessage(119);
      }
    });

//    ImageView btnMore = (ImageView) findViewById(R.id.btn_more);
//    btnMore.setOnClickListener(new View.OnClickListener() {
//      @Override public void onClick(View view) {
//        showMoreDialog(view);
//      }
//    });
  }

  private void showMoreDialog(View view) {
    PopupMenu popupMenu = new PopupMenu(this, view, Gravity.BOTTOM);
    popupMenu.inflate(R.menu.menu_main);
    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
      @Override public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
          case R.id.action_playground:
            Intent intent = new Intent(MainActivity.this, CollageLayoutSelectionActivity.class);
            startActivity(intent);
            break;
//          case R.id.action_about:
//            showAboutInfo();
//            break;
        }
        return false;
      }
    });
    popupMenu.show();
  }

//  private void showAboutInfo() {
//    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
//    bottomSheetDialog.setContentView(R.layout.about_info);
//    bottomSheetDialog.show();
//  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == 110
        && grantResults[0] == PackageManager.PERMISSION_GRANTED
        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
      loadPhoto();
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();

    arrayBitmaps.clear();
    arrayBitmaps = null;

    bitmaps.clear();
    bitmaps = null;
  }

  private void refreshLayout() {
    puzzleList.post(new Runnable() {
      @Override public void run() {
        collageAdapter.refreshData(CollageUtils.getPuzzleLayouts(bitmaps.size()), bitmaps);
      }
    });
  }

  public void fetchBitmap(final String path) {
    Log.d(TAG, "fetchBitmap: ");
    final Target target = new Target() {
      @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

        Log.d(TAG, "onBitmapLoaded: ");

        arrayBitmaps.put(path, bitmap);
        bitmaps.add(bitmap);
        selectedPath.add(path);

        puzzleHandler.sendEmptyMessage(119);
        targets.remove(this);
      }

      @Override public void onBitmapFailed(Drawable errorDrawable) {

      }

      @Override public void onPrepareLoad(Drawable placeHolderDrawable) {

      }
    };

    Picasso.with(this)
        .load("file:///" + path)
        .resize(300, 300)
        .centerInside()
        .config(Bitmap.Config.RGB_565)
        .into(target);

    targets.add(target);
  }

  private static class PuzzleHandler extends Handler {
    private WeakReference<MainActivity> mReference;

    PuzzleHandler(MainActivity activity) {
      mReference = new WeakReference<>(activity);
    }

    @Override public void handleMessage(Message msg) {
      super.handleMessage(msg);
      if (msg.what == 119) {
        mReference.get().refreshLayout();
      } else if (msg.what == 120) {
        mReference.get().fetchBitmap((String) msg.obj);
      }
    }
  }
}
