package com.demo.moneymanagement.presentation.screens.auth.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.demo.moneymanagement.presentation.CustomTextInput
import com.demo.moneymanagement.presentation.NavigationDestination
import com.demo.moneymanagement.R
import com.demo.moneymanagement.presentation.ProgressBar
import com.demo.moneymanagement.presentation.TextInputsPassword
import com.demo.moneymanagement.presentation.ui.theme.GreenColor
import com.demo.moneymanagement.presentation.ui.theme.YeColor

@Composable
fun LoginScreen(
    onNavigate: (NavigationDestination) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    ProgressBar(
        isShow = viewModel.state.value.isLoading,
        message = stringResource(id = R.string.loading),
        color = GreenColor,
    )
    val context = LocalContext.current
    if (viewModel.state.value.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, viewModel.state.value.error, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(end = 16.dp, start = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painter = painterResource(id = R.drawable.logo), contentDescription = "")
        Spacer(modifier = Modifier.height(18.dp))


        val username = rememberSaveable() {
            mutableStateOf("")
        }
        val passwordInput = rememberSaveable() {
            mutableStateOf("")
        }


        CustomTextInput(
            hint = stringResource(id = R.string.username),
            mutableState = username, modifier = Modifier.fillMaxWidth(),
            isError = viewModel.errorUsername.value
        )




        Spacer(modifier = Modifier.height(10.dp))
        TextInputsPassword(
            hint = stringResource(id = R.string.password),
            mutableState = passwordInput, modifier = Modifier.fillMaxWidth(),
            isError = viewModel.errorPassword.value


        )


        Spacer(modifier = Modifier.height(10.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                viewModel.login(
                    username.value,
                    passwordInput.value
                )
            },
            colors = ButtonDefaults.buttonColors(GreenColor),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.login),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onNavigate(NavigationDestination.Signup)
            },
            colors = ButtonDefaults.buttonColors(YeColor),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.sign_up),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Black
            )
        }
    }
}