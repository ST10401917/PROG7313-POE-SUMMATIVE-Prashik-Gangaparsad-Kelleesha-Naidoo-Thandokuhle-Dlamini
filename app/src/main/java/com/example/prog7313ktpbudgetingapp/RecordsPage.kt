package com.example.prog7313ktpbudgetingapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class RecordsPage : AppCompatActivity() {

    private lateinit var rvRecords: RecyclerView
    private lateinit var adapter: RecordsAdapter
    private val expenseList = mutableListOf<Expense>()
    
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_records_page)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        rvRecords = findViewById(R.id.rvRecords)
        rvRecords.layoutManager = LinearLayoutManager(this)
        adapter = RecordsAdapter(expenseList)
        rvRecords.adapter = adapter

        fetchExpenses()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            bottomNav.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        setupBottomNavigation()
    }

    // Reads expense data from Firebase and updates the list
    private fun fetchExpenses() {
        val userId = auth.currentUser?.uid ?: return
        val ref = database.getReference("users").child(userId).child("expenses")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                expenseList.clear()
                for (child in snapshot.children) {
                    val expense = child.getValue(Expense::class.java)
                    if (expense != null) {
                        expenseList.add(expense)
                    }
                }
                expenseList.reverse() // Newest first
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // Navigation bar
    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_records
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomePage::class.java))
                    true
                }
                R.id.nav_expenses -> {
                    startActivity(Intent(this, ExpensesPage::class.java))
                    true
                }
                R.id.nav_records -> true
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
