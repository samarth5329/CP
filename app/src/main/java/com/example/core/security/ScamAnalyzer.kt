package com.example.core.security

import java.net.URI
import java.util.Locale

enum class ScamVerdict(val label: String, val badgeText: String) {
    GREEN("No Known Threat Detected", "Low Risk / Verified Domain"),
    YELLOW("Suspicious — Verify First", "Suspicious"),
    RED("High-Risk Scam Indicators", "High Risk"),
    GREY("Unable to Verify", "Unverified")
}

data class LinkAnalysisResult(
    val normalizedInput: String,
    val host: String,
    val scheme: String,
    val verdict: ScamVerdict,
    val headline: String,
    val simpleExplanation: String,
    val indicators: List<String>,
    val recommendedAction: String,
    val limitationsNote: String = "Local heuristic analysis checks domain structure, brand lookalikes, shorteners, and risky patterns without opening or downloading the page. It cannot guarantee 100% safety for unknown links."
)

data class MessageAnalysisResult(
    val inputPreview: String,
    val verdict: ScamVerdict,
    val riskLabel: String,
    val detectedPhrases: List<String>,
    val simpleExplanation: String,
    val recommendedAction: String,
    val avoidActions: List<String>,
    val extractedUrls: List<String>,
    val disclaimer: String = "Heuristic & pattern-based safety check. Never share OTP, UPI PIN, CVV, or bank passwords."
)

object ScamAnalyzer {

    private val officialDomains = setOf(
        "npci.org.in",
        "rbi.org.in",
        "sbi.co.in",
        "onlinesbi.sbi",
        "hdfcbank.com",
        "icicibank.com",
        "axisbank.com",
        "kotak.com",
        "pnbindia.in",
        "bankofbaroda.in",
        "canarabank.com",
        "unionbankofindia.co.in",
        "cybercrime.gov.in",
        "pmjdy.gov.in",
        "jansuraksha.gov.in",
        "npscra.nsdl.co.in",
        "uidai.gov.in",
        "incometax.gov.in",
        "india.gov.in",
        "bhimupi.org.in"
    )

    private val protectedBrandTokens = listOf(
        "sbi", "hdfc", "icici", "axis", "kotak", "pnb", "canara", "bob", "rbi", "npci",
        "bhim", "paytm", "phonepe", "gpay", "googlepay", "upi", "aadhaar", "uidai",
        "epfo", "pmjdy", "bescom", "electricity", "lic", "irctc", "indane"
    )

    private val shortenerDomains = setOf(
        "bit.ly", "tinyurl.com", "t.co", "goo.gl", "ow.ly", "is.gd", "buff.ly",
        "rebrand.ly", "cutt.ly", "rb.gy", "shorturl.at", "tiny.cc", "s.id"
    )

    private val suspiciousTlds = setOf(
        "xyz", "top", "click", "tk", "ml", "ga", "cf", "gq", "club", "work",
        "buzz", "monster", "icu", "vip", "loan", "win", "cam", "rest"
    )

    private val credentialKeywords = listOf(
        "kyc", "verify", "update", "blocked", "suspend", "refund", "reward",
        "claim", "prize", "lottery", "cashback", "otp", "pin", "cvv", "login",
        "secure-login", "reverify", "reactivate", "pan-link", "aadhaar-update"
    )

    private val urgencyPhrases = listOf(
        "account will be blocked", "account blocked", "kyc expired", "kyc has expired",
        "update kyc", "immediately", "within 24 hours", "within 1 hour", "suspended today",
        "electricity connection", "power will be disconnected", "disconnect tonight",
        "urgent action", "last warning", "legal action", "police case", "customs parcel",
        "digital arrest", "account frozen"
    )

    private val rewardLurePhrases = listOf(
        "won a lottery", "won rs", "free government reward", "pm free", "claim your refund",
        "claim cashback", "scratch card", "lucky draw", "congratulations you won",
        "bonus credited", "receive money", "get rs", "free laptop", "free recharge"
    )

    private val credentialTrapPhrases = listOf(
        "share otp", "send otp", "enter otp", "enter upi pin", "upi pin to receive",
        "enter your pin", "share cvv", "card number", "atm pin", "netbanking password",
        "mpin", "share screen", "install anydesk", "teamviewer", "quicksupport",
        "rustdesk", "download apk", ".apk", "approve collect request"
    )

    private val urlRegex = Regex(
        """(?i)\b((?:https?://|www\.)[^\s<>"{}|\\^`\[\]]+|[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?\.(?:com|in|org|net|xyz|top|click|co\.in|org\.in|gov\.in|info|biz|io|me|ly|cc|tk)(?:/[^\s]*)?)"""
    )

