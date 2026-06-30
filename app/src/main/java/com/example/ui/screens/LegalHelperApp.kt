@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DocumentAnalysis
import com.example.data.model.GeminiDocResponse
import com.example.data.model.GeminiQueryResponse
import com.example.data.model.Lawyer
import com.example.data.model.LawyerBooking
import com.example.data.model.LegalQuery
import com.example.data.model.MockData
import com.example.ui.theme.AccentSaffron
import com.example.ui.theme.ActionBlue
import com.example.ui.theme.LawGreen
import com.example.ui.theme.LawRed
import com.example.ui.theme.LightGreen
import com.example.ui.theme.LightRed
import com.example.ui.theme.LightYellow
import com.example.ui.theme.PaperBorder
import com.example.ui.theme.PaperDeskBg
import com.example.ui.theme.PaperWhite
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextGray
import com.example.ui.theme.TrustNavy
import com.example.ui.viewmodel.DocUiState
import com.example.ui.viewmodel.LegalHelperViewModel
import com.example.ui.viewmodel.QueryUiState
import com.example.ui.viewmodel.fromJsonArray
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Screen navigation states
enum class AppScreen {
    HOME,
    ASK_QUESTION,
    ANALYZE_DOC,
    FIND_LAWYER,
    HISTORY,
    IPC_BNS_CONVERTER,
    COURT_PROCESS_STEPPER,
    LAWYER_PROFILE,
    BOOKING_FLOW,
    PAYMENT_FLOW,
    CONFIRMATION
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalHelperApp(viewModel: LegalHelperViewModel) {
    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
    var previousScreen by remember { mutableStateOf(AppScreen.HOME) }
    
    // Selection state for detail navigation
    var selectedLawyer by remember { mutableStateOf<Lawyer?>(null) }
    var activeBookingDetails by remember { mutableStateOf<BookingDetailsState?>(null) }
    var showPaywall by remember { mutableStateOf(false) }

    // Observers
    val isPro by viewModel.isPro.collectAsState()
    val remainingFreeQueries by viewModel.remainingFreeQueries.collectAsState()
    val remainingFreeDocs by viewModel.remainingFreeDocs.collectAsState()

    val savedQueries by viewModel.savedQueries.collectAsState()
    val savedAnalyses by viewModel.savedAnalyses.collectAsState()
    val savedBookings by viewModel.savedBookings.collectAsState()

    val queryUiState by viewModel.queryUiState.collectAsState()
    val docUiState by viewModel.docUiState.collectAsState()

    val viewingQuery by viewModel.viewingQuery.collectAsState()
    val viewingDoc by viewModel.viewingDoc.collectAsState()

    // Helper navigate functions to handle back stack simply
    fun navigateTo(screen: AppScreen) {
        previousScreen = currentScreen
        currentScreen = screen
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        // Unique modern Hindi "न" logo container
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(TrustNavy, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "न",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "न्याय साथी",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark,
                                letterSpacing = (-0.3).sp
                            )
                            Text(
                                text = "AI Legal Helper",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextGray,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                },
                actions = {
                    if (isPro) {
                        Surface(
                            color = AccentSaffron,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = "PRO MEMBER",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    } else {
                        Button(
                            onClick = { showPaywall = true },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentSaffron),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .padding(end = 8.dp)
                                .testTag("upgrade_button")
                        ) {
                            Text("Go Pro", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                navigationIcon = {
                    if (currentScreen != AppScreen.HOME) {
                        IconButton(onClick = {
                            // Back navigation logic
                            when (currentScreen) {
                                AppScreen.LAWYER_PROFILE -> navigateTo(AppScreen.FIND_LAWYER)
                                AppScreen.BOOKING_FLOW -> navigateTo(AppScreen.LAWYER_PROFILE)
                                AppScreen.PAYMENT_FLOW -> navigateTo(AppScreen.BOOKING_FLOW)
                                AppScreen.CONFIRMATION -> navigateTo(AppScreen.HOME)
                                AppScreen.IPC_BNS_CONVERTER -> navigateTo(AppScreen.HOME)
                                AppScreen.COURT_PROCESS_STEPPER -> navigateTo(AppScreen.HOME)
                                else -> {
                                    navigateTo(AppScreen.HOME)
                                    viewModel.resetQueryState()
                                    viewModel.resetDocState()
                                    viewModel.setViewingQuery(null)
                                    viewModel.setViewingDoc(null)
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextDark
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PaperDeskBg,
                    titleContentColor = TextDark
                )
            )
        },
        bottomBar = {
            // Standard Navigation Bar
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.border(1.dp, PaperBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                NavigationBarItem(
                    selected = currentScreen == AppScreen.HOME || currentScreen == AppScreen.IPC_BNS_CONVERTER || currentScreen == AppScreen.COURT_PROCESS_STEPPER,
                    onClick = { navigateTo(AppScreen.HOME) },
                    icon = { Icon(imageVector = if (currentScreen == AppScreen.HOME) Icons.Filled.Home else Icons.Outlined.Home, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = currentScreen == AppScreen.ASK_QUESTION,
                    onClick = {
                        viewModel.resetQueryState()
                        navigateTo(AppScreen.ASK_QUESTION)
                    },
                    icon = { Icon(imageVector = Icons.Filled.Layers, contentDescription = "Ask AI") },
                    label = { Text("Ask AI", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = currentScreen == AppScreen.ANALYZE_DOC,
                    onClick = {
                        viewModel.resetDocState()
                        navigateTo(AppScreen.ANALYZE_DOC)
                    },
                    icon = { Icon(imageVector = Icons.Filled.DocumentScanner, contentDescription = "Scan") },
                    label = { Text("Scan Doc", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = currentScreen == AppScreen.FIND_LAWYER || currentScreen == AppScreen.LAWYER_PROFILE || currentScreen == AppScreen.BOOKING_FLOW || currentScreen == AppScreen.PAYMENT_FLOW || currentScreen == AppScreen.CONFIRMATION,
                    onClick = { navigateTo(AppScreen.FIND_LAWYER) },
                    icon = { Icon(imageVector = if (currentScreen == AppScreen.FIND_LAWYER) Icons.Filled.Gavel else Icons.Outlined.Gavel, contentDescription = "Lawyers") },
                    label = { Text("Lawyers", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = currentScreen == AppScreen.HISTORY,
                    onClick = { navigateTo(AppScreen.HISTORY) },
                    icon = { Icon(imageVector = if (currentScreen == AppScreen.HISTORY) Icons.Filled.History else Icons.Outlined.History, contentDescription = "History") },
                    label = { Text("History", fontSize = 11.sp) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (currentScreen) {
                AppScreen.HOME -> HomeScreen(
                    isPro = isPro,
                    freeQueries = remainingFreeQueries,
                    freeDocs = remainingFreeDocs,
                    onNavigate = { navigateTo(it) },
                    onShowPaywall = { showPaywall = true }
                )
                AppScreen.ASK_QUESTION -> AskQuestionScreen(
                    viewModel = viewModel,
                    uiState = queryUiState,
                    viewingQuery = viewingQuery,
                    onConsultLawyer = { specialty ->
                        navigateTo(AppScreen.FIND_LAWYER)
                    }
                )
                AppScreen.ANALYZE_DOC -> AnalyzeDocScreen(
                    viewModel = viewModel,
                    uiState = docUiState,
                    viewingDoc = viewingDoc,
                    onConsultLawyer = {
                        navigateTo(AppScreen.FIND_LAWYER)
                    }
                )
                AppScreen.FIND_LAWYER -> FindLawyerScreen(
                    onLawyerSelected = {
                        selectedLawyer = it
                        navigateTo(AppScreen.LAWYER_PROFILE)
                    }
                )
                AppScreen.LAWYER_PROFILE -> LawyerProfileScreen(
                    lawyer = selectedLawyer,
                    onBookConsult = {
                        activeBookingDetails = BookingDetailsState(lawyer = it)
                        navigateTo(AppScreen.BOOKING_FLOW)
                    }
                )
                AppScreen.BOOKING_FLOW -> BookingFlowScreen(
                    bookingDetailsState = activeBookingDetails,
                    savedQueries = savedQueries,
                    savedAnalyses = savedAnalyses,
                    onSlotSelected = { date, time, mode, summary ->
                        activeBookingDetails = activeBookingDetails?.copy(
                            date = date,
                            time = time,
                            mode = mode,
                            caseSummary = summary
                        )
                        navigateTo(AppScreen.PAYMENT_FLOW)
                    }
                )
                AppScreen.PAYMENT_FLOW -> PaymentFlowScreen(
                    bookingDetailsState = activeBookingDetails,
                    onPaymentSuccess = {
                        val details = activeBookingDetails
                        if (details != null) {
                            viewModel.bookLawyerConsultation(
                                lawyer = details.lawyer,
                                date = details.date,
                                time = details.time,
                                mode = details.mode,
                                caseSummaryText = details.caseSummary
                            )
                        }
                        navigateTo(AppScreen.CONFIRMATION)
                    }
                )
                AppScreen.CONFIRMATION -> BookingConfirmationScreen(
                    bookingDetailsState = activeBookingDetails,
                    onGoHome = { navigateTo(AppScreen.HOME) },
                    onGoHistory = { navigateTo(AppScreen.HISTORY) }
                )
                AppScreen.HISTORY -> HistoryScreen(
                    viewModel = viewModel,
                    savedQueries = savedQueries,
                    savedAnalyses = savedAnalyses,
                    savedBookings = savedBookings,
                    onQueryClick = {
                        viewModel.setViewingQuery(it)
                        navigateTo(AppScreen.ASK_QUESTION)
                    },
                    onAnalysisClick = {
                        viewModel.setViewingDoc(it)
                        navigateTo(AppScreen.ANALYZE_DOC)
                    }
                )
                AppScreen.IPC_BNS_CONVERTER -> IpcBnsConverterScreen()
                AppScreen.COURT_PROCESS_STEPPER -> CourtProcessJourneyScreen()
            }

            // Paywall Dialog Sheet
            if (showPaywall) {
                PaywallOverlay(
                    onDismiss = { showPaywall = false },
                    onUpgradeSuccess = {
                        viewModel.upgradeToPro()
                        showPaywall = false
                    }
                )
            }
        }
    }
}

data class BookingDetailsState(
    val lawyer: Lawyer,
    val date: String = "",
    val time: String = "",
    val mode: String = "Video Consult",
    val caseSummary: String = ""
)

// ==========================================
// 1. HOME SCREEN
// ==========================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    isPro: Boolean,
    freeQueries: Int,
    freeDocs: Int,
    onNavigate: (AppScreen) -> Unit,
    onShowPaywall: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // --- Welcome Section (Clean Minimalism Theme) ---
        Column(modifier = Modifier.padding(bottom = 20.dp)) {
            Text(
                text = "नमस्ते, रिया",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "समझो अपना केस, सिर्फ 2 मिनट में",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextGray
            )
        }

        // Limit Indicator if Free (Minimal styled alert banner)
        if (!isPro) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .clickable { onShowPaywall() },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEFBF0)),
                border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFFEF3C7), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Free Account Limits Active",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Text(
                            text = "Remaining: $freeQueries AI Queries • $freeDocs Document Scans. Tap to Upgrade.",
                            fontSize = 11.sp,
                            color = TextGray
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.NavigateNext,
                        contentDescription = null,
                        tint = TextGray.copy(alpha = 0.6f)
                    )
                }
            }
        }

        // --- Core Action Cards ---
        Text(
            text = "CHOOSE A SERVICE / सेवा चुनें",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextGray,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        CoreFeatureCard(
            titleEng = "Ask AI Legal Assistant",
            titleHindi = "सवाल पूछें",
            descEng = "Explain an FIR section or describe a dispute to receive plain-language guidance, bailable status, and process details.",
            descHindi = "FIR या किसी विवाद के बारे में आसान हिंदी और इंग्लिश में सलाह पाएं।",
            icon = Icons.Default.Layers,
            testTag = "ask_ai_card",
            onClick = { onNavigate(AppScreen.ASK_QUESTION) }
        )

        CoreFeatureCard(
            titleEng = "Analyze Legal Document",
            titleHindi = "दस्तावेज़ जाँचें",
            descEng = "Scan an agreement, rent deed, or police notice. Our AI will perform OCR, detect risk clauses, summaries, and acts.",
            descHindi = "नोटिस या एग्रीमेंट स्कैन करें और छुपे हुए रिस्क और कानूनी सारांश देखें।",
            icon = Icons.Default.DocumentScanner,
            testTag = "analyze_doc_card",
            onClick = { onNavigate(AppScreen.ANALYZE_DOC) }
        )

        CoreFeatureCard(
            titleEng = "Find Vetted Lawyers",
            titleHindi = "वकील खोजें",
            descEng = "Browse and filter practicing lawyers across India by location, price, and specialty. Book phone, chat, or office consultations.",
            descHindi = "अपने पास के सत्यापित वकीलों से जुड़ें और अपॉइंटमेंट बुक करें।",
            icon = Icons.Default.Gavel,
            testTag = "find_lawyer_card",
            onClick = { onNavigate(AppScreen.FIND_LAWYER) }
        )

        CoreFeatureCard(
            titleEng = "My Cases & History",
            titleHindi = "मेरे मामले",
            descEng = "Review all your saved AI chats, document scanning summaries, and confirmed lawyer appointments in one secure locker.",
            descHindi = "अपने पुराने केस, डॉक्युमेंट्स और वकील बुकिंग को देखें।",
            icon = Icons.Default.History,
            testTag = "case_history_card",
            onClick = { onNavigate(AppScreen.HISTORY) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // --- Quick Tools & Highlights (BNS Code Converter and Court Process) ---
        Text(
            text = "HIGHLIGHTED FEATURES / विशेष साधन",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextGray,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Feature Highlight: IPC-BNS (Styled exactly like the modern royal blue banner in HTML)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp)
                .clickable { onNavigate(AppScreen.IPC_BNS_CONVERTER) },
            colors = CardDefaults.cardColors(containerColor = TrustNavy),
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Text(
                            text = "NEW",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "IPC ↔ BNS Converter",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "धारा 420 अब क्या है? यहाँ देखें।",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔄",
                        fontSize = 20.sp
                    )
                }
            }
        }

        // Court Process Journey Highlight (White minimal styled card)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
                .clickable { onNavigate(AppScreen.COURT_PROCESS_STEPPER) },
            colors = CardDefaults.cardColors(containerColor = PaperWhite),
            border = BorderStroke(1.dp, PaperBorder),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFFECFDF5), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚖️",
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Court Process Journey Guide",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = "कोर्ट प्रक्रिया गाइड — Step-by-step guidance",
                        fontSize = 11.sp,
                        color = TextGray
                    )
                }
                Icon(
                    imageVector = Icons.Default.NavigateNext,
                    contentDescription = null,
                    tint = TextGray.copy(alpha = 0.4f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Minimal Disclaimer at bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "GENERAL LEGAL INFORMATION ONLY. NOT LEGAL ADVICE.",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = TextGray.copy(alpha = 0.6f),
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CoreFeatureCard(
    titleEng: String,
    titleHindi: String,
    descEng: String,
    descHindi: String,
    icon: ImageVector,
    testTag: String,
    onClick: () -> Unit
) {
    // Determine custom colors based on the card's function
    val (iconBgColor, iconColor) = when (testTag) {
        "ask_ai_card" -> Color(0xFFEEF2F6) to Color(0xFF6366F1)       // Indigo accents
        "analyze_doc_card" -> Color(0xFFFFFBEB) to Color(0xFFD97706)   // Amber accents
        "find_lawyer_card" -> Color(0xFFECFDF5) to Color(0xFF10B981)   // Emerald accents
        else -> Color(0xFFF1F5F9) to Color(0xFF64748B)                 // Slate gray accents
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp)
            .clickable { onClick() }
            .testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = PaperWhite),
        border = BorderStroke(1.dp, PaperBorder),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(iconBgColor, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = titleHindi,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• $titleEng",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = iconColor
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = descEng,
                    fontSize = 12.sp,
                    color = TextGray,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = descHindi,
                    fontSize = 11.sp,
                    color = TextGray.copy(alpha = 0.85f),
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.Normal
                )
            }
            Icon(
                imageVector = Icons.Default.NavigateNext,
                contentDescription = null,
                tint = TextGray.copy(alpha = 0.4f),
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

// ==========================================
// 2. ASK LEGAL QUESTION SCREEN (AI ASSISTANT)
// ==========================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AskQuestionScreen(
    viewModel: LegalHelperViewModel,
    uiState: QueryUiState,
    viewingQuery: LegalQuery?,
    onConsultLawyer: (String) -> Unit
) {
    var queryInput by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Suggested Chips list
    val suggestionChips = listOf(
        "मेरे खिलाफ धारा 354 में FIR हुई है, क्या यह जमानती है?" to "My friend outrage modesty IPC 354 BNS 74",
        "चेक बाउंस नोटिस मिला है, क्या करना चाहिए?" to "Received a Cheque Bounce notice under Section 138 of NI Act. What are my next steps?",
        "फ्लैट खाली करने का मकान मालिक नोटिस" to "Landlord sent eviction notice on a rented flat in Mumbai",
        "दुकानदार ने खराब सामान दिया और पैसा वापस नहीं कर रहा" to "Consumer dispute with shopkeeper who sold defective electronics and refused refund"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            BilingualText(
                english = "AI Legal Advisor / एआई सलाह",
                hindi = "वर्णन करें अपनी समस्या का आसान समाधान पाने के लिए",
                englishSize = 18
            )
        }

        // Active input box if not viewing historical queries
        if (viewingQuery == null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = PaperWhite),
                    border = BorderStroke(1.dp, PaperBorder),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Describe your situation (Hindi, English, or mixed):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = queryInput,
                            onValueChange = { queryInput = it },
                            placeholder = { Text("E.g., मेरे खिलाफ पुलिस में चोरी की धारा 379 / BNS 303 के तहत FIR दर्ज हुई है। अब क्या प्रक्रिया होगी?", fontSize = 12.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .testTag("legal_query_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ActionBlue,
                                unfocusedBorderColor = PaperBorder
                            ),
                            shape = RoundedCornerShape(4.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Suggestion Chips
                        Text("TRY THESE EXAMPLES / उदाहरण देखें:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextGray)
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            suggestionChips.forEach { (display, englishText) ->
                                Surface(
                                    modifier = Modifier
                                        .clickable { queryInput = englishText }
                                        .testTag("example_chip_${display.take(5)}"),
                                    color = ActionBlue.copy(alpha = 0.05f),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, ActionBlue.copy(alpha = 0.15f))
                                ) {
                                    Text(
                                        text = display,
                                        fontSize = 10.sp,
                                        color = ActionBlue,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                viewModel.askLegalQuestion(queryInput)
                            },
                            enabled = queryInput.trim().isNotEmpty() && uiState != QueryUiState.Loading,
                            colors = ButtonDefaults.buttonColors(containerColor = ActionBlue),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ask_ai_submit_button")
                        ) {
                            if (uiState == QueryUiState.Loading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Analyze Case / केस स्पष्ट करें", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Handle States
        when {
            uiState is QueryUiState.Loading -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = PaperWhite),
                        border = BorderStroke(1.dp, PaperBorder),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = ActionBlue, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("AI is mapping IPC ↔ BNS equivalents...", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            Text("Assessing bailable status & legal definitions", fontSize = 11.sp, color = TextGray)
                        }
                    }
                }
            }
            uiState is QueryUiState.Error -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = LightRed),
                        border = BorderStroke(1.dp, LawRed.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = "Error", tint = LawRed)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = uiState.message, fontSize = 12.sp, color = LawRed, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            uiState is QueryUiState.Success -> {
                item {
                    QueryResultDisplay(
                        queryText = queryInput,
                        data = uiState.data,
                        onReset = { viewModel.resetQueryState() },
                        onConsultLawyer = onConsultLawyer
                    )
                }
            }
            viewingQuery != null -> {
                // Showing query loaded from history
                item {
                    QueryResultDisplay(
                        queryText = viewingQuery.queryText,
                        data = GeminiQueryResponse(
                            sections = viewingQuery.sections,
                            isBailable = viewingQuery.isBailable,
                            isCognizable = viewingQuery.isCognizable,
                            explanationHindi = viewingQuery.explanationHindi,
                            explanationEnglish = viewingQuery.explanationEnglish,
                            punishment = viewingQuery.punishment,
                            timeline = viewingQuery.timelineJson.fromJsonArray(),
                            checklist = viewingQuery.checklistJson.fromJsonArray()
                        ),
                        isHistory = true,
                        onReset = null,
                        onConsultLawyer = onConsultLawyer
                    )
                }
            }
        }
    }
}

@Composable
fun QueryResultDisplay(
    queryText: String,
    data: GeminiQueryResponse,
    isHistory: Boolean = false,
    onReset: (() -> Unit)? = null,
    onConsultLawyer: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (!isHistory && onReset != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("AI CASE ANALYSIS", fontSize = 12.sp, fontWeight = FontWeight.Black, color = ActionBlue)
                TextButton(onClick = { onReset() }) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear Result", fontSize = 12.sp)
                }
            }
        }

        // Original Query Summary Box
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Your Case Description / आपकी समस्या:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = queryText, fontSize = 12.sp, color = TextDark)
            }
        }

        // Status Badges & Mapped Sections Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PaperWhite),
            border = BorderStroke(1.dp, PaperBorder),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                BilingualText(
                    english = "Applicable Sections (IPC ↔ BNS)",
                    hindi = "लागू होने वाली धाराएं",
                    englishSize = 13,
                    englishWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = data.sections,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ActionBlue
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatusBadge(
                        labelEnglish = if (data.isBailable) "Bailable / जमानती" else "Non-Bailable / गैर-जमानती",
                        labelHindi = if (data.isBailable) "बेल मिलेगी" else "कोर्ट तय करेगा",
                        isPositive = data.isBailable,
                        modifier = Modifier.weight(1f)
                    )
                    StatusBadge(
                        labelEnglish = if (data.isCognizable) "Cognizable / संज्ञेय" else "Non-Cognizable / असंज्ञेय",
                        labelHindi = if (data.isCognizable) "पुलिस तुरंत गिरफ्तार कर सकती है" else "वारंट जरूरी है",
                        isPositive = !data.isCognizable,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Punishment Warning Alert
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = LightRed),
            border = BorderStroke(1.dp, LawRed.copy(alpha = 0.15f)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = LawRed, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    BilingualText(
                        english = "Likely Punishment Range",
                        hindi = "संभावित सजा",
                        englishSize = 12,
                        englishWeight = FontWeight.Bold,
                        textColor = LawRed
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = data.punishment, fontSize = 12.sp, color = TextDark)
                }
            }
        }

        // Explanations Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PaperWhite),
            border = BorderStroke(1.dp, PaperBorder),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                BilingualText(
                    english = "Simplified Legal Explanation",
                    hindi = "सरल कानूनी स्पष्टीकरण",
                    englishSize = 14,
                    englishWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "English Explanation:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ActionBlue
                )
                Text(
                    text = data.explanationEnglish,
                    fontSize = 12.sp,
                    color = TextDark,
                    lineHeight = 18.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = PaperBorder)
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "हिंदी स्पष्टीकरण:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ActionBlue
                )
                Text(
                    text = data.explanationHindi,
                    fontSize = 12.sp,
                    color = TextDark,
                    lineHeight = 18.sp
                )
            }
        }

        // Visual Stepper Process Timeline
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PaperWhite),
            border = BorderStroke(1.dp, PaperBorder),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                BilingualText(
                    english = "Typical Court Trial Process",
                    hindi = "कोर्ट ट्रायल प्रक्रिया",
                    englishSize = 14,
                    englishWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                data.timeline.forEachIndexed { index, step ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        if (index == 0) ActionBlue else PaperBorder,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (index + 1).toString(),
                                    fontSize = 11.sp,
                                    color = if (index == 0) Color.White else TextGray,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (index < data.timeline.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(30.dp)
                                        .background(PaperBorder)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.padding(top = 2.dp)) {
                            Text(
                                text = step,
                                fontSize = 12.sp,
                                fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Medium,
                                color = if (index == 0) ActionBlue else TextDark
                            )
                            if (index == 0) {
                                Text(
                                    text = "Current / Immediate Stage",
                                    fontSize = 9.sp,
                                    color = LawGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Checklist of Action Items
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PaperWhite),
            border = BorderStroke(1.dp, PaperBorder),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                BilingualText(
                    english = "What to do Now (Checklist)",
                    hindi = "अभी क्या करना चाहिए",
                    englishSize = 14,
                    englishWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                data.checklist.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = LawGreen,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = item, fontSize = 12.sp, color = TextDark, lineHeight = 16.sp)
                    }
                }
            }
        }

