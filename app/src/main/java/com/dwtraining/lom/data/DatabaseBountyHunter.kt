package com.dwtraining.lom.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.dwtraining.lom.models.DeletionLog
import com.dwtraining.lom.models.Fugitive

/** ------------------- Nombre de Base de Datos --------------------------**/
const val DATABASE_NAME = "DroidBountyHunterDatabase"
/** ------------------ Versión de Base de Datos --------------------------**/
const val VERSION = 4
/** ---------------------- Tablas y Campos -------------------------------**/
const val TABLE_NAME_FUGITIVOS = "fugitivos"
const val TABLE_NAME_LOG = "Logs"

const val COLUMN_NAME_DATE = "date"
const val COLUMN_NAME_ID = "id"
const val COLUMN_NAME_NAME = "name"
const val COLUMN_NAME_STATUS = "status"
const val COLUMN_NAME_PHOTO = "photo"
const val COLUMN_NAME_NOTIFICATION = "notification"
class DatabaseBountyHunter(private val context: Context) {

    private val TAG: String = DatabaseBountyHunter::class.java.simpleName
    /** ------------------- Declaración de Tablas ----------------------------**/
    private val TFugitivos = "CREATE TABLE " + TABLE_NAME_FUGITIVOS + " (" +
            COLUMN_NAME_ID + " INTEGER PRIMARY KEY NOT NULL, " +
            COLUMN_NAME_NAME + " TEXT NOT NULL, " +
            COLUMN_NAME_STATUS + " INTEGER, " +
            COLUMN_NAME_PHOTO + " TEXT, " +
            COLUMN_NAME_NOTIFICATION + " INTEGER, " +
            "UNIQUE (" + COLUMN_NAME_NAME + ") ON CONFLICT REPLACE);"

    /** ---------------Log table creation Query **/
    private val TLogs = "CREATE TABLE $TABLE_NAME_LOG ($COLUMN_NAME_NAME TEXT NOT NULL," +
            "$COLUMN_NAME_DATE DATE, $COLUMN_NAME_STATUS INTEGER , $COLUMN_NAME_NOTIFICATION INTEGER); "
    /** ---------------------- Variables y Helpers ---------------------------**/
    private var helper: DBHelper? = null
    private var database: SQLiteDatabase? = null