    private val phoneRegex = Regex("""(?<!\d)(?:\+91[\s-]?)?[6-9]\d{9}(?!\d)|(?<!\d)1800[\s-]?\d{3,8}(?!\d)""")

    fun analyzeUrl(rawInput: String): LinkAnalysisResult {
        val trimmed = rawInput.trim()
        if (trimmed.isBlank()) {
            return LinkAnalysisResult(
                normalizedInput = "",
                host = "",
                scheme = "",
                verdict = ScamVerdict.GREY,
                headline = "No link entered",
                simpleExplanation = "Paste a website link (for example: https://example.com) to inspect it safely.",
                indicators = listOf("Empty input"),
                recommendedAction = "Paste the full link you received without opening it in a browser."
            )
        }

        val withScheme = if (trimmed.startsWith("http://", true) || trimmed.startsWith("https://", true)) {
            trimmed
        } else {
            "https://$trimmed"
        }

        val uri = try {
            URI(withScheme)
        } catch (_: Exception) {
            null
        }

        val rawHost = uri?.host?.lowercase(Locale.ROOT)?.removePrefix("www.") ?: ""
        if (rawHost.isBlank() || !rawHost.contains('.')) {
            return LinkAnalysisResult(
                normalizedInput = trimmed,
                host = rawHost.ifBlank { "Invalid host" },
                scheme = uri?.scheme.orEmpty(),
                verdict = ScamVerdict.GREY,
                headline = "Unable to verify link format",
                simpleExplanation = "This text does not look like a complete website domain. Do not open unverified links.",
                indicators = listOf("Malformed or incomplete domain name"),
                recommendedAction = "Ask the sender for details or visit your bank's official app directly."
            )
        }

        val indicators = mutableListOf<String>()
        var riskScore = 0

        val isOfficial = officialDomains.any { official ->
            rawHost == official || rawHost.endsWith(".$official")
        }
        if (isOfficial && !trimmed.startsWith("http://", true) && !trimmed.contains("@")) {
            return LinkAnalysisResult(
                normalizedInput = trimmed,
                host = rawHost,
                scheme = "https",
                verdict = ScamVerdict.GREEN,
                headline = "Matches Known Official Domain",
                simpleExplanation = "This domain ($rawHost) matches a recognized official Indian banking or government domain. Still double-check the exact spelling in your browser.",
                indicators = listOf(
                    "Recognized official domain: $rawHost",
                    "No URL shortener or lookalike mismatch found"
                ),
                recommendedAction = "Proceed only if you intentionally visited this site. Never share OTP or PIN."
            )
        }

        // Check HTTP (no encryption)
        if (trimmed.startsWith("http://", true)) {
            indicators += "Uses unencrypted HTTP instead of HTTPS"
            riskScore += 2
        }

        // Check userinfo '@' trick (e.g. https://sbi.co.in@evil.com)
        if (uri?.userInfo != null || trimmed.contains("@")) {
            indicators += "Contains '@' redirect trick hiding the real destination domain"
            riskScore += 5
        }

        // Check raw IPv4 address
        if (Regex("""^\d{1,3}(\.\d{1,3}){3}$""").matches(rawHost)) {
            indicators += "Uses a raw IP address ($rawHost) instead of a registered domain name"
            riskScore += 5
        }

        // Check punycode IDN homograph
        if (rawHost.contains("xn--")) {
            indicators += "Contains encoded international characters (punycode) often used for lookalike spoofing"
            riskScore += 4
        }

        // Check URL shorteners
        if (shortenerDomains.any { rawHost == it || rawHost.endsWith(".$it") }) {
            indicators += "URL shortener ($rawHost) hides the real destination website"
            riskScore += 3
        }

        // Check suspicious TLDs
        val tld = rawHost.substringAfterLast('.', "")
        if (tld in suspiciousTlds) {
            indicators += "Unusual or high-abuse domain extension (.$tld)"
            riskScore += 3
        }

        // Check brand impersonation outside official domains
        val matchedBrands = protectedBrandTokens.filter { brand ->
            rawHost.contains(brand) && !isOfficial
        }
        if (matchedBrands.isNotEmpty()) {
            indicators += "Mentions trusted brand (${matchedBrands.joinToString(", ").uppercase()}) on a non-official domain ($rawHost)"
            riskScore += 5
        }

        // Check credential / urgency keywords in host or path
        val fullLower = trimmed.lowercase(Locale.ROOT)
        val matchedKeywords = credentialKeywords.filter { kw -> fullLower.contains(kw) }
        if (matchedKeywords.isNotEmpty()) {
            indicators += "Contains sensitive lure keywords: ${matchedKeywords.take(4).joinToString(", ")}"
            riskScore += 2 + matchedKeywords.size.coerceAtMost(3)
        }

        // Check excessive subdomains or hyphens
        val hyphenCount = rawHost.count { it == '-' }
        val dotCount = rawHost.count { it == '.' }
        if (hyphenCount >= 2 || dotCount >= 3) {
            indicators += "Complex domain with multiple hyphens or subdomains ($rawHost)"
            riskScore += 2
        }

        // Check APK or executable download link
        if (fullLower.endsWith(".apk") || fullLower.contains(".apk?")) {
            indicators += "Links directly to an Android APK app download outside the Play Store"
            riskScore += 5
        }

        val verdict = when {
            riskScore >= 5 -> ScamVerdict.RED
            riskScore >= 2 -> ScamVerdict.YELLOW
            else -> ScamVerdict.GREY
        }

        val headline = when (verdict) {
            ScamVerdict.RED -> "High-Risk Phishing Indicators Detected"
            ScamVerdict.YELLOW -> "Suspicious Link — Verification Needed"
            ScamVerdict.GREY -> "Unknown Website — Unable to Verify Safety"
            ScamVerdict.GREEN -> "No Known Threat Detected"
        }

        val explanation = when (verdict) {
            ScamVerdict.RED -> if (matchedBrands.isNotEmpty()) {
                "This website looks similar to an official service (${matchedBrands.first().uppercase()}), but its real domain is $rawHost. Do not open it or enter banking details."
            } else {
                "This link shows strong scam warning signs (${indicators.firstOrNull() ?: "high-risk pattern"}). Do not click it or enter any OTP, PIN, or password."
            }
            ScamVerdict.YELLOW -> "This link has warning signs such as ${indicators.firstOrNull()?.lowercase() ?: "unusual structure"}. Verify with the official app or website before opening."
            ScamVerdict.GREY -> "FinSmart cannot verify whether $rawHost is safe because it is not in the recognized official Indian banking/government directory. Treat unknown links cautiously."
            ScamVerdict.GREEN -> "No known threat detected for $rawHost."
        }

        val action = when (verdict) {
            ScamVerdict.RED -> "Delete the message, do NOT open this link, and block the sender. If you already shared details, call 1930 immediately."
            ScamVerdict.YELLOW -> "Do not tap the link. Open your official bank or service app manually or ask a trusted family member."
            ScamVerdict.GREY -> "Only open if you completely trust the sender. Never enter UPI PIN, OTP, CVV, or netbanking passwords on unknown sites."
            ScamVerdict.GREEN -> "Continue safely, and still never share OTP or PIN."
        }

        return LinkAnalysisResult(
            normalizedInput = trimmed,
            host = rawHost,
            scheme = uri?.scheme ?: "unknown",
            verdict = verdict,
            headline = headline,
            simpleExplanation = explanation,
            indicators = if (indicators.isEmpty()) listOf("Domain $rawHost is not in the verified official directory") else indicators,
            recommendedAction = action
        )
    }

