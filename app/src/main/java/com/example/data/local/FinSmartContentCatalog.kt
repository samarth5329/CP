package com.example.data.local

import com.example.data.model.AppLanguage
import com.example.data.model.AwarenessVideoGuide
import com.example.data.model.BeforeYouPaySituation
import com.example.data.model.FraudScenario
import com.example.data.model.GovSchemeInfo
import com.example.data.model.LearningModuleId
import com.example.data.model.LessonItem
import com.example.data.model.NeedsVsWantsItem
import com.example.data.model.QuizQuestion
import com.example.data.model.SafetyBadge

object FinSmartContentCatalog {

    val dailySafetyTips: Map<AppLanguage, List<String>> = mapOf(
        AppLanguage.ENGLISH to listOf(
            "Never share your OTP, UPI PIN, or card CVV with anyone — not even someone claiming to be from your bank or police.",
            "You NEVER need to enter your UPI PIN or scan a QR code to RECEIVE money. PIN is only for sending money.",
            "Do not install screen-sharing apps like AnyDesk or TeamViewer when asked by a caller claiming to fix your bank or KYC.",
            "Always verify customer-care numbers from your bank passbook or official bank app — never trust random search results."
        ),
        AppLanguage.KANNADA to listOf(
            "ನಿಮ್ಮ OTP, UPI PIN ಅಥವಾ ಕಾರ್ಡ್ CVV ಅನ್ನು ಯಾರೊಂದಿಗೂ ಹಂಚಿಕೊಳ್ಳಬೇಡಿ — ಬ್ಯಾಂಕ್ ಅಧಿಕಾರಿ ಎಂದು ಹೇಳುವವರಿಗೂ ಸಹ.",
            "ಹಣ ಪಡೆಯಲು ನೀವು ಎಂದಿಗೂ UPI PIN ನಮೂದಿಸುವ ಅಥವಾ QR ಕೋಡ್ ಸ್ಕ್ಯಾನ್ ಮಾಡುವ ಅಗತ್ಯವಿಲ್ಲ.",
            "KYC ಅಪ್‌ಡೇಟ್ ಹೆಸರಿನಲ್ಲಿ ಅಪರಿಚಿತರು ಹೇಳುವ AnyDesk ಅಥವಾ TeamViewer ಆ್ಯಪ್‌ಗಳನ್ನು ಇನ್‌ಸ್ಟಾಲ್ ಮಾಡಬೇಡಿ.",
            "ಗ್ರಾಹಕ ಸೇವಾ ಸಂಖ್ಯೆಯನ್ನು ನಿಮ್ಮ ಬ್ಯಾಂಕ್ ಪಾಸ್‌ಬುಕ್ ಅಥವಾ ಅಧಿಕೃತ ಆ್ಯಪ್‌ನಲ್ಲಿ ಮಾತ್ರ ಪರಿಶೀಲಿಸಿ."
        ),
        AppLanguage.HINDI to listOf(
            "अपना OTP, UPI PIN या कार्ड CVV किसी के साथ साझा न करें — चाहे कोई खुद को बैंक अधिकारी ही क्यों न बताए।",
            "पैसे प्राप्त करने के लिए आपको कभी भी UPI PIN डालने या QR कोड स्कैन करने की आवश्यकता नहीं होती।",
            "KYC अपडेट के नाम पर किसी अनजान कॉलर के कहने पर AnyDesk या TeamViewer जैसे स्क्रीन-शेयरिंग ऐप इंस्टॉल न करें।",
            "कस्टमर केयर नंबर हमेशा अपनी बैंक पासबुक या आधिकारिक बैंक ऐप से ही जांचें।"
        )
    )

