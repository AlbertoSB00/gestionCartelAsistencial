package com.example.gestioncartelasistencial.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.gestioncartelasistencial.R;
import com.example.gestioncartelasistencial.fragments.DoctorFragment;
import com.example.gestioncartelasistencial.fragments.HomeFragment;
import com.example.gestioncartelasistencial.fragments.ReserveFragment;
import com.example.gestioncartelasistencial.fragments.SettingsFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    // Variables.
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    TextView userEmailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtengo el correo del intent.
        Intent intent = getIntent();
        String correo = intent.getStringExtra("CORREO");

        // Inicializo las variables.
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.nav);
        View headerView = navigationView.getHeaderView(0);
        userEmailTextView = headerView.findViewById(R.id.user_email_textview);
        userEmailTextView.setText(correo);

        // Seteo el primer fragment.
        fragmentR(new HomeFragment());

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int itemId = menuItem.getItemId();

            if( itemId == R.id.home ) {
                drawerLayout.closeDrawer(GravityCompat.START);
                fragmentR(new HomeFragment());

            } else if( itemId == R.id.doctor ) {
                drawerLayout.closeDrawer(GravityCompat.START);
                fragmentR(new DoctorFragment());

            }else if( itemId == R.id.reserve ) {
                drawerLayout.closeDrawer(GravityCompat.START);
                fragmentR(new ReserveFragment());

            }else if( itemId == R.id.settings ) {
                drawerLayout.closeDrawer(GravityCompat.START);
                fragmentR(new SettingsFragment());
            }

            return true;
        });

    }

    private void fragmentR(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}