    inner class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, VERSION) {
        override fun onCreate(db: SQLiteDatabase?) {
            Log.d(TAG, "Creación de la base de datos")
            db?.execSQL(TFugitivos)
            db?.execSQL(TLogs)
            db?.execSQL("CREATE TRIGGER LogEliminacion Before DELETE ON $TABLE_NAME_FUGITIVOS" +
                    " FOR EACH ROW BEGIN INSERT INTO $TABLE_NAME_LOG($COLUMN_NAME_NAME," +
                    "$COLUMN_NAME_DATE,$COLUMN_NAME_STATUS,$COLUMN_NAME_NOTIFICATION) VALUES(old.name, " +
                    "datetime('now'), old.status, 0); END")

        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            Log.w(TAG, "Actualización de la BDD de la versión $oldVersion a la " +
                    "$newVersion, de la que se destruirá la información anterior")
            // Destruir BDD anterior y crearla nuevamente las tablas actualizadas
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_FUGITIVOS")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_LOG")

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
            put(COLUMN_NAME_PHOTO, fugitive.photo)
            put(COLUMN_NAME_NOTIFICATION, fugitive.notification)
        }
        database?.update(TABLE_NAME_FUGITIVOS, values, "$COLUMN_NAME_ID=?", arrayOf(fugitive.id.toString()))
        close()
    }

    fun insertFugitive(fugitive: Fugitive) {
        open()
        val values = ContentValues().apply {
            put(COLUMN_NAME_NAME, fugitive.name)
            put(COLUMN_NAME_STATUS, fugitive.status)
            put(COLUMN_NAME_PHOTO, fugitive.photo)
            put(COLUMN_NAME_NOTIFICATION, fugitive.notification)
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
                val photo = it.getString(it.getColumnIndex(COLUMN_NAME_PHOTO))
                return@map Fugitive(id, name, statusFugitive,photo)
            }.toList().toTypedArray()
        }
        return fugitives
    }
    @SuppressLint("Range")
    fun obtainDeletionLogs() : Array<DeletionLog> {
        var deletionList: Array<DeletionLog> = arrayOf()
        val dataCursor = querySQL("SELECT * FROM $TABLE_NAME_LOG", arrayOf())
        if (dataCursor.count > 0) {
            deletionList = generateSequence {
                if (dataCursor.moveToNext()) dataCursor else null
            }.map {
                val name = it.getString(it.getColumnIndex(COLUMN_NAME_NAME))
                val date = it.getString(it.getColumnIndex(COLUMN_NAME_DATE))
                val status = it.getInt(it.getColumnIndex(COLUMN_NAME_STATUS))
                return@map DeletionLog(name, date,status)
            }.toList().toTypedArray()
        }
        return deletionList
    }
    private fun notifyAllFugitives() {
        val values = ContentValues().apply { put(COLUMN_NAME_NOTIFICATION, 1) }
        database?.update(TABLE_NAME_FUGITIVOS, values, "$COLUMN_NAME_NOTIFICATION=?", arrayOf("0"))
    }
    fun getLastFugitive(status: Int) : Cursor = querySQL("SELECT * FROM $TABLE_NAME_FUGITIVOS " +
            "WHERE $COLUMN_NAME_STATUS = ? ORDER BY $COLUMN_NAME_ID DESC", arrayOf(status.toString()))

    private fun notifyAllDeletionLogs() {
        val values = ContentValues().apply { put(COLUMN_NAME_NOTIFICATION, 1) }
        database?.update(TABLE_NAME_LOG, values, "$COLUMN_NAME_NOTIFICATION=?", arrayOf("0"))
    }

    @SuppressLint("Range")
    fun obtainFugitivesNotNotified() : Array<Fugitive> {
        var fugitives: Array<Fugitive> = arrayOf()
        val dataCursor = querySQL("SELECT * FROM $TABLE_NAME_FUGITIVOS WHERE " +
                "$COLUMN_NAME_NOTIFICATION=? ORDER BY $COLUMN_NAME_NAME", arrayOf("0"))
        if (dataCursor.count > 0) {
            fugitives = generateSequence {
                if (dataCursor.moveToNext()) dataCursor else null
            }.map {
                val name = it.getString(it.getColumnIndex(COLUMN_NAME_NAME))
                val statusFugitive = it.getInt(it.getColumnIndex(COLUMN_NAME_STATUS))
                val id = it.getInt(it.getColumnIndex(COLUMN_NAME_ID))
                val photo = it.getString(it.getColumnIndex(COLUMN_NAME_PHOTO))
                val notification = it.getInt(it.getColumnIndex(COLUMN_NAME_NOTIFICATION))
                return@map Fugitive(id, name, statusFugitive, photo, notification)
            }.toList().toTypedArray()
        }
        notifyAllFugitives()
        close()
        return fugitives
    }
    @SuppressLint("Range")
    fun obtainDeletionLogsNotNotified() : Array<DeletionLog> {
        var deletionList: Array<DeletionLog> = arrayOf()
        val dataCursor = querySQL("SELECT * FROM $TABLE_NAME_LOG WHERE " +
                "$COLUMN_NAME_NOTIFICATION=? ORDER BY $COLUMN_NAME_NAME", arrayOf("0"))
        if (dataCursor.count > 0) {
            deletionList = generateSequence {
                if (dataCursor.moveToNext()) dataCursor else null
            }.map {
                val name = it.getString(it.getColumnIndex(COLUMN_NAME_NAME))
                val date = it.getString(it.getColumnIndex(COLUMN_NAME_DATE))
                val status = it.getInt(it.getColumnIndex(COLUMN_NAME_STATUS))
                val notification = it.getInt(it.getColumnIndex(COLUMN_NAME_NOTIFICATION))
                return@map DeletionLog(name, date, status, notification)
            }.toList().toTypedArray()
        }
        notifyAllDeletionLogs()
        close()
        return deletionList
    }


}