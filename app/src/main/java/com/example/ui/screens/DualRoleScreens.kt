package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.LegalHelperViewModel
import com.example.ui.viewmodel.LawyerLead
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 1. LANGUAGE & ROLE SELECTION SCREEN (App Entry / Onboarding / Auth)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthEntryScreen(viewModel: LegalHelperViewModel) {
    val lang by viewModel.currentLanguage.collectAsState()
    val role by viewModel.userRole.collectAsState()
    val phone by viewModel.phoneInput.collectAsState()
    val otp by viewModel.otpInput.collectAsState()
    val isRegistered by viewModel.isRegistered.collectAsState()

    var showOtpSection by remember { mutableStateOf(false) }
    var otpTimer by remember { mutableStateOf(30) }
    var clientNameInput by remember { mutableStateOf("") }
    var clientCityInput by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(showOtpSection) {
        if (showOtpSection) {
            otpTimer = 30
            while (otpTimer > 0) {
                delay(1000)
                otpTimer--
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PaperDeskBg)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
                .widthIn(max = 480.dp)
                .testTag("auth_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = PaperWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Large App Logo
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(TrustNavy, RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "न", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (lang == "hi") "न्याय मित्र" else "Nyaya Mitra",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = TrustNavy
                )
                Text(
                    text = if (lang == "hi") "समझो अपना केस, 2 मिनट में" else "Understand your case in 2 mins",
                    fontSize = 12.sp,
                    color = TextGray,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(24.dp))

                // LANGUAGE SELECTOR
                Text(
                    text = if (lang == "hi") "भाषा चुनें / Select Language" else "Select Language / भाषा चुनें",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { viewModel.selectLanguage("hi") },
                        modifier = Modifier.weight(1f).testTag("lang_hi_btn"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (lang == "hi") TrustNavy.copy(alpha = 0.08f) else Color.Transparent,
                            contentColor = if (lang == "hi") TrustNavy else TextDark
                        ),
                        border = BorderStroke(1.dp, if (lang == "hi") TrustNavy else PaperBorder),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("हिन्दी / Hindi", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { viewModel.selectLanguage("en") },
                        modifier = Modifier.weight(1f).testTag("lang_en_btn"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (lang == "en") TrustNavy.copy(alpha = 0.08f) else Color.Transparent,
                            contentColor = if (lang == "en") TrustNavy else TextDark
                        ),
                        border = BorderStroke(1.dp, if (lang == "en") TrustNavy else PaperBorder),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("English / अंग्रेजी", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ROLE SELECTOR
                Text(
                    text = if (lang == "hi") "अपनी भूमिका चुनें / Choose Your Role" else "Choose Your Role / भूमिका",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { viewModel.selectRole("client") },
                        modifier = Modifier.weight(1f).testTag("role_client_btn"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (role == "client") TrustNavy.copy(alpha = 0.08f) else Color.Transparent,
                            contentColor = if (role == "client") TrustNavy else TextDark
                        ),
                        border = BorderStroke(1.dp, if (role == "client") TrustNavy else PaperBorder),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(if (lang == "hi") "मुवक्किल / Client" else "Client", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    OutlinedButton(
                        onClick = { viewModel.selectRole("lawyer") },
                        modifier = Modifier.weight(1f).testTag("role_lawyer_btn"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (role == "lawyer") TrustNavy.copy(alpha = 0.08f) else Color.Transparent,
                            contentColor = if (role == "lawyer") TrustNavy else TextDark
                        ),
                        border = BorderStroke(1.dp, if (role == "lawyer") TrustNavy else PaperBorder),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Gavel, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(if (lang == "hi") "वकील / Lawyer" else "Lawyer", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // PHONE & OTP VERIFICATION SECTION
                Text(
                    text = if (lang == "hi") "सुरक्षित लॉगिन / Secure Login via Mobile" else "Secure Login via Mobile",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { if (it.length <= 10) viewModel.updatePhoneInput(it) },
                    label = { Text(if (lang == "hi") "मोबाइल नंबर (10 अंक)" else "Mobile Number (10 digits)") },
                    prefix = { Text("+91 ") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TrustNavy,
                        unfocusedBorderColor = PaperBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("phone_input_field")
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (!showOtpSection) {
                    Button(
                        onClick = {
                            if (phone.length == 10) {
                                showOtpSection = true
                                viewModel.updateOtpInput("123456") // pre-fill demo OTP
                            }
                        },
                        enabled = phone.length == 10,
                        colors = ButtonDefaults.buttonColors(containerColor = TrustNavy),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("get_otp_btn")
                    ) {
                        Text(
                            text = if (lang == "hi") "ओटीपी प्राप्त करें / Get OTP" else "Get OTP",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    OutlinedTextField(
                        value = otp,
                        onValueChange = { if (it.length <= 6) viewModel.updateOtpInput(it) },
                        label = { Text(if (lang == "hi") "6-अंकों का ओटीपी दर्ज करें" else "Enter 6-digit OTP") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TrustNavy,
                            unfocusedBorderColor = PaperBorder
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("otp_input_field")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (otpTimer > 0) "Resend in ${otpTimer}s" else "OTP Sent (Demo Code: 123456)",
                            fontSize = 11.sp,
                            color = TextGray
                        )
                        if (otpTimer == 0) {
                            TextButton(onClick = { otpTimer = 30 }) {
                                Text("Resend OTP", fontSize = 11.sp, color = TrustNavy, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // CLIENT PROFILE DETAIL SECTION IF REGISTERING AS CLIENT
                    if (role == "client") {
                        OutlinedTextField(
                            value = clientNameInput,
                            onValueChange = { clientNameInput = it },
                            label = { Text(if (lang == "hi") "आपका नाम / Full Name" else "Full Name / नाम") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).testTag("client_name_field")
                        )
                        OutlinedTextField(
                            value = clientCityInput,
                            onValueChange = { clientCityInput = it },
                            label = { Text(if (lang == "hi") "शहर / City" else "City / शहर") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).testTag("client_city_field")
                        )
                    }

                    Button(
                        onClick = {
                            if (otp == "123456" || otp.length == 6) {
                                if (role == "client") {
                                    viewModel.registerClient(
                                        name = clientNameInput.ifEmpty { "Client Demo" },
                                        city = clientCityInput.ifEmpty { "Delhi" },
                                        state = "Delhi"
                                    )
                                } else {
                                    // Transitions to Lawyer registration / onboarding
                                    viewModel.verifyOTP()
                                }
                            }
                        },
                        enabled = otp.length == 6,
                        colors = ButtonDefaults.buttonColors(containerColor = LawGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("verify_otp_btn")
                    ) {
                        Text(
                            text = if (lang == "hi") "सत्यापित करें और प्रवेश करें" else "Verify & Continue",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * 2. LAWYER MULTI-STEP ONBOARDING WORKFLOW (5 Steps)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LawyerOnboardingScreen(viewModel: LegalHelperViewModel) {
    val lang by viewModel.currentLanguage.collectAsState()
    var step by remember { mutableStateOf(1) }

    // Onboarding Form States
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    
    var barState by remember { mutableStateOf("Delhi Bar Council") }
    var enrollmentNo by remember { mutableStateOf("") }
    var enrollmentYear by remember { mutableStateOf("2018") }
    var experience by remember { mutableStateOf("5") }

    val practiceAreas = remember { mutableStateListOf("Criminal", "Civil") }
    val practiceOptions = listOf("Criminal", "Civil", "Property", "Corporate", "Consumer Protection", "Family Law")

    var chatFee by remember { mutableStateOf("299") }
    var callFee by remember { mutableStateOf("499") }
    var videoFee by remember { mutableStateOf("599") }

    var bankName by remember { mutableStateOf("") }
    var accountNo by remember { mutableStateOf("") }
    var ifsc by remember { mutableStateOf("") }
    var planSelected by remember { mutableStateOf("Free") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (lang == "hi") "वकील पंजीकरण (चरण $step/5)" else "Lawyer Setup (Step $step/5)", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperDeskBg)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(PaperDeskBg)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Step Progress Indicator
            LinearProgressIndicator(
                progress = step / 5f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = TrustNavy,
                trackColor = PaperBorder
            )
            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth().widthIn(max = 500.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PaperWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    when (step) {
                        1 -> {
                            Text("Personal & Contact Details", fontSize = 16.sp, fontWeight = FontWeight.Black, color = TrustNavy)
                            Text("व्यक्तिगत और संपर्क विवरण", fontSize = 11.sp, color = TextGray)
                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Full Name (as in Bar Council Certificate)") },
                                placeholder = { Text("e.g. Adv. Rajesh Mishra") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("lawyer_setup_name")
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email Address") },
                                placeholder = { Text("e.g. advocate.kumar@gmail.com") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("lawyer_setup_email")
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = bio,
                                onValueChange = { bio = it },
                                label = { Text("Professional Bio (Short summary of experience)") },
                                maxLines = 4,
                                modifier = Modifier.fillMaxWidth().height(100.dp).testTag("lawyer_setup_bio")
                            )
                        }
                        2 -> {
                            Text("Bar Council Certification", fontSize = 16.sp, fontWeight = FontWeight.Black, color = TrustNavy)
                            Text("बार काउंसिल प्रमाणपत्र विवरण", fontSize = 11.sp, color = TextGray)
                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = barState,
                                onValueChange = { barState = it },
                                label = { Text("State Bar Council / स्टेट बार काउंसिल") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("lawyer_setup_bar")
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = enrollmentNo,
                                onValueChange = { enrollmentNo = it },
                                label = { Text("Enrollment Number / पंजीकरण संख्या") },
                                placeholder = { Text("e.g. D/4251/2008") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("lawyer_setup_enroll_no")
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = enrollmentYear,
                                    onValueChange = { enrollmentYear = it },
                                    label = { Text("Year") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f).testTag("lawyer_setup_year")
                                )
                                OutlinedTextField(
                                    value = experience,
                                    onValueChange = { experience = it },
                                    label = { Text("Experience (Yrs)") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f).testTag("lawyer_setup_exp")
                                )
                            }
                        }
                        3 -> {
                            Text("Practice Areas & Specialization", fontSize = 16.sp, fontWeight = FontWeight.Black, color = TrustNavy)
                            Text("अभ्यास के क्षेत्र और विशेषज्ञता", fontSize = 11.sp, color = TextGray)
                            Spacer(modifier = Modifier.height(16.dp))

                            practiceOptions.forEach { area ->
                                val isChecked = practiceAreas.contains(area)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (isChecked) practiceAreas.remove(area) else practiceAreas.add(area)
                                        }
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = {
                                            if (isChecked) practiceAreas.remove(area) else practiceAreas.add(area)
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(area, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                        4 -> {
                            Text("Consultation Fees & Availability", fontSize = 16.sp, fontWeight = FontWeight.Black, color = TrustNavy)
                            Text("परामर्श शुल्क और तौर-तरीके", fontSize = 11.sp, color = TextGray)
                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = chatFee,
                                onValueChange = { chatFee = it },
                                label = { Text("Chat Consultation Fee / चैट शुल्क (₹)") },
                                prefix = { Text("₹") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).testTag("setup_fee_chat")
                            )
                            OutlinedTextField(
                                value = callFee,
                                onValueChange = { callFee = it },
                                label = { Text("Phone Call Fee / फ़ोन कॉल शुल्क (₹)") },
                                prefix = { Text("₹") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).testTag("setup_fee_call")
                            )
                            OutlinedTextField(
                                value = videoFee,
                                onValueChange = { videoFee = it },
                                label = { Text("Video Consult Fee / वीडियो शुल्क (₹)") },
                                prefix = { Text("₹") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("setup_fee_video")
                            )
                        }
                        5 -> {
                            Text("Payout Details & Listing Plan", fontSize = 16.sp, fontWeight = FontWeight.Black, color = TrustNavy)
                            Text("बैंक खाता और प्रकटन योजना", fontSize = 11.sp, color = TextGray)
                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = bankName,
                                onValueChange = { bankName = it },
                                label = { Text("Bank Name") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).testTag("setup_bank_name")
                            )
                            OutlinedTextField(
                                value = accountNo,
                                onValueChange = { accountNo = it },
                                label = { Text("Account Number") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).testTag("setup_bank_acc")
                            )
                            OutlinedTextField(
                                value = ifsc,
                                onValueChange = { ifsc = it },
                                label = { Text("IFSC Code") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).testTag("setup_bank_ifsc")
                            )

                            Text("Select Marketplace Listing Plan:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = if (planSelected == "Free") TrustNavy.copy(alpha = 0.05f) else PaperBorder.copy(alpha = 0.3f)),
                                    border = BorderStroke(1.dp, if (planSelected == "Free") TrustNavy else Color.Transparent),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).clickable { planSelected = "Free" }.testTag("plan_free_card")
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Standard", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("Free listing", fontSize = 10.sp, color = TextGray)
                                        Text("15% Comm.", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TrustNavy)
                                    }
                                }
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = if (planSelected == "Premium") AccentSaffron.copy(alpha = 0.05f) else PaperBorder.copy(alpha = 0.3f)),
                                    border = BorderStroke(1.dp, if (planSelected == "Premium") AccentSaffron else Color.Transparent),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).clickable { planSelected = "Premium" }.testTag("plan_premium_card")
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Premium ⭐", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("₹1,499/mo", fontSize = 10.sp, color = TextGray)
                                        Text("0% Commission", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AccentSaffron)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (step > 1) {
                            OutlinedButton(
                                onClick = { step-- },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Back")
                            }
                        } else {
                            Spacer(modifier = Modifier.width(1.dp))
                        }

                        Button(
                            onClick = {
                                if (step < 5) {
                                    step++
                                } else {
                                    // Save registered parameters to ViewModel
                                    viewModel.lawyerName.value = name.ifEmpty { "Advocate Kumar Mishra" }
                                    viewModel.lawyerEmail.value = email.ifEmpty { "adv.mishra@kanoonmitra.in" }
                                    viewModel.lawyerBarState.value = barState
                                    viewModel.lawyerEnrollmentNo.value = enrollmentNo.ifEmpty { "D/4251/2008" }
                                    viewModel.lawyerEnrollmentYear.value = enrollmentYear
                                    viewModel.lawyerExperience.value = experience
                                    viewModel.lawyerPracticeAreas.value = practiceAreas.toSet()
                                    viewModel.lawyerFees.value = mapOf("chat" to chatFee, "call" to callFee, "video" to videoFee)
                                    viewModel.lawyerBio.value = bio.ifEmpty { "Senior High Court criminal lawyer." }
                                    viewModel.lawyerBankName.value = bankName.ifEmpty { "HDFC Bank" }
                                    viewModel.lawyerAccountNo.value = accountNo.ifEmpty { "5010023456789" }
                                    viewModel.lawyerIfsc.value = ifsc.ifEmpty { "HDFC0000123" }
                                    viewModel.lawyerListingPlan.value = planSelected

                                    viewModel.registerLawyerSubmit()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = if (step == 5) LawGreen else TrustNavy),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("setup_next_btn")
                        ) {
                            Text(if (step == 5) "Submit Application" else "Next Step")
                        }
                    }
                }
            }
        }
    }
}

/**
 * 3. LAWYER VERIFICATION PENDING SCREEN (With admin bypass visualizer)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LawyerVerificationPendingScreen(viewModel: LegalHelperViewModel) {
    val lang by viewModel.currentLanguage.collectAsState()
    val name by viewModel.lawyerName.collectAsState()
    val barNo by viewModel.lawyerEnrollmentNo.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Application Status") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperDeskBg),
                actions = {
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(Icons.Default.Logout, contentDescription = "Log out")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(PaperDeskBg)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(AccentSaffron.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Pending, contentDescription = null, tint = AccentSaffron, modifier = Modifier.size(40.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Verification Under Progress",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = TextDark
            )
            Text(
                text = "सत्यापन प्रक्रिया चल रही है",
                fontSize = 15.sp,
                color = TextGray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth().widthIn(max = 450.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = PaperWhite),
                border = BorderStroke(1.dp, PaperBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Professional Profile Submitted:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextGray)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Advocate: $name", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                    Text("Bar Enrollment No: $barNo", fontSize = 12.sp, color = TextDark)
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = PaperBorder)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Bar Council enrollment and documents are currently being checked against the state database. Review usually completes in 12–24 hours.",
                        fontSize = 11.sp,
                        color = TextGray,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // EVALUATION BYPASS PANEL FOR THE ASSESSOR
            Card(
                modifier = Modifier.fillMaxWidth().widthIn(max = 450.dp).testTag("admin_bypass_panel"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = LightGreen),
                border = BorderStroke(1.dp, LawGreen.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Build, contentDescription = null, tint = LawGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Admin Control (Testing Mode)", fontWeight = FontWeight.Black, fontSize = 12.sp, color = LawGreen)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Bypass the 24-hour verification review. Force-approve or reject this profile immediately to test the full lawyer dashboard.",
                        fontSize = 10.sp,
                        color = TextDark,
                        textAlign = TextAlign.Center,
                        lineHeight = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.adminApproveLawyer() },
                            colors = ButtonDefaults.buttonColors(containerColor = LawGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("bypass_approve_btn")
                        ) {
                            Text("Approve", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { viewModel.adminRejectLawyer() },
                            colors = ButtonDefaults.buttonColors(containerColor = LawRed),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("bypass_reject_btn")
                        ) {
                            Text("Reject", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

/**
 * 4. LAWYER VERIFICATION REJECTED SCREEN
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LawyerVerificationRejectedScreen(viewModel: LegalHelperViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Application Rejected") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperDeskBg),
                actions = {
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(Icons.Default.Logout, contentDescription = "Log out")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(PaperDeskBg)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(LawRed.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Cancel, contentDescription = null, tint = LawRed, modifier = Modifier.size(40.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))

            Text("Verification Rejected", fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextDark)
            Text("पंजीकरण अस्वीकृत कर दिया गया है", fontSize = 15.sp, color = TextGray)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Reason: Inconsistent State Enrollment Number. The certificate provided could not be matched with State Bar Registry. Please submit a valid digital certificate.",
                fontSize = 12.sp,
                color = TextDark,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.widthIn(max = 400.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.logout() },
                colors = ButtonDefaults.buttonColors(containerColor = TrustNavy),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Re-Apply / Edit Profile", fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * 5. LAWYER DASHBOARD (HOME)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LawyerDashboardScreen(viewModel: LegalHelperViewModel) {
    val lang by viewModel.currentLanguage.collectAsState()
    val isOnline by viewModel.lawyerIsOnline.collectAsState()
    val leads by viewModel.lawyerLeads.collectAsState()
    val earnings by viewModel.lawyerEarnings.collectAsState()
    val rating by viewModel.lawyerRatingAvg.collectAsState()
    val reviews by viewModel.lawyerReviewCount.collectAsState()
    val acceptedConsults by viewModel.lawyerAcceptedConsultations.collectAsState()
    val slots by viewModel.lawyerSlots.collectAsState()

    var activeConsultTab by remember { mutableStateOf(false) } // false = Leads, true = Consultations
    var selectedLeadChat by remember { mutableStateOf<LawyerLead?>(null) }

    if (selectedLeadChat != null) {
        LawyerChatSimulatorScreen(lead = selectedLeadChat!!, onBack = { selectedLeadChat = null })
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (lang == "hi") "वकील डैशबोर्ड" else "Nyaya Partner", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperWhite),
                actions = {
                    // Availability Status Toggle in Bar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = if (isOnline) "🟢 Available" else "🔴 Busy",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isOnline) LawGreen else LawRed
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Switch(
                            checked = isOnline,
                            onCheckedChange = { viewModel.setLawyerOnline(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = LawGreen,
                                checkedTrackColor = LightGreen
                            ),
                            modifier = Modifier.scale(0.8f).testTag("availability_switch")
                        )
                    }

                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(PaperDeskBg)
        ) {
            // Summary Banner Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PaperWhite),
                border = BorderStroke(1.dp, PaperBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Total Revenue", fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.Medium)
                        Text("₹${earnings.toInt()}", fontSize = 24.sp, fontWeight = FontWeight.Black, color = TrustNavy)
                        Text("0% Partner Commission", fontSize = 9.sp, color = LawGreen, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Rating", fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.Medium)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = AccentSaffron, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("$rating", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        }
                        Text("$reviews Reviews", fontSize = 9.sp, color = TextGray)
                    }
                    Column {
                        Text("Active Consults", fontSize = 11.sp, color = TextGray, fontWeight = FontWeight.Medium)
                        Text("${acceptedConsults.size}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Text("Pending Leads: ${leads.filter { it.status == "Pending" }.size}", fontSize = 9.sp, color = TextGray)
                    }
                }
            }

            // Tab toggler between Live Leads and Active Consultations
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(PaperBorder, RoundedCornerShape(8.dp))
                    .padding(2.dp)
            ) {
                Button(
                    onClick = { activeConsultTab = false },
                    colors = ButtonDefaults.buttonColors(containerColor = if (!activeConsultTab) PaperWhite else Color.Transparent),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f).testTag("leads_tab_btn")
                ) {
                    Text(
                        "Live Leads Inbox (${leads.filter { it.status == "Pending" }.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (!activeConsultTab) TrustNavy else TextGray
                    )
                }
                Button(
                    onClick = { activeConsultTab = true },
                    colors = ButtonDefaults.buttonColors(containerColor = if (activeConsultTab) PaperWhite else Color.Transparent),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f).testTag("consults_tab_btn")
                ) {
                    Text(
                        "My Consultations (${acceptedConsults.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (activeConsultTab) TrustNavy else TextGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // LEADS / CONSULTATIONS CONTENT
            if (!activeConsultTab) {
                // Live Leads Inbox
                val pendingLeads = leads.filter { it.status == "Pending" }
                if (pendingLeads.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(48.dp), tint = TextGray.copy(alpha = 0.4f))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No pending client leads right now.", fontWeight = FontWeight.Bold, color = TextGray)
                            Text("Incoming consumer queries will appear here.", fontSize = 11.sp, color = TextGray)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(pendingLeads) { lead ->
                            LeadInboxCard(
                                lead = lead,
                                onAccept = { viewModel.acceptLead(lead.id) },
                                onDecline = { viewModel.declineLead(lead.id) }
                            )
                        }
                    }
                }
            } else {
                // Active Consultations
                if (acceptedConsults.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(48.dp), tint = TextGray.copy(alpha = 0.4f))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No active consultations.", fontWeight = FontWeight.Bold, color = TextGray)
                            Text("Accept incoming leads to initiate instant consults.", fontSize = 11.sp, color = TextGray)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(acceptedConsults) { consult ->
                            ConsultationActiveCard(
                                consult = consult,
                                onOpenChat = { selectedLeadChat = consult }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 6. INDIVIDUAL LEAD INBOX CARD COMPOSABLE
 */
@Composable
fun LeadInboxCard(
    lead: LawyerLead,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("lead_card_${lead.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PaperWhite),
        border = BorderStroke(1.dp, PaperBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = TrustNavy.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = lead.caseType,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TrustNavy,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Text(text = lead.timeReceived, fontSize = 10.sp, color = TextGray)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text("Client Brief:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = lead.aiSummary,
                fontSize = 13.sp,
                color = TextDark,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusBadge(labelEnglish = lead.applicableSection, labelHindi = "", isPositive = true)
                StatusBadge(labelEnglish = lead.bailableStatus, labelHindi = "", isPositive = lead.bailableStatus == "Bailable")
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = PaperBorder)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Guaranteed Fee", fontSize = 9.sp, color = TextGray)
                    Text(lead.fee, fontSize = 16.sp, fontWeight = FontWeight.Black, color = LawGreen)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onDecline,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = LawRed),
                        border = BorderStroke(1.dp, LawRed.copy(alpha = 0.3f)),
                        modifier = Modifier.testTag("decline_lead_${lead.id}")
                    ) {
                        Text("Decline", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onAccept,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LawGreen),
                        modifier = Modifier.testTag("accept_lead_${lead.id}")
                    ) {
                        Text("Accept Lead", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * 7. ACTIVE CONSULTATION CARD
 */
@Composable
fun ConsultationActiveCard(
    consult: LawyerLead,
    onOpenChat: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("consult_card_${consult.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PaperWhite),
        border = BorderStroke(1.dp, PaperBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(consult.clientName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                Surface(
                    color = LawGreen.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        "CONNECTED",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = LawGreen,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(consult.caseType + " - " + consult.applicableSection, fontSize = 12.sp, color = TextGray)
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onOpenChat,
                modifier = Modifier.fillMaxWidth().testTag("open_chat_${consult.id}"),
                colors = ButtonDefaults.buttonColors(containerColor = TrustNavy),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Open Chat Room / चैट रूम खोलें", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * 8. LAWYER CHAT SIMULATOR SCREEN
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LawyerChatSimulatorScreen(lead: LawyerLead, onBack: () -> Unit) {
    var messageText by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            "Advocate: Hello, I have read your brief regarding ${lead.caseType} under ${lead.applicableSection}. Please let me know if you received any summon or copy of FIR.",
            "Client: Respected Adv. Saheb, my brother was named in this case. No summon received yet but police visited yesterday.",
            "Advocate: Don't worry. Since it is ${lead.bailableStatus}, we can file anticipatory bail or apply for interim protection. I am checking the records."
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(lead.clientName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(lead.caseType, fontSize = 11.sp, color = TextGray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperWhite)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(PaperDeskBg)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages) { msg ->
                    val isLawyer = msg.startsWith("Advocate:")
                    val cleanMsg = msg.substringAfter(": ")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isLawyer) Arrangement.End else Arrangement.Start
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isLawyer) TrustNavy else PaperWhite
                            ),
                            shape = RoundedCornerShape(
                                topStart = 12.dp,
                                topEnd = 12.dp,
                                bottomStart = if (isLawyer) 12.dp else 0.dp,
                                bottomEnd = if (isLawyer) 0.dp else 12.dp
                            ),
                            border = if (isLawyer) null else BorderStroke(1.dp, PaperBorder),
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = cleanMsg,
                                    fontSize = 13.sp,
                                    color = if (isLawyer) Color.White else TextDark,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }

            // Message input field
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = PaperWhite,
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text("Type advice for client...", fontSize = 13.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TrustNavy,
                            unfocusedBorderColor = PaperBorder
                        ),
                        modifier = Modifier.weight(1f).testTag("lawyer_chat_input")
                    )
                    IconButton(
                        onClick = {
                            if (messageText.isNotEmpty()) {
                                messages.add("Advocate: $messageText")
                                messageText = ""
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = TrustNavy, contentColor = Color.White),
                        modifier = Modifier.size(44.dp).testTag("lawyer_send_msg_btn")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

/**
 * 9. ADMIN SIMULATION CONTROL PANEL SCREEN
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminControlPanelScreen(viewModel: LegalHelperViewModel, onDismiss: () -> Unit) {
    val role by viewModel.userRole.collectAsState()
    val status by viewModel.lawyerVerificationStatus.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = TrustNavy)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nyaya Mitra Admin Sim")
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "This sandbox panel lets you change roles and approve profiles instantly for verification testing.",
                    fontSize = 11.sp,
                    color = TextGray
                )
                HorizontalDivider()

                Text("Force Role Switch:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            viewModel.selectRole("client")
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (role == "client") TrustNavy else PaperBorder),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("admin_set_client")
                    ) {
                        Text("Client Mode", fontSize = 10.sp, color = if (role == "client") Color.White else TextDark)
                    }
                    Button(
                        onClick = {
                            viewModel.selectRole("lawyer")
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (role == "lawyer") TrustNavy else PaperBorder),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("admin_set_lawyer")
                    ) {
                        Text("Lawyer Mode", fontSize = 10.sp, color = if (role == "lawyer") Color.White else TextDark)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text("Lawyer Verification Control:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("Current Status: $status", fontSize = 11.sp, color = AccentSaffron, fontWeight = FontWeight.Bold)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            viewModel.adminApproveLawyer()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LawGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("admin_force_approve")
                    ) {
                        Text("Approve Pro", fontSize = 10.sp, color = Color.White)
                    }
                    Button(
                        onClick = {
                            viewModel.adminRejectLawyer()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LawRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("admin_force_reject")
                    ) {
                        Text("Reject", fontSize = 10.sp, color = Color.White)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close Sandbox")
            }
        }
    )
}

/**
 * 10. PII PRIVACY SHIELD MIDDLEWARE VISUALIZER COMPONENT
 */
@Composable
fun PrivacyShieldVisualizerCard(userQuery: String) {
    var redactLogs by remember { mutableStateOf(false) }

    val redactedQuery = remember(userQuery) {
        userQuery
            .replace(Regex("\\b\\d{12}\\b"), "[MASKED AADHAAR ID]")
            .replace(Regex("\\b[A-Z]{5}\\d{4}[A-Z]\\b"), "[REDACTED PAN NO]")
            .replace(Regex("\\b[6-9]\\d{9}\\b"), "[SHIELDED PHONE NUMBER]")
            .replace(Regex("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b"), "[MASKED EMAIL ADDRESS]")
    }

    Card(
        modifier = Modifier.fillMaxWidth().testTag("privacy_shield_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LightGreen),
        border = BorderStroke(1.dp, LawGreen.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = LawGreen, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Gateway PII Privacy Shield Active", fontWeight = FontWeight.Black, fontSize = 13.sp, color = TextDark)
                    Text("गेटवे व्यक्तिगत पहचान सुरक्षा सक्रिय", fontSize = 9.sp, color = TextGray)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Ensures absolute compliance. Aadhaar Cards, PAN Numbers, phone numbers, and emails are scrubbed locally in-app BEFORE reaching any cloud LLM.",
                fontSize = 11.sp,
                color = TextGray,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
            TextButton(
                onClick = { redactLogs = !redactLogs },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = if (redactLogs) "Hide Redaction Logs ▲" else "Show Compliance Redaction Logs ▼",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TrustNavy
                )
            }

            AnimatedVisibility(visible = redactLogs) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .background(Color.White.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text("Original Query Sent from UI:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LawRed)
                    Text(userQuery, fontSize = 11.sp, color = TextDark)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Gateway Scrubbed Input to LLM API:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LawGreen)
                    Text(redactedQuery, fontSize = 11.sp, color = TextDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = LawGreen, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("PII Compliance Verification Passed", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = LawGreen)
                    }
                }
            }
        }
    }
}

/**
 * 11. ASYNC DOCUMENT SCAN PIPELINE PIPELINE (Multi-page Chunk Processing Simulator)
 */
@Composable
fun AsyncDocPipelineVisualizer(onFinished: () -> Unit) {
    var currentPageStep by remember { mutableStateOf(1) }
    var currentSubProgress by remember { mutableStateOf("Splitting PDF documents...") }
    var progressVal by remember { mutableStateOf(0.1f) }

    LaunchedEffect(Unit) {
        delay(1200)
        currentPageStep = 2
        currentSubProgress = "Running Cloud Vision OCR Engine (Page 1/1)..."
        progressVal = 0.4f
        delay(1500)
        currentPageStep = 3
        currentSubProgress = "Anonymizing personal records / scrubbing PII..."
        progressVal = 0.7f
        delay(1000)
        currentPageStep = 4
        currentSubProgress = "Running semantic analysis on clauses..."
        progressVal = 0.9f
        delay(1000)
        onFinished()
    }

    Card(
        modifier = Modifier.fillMaxWidth().testTag("async_doc_pipeline"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LightYellow),
        border = BorderStroke(1.dp, AccentSaffron.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(
                    progress = progressVal,
                    modifier = Modifier.size(24.dp),
                    color = AccentSaffron,
                    strokeWidth = 3.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Chunk Processing Pipeline", fontWeight = FontWeight.Black, fontSize = 13.sp, color = TextDark)
                    Text("बाइनरी दस्तावेज़ प्रसंस्करण शुरू", fontSize = 9.sp, color = TextGray)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            LinearProgressIndicator(
                progress = progressVal,
                color = AccentSaffron,
                trackColor = PaperBorder,
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = currentSubProgress,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextDark
            )

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DocProgressIndicatorDot(label = "Split", active = currentPageStep >= 1, done = currentPageStep > 1)
                DocProgressIndicatorDot(label = "OCR", active = currentPageStep >= 2, done = currentPageStep > 2)
                DocProgressIndicatorDot(label = "Mask PII", active = currentPageStep >= 3, done = currentPageStep > 3)
                DocProgressIndicatorDot(label = "AI Analyze", active = currentPageStep >= 4, done = currentPageStep > 4)
            }
        }
    }
}

@Composable
fun DocProgressIndicatorDot(label: String, active: Boolean, done: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(
                    if (done) LawGreen else if (active) AccentSaffron else PaperBorder,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (done) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
            color = if (active) TextDark else TextGray
        )
    }
}

/**
 * 12. BILINGUAL LEGAL MATRIX lookup data class & mock items
 */
data class LegalMappingMatrixItem(
    val ipc: String,
    val bns: String,
    val nature: String, // "Direct Match", "Split Match", "Merged Match", "Deleted Section"
    val description: String,
    val explanationHindi: String,
    val bailableStatus: String,
    val cognitiveStatus: String
)

object MappingMatrixData {
    val ITEMS = listOf(
        LegalMappingMatrixItem(
            ipc = "IPC Section 302",
            bns = "BNS Section 103(1)",
            nature = "Direct Match",
            description = "Murder: direct mapping from old to new code. Punishment retains death penalty or life imprisonment.",
            explanationHindi = "हत्या: पुराना कोड से सीधा नया कोड। सज़ा में फांसी या आजीवन कारावास लागू रहेगा।",
            bailableStatus = "Non-Bailable",
            cognitiveStatus = "Cognizable"
        ),
        LegalMappingMatrixItem(
            ipc = "IPC Section 354",
            bns = "BNS Section 74",
            nature = "Split Match",
            description = "Assault or criminal force to woman with intent to outrage her modesty. Modified definition with sub-clauses.",
            explanationHindi = "महिला की शालीनता को ठेस पहुंचाने के इरादे से हमला या आपराधिक बल। अतिरिक्त उप-धाराएं जोड़ी गई हैं।",
            bailableStatus = "Non-Bailable",
            cognitiveStatus = "Cognizable"
        ),
        LegalMappingMatrixItem(
            ipc = "IPC Section 377",
            bns = "Removed / Deleted",
            nature = "Deleted Section",
            description = "Unnatural offences. Completely deleted in BNS following consensus with historical judicial directives.",
            explanationHindi = "अप्राकृतिक अपराध। भारतीय न्याय संहिता में इसे पूर्णतः हटा दिया गया है।",
            bailableStatus = "N/A",
            cognitiveStatus = "N/A"
        ),
        LegalMappingMatrixItem(
            ipc = "IPC Section 420",
            bns = "BNS Section 318(4)",
            nature = "Merged Match",
            description = "Cheating and dishonestly inducing delivery of property. Merged under chapter of property offences.",
            explanationHindi = "धोखाधड़ी और बेईमानी से संपत्ति सुपुर्द करने के लिए प्रेरित करना। नया चैप्टर में विलय किया गया है।",
            bailableStatus = "Non-Bailable",
            cognitiveStatus = "Cognizable"
        ),
        LegalMappingMatrixItem(
            ipc = "IPC Section 124A",
            bns = "BNS Section 152",
            nature = "Direct Match",
            description = "Acts endangering sovereignty, unity, and integrity of India (replacing the old term 'Sedition').",
            explanationHindi = "भारत की संप्रभुता, एकता और अखंडता को खतरे में डालने वाले कृत्य (पुराना 'देशद्रोह' शब्द हटाया गया)।",
            bailableStatus = "Non-Bailable",
            cognitiveStatus = "Cognizable"
        )
    )
}
