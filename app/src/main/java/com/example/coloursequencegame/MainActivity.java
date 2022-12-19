package com.example.coloursequencegame;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    //Variables for sequence
    Button btnGreen, btnYellow, btnBlue, btnRed;
    TextView tvScore, tvGameState, tvBanner;
    ArrayList<Integer> sequence = new ArrayList<>();
    boolean Play = false;
    boolean demoSeq = false;
    int seqPos = 0;
    int clickPos = 0;
    int score = 0;
    //Variables for Vibration
    Vibrator v;
    boolean positionChange, atBase;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    //Variables for Database
    boolean buildDB = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //link buttons
        btnGreen = findViewById(R.id.btnGreen);
        btnYellow = findViewById(R.id.btnYellow);
        btnRed = findViewById(R.id.btnRed);
        btnBlue = findViewById(R.id.btnBlue);
        //link textviews
        tvScore = findViewById(R.id.tvScore);
        tvBanner = findViewById(R.id.tvBanner);

        // Get instance of Vibrator from current Context
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // choose the sensor you want
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //Set up database
        DatabaseHandler db = new DatabaseHandler(this);
        List<HighscoreClass> highscore = db.getAllHighscore();
        //Get the build command from previous activity:
        //If FALSE: dont rebuild database
        //if TRUE: rebuild database
        buildDB = getIntent().getBooleanExtra("build",true);

        if (buildDB){
            db.emptyHighscore();
            // Inserting Contacts
            Log.i("Insert: ", "Inserting ..");
            db.addHighscore(new HighscoreClass("Joe", 4));
            db.addHighscore(new HighscoreClass("Mary", 3));
            db.addHighscore(new HighscoreClass("Jack", 8));
            db.addHighscore(new HighscoreClass("Andrew", 5));
            db.addHighscore(new HighscoreClass("Harold", 2));
            db.addHighscore(new HighscoreClass("John", 9));
        };

        //Begin the game
        beginGame();
    }

    //region Sequence
    public void resetGame()
    {
        sequence.clear();
        seqPos = 0;
        clickPos = 0;
        score = 0;
        Play = false;
        onPause();
    }

    //launches the game
    public void beginGame()
    {
      buildSequence(4);
      runSequence();
    }

    //Adds inputted number of stages to sequence
    public void buildSequence(int quantity)
    {
        Random rng = new Random();

        for(int i = 0; i < quantity; i++)
        {
            int num = (rng.nextInt(4) + 1);
            Log.i("genNum",String.valueOf(num));
            sequence.add(num);
            Log.i("seqNum",String.valueOf(sequence.get(i)));
        }
    }

    //Demonstrate the sequence
    public void runSequence() {
        onPause();
        demoSeq = false;
        tvBanner.setText("Observe!");
        tvBanner.setTextColor(getColor(R.color.yellow));
        long time = (sequence.size() * 1000) + 1000;

        CountDownTimer cdt = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long l)
            {
                //Give a 1 second delay before beginning demo
                if(demoSeq)
                {
                    switch (sequence.get(seqPos))
                    {
                        case 1:
                            colorClick(btnBlue, demoSeq);
                            Log.i("btn", "Clicking Blue");
                            break;
                        case 2:
                            colorClick(btnRed, demoSeq);
                            Log.i("btn", "Clicking Red");
                            break;
                        case 3:
                            colorClick(btnGreen, demoSeq);
                            Log.i("btn", "Clicking Green");
                            break;
                        case 4:
                            colorClick(btnYellow, demoSeq);
                            Log.i("btn", "Clicking Yellow");
                            break;
                    }
                    seqPos++;
                }
                else
                {
                    demoSeq = true;
                }
            }
            @Override
            public void onFinish()
            {
                onResume();
                seqPos = 0;
                clickPos = 0;
                Play = true;
                tvBanner.setText("Play!");
                tvBanner.setTextColor(getColor(R.color.green));
            }
        };
        cdt.start();
        Play = false;
    }

    //Perform the click on an input button - used to demo sequence
    public void colorClick(Button btn, boolean click)
    {
        if(click) {
            btn.performClick();
            btn.setPressed(true);
            btn.invalidate();
            btn.setPressed(false);
            btn.invalidate();
        }
    }

    //Check if the user input matches the sequence
    public void CheckInput(int btn)
    {
        if(Play)
        {
            if(sequence.get(clickPos) == btn)
            {
                //Logs for debugging
                Log.i("inputVal", "Correct");
                //Log.i("seqSize", "Seq Size " + String.valueOf(sequence.size()));
                //Log.i("clickPos", "Click Pos " + String.valueOf(clickPos));
                //Log.i("clickPos", "Seq Val " + String.valueOf(sequence.get(clickPos)));

                //If player has finished sequence:
                // increase score and add 2 more steps to sequence
                if(clickPos == sequence.size()-1)
                {
                    Log.i("endSeq", "Sequence Completed");
                    Play = false;
                    score += 1;
                    tvScore.setText(String.valueOf(score));
                    endRound(true);
                }
                clickPos++;
            }
            else
            {
                //If player makes incorrect input:
                //stop and reset the game.
                Log.i("inputVal", "Incorrect");
                endRound(false);
            }
        }
    }

    //Display a message and take actions based on success or failure
    public void endRound(boolean success)
    {
        String message ="";
        long duration = 100;
        long interval = 100;
        int color = getColor(R.color.white);
        //Set message based on success or failure
        if (success)
        {
            message = "Round Completed !";
            duration = 1500;
            interval = 500;
            color = getColor(R.color.white);
        }
        else
        {
            message = "Game Over !";
            duration = 1500;
            interval = 300;
            color = getColor(R.color.red);
        }
        //Create timer for end of round actions
        CountDownTimer cdt = new CountDownTimer(duration, interval) {
            @Override
            public void onTick(long l) {
                v.vibrate(100);
            }

            @Override
            public void onFinish() {
                //if successful build next round
                //else end game and go to highscores
                if (success)
                {
                    buildSequence(2);
                    runSequence();
                }
                else
                {
                    goToHiScores();
                }
            }
        };
        tvBanner.setTextColor(color);
        tvBanner.setText(message);
        cdt.start();
    }

    public void goToHiScores()
    {
        Intent hsAct = new Intent(this, HighscoreActivity.class);
        hsAct.putExtra("score", score);
        startActivity(hsAct);
        finish();
    }

    public void doBlueClick(View view) {CheckInput(1);}

    public void doRedClick(View view) {CheckInput(2);}

    public void doGreenClick(View view) {CheckInput(3);}

    public void doYellowClick(View view) {CheckInput(4);}
    //endregion

    //region Accelerometer
    /*
     * When the app is brought to the foreground - using app on screen
     */
    protected void onResume() {
        super.onResume();
        // turn on the sensor
        mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }
    /*
     * App running but not on screen - in the background
     */
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);    // turn off listener to save power
    }
    /*
     * Called by the system every x millisecs when sensor changes
     */
    public void onSensorChanged(SensorEvent event) {
        // called byt the system every x ms
        float x, y, z;
        x = Math.abs(event.values[0]); // get x value
        y = event.values[1];
        z = Math.abs(event.values[2]);
        if(y < -2)
        {
            colorClick(btnBlue, atBase);//green
            positionChange = true;
        }
        else if(y > 2)
        {
            colorClick(btnRed, atBase);//yellow
            positionChange = true;
        }
        else if(x>9)
        {
            colorClick(btnYellow, atBase);//blue
            positionChange = true;
        }
        else if(x<4)
        {
            colorClick(btnGreen, atBase);//red
            positionChange = true;
        }
        else{
            positionChange = false;
            atBase = true;
        }
        if(positionChange && atBase)
        {
            v.vibrate(100);
            positionChange = false;
            atBase = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not using
    }
    //endregion

}