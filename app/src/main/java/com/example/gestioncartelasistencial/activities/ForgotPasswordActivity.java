package com.example.gestioncartelasistencial.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gestioncartelasistencial.R;
import com.example.gestioncartelasistencial.connection.ConnectionSQL;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ForgotPasswordActivity extends AppCompatActivity {

    ConnectionSQL connection;
    Connection connect;

    EditText campoEmail;
    EditText campoPassword;
    EditText campoRepitePassword;
    LinearLayout layoutPasswordFields;
    Button botonSiguiente;
    Button botonSiguiente2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        connection = new ConnectionSQL();

        campoEmail = findViewById(R.id.campoUser);
        campoPassword = findViewById(R.id.campoPassword);
        campoRepitePassword = findViewById(R.id.campoRepitePassword);

        layoutPasswordFields = findViewById(R.id.layoutPasswordFields);
        layoutPasswordFields.setVisibility(View.GONE);

        botonSiguiente = findViewById(R.id.botonSiguiente);
        botonSiguiente2 = findViewById(R.id.botonSiguiente2);


        botonSiguiente.setOnClickListener(v -> {
            String email = campoEmail.getText().toString();
            if (email.isEmpty()) {
                Toast.makeText(ForgotPasswordActivity.this, "Por favor, ingrese su correo electrónico", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verificar si el correo electrónico existe en la base de datos
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                try {
                    connect = connection.CONN();
                    if (connect != null) {
                        String query = "SELECT correo FROM usuario WHERE correo = ?";
                        try (PreparedStatement preparedStatement = connect.prepareStatement(query)) {
                            preparedStatement.setString(1, email);
                            ResultSet resultSet = preparedStatement.executeQuery();

                            if (resultSet.next()) {
                                runOnUiThread(() -> {
                                    layoutPasswordFields.setVisibility(View.VISIBLE);

                                    botonSiguiente.setVisibility(View.INVISIBLE);
                                    botonSiguiente2.setVisibility(View.VISIBLE);

                                });
                            } else {
                                runOnUiThread(() -> Toast.makeText(ForgotPasswordActivity.this, "Este correo no está registrado en la base de datos", Toast.LENGTH_SHORT).show());
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else {
                        runOnUiThread(() -> Toast.makeText(ForgotPasswordActivity.this, "Error al conectar con la base de datos", Toast.LENGTH_SHORT).show());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });
        });

        // Restablece la contraseña en la bd.
        botonSiguiente2.setOnClickListener(v -> {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                try {
                    connect = connection.CONN();
                    if (connect != null) {
                        String email = campoEmail.getText().toString();
                        String password = campoPassword.getText().toString();
                        String repeatPassword = campoRepitePassword.getText().toString();

                        if (!password.equals(repeatPassword)) {
                            runOnUiThread(() -> Toast.makeText(ForgotPasswordActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show());
                            return;
                        }

                        String hashedPassword = cifrarPassword(password);

                        String query = "UPDATE usuario SET contraseña = ? WHERE correo = ?";
                        try (PreparedStatement preparedStatement = connect.prepareStatement(query)) {
                            preparedStatement.setString(1, hashedPassword);
                            preparedStatement.setString(2, email);

                            int rowsAffected = preparedStatement.executeUpdate();
                            if (rowsAffected > 0) {
                                runOnUiThread(() -> {
                                    Toast.makeText(ForgotPasswordActivity.this, "Contraseña actualizada exitosamente", Toast.LENGTH_SHORT).show();
                                    finish();
                                });
                            } else {
                                runOnUiThread(() -> Toast.makeText(ForgotPasswordActivity.this, "No se pudo actualizar la contraseña", Toast.LENGTH_SHORT).show());
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else {
                        runOnUiThread(() -> Toast.makeText(ForgotPasswordActivity.this, "Error al conectar con la bd", Toast.LENGTH_SHORT).show());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });
        });

    }

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
}
