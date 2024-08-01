package com.example.cannabisappmobile

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
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
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import java.io.File
import java.io.FileReader

class InventoryActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val gson = Gson()
    private val plantFileName = "plants.json"
    private val plants = mutableListOf<Plant>()

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            val plant = plants.find { it.id == result.contents }
            if (plant != null) {
                // Afficher les informations de la plante
                // Vous pouvez rediriger vers une autre activité pour afficher les détails de la plante
                // ou afficher un dialogue avec les informations de la plante
            } else {
                // Gérer le cas où la plante n'est pas trouvée
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

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

        loadPlants()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = PlantAdapter(plants.filter { it.active == 1 }) // Filtrer les plantes actives

        val btnScanQRCode = findViewById<Button>(R.id.btnScanQRCode)
        btnScanQRCode.setOnClickListener {
            val options = ScanOptions()
            options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            options.setPrompt("Scan a QR code")
            options.setOrientationLocked(true) // Force le mode portrait
            options.setBeepEnabled(true)
            options.setBarcodeImageEnabled(true)
            barcodeLauncher.launch(options)
        }
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
}
