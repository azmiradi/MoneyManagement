package com.demo.moneymanagement.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.demo.moneymanagement.R
import com.demo.moneymanagement.data.Category
import com.demo.moneymanagement.presentation.ui.theme.GreenColor
import com.google.accompanist.insets.navigationBarsWithImePadding

@Composable
fun CustomTextInput(
    modifier: Modifier = Modifier,
    hint: String,
    mutableState: MutableState<String>,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    enable: Boolean = true,
    onClick: (() -> (Unit))? = null,
    leadingIcon: @Composable (() -> Unit)? = null,

    ) {
    val focusManager = LocalFocusManager.current


    OutlinedTextField(
        enabled = enable,
        textStyle = TextStyle(
            color = GreenColor,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
        ),
        isError = isError,
        colors = TextFieldDefaults.textFieldColors(
            cursorColor = GreenColor,
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = GreenColor,
            unfocusedIndicatorColor = Color.Gray,
            focusedLabelColor = GreenColor
        ),
        singleLine = true,
        value = mutableState.value,
        leadingIcon = leadingIcon,
        modifier = modifier.navigationBarsWithImePadding()
            .clickable(role = Role.Tab) {
                onClick?.let { it() }
            },
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),


        placeholder = {
            Text(
                text = hint, fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
        },
        onValueChange = {
            mutableState.value = it
        }
    )
}


@Composable
fun TextInputsPassword(
    modifier: Modifier = Modifier,
    hint: String,
    isError: Boolean = false,
    mutableState: MutableState<String>,
) {
    val focusManager = LocalFocusManager.current


    var passwordVisibility by remember { mutableStateOf(false) }
    OutlinedTextField(
        textStyle = TextStyle(
            color = GreenColor,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
        ), isError = isError,
        colors = TextFieldDefaults.textFieldColors(
            cursorColor = GreenColor,
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = GreenColor,
            unfocusedIndicatorColor = Color.Gray,
            focusedLabelColor = GreenColor
        ),
        maxLines = 1,
        value = mutableState.value,
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        modifier = modifier.navigationBarsWithImePadding(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),


        placeholder = {
            Text(
                text = hint, fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
        },
        onValueChange = {
            mutableState.value = it
        }, trailingIcon = {
            val image = if (passwordVisibility)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff

            IconButton(onClick = {
                passwordVisibility = !passwordVisibility
            }) {
                Icon(tint = GreenColor, imageVector = image, contentDescription = "")
            }
        },
        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation()

    )
}

@Composable
fun NormalTextFiled(
    mutableState: MutableState<String>,
    hint: String,
    hintColor: Color,
    modifier: Modifier, backGroundColor: Color,
    enable: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    BasicTextField(
        enabled = enable,
        textStyle = TextStyle(
            color = GreenColor,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Start,
        ),
        modifier = modifier
            .clip(RoundedCornerShape(percent = 10))
            .background(Color.Transparent)
            .navigationBarsWithImePadding()
            .clickable { onClick?.let { it() } },
        value = mutableState.value,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        onValueChange = {
            mutableState
                .value = it
        },
        decorationBox = { innerTextField ->

            Row(
                Modifier
                    .background(backGroundColor)
                    .padding(10.dp)
            ) {

                AnimatedVisibility(visible = mutableState.value.isEmpty()) {
                    Text(
                        text = hint,
                        fontWeight = FontWeight.Normal, textAlign = TextAlign.Start,
                        fontSize = 12.sp,
                        color = hintColor
                    )
                }
                innerTextField()
            }
        },
    )
}


@Composable
fun SampleSpinner(
    hint: String,
    list: List<Pair<String,String>>,
    onSelectionChanged: (id:String) -> Unit
) {

    var selected by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) } // initial value

    Box (Modifier.fillMaxWidth().padding(end = 16.dp, start = 16.dp)){
        Column {
            OutlinedTextField(
                value = (selected),
                colors = TextFieldDefaults.textFieldColors(
                    cursorColor = GreenColor,
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = GreenColor,
                    unfocusedIndicatorColor = Color.Gray,
                    focusedLabelColor = GreenColor
                ),
                onValueChange = { },
                placeholder = {
                    Text(
                        text = hint,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                },
                modifier = Modifier.fillMaxWidth()
                    .navigationBarsWithImePadding(),
                trailingIcon = { Icon(Icons.Outlined.ArrowDropDown, null) },
                readOnly = true,
                textStyle = TextStyle(
                    color = GreenColor,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                )
            )
            DropdownMenu(
                modifier = Modifier.fillMaxWidth()
                    .padding(end = 16.dp, start = 16.dp),
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                list.forEach { entry ->
                    DropdownMenuItem(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onSelectionChanged(entry.second)
                            selected = entry.first
                            expanded = false
                        }) {
                        Text(
                            text = (entry.first.toString()),
                            modifier = Modifier
                                .wrapContentWidth()
                        )

                    }
                }
            }
        }

        Spacer(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Transparent)
                .padding(10.dp)
                .clickable(
                    onClick = { expanded = !expanded }
                )
        )
    }
}

