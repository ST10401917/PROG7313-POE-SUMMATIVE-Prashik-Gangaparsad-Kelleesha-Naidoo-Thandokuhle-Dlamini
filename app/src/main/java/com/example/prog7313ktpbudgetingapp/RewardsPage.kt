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
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import androidx.core.content.edit
import kotlin.math.roundToLong

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

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private var rewardsListener: com.google.firebase.database.ValueEventListener? = null

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

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_rewards
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
                R.id.nav_records -> {
                    startActivity(Intent(this, RecordsPage::class.java))
                    true
                }
                R.id.nav_reports -> {
                    startActivity(Intent(this, ReportPage::class.java))
                    true
                }
                R.id.nav_rewards -> true
                R.id.nav_help -> {
                    startActivity(Intent(this, ChatbotPage::class.java))
                    true
                }
                else -> false
            }
        }
    }

    override fun onStart() {
        super.onStart()
        startListeningForRewards()
    }

    override fun onStop() {
        super.onStop()
        stopListeningForRewards()
    }

    private fun startListeningForRewards() {
        val userId = auth.currentUser?.uid ?: return
        val userRef = database.getReference("users").child(userId)
        val prefs = getSharedPreferences("Rewards", MODE_PRIVATE)

        rewardsListener = userRef.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val expensesSnapshot = snapshot.child("expenses")
                val goalsSnapshot = snapshot.child("goals")

                var totalCents = 0L
                var expenseCount = 0

                for (child in expensesSnapshot.children) {
                    val amountValue = child.child("amount").value
                    val amount = (amountValue as? Number)?.toDouble() ?: 0.0
                    totalCents += (amount * 100.0).roundToLong()
                    expenseCount++
                }

                val minGoalVal = goalsSnapshot.child("minGoal").value
                val maxGoalVal = goalsSnapshot.child("maxGoal").value
                val minGoal = (minGoalVal as? Number)?.toDouble() ?: 0.0
                val maxGoal = (maxGoalVal as? Number)?.toDouble() ?: 0.0

                prefs.edit {
                    val minCents = (minGoal * 100.0).roundToLong()
                    val maxCents = (maxGoal * 100.0).roundToLong()

                    // Expense Tracker
                    val trackerProgress = (expenseCount * 100 / 10).coerceAtMost(100)
                    putInt("ExpenseTrackerProgress", trackerProgress)
                    putBoolean("ExpenseTracker", expenseCount >= 10)

                    // Budget Master
                    if (maxCents > 0) {
                        val budgetProgress = if (totalCents > 0) {
                            (totalCents.toDouble() / maxCents.toDouble()) * 100.0
                        } else 0.0
                        putFloat("BudgetMasterProgressFloat", budgetProgress.toFloat())
                        putInt("BudgetMasterProgress", budgetProgress.toInt().coerceAtMost(100))
                        putBoolean("BudgetMaster", expenseCount > 0 && totalCents <= maxCents)
                        putBoolean("BudgetMasterOver", expenseCount > 0 && totalCents > maxCents)
                    } else {
                        putFloat("BudgetMasterProgressFloat", 0f)
                        putInt("BudgetMasterProgress", 0)
                        putBoolean("BudgetMaster", false)
                        putBoolean("BudgetMasterOver", false)
                    }

                    // Savings Star
                    if (minCents > 0) {
                        val savingsProgress = if (totalCents > 0) {
                            (totalCents.toDouble() / minCents.toDouble()) * 100.0
                        } else 0.0
                        putFloat("SavingsStarProgressFloat", savingsProgress.toFloat())
                        putInt("SavingsStarProgress", savingsProgress.toInt().coerceAtMost(100))
                        putBoolean("SavingsStar", totalCents >= minCents)
                    } else {
                        putFloat("SavingsStarProgressFloat", 0f)
                        putInt("SavingsStarProgress", 0)
                        putBoolean("SavingsStar", false)
                    }
                }
                loadRewards()
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                android.util.Log.e("RewardsPage", "Error listening for rewards", error.toException())
            }
        })
    }

    private fun stopListeningForRewards() {
        val userId = auth.currentUser?.uid ?: return
        rewardsListener?.let {
            database.getReference("users").child(userId).removeEventListener(it)
        }
    }

    override fun onResume() {
        super.onResume()
        // Initial load from local data
        loadRewards()
    }

    @SuppressLint("SetTextI18n")
    private fun updateBadge(
        earned: Boolean,
        badgeName: String,
        progress: Int,
        percentText: String,
        progressBar: ProgressBar,
        titleView: TextView,
        percentView: TextView
    ) {
        if (earned) {
            progressBar.progress = 100
            titleView.text = "🏅 Earned - $badgeName"
            percentView.text = "100%"
            percentView.setTextColor("#4CAF50".toColorInt())
        } else {
            progressBar.progress = progress
            titleView.text = "🔒 Locked - $badgeName"
            percentView.text = percentText
            percentView.setTextColor(Color.GRAY)
        }
    }


    @SuppressLint("SetTextI18n")
    private fun loadRewards() {

        val prefs = getSharedPreferences("Rewards", MODE_PRIVATE)
        val locale = Locale.getDefault()

        // Budget Master
        val budgetEarned = prefs.getBoolean("BudgetMaster", false)
        val budgetOver = prefs.getBoolean("BudgetMasterOver", false)
        val budgetProgress = prefs.getInt("BudgetMasterProgress", 0)
        val budgetProgressFloat = prefs.getFloat("BudgetMasterProgressFloat", 0f)

        if (budgetOver) {
            progressBudgetMaster.progress = budgetProgress

            txtBudgetMaster.text = "❌ Over Budget - Budget Master"

            val displayVal =
                if (budgetProgressFloat <= 100f) 100.1f
                else budgetProgressFloat

            percentBudgetMaster.text =
                String.format(locale, "%.1f%%", displayVal)

            percentBudgetMaster.setTextColor(Color.RED)
        }
        else {
            updateBadge(
                budgetEarned,
                "Budget Master",
                budgetProgress,
                if (budgetEarned) "100%"
                else String.format(locale, "%.1f%%", budgetProgressFloat),
                progressBudgetMaster,
                txtBudgetMaster,
                percentBudgetMaster
            )
        }

        // Savings Star
        val savingsEarned = prefs.getBoolean("SavingsStar", false)
        val savingsProgress = prefs.getInt("SavingsStarProgress", 0)
        val savingsProgressFloat =
            prefs.getFloat("SavingsStarProgressFloat", 0f)

        val savingsText =
            if (savingsEarned)
                "100%"
            else
                String.format(
                    locale,
                    "%.1f%%",
                    if (savingsProgressFloat > 99.9f &&
                        savingsProgressFloat < 100f)
                        99.9f
                    else
                        savingsProgressFloat
                )

        updateBadge(
            savingsEarned,
            "Savings Star",
            savingsProgress,
            savingsText,
            progressSavingsStar,
            txtSavingsStar,
            percentSavingsStar
        )

        // Expense Tracker
        val expenseEarned =
            prefs.getBoolean("ExpenseTracker", false)

        val expenseProgress =
            prefs.getInt("ExpenseTrackerProgress", 0)

        updateBadge(
            expenseEarned,
            "Expense Tracker",
            expenseProgress,
            "$expenseProgress%",
            progressExpenseTracker,
            txtExpenseTracker,
            percentExpenseTracker
        )
    }
}

