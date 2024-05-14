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
import com.example.gestioncartelasistencial.fragments.AdminFragment;
import com.example.gestioncartelasistencial.fragments.DoctorFragment;
import com.example.gestioncartelasistencial.fragments.HomeFragment;
import com.example.gestioncartelasistencial.fragments.ReserveFragment;
import com.example.gestioncartelasistencial.fragments.SettingsFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtengo el correo del intent.
        Intent intent = getIntent();
        String correo = intent.getStringExtra("CORREO");

        // Inicializo las variables.
        Toolbar toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer);
        NavigationView navigationView = findViewById(R.id.nav);
        View headerView = navigationView.getHeaderView(0);
        TextView userEmailTextView = headerView.findViewById(R.id.user_email_textview);
        userEmailTextView.setText(correo);

        // Seteo el primer fragment.
        fragmentR(new HomeFragment());

        // Inicializo la barra de navegación.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));

        // Lógica para el menú y cambio de fragments.
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int itemId = menuItem.getItemId();

            if (itemId == R.id.home) {
                fragmentR(new HomeFragment());
            } else if (itemId == R.id.doctor) {
                fragmentR(new DoctorFragment());
            } else if (itemId == R.id.reserve) {
                fragmentR(new ReserveFragment());
            } else if (itemId == R.id.settings) {
                fragmentR(new SettingsFragment());
            } else if (itemId == R.id.admin) {
                fragmentR(new AdminFragment());
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void fragmentR(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}