package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LawGreen
import com.example.ui.theme.LawRed
import com.example.ui.theme.LightGreen
import com.example.ui.theme.LightRed

/**
 * Clean bilingual text component displaying English primarily and Hindi secondary.
 * Respects visual weight parity by making Hindi text slightly smaller.
 */
@Composable
fun BilingualText(
    english: String,
    hindi: String,
    modifier: Modifier = Modifier,
    englishWeight: FontWeight = FontWeight.Bold,
    englishSize: Int = 16,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(modifier = modifier) {
        Text(
            text = english,
            fontSize = englishSize.sp,
            fontWeight = englishWeight,
            color = textColor,
            lineHeight = (englishSize + 6).sp
        )
        if (hindi.isNotEmpty()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = hindi,
                fontSize = (englishSize * 0.85).sp, // 15% smaller for parity
                fontWeight = FontWeight.Normal,
                color = textColor.copy(alpha = 0.75f),
                lineHeight = (englishSize * 1.2).sp
            )
        }
    }
}

/**
 * Status Badge for legal tags like Bailable / Non-Bailable
 */
@Composable
fun StatusBadge(
    labelEnglish: String,
    labelHindi: String,
    isPositive: Boolean, // green if true, red if false
    modifier: Modifier = Modifier
) {
    val bgColor = if (isPositive) LightGreen else LightRed
    val textColor = if (isPositive) LawGreen else LawRed

    Box(
        modifier = modifier
            .background(bgColor, shape = RoundedCornerShape(24.dp))
            .border(1.dp, textColor.copy(alpha = 0.3f), shape = RoundedCornerShape(24.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(textColor, shape = RoundedCornerShape(50))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = labelEnglish,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Text(
                    text = labelHindi,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}

/**
 * Persistent, non-dismissible Disclaimer card at the top/bottom of legal analysis
 */
@Composable
fun LegalDisclaimerBanner(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("disclaimer_banner"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Legal Disclaimer",
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Disclaimer: General Legal Information Only",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "This application provides AI-powered guidance and legal explanation based on IPC/BNS. This does NOT constitute formal legal advice. Please consult a qualified lawyer for actionable advice on your case.",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "अस्वीकरण: केवल सामान्य कानूनी जानकारी",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "यह ऐप केवल AI द्वारा संचालित कानूनी समझ प्रदान करता है। यह कानूनी सलाह नहीं है। उचित समाधान के लिए कृपया किसी वकील से परामर्श लें।",
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 13.sp
                )
            }
        }
    }
}
