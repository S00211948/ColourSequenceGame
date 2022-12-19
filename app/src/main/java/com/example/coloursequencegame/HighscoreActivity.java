package com.example.coloursequencegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

public class HighscoreActivity extends AppCompatActivity {

    //Variables for database
    TextView tvName;
    int newHiscore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        //Set up database
        DatabaseHandler db = new DatabaseHandler(this);

        //Get players score
        newHiscore = getIntent().getIntExtra("score", 0);

        //Load top 5 highscores
        topFiveFilter();
    }

    //region Database
    public void addTableRow(HighscoreClass score)
    {
        TableLayout tblHighscores = findViewById(R.id.tblHighscores);
        Log.i("reached", "1");
        TableRow tr = new TableRow(this);
        tr.setMinimumWidth(tblHighscores.getWidth());
        Log.i("reached", "2");
        TextView tvName = new TextView(this);
        TextView tvScore = new TextView(this);
        tvName.setText(score.getName());
        Log.i("reached", "3");
        tvName.setTextSize(20);
        tvName.setGravity(3);
        tvName.setTextColor(getColor(R.color.white));
        tvScore.setText(String.valueOf(score.getHighscore()));
        Log.i("reached", "4");
        tvScore.setTextSize(20);
        tvScore.setGravity(5);
        tvScore.setTextColor(getColor(R.color.white));
        tr.addView(tvName);
        tr.addView(tvScore);
        tblHighscores.addView(tr);
    }

    public void showScoreEntry(int playerScore)
    {
        //Get table
        TableLayout tblHighscores = findViewById(R.id.tblHighscores);
        //Create new row and two textviews
        TableRow tr = new TableRow(this);
        tr.setMinimumWidth(tblHighscores.getWidth());
        tvName = new EditText(this);
        TextView tvScore = new TextView(this);
        //Name TextView attributes
        tvName.setText("Your Name");
        tvName.setTextSize(20);
        tvName.setGravity(3);
        tvName.setTextColor(getColor(R.color.white));
        //Score TextView attributes
        newHiscore = playerScore;
        tvScore.setText(String.valueOf(newHiscore));
        tvScore.setTextSize(20);
        tvScore.setGravity(5);
        tvScore.setTextColor(getColor(R.color.white));
        tr.addView(tvName);
        tr.addView(tvScore);
        tblHighscores.addView(tr);
        //Adding Button
        TableRow trBtn = new TableRow(this);
        trBtn.setMinimumWidth(tblHighscores.getWidth());
        Button btnSubmitNewScore = new Button(this);
        btnSubmitNewScore.setText("Add Your Highscore");
        btnSubmitNewScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPlayerScore();
            }
        });
        trBtn.addView(btnSubmitNewScore);
        tblHighscores.addView(trBtn);
    }

    public void addPlayerScore()
    {
        String name = String.valueOf(tvName.getText());
        DatabaseHandler db = new DatabaseHandler(this);
        db.addHighscore(new HighscoreClass(name, newHiscore));
        List<HighscoreClass> highscoreList = db.getAllHighscore();
        for (HighscoreClass cn2 : highscoreList) {


            String log = "Id: " + cn2.getID() + " ,Name: " + cn2.getName() + " ,Highscore: " +
                    cn2.getHighscore();
            //if(cn2.getHighscore() >= )
            Log.i("Name: ", log);
        }
    }

    public void topFiveFilter()
    {
        DatabaseHandler db = new DatabaseHandler(this);
        List<HighscoreClass> highscoreList = db.top5Highscore();
        Log.i("Name: ", "list successful");
        boolean validHiScore = false;
        for (HighscoreClass cn2 : highscoreList)
        {
            String log = "Id: " + cn2.getID() + " ,Name: " + cn2.getName() + " ,Highscore: " +
                    cn2.getHighscore();
            addTableRow(cn2);
            Log.i("Name: ", log);
            //Check if players score is higher than any of the top 5
            if(newHiscore > cn2.getHighscore())
            {
                validHiScore = true;
            }
        }
        //If player score is higher than any of the top 5:
        //Allow the player to add their score to the db
        if(validHiScore)
        {
            showScoreEntry(newHiscore);
        }
    }

    public void doHome(View view) {
        Intent homeAct = new Intent(this, HomeActivity.class);
        startActivity(homeAct);
        finish();
    }
    //endregion
}