        // Disclaimer
        LegalDisclaimerBanner()

        // CTA: Talk to a Lawyer
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = TrustNavy),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Need a Vetted Lawyer for this specific Case?",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "इस मामले के लिए विशेषज्ञ वकील से बात करें",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        // Navigate to lawyer with pre-filter context
                        onConsultLawyer(data.sections)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionBlue),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth().testTag("consult_lawyer_cta")
                ) {
                    Icon(imageVector = Icons.Default.Gavel, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Connect with Criminal Lawyer", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==========================================
// 3. AI DOCUMENT ANALYSIS SCREEN
// ==========================================
@Composable
fun AnalyzeDocScreen(
    viewModel: LegalHelperViewModel,
    uiState: DocUiState,
    viewingDoc: DocumentAnalysis?,
    onConsultLawyer: () -> Unit
) {
    var notesInput by remember { mutableStateOf("") }
    var selectedFileLabel by remember { mutableStateOf<String?>(null) }
    var simulatedProgressState by remember { mutableStateOf("") }
    var progressVal by remember { mutableStateOf(0f) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Preset Mock Files for upload simulation
    val mockDocs = listOf(
        "Delhi_Rent_Agreement_2026.pdf" to "Eviction clause, security deposit refund terms, and landlord-tenant arbitration clauses.",
        "Cheque_Bounce_Notice_Sec138.jpg" to "Official legal notice received for unpaid balance of Rs. 4,50,000 from ICICI bank.",
        "FIR_Copy_Outrage_Modesty.png" to "Police complaint charging cognizable offenses under IPC Section 354 and BNS equivalent."
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            BilingualText(
                english = "AI Document Analyzer / दस्तावेज़ जाँच",
                hindi = "एग्रीमेंट, नोटिस या एफआईआर स्कैन करके महत्वपूर्ण जोखिम समझें",
                englishSize = 18
            )
        }

        if (viewingDoc == null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = PaperWhite),
                    border = BorderStroke(1.dp, PaperBorder),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "1. Scan / Upload Document Source:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = {
                                    // Simulate pick document 1
                                    selectedFileLabel = mockDocs[0].first
                                    notesInput = mockDocs[0].second
                                },
                                shape = RoundedCornerShape(4.dp),
                                border = BorderStroke(1.dp, if (selectedFileLabel == mockDocs[0].first) ActionBlue else PaperBorder),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (selectedFileLabel == mockDocs[0].first) ActionBlue.copy(alpha = 0.05f) else Color.Transparent
                                ),
                                modifier = Modifier.weight(1f).testTag("upload_mock_1")
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(imageVector = Icons.Default.Description, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Rent Deed", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    selectedFileLabel = mockDocs[1].first
                                    notesInput = mockDocs[1].second
                                },
                                shape = RoundedCornerShape(4.dp),
                                border = BorderStroke(1.dp, if (selectedFileLabel == mockDocs[1].first) ActionBlue else PaperBorder),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (selectedFileLabel == mockDocs[1].first) ActionBlue.copy(alpha = 0.05f) else Color.Transparent
                                ),
                                modifier = Modifier.weight(1f).testTag("upload_mock_2")
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Court Notice", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    selectedFileLabel = mockDocs[2].first
                                    notesInput = mockDocs[2].second
                                },
                                shape = RoundedCornerShape(4.dp),
                                border = BorderStroke(1.dp, if (selectedFileLabel == mockDocs[2].first) ActionBlue else PaperBorder),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (selectedFileLabel == mockDocs[2].first) ActionBlue.copy(alpha = 0.05f) else Color.Transparent
                                ),
                                modifier = Modifier.weight(1f).testTag("upload_mock_3")
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(imageVector = Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Police FIR", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        if (selectedFileLabel != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                color = LightGreen,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp).fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = LawGreen, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Selected File: $selectedFileLabel", fontSize = 11.sp, color = LawGreen, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "2. Additional Context or Notes (Optional):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = notesInput,
                            onValueChange = { notesInput = it },
                            placeholder = { Text("E.g., Check if there is any lock-in period or hidden cancellation fee...", fontSize = 12.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ActionBlue,
                                unfocusedBorderColor = PaperBorder
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                scope.launch {
                                    // Mocking OCR stages in visual UI for premium look before starting VM analysis
                                    simulatedProgressState = "Performing OCR Text Extraction..."
                                    progressVal = 0.25f
                                    delay(1000)
                                    simulatedProgressState = "Detecting Devanagari & Latin scripts..."
                                    progressVal = 0.55f
                                    delay(1000)
                                    simulatedProgressState = "AI checking contract liability & clause risks..."
                                    progressVal = 0.85f
                                    delay(800)
                                    
                                    val dummyBitmap = BitmapFactory.decodeResource(context.resources, android.R.drawable.ic_menu_save)
                                    viewModel.analyzeDocument(notesInput, dummyBitmap, selectedFileLabel ?: "Scanned_Notice.jpg")
                                    simulatedProgressState = ""
                                    progressVal = 0f
                                }
                            },
                            enabled = selectedFileLabel != null && uiState != DocUiState.Loading,
                            colors = ButtonDefaults.buttonColors(containerColor = ActionBlue),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("analyze_doc_submit_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.DocumentScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Upload & Analyze Document", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Scanning / OCR state
        if (simulatedProgressState.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = PaperWhite),
                    border = BorderStroke(1.dp, PaperBorder),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = ActionBlue, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = simulatedProgressState, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = progressVal,
                            color = ActionBlue,
                            trackColor = PaperBorder,
                            modifier = Modifier.fillMaxWidth().height(4.dp)
                        )
                    }
                }
            }
        }

        // VM states
        when {
            uiState is DocUiState.Loading -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = PaperWhite),
                        border = BorderStroke(1.dp, PaperBorder),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = ActionBlue, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("AI is compiling structural document insights...", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            Text("Assessing regulatory clauses & action points", fontSize = 11.sp, color = TextGray)
                        }
                    }
                }
            }
            uiState is DocUiState.Error -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = LightRed),
                        border = BorderStroke(1.dp, LawRed.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = "Error", tint = LawRed)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = uiState.message, fontSize = 12.sp, color = LawRed, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            uiState is DocUiState.Success -> {
                item {
                    DocResultDisplay(
                        fileName = selectedFileLabel ?: "Scanned_Document.jpg",
                        data = uiState.data,
                        onReset = { viewModel.resetDocState() },
                        onConsultLawyer = onConsultLawyer
                    )
                }
            }
            viewingDoc != null -> {
                item {
                    DocResultDisplay(
                        fileName = viewingDoc.fileName,
                        data = GeminiDocResponse(
                            docType = viewingDoc.docType,
                            summary = viewingDoc.summaryJson.fromJsonArray(),
                            risks = viewingDoc.risksJson.fromJsonArray(),
                            lawReferences = viewingDoc.lawReferencesJson.fromJsonArray(),
                            suggestedActions = viewingDoc.suggestedActionsJson.fromJsonArray()
                        ),
                        isHistory = true,
                        onReset = null,
                        onConsultLawyer = onConsultLawyer
                    )
                }
            }
        }
    }
}

