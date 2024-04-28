package com.example.parkingsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomePage(){
    Column{
        Column(
            Modifier
                .height(200.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally )
        {
            Row(Modifier.padding(0.dp)) {
                Text("PARK SIGHT", style = MaterialTheme.typography.displayLarge)
            }
            Row {
                Text("Your Trusted Parking Space Finder")
            }
        }
        Column(
            Modifier
                .fillMaxHeight()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center) {
                Text(
                    text = "WHERE ARE YOU GOING",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center) {
                Button(onClick = { /*TODO*/ }) {
                    Text("Login")
                }
            }
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center) {
                Button(onClick = { /*TODO*/ }) {
                    Text("Register")
                }
            }
        }

    }
}

@Preview(showBackground = true,
    showSystemUi = true)
@Composable
fun WelcomePagePreview(
) {
    WelcomePage()
}