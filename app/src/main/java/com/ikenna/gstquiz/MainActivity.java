package com.ikenna.gstquiz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements MaxAdListener {
    FirebaseFirestore database;
    DocumentReference reference;
    TextView name,email,department,points;
    RecyclerView recyclerView;
    LottieAnimationView chart,support;
    FirebaseUser user;
    FirebaseAuth auth;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private MaxInterstitialAd interstitialAd;
    private int retryAttempt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);

        name = findViewById(R.id.usernameTv);
        email = findViewById(R.id.useremailTv);
        department = findViewById(R.id.userdeptTv);
        points = findViewById(R.id.userpointsTv);
//        logout = findViewById(R.id.animationViewlogout);

        AppLovinSdk.getInstance(MainActivity.this).setMediationProvider( "max" );
        AppLovinSdk.initializeSdk( MainActivity.this, new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration configuration)
            {
                // AppLovin SDK is initialized, start loading ads
                interstitialAd = new MaxInterstitialAd( "7d38174b6dea0627", MainActivity.this );
                interstitialAd.setListener( MainActivity.this );

                // Load the first ad
                interstitialAd.loadAd();
            }
        } );


        auth = FirebaseAuth.getInstance();

       FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        chart = findViewById(R.id.animationViewchart);
        support = findViewById(R.id.animationViewsupport);
        String currentuid = user.getUid();


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");
        usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String emaildp = document.getString("email");
                        String namedp = document.getString("name");
                        String departmentdp = document.getString("department");
                        long pointdp = document.getLong("points");
                        name.setText(namedp);
                        email.setText(emaildp);
                        department.setText(departmentdp);
                        points.setText(Math.toIntExact(pointdp)+" points");

//
                    }
                } else {
//                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        AdRequest adRequestint = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-5558886176700454/4102609927", adRequestint,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        mInterstitialAd.show(MainActivity.this);
                        Log.i("TAG", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("TAG", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });



        database = FirebaseFirestore.getInstance();

        final ArrayList<CategoryModel> categories = new ArrayList<>();

        final CategoryAdapter adapter = new CategoryAdapter(MainActivity.this, categories);

        database.collection("categories")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        categories.clear();
                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                            CategoryModel model = snapshot.toObject(CategoryModel.class);
                            model.setCategoryId(snapshot.getId());
                            categories.add(model);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });

       recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,2));
        recyclerView.setAdapter(adapter);

        chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( interstitialAd.isReady() )
                {
                    interstitialAd.showAd();
                    startActivity(new Intent(MainActivity.this,LeaderBoardActivity.class));
                }

            }
        });

        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PackageManager packageManager = MainActivity.this.getPackageManager();
                    Intent intent = new Intent(Intent.ACTION_VIEW);

                    String message = "Goodday am a user of your app, please i have few questions to ask";
                    String url = "https://api.whatsapp.com/send?phone=+2349138473122"+"&text=" + URLEncoder.encode(message, "UTF-8");
                    intent.setPackage("com.whatsapp");
                    intent.setData(Uri.parse(url));
                    if (intent.resolveActivity(packageManager) !=null){
                        startActivity(intent);
                    }else{
                        Toast.makeText(MainActivity.this, "An Error occurred whatsapp not found", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }

            }
        });

//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(MainActivity.this, LoginActivity.class));
//                auth.signOut();
//                finish();
////                startActivity(new Intent(MainActivity.this,LoginActivity.class));
////                finish();
//            }
//        });



    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public void onAdLoaded(MaxAd ad) {
        // Interstitial ad is ready to be shown. interstitialAd.isReady() will now return 'true'

        // Reset retry attempt
        retryAttempt = 0;
        Toast.makeText(this, "Ads loaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdDisplayed(MaxAd ad) {

    }

    @Override
    public void onAdHidden(MaxAd ad) {

    }

    @Override
    public void onAdClicked(MaxAd ad) {

    }

    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {
        retryAttempt++;
        long delayMillis = TimeUnit.SECONDS.toMillis( (long) Math.pow( 2, Math.min( 6, retryAttempt ) ) );

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                interstitialAd.loadAd();
            }
        }, delayMillis );
    }

    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
        interstitialAd.loadAd();
    }
}