package ru.kontur.mockito.annotation.annotations

import org.mockito.Answers
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.TYPE_PARAMETER)
@Retention
@MustBeDocumented
annotation class KMock(
    val name: String = "",
    val stubOnly: Boolean = false,
    val answer: Answers = Answers.RETURNS_DEFAULTS,
    val extraInterfaces: Array<KClass<*>> = [],
    val serializable: Boolean = false,
    val lenient: Boolean = false
)