    val lessons: List<LessonItem> = listOf(
        // MODULE 1: BASIC BANKING (5 lessons)
        LessonItem(
            id = "bb_1",
            moduleId = LearningModuleId.BASIC_BANKING,
            titleEn = "What is a Savings Account?",
            titleKn = "ಉಳಿತಾಯ ಖಾತೆ ಎಂದರೇನು?",
            titleHi = "बचत खाता (Savings Account) क्या है?",
            summaryEn = "Keep money safe in a bank and earn steady interest.",
            contentEn = "A Savings Account is a safe place in a bank to keep your hard-earned money instead of keeping cash at home.\n\n• Your money stays protected and earns small interest every quarter.\n• You can deposit and withdraw anytime using a passbook, ATM card, or UPI.\n• Under PM Jan Dhan Yojana (PMJDY), you can open a Basic Savings Account even with zero minimum balance.",
            contentKn = "ಉಳಿತಾಯ ಖಾತೆ (Savings Account) ನಿಮ್ಮ ಹಣವನ್ನು ಮನೆಯಲ್ಲಿ ಇಡುವ ಬದಲು ಬ್ಯಾಂಕ್‌ನಲ್ಲಿ ಸುರಕ್ಷಿತವಾಗಿ ಇಡುವ ಖಾತೆಯಾಗಿದೆ.\n\n• ನಿಮ್ಮ ಹಣ ಸುರಕ್ಷಿತವಾಗಿರುತ್ತದೆ ಮತ್ತು ಬ್ಯಾಂಕ್ ಬಡ್ಡಿ ನೀಡುತ್ತದೆ.\n• ಪಾಸ್‌ಬುಕ್, ATM ಕಾರ್ಡ್ ಅಥವಾ UPI ಮೂಲಕ ಹಣ ಪಡೆಯಬಹುದು.\n• PMJDY ಅಡಿಯಲ್ಲಿ ಶೂನ್ಯ ಬ್ಯಾಲೆನ್ಸ್ ಖಾತೆಯನ್ನೂ ತೆರೆಯಬಹುದು.",
            contentHi = "बचत खाता (Savings Account) घर में नकदी रखने के बजाय बैंक में पैसा सुरक्षित रखने का सबसे अच्छा तरीका है।\n\n• आपका पैसा सुरक्षित रहता है और बैंक उस पर ब्याज देता है।\n• आप पासबुक, एटीएम कार्ड या UPI से कभी भी पैसे निकाल सकते हैं।\n• जन धन योजना (PMJDY) के तहत जीरो बैलेंस खाता भी खोला जा सकता है।",
            keyTakeawayEn = "Bank deposits are far safer than keeping cash at home and earn interest automatically.",
            durationMinutes = 3
        ),
        LessonItem(
            id = "bb_2",
            moduleId = LearningModuleId.BASIC_BANKING,
            titleEn = "Passbook & Bank Statements",
            titleKn = "ಪಾಸ್‌ಬುಕ್ ಮತ್ತು ಬ್ಯಾಂಕ್ ಸ್ಟೇಟ್‌ಮೆಂಟ್",
            titleHi = "पासबुक और बैंक स्टेटमेंट",
            summaryEn = "Track every rupee credited or debited from your account.",
            contentEn = "Your bank passbook is your written record of every deposit (Credit) and withdrawal (Debit).\n\n• Update your passbook regularly at the bank or passbook kiosk.\n• Check unknown entries immediately.\n• Never share photos of your account number and passbook with strangers on WhatsApp.",
            contentKn = "ನಿಮ್ಮ ಪಾಸ್‌ಬುಕ್ ಪ್ರತಿ ಜಮೆ (Credit) ಮತ್ತು ಖರ್ಚು (Debit) ವಿವರಗಳನ್ನು ತೋರಿಸುತ್ತದೆ.\n\n• ನಿಯಮಿತವಾಗಿ ಪಾಸ್‌ಬುಕ್ ಪ್ರಿಂಟ್ ಮಾಡಿಸಿ.\n• ಅಪರಿಚಿತ ವಹಿವಾಟು ಕಂಡರೆ ತಕ್ಷಣ ಬ್ಯಾಂಕ್‌ಗೆ ತಿಳಿಸಿ.",
            contentHi = "पासबुक आपके खाते में आने वाले (Credit) और जाने वाले (Debit) हर पैसे का रिकॉर्ड रखती है।\n\n• नियमित रूप से पासबुक अपडेट कराएं।\n• किसी अनजान लेनदेन पर तुरंत बैंक से संपर्क करें।",
            keyTakeawayEn = "Check your passbook or SMS alerts regularly to catch unauthorized transactions early.",
            durationMinutes = 3
        ),
        LessonItem(
            id = "bb_3",
            moduleId = LearningModuleId.BASIC_BANKING,
            titleEn = "Using an ATM Card Safely",
            titleKn = "ATM ಕಾರ್ಡ್ ಸುರಕ್ಷಿತ ಬಳಕೆ",
            titleHi = "एटीएम कार्ड का सुरक्षित उपयोग",
            summaryEn = "Protect your 4-digit ATM PIN and card details.",
            contentEn = "An ATM / Debit Card lets you withdraw cash and pay at shops.\n\n• Cover the keypad with your hand while typing your 4-digit PIN.\n• Never write your PIN on the card or keep it in your wallet.\n• Never accept help from strangers standing inside the ATM booth.\n• Count your cash and wait for the welcome screen before leaving.",
            contentKn = "ATM ಕಾರ್ಡ್‌ನಿಂದ ಹಣ ತೆಗೆಯುವಾಗ ಕೀಪ್ಯಾಡ್ ಅನ್ನು ಕೈಯಿಂದ ಮುಚ್ಚಿ PIN ನಮೂದಿಸಿ.\n\n• PIN ಅನ್ನು ಕಾರ್ಡ್ ಮೇಲೆ ಬರೆಯಬೇಡಿ.\n• ATM ಒಳಗೆ ಅಪರಿಚಿತರ ಸಹಾಯ ಪಡೆಯಬೇಡಿ.",
            contentHi = "एटीएम से पैसे निकालते समय हमेशा कीपैड को हाथ से ढककर अपना 4-अंकीय PIN डालें।\n\n• कार्ड पर कभी भी PIN न लिखें।\n• एटीएम के अंदर किसी अनजान व्यक्ति की मदद न लें।",
            keyTakeawayEn = "Shield the ATM keypad and never take help from strangers inside an ATM.",
            durationMinutes = 4
        ),
        LessonItem(
            id = "bb_4",
            moduleId = LearningModuleId.BASIC_BANKING,
            titleEn = "What is an IFSC Code?",
            titleKn = "IFSC ಕೋಡ್ ಎಂದರೇನು?",
            titleHi = "IFSC कोड क्या होता है?",
            summaryEn = "Understand the 11-character bank branch address.",
            contentEn = "IFSC stands for Indian Financial System Code. It is an 11-character code (for example, SBIN0001234) printed on your cheque book and passbook.\n\n• Think of IFSC as the digital pin code of your exact bank branch.\n• It is required for NEFT, RTGS, and IMPS bank transfers.\n• Sharing an IFSC code alone cannot steal money, unlike OTP or PIN.",
            contentKn = "IFSC ಎಂದರೆ ನಿಮ್ಮ ಬ್ಯಾಂಕ್ ಶಾಖೆಯ 11-ಅಕ್ಷರಗಳ ವಿಳಾಸ ಕೋಡ್. NEFT/RTGS ಹಣ ವರ್ಗಾವಣೆಗೆ ಇದು ಅಗತ್ಯ.",
            contentHi = "IFSC आपके बैंक ब्रांच का 11-अक्षरों का डिजिटल पता होता है। यह पासबुक और चेकबुक पर छपा होता है और NEFT/IMPS में काम आता है।",
            keyTakeawayEn = "IFSC identifies your bank branch for transfers and is printed on your passbook.",
            durationMinutes = 2
        ),
        LessonItem(
            id = "bb_5",
            moduleId = LearningModuleId.BASIC_BANKING,
            titleEn = "Debit Card vs Credit Card",
            titleKn = "ಡೆಬಿಟ್ ಕಾರ್ಡ್ ಮತ್ತು ಕ್ರೆಡಿಟ್ ಕಾರ್ಡ್ ವ್ಯತ್ಯಾಸ",
            titleHi = "डेबिट कार्ड बनाम क्रेडिट कार्ड",
            summaryEn = "Spend your own money vs borrowed money with interest.",
            contentEn = "A Debit Card spends money that is already in your savings account.\n\nA Credit Card is a short-term loan from the bank. If you do not pay the full bill on time, banks charge very high interest (often 30%–42% per year).\n\n• First-time digital users should start with a Debit Card or UPI and avoid unnecessary debt.",
            contentKn = "ಡೆಬಿಟ್ ಕಾರ್ಡ್ ನಿಮ್ಮ ಖಾತೆಯಲ್ಲಿರುವ ಹಣವನ್ನೇ ಬಳಸುತ್ತದೆ. ಕ್ರೆಡಿಟ್ ಕಾರ್ಡ್ ಸಾಲವಾಗಿದೆ — ಸಮಯಕ್ಕೆ ಮರುಪಾವತಿಸದಿದ್ದರೆ ಹೆಚ್ಚಿನ ಬಡ್ಡಿ ಬೀಳುತ್ತದೆ.",
            contentHi = "डेबिट कार्ड आपके बैंक खाते में मौजूद आपका अपना पैसा खर्च करता है। क्रेडिट कार्ड बैंक से लिया गया उधार है — समय पर न चुकाने पर भारी ब्याज लगता है।",
            keyTakeawayEn = "Debit uses your own balance; Credit is a loan that becomes costly if unpaid.",
            durationMinutes = 3
        ),

        // MODULE 2: SAVINGS & PLANNING (4 lessons)
        LessonItem(
            id = "sp_1",
            moduleId = LearningModuleId.SAVINGS_PLANNING,
            titleEn = "Pay Yourself First: Monthly Saving",
            titleKn = "ಮೊದಲು ಉಳಿತಾಯ ಮಾಡಿ",
            titleHi = "पहले बचत करें: मासिक बचत नियम",
            summaryEn = "Set aside savings as soon as income arrives.",
            contentEn = "Most families try to save whatever is left at the end of the month — and often nothing is left.\n\nInstead, try: Income - Savings = Expenses.\n• Even saving ₹500 or ₹1,000 every month builds a strong cushion over a year.\n• Start small, stay consistent, and aim for 15%–20% savings when possible.",
            contentKn = "ತಿಂಗಳ ಕೊನೆಯಲ್ಲಿ ಉಳಿದದ್ದನ್ನು ಉಳಿಸುವ ಬದಲು, ಆದಾಯ ಬಂದ ತಕ್ಷಣ ಉಳಿತಾಯವನ್ನು ಪ್ರತ್ಯೇಕವಾಗಿ ಇಡಿ.",
            contentHi = "महीने के अंत में बचे पैसे बचाने के बजाय, आय आते ही पहले बचत अलग रख दें। छोटी शुरुआत भी बड़ा सहारा बनती है।",
            keyTakeawayEn = "Save a fixed amount right when you receive income before spending on extras.",
            durationMinutes = 3
        ),
        LessonItem(
            id = "sp_2",
            moduleId = LearningModuleId.SAVINGS_PLANNING,
            titleEn = "Needs vs Wants",
            titleKn = "ಅಗತ್ಯಗಳು ಮತ್ತು ಆಸೆಗಳು (Needs vs Wants)",
            titleHi = "जरूरतें बनाम इच्छाएं (Needs vs Wants)",
            summaryEn = "Separate essential living expenses from optional desires.",
            contentEn = "NEEDS are essentials you cannot skip: food, housing, school fees, essential medicines, electricity & water bills.\n\nWANTS are nice-to-have items you can delay: impulse shopping, extra subscriptions, eating out frequently, luxury upgrades.\n\n• Covering Needs first prevents borrowing for emergencies.",
            contentKn = "ಆಹಾರ, ಮನೆ, ಶಿಕ್ಷಣ ಮತ್ತು ಔಷಧಿ ಅಗತ್ಯಗಳು (Needs). ಅನಗತ್ಯ ಶಾಪಿಂಗ್ ಮತ್ತು ಮನರಂಜನೆ ಆಸೆಗಳು (Wants).",
            contentHi = "भोजन, घर, पढ़ाई और दवाई ज़रूरतें (Needs) हैं। दिखावे की खरीदारी या अनावश्यक खर्च इच्छाएं (Wants) हैं।",
            keyTakeawayEn = "Always budget for Needs and Emergency Savings before spending on Wants.",
            durationMinutes = 3
        ),
        LessonItem(
            id = "sp_3",
            moduleId = LearningModuleId.SAVINGS_PLANNING,
            titleEn = "Building an Emergency Fund",
            titleKn = "ತುರ್ತು ನಿಧಿ (Emergency Fund) ನಿರ್ಮಾಣ",
            titleHi = "आपातकालीन फंड (Emergency Fund) बनाना",
            summaryEn = "Protect your household from sudden medical or job shocks.",
            contentEn = "An Emergency Fund is money kept strictly for unexpected events like medical bills, crop loss, or sudden repairs.\n\n• Goal: Save at least 3 to 6 months of basic household expenses.\n• Keep it in a safe savings account or short-term bank deposit so you can access it fast without taking high-interest loans.",
            contentKn = "ಆರೋಗ್ಯ ತುರ್ತು ಅಥವಾ ಅನಿರೀಕ್ಷಿತ ಖರ್ಚುಗಳಿಗೆ 3-6 ತಿಂಗಳ ಮನೆ ಖರ್ಚಿನಷ್ಟು ಹಣವನ್ನು ಸುರಕ್ಷಿತವಾಗಿ ಉಳಿಸಿ.",
            contentHi = "अचानक बीमारी या काम बंद होने जैसी स्थिति के लिए कम से कम 3 से 6 महीने के घरेलू खर्च के बराबर आपातकालीन फंड रखें।",
            keyTakeawayEn = "An Emergency Fund keeps your family out of high-interest debt during crises.",
            durationMinutes = 4
        ),
        LessonItem(
            id = "sp_4",
            moduleId = LearningModuleId.SAVINGS_PLANNING,
            titleEn = "Smart Household Budgeting",
            titleKn = "ಸ್ಮಾರ್ಟ್ ಕುಟುಂಬ ಬಜೆಟ್",
            titleHi = "स्मार्ट घरेलू बजट योजना",
            summaryEn = "Use the 50-30-20 guide adapted for Indian households.",
            contentEn = "A simple monthly guide:\n• 50%–60% for Essential Needs (groceries, rent, bills, school, health)\n• 20%–30% for Savings & Debt Repayment\n• 10%–20% for Family Wants & Festivals\n\nUse FinSmart's Money Manager to record real expenses and track your monthly savings percentage.",
            contentKn = "ನಿಮ್ಮ ಆದಾಯದಲ್ಲಿ 50-60% ಅಗತ್ಯಗಳಿಗೆ, 20-30% ಉಳಿತಾಯಕ್ಕೆ ಮತ್ತು ಉಳಿದದ್ದನ್ನು ಇತರೆ ಖರ್ಚಿಗೆ ಮೀಸಲಿಡಿ.",
            contentHi = "अपनी आय का 50-60% ज़रूरतों पर, 20-30% बचत पर और बाकी अन्य खर्चों पर रखें। FinSmart Money Manager से हर खर्च ट्रैक करें।",
            keyTakeawayEn = "Tracking every expense for 30 days reveals where money quietly leaks.",
            durationMinutes = 4
        ),

        // MODULE 3: DIGITAL PAYMENTS (UPI) (4 lessons)
        LessonItem(
            id = "up_1",
            moduleId = LearningModuleId.DIGITAL_UPI,
            titleEn = "How UPI Works Simply",
            titleKn = "UPI ಹೇಗೆ ಕೆಲಸ ಮಾಡುತ್ತದೆ?",
            titleHi = "UPI कैसे काम करता है?",
            summaryEn = "Instant bank-to-bank transfers using a UPI ID or QR.",
            contentEn = "UPI (Unified Payments Interface) connects your bank account directly to payment apps like BHIM, PhonePe, Google Pay, or Paytm.\n\n• Money moves directly from bank to bank in seconds.\n• You create a 4- or 6-digit UPI PIN that acts like a lock on your account.\n• Never share your UPI PIN with anyone.",
            contentKn = "UPI ನಿಮ್ಮ ಬ್ಯಾಂಕ್ ಖಾತೆಯಿಂದ ನೇರವಾಗಿ ಇನ್ನೊಂದು ಬ್ಯಾಂಕ್ ಖಾತೆಗೆ ಸೆಕೆಂಡುಗಳಲ್ಲಿ ಹಣ ಕಳುಹಿಸುತ್ತದೆ. UPI PIN ಅನ್ನು ರಹಸ್ಯವಾಗಿಡಿ.",
            contentHi = "UPI आपके बैंक खाते को सीधे दूसरे बैंक खाते से जोड़ता है। पैसे भेजने के लिए आपका 4 या 6 अंकों का UPI PIN ही मुख्य सुरक्षा ताला है।",
            keyTakeawayEn = "UPI transfers directly from your bank; your UPI PIN protects your money.",
            durationMinutes = 3
        ),
        LessonItem(
            id = "up_2",
            moduleId = LearningModuleId.DIGITAL_UPI,
            titleEn = "Receiving Money NEVER Needs PIN",
            titleKn = "ಹಣ ಪಡೆಯಲು UPI PIN ಬೇಕಾಗಿಲ್ಲ!",
            titleHi = "पैसे पाने के लिए कभी UPI PIN नहीं लगता!",
            summaryEn = "The #1 rule that stops 80% of UPI QR and collect scams.",
            contentEn = "IMPORTANT GOLDEN RULE:\n\n• You enter your UPI PIN ONLY when money LEAVES your bank account.\n• To RECEIVE money, the sender pays you — you do NOT scan a QR code, you do NOT approve a payment request, and you NEVER enter your UPI PIN.\n\nIf anyone asks you to enter PIN or scan QR to receive cashback, refund, or OLX payment, it is a scam.",
            contentKn = "ಗೋಲ್ಡನ್ ರೂಲ್: ಹಣ ಕಳುಹಿಸಲು ಮಾತ್ರ UPI PIN ಬೇಕು. ಹಣ ಪಡೆಯಲು PIN ನಮೂದಿಸಬೇಡಿ ಅಥವಾ QR ಸ್ಕ್ಯಾನ್ ಮಾಡಬೇಡಿ.",
            contentHi = "सुनहरा नियम: UPI PIN केवल पैसे भेजने के लिए होता है। पैसे प्राप्त करने के लिए न तो PIN डालना होता है और न ही QR कोड स्कैन करना होता है।",
            keyTakeawayEn = "Entering UPI PIN or scanning a QR code ALWAYS sends money out — never brings money in.",
            durationMinutes = 3
        ),
        LessonItem(
            id = "up_3",
            moduleId = LearningModuleId.DIGITAL_UPI,
            titleEn = "Safe QR Code Payments at Shops",
            titleKn = "ಅಂಗಡಿಗಳಲ್ಲಿ ಸುರಕ್ಷಿತ QR ಪಾವತಿ",
            titleHi = "दुकानों पर सुरक्षित QR भुगतान",
            summaryEn = "Check merchant name and amount before tapping Pay.",
            contentEn = "Before completing any QR payment:\n1. Look closely at the recipient name shown on your screen and ask the shopkeeper to confirm.\n2. Check the exact amount (e.g. ₹150 vs ₹1,500).\n3. Confirm nobody pasted a fake QR sticker over the shop's original QR board.",
            contentKn = "QR ಸ್ಕ್ಯಾನ್ ಮಾಡಿದ ನಂತರ ಪರದೆಯ ಮೇಲೆ ಕಾಣುವ ಹೆಸರು ಮತ್ತು ಮೊತ್ತವನ್ನು ಅಂಗಡಿಯವರೊಂದಿಗೆ ದೃಢೀಕರಿಸಿ.",
            contentHi = "QR स्कैन करने के बाद स्क्रीन पर दिखने वाले दुकानदार का नाम और सही राशि ज़रूर मिलाएं, फिर ही PIN डालें।",
            keyTakeawayEn = "Always verify the displayed recipient name and exact amount before entering your PIN.",
            durationMinutes = 3
        ),
        LessonItem(
            id = "up_4",
            moduleId = LearningModuleId.DIGITAL_UPI,
            titleEn = "Collect Requests & Payment Links",
            titleKn = "ಕಲೆಕ್ಟ್ ರಿಕ್ವೆಸ್ಟ್ ಮತ್ತು ಪೇಮೆಂಟ್ ಲಿಂಕ್‌ಗಳು",
            titleHi = "कलेक्ट रिक्वेस्ट और पेमेंट लिंक से सावधानी",
            summaryEn = "Never approve unexpected 'Pay' popups inside UPI apps.",
            contentEn = "A UPI Collect Request is someone asking YOU to pay THEM.\n\n• Scammers send collect requests labeled 'Claim ₹5,000 Refund' or 'Cashback Received'.\n• If you tap Pay and enter your PIN, ₹5,000 is deducted from YOUR account.\n• Decline any unknown collect request immediately.",
            contentKn = "ಅಪರಿಚಿತರು ಕಳುಹಿಸಿದ 'Pay / Collect Request' ಅನ್ನು ಎಂದಿಗೂ ಸ್ವೀಕರಿಸಬೇಡಿ — ಅದು ನಿಮ್ಮ ಖಾತೆಯಿಂದ ಹಣ ಕಡಿತಗೊಳಿಸುತ್ತದೆ.",
            contentHi = "अनजान कलेक्ट रिक्वेस्ट हमेशा पैसे काटने के लिए होती है, मिलने के लिए नहीं। तुरंत 'Decline' करें।",
            keyTakeawayEn = "Approve a collect request ONLY if you clearly ordered something and want to pay.",
            durationMinutes = 3
        ),

        // MODULE 4: FRAUD & SCAM SAFETY (5 lessons)
        LessonItem(
            id = "fs_1",
            moduleId = LearningModuleId.FRAUD_SAFETY,
            titleEn = "Fake KYC & Account Block SMS",
            titleKn = "ನಕಲಿ KYC ಮತ್ತು ಖಾತೆ ಬ್ಲಾಕ್ SMS",
            titleHi = "नकली KYC और खाता बंद होने के SMS",
            summaryEn = "Spot urgent bank, PAN, and electricity disconnection scams.",
            contentEn = "Scammers send fearful messages like: 'Your bank account will be blocked today, click bit.ly/kyc-update' or 'Your electricity will be cut tonight'.\n\n• Real banks never ask you to update KYC by clicking random SMS links.\n• Use FinSmart Scam Shield to scan suspicious links or messages first.",
            contentKn = "'ನಿಮ್ಮ ಖಾತೆ ಇಂದೇ ಬ್ಲಾಕ್ ಆಗುತ್ತದೆ, ಈ ಲಿಂಕ್ ಕ್ಲಿಕ್ ಮಾಡಿ' ಎಂಬ SMS ಗಳು ನಕಲಿ. ಬ್ಯಾಂಕ್ ಎಂದಿಗೂ ಲಿಂಕ್ ಮೂಲಕ KYC ಕೇಳುವುದಿಲ್ಲ.",
            contentHi = "बैंक या बिजली विभाग कभी भी SMS में अनजान लिंक भेजकर आज ही खाता बंद होने की धमकी नहीं देते। ऐसे लिंक पर क्लिक न करें।",
            keyTakeawayEn = "Urgent threats + clickable links in SMS = almost always a phishing scam.",
            durationMinutes = 4
        ),
        LessonItem(
            id = "fs_2",
            moduleId = LearningModuleId.FRAUD_SAFETY,
            titleEn = "OTP & Screen-Sharing Traps",
            titleKn = "OTP ಮತ್ತು ಸ್ಕ್ರೀನ್-ಶೇರಿಂಗ್ ಮೋಸಗಳು",
            titleHi = "OTP और स्क्रीन-शेयरिंग ऐप का जाल",
            summaryEn = "Why remote-access apps let thieves control your phone.",
            contentEn = "Never install apps like AnyDesk, TeamViewer, QuickSupport, or unknown '.apk' files sent on WhatsApp.\n\n• Screen-sharing apps let a stranger watch your OTPs, passwords, and banking apps live.\n• No legitimate bank agent, RBI officer, or customer care executive needs screen-sharing.",
            contentKn = "AnyDesk, TeamViewer ಅಥವಾ WhatsApp ಮೂಲಕ ಬರುವ APK ಆ್ಯಪ್‌ಗಳನ್ನು ಇನ್‌ಸ್ಟಾಲ್ ಮಾಡಬೇಡಿ — ಅವು ನಿಮ್ಮ ಫೋನ್ ಪರದೆಯನ್ನು ಕಳ್ಳರಿಗೆ ತೋರಿಸುತ್ತವೆ.",
            contentHi = "किसी कॉलर के कहने पर AnyDesk, TeamViewer या WhatsApp से भेजी गई APK फ़ाइल कभी इंस्टॉल न करें — इससे ठग आपका OTP देख सकते हैं।",
            keyTakeawayEn = "Never install screen-sharing apps or APK files requested by any caller.",
            durationMinutes = 4
        ),
        LessonItem(
            id = "fs_3",
            moduleId = LearningModuleId.FRAUD_SAFETY,
            titleEn = "Fake Customer-Care Numbers",
            titleKn = "ನಕಲಿ ಕಸ್ಟಮರ್ ಕೇರ್ ನಂಬರ್‌ಗಳು",
            titleHi = "नकली कस्टमर केयर नंबर की पहचान",
            summaryEn = "Avoid fraudulent numbers planted in online search results.",
            contentEn = "Scammers post fake helpline numbers on Google Maps, ads, and social media for banks, UPI apps, couriers, and gas agencies.\n\n• When you call, they pretend to help and ask you to pay ₹10 for 'verification' or share an OTP.\n• Always get official numbers ONLY from your bank's official website, passbook, or the back of your debit card.",
            contentKn = "ಗೂಗಲ್ ಸರ್ಚ್‌ನಲ್ಲಿ ಸಿಗುವ ನಂಬರ್‌ಗಳು ನಕಲಿಯಾಗಿರಬಹುದು. ಬ್ಯಾಂಕ್ ಪಾಸ್‌ಬುಕ್ ಅಥವಾ ಕಾರ್ಡ್ ಹಿಂಭಾಗದಲ್ಲಿರುವ ಅಧಿಕೃತ ಸಂಖ್ಯೆಗೆ ಮಾತ್ರ ಕರೆ ಮಾಡಿ.",
            contentHi = "इंटरनेट सर्च में दिखने वाले कई हेल्पलाइन नंबर ठगों के होते हैं। हमेशा बैंक पासबुक, डेबिट कार्ड के पीछे या आधिकारिक ऐप से ही नंबर लें।",
            keyTakeawayEn = "Never trust random customer-care numbers from web search results or SMS.",
            durationMinutes = 3
        ),
        LessonItem(
            id = "fs_4",
            moduleId = LearningModuleId.FRAUD_SAFETY,
            titleEn = "Phishing Links & Lookalike Domains",
            titleKn = "ಫಿಶಿಂಗ್ ಲಿಂಕ್‌ಗಳು ಮತ್ತು ನಕಲಿ ವೆಬ್‌ಸೈಟ್‌ಗಳು",
            titleHi = "फिशिंग लिंक और नकली वेबसाइट की पहचान",
            summaryEn = "Notice tiny spelling tricks like sbi-kyc-update.in.",
            contentEn = "Fake websites copy bank logos and colors, but their web address (domain) is different — for example, 'sbi-kyc-update.in' instead of 'sbi.co.in'.\n\n• Look carefully at the part just before '.com' or '.in'.\n• Paste any doubtful link into FinSmart Scam Shield before tapping it.",
            contentKn = "ನಕಲಿ ವೆಬ್‌ಸೈಟ್‌ಗಳು ಬ್ಯಾಂಕ್ ಲೋಗೋ ಬಳಸುತ್ತವೆ, ಆದರೆ sbi.co.in ಬದಲಿಗೆ sbi-kyc-update.in ನಂತಹ ಹೆಸರನ್ನು ಹೊಂದಿರುತ್ತವೆ.",
            contentHi = "नकली वेबसाइटें बैंक जैसा रंग-रूप रखती हैं लेकिन उनका पता अलग होता है (जैसे sbi.co.in के बजाय sbi-kyc-update.in)। पहले FinSmart Scam Shield में जांचें।",
            keyTakeawayEn = "Inspect the exact domain name and check doubtful links with FinSmart Scam Shield.",
            durationMinutes = 4
        ),
        LessonItem(
            id = "fs_5",
            moduleId = LearningModuleId.FRAUD_SAFETY,
            titleEn = "Golden Hour: What To Do If Scammed",
            titleKn = "ಮೋಸವಾದರೆ ತಕ್ಷಣ ಏನು ಮಾಡಬೇಕು (1930)?",
            titleHi = "ठगी होने पर तुरंत क्या करें (हेल्पलाइन 1930)?",
            summaryEn = "Call 1930 and report at cybercrime.gov.in immediately.",
            contentEn = "If money is stolen through fraud, the first 1–2 hours are critical:\n1. Stop talking to the scammer immediately.\n2. Call the National Cyber Crime Helpline 1930.\n3. Call your bank's official helpline to freeze your account/UPI.\n4. Report on https://cybercrime.gov.in/ with transaction ID and screenshots.\n5. Never pay someone who promises to recover your lost money.",
            contentKn = "ಮೋಸವಾದ ತಕ್ಷಣ 1930 ಗೆ ಕರೆ ಮಾಡಿ, ನಿಮ್ಮ ಬ್ಯಾಂಕ್‌ಗೆ ತಿಳಿಸಿ ಮತ್ತು cybercrime.gov.in ನಲ್ಲಿ ದೂರು ದಾಖಲಿಸಿ.",
            contentHi = "ठगी होने पर तुरंत 1930 पर कॉल करें, अपने बैंक को सूचित करें और https://cybercrime.gov.in/ पर रिपोर्ट दर्ज करें।",
            keyTakeawayEn = "Act immediately: Dial 1930, contact your bank, and save all evidence.",
            durationMinutes = 4
        )
    )

