package com.zero.amaraty.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zero.amaraty.R

@Composable
fun AboutScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Button(onClick = onBack) { Text(stringResource(R.string.back)) }
        Text(text = stringResource(R.string.about_title), style = MaterialTheme.typography.headlineSmall)
        Text(text = stringResource(R.string.about_local_storage), style = MaterialTheme.typography.bodyLarge)
        Text(text = stringResource(R.string.about_no_internet), style = MaterialTheme.typography.bodyLarge)
        Text(text = stringResource(R.string.about_no_accounts), style = MaterialTheme.typography.bodyLarge)
        Text(text = stringResource(R.string.about_security_note), style = MaterialTheme.typography.bodyMedium)
    }
}
