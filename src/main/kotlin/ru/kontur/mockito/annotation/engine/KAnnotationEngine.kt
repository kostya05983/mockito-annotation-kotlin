package ru.kontur.mockito.annotation.engine

import org.mockito.exceptions.base.MockitoException
import org.mockito.internal.configuration.FieldAnnotationProcessor
import org.mockito.internal.exceptions.Reporter.moreThanOneAnnotationNotAllowed
import org.mockito.internal.util.reflection.FieldSetter.setField
import org.mockito.plugins.AnnotationEngine
import ru.kontur.mockito.annotation.annotations.KMock
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * @author Konstantin Volivach
 */
class KAnnotationEngine : AnnotationEngine {
    private val annotationProcessorMap = HashMap<KClass<out Annotation>, FieldAnnotationProcessor<*>>()

    init {
        annotationProcessorMap[KMock::class] = KMockAnnotationEngine()
    }

    override fun process(clazz: Class<*>, testInstance: Any?) {
        val fields = clazz.declaredFields
        for (field in fields) {
            var alreadyAssigned = false
            for (annotation in field.annotations) {
                val mock = createMockFor(annotation, field)
                if (mock != null) {
                    throwIfAlreadyAssigned(field, alreadyAssigned)
                    alreadyAssigned = true
                    try {
                        setField(testInstance, field, mock)
                    } catch (e: Exception) {
                        throw MockitoException(
                            "Problems setting field " + field.name + " annotated with "
                                    + annotation, e
                        )
                    }
                }
            }
        }
    }

    private fun createMockFor(annotation: Annotation, field: Field): Any? {
        return forAnnotation(annotation).process(annotation, field)
    }

    private fun <A : Annotation> forAnnotation(annotation: A): FieldAnnotationProcessor<A> {
        return if (annotationProcessorMap.containsKey(annotation.annotationClass)) {
            annotationProcessorMap[annotation.annotationClass] as FieldAnnotationProcessor<A>
        } else {
            FieldAnnotationProcessor { annotation, field -> null }
        }
    }

    private fun throwIfAlreadyAssigned(field: Field, alreadyAssigned: Boolean) {
        if (alreadyAssigned) {
            throw moreThanOneAnnotationNotAllowed(field.name)
        }
    }
}