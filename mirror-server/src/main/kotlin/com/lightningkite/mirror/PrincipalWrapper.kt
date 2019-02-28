package com.lightningkite.mirror

import io.ktor.application.ApplicationCall
import io.ktor.auth.Principal
import io.ktor.auth.principal

data class PrincipalWrapper<T>(val wraps: T) : Principal

fun <T> ApplicationCall.unwrappedPrincipal(): T? = this.principal<PrincipalWrapper<T>>()?.wraps