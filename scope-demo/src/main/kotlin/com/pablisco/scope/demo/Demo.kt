package com.pablisco.scope.demo

import com.pablisco.scope.api.Scopable

@Scopable
@Retention(AnnotationRetention.SOURCE)
annotation class CustomScope

fun main() {
    parent()
    invalidParent()
}

@CustomScope
fun parent() {
    child()
}

fun invalidParent() {
    child() // should fail
}

@CustomScope
fun child() {

}