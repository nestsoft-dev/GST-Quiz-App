package com.ikenna.gstquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class ResultActivity extends AppCompatActivity {
    int POINTS = 10;
    TextView score,coinsearn,gradingTxt;
    Button share,home,showAds;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore database;
    private RewardedAd mRewardedAd;
    private final String TAG = "Reward";
    LottieAnimationView resultAnimation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        score = findViewById(R.id.score);
        coinsearn = findViewById(R.id.earnedCoins);
        share = findViewById(R.id.shareBtn);
        home = findViewById(R.id.restartBtn);
        showAds = findViewById(R.id.showAds);
        gradingTxt = findViewById(R.id.gradingTxt);
        resultAnimation = findViewById(R.id.animationViewResult);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(this, "ca-app-pub-5558886176700454/8297839954",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.getMessage());
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        mRewardedAd.show(ResultActivity.this, null);
                        Log.d(TAG, "Ad was loaded.");
                    }
                });

        int correctAnswers = getIntent().getIntExtra("correct", 0);
        int totalQuestions = getIntent().getIntExtra("total", 0);

        long points = correctAnswers * POINTS;
        
        if (correctAnswers <20){
            //TODO: change lottie animation
            resultAnimation.setAnimation();
            gradingTxt.setText("You Can do better if you try harder");


        }else if (correctAnswers < 35){
            gradingTxt.setText("Thats great, but you can do better and increase your score");
            //TODO: change lottie animation
            resultAnimation.setAnimation();


        }else if (correctAnswers >50){
            //TODO: change lottie animation
            resultAnimation.setAnimation();
            gradingTxt.setText("Great performance,We are pleased to be your learning aids.");
        }

        score.setText(String.format("%d/%d", correctAnswers, totalQuestions));
        coinsearn.setText(String.valueOf(points));

        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .update("points", FieldValue.increment(points));

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRewardedAd != null){
                    mRewardedAd.show(ResultActivity.this, null);
                    startActivity(new Intent(ResultActivity.this, MainActivity.class));
                    finish();
                }else {

                    startActivity(new Intent(ResultActivity.this, MainActivity.class));
                    finish();
                }

            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = "Hey, check out these amazing GST QUIZ APP  for free  check it out\n "
                        + "http://play.google.com/store/apps/details?id="+getPackageName();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT,message);
                intent.setType("text/plain");
                startActivity(intent);
            }
        });

        showAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRewardedAd.show(ResultActivity.this, null);
            }
        });
    }
//    void increasePoints(long points){
//       long points + 50;
//    }

    @Override
    public void onBackPressed() {
//        mRewardedAd.show(ResultActivity.this, null);
        startActivity(new Intent(ResultActivity.this,MainActivity.class));
        finish();
        super.onBackPressed();
    }
}