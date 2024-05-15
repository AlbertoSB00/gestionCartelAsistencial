package com.example.gestioncartelasistencial.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gestioncartelasistencial.R;
import com.example.gestioncartelasistencial.activities.InsertActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AdminFragment extends Fragment {

    private EditText campoUser;
    private EditText campoPassword;

    public AdminFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        // Inicializar las vistas
        campoUser = view.findViewById(R.id.campoUser);
        campoPassword = view.findViewById(R.id.campoPassword);
        Button botonSiguiente = view.findViewById(R.id.botonSiguiente);

        // Al pulsar "Siguiente"
        botonSiguiente.setOnClickListener(v -> {
            String user = campoUser.getText().toString().trim();
            String password = campoPassword.getText().toString().trim();
            String passwordHashed = cifrarPassword(password);

            // Validamos usuario y contraseña
            if (user.isEmpty() || password.isEmpty()) {
                Toast.makeText(getActivity(), "Por favor, rellene todos los campos.", Toast.LENGTH_SHORT).show();
                return;
            }

            ordenServer(user, passwordHashed);
        });

        // Al pulsar ¿Olvidaste la contraseña?
        TextView textoOlvidastePassword = view.findViewById(R.id.textoOlvidastePassword);
        textoOlvidastePassword.setOnClickListener(v -> Toast.makeText(getActivity(), "Para recuperar la contraseña, contacta con el administrador", Toast.LENGTH_SHORT).show());

        return view;
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

    private void ordenServer(String user, String passwordHashed) {
        new AuthenticationTask().execute("ADMIN", user, passwordHashed);
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
            if (result.equals("ADMIN_SUCCESS")) {
                // Aquí puedes abrir la actividad principal de tu aplicación
                Intent intent = new Intent(getActivity(), InsertActivity.class);
                startActivity(intent);

            } else if (result.equals("ADMIN_FAILED")) {
                Toast.makeText(getActivity(), "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getActivity(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        }
    }
}