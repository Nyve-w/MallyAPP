package com.example.mally;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DetailActualiteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_actualite);

        TextView titre=findViewById(R.id.txtTitreDetail);
        TextView date=findViewById(R.id.txtDateDetail);
        TextView description=findViewById(R.id.txtDescriptionDetail);

        Intent intent=getIntent();
        titre.setText(intent.getStringExtra("titre"));
        date.setText(intent.getStringExtra("date"));
        description.setText(intent.getStringExtra("description"));
    }
}