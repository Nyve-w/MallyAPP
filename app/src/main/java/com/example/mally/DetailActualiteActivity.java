
package com.example.mally;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.mally.R;

public class DetailActualiteActivity extends AppCompatActivity {
    TextView txtTitre, txtDescription;
    ImageView imgActualite;
    Button btnLireArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_actualite);

        imgActualite = findViewById(R.id.imgActualite);
        txtTitre = findViewById(R.id.txtTitreDetail);
        txtDescription = findViewById(R.id.txtDescriptionDetail);
        btnLireArticle = findViewById(R.id.btnLireArticle);

        Intent intent = getIntent();

        String titre = intent.getStringExtra("titre");
        String description = intent.getStringExtra("description");
        String imageUrl = intent.getStringExtra("image");
        String urlArticle = intent.getStringExtra("url");

        txtTitre.setText(titre);
        txtDescription.setText(description);

        // Mise en cache des images
        Glide.with(this).load(imageUrl).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(imgActualite);

        //Bouton pour cliquer sur une actualité
        btnLireArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent navigateur = new Intent(Intent.ACTION_VIEW);
                navigateur.setData(Uri.parse(urlArticle));
                startActivity(navigateur);
            }
        });
    }
}
