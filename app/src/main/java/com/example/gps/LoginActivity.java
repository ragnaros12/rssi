package com.example.gps;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Toast;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.gps.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.containedButton.setOnClickListener(v -> {
            if(!binding.loginField.getEditText().getText().toString().equals("admin")
             || !binding.passwordField.getEditText().getText().toString().equals("admin")){
                Toast.makeText(getApplicationContext(), "Login or password is wrong", Toast.LENGTH_LONG).show();
                return;
            }

            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        });

    }
}