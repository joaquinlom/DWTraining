package com.dwtraining.lom.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.dwtraining.lom.R
import com.dwtraining.lom.data.DatabaseBountyHunter
import com.dwtraining.lom.fragments.NamesListFragment
import com.dwtraining.lom.models.Fugitive
import com.dwtraining.lom.network.JsonParser
import com.dwtraining.lom.network.NetworkServices
import com.dwtraining.lom.network.SERVICE_TYPE
import com.dwtraining.lom.network.onTaskListener
import com.dwtraining.lom.utils.PictureTools
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {
    private lateinit var selectedFugitive: Fugitive
    private val database by lazy {DatabaseBountyHunter(this)}

    private var imagePath: Uri? = null
    private val getResultCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                Log.d("Detail",PictureTools.currentPhotoPath)
                selectedFugitive.photo = PictureTools.currentPhotoPath
                val bitmap = PictureTools.decodeSampledBitmapFromUri(PictureTools.currentPhotoPath, 200, 200)
                pictureFugitive.setImageBitmap(bitmap)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        selectedFugitive = intent.extras?.get(NamesListFragment.SELECTED_FUGITIVE) as Fugitive
        // Se obtiene el nombre del fugitivo del objeto fugitivo y se usa como título
        title = "${selectedFugitive.name} - ${selectedFugitive.id}"
        selectedFugitive.photo?.also{
            val bitmap = PictureTools.decodeSampledBitmapFromUri(it, 200, 200)
            pictureFugitive.setImageBitmap(bitmap)
        }

        // Se identifica si es Fugitivo o capturado para el mensaje...
        if (selectedFugitive.status == 0) {
            labelMessage.text = getString(R.string.detail_fugitive_still_not_catch)
        } else {
            labelMessage.text = getString(R.string.detail_fugitive_in_jail)
            buttonCapture.visibility =  GONE
        }
    }

    fun onCaptureFugitive(view:View){
        selectedFugitive.status = 1
        if (selectedFugitive.photo!!.isEmpty()) {
            Toast.makeText(this, getString(R.string.detail_fugitive_no_photo_message),
                Toast.LENGTH_LONG).show()
            return
        }

        database.updateFugitive(selectedFugitive)

        NetworkServices(object : onTaskListener {
            override fun tareaCompletada(respuesta: String) {
                // después de traer los datos del web service se actualiza la interfaz...
                messageClose(JsonParser.parseMessageFromAPI(respuesta))
            }
            override fun tareaConError(codigo: Int, mensaje: String, error: String) {
                Toast.makeText(this@DetailActivity,
                    getString(R.string.add_fugitive_error_loading_from_webservice),
                    Toast.LENGTH_LONG).show()
            }
        }).execute(SERVICE_TYPE.ATRAPADOS.name, HomeActivity.UDID)
        setResult(RESULT_OK)
    }
    fun onDeleteFugitive(view: View){
        database.deleteFugitive(selectedFugitive)
        setResult(RESULT_OK)
        finish()
    }
    private fun messageClose(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.detail_fugitive_title_alert))
            setMessage(message)
            setOnDismissListener {
                setResult(Activity.RESULT_OK)
                finish()
            }
            create()
        }.show()
    }

    private fun openCameraAndTakePicture() {
        imagePath = PictureTools.getOutputMediaFileUri(this)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply { putExtra(MediaStore.EXTRA_OUTPUT, imagePath) }
        getResultCamera.launch(intent)
    }

    fun takePicture(view: View) {
        if (PictureTools.permissionReadMemmory(this)) {
            openCameraAndTakePicture()
        }
    }


}