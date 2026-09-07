package com.example.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.database.DatabaseHelper;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;

public class ReporteActivity extends AppCompatActivity {

    TextView txtPeriodo;
    TextView txtTipo;
    TextView txtVolumen;
    TextView txtTotalKg;
    TextView txtDetalle;

    TextView btnVolver;
    Button btnDescargarPdf;

    DatabaseHelper db;

    double totalKg = 0;

    // Datos para el PDF
    ArrayList<String> nombresResiduos = new ArrayList<>();
    ArrayList<Double> cantidades = new ArrayList<>();
    ArrayList<Integer> recolecciones = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reporte);

        // Base de datos
        db = new DatabaseHelper(this);

        // Referencias
        txtPeriodo = findViewById(R.id.txtPeriodo);
        txtTipo = findViewById(R.id.txtTipo);
        txtVolumen = findViewById(R.id.txtVolumen);
        txtTotalKg = findViewById(R.id.txtTotalKg);
        txtDetalle = findViewById(R.id.txtDetalle);

        btnVolver = findViewById(R.id.btnVolver);
        btnDescargarPdf = findViewById(R.id.btnDescargarPdf);

        // Recibir filtros
        String fechaInicio =
                getIntent().getStringExtra("fecha_inicio");

        String fechaFin =
                getIntent().getStringExtra("fecha_fin");

        String tipoResiduo =
                getIntent().getStringExtra("tipo_residuo");

        String volumen =
                getIntent().getStringExtra("volumen");

        // Valores por defecto
        if (fechaInicio == null) {
            fechaInicio = "";
        }

        if (fechaFin == null) {
            fechaFin = "";
        }

        if (tipoResiduo == null) {
            tipoResiduo = "Todos";
        }

        if (volumen == null) {
            volumen = "Todos";
        }

        // Mostrar filtros
        txtPeriodo.setText(
                fechaInicio + " - " + fechaFin
        );

        txtTipo.setText(tipoResiduo);

        txtVolumen.setText(volumen);

        // Cargar información desde SQLite
        cargarReporte(
                fechaInicio,
                fechaFin,
                tipoResiduo,
                volumen
        );

        // Volver
        btnVolver.setOnClickListener(v -> finish());

        // Descargar PDF
        btnDescargarPdf.setOnClickListener(v -> generarPDF());
    }

    // =========================================================
    // CARGAR REPORTE DESDE SQLITE
    // =========================================================

    private void cargarReporte(
            String fechaInicio,
            String fechaFin,
            String tipoResiduo,
            String volumen) {

        Cursor cursor = null;

        try {

            cursor = db.obtenerReporte(
                    fechaInicio,
                    fechaFin,
                    tipoResiduo,
                    volumen
            );

            totalKg = 0;

            nombresResiduos.clear();
            cantidades.clear();
            recolecciones.clear();

            StringBuilder detalle =
                    new StringBuilder();

            if (!cursor.moveToFirst()) {

                txtTotalKg.setText("0.00 kg");

                txtDetalle.setText(
                        "No se encontraron residuos para los filtros seleccionados."
                );

                return;
            }

            do {

                String residuo =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "residuo"
                                )
                        );

                double cantidad =
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow(
                                        "total_kg"
                                )
                        );

                int cantidadRecolecciones =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "recolecciones"
                                )
                        );

                // Guardar datos para PDF
                nombresResiduos.add(residuo);
                cantidades.add(cantidad);
                recolecciones.add(
                        cantidadRecolecciones
                );

                // Sumar total
                totalKg += cantidad;

                // Detalle en pantalla
                detalle.append("♻ ")
                        .append(residuo)
                        .append("\n");

                detalle.append("   Cantidad: ")
                        .append(
                                String.format(
                                        Locale.getDefault(),
                                        "%.2f kg",
                                        cantidad
                                )
                        )
                        .append("\n");

                detalle.append("   Recolecciones: ")
                        .append(cantidadRecolecciones)
                        .append("\n\n");

            } while (cursor.moveToNext());

            // Mostrar total
            txtTotalKg.setText(
                    String.format(
                            Locale.getDefault(),
                            "%.2f kg",
                            totalKg
                    )
            );

            // Mostrar detalle
            txtDetalle.setText(
                    detalle.toString()
            );

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Error al cargar reporte: "
                            + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // =========================================================
    // GENERAR PDF
    // =========================================================

    private void generarPDF() {

        PdfDocument documento =
                new PdfDocument();

        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(
                        595,
                        842,
                        1
                ).create();

        PdfDocument.Page pagina =
                documento.startPage(pageInfo);

        Canvas canvas =
                pagina.getCanvas();

        Paint paint =
                new Paint(Paint.ANTI_ALIAS_FLAG);

        // ==========================================
        // FONDO
        // ==========================================

        canvas.drawColor(Color.WHITE);

        // ==========================================
        // TÍTULO
        // ==========================================

        paint.setColor(Color.rgb(8, 127, 63));
        paint.setTextSize(24);
        paint.setFakeBoldText(true);

        canvas.drawText(
                "REPORTE DE RESIDUOS",
                155,
                55,
                paint
        );

        // Línea debajo del título
        paint.setStrokeWidth(2);

        canvas.drawLine(
                50,
                70,
                545,
                70,
                paint
        );

        // ==========================================
        // INFORMACIÓN
        // ==========================================

        paint.setColor(Color.BLACK);
        paint.setTextSize(13);
        paint.setFakeBoldText(false);

        int y = 105;

        canvas.drawText(
                "Periodo:",
                50,
                y,
                paint
        );

        canvas.drawText(
                txtPeriodo.getText().toString(),
                150,
                y,
                paint
        );

        y += 25;

        canvas.drawText(
                "Tipo de residuo:",
                50,
                y,
                paint
        );

        canvas.drawText(
                txtTipo.getText().toString(),
                150,
                y,
                paint
        );

        y += 25;

        canvas.drawText(
                "Volumen:",
                50,
                y,
                paint
        );

        canvas.drawText(
                txtVolumen.getText().toString(),
                150,
                y,
                paint
        );

        // ==========================================
        // RESUMEN
        // ==========================================

        y += 50;

        paint.setColor(Color.rgb(8, 127, 63));
        paint.setTextSize(17);
        paint.setFakeBoldText(true);

        canvas.drawText(
                "RESUMEN",
                50,
                y,
                paint
        );

        y += 30;

        paint.setColor(Color.BLACK);
        paint.setTextSize(14);
        paint.setFakeBoldText(false);

        canvas.drawText(
                "TOTAL RECOLECTADO",
                50,
                y,
                paint
        );

        y += 30;

        paint.setColor(Color.rgb(8, 127, 63));
        paint.setTextSize(26);
        paint.setFakeBoldText(true);

        canvas.drawText(
                String.format(
                        Locale.getDefault(),
                        "%.2f kg",
                        totalKg
                ),
                50,
                y,
                paint
        );

        // ==========================================
        // DETALLE
        // ==========================================

        y += 55;

        paint.setColor(Color.rgb(8, 127, 63));
        paint.setTextSize(17);
        paint.setFakeBoldText(true);

        canvas.drawText(
                "DETALLE",
                50,
                y,
                paint
        );

        y += 25;

        // ==========================================
        // ENCABEZADO DE TABLA
        // ==========================================

        paint.setColor(Color.LTGRAY);

        canvas.drawRect(
                50,
                y,
                545,
                y + 30,
                paint
        );

        paint.setColor(Color.BLACK);
        paint.setTextSize(12);
        paint.setFakeBoldText(true);

        canvas.drawText(
                "RESIDUO",
                60,
                y + 20,
                paint
        );

        canvas.drawText(
                "CANTIDAD",
                300,
                y + 20,
                paint
        );

        canvas.drawText(
                "RECOLECC.",
                430,
                y + 20,
                paint
        );

        y += 30;

        // ==========================================
        // FILAS
        // ==========================================

        paint.setFakeBoldText(false);
        paint.setTextSize(12);

        for (int i = 0;
             i < nombresResiduos.size();
             i++) {

            // Línea de fila
            paint.setColor(Color.GRAY);

            canvas.drawLine(
                    50,
                    y,
                    545,
                    y,
                    paint
            );

            paint.setColor(Color.BLACK);

            // Nombre residuo
            canvas.drawText(
                    nombresResiduos.get(i),
                    60,
                    y + 20,
                    paint
            );

            // Cantidad
            canvas.drawText(
                    String.format(
                            Locale.getDefault(),
                            "%.2f kg",
                            cantidades.get(i)
                    ),
                    300,
                    y + 20,
                    paint
            );

            // Recolecciones
            canvas.drawText(
                    String.valueOf(
                            recolecciones.get(i)
                    ),
                    455,
                    y + 20,
                    paint
            );

            y += 30;

            // Evitar salir de la página
            if (y > 760) {
                break;
            }
        }

        // ==========================================
        // PIE DE PÁGINA
        // ==========================================

        paint.setColor(Color.GRAY);
        paint.setTextSize(10);
        paint.setFakeBoldText(false);

        canvas.drawText(
                "EcoLim - Sistema de gestión de residuos",
                50,
                810,
                paint
        );

        canvas.drawText(
                "Reporte generado desde la aplicación",
                50,
                825,
                paint
        );

        // Finalizar página
        documento.finishPage(pagina);

        // Guardar
        guardarPDF(documento);
    }

    // =========================================================
    // GUARDAR PDF EN DESCARGAS
    // =========================================================

    private void guardarPDF(
            PdfDocument documento) {

        String nombreArchivo =
                "Reporte_Residuos_" +
                        System.currentTimeMillis() +
                        ".pdf";

        try {

            ContentValues values =
                    new ContentValues();

            values.put(
                    MediaStore.Downloads.DISPLAY_NAME,
                    nombreArchivo
            );

            values.put(
                    MediaStore.Downloads.MIME_TYPE,
                    "application/pdf"
            );

            values.put(
                    MediaStore.Downloads.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS
            );

            Uri uri =
                    getContentResolver().insert(
                            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                            values
                    );

            if (uri != null) {

                OutputStream outputStream =
                        getContentResolver()
                                .openOutputStream(uri);

                documento.writeTo(
                        outputStream
                );

                outputStream.close();

                Toast.makeText(
                        this,
                        "PDF guardado en Descargas",
                        Toast.LENGTH_LONG
                ).show();

                abrirPDF(uri);

            } else {

                Toast.makeText(
                        this,
                        "No se pudo crear el archivo PDF",
                        Toast.LENGTH_LONG
                ).show();
            }

            documento.close();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Error al generar PDF: "
                            + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    // =========================================================
    // ABRIR PDF
    // =========================================================

    private void abrirPDF(Uri uri) {

        try {

            Intent intent =
                    new Intent(
                            Intent.ACTION_VIEW
                    );

            intent.setDataAndType(
                    uri,
                    "application/pdf"
            );

            intent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );

            startActivity(intent);

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "PDF guardado correctamente",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}