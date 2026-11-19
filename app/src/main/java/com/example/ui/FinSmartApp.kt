package com.example.ui

import android.app.Application
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.core.accessibility.FinSmartTtsManager
import com.example.core.accessibility.NetworkConnectivityObserver
import com.example.data.local.FinSmartStrings
import com.example.data.local.PreferencesManager
import com.example.data.local.UserSettingsState
import com.example.data.model.LearningModuleId
import com.example.data.repository.AiSahayakRepository
import com.example.data.repository.FinSmartFirestoreRepository
import com.example.ui.screens.AiSahayakScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.AwarenessVideosScreen
import com.example.ui.screens.BeforeYouPayScreen
import com.example.ui.screens.FraudSimulatorScreen
import com.example.ui.screens.GovSchemesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LearnScreen
import com.example.ui.screens.MoneyManagerScreen
import com.example.ui.screens.MoneyTab
import com.example.ui.screens.ScamShieldScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SosScreen
import com.example.ui.screens.ToolsScreen
import com.example.ui.screens.TrustedContactsScreen
import com.example.ui.screens.signOutUser
import com.example.ui.theme.FinSmartTheme
import com.example.ui.theme.LocalFinSmartColors
import com.example.ui.viewmodel.FinSmartMainViewModel
import com.example.ui.viewmodel.UiState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.serialization.Serializable

@Serializable data object HomeRoute
@Serializable data class LearnRoute(val moduleName: String? = null)
@Serializable data object AiHelpRoute
@Serializable data object ToolsRoute
@Serializable data object SosRoute
@Serializable data object ScamShieldRoute
@Serializable data object BeforeYouPayRoute
@Serializable data class MoneyManagerRoute(val tabName: String = MoneyTab.OVERVIEW.name)
@Serializable data object FraudSimRoute
@Serializable data object GovSchemesRoute
@Serializable data object AwarenessVideosRoute
@Serializable data object TrustedContactsRoute
@Serializable data object SettingsRoute

private data class TopNavItem(
    val route: Any,
    val label: String,
    val icon: ImageVector,
    val tag: String,
    val isSos: Boolean = false
)

fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { auth -> trySend(auth.currentUser) }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}

@Composable
fun FinSmartApp() {
    val context = LocalContext.current
    val app = context.applicationContext as Application
    val prefsManager = remember(app) { PreferencesManager(app) }
    val globalSettings by prefsManager.settingsFlow.collectAsStateWithLifecycle(initialValue = UserSettingsState())

    val currentUser by remember { Firebase.auth.authStateFlow() }
        .collectAsStateWithLifecycle(initialValue = Firebase.auth.currentUser)

    FinSmartTheme(
        themeMode = globalSettings.themeMode,
        textScale = globalSettings.textScale,
        highContrast = globalSettings.highContrast
    ) {
        val activeUser = currentUser
        if (activeUser == null) {
            AuthScreen(onAuthSuccess = { /* authStateFlow automatically transitions to main root */ })
        } else {
            key(activeUser.uid) {
                val vm: FinSmartMainViewModel = viewModel(
                    factory = viewModelFactory {
                        initializer {
                            FinSmartMainViewModel(
                                firestoreRepo = FinSmartFirestoreRepository(app),
                                aiRepo = AiSahayakRepository(),
                                preferencesManager = prefsManager,
                                currentUserId = activeUser.uid,
                                initialDisplayName = activeUser.displayName ?: activeUser.email?.substringBefore("@") ?: "FinSmart User"
                            )
                        }
                    }
                )
                FinSmartAuthenticatedRoot(
                    user = activeUser,
                    viewModel = vm
                )
            }
        }
    }
}

