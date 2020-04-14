package com.dwtraining.droidbountyhunter.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.dwtraining.droidbountyhunter.R
import com.dwtraining.droidbountyhunter.adapters.SectionsPagerAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private var pagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setSupportActionBar(toolbar)
        pagerAdapter = SectionsPagerAdapter(supportFragmentManager, this)

        // Set up the ViewPager with the sections adapter.
        container.adapter = pagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
        tabs.setupWithViewPager(container)

        fab.setOnClickListener { goToAddFugitiveActivity() }
    }

    private fun updateAllLists(index: Int = 0) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_ADD_FUGITIVE -> if (resultCode == Activity.RESULT_OK) { updateAllLists() }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add_fugitive) {
            goToAddFugitiveActivity()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun goToAddFugitiveActivity() {
        val intent = Intent(this, AddFugitiveActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE_ADD_FUGITIVE)
    }

    companion object {
        private const val REQUEST_CODE_ADD_FUGITIVE = 0
    }
}
