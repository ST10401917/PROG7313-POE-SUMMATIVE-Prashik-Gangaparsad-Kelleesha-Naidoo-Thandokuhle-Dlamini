package com.example.prog7313ktpbudgetingapp

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReportPage : AppCompatActivity() {

    private lateinit var startingDateEdit: TextInputEditText
    private lateinit var endingDateEdit: TextInputEditText
    private lateinit var filterBtn: MaterialButton
    private lateinit var totalText: TextView
    private lateinit var goalStatusText: TextView
    private lateinit var goalChart: HorizontalBarChart
    private lateinit var categoryChart: PieChart

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private val dateFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
    private var allExpenses = mutableListOf<Expense>()
    private var userGoal: Goal? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_report_page)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        startingDateEdit = findViewById(R.id.startingDate)
        endingDateEdit = findViewById(R.id.endingDate)
        filterBtn = findViewById(R.id.filter_Expenses)
        totalText = findViewById(R.id.textView9)
        goalStatusText = findViewById(R.id.goalStatusText)
        goalChart = findViewById(R.id.goalChart)
        categoryChart = findViewById(R.id.categoryChart)

        setupDatePickers()
        
        filterBtn.setOnClickListener {
            updateReport()
        }

        fetchData()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupDatePickers() {
        startingDateEdit.setOnClickListener { showDatePicker(startingDateEdit) }
        endingDateEdit.setOnClickListener { showDatePicker(endingDateEdit) }
        
        // Default range: last 30 days
        val calendar = Calendar.getInstance()
        val end = dateFormat.format(calendar.time)
        calendar.add(Calendar.DAY_OF_YEAR, -30)
        val start = dateFormat.format(calendar.time)
        
        startingDateEdit.setText(start)
        endingDateEdit.setText(end)
    }

    private fun showDatePicker(editText: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                val date = "$day/${month + 1}/$year"
                editText.setText(date)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun fetchData() {
        val userId = auth.currentUser?.uid ?: return
        
        // Fetch Goals
        database.getReference("users").child(userId).child("goals")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userGoal = snapshot.getValue(Goal::class.java)
                    updateReport()
                }
                override fun onCancelled(error: DatabaseError) {}
            })

        // Fetch Expenses
        database.getReference("users").child(userId).child("expenses")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    allExpenses.clear()
                    for (child in snapshot.children) {
                        val expense = child.getValue(Expense::class.java)
                        if (expense != null) {
                            allExpenses.add(expense)
                        }
                    }
                    updateReport()
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun updateReport() {
        val startDateStr = startingDateEdit.text.toString()
        val endDateStr = endingDateEdit.text.toString()

        if (startDateStr.isEmpty() || endDateStr.isEmpty()) return

        val startDate = try { dateFormat.parse(startDateStr) } catch (e: Exception) { null }
        val endDate = try { dateFormat.parse(endDateStr) } catch (e: Exception) { null }

        if (startDate == null || endDate == null) return

        val filteredExpenses = allExpenses.filter {
            val expenseDate = it.date?.let { d -> try { dateFormat.parse(d) } catch (e: Exception) { null } }
            expenseDate != null && !expenseDate.before(startDate) && !expenseDate.after(endDate)
        }

        displaySummary(filteredExpenses)
        displayCategoryChart(filteredExpenses)
        displayGoalChart(filteredExpenses)
    }

    private fun displaySummary(expenses: List<Expense>) {
        val total = expenses.sumOf { it.amount ?: 0.0 }
        totalText.text = String.format(Locale.getDefault(), "Total: R%.2f", total)

        val goal = userGoal
        if (goal != null) {
            val min = goal.minGoal ?: 0.0
            val max = goal.maxGoal ?: 0.0
            
            when {
                total < min -> {
                    goalStatusText.text = getString(R.string.status_below_min)
                    goalStatusText.setTextColor(Color.BLUE)
                }
                total in min..max -> {
                    goalStatusText.text = getString(R.string.status_within_range)
                    goalStatusText.setTextColor(Color.parseColor("#388E3C")) // Green
                }
                else -> {
                    goalStatusText.text = getString(R.string.status_above_max)
                    goalStatusText.setTextColor(Color.RED)
                }
            }
        } else {
            goalStatusText.text = getString(R.string.status_no_goals)
        }
    }

    private fun displayCategoryChart(expenses: List<Expense>) {
        val categoryMap = expenses.groupBy { it.category ?: "Other" }
            .mapValues { it.value.sumOf { e -> e.amount ?: 0.0 } }

        val entries = categoryMap.map { PieEntry(it.value.toFloat(), it.key) }
        val dataSet = PieDataSet(entries, "")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextSize = 14f
        dataSet.valueTextColor = Color.BLACK

        categoryChart.data = PieData(dataSet)
        categoryChart.description.isEnabled = false
        categoryChart.centerText = "Expenses"
        categoryChart.animateY(1000)
        categoryChart.invalidate()
    }

    private fun displayGoalChart(expenses: List<Expense>) {
        val totalSpending = expenses.sumOf { it.amount ?: 0.0 }.toFloat()
        val minGoal = userGoal?.minGoal?.toFloat() ?: 0f
        val maxGoal = userGoal?.maxGoal?.toFloat() ?: 0f

        val entries = mutableListOf<BarEntry>()
        entries.add(BarEntry(0f, totalSpending))
        entries.add(BarEntry(1f, minGoal))
        entries.add(BarEntry(2f, maxGoal))

        val dataSet = BarDataSet(entries, "Spending vs Goals")
        dataSet.colors = listOf(
            Color.parseColor("#4CAF50"), // Spending - Green
            Color.BLUE,                  // Min Goal - Blue
            Color.RED                    // Max Goal - Red
        )

        val labels = listOf(
            getString(R.string.actual_spending),
            getString(R.string.min_goal_label),
            getString(R.string.max_goal_label)
        )
        
        goalChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        goalChart.xAxis.granularity = 1f
        goalChart.xAxis.setDrawGridLines(false)
        goalChart.xAxis.labelCount = labels.size
        
        goalChart.data = BarData(dataSet)
        goalChart.description.isEnabled = false
        goalChart.animateY(1000)
        goalChart.invalidate()
    }
}
