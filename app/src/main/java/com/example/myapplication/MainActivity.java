package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.myapplication.database.DatabaseHelper;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // ==========================================
    // DRAWER
    // ==========================================

    DrawerLayout drawerLayout;

    TextView btnMenu;

    TextView menuInicio;
    TextView menuNuevaRecoleccion;
    TextView menuHistorial;
    TextView menuReportes;
    TextView menuTipos;
    TextView menuPerfil;


    // ==========================================
    // BASE DE DATOS
    // ==========================================

    DatabaseHelper db;


    // ==========================================
    // DASHBOARD
    // ==========================================

    TextView txtRecolecciones;
    TextView txtTotalKg;
    TextView txtTipos;
    TextView txtReportes;


    // ==========================================
    // NAVEGACIÓN INFERIOR
    // ==========================================

    TextView navInicio;
    TextView navRecolecciones;
    TextView navNuevaRecoleccion;
    TextView navReportes;
    TextView navPerfil;


    TextView txtNombreUsuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        txtNombreUsuario = findViewById(R.id.txtNombreUsuario);

        String nombre = getSharedPreferences("sesion", MODE_PRIVATE)
                .getString("nombre", "Usuario");

        txtNombreUsuario.setText("Hola, " + nombre);


        // ==========================================
        // BASE DE DATOS
        // ==========================================

        db = new DatabaseHelper(this);


        // ==========================================
        // REFERENCIAS DASHBOARD
        // ==========================================

        txtRecolecciones =
                findViewById(R.id.txtRecolecciones);

        txtTotalKg =
                findViewById(R.id.txtTotalKg);

        txtTipos =
                findViewById(R.id.txtTipos);

        txtReportes =
                findViewById(R.id.txtReportes);


        // ==========================================
        // DRAWER
        // ==========================================

        drawerLayout =
                findViewById(R.id.drawerLayout);

        btnMenu =
                findViewById(R.id.btnMenu);


        // ==========================================
        // OPCIONES DEL MENÚ LATERAL
        // ==========================================

        menuInicio =
                findViewById(R.id.menuInicio);

        menuNuevaRecoleccion =
                findViewById(R.id.menuNuevaRecoleccion);

        menuHistorial =
                findViewById(R.id.menuHistorial);

        menuReportes =
                findViewById(R.id.menuReportes);

        menuTipos =
                findViewById(R.id.menuTipos);

        menuPerfil =
                findViewById(R.id.menuPerfil);


        // ==========================================
        // BOTÓN ☰
        // ==========================================

        btnMenu.setOnClickListener(v -> {

            if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {

                drawerLayout.closeDrawer(Gravity.LEFT);

            } else {

                drawerLayout.openDrawer(Gravity.LEFT);

            }

        });


        // ==========================================
        // MENÚ - INICIO
        // ==========================================

        menuInicio.setOnClickListener(v -> {

            drawerLayout.closeDrawer(Gravity.LEFT);

        });


        // ==========================================
        // MENÚ - NUEVA RECOLECCIÓN
        // ==========================================

        menuNuevaRecoleccion.setOnClickListener(v -> {

            drawerLayout.closeDrawer(Gravity.LEFT);

            abrirNuevaRecoleccion();

        });


        // ==========================================
        // MENÚ - HISTORIAL
        // ==========================================

        menuHistorial.setOnClickListener(v -> {

            drawerLayout.closeDrawer(Gravity.LEFT);

            abrirHistorial();

        });


        // ==========================================
        // MENÚ - REPORTES
        // ==========================================

        menuReportes.setOnClickListener(v -> {

            drawerLayout.closeDrawer(Gravity.LEFT);

            abrirReportes();

        });


        // ==========================================
        // MENÚ - TIPOS
        // ==========================================

        menuTipos.setOnClickListener(v -> {

            drawerLayout.closeDrawer(Gravity.LEFT);

            Toast.makeText(
                    this,
                    "Tipos de residuos próximamente",
                    Toast.LENGTH_SHORT
            ).show();

        });


        // ==========================================
        // MENÚ - PERFIL
        // ==========================================

        menuPerfil.setOnClickListener(v -> {

            drawerLayout.closeDrawer(Gravity.LEFT);

            Toast.makeText(
                    this,
                    "Perfil próximamente",
                    Toast.LENGTH_SHORT
            ).show();

        });


        // ==========================================
        // NAVEGACIÓN INFERIOR
        // ==========================================

        navInicio =
                findViewById(R.id.navInicio);

        navRecolecciones =
                findViewById(R.id.navRecolecciones);

        navNuevaRecoleccion =
                findViewById(R.id.navNuevaRecoleccion);

        navReportes =
                findViewById(R.id.navReportes);

        navPerfil =
                findViewById(R.id.navPerfil);


        // ==========================================
        // ACCESOS RÁPIDOS
        // ==========================================

        findViewById(R.id.btnNuevaRecoleccion)
                .setOnClickListener(v -> {

                    abrirNuevaRecoleccion();

                });


        findViewById(R.id.btnHistorial)
                .setOnClickListener(v -> {

                    abrirHistorial();

                });


        findViewById(R.id.btnReportes)
                .setOnClickListener(v -> {

                    abrirReportes();

                });

