package com.example.parkingsystem

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun RegisterScreen(
    onLogin: (String, String) -> Unit, navController: NavController
) {
    // Define state variables for username and password inputs
    val fName = remember { mutableStateOf("") }
    val lName = remember { mutableStateOf("") }
    val address = remember { mutableStateOf("") }
    val pNumber = remember { mutableStateOf("") }

    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    var isPhoneNumberValid = remember { mutableStateOf(false) }
    var isfNameValid = remember { mutableStateOf(false) }
    var islNameValid = remember { mutableStateOf(false) }
    var isuNameValid = remember { mutableStateOf(false) }
    var isPasswordStrong = remember { mutableStateOf(false) }

    // Create the login screen layout
    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        Column(Modifier.padding(16.dp)) {

            Row() {
                TextField(value = fName.value,
                    onValueChange = { value -> fName.value = value
                                    isfNameValid.value = value.isNotEmpty() },
                    label = { Text("First Name") },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .weight(1f)
                        .padding(end = 4.dp),
                )

                TextField(value = lName.value,
                    onValueChange = { value -> lName.value = value
                                    islNameValid.value = value.isNotEmpty()},
                    label = { Text("Last Name") },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .weight(1f)
                )
            }

            Row {
                TextField(value = address.value,
                    onValueChange = { address.value = it },
                    label = { Text("Address") },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .weight(2f)
                )
            }
            Row {
                PhoneNumberInputField(
                    value = pNumber.value,
                    onValueChange = { pNumber.value = it },
                    onValidityChange = { isPhoneNumberValid.value = it }, // Use the state setter function
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
                    onChange = { value -> username.value = value
                                    isuNameValid.value = value.isNotEmpty() },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .weight(2f)
                )
            }

            Row {
                RegPasswordField(value = password.value,
                    onChange = { password.value = it },
                    onValidityChange = { isPasswordStrong.value = it },
                    submit = { /*TODO*/ },
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .weight(2f)
                )
            }

            // Login button
            Button(onClick = {
                // Handle the login action
                onLogin(username.value, password.value)
                navController.navigate(ParkingAppScreen.MapView.name)
            },
                enabled = isPhoneNumberValid.value && isfNameValid.value && islNameValid.value && isPasswordStrong.value){
                Text("Register")
            }
        }
    }
}

@Composable
fun RegPasswordField(
    value: String,
    onChange: (String) -> Unit,
    onValidityChange: (Boolean) -> Unit, // Add callback function
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
            Icons.Default.Key, contentDescription = "", tint = MaterialTheme.colorScheme.primary
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
    TextField(value = value,
        onValueChange = { newPassword ->
            // Update the password value
            onChange(newPassword)
            // Validate the password strength
            isPasswordStrong = isStrongPassword(newPassword)
            onValidityChange(isPasswordStrong)
        },
        modifier = modifier,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done, keyboardType = KeyboardType.Password
        ),
        keyboardActions = KeyboardActions(onDone = { submit() }),
        placeholder = { Text(placeholder) },
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        colors = TextFieldDefaults.colors(
            errorContainerColor = MaterialTheme.colorScheme.errorContainer,
        )
    )

    // Display an error message if the password is not strong
    if (!isPasswordStrong) {
        Text(
            text = "Password must be at least 8 characters long and include uppercase, lowercase, digit, and special character.",
            color = MaterialTheme.colorScheme.error,
            fontSize = 10.sp,
            modifier = Modifier
                .padding(top = 4.dp, start = 10.dp)
                .width(50.dp) // Add padding for separation
        )
    }
}

@Composable
fun PhoneNumberInputField(
    value: String,
    onValueChange: (String) -> Unit,
    onValidityChange: (Boolean) -> Unit, // Add callback function
    modifier: Modifier = Modifier,
    label: String = "Phone Number",
    placeholder: String = "Enter your phone number",
    errorMessage: String = "Invalid phone number"
) {
    var isPhoneNumberValid by remember { mutableStateOf(true) }

    // Validate the phone number when the value changes
    fun validatePhoneNumber(phoneNumber: String) {
        isPhoneNumberValid = isValidPhoneNumber(phoneNumber)
        onValidityChange(isPhoneNumberValid) // Invoke the callback function
    }

    // Phone number input field
    TextField(value = value,
        onValueChange = { newPhoneNumber ->
            onValueChange(newPhoneNumber)
            validatePhoneNumber(newPhoneNumber)
        },
        modifier = modifier,
        placeholder = { Text(placeholder) },
        label = { Text(label) },
        isError = !isPhoneNumberValid,
        colors = TextFieldDefaults.colors()
    )

    // Display an error message if the phone number is invalid
    if (!isPhoneNumberValid) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            fontSize = 10.sp,
            modifier = Modifier
                .padding(top = 4.dp, start = 10.dp)
                .width(50.dp)
        )
    }
}


fun isValidPhoneNumber(phoneNumber: String): Boolean {
    // Define the regex pattern for a valid phone number
    val phoneNumberPattern = Regex("^09\\d{9}$")
    // Check if the phone number matches the pattern
    return phoneNumberPattern.matches(phoneNumber)
}




