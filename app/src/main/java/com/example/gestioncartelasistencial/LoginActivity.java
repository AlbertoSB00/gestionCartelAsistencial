package com.example.gestioncartelasistencial;

import java.sql.Connection;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    ConnectionSQL connection;
    Connection connect;
    ResultSet resultSet;
    String name, str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        connection = new ConnectionSQL();
        connect();

        // Al pulsar "Regístrate ahora".
        TextView textoRegistrateAhora = findViewById(R.id.textoRegistrateAhora);
        textoRegistrateAhora.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    // Conexión a la bd.
    public void connect(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() ->{
            try{
                connect = connection.CONN();
                if(connect == null){
                    str = "Error in connection with MySQL Server.";
                }else{
                    str = "Connected with MySQL Server.";
                }
            }catch (Exception e){
                throw new RuntimeException(e);
            }

            runOnUiThread(() ->{
                try{
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
            });
        });
    }
}