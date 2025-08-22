package com.example.splitwiseclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.splitwiseclone.ui.theme.SplitwiseCloneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplitwiseCloneTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var groups by remember { mutableStateOf(sampleGroups) }

    NavHost(navController = navController, startDestination = "groups_screen") {
        composable("groups_screen") {
            GroupsScreen(
                navController = navController,
                groups = groups,
                onAddGroup = { newGroup -> groups = groups + newGroup }
            )
        }
        composable(
            route = "group_details_screen/{groupId}",
            arguments = listOf(navArgument("groupId") { type = NavType.IntType })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getInt("groupId") ?: 0
            val group = groups.find { it.id == groupId }
            if (group != null) {
                GroupDetailsScreen(
                    navController = navController,
                    group = group,
                    onAddExpense = { newExpense ->
                        val updatedGroups = groups.map {
                            if (it.id == groupId) {
                                it.copy(expenses = it.expenses + newExpense)
                            } else {
                                it
                            }
                        }
                        groups = updatedGroups
                    }
                )
            } else {
                Text("Group not found")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
    navController: NavController,
    groups: List<Group>,
    onAddGroup: (Group) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Groups") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(groups) { group ->
                GroupListItem(group = group, navController = navController)
            }
        }
    }

    if (showDialog) {
        var groupName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Create New Group") },
            text = {
                OutlinedTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    label = { Text("Group Name") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (groupName.isNotBlank()) {
                            val newGroup = Group(
                                id = (groups.maxOfOrNull { it.id } ?: 0) + 1,
                                name = groupName,
                                members = emptyList(), // Simplified for now
                                expenses = emptyList()
                            )
                            onAddGroup(newGroup)
                            showDialog = false
                        }
                    }
                ) { Text("Create") }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun GroupListItem(group: Group, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("group_details_screen/${group.id}")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = group.name, fontWeight = FontWeight.Bold)
            Text(text = "${group.members.size} members")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailsScreen(
    navController: NavController,
    group: Group,
    onAddExpense: (Expense) -> Unit
) {
    var showAddExpenseDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(group.name) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddExpenseDialog = true }) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Members", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    group.members.forEach { member ->
                        Text(member.name, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Expenses", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(group.expenses) { expense ->
                    ExpenseListItem(expense = expense, members = group.members)
                }
            }
        }

        if (showAddExpenseDialog) {
            AddExpenseDialog(
                group = group,
                onDismiss = { showAddExpenseDialog = false },
                onConfirm = { newExpense ->
                    onAddExpense(newExpense)
                    showAddExpenseDialog = false
                }
            )
        }
    }
}

@Composable
fun ExpenseListItem(expense: Expense, members: List<Member>) {
    val paidByMember = members.find { it.id == expense.paidByMemberId }?.name ?: "Unknown"
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(expense.description, fontWeight = FontWeight.Bold)
                Text("Paid by: $paidByMember")
            }
            Text(
                "₹${expense.amount}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseDialog(
    group: Group,
    onDismiss: () -> Unit,
    onConfirm: (Expense) -> Unit
) {
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var paidByMemberId by remember { mutableStateOf<Int?>(null) }
    var splitAmongMemberIds by remember { mutableStateOf(setOf<Int>()) }
    var isPaidByDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Expense") },
        text = {
            LazyColumn {
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                }
                item {
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Amount") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                }
                item {
                    ExposedDropdownMenuBox(
                        expanded = isPaidByDropdownExpanded,
                        onExpandedChange = { isPaidByDropdownExpanded = !isPaidByDropdownExpanded },
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        OutlinedTextField(
                            value = group.members.find { it.id == paidByMemberId }?.name ?: "Select who paid",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Paid By") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isPaidByDropdownExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = isPaidByDropdownExpanded,
                            onDismissRequest = { isPaidByDropdownExpanded = false }
                        ) {
                            group.members.forEach { member ->
                                DropdownMenuItem(
                                    text = { Text(member.name) },
                                    onClick = {
                                        paidByMemberId = member.id
                                        isPaidByDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                item {
                    Text("Split Among", style = MaterialTheme.typography.titleMedium)
                }
                items(group.members) { member ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().clickable {
                            val currentIds = splitAmongMemberIds.toMutableSet()
                            if (member.id in currentIds) {
                                currentIds.remove(member.id)
                            } else {
                                currentIds.add(member.id)
                            }
                            splitAmongMemberIds = currentIds
                        }
                    ) {
                        Checkbox(checked = member.id in splitAmongMemberIds, onCheckedChange = null)
                        Text(member.name)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amountDouble = amount.toDoubleOrNull()
                    if (description.isNotBlank() && amountDouble != null && paidByMemberId != null && splitAmongMemberIds.isNotEmpty()) {
                        val newExpense = Expense(
                            id = (group.expenses.maxOfOrNull { it.id } ?: 0) + 100, // High number to avoid collision
                            description = description,
                            amount = amountDouble,
                            paidByMemberId = paidByMemberId!!,
                            splitAmongMemberIds = splitAmongMemberIds.toList()
                        )
                        onConfirm(newExpense)
                    }
                }
            ) { Text("Save") }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Cancel") }
        }
    )
}