package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class NuevaRecoleccionActivity extends AppCompatActivity {

    EditText edtFecha;
    EditText edtHora;
    EditText edtObservacion;
    EditText edtCantidad;

    Spinner spinnerResiduo;

    TextView txtListaResiduos;
    TextView txtTotal;

    Button btnAgregarResiduo;
    Button btnGuardar;

    DatabaseHelper db;

    ArrayList<String> residuos;
    ArrayList<Double> cantidades;

    double total = 0;

    // VARIABLES PARA EDITAR
    boolean modoEditar = false;
    int recoleccionId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nueva_recoleccion);

        db = new DatabaseHelper(this);

        // Referencias
        edtFecha = findViewById(R.id.edtFecha);
        edtHora = findViewById(R.id.edtHora);
        edtObservacion = findViewById(R.id.edtObservacion);
        edtCantidad = findViewById(R.id.edtCantidad);

        spinnerResiduo = findViewById(R.id.spinnerResiduo);

        txtListaResiduos = findViewById(R.id.txtListaResiduos);
        txtTotal = findViewById(R.id.txtTotal);

        btnAgregarResiduo = findViewById(R.id.btnAgregarResiduo);
        btnGuardar = findViewById(R.id.btnGuardar);

        residuos = new ArrayList<>();
        cantidades = new ArrayList<>();


        // COMPROBAR SI ESTAMOS EDITANDO
        modoEditar =
                getIntent().getBooleanExtra(
                        "modo_editar",
                        false
                );

        recoleccionId =
                getIntent().getIntExtra(
                        "recoleccion_id",
                        -1
                );


        // FECHA Y HORA ACTUAL
        Calendar calendario = Calendar.getInstance();

        SimpleDateFormat formatoFecha =
                new SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                );

        edtFecha.setText(
                formatoFecha.format(
                        calendario.getTime()
                )
        );


        SimpleDateFormat formatoHora =
                new SimpleDateFormat(
                        "HH:mm",
                        Locale.getDefault()
                );

        edtHora.setText(
                formatoHora.format(
                        calendario.getTime()
                )
        );


        // SPINNER
        String[] tiposResiduo = {
                "Plástico",
                "Cartón",
                "Vidrio",
                "Papel",
                "Metal",
                "Orgánico",
                "Otros"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        tiposResiduo
                );

        spinnerResiduo.setAdapter(adapter);


        // SI ES EDICIÓN, CARGAR DATOS
        if (modoEditar && recoleccionId != -1) {

            cargarRecoleccion();
        }


        // SELECTOR DE FECHA
        edtFecha.setOnClickListener(v -> {

            Calendar fecha = Calendar.getInstance();

            DatePickerDialog dialog =
                    new DatePickerDialog(
                            this,
                            (view, year, month, dayOfMonth) -> {

                                String fechaSeleccionada =
                                        String.format(
                                                Locale.getDefault(),
                                                "%02d/%02d/%04d",
                                                dayOfMonth,
                                                month + 1,
                                                year
                                        );

                                edtFecha.setText(
                                        fechaSeleccionada
                                );
                            },
                            fecha.get(Calendar.YEAR),
                            fecha.get(Calendar.MONTH),
                            fecha.get(Calendar.DAY_OF_MONTH)
                    );

            dialog.show();
        });


        // SELECTOR DE HORA
        edtHora.setOnClickListener(v -> {

            Calendar hora = Calendar.getInstance();

            TimePickerDialog dialog =
                    new TimePickerDialog(
                            this,
                            (view, hourOfDay, minute) -> {

                                String horaSeleccionada =
                                        String.format(
                                                Locale.getDefault(),
                                                "%02d:%02d",
                                                hourOfDay,
                                                minute
                                        );

                                edtHora.setText(
                                        horaSeleccionada
                                );
                            },
                            hora.get(Calendar.HOUR_OF_DAY),
                            hora.get(Calendar.MINUTE),
                            true
                    );

            dialog.show();
        });


        // AGREGAR RESIDUO
        btnAgregarResiduo.setOnClickListener(v -> {

            agregarResiduo();

        });


        // VOLVER
        findViewById(R.id.btnVolver).setOnClickListener(v -> {

            finish();

        });


        // GUARDAR
        btnGuardar.setOnClickListener(v -> {

            guardarRecoleccion();

        });
    }


    // =========================================================
    // CARGAR RECOLECCIÓN PARA EDITAR
    // =========================================================

    private void cargarRecoleccion() {

        // Cargar datos principales
        Cursor cursor =
                db.obtenerRecoleccion(
                        recoleccionId
                );

        if (cursor.moveToFirst()) {

            String fecha =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "fecha"
                            )
                    );

            String hora =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "hora"
                            )
                    );

            String observacion =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "observacion"
                            )
                    );


            edtFecha.setText(fecha);

            edtHora.setText(hora);

            if (observacion != null) {

                edtObservacion.setText(
                        observacion
                );
            }
        }

        cursor.close();


        // Limpiar listas
        residuos.clear();

        cantidades.clear();

        total = 0;


        // Cargar residuos
        Cursor cursorDetalles =
                db.obtenerDetalleRecoleccion(
                        recoleccionId
                );

        while (cursorDetalles.moveToNext()) {

            String nombre =
                    cursorDetalles.getString(
                            cursorDetalles.getColumnIndexOrThrow(
                                    "nombre"
                            )
                    );

            double cantidad =
                    cursorDetalles.getDouble(
                            cursorDetalles.getColumnIndexOrThrow(
                                    "cantidad_kg"
                            )
                    );


            residuos.add(nombre);

            cantidades.add(cantidad);

            total += cantidad;
        }

        cursorDetalles.close();


        actualizarLista();
    }


    // =========================================================
    // AGREGAR RESIDUO
    // =========================================================

    private void agregarResiduo() {

        String cantidadTexto =
                edtCantidad.getText().toString().trim();

        if (cantidadTexto.isEmpty()) {

            edtCantidad.setError(
                    "Ingrese una cantidad"
            );

            return;
        }


        double cantidad;

        try {

            cantidad =
                    Double.parseDouble(
                            cantidadTexto
                    );

        } catch (NumberFormatException e) {

            edtCantidad.setError(
                    "Ingrese una cantidad válida"
            );

            return;
        }


        if (cantidad <= 0) {

            edtCantidad.setError(
                    "La cantidad debe ser mayor a 0"
            );

            return;
        }


        String residuo =
                spinnerResiduo
                        .getSelectedItem()
                        .toString();


        residuos.add(residuo);

        cantidades.add(cantidad);

        total += cantidad;


        actualizarLista();


        edtCantidad.setText("");
    }


    // =========================================================
    // ACTUALIZAR LISTA
    // =========================================================

    private void actualizarLista() {

        StringBuilder lista =
                new StringBuilder();

        for (int i = 0; i < residuos.size(); i++) {

            lista.append("♻  ")
                    .append(residuos.get(i))
                    .append("     ")
                    .append(cantidades.get(i))
                    .append(" kg\n\n");
        }


        txtListaResiduos.setText(
                lista.toString()
        );


        txtTotal.setText(
                String.format(
                        Locale.getDefault(),
                        "%.2f kg",
                        total
                )
        );
    }


    // =========================================================
    // GUARDAR / ACTUALIZAR
    // =========================================================

    private void guardarRecoleccion() {

        // Verificar que exista al menos un residuo
        if (residuos.isEmpty()) {

            Toast.makeText(
                    this,
                    "Agregue al menos un residuo",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // Obtener datos
        String fecha =
                edtFecha.getText()
                        .toString()
                        .trim();

        String hora =
                edtHora.getText()
                        .toString()
                        .trim();

        String observacion =
                edtObservacion.getText()
                        .toString()
                        .trim();


        // =====================================================
        // MODO EDITAR
        // =====================================================

        if (modoEditar) {

            boolean actualizado =
                    db.actualizarRecoleccion(
                            recoleccionId,
                            fecha,
                            hora,
                            observacion,
                            residuos,
                            cantidades
                    );


            if (actualizado) {

                Toast.makeText(
                        this,
                        "Recolección actualizada correctamente",
                        Toast.LENGTH_LONG
                ).show();

                finish();

            } else {

                Toast.makeText(
                        this,
                        "Error al actualizar la recolección",
                        Toast.LENGTH_LONG
                ).show();
            }

            return;
        }


        // =====================================================
        // MODO NUEVA RECOLECCIÓN
        // =====================================================

        long resultado =
                db.guardarRecoleccion(
                        fecha,
                        hora,
                        observacion,
                        residuos,
                        cantidades
                );


        if (resultado != -1) {

            Toast.makeText(
                    this,
                    "Recolección guardada correctamente",
                    Toast.LENGTH_LONG
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Error al guardar la recolección",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}