@Composable
fun DocResultDisplay(
    fileName: String,
    data: GeminiDocResponse,
    isHistory: Boolean = false,
    onReset: (() -> Unit)? = null,
    onConsultLawyer: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (!isHistory && onReset != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("DOCUMENT ANALYSIS", fontSize = 12.sp, fontWeight = FontWeight.Black, color = ActionBlue)
                TextButton(onClick = { onReset() }) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear Result", fontSize = 12.sp)
                }
            }
        }

        // Mapped Document Badge Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PaperWhite),
            border = BorderStroke(1.dp, PaperBorder),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(ActionBlue.copy(alpha = 0.08f), CircleShape)
                        .padding(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = ActionBlue, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = "DETECTED DOCUMENT TYPE:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextGray)
                    Text(text = data.docType, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = TrustNavy)
                    Text(text = "File: $fileName", fontSize = 11.sp, color = TextGray)
                }
            }
        }

        // Summary points
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PaperWhite),
            border = BorderStroke(1.dp, PaperBorder),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                BilingualText(
                    english = "Simplified Summary / दस्तावेज़ सारांश",
                    hindi = "प्रमुख बातों का आसान स्पष्टीकरण",
                    englishSize = 13,
                    englishWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                data.summary.forEach { bullet ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(text = "•", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ActionBlue)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = bullet, fontSize = 12.sp, color = TextDark, lineHeight = 16.sp)
                    }
                }
            }
        }

        // Risks Alert Cards
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = LightRed),
            border = BorderStroke(1.dp, LawRed.copy(alpha = 0.15f)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = LawRed, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    BilingualText(
                        english = "Attention / Risks & Deadlines",
                        hindi = "सावधानियां और महत्वपूर्ण समयसीमा",
                        englishSize = 13,
                        englishWeight = FontWeight.Bold,
                        textColor = LawRed
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                data.risks.forEach { risk ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .size(6.dp)
                                .background(LawRed, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = risk, fontSize = 12.sp, color = TextDark, lineHeight = 16.sp)
                    }
                }
            }
        }

        // Legal Act References
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PaperWhite),
            border = BorderStroke(1.dp, PaperBorder),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                BilingualText(
                    english = "Relevant Law Acts & References",
                    hindi = "संबंधित कानून और धाराएं",
                    englishSize = 13,
                    englishWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                data.lawReferences.forEach { reference ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Gavel, contentDescription = null, tint = ActionBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = reference, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    }
                }
            }
        }

        // Suggested Actions
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PaperWhite),
            border = BorderStroke(1.dp, PaperBorder),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                BilingualText(
                    english = "Suggested Next Action Steps",
                    hindi = "आगे के लिए सुझाव",
                    englishSize = 13,
                    englishWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                data.suggestedActions.forEach { action ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = LawGreen, modifier = Modifier.size(16.dp).padding(top = 2.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = action, fontSize = 12.sp, color = TextDark, lineHeight = 16.sp)
                    }
                }
            }
        }

        // Disclaimer
        LegalDisclaimerBanner()

        // CTA: Send analysis to lawyer
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = TrustNavy),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Book a Lawyer Consultation about this Document?",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "वकील के साथ इस दस्तावेज़ पर चर्चा करें",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { onConsultLawyer() },
                    colors = ButtonDefaults.buttonColors(containerColor = ActionBlue),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth().testTag("consult_lawyer_doc_cta")
                ) {
                    Icon(imageVector = Icons.Default.Gavel, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select lawyer to review this doc", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==========================================
// 4. FIND A LAWYER (MARKETPLACE) SCREEN
// ==========================================
@Composable
fun FindLawyerScreen(onLawyerSelected: (Lawyer) -> Unit) {
    var searchInput by remember { mutableStateOf("") }
    var selectedSpecialtyFilter by remember { mutableStateOf("All Specialties") }
    var selectedCityFilter by remember { mutableStateOf("All Cities") }

    val specialties = listOf("All Specialties", "Criminal Law / BNS / IPC", "Property & Real Estate Disputes", "Consumer Protection & Contracts", "Family & Matrimonial Law")
    val cities = listOf("All Cities", "Delhi NCR", "Mumbai", "Bengaluru", "Chennai", "Chandigarh")

    // Filtered lawyers
    val filteredLawyers = MockData.LAWYERS_LIST.filter { lawyer ->
        val matchesSearch = lawyer.name.contains(searchInput, ignoreCase = true) || lawyer.bio.contains(searchInput, ignoreCase = true)
        val matchesSpecialty = selectedSpecialtyFilter == "All Specialties" || lawyer.specialty == selectedSpecialtyFilter
        val matchesCity = selectedCityFilter == "All Cities" || lawyer.city == selectedCityFilter
        matchesSearch && matchesSpecialty && matchesCity
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            BilingualText(
                english = "Verified Lawyers Directory / वकील खोजें",
                hindi = "सत्यापित भारतीय बार काउंसिल वकीलों की सूची",
                englishSize = 18
            )
        }

        // Search Bar & Filter Controls
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PaperWhite),
                border = BorderStroke(1.dp, PaperBorder),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    OutlinedTextField(
                        value = searchInput,
                        onValueChange = { searchInput = it },
                        placeholder = { Text("Search by name, expertise...", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("lawyer_search_input"),
                        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextGray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ActionBlue,
                            unfocusedBorderColor = PaperBorder
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Text("FILTER BY EXPERTISE / श्रेणी:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        specialties.forEach { filter ->
                            Surface(
                                modifier = Modifier
                                    .clickable { selectedSpecialtyFilter = filter }
                                    .testTag("filter_spec_${filter.take(5)}"),
                                color = if (selectedSpecialtyFilter == filter) ActionBlue else PaperBorder.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, if (selectedSpecialtyFilter == filter) ActionBlue else PaperBorder)
                            ) {
                                Text(
                                    text = filter,
                                    fontSize = 10.sp,
                                    color = if (selectedSpecialtyFilter == filter) Color.White else TextDark,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("FILTER BY CITY / शहर:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        cities.forEach { city ->
                            Surface(
                                modifier = Modifier
                                    .clickable { selectedCityFilter = city }
                                    .testTag("filter_city_${city.take(5)}"),
                                color = if (selectedCityFilter == city) ActionBlue else PaperBorder.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, if (selectedCityFilter == city) ActionBlue else PaperBorder)
                            ) {
                                Text(
                                    text = city,
                                    fontSize = 10.sp,
                                    color = if (selectedCityFilter == city) Color.White else TextDark,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Directory List
        if (filteredLawyers.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = PaperWhite),
                    border = BorderStroke(1.dp, PaperBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = TextGray, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No matching lawyers found in the specified location.", fontSize = 12.sp, color = TextGray, textAlign = TextAlign.Center)
                    }
                }
            }
        } else {
            items(filteredLawyers) { lawyer ->
                LawyerCard(lawyer = lawyer, onClick = { onLawyerSelected(lawyer) })
            }
        }
    }
}

@Composable
fun LawyerCard(lawyer: Lawyer, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("lawyer_card_${lawyer.id}"),
        colors = CardDefaults.cardColors(containerColor = PaperWhite),
        border = BorderStroke(1.dp, PaperBorder),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(modifier = Modifier.padding(18.dp), verticalAlignment = Alignment.Top) {
            // Profile Drawing Avatar
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFFECFDF5), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = LawGreen,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = lawyer.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(imageVector = Icons.Default.Verified, contentDescription = "Verified Bar Badge", tint = ActionBlue, modifier = Modifier.size(16.dp))
                }
                Text(text = lawyer.specialty, fontSize = 11.sp, color = ActionBlue, fontWeight = FontWeight.Bold)
                
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = AccentSaffron, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(text = "${lawyer.rating} (${lawyer.reviewsCount} reviews)", fontSize = 11.sp, color = TextDark)
                    }
                    Text(text = "•", fontSize = 11.sp, color = TextGray)
                    Text(text = "${lawyer.experienceYears} yrs Exp", fontSize = 11.sp, color = TextDark)
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Map, contentDescription = null, tint = TextGray, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = lawyer.city, fontSize = 11.sp, color = TextGray)
                }

                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = PaperBorder)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("CONSULTATION FEE:", fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Bold)
                        Text(text = lawyer.startingFee, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = LawGreen)
                    }
                    Button(
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(containerColor = TrustNavy),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("View Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

// ==========================================
// 5. LAWYER PROFILE DETAILS SHEET/SCREEN
// ==========================================
@Composable
fun LawyerProfileScreen(lawyer: Lawyer?, onBookConsult: (Lawyer) -> Unit) {
    if (lawyer == null) return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PaperWhite),
            border = BorderStroke(1.dp, PaperBorder),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(ActionBlue.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = ActionBlue, modifier = Modifier.size(40.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = lawyer.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TrustNavy)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(imageVector = Icons.Default.Verified, contentDescription = "Verified Bar Badge", tint = ActionBlue, modifier = Modifier.size(20.dp))
                }
                Text(text = lawyer.specialty, fontSize = 13.sp, color = ActionBlue, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                
                Surface(
                    color = LightGreen,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = LawGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Bar Council Verified: ${lawyer.barId}", fontSize = 11.sp, color = LawGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Credentials & Specs
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PaperWhite),
            border = BorderStroke(1.dp, PaperBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "CREDENTIALS & STATS", fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Experience", fontSize = 11.sp, color = TextGray)
                        Text(text = "${lawyer.experienceYears}+ Yrs", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TrustNavy)
                    }
                    Box(modifier = Modifier.width(1.dp).height(30.dp).background(PaperBorder))
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Avg Response", fontSize = 11.sp, color = TextGray)
                        Text(text = lawyer.responseTime, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TrustNavy)
                    }
                    Box(modifier = Modifier.width(1.dp).height(30.dp).background(PaperBorder))
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Fee starting at", fontSize = 11.sp, color = TextGray)
                        Text(text = lawyer.startingFee, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = LawGreen)
                    }
                }
            }
        }

        // Bio Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PaperWhite),
            border = BorderStroke(1.dp, PaperBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                BilingualText(english = "Professional Bio", hindi = "वकील के बारे में जानकारी", englishSize = 13)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = lawyer.bio, fontSize = 12.sp, color = TextDark, lineHeight = 18.sp)
                
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = PaperBorder)
                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "Languages Spoken:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TrustNavy)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = lawyer.languages.joinToString(", "), fontSize = 12.sp, color = TextDark)
            }
        }

        // Action CTA Booking
        Button(
            onClick = { onBookConsult(lawyer) },
            colors = ButtonDefaults.buttonColors(containerColor = ActionBlue),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("book_consult_button")
        ) {
            Icon(imageVector = Icons.Default.Event, contentDescription = null)
            Spacer(modifier = Modifier.width(10.dp))
            Text("Book Consultation Now", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ==========================================
// 6. LAWYER BOOKING FLOW (SLOTS & CONTEXT)
// ==========================================
@Composable
fun BookingFlowScreen(
    bookingDetailsState: BookingDetailsState?,
    savedQueries: List<LegalQuery>,
    savedAnalyses: List<DocumentAnalysis>,
    onSlotSelected: (String, String, String, String) -> Unit
) {
    if (bookingDetailsState == null) return

    val dates = listOf("Tomorrow, July 1", "July 2", "July 3", "July 4")
    val times = listOf("10:00 AM", "11:30 AM", "2:00 PM", "4:30 PM", "6:00 PM")
    val modes = listOf("Video Consult", "Phone Consult", "In-Office Meet")

    var selectedDate by remember { mutableStateOf(dates[0]) }
    var selectedTime by remember { mutableStateOf(times[0]) }
    var selectedMode by remember { mutableStateOf(modes[0]) }

    var attachContextChecked by remember { mutableStateOf(true) }
    var contextSummary by remember { mutableStateOf("") }
    var customNotes by remember { mutableStateOf("") }

    // Auto load summary text from latest saved files if available
    LaunchedEffect(savedQueries, savedAnalyses) {
        if (savedQueries.isNotEmpty()) {
            val q = savedQueries.first()
            contextSummary = "Case regarding: ${q.queryText}\nApplicable: ${q.sections}"
        } else if (savedAnalyses.isNotEmpty()) {
            val a = savedAnalyses.first()
            contextSummary = "Uploaded document: ${a.docType}\nSummary highlights: ${a.fileName}"
        } else {
            contextSummary = "No active queries yet. Booking general consultation."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BilingualText(
            english = "Select Consultation Slot",
            hindi = "तारीख और समय का चयन करें",
            englishSize = 16
        )

        // Date selection cards
        Card(
            colors = CardDefaults.cardColors(containerColor = PaperWhite),
            border = BorderStroke(1.dp, PaperBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("1. SELECT DATE:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextGray)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    dates.take(3).forEach { d ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedDate = d }
                                .background(if (selectedDate == d) ActionBlue.copy(alpha = 0.08f) else Color.Transparent, RoundedCornerShape(4.dp))
                                .border(1.dp, if (selectedDate == d) ActionBlue else PaperBorder, RoundedCornerShape(4.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = d, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (selectedDate == d) ActionBlue else TextDark, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }

        // Time selection cards
        Card(
            colors = CardDefaults.cardColors(containerColor = PaperWhite),
            border = BorderStroke(1.dp, PaperBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("2. SELECT TIME SLOT:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextGray)
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    times.forEach { t ->
                        Box(
                            modifier = Modifier
                                .clickable { selectedTime = t }
                                .background(if (selectedTime == t) ActionBlue.copy(alpha = 0.08f) else Color.Transparent, RoundedCornerShape(4.dp))
                                .border(1.dp, if (selectedTime == t) ActionBlue else PaperBorder, RoundedCornerShape(4.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = t, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (selectedTime == t) ActionBlue else TextDark)
                        }
                    }
                }
            }
        }

        // Mode Selection
        Card(
            colors = CardDefaults.cardColors(containerColor = PaperWhite),
            border = BorderStroke(1.dp, PaperBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("3. SELECT CONSULTATION MODE:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextGray)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    modes.forEach { m ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedMode = m }
                                .background(if (selectedMode == m) ActionBlue.copy(alpha = 0.08f) else Color.Transparent, RoundedCornerShape(4.dp))
                                .border(1.dp, if (selectedMode == m) ActionBlue else PaperBorder, RoundedCornerShape(4.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = m, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (selectedMode == m) ActionBlue else TextDark, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }

        // Case Sharing Context Consent Checkbox
        Card(
            colors = CardDefaults.cardColors(containerColor = PaperWhite),
            border = BorderStroke(1.dp, PaperBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { attachContextChecked = !attachContextChecked }) {
                        Icon(
                            imageVector = if (attachContextChecked) Icons.Default.CheckCircle else Icons.Default.Info,
                            tint = if (attachContextChecked) LawGreen else TextGray,
                            contentDescription = null
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Share AI Case summary with lawyer",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TrustNavy
                    )
                }
                if (attachContextChecked) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = contextSummary,
                            fontSize = 11.sp,
                            color = TextGray,
                            modifier = Modifier.padding(8.dp),
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Custom Notes input
        Card(
            colors = CardDefaults.cardColors(containerColor = PaperWhite),
            border = BorderStroke(1.dp, PaperBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("ANY ADDITIONAL QUESTIONS FOR LAWYER (OPTIONAL):", fontSize = 10.sp, color = TextGray)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = customNotes,
                    onValueChange = { customNotes = it },
                    placeholder = { Text("Write notes for advocate...", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ActionBlue,
                        unfocusedBorderColor = PaperBorder
                    )
                )
            }
        }

        // CTA Select slot
        Button(
            onClick = {
                val fullContext = if (attachContextChecked) "$contextSummary\n\nNotes: $customNotes" else "Notes: $customNotes"
                onSlotSelected(selectedDate, selectedTime, selectedMode, fullContext)
            },
            colors = ButtonDefaults.buttonColors(containerColor = ActionBlue),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth().testTag("slot_selected_confirm_btn")
        ) {
            Text("Proceed to Secure Payment", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
        }
    }
}

// ==========================================
// 7. SECURE PAYMENT SIMULATION
// ==========================================
@Composable
fun PaymentFlowScreen(
    bookingDetailsState: BookingDetailsState?,
    onPaymentSuccess: () -> Unit
) {
    if (bookingDetailsState == null) return

    var upiIdInput by remember { mutableStateOf("") }
    var isProcessingPayment by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BilingualText(
            english = "Secure Consultation Booking",
            hindi = "सुरक्षित भुगतान प्रक्रिया",
            englishSize = 16
        )

        // Summary Billing details
        Card(
            colors = CardDefaults.cardColors(containerColor = PaperWhite),
            border = BorderStroke(1.dp, PaperBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("BILLING SUMMARY / बिल विवरण:", fontSize = 10.sp, fontWeight = FontWeight.Black, color = TextGray)
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Consultation with:", fontSize = 12.sp, color = TextDark)
                    Text(text = bookingDetailsState.lawyer.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TrustNavy)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Appt Date & Time:", fontSize = 12.sp, color = TextDark)
                    Text(text = "${bookingDetailsState.date} at ${bookingDetailsState.time}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TrustNavy)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Consultation Mode:", fontSize = 12.sp, color = TextDark)
                    Text(text = bookingDetailsState.mode, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ActionBlue)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = PaperBorder)
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Total Payable Amount:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TrustNavy)
                    Text(text = bookingDetailsState.lawyer.startingFee, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = LawGreen)
                }
            }
        }

        // UPI simulated payment input
        Card(
            colors = CardDefaults.cardColors(containerColor = PaperWhite),
            border = BorderStroke(1.dp, PaperBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("PAY SECURELY VIA BHIM UPI:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextGray)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = upiIdInput,
                    onValueChange = { upiIdInput = it },
                    placeholder = { Text("E.g., name@okaxis, phone@paytm", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth().testTag("upi_id_input"),
                    leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = TextGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ActionBlue,
                        unfocusedBorderColor = PaperBorder
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = {
                        scope.launch {
                            isProcessingPayment = true
                            delay(2000) // simulated bank transaction
                            isProcessingPayment = false
                            onPaymentSuccess()
                        }
                    },
                    enabled = upiIdInput.trim().isNotEmpty() && !isProcessingPayment,
                    colors = ButtonDefaults.buttonColors(containerColor = LawGreen),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth().testTag("confirm_payment_sim_btn")
                ) {
                    if (isProcessingPayment) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                    } else {
                        Text("Pay ${bookingDetailsState.lawyer.startingFee} & Book", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ==========================================
// 8. BOOKING CONFIRMATION SCREEN
// ==========================================
@Composable
fun BookingConfirmationScreen(
    bookingDetailsState: BookingDetailsState?,
    onGoHome: () -> Unit,
    onGoHistory: () -> Unit
) {
    if (bookingDetailsState == null) return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .background(LightGreen, CircleShape)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = LawGreen,
                modifier = Modifier.size(64.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        BilingualText(
            english = "Consultation Confirmed!",
            hindi = "अपॉइंटमेंट सफलतापूर्वक बुक हो गया है!",
            englishSize = 20,
            textColor = TrustNavy,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Your digital pass has been added to My Cases locker. The advocate will call or send a link 5 mins prior to the session.",
            fontSize = 12.sp,
            color = TextGray,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(24.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = PaperWhite),
            border = BorderStroke(1.dp, PaperBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "BOOKING PASS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextGray)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = bookingDetailsState.lawyer.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TrustNavy)
                Text(text = bookingDetailsState.lawyer.specialty, fontSize = 11.sp, color = ActionBlue)
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("DATE", fontSize = 9.sp, color = TextGray)
                        Text(bookingDetailsState.date, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("TIME", fontSize = 9.sp, color = TextGray)
                        Text(bookingDetailsState.time, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("MODE", fontSize = 9.sp, color = TextGray)
                        Text(bookingDetailsState.mode, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ActionBlue)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onGoHistory,
            colors = ButtonDefaults.buttonColors(containerColor = ActionBlue),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth().testTag("view_my_bookings_cta")
        ) {
            Text("Go to My Locker / बुकिंग्स देखें", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = onGoHome,
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(1.dp, PaperBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Return to Dashboard", fontSize = 12.sp, color = TextDark)
        }
    }
}

// ==========================================
// 9. HISTORY SCREEN
// ==========================================
@Composable
fun HistoryScreen(
    viewModel: LegalHelperViewModel,
    savedQueries: List<LegalQuery>,
    savedAnalyses: List<DocumentAnalysis>,
    savedBookings: List<LawyerBooking>,
    onQueryClick: (LegalQuery) -> Unit,
    onAnalysisClick: (DocumentAnalysis) -> Unit
) {
    var selectedTab by remember { mutableStateOf("Queries") } // Queries, Documents, Bookings

    val tabs = listOf("Queries", "Documents", "Bookings")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BilingualText(
            english = "My Cases Locker / मेरा इतिहास",
            hindi = "आपके सुरक्षित सहेजे गए विश्लेषण और वकील नियुक्तियां",
            englishSize = 18
        )

        // Tab Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(PaperBorder.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .padding(4.dp)
        ) {
            tabs.forEach { t ->
                val isSelected = selectedTab == t
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = t }
                        .background(if (isSelected) Color.White else Color.Transparent, RoundedCornerShape(6.dp))
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = t,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) ActionBlue else TextGray
                    )
                }
            }
        }

        // List Display
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            when (selectedTab) {
                "Queries" -> {
                    if (savedQueries.isEmpty()) {
                        item {
                            EmptyHistoryPlaceholder("No queries analyzed yet. Describe a case under 'Ask AI' tab.")
                        }
                    } else {
                        items(savedQueries) { item ->
                            HistoryQueryCard(
                                item = item,
                                onClick = { onQueryClick(item) },
                                onDelete = { viewModel.deleteQuery(item) }
                            )
                        }
                    }
                }
                "Documents" -> {
                    if (savedAnalyses.isEmpty()) {
                        item {
                            EmptyHistoryPlaceholder("No documents scanned yet. Scan a notice under 'Scan Doc' tab.")
                        }
                    } else {
                        items(savedAnalyses) { item ->
                            HistoryDocCard(
                                item = item,
                                onClick = { onAnalysisClick(item) },
                                onDelete = { viewModel.deleteAnalysis(item) }
                            )
                        }
                    }
                }
                "Bookings" -> {
                    if (savedBookings.isEmpty()) {
                        item {
                            EmptyHistoryPlaceholder("No upcoming lawyer bookings. Browse lawyers under 'Lawyers' tab.")
                        }
                    } else {
                        items(savedBookings) { item ->
                            HistoryBookingCard(
                                item = item,
                                onCancel = { viewModel.cancelBooking(item) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyHistoryPlaceholder(text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        colors = CardDefaults.cardColors(containerColor = PaperWhite),
        border = BorderStroke(1.dp, PaperBorder)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = Icons.Default.History, contentDescription = null, tint = TextGray.copy(alpha = 0.5f), modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = text, fontSize = 12.sp, color = TextGray, textAlign = TextAlign.Center, lineHeight = 16.sp)
        }
    }
}

@Composable
fun HistoryQueryCard(item: LegalQuery, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("history_query_card_${item.id}"),
        colors = CardDefaults.cardColors(containerColor = PaperWhite),
        border = BorderStroke(1.dp, PaperBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = item.sections, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ActionBlue)
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Delete", tint = LawRed, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = item.queryText, fontSize = 12.sp, color = TextDark, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(color = if (item.isBailable) LightGreen else LightRed, shape = RoundedCornerShape(4.dp)) {
                    Text(text = if (item.isBailable) "Bailable" else "Non-Bailable", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (item.isBailable) LawGreen else LawRed, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
                Surface(color = LightGreen, shape = RoundedCornerShape(4.dp)) {
                    Text(text = "Saved in Locker", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = LawGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
        }
    }
}

@Composable
fun HistoryDocCard(item: DocumentAnalysis, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("history_doc_card_${item.id}"),
        colors = CardDefaults.cardColors(containerColor = PaperWhite),
        border = BorderStroke(1.dp, PaperBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = item.docType, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TrustNavy)
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Delete", tint = LawRed, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "File Name: ${item.fileName}", fontSize = 11.sp, color = TextGray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Checklist context: ${item.originalText}", fontSize = 11.sp, color = TextDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun HistoryBookingCard(item: LawyerBooking, onCancel: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("history_booking_card_${item.id}"),
        colors = CardDefaults.cardColors(containerColor = PaperWhite),
        border = BorderStroke(1.dp, PaperBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column {
                    Text(text = item.lawyerName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TrustNavy)
                    Text(text = item.lawyerSpecialty, fontSize = 11.sp, color = ActionBlue)
                }
                Surface(color = LightGreen, shape = RoundedCornerShape(4.dp)) {
                    Text(text = item.status, fontSize = 10.sp, fontWeight = FontWeight.Black, color = LawGreen, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = PaperBorder)
            Spacer(modifier = Modifier.height(10.dp))
            
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("SLOT SCHEDULE", fontSize = 9.sp, color = TextGray)
                    Text("${item.appointmentDate} at ${item.appointmentTime}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("MODE", fontSize = 9.sp, color = TextGray)
                    Text(item.consultingMode, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ActionBlue)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("CONSULTATION FEE", fontSize = 9.sp, color = TextGray)
                    Text(item.fee, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LawGreen)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = onCancel,
                border = BorderStroke(1.dp, LawRed.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel Appointment / अपॉइंटमेंट रद्द करें", fontSize = 11.sp, color = LawRed, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==========================================
// 10. IPC ↔ BNS OFFLINE CONVERTER SCREEN
// ==========================================
@Composable
fun IpcBnsConverterScreen() {
    var searchQuery by remember { mutableStateOf("") }

    val conversions = listOf(
        ConversionMapping("Murder (हत्या)", "IPC Section 302", "BNS Section 103", "Death penalty or life imprisonment.", "मृत्युदंड या आजीवन कारावास"),
        ConversionMapping("Theft (चोरी)", "IPC Section 379", "BNS Section 303", "Up to 3 years imprisonment or fine.", "3 साल तक की जेल या जुर्माना"),
        ConversionMapping("Outraging Modesty (महिला शालीनता भंग)", "IPC Section 354", "BNS Section 74", "1 to 5 years imprisonment + fine.", "1 से 5 साल की जेल + जुर्माना"),
        ConversionMapping("Cheating & Dishonesty (धोखाधड़ी)", "IPC Section 420", "BNS Section 318", "Up to 7 years imprisonment + fine.", "7 साल तक की जेल + जुर्माना"),
        ConversionMapping("Simple Hurt (साधारण चोट)", "IPC Section 323", "BNS Section 115", "Up to 1 year or Rs. 1000 fine.", "1 साल तक की जेल या जुर्माना"),
        ConversionMapping("Criminal Trespass (अतिक्रमण)", "IPC Section 447", "BNS Section 329", "Up to 3 months or Rs. 500 fine.", "3 महीने तक की जेल"),
        ConversionMapping("Dowry Death (दहेज मृत्यु)", "IPC Section 304B", "BNS Section 80", "Imprisonment of 7 years to life.", "7 साल से लेकर आजीवन कारावास तक")
    )

    val filteredConversions = conversions.filter {
        it.crimeName.contains(searchQuery, ignoreCase = true) ||
        it.ipc.contains(searchQuery, ignoreCase = true) ||
        it.bns.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BilingualText(
            english = "IPC ↔ BNS Code Converter",
            hindi = "भारतीय न्याय संहिता 2023 और आईपीसी परिवर्तन तालिका",
            englishSize = 18
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("E.g., 302, 420, Theft, Murder, चोरी...", fontSize = 12.sp) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().testTag("converter_search_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ActionBlue,
                unfocusedBorderColor = PaperBorder
            ),
            singleLine = true
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
            items(filteredConversions) { mapping ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = PaperWhite),
                    border = BorderStroke(1.dp, PaperBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = mapping.crimeName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TrustNavy)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("OLD IPC SECTION:", fontSize = 9.sp, color = TextGray)
                                Surface(color = LightRed, shape = RoundedCornerShape(4.dp)) {
                                    Text(text = mapping.ipc, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LawRed, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("NEW BNS SECTION:", fontSize = 9.sp, color = TextGray)
                                Surface(color = LightGreen, shape = RoundedCornerShape(4.dp)) {
                                    Text(text = mapping.bns, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LawGreen, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = PaperBorder)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "English Punishment: ${mapping.punishmentEng}", fontSize = 11.sp, color = TextGray)
                        Text(text = "हिंदी सजा: ${mapping.punishmentHindi}", fontSize = 11.sp, color = TextGray)
                    }
                }
            }
        }
    }
}

data class ConversionMapping(
    val crimeName: String,
    val ipc: String,
    val bns: String,
    val punishmentEng: String,
    val punishmentHindi: String
)

// ==========================================
// 11. COURT PROCESS JOURNEY SCREEN
// ==========================================
@Composable
fun CourtProcessJourneyScreen() {
    val steps = listOf(
        CourtStage("1. FIR Registration (प्रथम सूचना रिपोर्ट)", "A formal complaint registered by police for cognizable offenses.", "पुलिस में संज्ञेय अपराध की शिकायत दर्ज करना।"),
        CourtStage("2. Investigation (पुलिस जांच प्रक्रिया)", "Investigating Officer (IO) gathers evidence, takes witness statements, and performs arrests.", "आईओ द्वारा सबूत और गवाहों के बयान सहेजना।"),
        CourtStage("3. Filing of Chargesheet (आरोप पत्र)", "Police submits report with all evidence to Court within 60 or 90 days.", "पुलिस जांच समाप्त करके कोर्ट में रिपोर्ट पेश करती है।"),
        CourtStage("4. Framing of Charges (आरोप तय करना)", "Court formalizes criminal charges against the accused person.", "न्यायाधीश आरोपी के खिलाफ आरोप तय करता है।"),
        CourtStage("5. Trial & Evidence (गवाही और दलीलें)", "Prosecution and defense lawyers examine witnesses and submit documentation proofs.", "दोनों पक्षों के वकीलों द्वारा कोर्ट में सबूत और बहस करना।"),
        CourtStage("6. Final Judgment (अंतिम निर्णय)", "Court delivers final acquittal or conviction and sentences the offender.", "न्यायालय द्वारा दोषसिद्धि या रिहाई का अंतिम आदेश।")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BilingualText(
            english = "Indian Court Trial Journey Guide",
            hindi = "भारत की न्यायिक कोर्ट प्रक्रिया",
            englishSize = 18
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.weight(1f)) {
            items(steps) { stage ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = PaperWhite),
                    border = BorderStroke(1.dp, PaperBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = stage.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TrustNavy)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "English: ${stage.descEng}", fontSize = 11.sp, color = TextGray, lineHeight = 16.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "हिंदी: ${stage.descHindi}", fontSize = 10.sp, color = TextGray.copy(alpha = 0.8f), lineHeight = 15.sp)
                    }
                }
            }
        }
    }
}

data class CourtStage(
    val title: String,
    val descEng: String,
    val descHindi: String
)

// ==========================================
// 12. PAYWALL OVERLAY PREMIUM
// ==========================================
@Composable
fun PaywallOverlay(
    onDismiss: () -> Unit,
    onUpgradeSuccess: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize().testTag("paywall_overlay"),
        color = Color.Black.copy(alpha = 0.65f)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(24.dp)) {
            Card(
                colors = CardDefaults.cardColors(containerColor = PaperWhite),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, PaperBorder),
                modifier = Modifier.fillMaxWidth().widthIn(max = 450.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Close", tint = TextGray)
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    BilingualText(
                        english = "Upgrade to Pro Locker",
                        hindi = "असीमित एआई कानूनी विश्लेषण अनलॉक करें",
                        englishSize = 18,
                        textColor = TrustNavy,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Get professional, unrestricted access for your case needs. Cancel anytime.",
                        fontSize = 11.sp,
                        color = TextGray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = PaperBorder)
                    Spacer(modifier = Modifier.height(16.dp))

                    PremiumBenefitRow("Unlimited AI Case Queries / असीमित एआई सवाल")
                    PremiumBenefitRow("Unlimited PDF Notice & Deeds Scans / असीमित स्कैन")
                    PremiumBenefitRow("Prioritized response times / तेजी से विश्लेषण")
                    PremiumBenefitRow("Downloadable case PDF report / पीडीएफ रिपोर्ट डाउनलोड")

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Surface(
                        color = LightYellow,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "PRO PLAN", fontSize = 10.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.tertiary)
                            Text(text = "₹199 / Month", fontSize = 24.sp, fontWeight = FontWeight.Black, color = TrustNavy)
                            Text(text = "Pay secure via UPI card", fontSize = 9.sp, color = TextGray)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onUpgradeSuccess,
                        colors = ButtonDefaults.buttonColors(containerColor = ActionBlue),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth().testTag("upgrade_pro_success_btn")
                    ) {
                        Text("Upgrade Instantly / अभी अपग्रेड करें", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumBenefitRow(benefit: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = LawGreen, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = benefit, fontSize = 11.sp, color = TextDark)
    }
}
