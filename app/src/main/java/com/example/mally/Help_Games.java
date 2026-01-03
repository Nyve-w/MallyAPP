package com.example.mally;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.google.android.material.appbar.MaterialToolbar;

public class Help_Games extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_games);
        MaterialToolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent open= new Intent(Help_Games.this,MyGame.class);
                startActivity(open);
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.search:
                        messageAlert("Tu veux rechercher quoi ? Toi lis seulement","Information");

                }
                return false;
            }
        });
    }

    private void messageAlert(String message,String title){
        AlertDialog.Builder messageAlert=new AlertDialog.Builder(this);
        messageAlert.setTitle(title);
        messageAlert.setMessage(message);
        messageAlert.setIcon(android.R.drawable.ic_dialog_info);
        messageAlert.show();
    }
}