#include <jni.h>
#include <string>
#include <android/log.h>
#include "llama.h"

#define TAG "DocuMindNative"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

static llama_model* g_model = nullptr;
static llama_context* g_ctx = nullptr;

extern "C" JNIEXPORT jboolean JNICALL
Java_com_saswata_documindai_llm_LlamaBridge_loadModel(
        JNIEnv* env, jobject /* this */, jstring modelPath) {

    const char* path = env->GetStringUTFChars(modelPath, nullptr);
    LOGI("Loading model from: %s", path);

    llama_model_params model_params = llama_model_default_params();
    g_model = llama_model_load_from_file(path, model_params);
    env->ReleaseStringUTFChars(modelPath, path);

    if (g_model == nullptr) {
        LOGE("Failed to load model");
        return JNI_FALSE;
    }

    llama_context_params ctx_params = llama_context_default_params();
    ctx_params.n_ctx = 2048;   // context window — keep small for tiny models
    ctx_params.n_threads = 4;  // tune based on device core count later

    g_ctx = llama_init_from_model(g_model, ctx_params);
    if (g_ctx == nullptr) {
        LOGE("Failed to create context");
        llama_model_free(g_model);
        g_model = nullptr;
        return JNI_FALSE;
    }

    LOGI("Model loaded successfully");
    return JNI_TRUE;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_saswata_documindai_llm_LlamaBridge_runInference(
        JNIEnv* env, jobject /* this */, jstring prompt) {

    if (g_ctx == nullptr || g_model == nullptr) {
        return env->NewStringUTF("[error] model not loaded");
    }

    const char* promptChars = env->GetStringUTFChars(prompt, nullptr);
    std::string promptStr(promptChars);
    env->ReleaseStringUTFChars(prompt, promptChars);

    // NOTE: this is a bare-bones single-shot completion stub, not a full
    // sampling/streaming loop. It exists to prove the JNI <-> llama.cpp
    // pipeline round-trips correctly. Token sampling, streaming, and a
    // proper chat template will replace this in the next pass.
    std::string result = "[stub] received prompt of length " + std::to_string(promptStr.size());
    LOGI("Inference stub called, prompt length: %zu", promptStr.size());

    return env->NewStringUTF(result.c_str());
}

extern "C" JNIEXPORT void JNICALL
Java_com_saswata_documindai_llm_LlamaBridge_unloadModel(
        JNIEnv* env, jobject /* this */) {
if (g_ctx) {
llama_free(g_ctx);
g_ctx = nullptr;
}
if (g_model) {
llama_model_free(g_model);
g_model = nullptr;
}
LOGI("Model unloaded");
}