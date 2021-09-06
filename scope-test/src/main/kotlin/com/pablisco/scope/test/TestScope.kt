package com.pablisco.scope.test

import com.pablisco.scope.api.WIP
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


@WIP
@OptIn(ExperimentalContracts::class)
inline fun <reified A> testScope(block: TestScope.() -> Unit) where A : Annotation {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    TestScope().block()
}

class TestScope
