package com.example.mally;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.io.File;

public class MyFormation extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    MaterialToolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_formation);
        matchID();

       /* buttonop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdf();
            }
        });*/

        // 2️⃣ Toolbar

        setSupportActionBar(toolbar);

        // 3️⃣ Drawer toggle (hamburger)
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // 4️⃣ Gérer les clics du drawer
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Intent intent = null;
                switch(item.getItemId()) {
                    case R.id.nav_home:

                        // code pour Home
                        break;
                    case R.id.nav_profile:
                        // code pour Profile
                        break;
                    case R.id.nav_settings:
                        // code pour Settings
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    // Optionnel : gérer le back pour fermer le drawer
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void pdf(){
        File file = new File(getFilesDir(), "cours1.pdf");

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(
                FileProvider.getUriForFile(
                        this,
                        getPackageName() + ".provider",
                        file
                ),
                "application/pdf"
        );
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);


    }
    public void matchID() {
        toolbar = findViewById(R.id.toolbar);
        // 1️⃣ initialisation après setContentView
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.nav_view);
    }
   public void moveToActivity(View v){
        Intent open = new Intent(MyFormation.this,nosformations.class);
        startActivity(open);
   }
    /*public void moveTooFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container_view,fragment).commit();
    }*/

}
