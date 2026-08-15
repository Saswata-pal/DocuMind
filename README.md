# DocuMind AI

A privacy-first, fully offline Android document & chat assistant. Load PDFs or Markdown files and ask questions about them — no internet connection required, no data ever leaves the device.

## Status

🚧 Early development — project scaffolding in progress.

## Planned Architecture

- **UI**: Kotlin + XML (View-based), RecyclerView chat interface
- **On-device LLM**: llama.cpp via JNI/NDK, running a quantized GGUF model
- **RAG pipeline**: PDF/Markdown parsing → chunking → on-device embeddings (TFLite sentence-transformer) → cosine similarity retrieval → prompt-stuffing
- **Target**: Android 8.0 (API 26) and above
- **App size budget**: ~300–400MB, achieved via a small quantized model (sub-1B parameters)

## Requirements

- Android Studio (latest stable)
- Android SDK 26+ (compileSdk 35)
- Kotlin 2.0+

## Setup

1. Clone the repo
2. Open in Android Studio
3. Let Gradle sync
4. Run on a physical device or emulator (API 26+)

## License

TBD