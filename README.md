# VoiceGPT

[![License](https://img.shields.io/github/license/Femosky/VoiceGPT.svg)](LICENSE) ![Last Commit](https://img.shields.io/github/last-commit/Femosky/VoiceGPT.svg) ![Issues](https://img.shields.io/github/issues/Femosky/VoiceGPT.svg)  

An intelligent, voice‑powered AI assistant available on both Wear OS smartwatches and Android smartphones. The core functionality revolves around integrating OpenAI's ChatGPT API with modern voice technologies to allow seamless, conversational interaction.

---

## ✨ Features

- **Real-Time Speech Recognition**  
  Uses the Web Speech API to convert your spoken words into text instantly.
- **GPT-Powered Conversation**  
  Sends your transcribed text to OpenAI’s ChatGPT API and retrieves a coherent response.
- **Text-to-Speech Output**  
  Reads ChatGPT’s replies back to you using browser TTS for a fully voice-driven experience.
- **Interactive Chat UI**  
  Displays both your spoken inputs and GPT’s text responses in a clean, scrollable chat window.
- **Conversation History**  
  Maintains the context of your session so the model “remembers” earlier messages.
- **Configurable Voice Settings**  
  Select between different voices, pitch, and speaking rates based on your preference.
- **Responsive Design**  
  Works equally well on desktop, tablet, and mobile browsers.

---

## 🛠 Tech Stack
  
- **Java + Android SDK + Wearable** 
- **REST API**

---

## 📥 Quick Start

1. **Clone this repo**  
   ```bash
   git clone https://github.com/Femosky/MapOfSecrets.git
   cd MapOfSecrets
   
2. **Replace API key with yours**
   Navigate to both module's `build.gradle.kts` file and replace `chatGPTApiKey` here `android` -> `defaultConfig`
   ```bash
   val chatGPTApiKey: String = project.findProperty("CHATGPT_API_KEY") as? String ?: "REPLACE_WITH_YOUR_CHATGPT_API_KEY"
   buildConfigField("String", "CHATGPT_API_KEY", "\"$chatGPTApiKey\"" )
   
3. **Update the `sdk.dir` to yours**
   In the `local.properties`, replace `sdk.dir` with the directory path of where you cloned or saved the project on your computer.

4. **Pair your Phone and Watch emulators together**
   - Open Device Manager and make sure you already have a Pixel and Wear OS emulator of at least API 34 setup (you can use any other version, but the TextToSpeach engine only works on aPI 34 and above).
   - Click on the three dots on the Pixel and click on `Pair Wearable`.
   - Follow the pairing process that automatically installs the `Google Pixel Watch` app on both emulators.
   
   <img width=30% height=30% alt="pair wearable screenshot" src="https://github.com/user-attachments/assets/e8981b6c-7099-4e7c-9fde-4dbf398ce97d" />

5. **Run the project on both emulators.**

---

## 📂 Project Structure

VoiceGPT/
├── mobileapp/                      # Android phone module
│   ├── manifests/                  # AndroidManifest.xml
│   ├── java/com/example/           # App package namespace
│   │   └── com.example.group6finalgroupproject/
│   │       ├── activity/           # Activities & Compose entry points
│   │       ├── adapter/            # RecyclerView adapters (if used)
│   │       ├── helpers/            # Helper classes (permissions, UI utils)
│   │       ├── model/              # Data models (Request/Response DTOs)
│   │       ├── service/            # Retrofit/OpenAI API service interfaces
│   │       └── utils/              # Utility functions (coroutines, logging)
│   ├── res/                        # XML layouts, drawables, strings, etc.
│   │   ├── drawable/
│   │   ├── layout/
│   │   ├── menu/
│   │   ├── mipmap/
│   │   └── values/
│   └── build.gradle.kts            # Module-level build file
│
├── wear/                           # Wear OS watch module
│   ├── manifests/
│   ├── java/com/example/
│   │   └── com.example.group6finalgroupproject/
│   │       ├── activity/           # Wear-specific Activities & Compose UIs
│   │       ├── adapter/            # (If lists are shown on watch)
│   │       ├── helper/             # Permission & speech helpers for Wear
│   │       ├── model/              # Shared data models
│   │       ├── service/            # API clients (OpenAI, etc.)
│   │       └── utils/              # Utility functions (TTS, logging)
│   ├── res/
│   │   ├── drawable/
│   │   ├── layout/
│   │   ├── mipmap/
│   │   └── values/
│   └── build.gradle.kts
│
├── build.gradle.kts                # Top-level Gradle Kotlin DSL config
├── settings.gradle.kts             # Modules inclusion
├── gradle.properties               # Global Gradle settings
├── local.properties                # (Not committed) Holds OPENAI_API_KEY
├── gradlew / gradlew.bat            # Gradle wrapper scripts
└── gradle/                         # Gradle wrapper files
