package com.demo.moneymanagement.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.demo.moneymanagement.presentation.NavigationDestination
import com.demo.moneymanagement.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onNavigate: (NavigationDestination) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painter = painterResource(id = R.drawable.logo), contentDescription = "")
        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = stringResource(id = R.string.money),
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = stringResource(id = R.string.management),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        LaunchedEffect(Unit) {
            coroutineScope.launch {
                delay(3000)
                onNavigate(NavigationDestination.Login)
            }
        }

    }
}