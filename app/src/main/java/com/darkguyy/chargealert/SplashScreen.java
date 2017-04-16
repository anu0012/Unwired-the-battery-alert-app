package com.darkguyy.chargealert;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.airbnb.lottie.LottieAnimationView;

/**
 * Created by anuragsharma on 12/04/17.
 */

public class SplashScreen extends AppCompatActivity {
    LottieAnimationView animationView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        animationView = (LottieAnimationView) findViewById(R.id.animation_view);

        Thread t = new Thread(){
            @Override
            public void run() {
                try{
                    sleep(3000);
                    Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                    //animationView.cancelAnimation();
                    startActivity(intent);

                    finish();
                }
                catch (Exception e){
                    Log.e("Error", "Pakodi");
                }
            }
        };

        t.start();
    }
}
