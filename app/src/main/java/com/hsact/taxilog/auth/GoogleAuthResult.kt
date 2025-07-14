package com.hsact.taxilog.auth

sealed class GoogleAuthResult {
    data class Success(val email: String?) : GoogleAuthResult()
    data class Error(val exception: Throwable) : GoogleAuthResult()
}