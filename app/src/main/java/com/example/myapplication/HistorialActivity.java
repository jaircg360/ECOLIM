package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.database.DatabaseHelper;

import java.util.Locale;

public class HistorialActivity extends AppCompatActivity {

    DatabaseHelper db;

    LinearLayout contenedorRecolecciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_historial);

        db = new DatabaseHelper(this);

        contenedorRecolecciones =
                findViewById(R.id.contenedorRecolecciones);


        // VOLVER
        findViewById(R.id.btnVolver)
                .setOnClickListener(v -> finish());


        // INICIO
        findViewById(R.id.navInicio)
                .setOnClickListener(v -> {

                    Intent intent =
                            new Intent(
                                    HistorialActivity.this,
                                    MainActivity.class
                            );

                    intent.setFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
                    );

                    startActivity(intent);
                });


        // NUEVA RECOLECCIÓN
        findViewById(R.id.navAgregar)
                .setOnClickListener(v -> {

                    Intent intent =
                            new Intent(
                                    HistorialActivity.this,
                                    NuevaRecoleccionActivity.class
                            );

                    startActivity(intent);
                });


        cargarHistorial();
    }


    @Override
    protected void onResume() {

        super.onResume();

        cargarHistorial();
    }


    private void cargarHistorial() {

        contenedorRecolecciones.removeAllViews();


        Cursor cursor =
                db.obtenerHistorial();


        if (cursor.getCount() == 0) {

            TextView vacio =
                    new TextView(this);

            vacio.setText(
                    "No hay recolecciones registradas"
            );

            vacio.setTextSize(16);

            vacio.setTextColor(
                    android.graphics.Color.GRAY
            );

            vacio.setGravity(Gravity.CENTER);

            vacio.setPadding(
                    20,
                    50,
                    20,
                    50
            );

            contenedorRecolecciones
                    .addView(vacio);

            cursor.close();

            return;
        }


        while (cursor.moveToNext()) {

            int id =
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow("id")
                    );

            String fecha =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("fecha")
                    );

            String hora =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("hora")
                    );

            String estado =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow("estado")
                    );

            int cantidadResiduos =
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    "cantidad_residuos"
                            )
                    );

            double totalKg =
                    cursor.getDouble(
                            cursor.getColumnIndexOrThrow(
                                    "total_kg"
                            )
                    );


            crearTarjeta(
                    id,
                    fecha,
                    hora,
                    estado,
                    cantidadResiduos,
                    totalKg
            );
        }


        cursor.close();
    }


    private void crearTarjeta(
            int id,
            String fecha,
            String hora,
            String estado,
            int cantidadResiduos,
            double totalKg) {


        // TARJETA PRINCIPAL
        LinearLayout tarjeta =
                new LinearLayout(this);

        tarjeta.setOrientation(
                LinearLayout.VERTICAL
        );

        tarjeta.setPadding(
                16,
                14,
                16,
                14
        );

        tarjeta.setBackgroundColor(
                android.graphics.Color.WHITE
        );


        LinearLayout.LayoutParams parametros =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        parametros.setMargins(
                0,
                0,
                0,
                10
        );

        tarjeta.setLayoutParams(parametros);


        // PRIMERA FILA
        LinearLayout fila =
                new LinearLayout(this);

        fila.setOrientation(
                LinearLayout.HORIZONTAL
        );

        fila.setGravity(
                Gravity.CENTER_VERTICAL
        );


        // FECHA Y HORA
        TextView txtFecha =
                new TextView(this);

        txtFecha.setLayoutParams(
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        txtFecha.setText(
                fecha + "   " + hora
        );

        txtFecha.setTextColor(
                android.graphics.Color.DKGRAY
        );

        txtFecha.setTextSize(14);


        // FLECHA
        TextView flecha =
                new TextView(this);

        flecha.setText("›");

        flecha.setTextSize(28);

        flecha.setTextColor(
                android.graphics.Color.GRAY
        );


        fila.addView(txtFecha);
        fila.addView(flecha);


        // SEGUNDA FILA
        LinearLayout filaDatos =
                new LinearLayout(this);

        filaDatos.setOrientation(
                LinearLayout.HORIZONTAL
        );

        filaDatos.setGravity(
                Gravity.CENTER_VERTICAL
        );


        TextView txtResiduos =
                new TextView(this);

        txtResiduos.setLayoutParams(
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        txtResiduos.setText(
                cantidadResiduos +
                        " residuos"
        );

        txtResiduos.setTextColor(
                android.graphics.Color.GRAY
        );

        txtResiduos.setTextSize(14);


        TextView txtKg =
                new TextView(this);

        txtKg.setText(
                String.format(
                        Locale.getDefault(),
                        "%.0f kg",
                        totalKg
                )
        );

        txtKg.setTextColor(
                android.graphics.Color.DKGRAY
        );

        txtKg.setTextSize(15);

        txtKg.setGravity(
                Gravity.END
        );


        filaDatos.addView(txtResiduos);
        filaDatos.addView(txtKg);


        // ESTADO
        TextView txtEstado =
                new TextView(this);

        txtEstado.setText(
                estado
        );

        txtEstado.setTextColor(
                android.graphics.Color.rgb(
                        30,
                        130,
                        70
                )
        );

        txtEstado.setTextSize(12);

        txtEstado.setPadding(
                0,
                6,
                0,
                0
        );


        tarjeta.addView(fila);
        tarjeta.addView(filaDatos);
        tarjeta.addView(txtEstado);


        // CLICK EN LA TARJETA
        tarjeta.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            HistorialActivity.this,
                            DetalleRecoleccionActivity.class
                    );

            intent.putExtra(
                    "recoleccion_id",
                    id
            );

            startActivity(intent);
        });


        contenedorRecolecciones
                .addView(tarjeta);
    }
}