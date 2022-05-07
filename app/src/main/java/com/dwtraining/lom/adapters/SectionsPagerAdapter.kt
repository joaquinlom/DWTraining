package com.dwtraining.lom.adapters

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.dwtraining.lom.R
import com.dwtraining.lom.fragments.AboutUsFragment
import com.dwtraining.lom.fragments.NamesListFragment
import com.dwtraining.lom.fragments.NamesListFragment.Companion.SECTION_NUMBER

class SectionsPagerAdapter(fm: FragmentManager, private val context: Context) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private var fragments: ArrayList<Fragment> = ArrayList()

    override fun getItem(position: Int): Fragment {
        if (fragments.size < 3) { // Si no contiene los 3 fragments los agregará
            if (position < 2) {
                val arguments = Bundle().apply { putInt(SECTION_NUMBER, position) }
                fragments.add(position, NamesListFragment().also { it.arguments = arguments })
            } else {
                fragments.add(position, AboutUsFragment())
            }
        }
        return fragments[position]

    }

    // Show 3 total pages.
    override fun getCount(): Int = 3

    override fun getPageTitle(position: Int) = when (position) {
        0 -> context.getString(R.string.title_fugitives).uppercase()
        1 -> context.getString(R.string.title_catch).uppercase()
        else -> context.getString(R.string.title_about_us).uppercase()
    }
}