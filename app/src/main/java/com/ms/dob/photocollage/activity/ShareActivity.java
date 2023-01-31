package com.ms.dob.photocollage.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.squareup.picasso.Picasso;
import com.ms.dob.photocollage.R;

import java.io.File;

public class ShareActivity extends AppCompatActivity {
    View iv_back, iv_home;
    String shared_file;
    public static final String FACEBOOK_PACKAGE_NAME = "com.facebook.katana";
    public static final String TWITTER_PACKAGE_NAME = "com.twitter.android";
    public static final String INSTAGRAM_PACKAGE_NAME = "com.instagram.android";
    public static final String PINTEREST_PACKAGE_NAME = "com.pinterest";
    public static final String WHATS_PACKAGE_NAME = "com.whatsapp";
    ImageView iv_photo;
    LinearLayout ly_adcontainer_share;
    private View ad_default_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        shared_file = getIntent().getStringExtra("shared_file");
        ly_adcontainer_share = findViewById(R.id.ly_adcontainer_share);
        ad_default_layout = findViewById(R.id.ad_default_layout);
        iv_back = findViewById(R.id.iv_back);
        iv_photo = findViewById(R.id.iv_photo);

        Picasso.with(ShareActivity.this)
                .load(new File(shared_file))
                //                .resize(deviceWidth, deviceWidth)
//                .centerInside()
//                .config(Bitmap.Config.RGB_565)
                .into(iv_photo);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        iv_home = findViewById(R.id.iv_home);
        iv_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShareActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        loadBanner(ly_adcontainer_share, ad_default_layout);
    }

    public void onShareClick(View view) {
        switch (view.getId()) {
            case R.id.share_other:
                Intent share = new Intent(Intent.ACTION_SEND);
                Uri screenshotUri = Uri.parse(shared_file);
                share.setType("image/*");
                share.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                startActivity(Intent.createChooser(share, "Share via"));
                break;
            case R.id.share_whatsapp:
                shareAppWithSocial(ShareActivity.this, WHATS_PACKAGE_NAME);
//                Intent shareIntent = new Intent();
//                shareIntent.setAction(Intent.ACTION_SEND);
//                Uri imageUri = Uri.parse(shared_file);
//
//                //Target whatsapp:
//                shareIntent.setPackage("com.whatsapp");
//                //Add text and then Image URI
////                shareIntent.putExtra(Intent.EXTRA_TEXT, "");
//                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
//                shareIntent.setType("image/*");
//                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//                try {
//                    startActivity(shareIntent);
//                } catch (android.content.ActivityNotFoundException ex) {
//                    Toast.makeText(this, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
////                    ToastHelper.MakeShortText("Whatsapp have not been installed.");
//                }
                break;
            case R.id.share_facebook:
                shareAppWithSocial(ShareActivity.this, FACEBOOK_PACKAGE_NAME);

//                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
//                Uri imageUri1 = Uri.parse(shared_file);
//                sendIntent.setType("image/*");
//                sendIntent.setAction(Intent.ACTION_SEND);
//                sendIntent.putExtra(Intent.EXTRA_STREAM, imageUri1);
////                sendIntent.putExtra(Intent.EXTRA_TEXT, "<---MY TEXT--->.");
//                sendIntent.setPackage("com.facebook.orca");
//                sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                try {
//                    startActivity(Intent.createChooser(sendIntent, "Share images..."));
//                } catch (android.content.ActivityNotFoundException ex) {
//                    Toast.makeText(this, "Please Install Facebook Messenger", Toast.LENGTH_LONG).show();
//                }
                break;
            case R.id.share_instagram:
                shareAppWithSocial(ShareActivity.this, INSTAGRAM_PACKAGE_NAME);

                break;
            case R.id.share_gmail:
//                shareAppWithSocial(ShareActivity.this,TWITTER_PACKAGE_NAME);

                Intent i = new Intent(Intent.ACTION_SEND);
//                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"fake@fake.edu"});
//                i.putExtra(Intent.EXTRA_SUBJECT, "On The Job");
                //Log.d("URI@!@#!#!@##!", Uri.fromFile(pic).toString() + "   " + pic.exists());
//                if(pic != null)
//                {
                i.putExtra(Intent.EXTRA_STREAM, Uri.parse(shared_file));
//                }

                i.setType("image/*");
                i.setType("message/rfc822");

                startActivity(Intent.createChooser(i, "Share via"));
                break;
            case R.id.share_twitter:
                shareAppWithSocial(ShareActivity.this, TWITTER_PACKAGE_NAME);
                break;
        }
    }

    public void shareAppWithSocial(Context context, String application) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setPackage(application);
        Uri imageUri = Uri.parse(shared_file);
//        intent.putExtra(android.content.Intent.EXTRA_TITLE, title);
//        intent.putExtra(Intent.EXTRA_TEXT, description);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        try {
            // Start the specific social application
            context.startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            // The application does not exist
            Toast.makeText(context, "app have not been installed.", Toast.LENGTH_SHORT).show();
        }


    }

    private void loadBanner(LinearLayout adcontainer, View ad_default_layout) {
        AdView adView = new AdView(ShareActivity.this);
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