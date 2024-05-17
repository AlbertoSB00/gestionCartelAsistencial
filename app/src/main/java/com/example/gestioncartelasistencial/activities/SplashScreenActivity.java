package com.example.gestioncartelasistencial.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.gestioncartelasistencial.databinding.ActivitySplashScreenBinding;


@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    private View mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySplashScreenBinding binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtener la vista principal
        mContentView = binding.getRoot();

        // Iniciar la animación
        fadeIn();
    }

    private void fadeIn() {
        // Aplicar la animación de fundido a la vista principal
        mContentView.setAlpha(0f);
        mContentView.animate()
                .alpha(1f)
                .setDuration(1500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // Iniciar la siguiente actividad cuando termine la animación
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    }
                });
    }
}