package com.demo.moneymanagement.presentation.screens.home

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.demo.moneymanagement.presentation.NavigationDestination
import com.demo.moneymanagement.R
import com.demo.moneymanagement.data.RegistrarRequest
import com.demo.moneymanagement.presentation.CustomTextInput
import com.demo.moneymanagement.presentation.ProgressBar
import com.demo.moneymanagement.presentation.ui.theme.Brown
import com.demo.moneymanagement.presentation.ui.theme.DividerColor
import com.demo.moneymanagement.presentation.ui.theme.GreenColor
import com.demo.moneymanagement.presentation.ui.theme.YeColor

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigate: (NavigationDestination) -> Unit,
    onBack: () -> Unit,
) {
    BackHandler {
        viewModel.resetState()
        onBack()
    }
    ProgressBar(
        isShow = viewModel.state.value.isLoading || viewModel.stateAddReach.value.isLoading,
        message = stringResource(id = R.string.loading),
        color = GreenColor,
    )
    val context = LocalContext.current
    if (viewModel.state.value.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, viewModel.state.value.error, Toast.LENGTH_SHORT).show()
        }
    }

    if (viewModel.stateAddReach.value.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, viewModel.stateAddReach.value.error, Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.getUserData()
    }
    val userData = remember {
        mutableStateOf(RegistrarRequest())
    }
    val salaryInput = rememberSaveable() {
        mutableStateOf("")
    }
    viewModel.state.value.data?.let {
        LaunchedEffect(Unit) {
            if (!it.reachAmount.isNullOrEmpty())
                salaryInput.value = it.reachAmount ?: "0"
            userData.value = it
        }
    }

    viewModel.stateAddReach.value.data?.let {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Reach Money Added", Toast.LENGTH_SHORT).show()
        }
    }
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(end = 16.dp, start = 16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                     viewModel.logout()
                    onNavigate(NavigationDestination.Login)
                    viewModel.resetState()

                },
                colors = ButtonDefaults.buttonColors(YeColor),
                shape = RoundedCornerShape(8.dp)
            ) {

                Text(
                    text = stringResource(id = R.string.logout),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Icon(
                    imageVector = Icons.Default.Logout, contentDescription = "",
                    tint = GreenColor
                )
            }
            Button(
                onClick = {
                    onNavigate(NavigationDestination.Categories)
                    viewModel.resetState()
                },
                colors = ButtonDefaults.buttonColors(YeColor),
                shape = RoundedCornerShape(8.dp)
            ) {

                Text(
                    text = stringResource(id = R.string.set_categories),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Icon(
                    imageVector = Icons.Default.Settings, contentDescription = "",
                    tint = GreenColor
                )
            }
        }

        Image(
            painter = painterResource(id = R.drawable.logo),
            modifier = Modifier.align(CenterHorizontally),
            contentDescription = ""
        )
        Spacer(modifier = Modifier.height(18.dp))

        Row(Modifier.fillMaxWidth()) {
            DataCardVertical(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                backgroundColor = YeColor,
                title = userData.value.salary ?: "0",
                details = "Your salary"
            )
            Spacer(modifier = Modifier.width(9.dp))
            DataCardVertical(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                backgroundColor = GreenColor,
                title = userData.value.totalSpent ?: "0",
                details = "You spent"
            )
        }
        Spacer(modifier = Modifier.height(9.dp))

        DataCardHorizontal(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Brown,
            title = ((userData.value.salary ?: "0").toInt() - (userData.value.totalSpent
                ?: "0").toInt()).toString(),
            details = "The rest of your \n Salary"
        )

        Spacer(modifier = Modifier.height(25.dp))

        Divider(Modifier.fillMaxWidth(), thickness = 2.dp, color = DividerColor)

        Spacer(modifier = Modifier.height(25.dp))

        ReportItem(title=stringResource(id = R.string.monthly_report),
        onClick = {
            onNavigate(NavigationDestination.MonthlyReport)
            viewModel.resetState()
        })

        Spacer(modifier = Modifier.height(25.dp))

        Spacer(modifier = Modifier.height(10.dp))

        ReportItem(title=stringResource(id = R.string.all_spend),
            onClick = {
                onNavigate(NavigationDestination.AllSpend)
                viewModel.resetState()
            })

        Spacer(modifier = Modifier.height(25.dp))

        Divider(Modifier.fillMaxWidth(), thickness = 2.dp, color = DividerColor)

        Spacer(modifier = Modifier.height(25.dp))

        Row(Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = R.string.when_reiche),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Brown
            )

            Text(
                text = stringResource(id = R.string.notifiy),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = GreenColor
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        CustomTextInput(
            hint = "Ex: 3000",
            mutableState = salaryInput, modifier = Modifier.fillMaxWidth(),
            isError = viewModel.salaryInput.value,
            keyboardType = KeyboardType.Number
        )
        Spacer(modifier = Modifier.height(8.dp))


        Button(
            modifier = Modifier.align(Alignment.Start),
            onClick = {
                viewModel.addReachMoney(salaryInput.value)
            },
            colors = ButtonDefaults.buttonColors(GreenColor),
        ) {
            Text(
                text = stringResource(id = R.string.confirm),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
            )
            Icon(
                imageVector = Icons.Default.ConfirmationNumber, contentDescription = "",
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        Divider(Modifier.fillMaxWidth(), thickness = 2.dp, color = DividerColor)

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = stringResource(id = R.string.what_did_spend),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(10.dp))


        Button(
            modifier = Modifier.align(Alignment.Start),
            onClick = {
                onNavigate(NavigationDestination.AddSpend)
                viewModel.resetState()
            },
            colors = ButtonDefaults.buttonColors(YeColor),
        ) {

            Text(
                text = stringResource(id = R.string.add),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
            )
            Icon(
                imageVector = Icons.Default.Add, contentDescription = "",
                tint = Color.White
            )
        }

    }
}

@Composable
fun ReportItem(onClick: () -> Unit,title:String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        backgroundColor = GreenColor
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painterResource(id = R.drawable.report),
                contentDescription = "",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title ,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)

            )
            Icon(
                imageVector = Icons.Default.ArrowRight,
                contentDescription = "",
                tint = Color.White,

                )


        }
    }
}

@Composable
fun DataCardVertical(
    modifier: Modifier,
    backgroundColor: Color,
    title: String,
    details: String
) {
    Card(
        modifier = modifier,
        backgroundColor = backgroundColor
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontSize = 30.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = details,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(12.dp))

        }
    }
}


@Composable
fun DataCardHorizontal(
    modifier: Modifier,
    backgroundColor: Color,
    title: String,
    details: String
) {
    Card(
        modifier = modifier,
        backgroundColor = backgroundColor,
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = details,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = YeColor,
                modifier = Modifier.padding(start = 16.dp)

            )
            Text(
                text = title,
                fontSize = 30.sp,
                color = YeColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 43.dp)
            )


        }
    }
}