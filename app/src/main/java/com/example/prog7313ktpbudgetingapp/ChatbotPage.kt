package com.example.prog7313ktpbudgetingapp

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class ChatbotPage : AppCompatActivity() {
    private lateinit var questionSpinner: Spinner
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var btnSend: Button
    
    private val chatMessages = mutableListOf<ChatMessage>()
    private lateinit var chatAdapter: ChatAdapter

    // Stores the predefined chatbot questions and answers
    private val faqData = mapOf(
        "Select a question..." to "",
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
        chatRecyclerView = findViewById(R.id.rv_chat)
        btnSend = findViewById(R.id.btn_send)

        setupRecyclerView()
        setupSpinner()
        setupBottomNavigation()

        btnSend.setOnClickListener {
            handleSendMessage()
        }

        // Displays a welcome message when the chatbot opens
        addMessage("Hello! I am your KTP Budget Assistant. How can I help you today?", false)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(chatMessages)
        chatRecyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        chatRecyclerView.adapter = chatAdapter
    }

    // Loads the chatbot questions into the spinner
    private fun setupSpinner() {
        val questions = faqData.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, questions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        questionSpinner.adapter = adapter
    }

    // Checks to see which question is selected from the spinner and displays the answer
    private fun handleSendMessage() {
        val selectedQuestion = questionSpinner.selectedItem.toString()
        if (selectedQuestion != "Select a question...") {
            // Add user message
            addMessage(selectedQuestion, true)
            
            // Add bot response
            val answer = faqData[selectedQuestion] ?: "I'm sorry, I don't have an answer for that."
            addMessage(answer, false)
            
            // Reset spinner
            questionSpinner.setSelection(0)
        }
    }

    // Adds a message to the chat and updates the RecyclerView
    private fun addMessage(text: String, isUser: Boolean) {
        chatMessages.add(ChatMessage(text, isUser))
        chatAdapter.notifyItemInserted(chatMessages.size - 1)
        chatRecyclerView.smoothScrollToPosition(chatMessages.size - 1)
    }

    // Navigation bar
    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_help
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
                R.id.nav_rewards -> {
                    startActivity(Intent(this, RewardsPage::class.java))
                    true
                }
                R.id.nav_help -> true
                else -> false
            }
        }
    }
}
