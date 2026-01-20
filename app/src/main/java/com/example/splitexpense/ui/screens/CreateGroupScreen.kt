package com.example.splitexpense.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupScreen(
    onBack: () -> Unit,
    onCreate: (String, List<String>) -> Unit
) {
    var groupName by remember { mutableStateOf("") }
    var memberNames by remember { mutableStateOf(listOf("")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Group") },
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
                value = groupName,
                onValueChange = { groupName = it },
                label = { Text("Group Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Members", style = MaterialTheme.typography.titleMedium)

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(memberNames) { index, name ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { newName ->
                                memberNames = memberNames.toMutableList().apply {
                                    this[index] = newName
                                }
                            },
                            label = { Text("Member ${index + 1}") },
                            modifier = Modifier.weight(1f)
                        )
                        if (memberNames.size > 1) {
                            IconButton(onClick = {
                                memberNames = memberNames.toMutableList().apply {
                                    removeAt(index)
                                }
                            }) {
                                Icon(Icons.Default.Delete, "Remove")
                            }
                        }
                    }
                }
            }

            Button(
                onClick = { memberNames = memberNames + "" },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(8.dp))
                Text("Add Member")
            }

            Button(
                onClick = {
                    val validMembers = memberNames.filter { it.isNotBlank() }
                    if (groupName.isNotBlank() && validMembers.isNotEmpty()) {
                        onCreate(groupName, validMembers)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = groupName.isNotBlank() && memberNames.any { it.isNotBlank() }
            ) {
                Text("Create Group")
            }
        }
    }
}