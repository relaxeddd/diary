package relaxeddd.simplediary.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Task(

    val id: Long,
    val title: String = "",
    val description: String = ""
)