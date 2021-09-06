package com.pablisco.scope.api

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 *
 */
@WIP
@OptIn(ExperimentalContracts::class)
inline fun <reified A> scope(block: () -> Unit) where A : Annotation {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    // TODO: allow calls with annotation A. If there is not scope annotations presents in a call shoulf be possible to call it. If
    block()
}

// TODO: overload `scope` to have multiple types (up to 23?)