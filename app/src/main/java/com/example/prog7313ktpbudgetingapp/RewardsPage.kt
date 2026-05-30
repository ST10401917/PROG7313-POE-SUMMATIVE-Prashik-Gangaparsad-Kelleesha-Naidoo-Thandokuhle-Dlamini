package com.example.prog7313ktpbudgetingapp

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale
import androidx.core.graphics.toColorInt

class RewardsPage : AppCompatActivity() {

    private lateinit var txtBudgetMaster: TextView
    private lateinit var txtSavingsStar: TextView
    private lateinit var txtExpenseTracker: TextView

    private lateinit var percentBudgetMaster: TextView
    private lateinit var percentSavingsStar: TextView
    private lateinit var percentExpenseTracker: TextView

    private lateinit var progressBudgetMaster: ProgressBar
    private lateinit var progressSavingsStar: ProgressBar
    private lateinit var progressExpenseTracker: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_rewards_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Bind views
        txtBudgetMaster = findViewById(R.id.txtBudgetMaster)
        txtSavingsStar = findViewById(R.id.txtSavingsStar)
        txtExpenseTracker = findViewById(R.id.txtExpenseTracker)

        percentBudgetMaster = findViewById(R.id.percentBudgetMaster)
        percentSavingsStar = findViewById(R.id.percentSavingsStar)
        percentExpenseTracker = findViewById(R.id.percentExpenseTracker)

        progressBudgetMaster = findViewById(R.id.progressBudgetMaster)
        progressSavingsStar = findViewById(R.id.progressSavingsStar)
        progressExpenseTracker = findViewById(R.id.progressExpenseTracker)
    }

    override fun onResume() {
        super.onResume()
        // Load badge data every time the page becomes visible
        loadRewards()
    }


    @SuppressLint("SetTextI18n")
    private fun loadRewards() {
        val prefs = getSharedPreferences("Rewards", MODE_PRIVATE)
        val locale = Locale.getDefault()

        // Budget Master
        val isBudgetMasterEarned = prefs.getBoolean("BudgetMaster", false)
        val isBudgetMasterOver = prefs.getBoolean("BudgetMasterOver", false)
        val budgetProgressInt = prefs.getInt("BudgetMasterProgress", 0)
        val budgetProgressFloat = prefs.getFloat("BudgetMasterProgressFloat", 0f)
        
        progressBudgetMaster.progress = budgetProgressInt
        
        if (isBudgetMasterEarned) {
            txtBudgetMaster.text = "🏅 Earned - Budget Master"
            percentBudgetMaster.text = "100%"
            percentBudgetMaster.setTextColor("#4CAF50".toColorInt()) // Green
        } else if (isBudgetMasterOver) {
            txtBudgetMaster.text = "❌ Over Budget - Budget Master"
            // Ensure they see it is slightly over 100% if rounding would hide it
            val displayVal = if (budgetProgressFloat <= 100f) 100.1f else budgetProgressFloat
            percentBudgetMaster.text = String.format(locale, "%.1f%%", displayVal)
            percentBudgetMaster.setTextColor(Color.RED)
        } else {
            txtBudgetMaster.text = "🔒 Locked - Budget Master"
            percentBudgetMaster.text = String.format(locale, "%.1f%%", budgetProgressFloat)
            percentBudgetMaster.setTextColor(Color.GRAY)
        }

        // 🏅 Savings Star
        val isSavingsStarEarned = prefs.getBoolean("SavingsStar", false)
        val savingsProgressInt = prefs.getInt("SavingsStarProgress", 0)
        val savingsProgressFloat = prefs.getFloat("SavingsStarProgressFloat", 0f)
        
        progressSavingsStar.progress = savingsProgressInt
        
        if (isSavingsStarEarned) {
            txtSavingsStar.text = "🏅 Earned - Savings Star"
            percentSavingsStar.text = "100%"
            percentSavingsStar.setTextColor("#4CAF50".toColorInt())
        } else {
            txtSavingsStar.text = "🔒 Locked - Savings Star"

            val displayVal = if (savingsProgressFloat > 99.9f && savingsProgressFloat < 100f) 99.9f else savingsProgressFloat
            percentSavingsStar.text = String.format(locale, "%.1f%%", displayVal)
            percentSavingsStar.setTextColor(Color.GRAY)
        }

        // 🏅 Expense Tracker
        val isExpenseTrackerEarned = prefs.getBoolean("ExpenseTracker", false)
        val expenseProgress = prefs.getInt("ExpenseTrackerProgress", 0)
        
        progressExpenseTracker.progress = expenseProgress
        percentExpenseTracker.text = "$expenseProgress%"

        if (isExpenseTrackerEarned) {
            txtExpenseTracker.text = "🏅 Earned - Expense Tracker"
            percentExpenseTracker.setTextColor("#4CAF50".toColorInt())
        } else {
            txtExpenseTracker.text = "🔒 Locked - Expense Tracker"
            percentExpenseTracker.setTextColor(Color.GRAY)
        }
    }
}

