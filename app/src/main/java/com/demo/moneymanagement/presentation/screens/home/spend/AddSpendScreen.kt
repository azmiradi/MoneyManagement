package com.demo.moneymanagement.presentation.screens.home.spend

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
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
import com.demo.moneymanagement.R
import com.demo.moneymanagement.data.Category
import com.demo.moneymanagement.presentation.*
import com.demo.moneymanagement.presentation.ui.theme.GreenColor
import com.demo.moneymanagement.presentation.ui.theme.YeColor

@Composable
fun AddSpendScreen(
    onNavigate: (NavigationDestination) -> Unit,
    onBack: () -> Unit,
    viewModel: AddSpendViewModel = hiltViewModel()
) {
    BackHandler {
        viewModel.resetState()
        onBack()
    }
    ProgressBar(
        isShow = viewModel.state.value.isLoading
                || viewModel.stateAddAmount.value.isLoading,
        message = stringResource(id = R.string.loading),
        color = GreenColor,
    )
    val context = LocalContext.current
    if (viewModel.state.value.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, viewModel.state.value.error, Toast.LENGTH_SHORT).show()
        }
    }

    if (viewModel.stateAddAmount.value.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, viewModel.stateAddAmount.value.error, Toast.LENGTH_SHORT)
                .show()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.getCategories()
    }
    val categories = remember {
        mutableStateOf(listOf<Category>())
    }


    viewModel.state.value.data?.let {
        LaunchedEffect(Unit) {
            categories.value = it
        }
    }
    val category = rememberSaveable() {
        mutableStateOf("")
    }
    val amount = rememberSaveable() {
        mutableStateOf("")
    }

    viewModel.stateAddAmount.value.data?.let {
        LaunchedEffect(Unit) {
            amount.value = ""
            Toast.makeText(context, "Amount Added", Toast.LENGTH_SHORT).show()
        }
    }

    if (viewModel.categoryInput.value){
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Select Category Please", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            IconButton(
                onClick = { onBack() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back_arrow), contentDescription = "",
                    tint = GreenColor,

                    )
            }
            Text(
                text = stringResource(id = R.string.categories),
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.height(100.dp))

        Image(
            painter = painterResource(id = R.drawable.logo),
            modifier = Modifier
                .height(135.dp)
                .width(95.dp), contentDescription = ""
        )
        Spacer(modifier = Modifier.height(77.dp))




        SampleSpinner(list = categories.value) {
            category.value = it.id.toString()
        }

        Spacer(modifier = Modifier.height(20.dp))
        CustomTextInput(
            hint = "Ex: 3000",
            mutableState = amount,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp, start = 16.dp),
            isError = viewModel.amountInput.value,
            keyboardType = KeyboardType.Number)


        Spacer(modifier = Modifier.height(77.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp, start = 16.dp),
            onClick = {
                viewModel.addSpend(amount.value, category.value)
            },
            colors = ButtonDefaults.buttonColors(GreenColor),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.confirm),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White
            )
        }


    }
}