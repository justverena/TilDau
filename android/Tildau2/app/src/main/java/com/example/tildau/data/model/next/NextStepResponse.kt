package com.example.tildau.data.model.next

import java.io.Serializable

/**
 * Ответ от бэка, определяющий следующий шаг пользователя
 *
 * type:
 *  - EXERCISE → перейти к упражнению (id = exerciseId)
 *  - RESOURCE → открыть ресурс (пока можно игнорить)
 *  - RETRY → повторить текущее упражнение (id = exerciseId)
 *  - FINISH → курс завершён (id = null)
 */
data class NextStepResponse(
    val type: NextStepType,
    val id: String? // может быть null при FINISH
) : Serializable