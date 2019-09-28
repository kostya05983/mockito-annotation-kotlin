package ru.kontur.mockito.annotation.engine

import org.mockito.Mockito
import org.mockito.internal.configuration.FieldAnnotationProcessor
import ru.kontur.mockito.annotation.annotations.KMock
import java.lang.reflect.Field

/**
 * Instantiates a mock on a field annotated by {@link @KMock}
 */
class KMockAnnotationEngine : FieldAnnotationProcessor<KMock> {
    override fun process(annotation: KMock, field: Field): Any {
        return processAnnotationForMock(annotation, field)
    }

    companion object {
        fun processAnnotationForMock(annotation: KMock, field: Field): Any {
            val mockSettings = Mockito.withSettings()
            if (annotation.extraInterfaces.isNotEmpty()) {
                mockSettings.extraInterfaces()
            }
            if (annotation.name.isEmpty()) {
                mockSettings.name(field.name)
            } else {
                mockSettings.name(annotation.name)
            }

            if (annotation.serializable) {
                mockSettings.serializable()
            }
            if (annotation.stubOnly) {
                mockSettings.stubOnly()
            }
            if (annotation.lenient) {
                mockSettings.lenient()
            }

            mockSettings.defaultAnswer(annotation.answer)
            return Mockito.mock(field.javaClass, mockSettings)
        }
    }
}