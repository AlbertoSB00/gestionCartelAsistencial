package com.example.gestioncartelasistencial.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText campoEmail;
    private EditText campoPassword;
    private EditText campoRepitePassword;
    private Button botonSiguiente;
    private Button botonSiguiente2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        campoEmail = findViewById(R.id.campoUser);
        campoPassword = findViewById(R.id.campoPassword);
        campoPassword.setVisibility(View.GONE);
        campoRepitePassword = findViewById(R.id.campoRepitePassword);
        campoRepitePassword.setVisibility(View.GONE);
        botonSiguiente = findViewById(R.id.botonSiguiente);
        botonSiguiente2 = findViewById(R.id.botonSiguiente2);
        botonSiguiente2.setVisibility(View.GONE);

        botonSiguiente.setOnClickListener(v -> verificarCorreo());

        botonSiguiente2.setOnClickListener(v -> modificarPassword());
    }

    private void verificarCorreo() {
        String email = campoEmail.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese su correo electrónico", Toast.LENGTH_SHORT).show();
            return;
        }

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try (Socket socket = new Socket("192.168.1.10", 12345);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // Enviamos orden y datos al servidor para verificar el correo.
                out.println("FORGOT");
                out.println(email);

                // Leemos la respuesta del servidor.
                String response = in.readLine();

                runOnUiThread(() -> {
                    if (response.equals("FORGOT_SUCCESS")) {
                        botonSiguiente.setVisibility(View.INVISIBLE);
                        campoPassword.setVisibility(View.VISIBLE);
                        campoRepitePassword.setVisibility(View.VISIBLE);
                        botonSiguiente2.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "Este correo no está registrado en la base de datos", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ForgotPasswordActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void modificarPassword() {
        String email = campoEmail.getText().toString().trim();
        String password = campoPassword.getText().toString().trim();
        String repeatPassword = campoRepitePassword.getText().toString().trim();

        if (!password.equals(repeatPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        String hashPassword = cifrarPassword(password);
        if (hashPassword != null) {
            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                try (Socket socket = new Socket("192.168.1.10", 12345);
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    // Enviamos orden y datos al servidor para modificar la contraseña.
                    out.println("UPDATE");
                    out.println(email);
                    out.println(hashPassword);

                    // Leemos la respuesta del servidor.
                    String response = in.readLine();

                    runOnUiThread(() -> {
                        if (response.equals("UPDATE_SUCCESS")) {
                            Toast.makeText(ForgotPasswordActivity.this, "Contraseña actualizada exitosamente", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this, "No se pudo actualizar la contraseña", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(ForgotPasswordActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show());
                }
            });
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
}