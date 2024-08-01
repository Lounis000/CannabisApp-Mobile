package com.example.cannabisappmobile

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import android.view.MenuItem

class ArchiveActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val gson = Gson()
    private val plantFileName = "plants.json"
    private val archivedPlants = mutableListOf<Plant>()

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        drawerLayout = findViewById(R.id.drawer_layout)

        // Mettre à jour le titre
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        tvTitle.text = "Archive"

        loadArchivedPlants()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = PlantAdapter(archivedPlants)

        val ivMenu = findViewById<ImageView>(R.id.ivMenu)
        ivMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_dashboard -> {
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
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
                // Déjà sur cette activité, rien à faire
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

    private fun loadArchivedPlants() {
        val file = File(filesDir, plantFileName)
        if (file.exists()) {
            val fileReader = FileReader(file)
            val plantType = object : TypeToken<MutableList<Plant>>() {}.type
            val allPlants: List<Plant> = gson.fromJson(fileReader, plantType)
            fileReader.close()
            archivedPlants.clear()
            archivedPlants.addAll(allPlants.filter { it.active == 0 }) // Supposant que 'active == 0' indique une plante supprimée
        }
    }
}
