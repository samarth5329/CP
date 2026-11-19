package com.example

import com.example.core.security.ScamAnalyzer
import com.example.core.security.ScamVerdict
import com.example.data.local.FinSmartContentCatalog
import com.example.data.model.LearningModuleId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `scam analyzer flags phishing lookalike domain as RED`() {
        val res = ScamAnalyzer.analyzeUrl("https://sbi-kyc-update.in/login")
        assertEquals(ScamVerdict.RED, res.verdict)
        assertTrue(res.indicators.any { it.contains("SBI", ignoreCase = true) })
    }

    @Test
    fun `scam analyzer marks unknown unverified domain as GREY or YELLOW and never claims 100 percent safe`() {
        val cleanUnknown = ScamAnalyzer.analyzeUrl("https://localbakery.in")
        assertEquals(ScamVerdict.GREY, cleanUnknown.verdict)
        assertTrue(cleanUnknown.limitationsNote.contains("cannot guarantee", ignoreCase = true))

        val hyphenUnknown = ScamAnalyzer.analyzeUrl("https://random-shop-example-site.in")
        assertEquals(ScamVerdict.YELLOW, hyphenUnknown.verdict)
        assertTrue(hyphenUnknown.limitationsNote.contains("cannot guarantee", ignoreCase = true))
    }

    @Test
    fun `scam analyzer recognizes official npci domain conservatively as GREEN`() {
        val res = ScamAnalyzer.analyzeUrl("https://npci.org.in")
        assertEquals(ScamVerdict.GREEN, res.verdict)
        assertTrue(res.limitationsNote.contains("cannot guarantee", ignoreCase = true))
    }

    @Test
    fun `scam analyzer catches url shorteners and userinfo @ spoofing`() {
        val shortRes = ScamAnalyzer.analyzeUrl("https://bit.ly/free-cashback")
        assertTrue(shortRes.verdict == ScamVerdict.RED || shortRes.verdict == ScamVerdict.YELLOW)

        val atRes = ScamAnalyzer.analyzeUrl("https://sbi.co.in@evil-phish.com")
        assertEquals(ScamVerdict.RED, atRes.verdict)
    }

    @Test
    fun `scam analyzer detects kyc sms and remote access call scams`() {
        val smsRes = ScamAnalyzer.analyzeMessage(
            "Dear customer, your bank KYC has expired today. Click bit.ly/kyc-fast or share OTP to avoid block."
        )
        assertEquals(ScamVerdict.RED, smsRes.verdict)
        assertFalse(smsRes.detectedPhrases.isEmpty())

        val callRes = ScamAnalyzer.analyzeCallClaim(
            "9876543210",
            "Caller asked me to install AnyDesk and enter UPI PIN to get refund"
        )
        assertEquals(ScamVerdict.RED, callRes.verdict)
    }

    @Test
    fun `content catalog includes all 18 lessons across 4 modules and 4 official schemes`() {
        assertEquals(18, FinSmartContentCatalog.lessons.size)
        assertEquals(4, LearningModuleId.entries.size)
        assertEquals(4, FinSmartContentCatalog.govSchemes.size)
        assertEquals(4, FinSmartContentCatalog.beforeYouPaySituations.size)
        assertTrue(FinSmartContentCatalog.quizzes.size >= 6)
        assertTrue(FinSmartContentCatalog.fraudScenarios.size >= 5)
    }
}