    val quizzes: List<QuizQuestion> = listOf(
        QuizQuestion(
            id = "q1",
            moduleId = LearningModuleId.DIGITAL_UPI,
            question = "A buyer says they are sending ₹2,000 for your old bicycle and sends a QR code asking you to scan and enter your UPI PIN. What happens if you do?",
            options = listOf(
                "You will receive ₹2,000 instantly",
                "₹2,000 will be DEDUCTED from your bank account",
                "Your bank KYC gets verified",
                "Nothing happens"
            ),
            correctIndex = 1,
            explanation = "Entering your UPI PIN after scanning a QR code ALWAYS sends money out of your account. Receiving money never requires scanning a QR code or entering a PIN."
        ),
        QuizQuestion(
            id = "q2",
            moduleId = LearningModuleId.FRAUD_SAFETY,
            question = "You receive an SMS: 'Your SBI account will be blocked today. Update KYC at http://sbi-kyc-update.in'. What should you do?",
            options = listOf(
                "Tap the link and enter your netbanking password",
                "Call the mobile number inside the SMS",
                "Do not tap the link; check inside your official bank app or visit the branch",
                "Forward your Aadhaar photo to the sender"
            ),
            correctIndex = 2,
            explanation = "sbi-kyc-update.in is not an official SBI domain, and banks do not block accounts through urgent SMS links."
        ),
        QuizQuestion(
            id = "q3",
            moduleId = LearningModuleId.BASIC_BANKING,
            question = "Which information is safe to share when someone genuinely needs to transfer money into your bank account via NEFT?",
            options = listOf(
                "Your ATM PIN and CVV",
                "Your OTP received on SMS",
                "Your Account Number and IFSC Code",
                "Your UPI PIN"
            ),
            correctIndex = 2,
            explanation = "Account number and IFSC code are needed to receive NEFT/IMPS bank transfers. Never share OTP, PIN, CVV, or passwords."
        ),
        QuizQuestion(
            id = "q4",
            moduleId = LearningModuleId.FRAUD_SAFETY,
            question = "Someone claiming to be a bank helper asks you to install AnyDesk or TeamViewer to 'activate your refund'. Why is this dangerous?",
            options = listOf(
                "It slows down battery charging",
                "It lets the stranger see and control your phone screen, OTPs, and banking apps",
                "It deletes your contacts",
                "It changes your ringtone"
            ),
            correctIndex = 1,
            explanation = "Screen-sharing apps expose every OTP and action on your phone to the caller. Legitimate banks never ask you to install remote-access apps."
        ),
        QuizQuestion(
            id = "q5",
            moduleId = LearningModuleId.SAVINGS_PLANNING,
            question = "Which of these is an essential NEED rather than a WANT?",
            options = listOf(
                "Monthly medicines and school fees",
                "Flash-sale luxury watch",
                "Extra streaming subscriptions",
                "Impulse online shopping"
            ),
            correctIndex = 0,
            explanation = "Healthcare and education are essential Needs; luxury shopping and extra entertainment subscriptions are Wants."
        ),
        QuizQuestion(
            id = "q6",
            moduleId = LearningModuleId.FRAUD_SAFETY,
            question = "If you lose money in an online payment scam in India, what is the official National Cyber Crime Helpline number to call immediately?",
            options = listOf("1000", "1930", "1400", "1999"),
            correctIndex = 1,
            explanation = "1930 is India's National Cyber Crime Helpline, alongside the official portal https://cybercrime.gov.in/."
        )
    )

