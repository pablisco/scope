package com.pablisco.scope.api

import java.util.concurrent.atomic.AtomicReference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * This function helps create a property that persists across a given scope.
 * When the scope ends, this value is garbage collected.
 *
 * Usage:
 *
 * ```kotlin
 * // as read only property
 * val screens by scoped { Config.Screens }
 *
 * // as a mutable property
 * var notes by scoped { listOf<String>() }
 * ```
 */
@WIP
fun <R> scoped(create: () -> R): ReadWriteProperty<Any?, R> = Scoped(create)

@WIP
private class Scoped<R>(
    private val create: () -> R
) : ReadWriteProperty<Any?, R> {
    // TODO: Need to find a way to share this across scoped calls
    private val self: AtomicReference<R> by lazy { AtomicReference<R>(create()) }
    override fun getValue(thisRef: Any?, property: KProperty<*>): R = self.get()
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: R) = self.set(value)
}