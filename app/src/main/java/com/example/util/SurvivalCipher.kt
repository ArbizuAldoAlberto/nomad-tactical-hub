package com.example.util

import java.util.Locale

object SurvivalCipher {

    // Morse Code Table mappings
    private val TEXT_TO_MORSE = mapOf(
        'A' to ".-", 'B' to "-...", 'C' to "-.-.", 'D' to "-..", 'E' to ".", 'F' to "..-.",
        'G' to "--.", 'H' to "....", 'I' to "..", 'J' to ".---", 'K' to "-.-", 'L' to ".-..",
        'M' to "--", 'N' to "-.", 'O' to "---", 'P' to ".--.", 'Q' to "--.-", 'R' to ".-.",
        'S' to "...", 'T' to "-", 'U' to "..-", 'V' to "...-", 'W' to ".--", 'X' to "-..-",
        'Y' to "-.--", 'Z' to "--..",
        '0' to "-----", '1' to ".----", '2' to "..---", '3' to "...--", '4' to "....-",
        '5' to ".....", '6' to "-....", '7' to "--...", '8' to "---..", '9' to "----.",
        ' ' to "/"
    )

    private val MORSE_TO_TEXT = TEXT_TO_MORSE.entries.associate { (k, v) -> v to k }

    /**
     * Encrpyts/Decrypts using standard robust Vigenère Cipher, restricting to printable uppercase A-Z, 0-9
     */
    fun encryptVigenere(text: String, key: String): String {
        if (key.isBlank()) return text
        val normalizedText = text.uppercase(Locale.getDefault()).filter { it in 'A'..'Z' || it in '0'..'9' || it == ' ' }
        val normalizedKey = key.uppercase(Locale.getDefault()).filter { it in 'A'..'Z' || it in '0'..'9' }
        if (normalizedKey.isEmpty()) return normalizedText

        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val n = alphabet.length
        val result = StringBuilder()
        var keyIndex = 0

        for (char in normalizedText) {
            if (char == ' ') {
                result.append(' ')
                continue
            }
            val textPos = alphabet.indexOf(char)
            if (textPos == -1) {
                result.append(char)
                continue
            }
            val keyChar = normalizedKey[keyIndex % normalizedKey.length]
            val keyPos = alphabet.indexOf(keyChar).coerceAtLeast(0)

            val newPos = (textPos + keyPos) % n
            result.append(alphabet[newPos])
            keyIndex++
        }
        return result.toString()
    }

    fun decryptVigenere(cipherText: String, key: String): String {
        if (key.isBlank()) return cipherText
        val normalizedText = cipherText.uppercase(Locale.getDefault())
        val normalizedKey = key.uppercase(Locale.getDefault()).filter { it in 'A'..'Z' || it in '0'..'9' }
        if (normalizedKey.isEmpty()) return normalizedText

        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val n = alphabet.length
        val result = StringBuilder()
        var keyIndex = 0

        for (char in normalizedText) {
            if (char == ' ') {
                result.append(' ')
                continue
            }
            val textPos = alphabet.indexOf(char)
            if (textPos == -1) {
                result.append(char)
                continue
            }
            val keyChar = normalizedKey[keyIndex % normalizedKey.length]
            val keyPos = alphabet.indexOf(keyChar).coerceAtLeast(0)

            val newPos = (textPos - keyPos + n) % n
            result.append(alphabet[newPos])
            keyIndex++
        }
        return result.toString()
    }

    /**
     * Converts a standard phrase to Morse code (space separated)
     */
    fun textToMorse(text: String): String {
        val upper = text.uppercase(Locale.getDefault())
        val result = mutableListOf<String>()
        for (char in upper) {
            val morse = TEXT_TO_MORSE[char]
            if (morse != null) {
                result.add(morse)
            }
        }
        return result.joinToString(" ")
    }

    /**
     * Converts a Morse sequence back to plaintext
     */
    fun morseToText(morse: String): String {
        val tokens = morse.trim().split("\\s+".toRegex())
        val result = StringBuilder()
        for (token in tokens) {
            val char = MORSE_TO_TEXT[token]
            if (char != null) {
                result.append(char)
            } else if (token == "/") {
                result.append(" ")
            }
        }
        return result.toString()
    }

    /**
     * Compiles a timing pattern of light states (Long representing ms duration)
     * e.g. dot = 200ms, dash = 600ms, pause between symbols = 200ms, pause between words = 600ms
     * returns a list of Pair<Boolean, Long> (isOn, durationMs)
     */
    fun getMorseTimingPattern(morseString: String): List<Pair<Boolean, Long>> {
        val pattern = mutableListOf<Pair<Boolean, Long>>()
        val symbols = morseString.split(" ")
        for (symbolIndex in symbols.indices) {
            val symbol = symbols[symbolIndex]
            if (symbol == "/") {
                pattern.add(Pair(false, 600L)) // Word spacer
                continue
            }
            for (charIndex in symbol.indices) {
                val sign = symbol[charIndex]
                if (sign == '.') {
                    pattern.add(Pair(true, 200L))
                } else if (sign == '-') {
                    pattern.add(Pair(true, 600L))
                }
                // Intracharacter gap
                if (charIndex < symbol.length - 1) {
                    pattern.add(Pair(false, 200L))
                }
            }
            // Space between letters
            if (symbolIndex < symbols.size - 1) {
                pattern.add(Pair(false, 400L))
            }
        }
        return pattern
    }
}