    val fraudScenarios: List<FraudScenario> = listOf(
        FraudScenario(
            id = "sc_1",
            title = "Fake Bank OTP Call",
            situation = "Someone calls speaking politely: 'Hello sir, I am calling from your bank's head office. Your debit card is expiring tonight. Please tell me the 6-digit OTP we just sent so I can renew it free.'",
            options = listOf(
                "Read out the OTP quickly so the card is not blocked",
                "Hang up immediately and never share the OTP",
                "Ask them to wait while you check your PIN too"
            ),
            safeOptionIndex = 1,
            explanation = "Banks NEVER call asking for your OTP, PIN, or CVV. That OTP is actually authorizing a transaction by the scammer.",
            safetyRule = "Never share OTP with anyone — even if they know your name or card digits."
        ),
        FraudScenario(
            id = "sc_2",
            title = "QR Code to Receive Cashback",
            situation = "You receive a WhatsApp message saying you won ₹4,999 cashback! The sender shares a QR code and says: 'Scan this in your UPI app and enter your PIN to claim ₹4,999 into your bank.'",
            options = listOf(
                "Scan the QR code and enter UPI PIN to receive ₹4,999",
                "Refuse and block — QR + UPI PIN only sends money out",
                "Ask a friend to scan it on their phone instead"
            ),
            safeOptionIndex = 1,
            explanation = "Scanning a QR code and entering your UPI PIN ALWAYS debits money from your account. It can never deposit money.",
            safetyRule = "Zero PIN needed to receive money."
        ),
        FraudScenario(
            id = "sc_3",
            title = "1-Hour KYC Expiry Threat",
            situation = "An SMS arrives: 'Dear Customer, your PAN-KYC expires in 1 hour! Account will be frozen. Click bit.ly/kyc-fast-bank now.'",
            options = listOf(
                "Tap the shortened bit.ly link right away",
                "Ignore the link, inspect it in FinSmart Scam Shield, or open your official bank app directly",
                "Reply with your PAN and Aadhaar numbers"
            ),
            safeOptionIndex = 1,
            explanation = "Scammers use fake 1-hour panic deadlines and URL shorteners (bit.ly) to hide phishing websites.",
            safetyRule = "Never act in panic from an SMS link; check directly in your official bank app."
        ),
        FraudScenario(
            id = "sc_4",
            title = "Unknown UPI Collect Request",
            situation = "An unknown buyer on an online marketplace sends a ₹5,000 payment request inside your UPI app with the note 'Receive ₹5,000 Advance'. The button says 'PAY ₹5,000'.",
            options = listOf(
                "Tap PAY and enter your UPI PIN",
                "Tap DECLINE — a 'PAY' prompt takes money FROM you",
                "Wait 5 minutes and then accept"
            ),
            safeOptionIndex = 1,
            explanation = "Even if the message note says 'Receive Advance', a UPI collect prompt with a 'PAY' button always debits your account.",
            safetyRule = "Decline any collect request you did not initiate to pay for a purchase."
        ),
        FraudScenario(
            id = "sc_5",
            title = "Fake Customer-Care & Screen Share",
            situation = "You searched online for a courier helpline and called the top number. The agent says: 'Your refund is ready. Please install AnyDesk from Play Store and tell me the 9-digit code.'",
            options = listOf(
                "Install AnyDesk and share the 9-digit code",
                "End the call immediately — never install remote-access apps for support",
                "Give them your UPI PIN directly instead"
            ),
            safeOptionIndex = 1,
            explanation = "Random search results often carry scammer phone numbers. AnyDesk gives them full view and control of your banking apps and OTPs.",
            safetyRule = "Never install remote-access apps (AnyDesk/TeamViewer) for customer support."
        )
    )

