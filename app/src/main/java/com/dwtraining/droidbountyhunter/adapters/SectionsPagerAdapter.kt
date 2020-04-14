package com.dwtraining.droidbountyhunter.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.dwtraining.droidbountyhunter.R
import com.dwtraining.droidbountyhunter.fragments.NamesListFragment
import java.util.*

class SectionsPagerAdapter(fm: FragmentManager, private val context: Context) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment = NamesListFragment()

    // Show 3 total pages.
    override fun getCount(): Int = 3

    override fun getPageTitle(position: Int) = when (position) {
        0 -> context.getString(R.string.title_fugitives).toUpperCase(Locale.getDefault())
        1 -> context.getString(R.string.title_catch).toUpperCase(Locale.getDefault())
        else -> context.getString(R.string.title_about_us).toUpperCase(Locale.getDefault())
    }
}