package com.example.ahmed.tamrah;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class IntroWelcomeActivity extends AppCompatActivity {
    TextView tV;
    TextView tV2;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_welcome);
        tV = (TextView) findViewById(R.id.textVIntro);
        tV2 = (TextView) findViewById(R.id.textVIntro);
        imageView =(ImageView) findViewById(R.id.imageViewIntro);
        Animation myAnim = AnimationUtils.loadAnimation(this,R.anim.my_transition);
        tV.startAnimation(myAnim);
        //tV2.startAnimation(myAnim);
        imageView.startAnimation(myAnim);
        //For the Welcome intro animation
        final Intent Intro = new Intent(this,  MainActivity.class);
        Thread timer = new Thread(){
            public void run(){
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    startActivity(Intro);
                    finish();
                }
            }
        };
        timer.start();

    }


    //Not Completed -- for the Skip buttom
    public void finishActivity(View view) {
        finish();
        ///final Intent Intro = new Intent(this,  MainActivity.class);

    }
}