@Composable
private fun FinSmartAuthenticatedRoot(
    user: FirebaseUser,
    viewModel: FinSmartMainViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val colors = LocalFinSmartColors.current
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val profile = (profileState as? UiState.Success)?.data
    val summary by viewModel.monthlySummary.collectAsStateWithLifecycle()
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    val budgets by viewModel.budgets.collectAsStateWithLifecycle()
    val goals by viewModel.goals.collectAsStateWithLifecycle()
    val scanHistory by viewModel.scanHistory.collectAsStateWithLifecycle()
    val trustedContacts by viewModel.trustedContacts.collectAsStateWithLifecycle()
    val incidentReports by viewModel.incidentReports.collectAsStateWithLifecycle()
    val quizResults by viewModel.quizResults.collectAsStateWithLifecycle()

    val latestLinkResult by viewModel.latestLinkResult.collectAsStateWithLifecycle()
    val latestMessageResult by viewModel.latestMessageResult.collectAsStateWithLifecycle()
    val aiMessages by viewModel.aiMessages.collectAsStateWithLifecycle()
    val streamingAiText by viewModel.streamingAiText.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    val aiError by viewModel.aiError.collectAsStateWithLifecycle()
    val statusBanner by viewModel.statusBanner.collectAsStateWithLifecycle()

    val isOnline by remember(context) { NetworkConnectivityObserver.observe(context) }
        .collectAsStateWithLifecycle(initialValue = true)

    val ttsManager = remember(context) { FinSmartTtsManager(context) }
    val isSpeaking by ttsManager.isSpeaking.collectAsStateWithLifecycle()
    val ttsStatus by ttsManager.statusMessage.collectAsStateWithLifecycle()

    DisposableEffect(ttsManager) {
        onDispose { ttsManager.shutdown() }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(statusBanner) {
        val msg = statusBanner
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearStatusBanner()
        }
    }
    LaunchedEffect(ttsStatus) {
        val msg = ttsStatus
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            ttsManager.clearStatus()
        }
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDest = navBackStackEntry?.destination
    val strings = FinSmartStrings.get(settings.language)

    val topNavItems = remember(strings) {
        listOf(
            TopNavItem(HomeRoute, strings.navHome, Icons.Default.Home, "nav_home"),
            TopNavItem(LearnRoute(null), strings.navLearn, Icons.Default.MenuBook, "nav_learn"),
            TopNavItem(AiHelpRoute, strings.navAiHelp, Icons.Default.SmartToy, "nav_ai_help"),
            TopNavItem(ToolsRoute, strings.navTools, Icons.Default.Build, "nav_tools"),
            TopNavItem(SosRoute, strings.navSos, Icons.Default.NotificationsActive, "nav_sos", isSos = true)
        )
    }

    fun navigateTopTab(route: Any) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun isTopTabSelected(item: TopNavItem): Boolean {
        val dest = currentDest ?: return false
        return when (item.route) {
            is HomeRoute -> dest.hasRoute<HomeRoute>()
            is LearnRoute -> dest.hasRoute<LearnRoute>()
            is AiHelpRoute -> dest.hasRoute<AiHelpRoute>()
            is ToolsRoute -> dest.hasRoute<ToolsRoute>() ||
                dest.hasRoute<ScamShieldRoute>() ||
                dest.hasRoute<BeforeYouPayRoute>() ||
                dest.hasRoute<MoneyManagerRoute>() ||
                dest.hasRoute<FraudSimRoute>() ||
                dest.hasRoute<GovSchemesRoute>() ||
                dest.hasRoute<AwarenessVideosRoute>() ||
                dest.hasRoute<TrustedContactsRoute>() ||
                dest.hasRoute<SettingsRoute>()
            is SosRoute -> dest.hasRoute<SosRoute>()
            else -> false
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useRail = maxWidth >= 600.dp

        Scaffold(
            containerColor = colors.background,
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                if (!useRail) {
                    NavigationBar(containerColor = colors.surface) {
                        topNavItems.forEach { item ->
                            val selected = isTopTabSelected(item)
                            val activeTint = if (item.isSos) colors.danger else colors.primary
                            NavigationBarItem(
                                selected = selected,
                                onClick = { navigateTopTab(item.route) },
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label
                                    )
                                },
                                label = { Text(item.label, maxLines = 1) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = activeTint,
                                    selectedTextColor = activeTint,
                                    indicatorColor = activeTint.copy(alpha = 0.14f),
                                    unselectedIconColor = if (item.isSos) colors.danger.copy(alpha = 0.75f) else colors.textSecondary,
                                    unselectedTextColor = if (item.isSos) colors.danger.copy(alpha = 0.75f) else colors.textSecondary
                                ),
                                modifier = Modifier.testTag(item.tag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useRail) {
                    NavigationRail(containerColor = colors.surface) {
                        topNavItems.forEach { item ->
                            val selected = isTopTabSelected(item)
                            NavigationRailItem(
                                selected = selected,
                                onClick = { navigateTopTab(item.route) },
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label) },
                                modifier = Modifier.testTag(item.tag)
                            )
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = HomeRoute,
                    modifier = Modifier.weight(1f)
                ) {
                    composable<HomeRoute> {
                        HomeScreen(
                            userName = profile?.displayName ?: user.displayName ?: "Friend",
                            profile = profile,
                            settings = settings,
                            summary = summary,
                            isOnline = isOnline,
                            isSpeaking = isSpeaking,
                            onReadTip = { tip -> ttsManager.speak(tip, settings.language, settings.voiceSpeed) },
                            onStopTts = { ttsManager.stop() },
                            onOpenScamShield = { navController.navigate(ScamShieldRoute) },
                            onOpenBeforeYouPay = { navController.navigate(BeforeYouPayRoute) },
                            onOpenAiSahayak = { navigateTopTab(AiHelpRoute) },
                            onOpenMoneyManager = { navController.navigate(MoneyManagerRoute(MoneyTab.EXPENSES.name)) },
                            onOpenSavingsGoals = { navController.navigate(MoneyManagerRoute(MoneyTab.GOALS.name)) },
                            onOpenSos = { navigateTopTab(SosRoute) },
                            onOpenLearnModule = { mod -> navController.navigate(LearnRoute(mod.name)) },
                            onOpenSettings = { navController.navigate(SettingsRoute) }
                        )
                    }

                    composable<LearnRoute> { backStackEntry ->
                        val args = backStackEntry.toRoute<LearnRoute>()
                        val initialModule = LearningModuleId.entries.firstOrNull { it.name == args.moduleName }
                        LearnScreen(
                            profile = profile,
                            quizResults = quizResults,
                            settings = settings,
                            initialModule = initialModule,
                            isSpeaking = isSpeaking,
                            onReadText = { txt -> ttsManager.speak(txt, settings.language, settings.voiceSpeed) },
                            onStopTts = { ttsManager.stop() },
                            onCompleteLesson = { lessonId -> viewModel.completeLesson(lessonId) },
                            onSubmitQuiz = { qId, qTitle, score, total -> viewModel.submitQuizScore(qId, qTitle, score, total) },
                            onToggleOfflineModule = { modId -> viewModel.toggleOfflineModule(modId) },
                            onOpenFraudSimulator = { navController.navigate(FraudSimRoute) }
                        )
                    }

                    composable<AiHelpRoute> {
                        AiSahayakScreen(
                            messages = aiMessages,
                            streamingText = streamingAiText,
                            isLoading = isAiLoading,
                            errorMessage = aiError,
                            settings = settings,
                            isSpeaking = isSpeaking,
                            onSelectLanguage = { lang -> viewModel.setLanguage(lang) },
                            onSendMessage = { question, detailed -> viewModel.sendAiMessage(question, detailed) },
                            onSpeakText = { txt -> ttsManager.speak(txt, settings.language, settings.voiceSpeed) },
                            onStopSpeaking = { ttsManager.stop() },
                            onDismissError = { viewModel.clearAiError() }
                        )
                    }

                    composable<ToolsRoute> {
                        ToolsScreen(
                            onOpenScamShield = { navController.navigate(ScamShieldRoute) },
                            onOpenBeforeYouPay = { navController.navigate(BeforeYouPayRoute) },
                            onOpenMoneyManager = { navController.navigate(MoneyManagerRoute(MoneyTab.OVERVIEW.name)) },
                            onOpenSavingsGoals = { navController.navigate(MoneyManagerRoute(MoneyTab.GOALS.name)) },
                            onOpenCalculators = { navController.navigate(MoneyManagerRoute(MoneyTab.CALCULATORS.name)) },
                            onOpenFraudSimulator = { navController.navigate(FraudSimRoute) },
                            onOpenGovSchemes = { navController.navigate(GovSchemesRoute) },
                            onOpenAwarenessVideos = { navController.navigate(AwarenessVideosRoute) },
                            onOpenTrustedContacts = { navController.navigate(TrustedContactsRoute) },
                            onOpenSettings = { navController.navigate(SettingsRoute) }
                        )
                    }

                    composable<SosRoute> {
                        SosScreen(
                            settings = settings,
                            incidentReports = incidentReports,
                            onSaveIncidentReport = { date, time, amt, refId, method, desc, evidence ->
                                viewModel.saveIncidentReport(date, time, amt, refId, method, desc, evidence)
                            }
                        )
                    }

                    composable<ScamShieldRoute> {
                        ScamShieldScreen(
                            latestLinkResult = latestLinkResult,
                            latestMessageResult = latestMessageResult,
                            scanHistory = scanHistory,
                            trustedContacts = trustedContacts,
                            onCheckUrl = { url -> viewModel.checkUrlSafety(url) },
                            onCheckMessage = { msg, type -> viewModel.checkMessageSafety(msg, type) },
                            onCheckCall = { num, claim -> viewModel.checkCallSafety(num, claim) },
                            onReportLatestScan = { viewModel.markLatestScanReported() },
                            onSharedWithTrustedContact = { contactId -> viewModel.recordSharedAlertToTrustedContact(contactId) },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable<BeforeYouPayRoute> {
                        BeforeYouPayScreen(
                            trustedContacts = trustedContacts,
                            onAskAi = { navigateTopTab(AiHelpRoute) },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable<MoneyManagerRoute> { backStackEntry ->
                        val args = backStackEntry.toRoute<MoneyManagerRoute>()
                        val initialTab = MoneyTab.entries.firstOrNull { it.name == args.tabName } ?: MoneyTab.OVERVIEW
                        MoneyManagerScreen(
                            initialTab = initialTab,
                            summary = summary,
                            expenses = expenses,
                            budgets = budgets,
                            goals = goals,
                            onShiftMonth = { delta -> viewModel.shiftSelectedMonth(delta) },
                            onSaveBudget = { budget -> viewModel.saveBudget(budget) },
                            onSaveExpense = { id, title, cat, amt, dateMillis, note ->
                                viewModel.saveExpense(id, title, cat, amt, dateMillis, note)
                            },
                            onDeleteExpense = { id -> viewModel.deleteExpense(id) },
                            onSaveGoal = { id, title, cat, target, saved, targetDate ->
                                viewModel.saveGoal(id, title, cat, target, saved, targetDate)
                            },
                            onDeleteGoal = { id -> viewModel.deleteGoal(id) },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable<FraudSimRoute> {
                        FraudSimulatorScreen(onBack = { navController.popBackStack() })
                    }

                    composable<GovSchemesRoute> {
                        GovSchemesScreen(onBack = { navController.popBackStack() })
                    }

                    composable<AwarenessVideosRoute> {
                        AwarenessVideosScreen(
                            isSpeaking = isSpeaking,
                            onPlayAudioGuide = { script -> ttsManager.speak(script, settings.language, settings.voiceSpeed) },
                            onStopAudio = { ttsManager.stop() },
                            onBack = {
                                ttsManager.stop()
                                navController.popBackStack()
                            }
                        )
                    }

                    composable<TrustedContactsRoute> {
                        TrustedContactsScreen(
                            contacts = trustedContacts,
                            onSaveContact = { id, name, rel, phone, consent ->
                                viewModel.saveTrustedContact(id, name, rel, phone, consent)
                            },
                            onToggleRevoke = { id, revoked ->
                                viewModel.toggleTrustedContactRevoke(id, revoked)
                            },
                            onDeleteContact = { id -> viewModel.deleteTrustedContact(id) },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable<SettingsRoute> {
                        SettingsScreen(
                            userEmail = user.email.orEmpty(),
                            settings = settings,
                            onSetSimpleMode = { viewModel.setSimpleMode(it) },
                            onSetLanguage = { viewModel.setLanguage(it) },
                            onSetThemeMode = { viewModel.setThemeMode(it) },
                            onSetTextScale = { viewModel.setTextScale(it) },
                            onSetHighContrast = { viewModel.setHighContrast(it) },
                            onSetVoiceSpeed = { viewModel.setVoiceSpeed(it) },
                            onTestVoice = {
                                ttsManager.speak(
                                    "FinSmart voice speed test. Never share your OTP or UPI PIN with anyone.",
                                    settings.language,
                                    settings.voiceSpeed
                                )
                            },
                            onSetNotifications = { viewModel.setNotificationsEnabled(it) },
                            onToggleOfflineModule = { viewModel.toggleOfflineModule(it) },
                            onOpenTrustedContacts = { navController.navigate(TrustedContactsRoute) },
                            onSignOut = {
                                ttsManager.stop()
                                signOutUser(context, scope)
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
