package com.example.cannabisappmobile

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import android.graphics.Color
import java.io.FileReader

class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var circularProgressBar: CircularProgressBar
    private lateinit var tvHealthyPlants: TextView
    private lateinit var tvAttentionPlants: TextView
    private val plants = mutableListOf<Plant>()
    private val gson = Gson()
    private val plantFileName = "plants.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        findViewById<ImageView>(R.id.ivMenu).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        circularProgressBar = findViewById(R.id.circularProgressBar)
        tvHealthyPlants = findViewById(R.id.tvHealthyPlants)
        tvAttentionPlants = findViewById(R.id.tvAttentionPlants)
    }

    override fun onResume() {
        super.onResume()
        loadPlants()
        updateStatistics()
        updateCircularProgressBar()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_dashboard -> {
                // Logique pour le tableau de bord
            }
            R.id.nav_add_plant -> {
                val intent = Intent(this, AddPlantActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_inventory -> {
                val intent = Intent(this, InventoryActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_history -> {
                val intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_archive -> {
                val intent = Intent(this, ArchiveActivity::class.java)
                startActivity(intent)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun loadPlants() {
        val file = File(filesDir, plantFileName)
        if (file.exists()) {
            val fileReader = FileReader(file)
            val plantType = object : TypeToken<MutableList<Plant>>() {}.type
            val loadedPlants: MutableList<Plant> = gson.fromJson(fileReader, plantType)
            plants.clear()
            plants.addAll(loadedPlants)
            fileReader.close()
        }
    }

    private fun updateStatistics() {
        val healthyPlantsCount = plants.count { it.healthStatus == Color.GREEN.toString() }
        val attentionPlantsCount = plants.count { it.healthStatus == Color.RED.toString() }

        tvHealthyPlants.text = healthyPlantsCount.toString()
        tvAttentionPlants.text = attentionPlantsCount.toString()
    }

    private fun updateCircularProgressBar() {
        val totalPlants = plants.size
        val progress = when {
            totalPlants > 300 -> 300
            else -> totalPlants
        }

        circularProgressBar.setProgress(progress)
    }
}