    val govSchemes: List<GovSchemeInfo> = listOf(
        GovSchemeInfo(
            id = "pmjdy",
            name = "Pradhan Mantri Jan Dhan Yojana",
            shortName = "PMJDY",
            purpose = "Universal access to banking facilities with a zero-balance Basic Savings Bank Deposit Account and RuPay debit card.",
            eligibility = listOf(
                "Any Indian citizen aged 10 years and above (minors via guardian) who does not have an existing bank account or wishes to open a BSBDA.",
                "Eligibility depends on official bank KYC norms."
            ),
            benefits = listOf(
                "No minimum balance requirement on Basic Savings Bank Deposit Account.",
                "RuPay Debit Card with built-in accidental insurance cover (subject to active card usage rules on official portal).",
                "Direct Benefit Transfer (DBT) of government subsidies straight to the bank account."
            ),
            documents = listOf("Aadhaar Card (or Voter ID / PAN / Passport / Driving License / NREGA Job Card)", "Passport-size photograph"),
            applicationSteps = listOf(
                "Visit your nearest scheduled bank branch or Bank Mitra (Business Correspondent).",
                "Fill out the official PMJDY / BSBDA account opening form.",
                "Submit KYC documents for verification — no fee is charged to open a PMJDY account."
            ),
            officialUrl = "https://pmjdy.gov.in/",
            lastVerified = "November 2026",
            agentWarning = "PMJDY account opening is FREE at bank branches and authorized Bank Mitras. Never pay middlemen or share your OTP with unofficial agents."
        ),
        GovSchemeInfo(
            id = "apy",
            name = "Atal Pension Yojana",
            shortName = "APY",
            purpose = "Government-backed pension scheme focused on workers in the unorganised sector to build a guaranteed monthly pension after age 60.",
            eligibility = listOf(
                "Indian citizen aged between 18 and 40 years.",
                "Must hold a savings bank account or post office savings account.",
                "Per current rules (post-Oct 2022), income tax payers are not eligible to join newly."
            ),
            benefits = listOf(
                "Guaranteed minimum monthly pension of ₹1,000, ₹2,000, ₹3,000, ₹4,000, or ₹5,000 starting at age 60 depending on contribution.",
                "Spouse continues receiving the pension after subscriber's demise, and accumulated corpus goes to nominee."
            ),
            documents = listOf("Active Bank/Post Office Savings Account", "Aadhaar & Mobile Number linked to account"),
            applicationSteps = listOf(
                "Approach the bank branch or post office where your savings account is held, or check your bank's official netbanking/mobile app.",
                "Fill the APY registration form and choose your pension slab and auto-debit frequency.",
                "Verify confirmation receipt and PRAN number through official NSDL/bank channels."
            ),
            officialUrl = "https://www.npscra.nsdl.co.in/scheme-details.php",
            lastVerified = "November 2026",
            agentWarning = "Enroll only through your official bank branch, post office, or official bank app. Never transfer APY premiums to a personal UPI ID."
        ),
        GovSchemeInfo(
            id = "pmsby",
            name = "Pradhan Mantri Suraksha Bima Yojana",
            shortName = "PMSBY",
            purpose = "Low-cost accidental death and disability insurance scheme renewed annually via bank auto-debit.",
            eligibility = listOf(
                "Individuals aged 18 to 70 years with an active savings bank or post office account.",
                "Consent for annual auto-debit of premium."
            ),
            benefits = listOf(
                "Accidental death or total permanent disability cover of ₹2,00,000.",
                "Partial permanent disability cover of ₹1,00,000.",
                "Affordable annual premium (₹20 per year per current scheme terms, subject to official updates)."
            ),
            documents = listOf("Savings Bank Account details", "Aadhaar Card / KYC", "Nominee details"),
            applicationSteps = listOf(
                "Visit your bank branch, Bank Mitra, or log in to your bank's official mobile/netbanking portal.",
                "Submit the PMSBY consent and nomination form before the annual renewal cycle.",
                "Keep your SMS confirmation and Certificate of Insurance safely."
            ),
            officialUrl = "https://jansuraksha.gov.in/",
            lastVerified = "November 2026",
            agentWarning = "Never pay cash to strangers promising instant government insurance cards. Premiums are auto-debited officially from your bank account."
        ),
        GovSchemeInfo(
            id = "pmjjby",
            name = "Pradhan Mantri Jeevan Jyoti Bima Yojana",
            shortName = "PMJJBY",
            purpose = "Affordable one-year renewable life insurance cover for death due to any reason.",
            eligibility = listOf(
                "Individuals aged 18 to 50 years having an active savings bank or post office account.",
                "Consent for annual auto-debit (cover continues up to age 55 if renewed continuously)."
            ),
            benefits = listOf(
                "Life insurance cover of ₹2,00,000 payable to the nominee on death due to any cause (subject to official lien period terms).",
                "Annual premium (₹436 per year per current official terms, or pro-rata depending on enrollment quarter)."
            ),
            documents = listOf("Savings Bank / Post Office Account", "Aadhaar KYC", "Nominee name and relationship"),
            applicationSteps = listOf(
                "Enroll via your bank branch, official bank app, or JanSamarth / JanSuraksha bank channel.",
                "Provide nominee details and authorize auto-debit.",
                "Download and save your official policy acknowledgment."
            ),
            officialUrl = "https://jansuraksha.gov.in/",
            lastVerified = "November 2026",
            agentWarning = "Do not share bank OTPs with callers claiming to 'verify PMJJBY'. Enroll only through your bank."
        )
    )

