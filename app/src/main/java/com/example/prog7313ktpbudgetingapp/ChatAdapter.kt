package com.example.prog7313ktpbudgetingapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(private val messages: List<ChatMessage>) : // This adapter shows chat messages in the RecyclerView
RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    // This stores the views for each chat message
    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val botLayout: LinearLayout = view.findViewById(R.id.botMessageLayout)
        val userLayout: LinearLayout = view.findViewById(R.id.userMessageLayout)
        val botText: TextView = view.findViewById(R.id.botMessageText)
        val userText: TextView = view.findViewById(R.id.userMessageText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return ChatViewHolder(view)
    }

    // Shows the message and checks if it is from the user or bot
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatMessage = messages[position]
        if (chatMessage.isUser) {
            holder.userLayout.visibility = View.VISIBLE
            holder.botLayout.visibility = View.GONE
            holder.userText.text = chatMessage.message
        } else {
            holder.userLayout.visibility = View.GONE
            holder.botLayout.visibility = View.VISIBLE
            holder.botText.text = chatMessage.message
        }
    }

    override fun getItemCount() = messages.size  // Returns the total number of messages
}
