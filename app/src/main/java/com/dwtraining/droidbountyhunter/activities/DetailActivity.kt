package com.dwtraining.droidbountyhunter.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dwtraining.droidbountyhunter.R
import com.dwtraining.droidbountyhunter.models.Fugitive
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {
    private lateinit var selectedFugitive: Fugitive

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        selectedFugitive = Fugitive(0, "Fugitivo de prueba", 0)
        // Se obtiene el nombre del fugitivo del objeto fugitivo y se usa como título
        title = "${selectedFugitive.name} - ${selectedFugitive.id}"
        // Se identifica si es Fugitivo o capturado para el mensaje...
        if (selectedFugitive.status == 0) {
            labelMessage.text = getString(R.string.detail_fugitive_still_not_catch)
        } else {
            labelMessage.text = getString(R.string.detail_fugitive_in_jail)
        }
    }
}