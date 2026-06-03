package com.example.prog7313ktpbudgetingapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

// this adapter that shows a list of expense records in a RecyclerView
class RecordsAdapter(private val expenses: List<Expense>) :
    RecyclerView.Adapter<RecordsAdapter.RecordViewHolder>() {

    // This holds the views for each expense item
    class RecordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
    }

    // Holds the views for each expense item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense_record, parent, false)
        return RecordViewHolder(view)
    }

    // Binds expense data to the views
    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val expense = expenses[position]
        holder.tvCategory.text = expense.category
        holder.tvAmount.text = String.format(Locale.getDefault(), "R%.2f", expense.amount ?: 0.0)
        holder.tvDate.text = expense.date
        holder.tvDescription.text = expense.description
    }

    override fun getItemCount() = expenses.size // Returns the total number of expense items
}
