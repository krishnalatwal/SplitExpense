package com.example.splitexpense.ui

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.splitexpense.ui.screens.*
import com.example.splitexpense.ui.viewmodel.ExpenseViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(viewModel: ExpenseViewModel = viewModel()) {
    val navController = rememberNavController()
    val groups by viewModel.groups.collectAsState()
    val members by viewModel.currentMembers.collectAsState()
    val expenses by viewModel.currentExpenses.collectAsState()
    val currentGroupId by viewModel.currentGroupId.collectAsState()

    val scope = rememberCoroutineScope()
    var balances by remember { mutableStateOf(emptyList<com.example.splitexpense.ui.viewmodel.BalanceInfo>()) }
    var settlements by remember { mutableStateOf(emptyList<com.example.splitexpense.ui.viewmodel.Settlement>()) }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                groups = groups,
                onGroupClick = { groupId ->
                    viewModel.selectGroup(groupId)
                    scope.launch {
                        balances = viewModel.calculateBalances(groupId)
                    }
                    navController.navigate("groupDetails")
                },
                onCreateGroup = { navController.navigate("createGroup") }
            )
        }

        composable("createGroup") {
            CreateGroupScreen(
                onBack = { navController.popBackStack() },
                onCreate = { name, memberNames ->
                    viewModel.createGroup(name, memberNames)
                    navController.popBackStack()
                }
            )
        }

        composable("groupDetails") {
            val currentGroup = groups.find { it.id == currentGroupId }
            GroupDetailsScreen(
                group = currentGroup,
                members = members,
                expenses = expenses,
                balances = balances,
                onBack = { navController.popBackStack() },
                onAddExpense = { navController.navigate("addExpense") },
                onExport = {
                    settlements = viewModel.calculateSettlements(balances)
                    navController.navigate("settleUp")
                }
            )
        }

        composable("addExpense") {
            val currentGroup = groups.find { it.id == currentGroupId }
            AddExpenseScreen(
                members = members,
                isPersonal = currentGroup?.isPinned == true,
                onBack = { navController.popBackStack() },
                onAdd = { title, amount, payer, participants ->
                    currentGroupId?.let { groupId ->
                        viewModel.addExpense(groupId, title, amount, payer, participants)
                    }
                    navController.popBackStack()
                }
            )
        }

        composable("settleUp") {
            SettleUpScreen(
                settlements = settlements,
                onBack = { navController.popBackStack() },
                onExportCSV = {
                    // TODO: Implement CSV export
                }
            )
        }
    }
}