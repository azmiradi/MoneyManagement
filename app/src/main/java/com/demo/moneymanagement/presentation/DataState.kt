package com.demo.moneymanagement.presentation

data class DataState<T>(
    val isLoading: Boolean = false,
    val data: T? = null,
    val error: String = "",
    val isUnAuthorized: Boolean = false
)
