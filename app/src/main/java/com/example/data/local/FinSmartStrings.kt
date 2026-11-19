package com.example.data.local

import com.example.data.model.AppLanguage

data class FinSmartLocalizedStrings(
    val greetingPrefix: String,
    val companionTagline: String,
    val safetyScoreTitle: String,
    val incomeLabel: String,
    val expensesLabel: String,
    val savingsLabel: String,
    val quickActionsTitle: String,
    val todaysTipTitle: String,
    val navHome: String,
    val navLearn: String,
    val navAiHelp: String,
    val navTools: String,
    val navSos: String,
    val learnTitle: String,
    val learnSubtitle: String,
    val aiTitle: String,
    val aiSubtitle: String,
    val aiPlaceholder: String,
    val sosTitle: String,
    val sosSubtitle: String,
    val call1930Title: String,
    val call1930Subtitle: String,
    val settingsTitle: String,
    val simpleModeTitle: String,
    val simpleModeSubtitle: String
)

object FinSmartStrings {
    private val english = FinSmartLocalizedStrings(
        greetingPrefix = "Namaste",
        companionTagline = "Your financial safety companion",
        safetyScoreTitle = "Financial Safety Score",
        incomeLabel = "Income",
        expensesLabel = "Expenses",
        savingsLabel = "Savings",
        quickActionsTitle = "Quick Actions",
        todaysTipTitle = "Today's Safety Tip",
        navHome = "Home",
        navLearn = "Learn",
        navAiHelp = "AI Help",
        navTools = "Tools",
        navSos = "SOS",
        learnTitle = "Learn & Grow",
        learnSubtitle = "Build real-life financial skills step by step",
        aiTitle = "FinSmart AI Sahayak",
        aiSubtitle = "Ask anything about banking, UPI, savings, schemes or fraud safety.",
        aiPlaceholder = "Ask a financial question...",
        sosTitle = "Financial Fraud Emergency",
        sosSubtitle = "Stay calm and follow these steps immediately.",
        call1930Title = "Call 1930",
        call1930Subtitle = "National Cyber Crime Helpline",
        settingsTitle = "Settings",
        simpleModeTitle = "Simple Mode",
        simpleModeSubtitle = "Larger buttons, simpler screens and easy navigation"
    )

    private val kannada = FinSmartLocalizedStrings(
        greetingPrefix = "ನಮಸ್ಕಾರ",
        companionTagline = "ನಿಮ್ಮ ಆರ್ಥಿಕ ಸುರಕ್ಷತಾ ಸಂಗಾತಿ",
        safetyScoreTitle = "ಆರ್ಥಿಕ ಸುರಕ್ಷತಾ ಅಂಕ",
        incomeLabel = "ಆದಾಯ",
        expensesLabel = "ಖರ್ಚು",
        savingsLabel = "ಉಳಿತಾಯ",
        quickActionsTitle = "ತ್ವರಿತ ಕ್ರಿಯೆಗಳು",
        todaysTipTitle = "ಇಂದಿನ ಸುರಕ್ಷತಾ ಸಲಹೆ",
        navHome = "ಮುಖಪುಟ",
        navLearn = "ಕಲಿಯಿರಿ",
        navAiHelp = "AI ಸಹಾಯ",
        navTools = "ಸಾಧನಗಳು",
        navSos = "SOS",
        learnTitle = "ಕಲಿಯಿರಿ ಮತ್ತು ಬೆಳೆಯಿರಿ",
        learnSubtitle = "ಹಂತ ಹಂತವಾಗಿ ಆರ್ಥಿಕ ಕೌಶಲ್ಯಗಳನ್ನು ಬೆಳೆಸಿಕೊಳ್ಳಿ",
        aiTitle = "ಫಿನ್‌ಸ್ಮಾರ್ಟ್ AI ಸಹಾಯಕ",
        aiSubtitle = "ಬ್ಯಾಂಕಿಂಗ್, UPI, ಉಳಿತಾಯ, ಯೋಜನೆಗಳು ಅಥವಾ ವಂಚನೆ ಸುರಕ್ಷತೆಯ ಬಗ್ಗೆ ಕೇಳಿ.",
        aiPlaceholder = "ಆರ್ಥಿಕ ಪ್ರಶ್ನೆಯನ್ನು ಕೇಳಿ...",
        sosTitle = "ಆರ್ಥಿಕ ವಂಚನೆ ತುರ್ತು ಸಹಾಯ",
        sosSubtitle = "ಶಾಂತವಾಗಿರಿ ಮತ್ತು ಈ ಹಂತಗಳನ್ನು ತಕ್ಷಣ ಅನುಸರಿಸಿ.",
        call1930Title = "1930 ಗೆ ಕರೆ ಮಾಡಿ",
        call1930Subtitle = "ರಾಷ್ಟ್ರೀಯ ಸೈಬರ್ ಅಪರಾಧ ಸಹಾಯವಾಣಿ",
        settingsTitle = "ಸೆಟ್ಟಿಂಗ್‌ಗಳು",
        simpleModeTitle = "ಸರಳ ಮೋಡ್ (Simple Mode)",
        simpleModeSubtitle = "ದೊಡ್ಡ ಬಟನ್‌ಗಳು ಮತ್ತು ಸುಲಭ ನ್ಯಾವಿಗೇಷನ್"
    )

    private val hindi = FinSmartLocalizedStrings(
        greetingPrefix = "नमस्ते",
        companionTagline = "आपका वित्तीय सुरक्षा साथी",
        safetyScoreTitle = "वित्तीय सुरक्षा स्कोर",
        incomeLabel = "आय",
        expensesLabel = "खर्च",
        savingsLabel = "बचत",
        quickActionsTitle = "त्वरित कार्य",
        todaysTipTitle = "आज का सुरक्षा सुझाव",
        navHome = "होम",
        navLearn = "सीखें",
        navAiHelp = "AI मदद",
        navTools = "टूल्स",
        navSos = "SOS",
        learnTitle = "सीखें और आगे बढ़ें",
        learnSubtitle = "कदम-दर-कदम वास्तविक वित्तीय कौशल सीखें",
        aiTitle = "फिनस्मार्ट AI सहायक",
        aiSubtitle = "बैंकिंग, UPI, बचत, योजनाओं या धोखाधड़ी सुरक्षा के बारे में पूछें।",
        aiPlaceholder = "कोई वित्तीय प्रश्न पूछें...",
        sosTitle = "वित्तीय धोखाधड़ी आपातकाल",
        sosSubtitle = "शांत रहें और तुरंत इन चरणों का पालन करें।",
        call1930Title = "1930 पर कॉल करें",
        call1930Subtitle = "राष्ट्रीय साइबर अपराध हेल्पलाइन",
        settingsTitle = "सेटिंग्स",
        simpleModeTitle = "सरल मोड (Simple Mode)",
        simpleModeSubtitle = "बड़े बटन, सरल स्क्रीन और आसान नेविगेशन"
    )

    fun get(language: AppLanguage): FinSmartLocalizedStrings = when (language) {
        AppLanguage.ENGLISH -> english
        AppLanguage.KANNADA -> kannada
        AppLanguage.HINDI -> hindi
    }
}
