package com.zero.amaraty.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.zero.amaraty.R
import com.zero.amaraty.domain.money.MoneyFormatter
import com.zero.amaraty.domain.money.MoneyInputError
import com.zero.amaraty.domain.money.MoneyParseResult
import com.zero.amaraty.domain.money.MoneyParser

@Composable
fun MoneyToolScreen(
    useArabicDigits: Boolean,
    onBack: () -> Unit,
) {
    var amountText by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<MoneyParseResult?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Button(onClick = onBack) { Text(stringResource(R.string.back)) }
        Text(text = stringResource(R.string.money_tool_title), style = MaterialTheme.typography.headlineSmall)
        Text(text = stringResource(R.string.money_tool_intro), style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = amountText,
            onValueChange = {
                amountText = it
                result = null
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.amount_label)) },
            placeholder = { Text(stringResource(R.string.amount_placeholder)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        )
        Button(
            onClick = { result = MoneyParser.parse(amountText) },
            enabled = amountText.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.check_amount))
        }
        when (val parsed = result) {
            is MoneyParseResult.Success -> {
                Text(stringResource(R.string.amount_valid), color = MaterialTheme.colorScheme.primary)
                Text(
                    text = MoneyFormatter.format(parsed.money, useArabicDigits = useArabicDigits),
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(
                    text = stringResource(R.string.amount_piasters, parsed.money.piasters),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            is MoneyParseResult.Failure -> {
                Text(
                    text = stringResource(errorMessage(parsed.reason)),
                    color = MaterialTheme.colorScheme.error,
                )
            }
            null -> Unit
        }
    }
}

private fun errorMessage(error: MoneyInputError): Int = when (error) {
    MoneyInputError.EMPTY -> R.string.money_error_empty
    MoneyInputError.INVALID_CHARACTER -> R.string.money_error_character
    MoneyInputError.NEGATIVE_NOT_ALLOWED -> R.string.money_error_negative
    MoneyInputError.INVALID_GROUPING -> R.string.money_error_grouping
    MoneyInputError.AMBIGUOUS_SEPARATOR -> R.string.money_error_ambiguous
    MoneyInputError.TOO_MANY_DECIMALS -> R.string.money_error_decimals
    MoneyInputError.OVERFLOW -> R.string.money_error_overflow
}
