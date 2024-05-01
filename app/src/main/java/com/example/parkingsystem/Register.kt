package com.example.parkingsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
        Column(Modifier.padding(16.dp)) {

            Row() {
                OutlinedTextField(
                    value = fName.value,
                    onValueChange = { fName.value = it },
                    label = { Text("First Name") },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .weight(1f)
                        .padding(end = 4.dp)
                )

                OutlinedTextField(
                    value = lName.value,
                    onValueChange = { lName.value = it },
                    label = { Text("Last Name") },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .weight(1f)
                )
            }

            Row {
                OutlinedTextField(
                    value = address.value,
                    onValueChange = { address.value = it },
                    label = { Text("Address") },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .weight(2f)
                )
            }
            Row(){
                OutlinedTextField(
                    value = pNumber.value,
                    onValueChange = { pNumber.value = it },
                    label = { Text("Mobile Number (e.g. 09** *** ****") },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .weight(2f)
                )
            }

        }
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Username input field
            Row {
                UsernameField(
                    value = username.value,
                    onChange = { username.value = it },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .weight(2f)
                )
            }

            Row {
                RegPasswordField(
                    value = password.value,
                    onChange = { password.value = it },
                    submit = { /*TODO*/ },
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .weight(2f)
                )
            }

            // Login button
            Button(
                onClick = {
                    // Handle the login action
                    onLogin(username.value, password.value)
                    navController.navigate(ParkingAppScreen.MapView.name)
                }
            ) {
                Text("Register")
            }
        }
    }
}

@Composable
fun RegPasswordField(
    value: String,
    onChange: (String) -> Unit,
    submit: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Password",
    placeholder: String = "Enter your Password"
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isPasswordStrong by remember { mutableStateOf(true) }

    // Define the function to check password strength
    fun isStrongPassword(password: String): Boolean {
        // Define minimum password length
        val minLength = 8

        // Check if the password is at least the minimum length
        if (password.length < minLength) return false

        // Check for at least one uppercase letter
        val hasUpperCase = password.any { it.isUpperCase() }

        // Check for at least one lowercase letter
        val hasLowerCase = password.any { it.isLowerCase() }

        // Check for at least one digit
        val hasDigit = password.any { it.isDigit() }

        // Check for at least one special character
        val hasSpecialChar = password.any { it in "!@#$%^&*()-_=+{}[]|:;\"'<>,.?/" }

        // Return true if all conditions are met
        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar
    }

    // Leading icon for the password field
    val leadingIcon = @Composable {
        Icon(
            Icons.Default.Key,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.primary
        )
    }

    // Trailing icon for toggling password visibility
    val trailingIcon = @Composable {
        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
            Icon(
                if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }

    // Password input field
    TextField(
        value = value,
        onValueChange = { newPassword ->
            // Update the password value
            onChange(newPassword)
            // Validate the password strength
            isPasswordStrong = isStrongPassword(newPassword)
        },
        modifier = modifier,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Password
        ),
        keyboardActions = KeyboardActions(
            onDone = { submit() }
        ),
        placeholder = { Text(placeholder) },
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
    )

    // Display an error message if the password is not strong
    if (!isPasswordStrong) {
        Text(
            text = "Password must be at least 8 characters long and include uppercase, lowercase, digit, and special character.",
            color = MaterialTheme.colorScheme.error,
            modifier = modifier // Reuse the same modifier to match the width of the TextField
        )
    }
}

