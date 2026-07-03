package com.example.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun OnboardingTourOverlay(
    step: Int,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onSkip: () -> Unit,
    onFinish: () -> Unit
) {
    // Full-screen overlay with dimmed background that consumes clicks
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable(enabled = true, onClick = {}) // block clicks to underlying views
            .testTag("onboarding_overlay_bg"),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp)
                .testTag("onboarding_tour_card"),
            colors = CardDefaults.cardColors(containerColor = PaperWhite),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with "Skip" option (for step < 4)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tour Step Indicator
                    Surface(
                        color = TrustNavy.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Text(
                            text = "STEP ${step + 1} OF 5",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TrustNavy,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    if (step < 4) {
                        TextButton(
                            onClick = onSkip,
                            colors = ButtonDefaults.textButtonColors(contentColor = TextGray),
                            modifier = Modifier.testTag("onboarding_skip_button")
                        ) {
                            Text("Skip Tour", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Interactive Content with smooth crossfades
                Crossfade(targetState = step) { currentStep ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        when (currentStep) {
                            0 -> WelcomeStep()
                            1 -> AskQuestionStep()
                            2 -> AnalyzeDocStep()
                            3 -> FindLawyerStep()
                            4 -> FinishStep()
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Progress Dots Indicator
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0..4) {
                        Box(
                            modifier = Modifier
                                .size(if (i == step) 16.dp else 8.dp, 8.dp)
                                .background(
                                    color = if (i == step) TrustNavy else PaperBorder,
                                    shape = CircleShape
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Navigation Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (step > 0) Arrangement.SpaceBetween else Arrangement.Center
                ) {
                    if (step > 0) {
                        OutlinedButton(
                            onClick = onPrev,
                            border = BorderStroke(1.dp, PaperBorder),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 6.dp)
                                .testTag("onboarding_back_button")
                        ) {
                            Text("Back", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        }
                    }

                    val buttonModifier = if (step > 0) Modifier.weight(1f).padding(start = 6.dp) else Modifier.fillMaxWidth(0.8f)
                    
                    if (step < 4) {
                        Button(
                            onClick = onNext,
                            colors = ButtonDefaults.buttonColors(containerColor = TrustNavy),
                            shape = RoundedCornerShape(12.dp),
                            modifier = buttonModifier.testTag("onboarding_next_button")
                        ) {
                            Text("Next Step", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    } else {
                        Button(
                            onClick = onFinish,
                            colors = ButtonDefaults.buttonColors(containerColor = LawGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = buttonModifier.testTag("onboarding_finish_button")
                        ) {
                            Text("Let's Explore!", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeStep() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(TrustNavy.copy(alpha = 0.08f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "न",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = TrustNavy
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "नमस्ते, Welcome!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "न्याय साथी (AI Legal Helper) is your simple, offline-first conversational guide to Indian laws. We explain complicated legal terms in plain English and Hindi.",
            fontSize = 13.sp,
            color = TextGray,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AskQuestionStep() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(Color(0xFFEEF2F6), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Layers,
                contentDescription = null,
                tint = Color(0xFF6366F1),
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "💬 Ask Legal Question",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Have an issue, police notice, or want to know about a specific FIR section? Describe it in English, Hindi, or Hinglish.",
            fontSize = 13.sp,
            color = TextGray,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            color = Color(0xFFF8FAFC),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, PaperBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "💡 HOW TO USE:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6366F1)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tap 'Ask AI Legal Assistant' on the home screen, type your question, and get bilingual sections, bailable status, and step-by-step checklists.",
                    fontSize = 11.sp,
                    color = TextDark,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun AnalyzeDocStep() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(Color(0xFFFFFBEB), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.DocumentScanner,
                contentDescription = null,
                tint = AccentSaffron,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "📄 Analyze Documents",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Analyze rental deeds, legal notices, or contracts. Our AI extracts and summarizes clauses, identifying hidden risks and obligations.",
            fontSize = 13.sp,
            color = TextGray,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            color = Color(0xFFF8FAFC),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, PaperBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "💡 HOW TO USE:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentSaffron
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tap 'Analyze Legal Document', upload/snap a photo of the document, and see the risk assessment and legal cross-references instantly.",
                    fontSize = 11.sp,
                    color = TextDark,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun FindLawyerStep() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(Color(0xFFECFDF5), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Gavel,
                contentDescription = null,
                tint = LawGreen,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "⚖️ Find Verified Lawyers",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Need professional representation? Connect directly with verified, vetted lawyers categorized by expertise and location.",
            fontSize = 13.sp,
            color = TextGray,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            color = Color(0xFFF8FAFC),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, PaperBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "💡 HOW TO USE:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = LawGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tap 'Find Verified Lawyer' to browse certified professionals, view their ratings/reviews, and book direct consult sessions securely.",
                    fontSize = 11.sp,
                    color = TextDark,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun FinishStep() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(Color(0xFFEEF2F6), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Explore,
                contentDescription = null,
                tint = TrustNavy,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "🚀 Ready to Explore!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "You are fully equipped to navigate Indian legal waters. Start asking questions, scanning documents, or scheduling expert consultations securely.",
            fontSize = 13.sp,
            color = TextGray,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "💡 Tip: Tap the Help (?) icon in the top right at any time to replay this quick tour.",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TrustNavy,
            textAlign = TextAlign.Center
        )
    }
}
