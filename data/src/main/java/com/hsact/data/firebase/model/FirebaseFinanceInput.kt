package com.hsact.data.firebase.model

data class FirebaseFinanceInput(
    val earnings: Long? = null,
    val tips: Long? = null,
    val taxRate: Int? = null,
    val wash: Long? = null,
    val fuelCost: Long? = null
)