//BARRA INFERIOR
        navInicio.setOnClickListener(v -> {
            animarNav(v);
        });

        navRecolecciones.setOnClickListener(v -> {
            animarNav(v);
            abrirHistorial();
        });

        navNuevaRecoleccion.setOnClickListener(v -> {
            animarNav(v);
            abrirNuevaRecoleccion();
        });

        navReportes.setOnClickListener(v -> {
            animarNav(v);
            abrirReportes();
        });

        navPerfil.setOnClickListener(v -> {
            animarNav(v);

            Toast.makeText(this,
                    "Perfil próximamente",
                    Toast.LENGTH_SHORT).show();
        });

        // ==========================================
        // MENSAJE
        // ==========================================

        Toast.makeText(
                this,
                "Base de datos SQLite creada correctamente",
                Toast.LENGTH_SHORT
        ).show();

    }


    // ==========================================
    // NUEVA RECOLECCIÓN
    // ==========================================

    private void abrirNuevaRecoleccion() {

        Intent intent = new Intent(
                MainActivity.this,
                NuevaRecoleccionActivity.class
        );

        startActivity(intent);

    }


    // ==========================================
    // HISTORIAL
    // ==========================================

    private void abrirHistorial() {

        Intent intent = new Intent(
                MainActivity.this,
                HistorialActivity.class
        );

        startActivity(intent);

    }


    // ==========================================
    // REPORTES
    // ==========================================

    private void abrirReportes() {

        Intent intent = new Intent(
                MainActivity.this,
                GenerarReporteActivity.class
        );

        startActivity(intent);

    }


    // ==========================================
    // ACTUALIZAR DASHBOARD
    // ==========================================

    @Override
    protected void onResume() {

        super.onResume();

        cargarDatosDashboard();

    }


    private void cargarDatosDashboard() {

        // Cantidad de recolecciones

        int cantidadRecolecciones =
                db.obtenerCantidadRecolecciones();

        txtRecolecciones.setText(
                String.valueOf(cantidadRecolecciones)
        );


        // Total de kilogramos

        double totalKg =
                db.obtenerTotalKg();

        txtTotalKg.setText(
                String.format(
                        Locale.getDefault(),
                        "%.0f kg",
                        totalKg
                )
        );


        // Cantidad de tipos

        int cantidadTipos =
                db.obtenerCantidadTiposResiduos();

        txtTipos.setText(
                String.valueOf(cantidadTipos)
        );


        // Reportes

        txtReportes.setText("0");

    }
    private void animarNav(android.view.View vista) {

        vista.animate()
                .scaleX(1.10f)
                .scaleY(1.10f)
                .translationY(-5f)
                .setDuration(150)
                .withEndAction(() -> {

                    vista.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .translationY(0f)
                            .setDuration(150)
                            .start();

                })
                .start();
    }

}