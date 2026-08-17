package com.saswata.documindai.llm

/**
 * Thin JNI bridge to the native llama.cpp inference engine.
 * All calls are blocking — always invoke from a background coroutine
 * (Dispatchers.Default or a dedicated single-thread executor), never
 * from the main thread.
 */
object LlamaBridge {

    init {
        System.loadLibrary("documind")
    }

    external fun loadModel(modelPath: String): Boolean

    external fun runInference(prompt: String): String

    external fun unloadModel()
}