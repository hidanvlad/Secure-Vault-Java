# Secure Vault Java 🔒

A secure file encryption and management system built with **Java** and **JavaFX**. This application allows users to encrypt sensitive files using **AES (Advanced Encryption Standard)** and protects access via a hashed authentication mechanism.

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Security](https://img.shields.io/badge/Security-AES_256-green?style=for-the-badge&logo=guard&logoColor=white)
![JavaFX](https://img.shields.io/badge/GUI-JavaFX-blue?style=for-the-badge)

## 🛡️ Core Security Features

This isn't just a file hider; it implements cryptographic standards to ensure data confidentiality:

* **AES Encryption:** Files are encrypted using the AES algorithm. The content is unreadable without the correct decryption key.
* **Password Hashing:** User passwords are not stored in plain text. We use **SHA-256** (or PBKDF2) to hash credentials, protecting against database leaks.
* **Secure File Handling:** Utilizes `CipherInputStream` and `CipherOutputStream` for efficient memory usage when processing large files.

## 📋 App Features

* **User Authentication:** Secure login screen preventing unauthorized access.
* **Drag & Drop Interface:** Easy drag-and-drop functionality to add files to the vault.
* **Lock/Unlock:** One-click encryption and decryption of selected files.
* **File Integrity:** Checks if files have been tampered with (optional, if implemented).

## 🛠️ Tech Stack

* **Language:** Java (JDK 17+)
* **GUI:** JavaFX (FXML, CSS)
* **Cryptography:** `javax.crypto` (Cipher, SecretKeySpec), `java.security` (MessageDigest)
* **I/O:** Java NIO (Non-blocking I/O) for file manipulation.


## 🚀 How it Works (Under the Hood)

1.  **Key Generation:** When a user logs in, their password generates a cryptographic key.
2.  **Encryption Process:**
    * The app reads the input file in blocks.
    * Passes data through the **AES Cipher**.
    * Writes the scrambled bytes to a new file (e.g., `file.enc`).
3.  **Decryption:** The process is reversed only if the correct key is provided.

## ⚙️ How to Run

1.  **Clone the repo:**
    ```bash
    git clone [https://github.com/hidanvlad/Secure-Vault-Java.git](https://github.com/hidanvlad/Secure-Vault-Java.git)
    ```
2.  **Run via IDE:**
    * Open in IntelliJ IDEA.
    * Run `Main.java` (or `Launcher.java`).

## ⚠️ Disclaimer

This tool is for educational purposes. While it uses standard algorithms, strictly sensitive data should be handled by enterprise-grade security solutions.
