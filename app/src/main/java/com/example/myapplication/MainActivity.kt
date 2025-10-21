package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme

data class Contact(
    val id: Int,
    val name: String,
    val address: String,
    val phoneNumber: String = "",
    val email: String = ""
)

object Destinations {
    const val CONTACT_LIST_SCREEN = "contactList"
    const val ADD_CONTACT_SCREEN = "addContact"
}

val initialContacts = mutableStateListOf(
    Contact(1, "natan", "jember", "081389732", "nath@example.com"),
    Contact(2, "azam", "jakarta", "949104", "reoa@example.com"),
    Contact(3, "raja", "banten", "892737", "ncaud@example.com")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destinations.CONTACT_LIST_SCREEN,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destinations.CONTACT_LIST_SCREEN) {
                ContactListScreen(navController = navController, contacts = initialContacts)
            }

            composable(Destinations.ADD_CONTACT_SCREEN) {
                AddContactScreen(navController = navController)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactListScreen(navController: NavController, contacts: List<Contact>) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Contact") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Destinations.ADD_CONTACT_SCREEN)
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Contact")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(contacts) { contact ->
                ContactListItem(contact = contact)
            }
        }
    }
}

@Composable
fun ContactListItem(contact: Contact) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle item click here */ }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = contact.name,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = contact.address,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    Divider()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactScreen(navController: NavController) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Contact") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            ContactInputField(name, { name = it }, "Name", KeyboardType.Text, ImeAction.Next)
            Spacer(modifier = Modifier.height(16.dp))

            ContactInputField(address, { address = it }, "Address", KeyboardType.Text, ImeAction.Next)
            Spacer(modifier = Modifier.height(16.dp))

            ContactInputField(phone, { phone = it }, "Phone", KeyboardType.Phone, ImeAction.Next)
            Spacer(modifier = Modifier.height(16.dp))

            ContactInputField(email, { email = it }, "Email", KeyboardType.Email, ImeAction.Done)
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (name.isBlank() || phone.isBlank()) {
                        Toast.makeText(context, "Name and Phone are required.", Toast.LENGTH_SHORT).show()
                    } else {
                        val newContact = Contact(
                            id = initialContacts.size + 1,
                            name = name.trim(),
                            address = address.trim(),
                            phoneNumber = phone.trim(),
                            email = email.trim()
                        )

                        initialContacts.add(newContact)

                        Toast.makeText(context, "Contact Saved: ${newContact.name}", Toast.LENGTH_LONG).show()
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Contact")
            }
        }
    }
}

@Composable
fun ContactInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType,
    imeAction: ImeAction
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun ContactListScreenPreview() {
    val minimalPreviewContacts = listOf(
        Contact(1, "Preview User 1", "123 Short Address Line 1"),
        Contact(2, "Preview User 2", "456 Longer Preview Address Line 2"),
    )

    MyApplicationTheme {
        ContactListScreen(
            navController = rememberNavController(),
            contacts = minimalPreviewContacts
        )
    }
}