package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.database.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText etUsuario;
    TextInputEditText etContrasena;
    CheckBox cbRecordar;
    Button btnIniciarSesion;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsuario = findViewById(R.id.etUsuario);
        etContrasena = findViewById(R.id.etContrasena);
        cbRecordar = findViewById(R.id.cbRecordar);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);

        db = new DatabaseHelper(this);

        btnIniciarSesion.setOnClickListener(v -> iniciarSesion());
    }

    private void iniciarSesion() {

        String usuario = etUsuario.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(
                    this,
                    "Completa usuario y contraseña",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String nombre = db.validarUsuario(usuario, contrasena);

        if (nombre != null) {

            getSharedPreferences("sesion", MODE_PRIVATE)
                    .edit()
                    .putString("nombre", nombre)
                    .apply();

            Intent intent = new Intent(
                    LoginActivity.this,
                    MainActivity.class
            );

            startActivity(intent);
            finish();

        } else {

            Toast.makeText(
                    this,
                    "Usuario o contraseña incorrectos",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}