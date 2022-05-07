package com.dwtraining.lom.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.dwtraining.lom.R
import com.dwtraining.lom.adapters.SectionsPagerAdapter
import com.dwtraining.lom.services.NotificationService
import com.dwtraining.lom.services.TokenFirebaseService
import com.google.android.material.tabs.TabLayout
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private var pagerAdapter: SectionsPagerAdapter? = null
    val getActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                updateAllLists()
            }
        }

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
        UDID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID) ?: "1234"

        if (!NotificationService.isRunning()) {
            startService(Intent(this, NotificationService::class.java))
        }
        initializeFirebase()
        fab.setOnClickListener { goToAddFugitiveActivity() }
    }
    private fun initializeFirebase() {

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) {
                return@addOnCompleteListener
            }

            val newToken = it.result ?: ""
            Log.d("Home", getString(R.string.token_firebase_service_new_token, newToken))
            Toast.makeText(this, getString(R.string.token_firebase_service_new_token, newToken),
                Toast.LENGTH_LONG).show()

        }
    }

    private fun updateAllLists(index: Int = 0) {
        container.apply {
            adapter = pagerAdapter
            currentItem = index
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_fugitive -> {
                goToAddFugitiveActivity()
                true
            }
            R.id.action_logs_eliminacion -> {
                goToAddDeletionLogsActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }
    private fun goToAddDeletionLogsActivity() {
        val intent = Intent(this, DeletionLogsActivity::class.java)
        getActivityResult.launch(intent)
    }

    private fun goToAddFugitiveActivity() {
        val intent = Intent(this, AddFugitiveActivity::class.java)
        getActivityResult.launch(intent)
    }

    companion object {
        private const val REQUEST_CODE_ADD_FUGITIVE = 0
        private const val REQUEST_CODE_DELETION_LOGS = 1
        var UDID = ""
    }

}
