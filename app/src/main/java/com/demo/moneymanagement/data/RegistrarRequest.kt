package com.demo.moneymanagement.data


data class RegistrarRequest(
    val username: String? = null,
    val password: String? = null,
    val salary: String? = null,
    val email: String? = null,
    val id: String? = null,
    val reachAmount: String? = null,
    var totalSpent: String? = null,
)