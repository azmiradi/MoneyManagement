package com.demo.moneymanagement.presentation.screens.home.spendDetails

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.demo.moneymanagement.R
import com.demo.moneymanagement.presentation.NavigationDestination
import com.demo.moneymanagement.presentation.ProgressBar
import com.demo.moneymanagement.presentation.ui.theme.DividerColor
import com.demo.moneymanagement.presentation.ui.theme.GreenColor
import com.demo.moneymanagement.presentation.ui.theme.YaAlpha
import kotlinx.coroutines.launch

@Composable
fun SpendDetailsScreen(
    onBack: () -> Unit,
    onNavigate: (NavigationDestination) -> Unit,
    viewModel: SpendDetailsViewModel = hiltViewModel(),
) {
    BackHandler {
        viewModel.resetState()
        onBack()
    }
    ProgressBar(
        isShow = viewModel.stateSpend.value.isLoading,
        message = stringResource(id = R.string.loading),
        color = GreenColor,
    )
    val context = LocalContext.current
    if (viewModel.stateSpend.value.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, viewModel.stateSpend.value.error, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getSpentData()
    }

    val spentList = remember {
        mutableStateOf(listOf<SpentDetails>())
    }
    val totalSpent = remember {
        mutableStateOf(0)
    }

    viewModel.stateSpend.value.data?.let {
        LaunchedEffect(Unit) {
            spentList.value = it
            var total = 0
            spentList.value.forEach {
                total += (it.spent ?: "0").toInt()
            }
            totalSpent.value = total
        }
    }


    Column(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(15.dp)
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
                text = stringResource(id = R.string.monthly_report),
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.height(40.dp))

        TotalCard(monthName = "All Spend", totalSpent = totalSpent.value.toString())
        Spacer(modifier = Modifier.height(33.dp))
        val coroutine = rememberCoroutineScope()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(end = 16.dp, start = 16.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp),
        ) {
            items(spentList.value) { item ->
                SpentItem(
                    categoryName = item.categoryName.toString(),
                    totalSpent = item.spent ?: "0"
                ) {
                    coroutine.launch {
                        val result = viewModel.deleteSpend(item.id.toString())
                        if (result) {
                            viewModel.getSpentData()
                        }else{
                            Toast.makeText(context, "Error in Delete Spend", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        Divider(
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp), thickness = 2.dp, color = DividerColor
        )

        Spacer(modifier = Modifier.height(25.dp))

        Total(totalSpent = totalSpent.value.toString())

        Spacer(modifier = Modifier.height(25.dp))


    }
}

@Composable
fun SpentItem(
    categoryName: String, totalSpent: String,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color.Transparent,
        border = BorderStroke(1.dp, DividerColor)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = categoryName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 20.dp)
            )

            Text(
                text = totalSpent,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.padding(end = 20.dp)
            )
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "",
                    tint = Color.Red
                )
            }


        }

    }
}


@Composable
fun TotalCard(monthName: String, totalSpent: String) {
    Card(
        elevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 16.dp, start = 16.dp),
        backgroundColor = GreenColor,
        border = BorderStroke(1.dp, DividerColor)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                Modifier.padding(start = 20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.comprehensive_report),
                    fontWeight = FontWeight.Bold,
                    fontSize = 21.sp,
                    color = Color.White,
                )

                Text(
                    text = monthName,
                    fontSize = 15.sp,
                    color = Color.White,
                )
            }


            Text(
                text = totalSpent,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Color.White,
                modifier = Modifier.padding(end = 20.dp)
            )

        }

    }
}


@Composable
fun Total(totalSpent: String) {
    Card(
        elevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        backgroundColor = YaAlpha,
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.total),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier.padding(start = 20.dp)
            )

            Text(
                text = totalSpent,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.padding(end = 20.dp)
            )

        }

    }
}