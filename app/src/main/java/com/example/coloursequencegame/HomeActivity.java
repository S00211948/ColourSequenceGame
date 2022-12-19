package com.example.coloursequencegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    boolean buildDB = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        buildDB = getIntent().getBooleanExtra("build",true);
    }

    public void doPlay(View view) {
        Intent playAct = new Intent(this, MainActivity.class);
        playAct.putExtra("build", buildDB);
        startActivity(playAct);
        finish();
    }

    public void doScores(View view) {
        Intent scoreAct = new Intent(this, HighscoreActivity.class);
        scoreAct.putExtra("score", 0);
        startActivity(scoreAct);
        finish();
    }
}