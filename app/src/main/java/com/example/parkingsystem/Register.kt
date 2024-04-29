package com.example.parkingsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun RegisterScreen(
    onLogin: (String, String) -> Unit,
    navController: NavController
) {
    // Define state variables for username and password inputs
    val fName = remember { mutableStateOf("") }
    val lName = remember { mutableStateOf("") }
    val address = remember { mutableStateOf("") }
    val pNumber = remember { mutableStateOf("") }

    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    // Create the login screen layout
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(Modifier.padding(16.dp)){

            Row(){
                OutlinedTextField(
                    value = fName.value,
                    onValueChange = {  fName.value = it },
                    label = { Text("First Name") },
                    modifier = Modifier.padding(bottom = 8.dp).weight(1f)
                )

                OutlinedTextField(
                    value = lName.value,
                    onValueChange = {  lName.value = it },
                    label = { Text("Last Name") },
                    modifier = Modifier.padding(bottom = 8.dp).weight(1f)
                )
            }

            Row(){
                OutlinedTextField(
                    value = address.value,
                    onValueChange = { address.value = it },
                    label = { Text("Address") },
                    modifier = Modifier.padding(bottom = 8.dp).weight(1f)
                )

                OutlinedTextField(
                    value = pNumber.value,
                    onValueChange = { pNumber.value = it },
                    label = { Text("Mobile Number (e.g. 09** *** ****") },
                    modifier = Modifier.padding(bottom = 8.dp).weight(1f)
                )
            }
        }
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Username input field
            OutlinedTextField(
                value = username.value,
                onValueChange = { username.value = it },
                label = { Text("Username") },
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Password input field
            OutlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Login button
            Button(
                onClick = {
                    // Handle the login action
                    onLogin(username.value, password.value)
                    navController.navigate(ParkingAppScreen.MapView.name)
                }
            ) {
                Text("Login")
            }
        }
    }
}
