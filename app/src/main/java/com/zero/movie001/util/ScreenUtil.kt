package com.zero.movie001.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// NOTE: LocalResources 在新版本中被移除，使用LocalContext.current.resources代替
@Composable
private fun screenWidthPx() = LocalContext.current.resources.displayMetrics.widthPixels

@Composable
private fun screenWidthDp() = LocalConfiguration.current.screenWidthDp

@Composable
private fun density() = LocalDensity.current.density

private const val DesignWidth = 390f

@Composable
private fun ratioPx() = screenWidthPx() / DesignWidth

@Composable
private fun ratioDp() = screenWidthDp() / DesignWidth

@Composable
fun Float.px() = (this * ratioPx() / density()).dp

@Composable
fun Int.px() = this.toFloat().px()

@Composable
fun Float.realPx() = (this * ratioDp()).dp

@Composable
fun Int.realPx() = this.toFloat().realPx()

@Composable
fun Float.textPx() = (this * ratioPx() / density()).sp

@Composable
fun Int.textPx() = this.toFloat().textPx()