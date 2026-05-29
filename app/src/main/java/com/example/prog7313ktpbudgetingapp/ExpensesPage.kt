package com.example.prog7313ktpbudgetingapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class ExpensesPage : AppCompatActivity() {

    private lateinit var categoryText: TextInputEditText
    private lateinit var amountText: TextInputEditText
    private lateinit var dateText: TextInputEditText
    private lateinit var descText: TextInputEditText
    private lateinit var saveExpenseBtn: MaterialButton

    private lateinit var minGoalText: TextInputEditText
    private lateinit var maxGoalText: TextInputEditText
    private lateinit var saveGoalBtn: MaterialButton

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_expenses_page)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Bind Expense views
        categoryText = findViewById(R.id.CategoryText)
        amountText = findViewById(R.id.AmountText)
        dateText = findViewById(R.id.DateText)
        descText = findViewById(R.id.DescipText)
        saveExpenseBtn = findViewById(R.id.Savebtn)

        // Bind Goal views
        minGoalText = findViewById(R.id.MinGoal)
        maxGoalText = findViewById(R.id.MaxGoal)
        saveGoalBtn = findViewById(R.id.Savebtn2)

        // Date picker for the date field
        dateText.setOnClickListener {
            showDatePicker()
        }

        // Save Expense listener
        saveExpenseBtn.setOnClickListener {
            saveExpense()
        }

        // Save Goal listener
        saveGoalBtn.setOnClickListener {
            saveGoal()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                dateText.setText(date)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun saveExpense() {
        val category = categoryText.text.toString().trim()
        val amountStr = amountText.text.toString().trim()
        val date = dateText.text.toString().trim()
        val desc = descText.text.toString().trim()

        if (category.isEmpty() || amountStr.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountStr.toDoubleOrNull()
        if (amount == null) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Push creates a unique ID for each expense entry
        val expenseRef = database.getReference("users").child(userId).child("expenses").push()
        val expense = Expense(expenseRef.key, category, amount, date, desc)

        expenseRef.setValue(expense).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Expense saved successfully", Toast.LENGTH_SHORT).show()
                clearExpenseFields()
            } else {
                Toast.makeText(this, "Failed to save expense: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveGoal() {
        val minGoalStr = minGoalText.text.toString().trim()
        val maxGoalStr = maxGoalText.text.toString().trim()

        if (minGoalStr.isEmpty() || maxGoalStr.isEmpty()) {
            Toast.makeText(this, "Please fill in both goal fields", Toast.LENGTH_SHORT).show()
            return
        }

        val minGoal = minGoalStr.toDoubleOrNull()
        val maxGoal = maxGoalStr.toDoubleOrNull()

        if (minGoal == null || maxGoal == null) {
            Toast.makeText(this, "Invalid goal amounts", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val goal = Goal(minGoal, maxGoal)
        database.getReference("users").child(userId).child("goals").setValue(goal)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Goals saved successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to save goals: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun clearExpenseFields() {
        categoryText.text?.clear()
        amountText.text?.clear()
        dateText.text?.clear()
        descText.text?.clear()
    }
}
