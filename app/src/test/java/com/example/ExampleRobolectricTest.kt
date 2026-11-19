package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.PreferencesManager
import com.example.data.model.AppLanguage
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `app name matches FinSmart`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("FinSmart", appName)
    }

    @Test
    fun `preferences manager persists theme mode, language, and simple mode`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = PreferencesManager(context)

        prefs.setThemeMode(ThemeMode.DARK_NEON)
        prefs.setLanguage(AppLanguage.KANNADA)
        prefs.setSimpleMode(true)
        prefs.setTextScale(1.15f)

        val state = prefs.settingsFlow.first()
        assertEquals(ThemeMode.DARK_NEON, state.themeMode)
        assertEquals(AppLanguage.KANNADA, state.language)
        assertTrue(state.simpleMode)
        assertEquals(1.15f, state.textScale, 0.01f)
    }
}
