package com.example.cannabisappmobile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvForgotPassword: TextView
    private lateinit var tvRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)
        tvRegister = findViewById(R.id.tvRegister)

        // Ajouter des listeners pour les boutons si nécessaire
        btnLogin.setOnClickListener {
            // Démarrer DashboardActivity sans vérifier les identifiants
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }

        tvForgotPassword.setOnClickListener {
            // Gérer la récupération de mot de passe
        }

        tvRegister.setOnClickListener {
            // Gérer l'inscription
        }
    }
}
