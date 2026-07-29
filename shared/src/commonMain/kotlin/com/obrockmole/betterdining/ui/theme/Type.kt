package com.obrockmole.betterdining.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun getTypography(): Typography {
    val acuminPro = getAcuminProFontFamily()

    return Typography(
        /* Normal fonts */
        bodySmall = TextStyle(
            fontFamily = acuminPro,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.sp
        ),

        bodyMedium = TextStyle(
            fontFamily = acuminPro,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.sp
        ),

        bodyLarge = TextStyle(
            fontFamily = acuminPro,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.sp
        ),

        titleSmall = TextStyle(
            fontFamily = acuminPro,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.sp
        ),

        titleMedium = TextStyle(
            fontFamily = acuminPro,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.sp
        ),

        titleLarge = TextStyle(
            fontFamily = acuminPro,
            fontWeight = FontWeight.Normal,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        ),

        headlineSmall = TextStyle(
            fontFamily = acuminPro,
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            letterSpacing = 0.sp
        ),

        headlineMedium = TextStyle(
            fontFamily = acuminPro,
            fontWeight = FontWeight.Normal,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            letterSpacing = 0.sp
        ),

        headlineLarge = TextStyle(
            fontFamily = acuminPro,
            fontWeight = FontWeight.Normal,
            fontSize = 32.sp,
            lineHeight = 40.sp,
            letterSpacing = 0.sp
        ),

        displaySmall = TextStyle(
            fontFamily = acuminPro,
            fontWeight = FontWeight.Normal,
            fontSize = 36.sp,
            lineHeight = 44.sp,
            letterSpacing = 0.sp
        ),

        displayMedium = TextStyle(
            fontFamily = acuminPro,
            fontWeight = FontWeight.Normal,
            fontSize = 45.sp,
            lineHeight = 52.sp,
            letterSpacing = 0.sp
        ),

        displayLarge = TextStyle(
            fontFamily = acuminPro,
            fontWeight = FontWeight.Normal,
            fontSize = 57.sp,
            lineHeight = 64.sp,
            letterSpacing = 0.sp
        ),


        /* Emphasized fonts */
        bodySmallEmphasized = TextStyle(
            fontFamily = acuminPro,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.sp
        ),

        bodyMediumEmphasized = TextStyle(
            fontFamily = acuminPro,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.sp
        ),

        bodyLargeEmphasized = TextStyle(
            fontFamily = acuminPro,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.sp
        ),

        titleSmallEmphasized = TextStyle(
            fontFamily = acuminPro,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.sp
        ),

        titleMediumEmphasized = TextStyle(
            fontFamily = acuminPro,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.sp
        ),

        titleLargeEmphasized = TextStyle(
            fontFamily = acuminPro,
            fontWeight = FontWeight.Medium,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        ),

        headlineSmallEmphasized = TextStyle(
            fontFamily = acuminPro,
            fontWeight = FontWeight.Medium,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            letterSpacing = 0.sp
        ),

        headlineMediumEmphasized = TextStyle(
            fontFamily = acuminPro,
            fontWeight = FontWeight.Medium,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            letterSpacing = 0.sp
        ),

        headlineLargeEmphasized = TextStyle(
            fontFamily = acuminPro,
            fontWeight = FontWeight.Medium,
            fontSize = 32.sp,
            lineHeight = 40.sp,
            letterSpacing = 0.sp
        ),

        displaySmallEmphasized = TextStyle(
            fontFamily = acuminPro,
            fontWeight = FontWeight.Medium,
            fontSize = 36.sp,
            lineHeight = 44.sp,
            letterSpacing = 0.sp
        ),

        displayMediumEmphasized = TextStyle(
            fontFamily = acuminPro,
            fontWeight = FontWeight.Medium,
            fontSize = 45.sp,
            lineHeight = 52.sp,
            letterSpacing = 0.sp
        ),

        displayLargeEmphasized = TextStyle(
            fontFamily = acuminPro,
            fontWeight = FontWeight.Medium,
            fontSize = 57.sp,
            lineHeight = 64.sp,
            letterSpacing = 0.sp
        )
    )
}