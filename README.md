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

| Path                                                                      | Description                                         |
| ------------------------------------------------------------------------- | --------------------------------------------------- |
| `VoiceGPT/`                                                               | Project root directory                              |
| `VoiceGPT/mobileapp/`                                                     | Android phone module                                |
| `VoiceGPT/mobileapp/manifests/`                                           | AndroidManifest.xml                                 |
| `VoiceGPT/mobileapp/java/com/example/group6finalgroupproject/activity/`   | Activities & Compose entry points                   |
| `VoiceGPT/mobileapp/java/com/example/group6finalgroupproject/adapter/`    | RecyclerView adapters (if used)                     |
| `VoiceGPT/mobileapp/java/com/example/group6finalgroupproject/helpers/`    | Helper classes (permissions, UI utils)              |
| `VoiceGPT/mobileapp/java/com/example/group6finalgroupproject/model/`      | Data models (Request/Response DTOs)                 |
| `VoiceGPT/mobileapp/java/com/example/group6finalgroupproject/service/`    | Retrofit/OpenAI API service interfaces              |
| `VoiceGPT/mobileapp/java/com/example/group6finalgroupproject/utils/`      | Utility functions (coroutines, logging)             |
| `VoiceGPT/mobileapp/res/`                                                 | XML layouts, drawables, strings, etc.               |
| `VoiceGPT/mobileapp/res/drawable/`                                        | Drawable resources                                  |
| `VoiceGPT/mobileapp/res/layout/`                                          | Layout XML files                                    |
| `VoiceGPT/mobileapp/res/menu/`                                            | Menu resource files                                 |
| `VoiceGPT/mobileapp/res/mipmap/`                                          | Launcher icons                                      |
| `VoiceGPT/mobileapp/res/values/`                                          | Values resources (strings, dimensions, styles)      |
| `VoiceGPT/mobileapp/build.gradle.kts`                                     | Module-level Gradle build script                    |
| `VoiceGPT/wear/`                                                          | Wear OS watch module                                |
| `VoiceGPT/wear/manifests/`                                                | AndroidManifest.xml for Wear OS                     |
| `VoiceGPT/wear/java/com/example/group6finalgroupproject/activity/`        | Wear-specific Activities & Compose UIs              |
| `VoiceGPT/wear/java/com/example/group6finalgroupproject/adapter/`         | (If lists are shown on watch)                       |
| `VoiceGPT/wear/java/com/example/group6finalgroupproject/helper/`          | Permission & speech helpers for Wear OS             |
| `VoiceGPT/wear/java/com/example/group6finalgroupproject/model/`           | Shared data models                                  |
| `VoiceGPT/wear/java/com/example/group6finalgroupproject/service/`         | API clients (OpenAI, etc.)                          |
| `VoiceGPT/wear/java/com/example/group6finalgroupproject/utils/`           | Utility functions (TTS, logging)                    |
| `VoiceGPT/wear/res/`                                                      | XML layouts, drawables, strings for Wear OS         |
| `VoiceGPT/wear/res/drawable/`                                             | Drawable resources                                  |
| `VoiceGPT/wear/res/layout/`                                               | Layout XML files                                    |
| `VoiceGPT/wear/res/mipmap/`                                               | Launcher icons                                      |
| `VoiceGPT/wear/res/values/`                                               | Values resources (strings, dimensions, styles)      |
| `VoiceGPT/wear/build.gradle.kts`                                          | Wear OS module Gradle build script                  |
| `VoiceGPT/build.gradle.kts`                                               | Top-level Gradle Kotlin DSL configuration           |
| `VoiceGPT/settings.gradle.kts`                                            | Modules inclusion settings                          |
| `VoiceGPT/gradle.properties`                                              | Global Gradle settings                              |
| `VoiceGPT/local.properties`                                               | (Not committed) Holds `OPENAI_API_KEY`              |
| `VoiceGPT/gradlew` / `VoiceGPT/gradlew.bat`                               | Gradle wrapper scripts                              |
| `VoiceGPT/gradle/`                                                        | Gradle wrapper files                                |


--- 

## 📄 License

Distributed under the MIT License. See LICENSE for more details.

## ✉️ Contact

[Contact me here](https://femiojeyemi.com/contact)
