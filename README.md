<p align="center">
  <img src="banner.png" alt="GUI Maker Banner" width="100%">
</p>

# GUI Maker

**GUI Maker** is a powerful datapack for Minecraft mapmakers, designed to create fully customizable Inventory GUIs using a clean, storage‑based architecture.

* 🔗 **Official Modrinth Page:** [https://modrinth.com/datapack/gui-maker](https://modrinth.com/datapack/gui-maker)
* 🧪 **This Repository:** Work‑in‑Progress and Unstable builds
* ✅ **Stable Releases:** Published on Modrinth only
* 📦 **Dependency:** [GU Library](https://github.com/gibbsly/gu/tree/main) (embedded)

---

## Supported Versions

**Minecraft 1.20.2 – 1.21.10**

> ⚠️ Always check the release notes before installing.

* For **Minecraft 1.21.6+**, a **world restart is required** after installing the datapack.

---

## What’s Different?

This is a fully **rewritten** version of GUI Maker with a focus on clarity, performance, and extensibility.

* ✨ Cleaned and refactored codebase for **easy readability**
* ⚡ Optimized execution to minimize performance overhead
* 🧱 New foundational system for **special GUI items**, including:

  * Toggle Buttons
  * Item Holders
* 💾 Built‑in **player cache system**, allowing each player to maintain their own GUI state
* 🧠 Storage‑driven design using macros and structured storage paths (powered by GU)

---

## About GUI Maker

GUI Maker is a **mapmaking utility datapack** that enables creators to build advanced Inventory‑based GUIs directly inside their Minecraft worlds.

* Uses a **storage‑based architecture** to define page layouts
* GUI pages are rendered dynamically using macros
* Designed for scalability and multi‑GUI environments

📜 **License:** MIT
You are free to download, modify, and adapt the source code to your needs.

---

## Features

### 🧩 Multiple Inventory GUIs

* Create and manage **multiple GUIs** within the same world
* Built‑in **page navigation** and page‑switching support

### 🚀 Performance‑Efficient

* Designed to avoid lag spikes and unnecessary function calls
* If you believe further optimizations are possible, feel free to open an issue or fork the project

---

## Important Notes

### ⚠️ Set Up First, Use Later

It is **strongly recommended** to configure **all GUI layouts and pages before using them in gameplay**.

Doing so will:

* Improve performance
* Speed up development
* Reduce runtime changes

---

## How to Make a GUI

### ⚙️ Initial Setup

After installing the datapack:

* **Singleplayer:** Rejoin the world
* **Server:** Restart the server

This is required because GUI Maker uses Minecraft’s **dialog system**, which only reloads on world restart.

---

### Step 1: Create a GUI

1. Run:

   ```mcfunction
   /function gm:menu
   ```
2. Click **CREATE GUI**
3. Click **CREATE GUI PROFILE**
4. A **GUI ID** will appear in chat — save it
5. Reopen the menu
6. Click **CREATE GUI → Append a Page**
7. Select your GUI using the saved GUI ID

Your GUI is now created.

---

### Step 2: Edit a Page

1. Open the menu again:

   ```mcfunction
   /function gm:menu
   ```
2. Click **Edit Page**
3. Select your **GUI ID** and **Page**
4. Choose between:

   * Editing Page Name
   * Editing Contents
5. When editing contents:

   * A prototype layout will appear
   * Only occupied slots are shown
   * Hover over buttons to identify items
6. Click a button to modify it
7. Fill in all required fields and click **Apply**

> ⚠️ Do not leave text inputs empty.

---

### Step 3: Spawn the GUI

1. Open the menu again
2. Click **Spawn GUI**
3. Select the page to spawn
4. Choose a spawn type:

   * Barrel
   * Chest Minecart
5. A command will appear in chat

   * This command can be copied and reused in your own datapacks

---

## Usage Rules

* ❌ **Do NOT** re‑upload this datapack as‑is
* ✅ You **may**:

  * Bundle it with your own datapack or map
  * Modify the source code
  * Upload combined projects that include GUI Maker

---

## Extras & Notes

* The datapack was previously split into two parts; it is now **fully merged**
* Until Mojang significantly changes the dialog system, this datapack will continue to be **maintained and upgraded** for future Minecraft versions

---

## Thank You 💙

* Thanks for downloading GUI Maker
* Thanks for using it in your creations
* Thanks for showing interest in this project

Happy mapmaking!
