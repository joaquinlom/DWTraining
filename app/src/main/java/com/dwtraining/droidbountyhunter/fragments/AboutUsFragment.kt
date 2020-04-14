package com.dwtraining.droidbountyhunter.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dwtraining.droidbountyhunter.R
import kotlinx.android.synthetic.main.fragment_about_us.*

class AboutUsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_about_us, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var rating = "0.0" // Variable para lectura del rating guardado en Properties
        if (System.getProperty("rating") != null) {
            rating = System.getProperty("rating") ?: ""
        }
        if (rating.isEmpty()) {
            rating = "0.0"
        }
        ratingBar.rating = rating.toFloat()
        ratingBar.setOnRatingBarChangeListener { ratingBar, ratingValue, fromUser ->
            System.setProperty("rating", ratingValue.toString())
            ratingBar.rating = ratingValue
        }
    }
}