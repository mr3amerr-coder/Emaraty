package com.zero.amaraty.ui

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.runtime.CompositionLocalProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zero.amaraty.SettingsViewModel
import com.zero.amaraty.ui.navigation.AboutRoute
import com.zero.amaraty.ui.navigation.DashboardRoute
import com.zero.amaraty.ui.navigation.MoneyToolRoute
import com.zero.amaraty.ui.navigation.SettingsRoute
import com.zero.amaraty.ui.screens.AboutScreen
import com.zero.amaraty.ui.screens.DashboardScreen
import com.zero.amaraty.ui.screens.MoneyToolScreen
import com.zero.amaraty.ui.screens.SettingsScreen
import com.zero.amaraty.ui.theme.AmaratyTheme

@Composable
fun AmaratyApp() {
    val viewModel: SettingsViewModel = hiltViewModel()
    val settings by viewModel.settings.collectAsState()
    val failure by viewModel.failure.collectAsState()
    val navController = rememberNavController()
    val context = LocalContext.current

    LaunchedEffect(settings.secureScreens) {
        val window = (context as? Activity)?.window ?: return@LaunchedEffect
        if (settings.secureScreens) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    AmaratyTheme(darkTheme = settings.darkTheme) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background,
            ) {
                NavHost(navController = navController, startDestination = DashboardRoute) {
                    composable<DashboardRoute> {
                        DashboardScreen(
                            onMoneyTool = { navController.navigate(MoneyToolRoute) },
                            onSettings = { navController.navigate(SettingsRoute) },
                            onAbout = { navController.navigate(AboutRoute) },
                        )
                    }
                    composable<MoneyToolRoute> {
                        MoneyToolScreen(
                            useArabicDigits = settings.useArabicDigits,
                            onBack = { navController.popBackStack() },
                        )
                    }
                    composable<SettingsRoute> {
                        SettingsScreen(
                            settings = settings,
                            failure = failure,
                            onClearFailure = viewModel::clearFailure,
                            onArabicDigitsChanged = viewModel::setArabicDigits,
                            onDarkThemeChanged = viewModel::setDarkTheme,
                            onAppLockChanged = viewModel::setAppLock,
                            onSecureScreensChanged = viewModel::setSecureScreens,
                            onBack = { navController.popBackStack() },
                        )
                    }
                    composable<AboutRoute> {
                        AboutScreen(onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}

@Composable
fun LockScreen(
    message: String,
    actionLabel: String?,
    onUnlock: () -> Unit,
) {
    AmaratyTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            ) {
                androidx.compose.material3.Text(
                    text = androidx.compose.ui.res.stringResource(com.zero.amaraty.R.string.app_name),
                    style = MaterialTheme.typography.headlineLarge,
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))
                androidx.compose.material3.Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))
                if (actionLabel != null) {
                    androidx.compose.material3.Button(onClick = onUnlock) {
                        androidx.compose.material3.Text(actionLabel)
                    }
                }
            }
        }
    }
}