    val beforeYouPaySituations: List<BeforeYouPaySituation> = listOf(
        BeforeYouPaySituation(
            id = "upi_transfer",
            title = "Send money through UPI",
            subtitle = "Transfer money to someone",
            riskReminder = "Once sent via UPI, money cannot be cancelled with a tap. Verify the recipient first.",
            checklist = listOf(
                "Verify the recipient's registered bank name shown on screen.",
                "Confirm the exact rupee amount (watch out for extra zeros).",
                "Know the real reason for the payment — never pay a fee to claim a prize or job.",
                "Never enter your UPI PIN if you are supposed to RECEIVE money.",
                "If paying a stranger, consult a trusted family member first."
            )
        ),
        BeforeYouPaySituation(
            id = "qr_scan",
            title = "Scan a QR code",
            subtitle = "Pay at a shop or online",
            riskReminder = "Scanning a QR code and entering your PIN ALWAYS sends money OUT.",
            checklist = listOf(
                "Confirm the QR code belongs to the real shop (check for pasted stickers over the board).",
                "Verify the shop/merchant name that appears after scanning.",
                "Check the amount carefully before entering your UPI PIN.",
                "Never scan a QR code sent on WhatsApp to 'receive' a refund or payment.",
                "Keep your payment receipt or UTR reference if paying a merchant."
            )
        ),
        BeforeYouPaySituation(
            id = "collect_request",
            title = "Accept a payment request",
            subtitle = "Someone is asking you to pay",
            riskReminder = "A collect request is a bill asking YOU to pay. Approving it deducts money.",
            checklist = listOf(
                "Stop and read clearly: tapping PAY will deduct money from YOUR account.",
                "Ignore messages written in the note like 'Click Pay to receive ₹2,000'.",
                "Decline immediately if you did not order anything from that merchant.",
                "Verify the requester's UPI handle is an official service you expect.",
                "Never share your screen or OTP while reviewing a payment request."
            )
        ),
        BeforeYouPaySituation(
            id = "online_seller",
            title = "Pay an online seller",
            subtitle = "E-commerce or service payment",
            riskReminder = "Unverified social-media sellers and fake websites often vanish after advance UPI payment.",
            checklist = listOf(
                "Verify the website domain carefully (avoid lookalike sites or bit.ly links).",
                "Be cautious if an unknown seller insists on 100% advance UPI payment with huge discounts.",
                "Prefer Cash-on-Delivery or verified payment gateways for unfamiliar stores.",
                "Check independent reviews and verify official contact details.",
                "Never pay extra 'customs fee' or 'delivery unlock deposit' to strangers."
            )
        )
    )

