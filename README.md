### NOTE: TO SUCCESSFULLY RUN THIS PROJECT - MAKE SURE TO DO THE FOLLOWING
- Replace the api key with your own ChatGPT API key in the "chatGPTApiKey" String variable in "defaultConfig" of the both "build.gradle.kts" files. (There is one for the "wear" and "mobileapp" module)
- In the "local.properties", replace "sdk.dir" with the directory path of where you cloned or saved the project on your computer.
- Make sure your android devices (wear and mobile), whether physical device or emulator, are paired using the "Google Pixel Watch" app or whichever wearable pairing apps you prefer.

# Android Wear Development: Final Group Project

![VoiceGPT Logo](./app-logo/VoiceGPT-logo.png)

---

## Table of Contents

1. [Group Details](#group-details)
2. [Project Title](#project-title)
3. [Project Description](#project-description)
    - [Wear OS App](#wear-os-app)
    - [Android App](#android-app)

---

## Group Details

**Group Name:**  
_GroupProjSec1-6 (Group 6)_

**Group Members:**

| S. No. | Student ID | Student Name                       |
|-------:|-----------:|------------------------------------|
|      1 |    8970204 | Gurjot Singh Saini                 |
|      2 |    8969004 | Sonam Rani                         |
|      3 |    8919998 | Olufemi Emmanuel Ojeyemi           |
|      4 |    8967584 | Arshdeep Singh                     |

---

## Project Title

**VoiceGPT – Smart Voice Assistant for Wear OS & Android**

---

## Project Description

This project aims to develop an intelligent, voice‑powered AI assistant available on both Wear OS smartwatches and Android smartphones. The core functionality revolves around integrating OpenAI's ChatGPT API with modern voice technologies to allow seamless, conversational interaction.

### Wear OS App

The Wear OS app will allow users to:

- **Tap a mic icon** to ask a question using voice.
- The converted text will be sent to ChatGPT via OpenAI’s API, and the generated response will be:
    - Displayed on‑screen as text
    - Read aloud using Text‑to‑Speech (TTS) functionality
- After receiving a response, users can:
    - Re‑listen to the response
    - Ask a follow‑up question
    - Start a new conversation

The UI will be optimized for smartwatch screens with a minimalist layout and intuitive navigation.

### Android App

The Android companion app will act as a control center for managing and viewing all conversations:

- **View chat history** synced from the watch.
- **Initiate new chats** via:
    - A text input box
    - Voice input (using the same speech‑to‑text + ChatGPT API integration)
- **Manual sync** button to reconcile data between watch and mobile for accurate history.
- **Persistent storage** exploration to maintain history across app restarts.
- UI following modern Material Design principles for a clean, interactive user experience.

---

VoiceGPT will demonstrate cross‑device data synchronization, AI interaction, and wearable technology integration—providing real‑world experience in Android and Wear OS development.  
:contentReference[oaicite:0]{index=0}&#8203;:contentReference[oaicite:1]{index=1}  
