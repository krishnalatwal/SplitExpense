package com.example.splitexpense.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.splitexpense.ui.viewmodel.Settlement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettleUpScreen(
    settlements: List<Settlement>,
    onBack: () -> Unit,
    onExportCSV: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settle Up") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (settlements.isEmpty()) {
                Text("All settled up! 🎉", style = MaterialTheme.typography.titleMedium)
            } else {
                Text("Suggested Payments", style = MaterialTheme.typography.titleMedium)

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(settlements) { settlement ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${settlement.from} → ${settlement.to}")
                                Text("₹%.2f".format(settlement.amount))
                            }
                        }
                    }
                }
            }

            Button(
                onClick = onExportCSV,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Export CSV")
            }
        }
    }
}