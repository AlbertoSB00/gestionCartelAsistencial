package com.example.gestioncartelasistencial.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
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

public class InsertActivity extends AppCompatActivity {

    private EditText campoNIF;
    private EditText campoName;
    private EditText campoSurname;
    private EditText campoPhoneNumber;
    private EditText campoAddress;
    private EditText campoEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);

        // Inicializar las vistas
        ImageView volver = findViewById(R.id.volver);

        campoNIF = findViewById(R.id.campoNIF);
        campoName = findViewById(R.id.campoName);
        campoSurname = findViewById(R.id.campoSurname);
        campoPhoneNumber = findViewById(R.id.campoPhoneNumber);
        campoAddress = findViewById(R.id.campoAddress);
        campoEmail = findViewById(R.id.campoEmail);
        Button botonSiguiente = findViewById(R.id.botonSiguiente);

        // Al pulsar "Volver".
        volver.setOnClickListener(v -> {
            Intent intent = new Intent(InsertActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        botonSiguiente.setOnClickListener(mouseEvent -> {
            // Compruebamos que al menos esté el nif.
            if (campoNIF.getText().toString().isEmpty()) {
                Toast.makeText(InsertActivity.this, "Por favor, rellene el campo NIF.", Toast.LENGTH_SHORT).show();
            }

            String nif = campoNIF.getText().toString();
            String name = campoName.getText().toString();
            String surname = campoSurname.getText().toString();
            String phoneNumber = campoPhoneNumber.getText().toString();
            String address = campoAddress.getText().toString();
            String email = campoEmail.getText().toString();

            orden(nif, name, surname, phoneNumber, address, email);
        });
    }

    private void orden(String nif, String name, String surname, String phoneNumber, String address, String email) {
        new AuthenticationTask().execute("EXIST", nif, name, surname, phoneNumber, address, email);
    }

    private class AuthenticationTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String response;

            try {
                Socket socket = new Socket("192.168.1.10", 12345);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Enviamos orden al servidor.
                out.println(strings[0]);

                // Enviamos nif al servidor.
                out.println(strings[1]);
                out.println(strings[2]);
                out.println(strings[3]);
                out.println(strings[4]);
                out.println(strings[5]);
                out.println(strings[6]);

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
            switch (result) {
                case "EXIST_SUCCESS":
                    Toast.makeText(InsertActivity.this, "Paciente insertado", Toast.LENGTH_SHORT).show();
                    campoNIF.setText("");
                    campoName.setText("");
                    campoSurname.setText("");
                    campoPhoneNumber.setText("");
                    campoAddress.setText("");
                    campoEmail.setText("");
                    break;

                case "EXIST_FAILED":
                    Toast.makeText(InsertActivity.this, "Este paciente ya existe", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    Toast.makeText(InsertActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}