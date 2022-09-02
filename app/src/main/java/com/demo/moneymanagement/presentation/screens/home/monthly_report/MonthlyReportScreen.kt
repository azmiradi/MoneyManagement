package com.demo.moneymanagement.presentation.screens.home.monthly_report

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.demo.moneymanagement.R
import com.demo.moneymanagement.data.Category
import com.demo.moneymanagement.data.Spend
import com.demo.moneymanagement.presentation.CustomTextInput
import com.demo.moneymanagement.presentation.NavigationDestination
import com.demo.moneymanagement.presentation.ProgressBar
import com.demo.moneymanagement.presentation.ui.theme.DividerColor
import com.demo.moneymanagement.presentation.ui.theme.GreenColor
import com.demo.moneymanagement.presentation.ui.theme.YaAlpha

@Composable
fun MonthlyReportScreen(
    onBack: () -> Unit,
    onNavigate: (NavigationDestination) -> Unit,
    viewModel: MonthlyReportViewModel = hiltViewModel(),
) {
    BackHandler {
        viewModel.resetState()
        onBack()
    }
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

    LaunchedEffect(Unit) {
        viewModel.getSpentData()
    }

    val spentList = remember {
        mutableStateOf(listOf<SpentDetails>())
    }
    val totalSpent = remember {
        mutableStateOf(0)
    }

    viewModel.state.value.data?.let {
        LaunchedEffect(Unit) {
            spentList.value = it
            var total=0
            spentList.value.forEach {
                total += (it.spent ?: "0").toInt()
            }
            totalSpent.value=total
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

        TotalCard(monthName = "August 2022", totalSpent = totalSpent.value.toString() )
        Spacer(modifier = Modifier.height(33.dp))

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
                )
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        Divider(Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp), thickness = 2.dp, color = DividerColor)

        Spacer(modifier = Modifier.height(25.dp))

        Total(totalSpent = totalSpent.value.toString())

        Spacer(modifier = Modifier.height(25.dp))


    }
}

@Composable
fun SpentItem(categoryName: String, totalSpent: String) {
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
                    text = stringResource(id = R.string.monthly_report),
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
fun Total( totalSpent: String) {
    Card(
        elevation = 0.dp,
        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp),
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