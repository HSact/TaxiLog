package com.hsact.data.firebase.model

data class FirebaseShift(
    val id: Int? = null,
    val carId: Int? = null,
    val remoteId: String? = null,
    val meta: FirebaseShiftMeta? = null,
    val carSnapshot: FirebaseCarSnapshot? = null,
    val period: FirebaseDateTimePeriod? = null,
    val rest: FirebaseDateTimePeriod? = null,
    val financeInput: FirebaseFinanceInput? = null,
    val note: String? = null,
)