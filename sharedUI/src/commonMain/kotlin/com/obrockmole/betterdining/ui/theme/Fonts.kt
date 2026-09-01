package com.obrockmole.betterdining.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import betterpurduedining.sharedui.generated.resources.*
import org.jetbrains.compose.resources.Font

@Composable
fun getAcuminProFontFamily() = FontFamily(
    Font(Res.font.acumin_pro_400_italic, FontWeight.Normal, FontStyle.Italic),
    Font(Res.font.acumin_pro_400_normal, FontWeight.Normal, FontStyle.Normal),
    Font(Res.font.acumin_pro_500_italic, FontWeight.Medium, FontStyle.Italic),
    Font(Res.font.acumin_pro_500_normal, FontWeight.Medium, FontStyle.Normal),
    Font(Res.font.acumin_pro_600_italic, FontWeight.SemiBold, FontStyle.Italic),
    Font(Res.font.acumin_pro_600_normal, FontWeight.SemiBold, FontStyle.Normal),
    Font(Res.font.acumin_pro_700_italic, FontWeight.Bold, FontStyle.Italic),
    Font(Res.font.acumin_pro_700_normal, FontWeight.Bold, FontStyle.Normal)
)