package com.example.myapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.database.DatabaseHelper;

public class DetalleRecoleccionActivity extends AppCompatActivity {

    DatabaseHelper db;

    Button btnEliminar;

    int recoleccionId;
    Button btnEditar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detalle_recoleccion);

        // Base de datos
        db = new DatabaseHelper(this);

        // Obtener el ID enviado desde HistorialActivity
        recoleccionId =
                getIntent().getIntExtra(
                        "recoleccion_id",
                        -1
                );

        // Botón eliminar
        btnEliminar = findViewById(R.id.btnEliminar);
        btnEditar = findViewById(R.id.btnEditar);

        btnEditar.setOnClickListener(v -> {

            Intent intent = new Intent(
                    DetalleRecoleccionActivity.this,
                    NuevaRecoleccionActivity.class
            );

            intent.putExtra(
                    "modo_editar",
                    true
            );

            intent.putExtra(
                    "recoleccion_id",
                    recoleccionId
            );

            startActivity(intent);
        });

        btnEliminar.setOnClickListener(v -> {

            new AlertDialog.Builder(
                    DetalleRecoleccionActivity.this
            )
                    .setTitle("Eliminar recolección")
                    .setMessage(
                            "¿Estás seguro de que deseas eliminar esta recolección?"
                    )
                    .setPositiveButton(
                            "Sí, eliminar",
                            (dialog, which) -> {

                                boolean eliminado =
                                        db.eliminarRecoleccion(
                                                recoleccionId
                                        );

                                if (eliminado) {

                                    Toast.makeText(
                                            DetalleRecoleccionActivity.this,
                                            "Recolección eliminada correctamente",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    // Regresar al historial
                                    finish();

                                } else {

                                    Toast.makeText(
                                            DetalleRecoleccionActivity.this,
                                            "No se pudo eliminar la recolección",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }
                    )
                    .setNegativeButton(
                            "Cancelar",
                            null
                    )
                    .show();
        });
    }
}