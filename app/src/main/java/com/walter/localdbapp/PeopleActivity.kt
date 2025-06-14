package com.walter.localdbapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.walter.localdbapp.db.Person
import com.walter.localdbapp.db.PersonDatabase
import com.walter.localdbapp.ui.theme.LocalDbAppTheme
import kotlinx.coroutines.launch

class PeopleActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LocalDbAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                    TopAppBar(title = { Text("Show Users") })
                }) { innerPadding ->
                    UsersList(innerPadding)
                }
            }
        }
    }
}

@Composable
fun UsersList(innerPadding: PaddingValues) {
    var users by remember { mutableStateOf<List<Person>>(emptyList()) }
    val context = LocalContext.current
    val db = remember {
        Room.databaseBuilder(
            context.applicationContext,
            PersonDatabase::class.java,
            "person_db"
        ).build()
    }
    val usersDao = remember { db.personDao() }
    val coroutineScope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var userToDelete by remember { mutableStateOf<Person?>(null) }

    LaunchedEffect(Unit) {
        users = usersDao.getAllPersons()
    }

    if (showDialog && userToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                userToDelete = null
            },
            title = { Text("Delete User") },
            text = { Text("Are you sure you want to delete ${userToDelete!!.firstName} ${userToDelete!!.lastName}?") },
            confirmButton = {
                TextButton(onClick = {
                    coroutineScope.launch {
                        usersDao.deletePersonById(userToDelete!!.id)
                        users = usersDao.getAllPersons()
                        showDialog = false
                        userToDelete = null
                        Toast.makeText(context, "User deleted", Toast.LENGTH_SHORT).show()
                    }

                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    userToDelete = null
                }) { Text("Cancel") }
            },
            modifier = Modifier.padding(16.dp)
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 12.dp)
    ) {
        items(items = users, itemContent = { user ->
            Text("${user.firstName} ${user.lastName}", fontWeight = FontWeight.Bold)
            Text(user.email)
            Text("${user.age}")
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${user.weight}")
                IconButton(modifier = Modifier.height(12.dp), onClick = {
                    userToDelete = user
                    showDialog = true
                }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
        })
    }

}