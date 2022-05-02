package com.dwtraining.droidbountyhunter.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.dwtraining.droidbountyhunter.models.Fugitive

/** ------------------- Nombre de Base de Datos --------------------------**/
const val DATABASE_NAME = "DroidBountyHunterDatabase"
/** ------------------ Versión de Base de Datos --------------------------**/
const val VERSION = 1
/** ---------------------- Tablas y Campos -------------------------------**/
const val TABLE_NAME_FUGITIVOS = "fugitivos"
const val COLUMN_NAME_ID = "id"
const val COLUMN_NAME_NAME = "name"
const val COLUMN_NAME_STATUS = "status"

class DatabaseBountyHunter(private val context: Context) {

    private val TAG: String = DatabaseBountyHunter::class.java.simpleName
    /** ------------------- Declaración de Tablas ----------------------------**/
    private val TFugitivos = "CREATE TABLE " + TABLE_NAME_FUGITIVOS + " (" +
            COLUMN_NAME_ID + " INTEGER PRIMARY KEY NOT NULL, " +
            COLUMN_NAME_NAME + " TEXT NOT NULL, " +
            COLUMN_NAME_STATUS + " INTEGER, " +
            "UNIQUE (" + COLUMN_NAME_NAME + ") ON CONFLICT REPLACE);"
    /** ---------------------- Variables y Helpers ---------------------------**/
    private var helper: DBHelper? = null
    private var database: SQLiteDatabase? = null

    inner class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, VERSION) {
        override fun onCreate(db: SQLiteDatabase?) {
            Log.d(TAG, "Creación de la base de datos")
            db?.execSQL(TFugitivos)
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            Log.w(TAG, "Actualización de la BDD de la versión $oldVersion a la " +
                    "$newVersion, de la que se destruirá la información anterior")
            // Destruir BDD anterior y crearla nuevamente las tablas actualizadas
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_FUGITIVOS")
            // Re-creando nuevamente la BDD actualizada
            onCreate(db)
        }
    }

    /** ---------------------- Funciones ---------------------------**/

    fun open() : DatabaseBountyHunter {
        helper = DBHelper(context)
        database = helper?.writableDatabase
        return this
    }

    fun close() {
        helper?.close()
        database?.close()
    }

    fun querySQL(sql: String, selectionArgs: Array<String>) : Cursor {
        open()
        return database!!.rawQuery(sql,selectionArgs)
    }

    fun deleteFugitive(fugitive: Fugitive) {
        open()
        database?.delete(TABLE_NAME_FUGITIVOS, "$COLUMN_NAME_ID=?", arrayOf(fugitive.id.toString()))
        close()
    }

    fun updateFugitive(fugitive: Fugitive) {
        open()
        val values = ContentValues().apply {
            put(COLUMN_NAME_NAME, fugitive.name)
            put(COLUMN_NAME_STATUS, fugitive.status)
        }
        database?.update(TABLE_NAME_FUGITIVOS, values, "$COLUMN_NAME_ID=?", arrayOf(fugitive.id.toString()))
        close()
    }

    fun insertFugitive(fugitive: Fugitive) {
        open()
        val values = ContentValues().apply {
            put(COLUMN_NAME_NAME, fugitive.name)
            put(COLUMN_NAME_STATUS, fugitive.status)
        }
        database?.insert(TABLE_NAME_FUGITIVOS, null, values)
        close()
    }

    @SuppressLint("Range")
    fun obtainFugitives(status: Int) : Array<Fugitive> {
        var fugitives: Array<Fugitive> = arrayOf()
        val dataCursor = querySQL("SELECT * FROM $TABLE_NAME_FUGITIVOS WHERE " +
                 "$COLUMN_NAME_STATUS=? ORDER BY $COLUMN_NAME_NAME", arrayOf(status.toString()))
        if (dataCursor.count > 0) {
            fugitives = generateSequence {
                if (dataCursor.moveToNext()) dataCursor else null
            }.map {
                val name = it.getString(it.getColumnIndex(COLUMN_NAME_NAME))
                val statusFugitive = it.getInt(it.getColumnIndex(COLUMN_NAME_STATUS))
                val id = it.getInt(it.getColumnIndex(COLUMN_NAME_ID))
                return@map Fugitive(id, name, statusFugitive)
            }.toList().toTypedArray()
        }
        return fugitives
    }
}