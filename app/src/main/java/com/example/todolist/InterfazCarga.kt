package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class InterfazCarga : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interfaz_carga)

        val settingsDataStore = SettingsDataStore(this)
        lifecycleScope.launch {
            val isDark = settingsDataStore.darkModeFlow.first()
            val langCode = settingsDataStore.languageFlow.first()

            val targetMode = if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            if (AppCompatDelegate.getDefaultNightMode() != targetMode) {
                AppCompatDelegate.setDefaultNightMode(targetMode)
            }

            val currentLocales = AppCompatDelegate.getApplicationLocales()
            if (currentLocales.toLanguageTags() != langCode && langCode.isNotEmpty()) {
                val appLocale = LocaleListCompat.forLanguageTags(langCode)
                AppCompatDelegate.setApplicationLocales(appLocale)
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, InterfazLogin::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}