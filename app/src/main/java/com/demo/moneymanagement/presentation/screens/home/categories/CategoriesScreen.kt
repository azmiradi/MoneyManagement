package com.demo.moneymanagement.presentation.screens.home.categories

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
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
import com.demo.moneymanagement.presentation.CustomTextInput
import com.demo.moneymanagement.presentation.NavigationDestination
import com.demo.moneymanagement.presentation.ProgressBar
import com.demo.moneymanagement.presentation.ui.theme.GreenColor
import com.demo.moneymanagement.presentation.ui.theme.YaAlpha

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoriesScreen(
    onBack: () -> Unit,
    onNavigate: (NavigationDestination) -> Unit,
    viewModel: CategoriesViewModel = hiltViewModel(),
) {
    BackHandler {
        viewModel.resetState()
        onBack()
    }
    ProgressBar(
        isShow = viewModel.state.value.isLoading ||
                viewModel.stateAddCategory.value.isLoading ||
                viewModel.stateDeleteCategory.value.isLoading,
        message = stringResource(id = R.string.loading),
        color = GreenColor,
    )
    val context = LocalContext.current
    if (viewModel.state.value.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, viewModel.state.value.error, Toast.LENGTH_SHORT).show()
        }
    }

    if (viewModel.stateAddCategory.value.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, viewModel.stateAddCategory.value.error, Toast.LENGTH_SHORT)
                .show()
        }
    }
    val categoryNameInput = rememberSaveable() {
        mutableStateOf("")
    }
    if (viewModel.stateDeleteCategory.value.error.isNotEmpty()) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, viewModel.stateAddCategory.value.error, Toast.LENGTH_SHORT)
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

    viewModel.stateAddCategory.value.data?.let {
        LaunchedEffect(Unit) {
            viewModel.getCategories()
            categoryNameInput.value = ""
            Toast.makeText(context, "Category Added", Toast.LENGTH_SHORT).show()
        }
    }
    viewModel.stateDeleteCategory.value.data?.let {
        LaunchedEffect(Unit) {
            viewModel.getCategories()
            Toast.makeText(context, "Category Deleted", Toast.LENGTH_SHORT).show()
        }
    }

    Column(Modifier.fillMaxSize()) {
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
        Spacer(modifier = Modifier.height(20.dp))

        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(end = 16.dp, start = 16.dp),
            cells = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(categories.value) { item ->
                CategoryItem(name = item.name.toString()) {
                    viewModel.deleteCategory(item.id.toString())
                }
            }
        }


        CustomTextInput(
            hint = stringResource(id = R.string.category_name),
            mutableState = categoryNameInput, modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp, start = 16.dp),
            isError = viewModel.categoryInput.value
        )
        Spacer(modifier = Modifier.height(15.dp))


        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp, start = 16.dp),
            onClick = {
                viewModel.addCategoryMoney(categoryNameInput.value)
            },
            colors = ButtonDefaults.buttonColors(GreenColor),
        ) {
            Text(
                text = stringResource(id = R.string.add_category),
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
fun CategoryItem(name: String, onDeleteItem: () -> Unit) {
    Card(
        elevation = 0.dp,
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = YaAlpha,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier.padding(start = 10.dp)
            )

            IconButton(onClick = { onDeleteItem() }) {
                Icon(
                    painter = painterResource(id = R.drawable.delete), contentDescription = "",
                    tint = GreenColor,
                    modifier = Modifier.padding(end = 5.dp)
                )
            }

        }

    }
}