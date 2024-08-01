package com.example.cannabisappmobile

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader

class HistoryActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val gson = Gson()
    private val historyFileName = "history.json"
    private val history = mutableListOf<History>()

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val ivMenu = findViewById<ImageView>(R.id.ivMenu)
        ivMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        loadHistory()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = HistoryAdapter(history)
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
            R.id.nav_logout -> {
                // Rediriger vers la page de connexion
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.nav_exit -> {
                // Quitter l'application
                finishAffinity() // Ferme toutes les activités et quitte l'application
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun loadHistory() {
        val file = File(filesDir, historyFileName)
        if (file.exists()) {
            val fileReader = FileReader(file)
            val historyType = object : TypeToken<MutableList<History>>() {}.type
            val loadedHistory: MutableList<History> = gson.fromJson(fileReader, historyType)
            history.clear()
            history.addAll(loadedHistory)
            fileReader.close()
        }
    }
}
