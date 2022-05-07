package com.dwtraining.lom.activities

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dwtraining.lom.R
import com.dwtraining.lom.data.DatabaseBountyHunter
import com.dwtraining.lom.models.Fugitive
import com.dwtraining.lom.network.JsonParser
import com.dwtraining.lom.network.NetworkServices
import com.dwtraining.lom.network.SERVICE_TYPE
import com.dwtraining.lom.network.onTaskListener
import kotlinx.android.synthetic.main.activity_add_fugitive.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory



class AddFugitiveActivity : AppCompatActivity() {
    private val factory: DocumentBuilderFactory by lazy { DocumentBuilderFactory.newInstance() }

    // Creación del parser DOM
    private val builder: DocumentBuilder by lazy { factory.newDocumentBuilder() }
    private val dom: Document by lazy { builder.parse(resources.openRawResource(R.raw.fugitives)) }
    private val root: Element by lazy { dom.documentElement }
    private val items: NodeList by lazy { root.getElementsByTagName("fugitivo") }
    private var value: String = ""
    private var percentageCounter = 0

    private val database by lazy { DatabaseBountyHunter(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_fugitive)
        title = getString(R.string.add_fugitive_title_screen)
    }

    fun saveSelectedFugitive(view: View){
        val newFugitiveName = nameFugitiveTextView.text.toString()
        if(newFugitiveName.isNotBlank()){
            DatabaseBountyHunter(this).apply {
                insertFugitive(Fugitive(name = newFugitiveName))
                setResult(Activity.RESULT_OK)
                finish()
            }
        }else{
            AlertDialog.Builder(this).setTitle(getString(R.string.add_fugitive_title_alert)).setMessage(getString(R.string.add_fugitive_message_alert))
        }
    }

    fun addFugitivesFromWebService(view: View) {
        if (database.obtainFugitives(0).isEmpty()) {
            NetworkServices(object : onTaskListener {
                override fun tareaCompletada(respuesta: String) {
                    JsonParser.parseJsonToFugitives(respuesta).takeIf { it.isNotEmpty() }
                        ?.forEach { database.insertFugitive(it) }
                    setResult(RESULT_OK)
                    finish()
                }

                override fun tareaConError(codigo: Int, mensaje: String, error: String) {
                    Toast.makeText(this@AddFugitiveActivity,
                        getString(R.string.add_fugitive_error_loading_from_webservice),
                        Toast.LENGTH_LONG).show()
                }
            }).execute(SERVICE_TYPE.FUGITIVOS.name)
        } else {
            Toast.makeText(this, getString(R.string.add_fugitive_error_no_empty_data),
                Toast.LENGTH_LONG).show()
        }
    }

    fun importFugitivesFromXML(view: View) {
        if (database.obtainFugitives(0).isEmpty()) {
            updateUIWhenLoadingFromXML()
            GlobalScope.launch { importXML() }
        } else {
            Toast.makeText(this, getString(R.string.add_fugitive_warning_not_empty_database), Toast.LENGTH_LONG).show()
        }
    }

    private suspend fun  importXML(){
        progressBar.max = 100
        progressBar.post { progressBar.progress = 0 }
        for (index in 0 until items.length) {
            value = items.item(index).firstChild.nodeValue
            percentageCounter = (index + 1) * 10
            database.insertFugitive(Fugitive(0, value, 0))
            progressBar.post {
                progressLabel.text = getString(R.string.add_fugitive_progress, "$percentageCounter%")
                progressBar.incrementProgressBy(10)
            }
            delay(500)
        }



        runOnUiThread {
            Toast.makeText(applicationContext, getString(R.string.add_fugitive_importation_finished), Toast.LENGTH_LONG).show()
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
    private fun updateUIWhenLoadingFromXML(){
        buttonAddWebService.isEnabled = false
        buttonSave.isEnabled = false
        addFugitivesFromXML.isEnabled = false

        progressBar.visibility = View.VISIBLE
        progressLabel.visibility = View.VISIBLE
    }
}