    fun analyzeMessage(rawMessage: String): MessageAnalysisResult {
        val text = rawMessage.trim()
        if (text.isBlank()) {
            return MessageAnalysisResult(
                inputPreview = "",
                verdict = ScamVerdict.GREY,
                riskLabel = "No message provided",
                detectedPhrases = emptyList(),
                simpleExplanation = "Paste a suspicious SMS, WhatsApp message, or extracted screenshot text to inspect it.",
                recommendedAction = "Paste the message text above and tap Check Safety.",
                avoidActions = listOf("Do not tap unverified links", "Do not call random numbers inside messages"),
                extractedUrls = emptyList()
            )
        }

        val lower = text.lowercase(Locale.ROOT)
        val detected = mutableListOf<String>()
        var score = 0

        urgencyPhrases.filter { lower.contains(it) }.forEach {
            detected += "Urgency threat: \"$it\""
            score += 3
        }
        rewardLurePhrases.filter { lower.contains(it) }.forEach {
            detected += "Fake reward/refund lure: \"$it\""
            score += 3
        }
        credentialTrapPhrases.filter { lower.contains(it) }.forEach {
            detected += "Credential / remote-access trap: \"$it\""
            score += 5
        }

        val urls = urlRegex.findAll(text).map { it.value }.distinct().toList()
        var hasHighRiskUrl = false
        urls.forEach { url ->
            val linkRes = analyzeUrl(url)
            if (linkRes.verdict == ScamVerdict.RED) {
                hasHighRiskUrl = true
                detected += "High-risk link: ${linkRes.host}"
                score += 5
            } else if (linkRes.verdict == ScamVerdict.YELLOW) {
                detected += "Suspicious link: ${linkRes.host}"
                score += 3
            } else if (linkRes.verdict == ScamVerdict.GREY) {
                detected += "Unverified link: ${linkRes.host}"
                score += 1
            }
        }

        val phones = phoneRegex.findAll(text).map { it.value }.distinct().toList()
        if (phones.isNotEmpty() && (lower.contains("call") || lower.contains("helpline") || lower.contains("customer care") || lower.contains("kyc") || lower.contains("block"))) {
            detected += "Unverified callback number in alert message: ${phones.joinToString(", ")}"
            score += 3
        }

        val verdict = when {
            score >= 5 || hasHighRiskUrl -> ScamVerdict.RED
            score >= 2 -> ScamVerdict.YELLOW
            urls.isNotEmpty() || phones.isNotEmpty() -> ScamVerdict.GREY
            else -> ScamVerdict.GREY
        }

        val riskLabel = when (verdict) {
            ScamVerdict.RED -> "High-Risk Scam Pattern Detected"
            ScamVerdict.YELLOW -> "Suspicious Message — Caution Advised"
            ScamVerdict.GREY -> "Unverified Message — Stay Careful"
            ScamVerdict.GREEN -> "No Known Threat Detected"
        }

        val explanation = when (verdict) {
            ScamVerdict.RED -> "This message uses classic scam tactics (${detected.take(2).joinToString("; ")}). Real banks and government offices never block accounts via SMS links or ask for OTP, PIN, or screen-sharing apps."
            ScamVerdict.YELLOW -> "This message contains suspicious pressure or unverified contact details. Verify independently using your bank passbook, card back, or official app."
            else -> "No overt scam keywords matched, but FinSmart cannot guarantee an unsolicited message is genuine. Never share OTPs or tap unknown links."
        }

        val recommendedAction = when (verdict) {
            ScamVerdict.RED -> "Do NOT reply, tap any link, or call the number in this message. Delete and report it. You can also share this check with a Trusted Family Contact."
            ScamVerdict.YELLOW -> "Ignore any urgency. Open your official bank app directly or call the number printed on the back of your bank card."
            else -> "Verify who sent this message before acting. Ask a trusted family member if unsure."
        }

        return MessageAnalysisResult(
            inputPreview = text.take(140),
            verdict = verdict,
            riskLabel = riskLabel,
            detectedPhrases = detected.distinct(),
            simpleExplanation = explanation,
            recommendedAction = recommendedAction,
            avoidActions = listOf(
                "Never share OTP, UPI PIN, CVV, or netbanking password",
                "Never install AnyDesk, TeamViewer, QuickSupport, or unknown .apk files",
                "Never scan a QR code or enter UPI PIN to receive money",
                "Never call phone numbers found inside SMS alerts or random web search ads"
            ),
            extractedUrls = urls
        )
    }

    fun analyzeCallClaim(numberInput: String, callerClaim: String): MessageAnalysisResult {
        val combined = "Caller number: ${numberInput.trim()}. Caller says: ${callerClaim.trim()}"
        val base = analyzeMessage(combined)
        val extraIndicators = base.detectedPhrases.toMutableList()
        val numClean = numberInput.filter { it.isDigit() }
        if (numClean.length == 10 && numClean.firstOrNull() in setOf('6', '7', '8', '9')) {
            extraIndicators += "10-digit personal mobile number used for bank/support claim (official bank helplines do not call from personal mobile numbers asking for OTP/PIN)"
        }
        val upgradedVerdict = if (extraIndicators.isNotEmpty() && base.verdict == ScamVerdict.GREY) {
            ScamVerdict.YELLOW
        } else {
            base.verdict
        }
        return base.copy(
            verdict = upgradedVerdict,
            riskLabel = when (upgradedVerdict) {
                ScamVerdict.RED -> "High-Risk Scam Call Indicators"
                ScamVerdict.YELLOW -> "Suspicious Call — Verify Independently"
                else -> "Unverified Caller — Never Share OTP or PIN"
            },
            detectedPhrases = extraIndicators.distinct()
        )
    }
}
