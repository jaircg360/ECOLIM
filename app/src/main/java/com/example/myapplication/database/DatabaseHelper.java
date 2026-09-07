package com.example.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ecolim.db";
    private static final int DATABASE_VERSION = 2;

    // TABLAS
    public static final String TABLE_RESIDUOS = "residuos";
    public static final String TABLE_RECOLECCIONES = "recolecciones";
    public static final String TABLE_DETALLE = "detalle_recoleccion";
    public static final String TABLE_USUARIOS = "usuarios";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // TABLA RESIDUOS
        db.execSQL(
                "CREATE TABLE " + TABLE_RESIDUOS + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "nombre TEXT NOT NULL, " +
                        "descripcion TEXT)"
        );

        // TABLA RECOLECCIONES
        db.execSQL(
                "CREATE TABLE " + TABLE_RECOLECCIONES + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "fecha TEXT NOT NULL, " +
                        "hora TEXT NOT NULL, " +
                        "observacion TEXT, " +
                        "estado TEXT NOT NULL)"
        );

        // TABLA DETALLE
        db.execSQL(
                "CREATE TABLE " + TABLE_DETALLE + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "recoleccion_id INTEGER NOT NULL, " +
                        "residuo_id INTEGER NOT NULL, " +
                        "cantidad_kg REAL NOT NULL, " +
                        "FOREIGN KEY(recoleccion_id) " +
                        "REFERENCES recolecciones(id), " +
                        "FOREIGN KEY(residuo_id) " +
                        "REFERENCES residuos(id))"
        );
        // TABLA USUARIOS
        db.execSQL(
                "CREATE TABLE " + TABLE_USUARIOS + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "usuario TEXT NOT NULL UNIQUE, " +
                        "nombre TEXT NOT NULL, " +
                        "contrasena TEXT NOT NULL)"
        );
        //INSERTAR USUARIOSS
        insertarUsuario(
                db,
                "Kevin01",
                "Kevin Capuñay Gamarra",
                "Kevin0101"
        );

        insertarUsuario(
                db,
                "Pedro02",
                "Pedro Mendiola Caro",
                "Pedro0202"
        );

        insertarUsuario(
                db,
                "Wilmer03",
                "Wilmer Huaripata Llanos",
                "Wilmer0303"
        );

        // INSERTAR TIPOS DE RESIDUOS
        insertarResiduo(
                db,
                "Plástico",
                "Materiales plásticos"
        );

        insertarResiduo(
                db,
                "Cartón",
                "Envases y cajas de cartón"
        );

        insertarResiduo(
                db,
                "Vidrio",
                "Botellas y frascos de vidrio"
        );

        insertarResiduo(
                db,
                "Papel",
                "Hojas, periódicos, revistas"
        );

        insertarResiduo(
                db,
                "Metal",
                "Latas, aluminio, hierro"
        );

        insertarResiduo(
                db,
                "Orgánico",
                "Restos de comida, vegetales"
        );

        insertarResiduo(
                db,
                "Otros",
                "Otros residuos"
        );
    }

    private void insertarResiduo(
            SQLiteDatabase db,
            String nombre,
            String descripcion) {

        ContentValues values = new ContentValues();

        values.put("nombre", nombre);
        values.put("descripcion", descripcion);

        db.insert(
                TABLE_RESIDUOS,
                null,
                values
        );
    }
    private void insertarUsuario(
            SQLiteDatabase db,
            String usuario,
            String nombre,
            String contrasena) {

        ContentValues values = new ContentValues();

        values.put("usuario", usuario);
        values.put("nombre", nombre);
        values.put("contrasena", contrasena);

        db.insert(
                TABLE_USUARIOS,
                null,
                values
        );
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        db.execSQL(
                "DROP TABLE IF EXISTS " + TABLE_DETALLE
        );

        db.execSQL(
                "DROP TABLE IF EXISTS " + TABLE_RECOLECCIONES
        );

        db.execSQL(
                "DROP TABLE IF EXISTS " + TABLE_RESIDUOS
        );
        db.execSQL(
                "DROP TABLE IF EXISTS " + TABLE_USUARIOS
        );

        onCreate(db);
    }

    // OBTENER ID DEL TIPO DE RESIDUO
    public int obtenerIdResiduo(String nombre) {

        SQLiteDatabase db = this.getReadableDatabase();

        int id = -1;

        android.database.Cursor cursor = db.rawQuery(
                "SELECT id FROM " + TABLE_RESIDUOS +
                        " WHERE nombre = ?",
                new String[]{nombre}
        );

        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }

        cursor.close();

        return id;
    }


    // GUARDAR UNA RECOLECCIÓN COMPLETA
    public long guardarRecoleccion(
            String fecha,
            String hora,
            String observacion,
            java.util.ArrayList<String> residuos,
            java.util.ArrayList<Double> cantidades) {

        SQLiteDatabase db = this.getWritableDatabase();

        // Iniciar transacción
        db.beginTransaction();

        long recoleccionId = -1;

        try {

            // ==========================================
            // 1. INSERTAR LA RECOLECCIÓN
            // ==========================================

            ContentValues valoresRecoleccion =
                    new ContentValues();

            valoresRecoleccion.put(
                    "fecha",
                    fecha
            );

            valoresRecoleccion.put(
                    "hora",
                    hora
            );

            valoresRecoleccion.put(
                    "observacion",
                    observacion
            );

            valoresRecoleccion.put(
                    "estado",
                    "Completada"
            );

            recoleccionId =
                    db.insert(
                            TABLE_RECOLECCIONES,
                            null,
                            valoresRecoleccion
                    );


            if (recoleccionId == -1) {
                throw new Exception(
                        "No se pudo guardar la recolección"
                );
            }


            // ==========================================
            // 2. INSERTAR CADA RESIDUO
            // ==========================================

            for (int i = 0; i < residuos.size(); i++) {

                String nombreResiduo =
                        residuos.get(i);

                double cantidad =
                        cantidades.get(i);


                // Obtener ID del residuo
                int residuoId =
                        obtenerIdResiduo(
                                nombreResiduo
                        );


                if (residuoId == -1) {
                    throw new Exception(
                            "No existe el residuo: "
                                    + nombreResiduo
                    );
                }


                // Datos del detalle
                ContentValues valoresDetalle =
                        new ContentValues();

                valoresDetalle.put(
                        "recoleccion_id",
                        recoleccionId
                );

                valoresDetalle.put(
                        "residuo_id",
                        residuoId
                );

                valoresDetalle.put(
                        "cantidad_kg",
                        cantidad
                );


                long resultado =
                        db.insert(
                                TABLE_DETALLE,
                                null,
                                valoresDetalle
                        );


                if (resultado == -1) {
                    throw new Exception(
                            "Error guardando el detalle"
                    );
                }
            }


            // Confirmar transacción
            db.setTransactionSuccessful();

        } catch (Exception e) {

            recoleccionId = -1;

        } finally {

            db.endTransaction();
        }


        return recoleccionId;
    }
    // CONTAR RECOLECCIONES
    public int obtenerCantidadRecolecciones() {

        SQLiteDatabase db = this.getReadableDatabase();

        android.database.Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_RECOLECCIONES,
                null
        );

        int cantidad = 0;

        if (cursor.moveToFirst()) {
            cantidad = cursor.getInt(0);
        }

        cursor.close();

        return cantidad;
    }


    // OBTENER TOTAL DE KILOGRAMOS
    public double obtenerTotalKg() {

        SQLiteDatabase db = this.getReadableDatabase();

        android.database.Cursor cursor = db.rawQuery(
                "SELECT SUM(cantidad_kg) FROM " + TABLE_DETALLE,
                null
        );

        double total = 0;

        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            total = cursor.getDouble(0);
        }

        cursor.close();

        return total;
    }


    // CONTAR TIPOS DE RESIDUOS
    public int obtenerCantidadTiposResiduos() {

        SQLiteDatabase db = this.getReadableDatabase();

        android.database.Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_RESIDUOS,
                null
        );

        int cantidad = 0;

        if (cursor.moveToFirst()) {
            cantidad = cursor.getInt(0);
        }

        cursor.close();

        return cantidad;
    }
    public android.database.Cursor obtenerHistorial() {

        SQLiteDatabase db = this.getReadableDatabase();

        String sql =
                "SELECT r.id, " +
                        "r.fecha, " +
                        "r.hora, " +
                        "r.estado, " +
                        "COUNT(d.id) AS cantidad_residuos, " +
                        "SUM(d.cantidad_kg) AS total_kg " +
                        "FROM " + TABLE_RECOLECCIONES + " r " +
                        "LEFT JOIN " + TABLE_DETALLE +
                        " d ON r.id = d.recoleccion_id " +
                        "GROUP BY r.id, r.fecha, r.hora, r.estado " +
                        "ORDER BY r.id DESC";

        return db.rawQuery(sql, null);
    }
    // ELIMINAR UNA RECOLECCIÓN
    public boolean eliminarRecoleccion(int recoleccionId) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();

        try {

            // Primero eliminar los detalles
            db.delete(
                    TABLE_DETALLE,
                    "recoleccion_id = ?",
                    new String[]{String.valueOf(recoleccionId)}
            );

            // Luego eliminar la recolección
            int filas = db.delete(
                    TABLE_RECOLECCIONES,
                    "id = ?",
                    new String[]{String.valueOf(recoleccionId)}
            );

            db.setTransactionSuccessful();

            return filas > 0;

        } finally {

            db.endTransaction();
        }
    }
    // OBTENER UNA RECOLECCIÓN POR ID
    public android.database.Cursor obtenerRecoleccion(int id) {

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT id, fecha, hora, observacion, estado " +
                        "FROM " + TABLE_RECOLECCIONES +
                        " WHERE id = ?",
                new String[]{
                        String.valueOf(id)
                }
        );
    }


    // OBTENER LOS RESIDUOS DE UNA RECOLECCIÓN
    public android.database.Cursor obtenerDetalleRecoleccion(int id) {

        SQLiteDatabase db = getReadableDatabase();

        String sql =
                "SELECT d.id, d.residuo_id, r.nombre, d.cantidad_kg " +
                        "FROM " + TABLE_DETALLE + " d " +
                        "INNER JOIN " + TABLE_RESIDUOS + " r " +
                        "ON d.residuo_id = r.id " +
                        "WHERE d.recoleccion_id = ? " +
                        "ORDER BY d.id";

        return db.rawQuery(
                sql,
                new String[]{
                        String.valueOf(id)
                }
        );
    }


    // ACTUALIZAR UNA RECOLECCIÓN
    public boolean actualizarRecoleccion(
            int recoleccionId,
            String fecha,
            String hora,
            String observacion,
            java.util.ArrayList<String> residuos,
            java.util.ArrayList<Double> cantidades
    ) {

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        try {

            // Actualizar datos principales
            ContentValues valores = new ContentValues();

            valores.put("fecha", fecha);
            valores.put("hora", hora);
            valores.put("observacion", observacion);

            int filas = db.update(
                    TABLE_RECOLECCIONES,
                    valores,
                    "id = ?",
                    new String[]{
                            String.valueOf(recoleccionId)
                    }
            );

            if (filas == 0) {
                return false;
            }


            // Eliminar los detalles anteriores
            db.delete(
                    TABLE_DETALLE,
                    "recoleccion_id = ?",
                    new String[]{
                            String.valueOf(recoleccionId)
                    }
            );


            // Insertar nuevamente los detalles
            for (int i = 0; i < residuos.size(); i++) {

                String nombreResiduo = residuos.get(i);
                double cantidad = cantidades.get(i);

                int residuoId = -1;

                android.database.Cursor cursor = db.rawQuery(
                        "SELECT id FROM " + TABLE_RESIDUOS +
                                " WHERE nombre = ?",
                        new String[]{
                                nombreResiduo
                        }
                );

                if (cursor.moveToFirst()) {
                    residuoId = cursor.getInt(0);
                }

                cursor.close();


                if (residuoId == -1) {
                    throw new Exception(
                            "No se encontró el residuo: " + nombreResiduo
                    );
                }


                ContentValues detalle = new ContentValues();

                detalle.put(
                        "recoleccion_id",
                        recoleccionId
                );

                detalle.put(
                        "residuo_id",
                        residuoId
                );

                detalle.put(
                        "cantidad_kg",
                        cantidad
                );

                db.insert(
                        TABLE_DETALLE,
                        null,
                        detalle
                );
            }


            db.setTransactionSuccessful();

            return true;

        } catch (Exception e) {

            e.printStackTrace();

            return false;

        } finally {

            db.endTransaction();
        }
    }
    // ==========================================
