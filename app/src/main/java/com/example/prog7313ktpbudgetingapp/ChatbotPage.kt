package com.example.prog7313ktpbudgetingapp

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ChatbotPage : AppCompatActivity() {
    private  lateinit var questionSpinner: Spinner
    private  lateinit var answerTextView: TextView

    // FAQ Data
    private val faqData = mapOf(
        "How do I add an expense?" to "Go to the Expenses page and click on 'Save Expense' after filling in the details.",
        "How do I set a goal?" to "You can set your minimum and maximum monthly goals in the Goal section.",
        "Where can I see my reports?" to "Click on the 'Reports' button on the home screen to see your spending analysis.",
        "How do I earn rewards?" to "Complete your financial goals to unlock badges and medals in the Rewards section.",
        "Can I logout?" to "Yes, there is a Logout button on the home page."
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chatbot_page)


        questionSpinner = findViewById(R.id.spinner_questions)
        answerTextView = findViewById(R.id.tv_answer)

        setupSpinner()


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupSpinner() {
        val questions = faqData.keys.toList()

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            questions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        questionSpinner.adapter = adapter

        //Set click listeners
        questionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                val selectedQuestion = questions[position]
                answerTextView.text = faqData[selectedQuestion]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                answerTextView.text = getString(R.string.select_a_question_above_to_see_an_answer)
            }
        }
    }
}
