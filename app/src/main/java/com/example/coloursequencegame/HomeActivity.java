package com.example.coloursequencegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void doPlay(View view) {
        Intent playAct = new Intent(this, MainActivity.class);
        startActivity(playAct);
        finish();
    }
}