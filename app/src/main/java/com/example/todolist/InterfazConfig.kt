package com.example.todolist

import android.os.Bundle
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class InterfazConfig : AppCompatActivity() {

    private lateinit var settingsDataStore: SettingsDataStore
    private var isInitializing = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interfaz_config)

        settingsDataStore = SettingsDataStore(this)

        val btnBackConfig = findViewById<ImageButton>(R.id.btnBackConfig)
        val rgTheme = findViewById<RadioGroup>(R.id.rgTheme)
        val rbLight = findViewById<RadioButton>(R.id.rbLight)
        val rbDark = findViewById<RadioButton>(R.id.rbDark)
        val switchNotifications = findViewById<SwitchMaterial>(R.id.switchNotifications)
        val rgLanguage = findViewById<RadioGroup>(R.id.rgLanguage)
        val rbSpanish = findViewById<RadioButton>(R.id.rbSpanish)
        val rbEnglish = findViewById<RadioButton>(R.id.rbEnglish)
        val rbJapanese = findViewById<RadioButton>(R.id.rbJapanese)

        btnBackConfig.setOnClickListener {
            finish()
        }

        // Cargar estado inicial guardado en DataStore
        lifecycleScope.launch {
            val isDark = settingsDataStore.darkModeFlow.first()
            val notificationsEnabled = settingsDataStore.notificationsFlow.first()
            val languageCode = settingsDataStore.languageFlow.first()

            if (isDark) {
                rbDark.isChecked = true
            } else {
                rbLight.isChecked = true
            }

            switchNotifications.isChecked = notificationsEnabled

            when (languageCode) {
                "en" -> rbEnglish.isChecked = true
                "ja" -> rbJapanese.isChecked = true
                else -> rbSpanish.isChecked = true
            }

            isInitializing = false
        }

        // Guardar cambios de Modo Claro / Oscuro en DataStore
        rgTheme.setOnCheckedChangeListener { _, checkedId ->
            if (isInitializing) return@setOnCheckedChangeListener
            val isDark = checkedId == R.id.rbDark

            lifecycleScope.launch {
                settingsDataStore.saveDarkMode(isDark)
                val mode = if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                AppCompatDelegate.setDefaultNightMode(mode)
                Toast.makeText(
                    this@InterfazConfig,
                    if (isDark) "Modo Oscuro activado" else "Modo Claro activado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Guardar estado de Notificaciones en DataStore
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            if (isInitializing) return@setOnCheckedChangeListener

            lifecycleScope.launch {
                settingsDataStore.saveNotifications(isChecked)
                Toast.makeText(
                    this@InterfazConfig,
                    if (isChecked) "Notificaciones activadas" else "Notificaciones desactivadas",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Guardar selección de Idioma en DataStore
        rgLanguage.setOnCheckedChangeListener { _, checkedId ->
            if (isInitializing) return@setOnCheckedChangeListener

            val langCode = when (checkedId) {
                R.id.rbEnglish -> "en"
                R.id.rbJapanese -> "ja"
                else -> "es"
            }

            lifecycleScope.launch {
                settingsDataStore.saveLanguage(langCode)
                val appLocale = LocaleListCompat.forLanguageTags(langCode)
                AppCompatDelegate.setApplicationLocales(appLocale)
                Toast.makeText(
                    this@InterfazConfig,
                    "Idioma actualizado: ${if (langCode == "en") "English" else if (langCode == "ja") "日本語" else "Español"}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}