    val needsVsWantsItems: List<NeedsVsWantsItem> = listOf(
        NeedsVsWantsItem("nw_1", "Monthly Rice, Dal & Vegetables", "₹4,500", true, "Essential daily nutrition for the household is a Need."),
        NeedsVsWantsItem("nw_2", "Electricity & Water Bill", "₹1,200", true, "Keeping lights and clean water running at home is a Need."),
        NeedsVsWantsItem("nw_3", "Children's School Fees & Books", "₹3,000", true, "Education builds future stability and is a core Need."),
        NeedsVsWantsItem("nw_4", "Flash-Sale Designer Sneakers", "₹3,500", false, "Replacing usable footwear just for style or a sale is a Want."),
        NeedsVsWantsItem("nw_5", "Prescribed Family Medicines", "₹900", true, "Healthcare and prescribed medicines are essential Needs."),
        NeedsVsWantsItem("nw_6", "Multiple Paid Movie Subscriptions", "₹800", false, "Extra entertainment subscriptions can be paused or trimmed — a Want."),
        NeedsVsWantsItem("nw_7", "Bus/Train Pass for Work", "₹1,100", true, "Essential travel required to earn income is a Need."),
        NeedsVsWantsItem("nw_8", "Impulse Late-Night Food Delivery", "₹650", false, "Frequent convenience ordering outside your meal plan is a Want.")
    )

