package com.example.mally;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

public class hangmanGame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hangman_game);
        TextView mng ;
        mng = findViewById(R.id.texteV);
        char[] letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        GridLayout grid = findViewById(R.id.gridLayout);

        for (char letter : letters) {
            Button button = new Button(this);
            button.setText(String.valueOf(letter));
            mng.setText(String.valueOf(letter));

            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {


                    checkLetter(letter);
                }
            });

            grid.addView(button);
        }


    }

    private void checkLetter(char letter) {

    }

}