package com.example.gestioncartelasistencial.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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

public class RegisterActivity extends AppCompatActivity {

    private EditText campoNombre;
    private EditText campoUser;
    private EditText campoPassword;
    private EditText campoRepitePassword;
    private CheckBox campoPrivacidad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ImageView volver = findViewById(R.id.volver);

        campoNombre = findViewById(R.id.campoNombre);
        campoUser = findViewById(R.id.campoUser);
        campoPassword = findViewById(R.id.campoPassword);
        campoRepitePassword = findViewById(R.id.campoRepitePassword);
        campoPrivacidad = findViewById(R.id.campoPrivacidad);
        Button botonSiguiente = findViewById(R.id.botonSiguiente);

        // Al pulsar "Volver".
        volver.setOnClickListener(v -> {
           Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
           startActivity(intent);
           finish();
        });

        // Al pulsar "Siguiente".
        botonSiguiente.setOnClickListener(v -> {
            String nombre = campoNombre.getText().toString().trim();
            String user = campoUser.getText().toString().trim();
            String password = campoPassword.getText().toString().trim();
            String repitePassword = campoRepitePassword.getText().toString().trim();
            String hashedPassword = cifrarPassword(password);
            boolean privacidad = campoPrivacidad.isChecked();

            // Validamos que los campos no estén vacíos.
            if (nombre.isEmpty() || user.isEmpty() || password.isEmpty() || repitePassword.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Por favor, rellene todos los campos.", Toast.LENGTH_SHORT).show();

            } else if (!privacidad) {
                Toast.makeText(this, "Por favor, lea y acepte la política de privacidad.", Toast.LENGTH_SHORT).show();

            } else if (!password.equals(repitePassword)) {
                Toast.makeText(RegisterActivity.this, "Las contraseña no coinciden.", Toast.LENGTH_SHORT).show();

            } else {
                ordenServer(nombre, user, hashedPassword);
            }
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

    private void ordenServer(String nombre, String user, String passwordHashed) {
        new AuthenticationTask().execute("REGISTER", nombre, user, passwordHashed);
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
                out.println(strings[3]);

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
            if (result.equals("REGISTER_SUCCESS")) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.putExtra("CORREO", campoUser.getText().toString().trim());
                startActivity(intent);

            } else if (result.equals("REGISTER_FAILED")) {
                Toast.makeText(RegisterActivity.this, "Algo ha ido mal...", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(RegisterActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        }
    }

}