    val awarenessVideos: List<AwarenessVideoGuide> = listOf(
        AwarenessVideoGuide(
            id = "vid_1",
            title = "How to Spot Fake KYC Messages in 30 Seconds",
            category = "Scam Safety",
            duration = "2 min audio guide",
            narrationScript = "Welcome to FinSmart Audio Guide. When an SMS threatens that your bank account or electricity will be cut today, pause for ten seconds. Check three clues: first, is there a strange shortened link like bit.ly or an unknown dot-in website? Second, does it ask for OTP, PIN, or KYC upload? Third, does it give a personal 10-digit mobile number? Real banks never operate this way. Close the message and verify inside your official bank app.",
            keyPoints = listOf("Spot panic deadlines", "Check the real domain name", "Never tap shortened links")
        ),
        AwarenessVideoGuide(
            id = "vid_2",
            title = "UPI Golden Rule: Receive vs Pay",
            category = "Digital Payments",
            duration = "2 min audio guide",
            narrationScript = "Always remember FinSmart's UPI Golden Rule. You enter your four-digit or six-digit UPI PIN only when money goes OUT of your account. If someone is sending you money for selling an item, winning a refund, or receiving rent, you do NOT need to scan a QR code, approve a collect request, or type your UPI PIN. If an app asks for your PIN, money is leaving your bank.",
            keyPoints = listOf("PIN = Money Leaving", "Never scan QR to receive", "Decline unknown collect requests")
        ),
        AwarenessVideoGuide(
            id = "vid_3",
            title = "5-Minute Monthly Family Budget Plan",
            category = "Money Management",
            duration = "3 min audio guide",
            narrationScript = "Start your month on paper or in FinSmart Money Manager. First write your total monthly income. Next, set aside your savings goal first, even if it is five hundred rupees. Then list essential household needs: groceries, school fees, medicines, electricity, and transport. What remains can be planned carefully for family wants without borrowing.",
            keyPoints = listOf("Write income first", "Pay savings first", "Track household expenses daily")
        )
    )

    val badges: List<SafetyBadge> = listOf(
        SafetyBadge("first_step", "First Safety Step", "Completed your first FinSmart lesson", requiredLessons = 1),
        SafetyBadge("banking_basics", "Banking Aware", "Completed 5 financial literacy lessons", requiredLessons = 5),
        SafetyBadge("quiz_starter", "Quiz Champion", "Completed your first Safety Knowledge Quiz", requiredQuizzes = 1),
        SafetyBadge("scam_fighter", "Scam Shield Defender", "Completed 10 lessons & tested Scam Shield", requiredLessons = 10),
        SafetyBadge("master_guardian", "FinSmart Guardian", "Completed all 18 core lessons", requiredLessons = 18)
    )
}
