package com.example.gestioncartelasistencial.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gestioncartelasistencial.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private static final String IP = "192.168.1.10";
    private static final int PORT = 12345;

    private EditText campoUser;
    private EditText campoPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        campoUser = findViewById(R.id.campoUser);
        campoPassword = findViewById(R.id.campoPassword);
        Button botonSiguiente = findViewById(R.id.botonSiguiente);
        TextView textoRegistrateAhora = findViewById(R.id.textoRegistrateAhora);
        TextView textoOlvidastePassword = findViewById(R.id.textoOlvidastePassword);

        // Manejo del clic en "Olvidaste tu contraseña".
        textoOlvidastePassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
            finish();
        });

        // Manejo del clic en "Siguiente".
        botonSiguiente.setOnClickListener(v -> handleLogin());

        // Manejo del clic en "Regístrate ahora".
        textoRegistrateAhora.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void handleLogin() {
        String user = campoUser.getText().toString().trim();
        String password = campoPassword.getText().toString().trim();

        if (user.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, rellene todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        String hashPassword = cifrarPassword(password);
        if (hashPassword != null) {
            ordenServer(user, hashPassword);
        } else {
            Toast.makeText(this, "Error al cifrar la contraseña", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para cifrar la contraseña usando SHA-256
    private String cifrarPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());

            // Convertir el hash en formato hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void ordenServer(String user, String hashPassword) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try (Socket socket = new Socket(IP, PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // Enviamos orden y datos al servidor.
                out.println("LOGIN");
                out.println(user);
                out.println(hashPassword);

                // Leemos respuesta.
                String response = in.readLine();

                runOnUiThread(() -> {
                    switch (response) {
                        case "LOGIN_SUCCESS":
                            // Abrir la actividad principal de la aplicación
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("CORREO", campoUser.getText().toString().trim());
                            startActivity(intent);
                            finish();
                            break;
                        case "LOGIN_FAILED":
                            Toast.makeText(LoginActivity.this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(LoginActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                            break;
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show());
            }
        });
    }
}