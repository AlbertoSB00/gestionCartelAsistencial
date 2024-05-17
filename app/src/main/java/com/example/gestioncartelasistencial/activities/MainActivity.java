package com.example.gestioncartelasistencial.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private String correo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtengo el correo del intent.
        Intent intent = getIntent();
        correo = intent.getStringExtra("CORREO");

        // Inicializo las variables.
        Toolbar toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer);
        NavigationView navigationView = findViewById(R.id.nav);
        View headerView = navigationView.getHeaderView(0);
        TextView userEmailTextView = headerView.findViewById(R.id.user_email_textview);
        userEmailTextView.setText(correo);

        // Inicializo la barra de navegación.
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Carga inicial del HomeFragment con el correo.
        fragmentR(HomeFragment.newInstance(correo));

        // Realiza la orden al servidor.
        ordenServer(correo);

        // Lógica para el menú y cambio de fragments.
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int itemId = menuItem.getItemId();

            if (itemId == R.id.home) {
                ordenServer(correo);
            } else if (itemId == R.id.doctor) {
                fragmentR(new DoctorFragment());
            } else if (itemId == R.id.reserve) {
                fragmentR(new ReserveFragment());
            } else if (itemId == R.id.settings) {
                fragmentR(new SettingsFragment());
            } else if (itemId == R.id.admin) {
                fragmentR(new AdminFragment());
            } else if (itemId == R.id.log_out) {
                finish();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
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

    private void ordenServer(String correo) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(new SearchTask("SEARCH", correo));

        try {
            String result = future.get();
            processResult(result);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Error al obtener los datos del servidor", Toast.LENGTH_SHORT).show();
        } finally {
            executor.shutdown();
        }
    }

    private void processResult(String result) {
        if (result != null) {
            if (result.startsWith("ADMIN_SUCCESS")) {
                String nombre = result.substring("ADMIN_SUCCESS".length()).trim();
                // Actualiza el HomeFragment con el nombre recibido.
                fragmentR(HomeFragment.newInstance(nombre));
            } else if (result.equals("ADMIN_FAILED")) {
                Toast.makeText(MainActivity.this, "No se encuentra el nombre", Toast.LENGTH_SHORT).show();
            } else if (result.startsWith("ERROR")) {
                Toast.makeText(MainActivity.this, "Error: " + result.substring("ERROR".length()).trim(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static class SearchTask implements Callable<String> {
        private final String orden;
        private final String correo;

        SearchTask(String orden, String correo) {
            this.orden = orden;
            this.correo = correo;
        }

        @Override
        public String call() {
            String response = "";
            try (Socket socket = new Socket("192.168.1.10", 12345);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // Enviamos orden al servidor.
                out.println(orden);

                // Enviamos correo al servidor.
                out.println(correo);

                // Leemos respuesta.
                response = in.readLine();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
    }
}
