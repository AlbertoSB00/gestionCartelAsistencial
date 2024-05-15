package com.example.gestioncartelasistencial.activities;

import android.content.Intent;
import android.os.AsyncTask;
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

public class LoginActivity extends AppCompatActivity {

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

        // Al pulsa ¿Olvidaste la contraseña?.
        textoOlvidastePassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // Al pulsar "Siguiente".
        botonSiguiente.setOnClickListener(v -> {
            String user = campoUser.getText().toString().trim();
            String password = campoPassword.getText().toString().trim();
            String hashPassword = cifrarPassword(password);

            // Validamos que los campos no estén vacíos.
            if (user.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Por favor, rellene todos los campos.", Toast.LENGTH_SHORT).show();
            }

            ordenServer(user, hashPassword);
        });

        // Al pulsar "Regístrate ahora".
        textoRegistrateAhora.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

    }

    // Método para cifrar la contraseña usando SHA-256
    public String cifrarPassword(String password) {
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
        new AuthenticationTask().execute("LOGIN", user, hashPassword);
    }

    private class AuthenticationTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String response;

            try{
                Socket socket = new Socket("192.168.1.10", 12345);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Enviamos orden al servidor.
                out.println(strings[0]);

                // Enviamos credenciales al servidor.
                out.println(strings[1]);
                out.println(strings[2]);

                // Leemos respuesta.
                response = in.readLine();

                // Cerramos el socket.
                out.close();
                in.close();
                socket.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("LOGIN_SUCCESS")) {
                // Aquí puedes abrir la actividad principal de tu aplicación
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("CORREO", campoUser.getText().toString().trim());
                startActivity(intent);

            } else if (result.equals("LOGIN_FAILED")) {
                Toast.makeText(LoginActivity.this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(LoginActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        }
    }
}