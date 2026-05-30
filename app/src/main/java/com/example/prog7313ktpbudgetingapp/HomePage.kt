package com.example.prog7313ktpbudgetingapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomePage : AppCompatActivity() {
    private  lateinit var expensebtn: Button
    private  lateinit var reportsbtn: Button
    private  lateinit var logoutbtn: Button
    private  lateinit var rewardsbtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)

        //Typecasting
        expensebtn = findViewById(R.id.expensebtn)
        reportsbtn = findViewById(R.id.reportsbtn)
        rewardsbtn = findViewById(R.id.rewardsbtn)
        logoutbtn = findViewById(R.id.logoutbtn)

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

        rewardsbtn.setOnClickListener {
            Toast.makeText(this,"Open the rewards screen", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, RewardsPage::class.java)
            startActivity(intent)
        }

        logoutbtn.setOnClickListener {
            Toast.makeText(this,"Logout", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}