// OBTENER DATOS PARA EL REPORTE
// ==========================================
    public Cursor obtenerReporte(
            String fechaInicio,
            String fechaFin,
            String tipoResiduo,
            String volumen) {

        SQLiteDatabase db = getReadableDatabase();

        StringBuilder sql = new StringBuilder();

        sql.append(
                "SELECT " +
                        "r.nombre AS residuo, " +
                        "SUM(d.cantidad_kg) AS total_kg, " +
                        "COUNT(DISTINCT d.recoleccion_id) AS recolecciones " +
                        "FROM detalle_recoleccion d " +
                        "INNER JOIN recolecciones rc " +
                        "ON d.recoleccion_id = rc.id " +
                        "INNER JOIN residuos r " +
                        "ON d.residuo_id = r.id " +
                        "WHERE " +
                        "substr(rc.fecha,7,4) || " +
                        "substr(rc.fecha,4,2) || " +
                        "substr(rc.fecha,1,2) " +
                        "BETWEEN ? AND ? "
        );

        ArrayList<String> argumentos =
                new ArrayList<>();

        // Convertir:
        // dd/MM/yyyy -> yyyyMMdd
        argumentos.add(convertirFecha(fechaInicio));
        argumentos.add(convertirFecha(fechaFin));

        // ==========================================
        // FILTRO POR TIPO DE RESIDUO
        // ==========================================
        if (!tipoResiduo.equals("Todos")) {

            sql.append("AND r.nombre = ? ");

            argumentos.add(tipoResiduo);
        }

        // ==========================================
        // AGRUPAR POR TIPO DE RESIDUO
        // ==========================================
        sql.append(
                "GROUP BY r.nombre "
        );

        // ==========================================
        // FILTRO POR VOLUMEN
        // ==========================================
        if (volumen.equals("0 - 10 kg")) {

            sql.append(
                    "HAVING SUM(d.cantidad_kg) BETWEEN 0 AND 10 "
            );

        } else if (volumen.equals("11 - 50 kg")) {

            sql.append(
                    "HAVING SUM(d.cantidad_kg) BETWEEN 11 AND 50 "
            );

        } else if (volumen.equals("Más de 50 kg")) {

            sql.append(
                    "HAVING SUM(d.cantidad_kg) > 50 "
            );
        }

        // ==========================================
        // ORDENAR
        // ==========================================
        sql.append(
                "ORDER BY total_kg DESC"
        );

        return db.rawQuery(
                sql.toString(),
                argumentos.toArray(new String[0])
        );
    }


    // ==========================================
// CONVERTIR FECHA
// dd/MM/yyyy -> yyyyMMdd
// ==========================================
    private String convertirFecha(String fecha) {

        try {

            String[] partes = fecha.split("/");

            return partes[2] +
                    partes[1] +
                    partes[0];

        } catch (Exception e) {

            return fecha;
        }
    }
    //VALIDAR USUARIOS

    public String validarUsuario(String usuario, String contrasena) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT nombre FROM usuarios WHERE usuario = ? AND contrasena = ?",
                new String[]{usuario, contrasena}
        );

        String nombre = null;

        if (cursor.moveToFirst()) {
            nombre = cursor.getString(
                    cursor.getColumnIndexOrThrow("nombre")
            );
        }

        cursor.close();

        return nombre;
    }
}