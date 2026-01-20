package com.example.splitexpense.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.splitexpense.data.entity.Member

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    members: List<Member>,
    isPersonal: Boolean,
    onBack: () -> Unit,
    onAdd: (String, Double, String, List<String>) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedPayer by remember { mutableStateOf(members.firstOrNull()?.id ?: "") }
    var selectedParticipants by remember {
        mutableStateOf(if (isPersonal) setOf(members.first().id) else emptySet<String>())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Expense") },
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
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth()
            )

            if (!isPersonal) {
                Text("Paid by", style = MaterialTheme.typography.titleSmall)
                members.forEach { member ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedPayer == member.id,
                            onClick = { selectedPayer = member.id }
                        )
                        Text(member.name)
                    }
                }

                Text("Split between", style = MaterialTheme.typography.titleSmall)
                members.forEach { member ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedParticipants.contains(member.id),
                            onCheckedChange = { checked ->
                                selectedParticipants = if (checked) {
                                    selectedParticipants + member.id
                                } else {
                                    selectedParticipants - member.id
                                }
                            }
                        )
                        Text(member.name)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val amountDouble = amount.toDoubleOrNull()
                    if (title.isNotBlank() && amountDouble != null && amountDouble > 0) {
                        val participants = if (isPersonal) {
                            listOf(members.first().id)
                        } else {
                            selectedParticipants.toList()
                        }
                        if (participants.isNotEmpty()) {
                            onAdd(title, amountDouble, selectedPayer, participants)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && amount.toDoubleOrNull() != null &&
                        (isPersonal || selectedParticipants.isNotEmpty())
            ) {
                Text("Add Expense")
            }
        }
    }
}