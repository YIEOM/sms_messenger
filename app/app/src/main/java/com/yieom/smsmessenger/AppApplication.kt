package com.yieom.smsmessenger

import android.app.Application
import com.yieom.smsmessenger.data.AppDatabase // Added import for your AppDatabase
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.util.Arrays

@HiltAndroidApp
class AppApplication : Application() {

    companion object {
        // IMPORTANT: This is a simple XOR obfuscation, not strong cryptographic protection.
        // For production, consider more robust key management (e.g., Android Keystore).
        private const val XOR_KEY_STRING = "SimpleSharedKey" // Example XOR key used for obfuscation

        // Original passphrase to be obfuscated: "smsmsg2025!"
        // The following IntArray stores the character codes of "smsmsg2025!"
        // after being XORed with the XOR_KEY_STRING.
        // "smsmsg2025!" XOR "SimpleSharedKey" results in:
        // 's' (115) ^ 'S' (83)  = 32
        // 'm' (109) ^ 'i' (105) = 20
        // 's' (115) ^ 'm' (109) = 6
        // 'm' (109) ^ 'p' (112) = 29
        // 's' (115) ^ 'l' (108) = 23
        // 'g' (103) ^ 'e' (101) = 6
        // '2' (50)  ^ 'S' (83)  = 113  (Key char 'S' from repeating key)
        // '0' (48)  ^ 'h' (104) = 72   (Key char 'h')
        // '2' (50)  ^ 'a' (97)  = 79   (Key char 'a')
        // '5' (53)  ^ 'r' (114) = 67   (Key char 'r')
        // '!' (33)  ^ 'e' (101) = 68   (Key char 'e')
        private val encodedPassphraseCharCodes: IntArray = intArrayOf(
            32, 20, 6, 29, 23, 6, 113, 72, 79, 67, 68
        )

        /**
         * Decodes the hardcoded obfuscated passphrase codes using XOR.
         * The caller is responsible for clearing the returned CharArray from memory.
         */
        private fun getDecodedPassphraseChars(): CharArray {
            val keyChars = XOR_KEY_STRING.toCharArray()
            val keyLength = keyChars.size
            val decodedChars = CharArray(encodedPassphraseCharCodes.size)
            for (i in encodedPassphraseCharCodes.indices) {
                decodedChars[i] = (encodedPassphraseCharCodes[i] xor keyChars[i % keyLength].code).toChar()
            }
            return decodedChars
        }
    }

    val database: AppDatabase by lazy {
        val decodedPassphraseChars = getDecodedPassphraseChars()
        // Pass a clone to AppDatabase.getInstance, which should also clear its copy.
        val dbInstance = AppDatabase.getInstance(this, decodedPassphraseChars.clone())
        // Clear the locally decoded passphrase from memory immediately after use.
        Arrays.fill(decodedPassphraseChars, ' ')
        dbInstance
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        // You can optionally force database initialization here if needed:
        // database.openHelper.writableDatabase
    }

    override fun onTerminate() {
        super.onTerminate()
        // The decoded passphrase chars are function-local to the `database` lazy initializer
        // and are cleared there. The `encodedPassphraseCharCodes` (obfuscated data)
        // remains in the companion object's memory.
    }
}
