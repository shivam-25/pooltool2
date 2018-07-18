package com.example.android.pooltool2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Thread thread=new Thread()
        {
            @Override
            public void run() {
                try{
                    sleep(6000);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                finally {
                    Intent mainIntent = new Intent(WelcomeActivity.this, Home.class);
                    startActivity(mainIntent);

                }

            }
        };
        thread.start();
    }

    @Override
    protected void onPause(){
        super.onPause();
        finish();
    }
}
