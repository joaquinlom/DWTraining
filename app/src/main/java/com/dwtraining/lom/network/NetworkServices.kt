package com.dwtraining.lom.network

import android.os.AsyncTask
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class NetworkServices(private val listener: onTaskListener) : AsyncTask<String, Void, Boolean>() {

    private var JsonResponse: String = ""
    private var tipo: SERVICE_TYPE = SERVICE_TYPE.FUGITIVOS
    private var codigo: Int = 0
    private var mensaje: String = ""
    private var error: String = ""

    override fun doInBackground(vararg params: String?): Boolean {
        val esFugitivo = params[0]?.equals(SERVICE_TYPE.FUGITIVOS.name, true) ?: false
        tipo = if (esFugitivo) SERVICE_TYPE.FUGITIVOS else SERVICE_TYPE.ATRAPADOS
        var urlConnection: HttpURLConnection? = null
        try {
            urlConnection = getStructuredRequest(
                tipo,
                if (esFugitivo) endpoint_fugitivos else endpoint_atrapados,
                if (params.size > 1) params[1]!! else ""
            )
            val inputStream = urlConnection.inputStream ?: return false
            val reader = BufferedReader(InputStreamReader(inputStream))
            val buffer = StringBuffer()
            do {
                val line: String? = reader.readLine()
                if (line != null) buffer.append(line).append("\n")
            } while (!line.isNullOrBlank())
            if (buffer.isEmpty()) return false
            JsonResponse = buffer.toString()
            Log.d(TAG, "Respuesta del Servidor: $JsonResponse")
            return true
        } catch (e: FileNotFoundException) {
            manageError(urlConnection)
            return false
        } catch (e: IOException) {
            manageError(urlConnection)
            return false
        } catch (e: Exception) {
            manageError(urlConnection)
            return false
        } finally {
            urlConnection?.disconnect()
        }

    }

    override fun onPostExecute(result: Boolean?) {
        if (result == true) {
            listener.tareaCompletada(JsonResponse)
        } else {
            listener.tareaConError(codigo, mensaje, error)
        }
    }

    private fun manageError(urlConnection: HttpURLConnection?) {
        if (urlConnection != null) {
            try {
                codigo = urlConnection.responseCode
                if (urlConnection.errorStream != null) {
                    val inputStream = urlConnection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val buffer = StringBuffer()
                    do {
                        val line: String? = reader.readLine()
                        if (line != null) buffer.append(line).append("\n")
                    } while (line != null)
                    error = buffer.toString()
                } else {
                    mensaje = urlConnection.responseMessage
                }
                error = urlConnection.errorStream.toString()
                Log.e(TAG, "Error: $error, code: $codigo")
            } catch (e1: IOException) {
                e1.printStackTrace()
                Log.e(TAG, "Error")
            }
        } else {
            codigo = 105
            mensaje = "Error: No internet connection"
            Log.e(TAG, "code: $codigo, $mensaje")
        }
    }

    @Throws(IOException::class, JSONException::class)
    private fun getStructuredRequest(type: SERVICE_TYPE, endpoint: String, id: String): HttpURLConnection {
        val url = URL(endpoint)
        Log.d(TAG, url.toString())
        if (type === SERVICE_TYPE.FUGITIVOS) { //---------- GET Fugitivos--------------
            return (url.openConnection() as HttpURLConnection).apply {
                readTimeout = TIME_OUT
                requestMethod = "GET"
                setRequestProperty("Content-Type", "application/json")
            }.also { it.connect() }
        } else { //--------------------- POST Atrapados------------------------
            return (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                readTimeout = TIME_OUT
                doInput = true
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
            }.also {
                it.connect()
                val `object` = JSONObject().apply { put("UDIDString", id) }
                DataOutputStream(it.outputStream).apply {
                    write(`object`.toString().toByteArray())
                    flush()
                    close()
                }
            }
        }
    }

    companion object {
        private val TAG = NetworkServices::class.java.simpleName
        private const val endpoint_fugitivos = "http://3.13.226.218/droidBHServices.svc/fugitivos"
        private const val endpoint_atrapados = "http://3.13.226.218/droidBHServices.svc/atrapados"
        private const val TIME_OUT = 500
    }
}

enum class SERVICE_TYPE {
    FUGITIVOS, ATRAPADOS
}

interface onTaskListener {
    fun tareaCompletada(respuesta: String)
    fun tareaConError(codigo: Int, mensaje: String, error: String)
}