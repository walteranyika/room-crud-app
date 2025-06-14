package com.walter.localdbapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.github.javafaker.Faker
import com.walter.localdbapp.db.Person
import com.walter.localdbapp.db.PersonDatabase
import com.walter.localdbapp.ui.theme.LocalDbAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LocalDbAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                    TopAppBar(title = { Text("Add User") })
                }) { innerPadding ->
                    RegForm(innerPadding)
                }
            }
        }
    }
}

@Composable
fun RegForm(innerPadding: PaddingValues) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var age by remember { mutableIntStateOf(0) }
    var weight by remember { mutableDoubleStateOf(0.0) }

    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    val faker = Faker()
    val db = Room.databaseBuilder(context, PersonDatabase::class.java, "person_db").build()
    val personDao = db.personDao()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 12.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") })
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") })
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = age.toString(),
            onValueChange = { age = it.toIntOrNull() ?: 0 },
            label = { Text("Age") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = weight.toString(),
            onValueChange = { weight = it.toDoubleOrNull() ?: 0.0 },
            label = { Text("Weight") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(onClick = {
            if (firstName.isNotBlank() && lastName.isNotBlank() && email.isNotBlank() && age > 0 && weight > 0) {
                coroutineScope.launch {
                    val p = Person(
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        age = age,
                        weight = weight.toInt()
                    )
                    personDao.insertPerson(p)
                    firstName = ""
                    lastName = ""
                    email = ""
                    age = 0
                    weight = 0.0
                }
            } else {
                firstName = faker.name().firstName()
                lastName = faker.name().lastName()
                email = faker.internet().emailAddress()
                age = faker.number().numberBetween(18, 65)
                weight = faker.number().numberBetween(30, 100).toDouble()
            }
        }, modifier = Modifier.fillMaxWidth()) { Text("Register") }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(onClick = {
            context.startActivity(Intent(context, PeopleActivity::class.java))
        }, modifier = Modifier.fillMaxWidth()) { Text("View Users") }

    }

}