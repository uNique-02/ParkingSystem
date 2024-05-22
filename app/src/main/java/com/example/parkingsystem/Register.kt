package com.example.parkingsystem.view

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.parkingsystem.AppViewModelProvider
import com.example.parkingsystem.ParkingAppScreen
import com.example.parkingsystem.viewmodel.LoginViewModel
import com.example.parkingsystem.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
) {
    val viewModel: RegisterViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val loginViewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val fName by viewModel.fName.collectAsState()
    val lName by viewModel.lName.collectAsState()
    val address by viewModel.address.collectAsState()
    val pNumber by viewModel.pNumber.collectAsState()
    val username by viewModel.username.collectAsState()
    val password by viewModel.password.collectAsState()

    val isPhoneNumberValid by viewModel.isPhoneNumberValid.collectAsState()
    val isfNameValid by viewModel.isfNameValid.collectAsState()
    val islNameValid by viewModel.islNameValid.collectAsState()
    val isuNameValid by viewModel.isuNameValid.collectAsState()
    val isPasswordStrong by viewModel.isPasswordStrong.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(Modifier.padding(16.dp)) {
            Row {
                TextField(
                    value = fName,
                    onValueChange = { viewModel.onfNameChange(it) },
                    label = { Text("First Name") },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .weight(1f)
                        .padding(end = 4.dp)
                )

                TextField(
                    value = lName,
                    onValueChange = { viewModel.onlNameChange(it) },
                    label = { Text("Last Name") },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .weight(1f)
                )
            }

            Row {
                TextField(
                    value = address,
                    onValueChange = { viewModel.onAddressChange(it) },
                    label = { Text("Address") },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .weight(2f)
                )
            }

            Row {
                PhoneNumberInputField(
                    value = pNumber,
                    onValueChange = { viewModel.onPhoneNumberChange(it) },
                    isPhoneNumberValid = isPhoneNumberValid,
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
            Row {
                UsernameField(
                    value = username,
                    onChange = { viewModel.onUsernameChange(it) },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .weight(2f)
                )
            }

            Row {
                RegPasswordField(
                    value = password,
                    onChange = { viewModel.onPasswordChange(it) },
                    isPasswordStrong = isPasswordStrong,
                    submit = { /* TODO */ },
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .weight(2f)
                )
            }

            Button(
                onClick = {
                    viewModel.register()
                    loginViewModel.login()
                    navController.navigate(ParkingAppScreen.MapView.name)
                },
                enabled = isPhoneNumberValid && isfNameValid && islNameValid && isPasswordStrong
            ) {
                Text("Register")
            }
        }
    }
}

@Composable
fun PhoneNumberInputField(
    value: String,
    onValueChange: (String) -> Unit,
    isPhoneNumberValid: Boolean,
    modifier: Modifier = Modifier,
    label: String = "Phone Number",
    placeholder: String = "Enter your phone number",
    errorMessage: String = "Invalid phone number"
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = { Text(placeholder) },
        label = { Text(label) },
        isError = !isPhoneNumberValid,
        colors = TextFieldDefaults.colors()
    )

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

@Composable
fun RegPasswordField(
    value: String,
    onChange: (String) -> Unit,
    isPasswordStrong: Boolean,
    submit: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Password",
    placeholder: String = "Enter your Password"
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    val leadingIcon = @Composable {
        Icon(
            Icons.Default.Key, contentDescription = "", tint = MaterialTheme.colorScheme.primary
        )
    }

    val trailingIcon = @Composable {
        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
            Icon(
                if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }

    TextField(
        value = value,
        onValueChange = onChange,
        modifier = modifier,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Password
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

    if (!isPasswordStrong) {
        Text(
            text = "Password must be at least 8 characters long and include uppercase, lowercase, digit, and special character.",
            color = MaterialTheme.colorScheme.error,
            fontSize = 10.sp,
            modifier = Modifier
                .padding(top = 4.dp, start = 10.dp)
                .width(50.dp)
        )
    }
}

@Composable
fun UsernameField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Username",
    placeholder: String = "Enter your username",
    errorMessage: String = "Invalid username"
) {
    var isUsernameValid by remember { mutableStateOf(true) }

    fun validateUsername(username: String) {
        isUsernameValid = username.isNotEmpty()
    }

    TextField(
        value = value,
        onValueChange = {
            onChange(it)
            validateUsername(it)
        },
        modifier = modifier,
        placeholder = { Text(placeholder) },
        label = { Text(label) },
        isError = !isUsernameValid,
        colors = TextFieldDefaults.colors()
    )

    if (!isUsernameValid) {
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
