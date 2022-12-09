package com.demo.moneymanagement.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.demo.moneymanagement.R
import com.demo.moneymanagement.presentation.ui.theme.GreenColor

@Composable
fun WarningMoneyScreen(onConfirm: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color.White,
        shape = RoundedCornerShape(29.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(37.dp, 37.dp)
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Image(
                painter = painterResource(id = R.drawable.wrrang),
                contentDescription = "",
                modifier = Modifier.align(CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(23.dp))

            Row(Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.warning),
                    fontSize = 17.sp, color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.width(3.dp))

                Text(
                    text = stringResource(id = R.string.expenses_that_exceed),
                    fontSize = 17.sp, color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start

                )
            }

            Spacer(modifier = Modifier.height(30.dp))
            Button(
                modifier = Modifier.align(CenterHorizontally),
                onClick = {
                    onConfirm()
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

            Spacer(modifier = Modifier.height(39.dp))

        }
    }
}