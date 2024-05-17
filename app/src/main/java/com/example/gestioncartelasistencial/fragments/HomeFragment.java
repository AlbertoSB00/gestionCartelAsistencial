package com.example.gestioncartelasistencial.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.Calendar;

import com.example.gestioncartelasistencial.R;

public class HomeFragment extends Fragment {

    private static final String ARG_EMAIL = "email";

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String email) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Obtener la referencia del TextView del layout
        TextView welcomeTextView = view.findViewById(R.id.welcome_text);

        // Obtener la hora actual
        int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        // Definir el saludo según la hora
        String greeting;
        if (hourOfDay >= 6 && hourOfDay < 12) {
            greeting = getString(R.string.good_morning);
        } else if (hourOfDay >= 12 && hourOfDay < 18) {
            greeting = getString(R.string.good_afternoon);
        } else {
            greeting = getString(R.string.good_evening);
        }

        // Configurar el texto del TextView con el saludo
        welcomeTextView.setText(getString(R.string.welcome, greeting));

        // Obtener el nombre del argumento
        assert getArguments() != null;
        String nombre = getArguments().getString(ARG_EMAIL);

        // Configurar el texto del TextView con el nombre
        TextView nameTextView = view.findViewById(R.id.nombre_text);
        nameTextView.setText(nombre);

        return view;
    }

}
