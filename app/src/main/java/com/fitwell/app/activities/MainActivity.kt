package com.fitwell.app.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.fitwell.app.R
import com.fitwell.app.database.AppDatabase
import com.fitwell.app.receivers.HydrationReceiver
import com.fitwell.app.services.StepService
import com.fitwell.app.utils.Session
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply persisted dark mode preference before setContentView
        val prefs = getSharedPreferences("fw_settings", MODE_PRIVATE)
        val isDark = prefs.getBoolean("dark_mode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)
        session = Session(this)
        setContentView(R.layout.activity_main)

        setupDrawer(prefs)
        setupNavigation()
        requestPermissions()
        startForegroundService(Intent(this, StepService::class.java))

        lifecycleScope.launch {
            val db = AppDatabase.get(this@MainActivity)
            db.userDao().getByIdFlow(session.userId).collectLatest { user ->
                user ?: return@collectLatest
                val navView = findViewById<NavigationView>(R.id.navView)
                val header = navView.getHeaderView(0)
                header.findViewById<TextView>(R.id.tvDrawerName)?.text = user.name
                header.findViewById<TextView>(R.id.tvDrawerEmail)?.text = user.email
                header.findViewById<TextView>(R.id.tvDrawerGoal)?.text =
                    user.goal.replace("_", " ").replaceFirstChar { it.uppercase() }
            }
        }
    }

    private fun setupDrawer(prefs: android.content.SharedPreferences) {
        drawerLayout = findViewById(R.id.drawerLayout)
        val navView = findViewById<NavigationView>(R.id.navView)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener { item ->
            drawerLayout.closeDrawers()
            when (item.itemId) {
                R.id.navProfile -> {
                    val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
                    bottomNav.selectedItemId = R.id.profileFragment
                    true
                }
                R.id.navSettings -> {
                    val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
                    bottomNav.selectedItemId = R.id.profileFragment
                    true
                }
                R.id.navDarkMode -> {
                    // Toggle and persist — Profile fragment reads same prefs key
                    val current = prefs.getBoolean("dark_mode", false)
                    val next = !current
                    prefs.edit().putBoolean("dark_mode", next).apply()
                    AppCompatDelegate.setDefaultNightMode(
                        if (next) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                    )
                    true
                }
                R.id.navLogout -> { doLogout(); true }
                else -> false
            }
        }
    }

    private fun setupNavigation() {
        val navHost = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        val navController = navHost.navController
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setupWithNavController(navController)
    }

    private fun doLogout() {
        HydrationReceiver.cancel(this)
        session.logout()
        startActivity(Intent(this, AuthActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }

    private fun requestPermissions() {
        val perms = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
            != PackageManager.PERMISSION_GRANTED) perms.add(Manifest.permission.ACTIVITY_RECOGNITION)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) perms.add(Manifest.permission.POST_NOTIFICATIONS)
        if (perms.isNotEmpty()) ActivityCompat.requestPermissions(this, perms.toTypedArray(), 100)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        if (toggle.onOptionsItemSelected(item)) true else super.onOptionsItemSelected(item)
}
