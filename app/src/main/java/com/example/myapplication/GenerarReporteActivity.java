package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class GenerarReporteActivity extends AppCompatActivity {

    EditText edtFechaInicio;
    EditText edtFechaFin;

    Spinner spinnerTipoResiduo;
    Spinner spinnerVolumen;

    Button btnGenerarReporte;
    TextView btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_generar_reporte);

        edtFechaInicio =
                findViewById(R.id.edtFechaInicio);

        edtFechaFin =
                findViewById(R.id.edtFechaFin);

        spinnerTipoResiduo =
                findViewById(R.id.spinnerTipoResiduo);

        spinnerVolumen =
                findViewById(R.id.spinnerVolumen);

        btnGenerarReporte =
                findViewById(R.id.btnGenerarReporte);

        btnVolver =
                findViewById(R.id.btnVolver);

        configurarSpinners();

        colocarFechasIniciales();

        // ==========================================
        // SELECCIONAR FECHA INICIO
        // ==========================================
        edtFechaInicio.setOnClickListener(v -> {

            seleccionarFecha(edtFechaInicio);

        });

        // ==========================================
        // SELECCIONAR FECHA FIN
        // ==========================================
        edtFechaFin.setOnClickListener(v -> {

            seleccionarFecha(edtFechaFin);

        });

        // ==========================================
        // VOLVER
        // ==========================================
        btnVolver.setOnClickListener(v -> {

            finish();

        });

        // ==========================================
        // GENERAR REPORTE
        // ==========================================
        btnGenerarReporte.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            GenerarReporteActivity.this,
                            ReporteActivity.class
                    );

            intent.putExtra(
                    "fecha_inicio",
                    edtFechaInicio.getText().toString()
            );

            intent.putExtra(
                    "fecha_fin",
                    edtFechaFin.getText().toString()
            );

            intent.putExtra(
                    "tipo_residuo",
                    spinnerTipoResiduo
                            .getSelectedItem()
                            .toString()
            );

            intent.putExtra(
                    "volumen",
                    spinnerVolumen
                            .getSelectedItem()
                            .toString()
            );

            startActivity(intent);

        });
    }

    // ==========================================
    // CONFIGURAR SPINNERS
    // ==========================================
    private void configurarSpinners() {

        String[] tiposResiduo = {
                "Todos",
                "Plástico",
                "Cartón",
                "Vidrio",
                "Papel",
                "Metal",
                "Orgánico",
                "Otros"
        };

        ArrayAdapter<String> adapterResiduo =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        tiposResiduo
                );

        adapterResiduo.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerTipoResiduo.setAdapter(
                adapterResiduo
        );


        String[] volumenes = {
                "Todos",
                "0 - 10 kg",
                "11 - 50 kg",
                "Más de 50 kg"
        };

        ArrayAdapter<String> adapterVolumen =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        volumenes
                );

        adapterVolumen.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerVolumen.setAdapter(
                adapterVolumen
        );
    }

    // ==========================================
    // COLOCAR FECHAS INICIALES
    // ==========================================
    private void colocarFechasIniciales() {

        Calendar calendario =
                Calendar.getInstance();

        SimpleDateFormat formato =
                new SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                );

        edtFechaInicio.setText(
                formato.format(
                        calendario.getTime()
                )
        );

        edtFechaFin.setText(
                formato.format(
                        calendario.getTime()
                )
        );
    }

    // ==========================================
    // SELECCIONAR FECHA
    // ==========================================
    private void seleccionarFecha(EditText campoFecha) {

        Calendar calendario =
                Calendar.getInstance();

        DatePickerDialog dialog =
                new DatePickerDialog(
                        this,
                        (view, year, month, dayOfMonth) -> {

                            String fecha =
                                    String.format(
                                            Locale.getDefault(),
                                            "%02d/%02d/%04d",
                                            dayOfMonth,
                                            month + 1,
                                            year
                                    );

                            campoFecha.setText(fecha);

                        },
                        calendario.get(Calendar.YEAR),
                        calendario.get(Calendar.MONTH),
                        calendario.get(Calendar.DAY_OF_MONTH)
                );

        dialog.show();
    }
}