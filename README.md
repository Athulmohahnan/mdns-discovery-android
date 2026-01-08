📡 mDNS Discovery Android App

An Android application built as part of a machine test assignment.
The app demonstrates OAuth 2.0 login, local network device discovery using mDNS, local persistence, and public IP detail fetching, following clean and professional Android development practices.

🛠 Tech Stack

Language: Kotlin

UI: XML (Material Components)

Architecture: Simple layered structure (UI / Data / Util)

Concurrency: Kotlin Coroutines

Local Storage: Room (SQLite)

Authentication: Google OAuth 2.0

Network: HttpURLConnection (No third-party libraries)


✨ Features & Implementation
🔐 1. Login (OAuth 2.0)

Google OAuth 2.0-based login
Token cached locally
Silent authentication on next launch
Forced logout if silent authentication fails due to no network

Key points:
No hardcoded credentials
Clean separation of auth logic
Defensive network checks

🏠 2. Home Screen – mDNS Device Discovery

Discovers devices connected to the same Wi-Fi network using mDNS
Displays discovered devices in a RecyclerView
Each device shows:
Device name
IP address
Online / Offline status
Persistence logic:
Discovered devices are stored in SQLite (Room)

On app relaunch:
Cached devices are loaded immediately
All devices start as offline
Rediscovered devices are marked online

Efficiency:
RecyclerView updates handled using DiffUtil
Avoids unnecessary UI refreshes

📄 3. Device Detail Screen

Navigate from the home screen on the device tap
Fetches the current public IP using:
https://api.ipify.org?format=json

Fetches geo and network information using:
https://ipinfo.io/<IP>/geo


Displays:
Public IP
Location (city, region, country)
Organization / Carrier info

Important constraint followed:

❌ No third-party networking libraries (Retrofit / OkHttp not used)
✔ Implemented using HttpURLConnection with proper timeouts

📂 Project Structure
com. example.mdnsdiscovery
│
├── auth
│   └── GoogleAuthManager.kt
│
├── data
│   ├── local
│   │   ├── db (Room entities, DAO, database)
│   │   └── AuthPreferences.kt
│   └── remote
│       └── IpInfoService.kt
│
├── mdns
│   └── MdnsDiscoveryManager.kt
│
├── ui
│   ├── login
│   ├── home
│   │   ├── adapter
│   │   └── models
│   └── detail
│
└── util
    └── NetworkUtil.kt
