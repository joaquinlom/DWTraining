package com.dwtraining.droidbountyhunter.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dwtraining.droidbountyhunter.R

class AddFugitiveActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_fugitive)
        title = getString(R.string.add_fugitive_title_screen)
    }
}
