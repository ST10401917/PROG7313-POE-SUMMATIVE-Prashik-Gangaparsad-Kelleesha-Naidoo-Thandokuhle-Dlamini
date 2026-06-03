package com.example.prog7313ktpbudgetingapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class HomePage : AppCompatActivity() {
    private  lateinit var expensebtn: LinearLayout
    private  lateinit var reportsbtn: LinearLayout
    private  lateinit var recordsbtn: LinearLayout
    private  lateinit var logoutbtn: Button
    private  lateinit var rewardsbtn: LinearLayout
    private  lateinit var Helpbtn: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)

        //Typecasting
        expensebtn = findViewById(R.id.expensebtn)
        reportsbtn = findViewById(R.id.reportsbtn)
        recordsbtn = findViewById(R.id.recordsbtn)
        rewardsbtn = findViewById(R.id.rewardsbtn)
        logoutbtn = findViewById(R.id.logoutbtn)
        Helpbtn = findViewById(R.id.Helpbtn)

        //Set click listeners
        expensebtn.setOnClickListener {
            Toast.makeText(this,"Open the expenses screen", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ExpensesPage::class.java)
            startActivity(intent)
        }


        reportsbtn.setOnClickListener {
            Toast.makeText(this,"Open the report screen", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ReportPage::class.java)
            startActivity(intent)

        }

        recordsbtn.setOnClickListener {
            Toast.makeText(this,"Open the records screen", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, RecordsPage::class.java)
            startActivity(intent)
        }

        rewardsbtn.setOnClickListener {
            Toast.makeText(this,"Open the rewards screen", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, RewardsPage::class.java)
            startActivity(intent)
        }

        Helpbtn.setOnClickListener {
            Toast.makeText(this,"Open the chatbot screen", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ChatbotPage::class.java)
            startActivity(intent)
        }


        logoutbtn.setOnClickListener {

            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupBottomNavigation()
    }

    // Navigation bar
    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_home
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_expenses -> {
                    startActivity(Intent(this, ExpensesPage::class.java))
                    true
                }
                R.id.nav_records -> {
                    startActivity(Intent(this, RecordsPage::class.java))
                    true
                }
                R.id.nav_reports -> {
                    startActivity(Intent(this, ReportPage::class.java))
                    true
                }
                R.id.nav_rewards -> {
                    startActivity(Intent(this, RewardsPage::class.java))
                    true
                }
                R.id.nav_help -> {
                    startActivity(Intent(this, ChatbotPage::class.java))
                    true
                }
                else -> false
            }
        }
    }
}