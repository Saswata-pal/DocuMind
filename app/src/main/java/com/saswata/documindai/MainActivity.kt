package com.saswata.documindai

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.saswata.documindai.databinding.ActivityMainBinding
import com.saswata.documindai.llm.LlamaBridge
import com.saswata.documindai.ui.ChatAdapter
import com.saswata.documindai.ui.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val messages = mutableListOf<ChatMessage>()
    private lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = ChatAdapter(messages)
        binding.recyclerViewChat.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewChat.adapter = adapter

        // Greeting so the chat doesn't open empty
        addMessage(ChatMessage("Hi! Load a document and ask me anything about it.", isUser = false))

        binding.buttonSend.setOnClickListener { sendMessage() }
    }

    private fun sendMessage() {
        val text = binding.editTextMessage.text.toString().trim()
        if (text.isEmpty()) return

        addMessage(ChatMessage(text, isUser = true))
        binding.editTextMessage.text.clear()

        // Real JNI call — currently returns "[error] model not loaded" since no
        // GGUF file has been wired in yet. This still proves the full
        // Kotlin -> JNI -> C++ -> llama.cpp round trip works end to end.
        lifecycleScope.launch {
            val response = withContext(Dispatchers.Default) {
                LlamaBridge.runInference(text)
            }
            addMessage(ChatMessage(response, isUser = false))
        }
    }

    private fun addMessage(message: ChatMessage) {
        adapter.addMessage(message)
        binding.recyclerViewChat.scrollToPosition(messages.size - 1)
    }
}