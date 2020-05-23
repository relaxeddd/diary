package relaxeddd.simplediary.source.network

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.serialization.json.Json
import relaxeddd.simplediary.domain.Response
import relaxeddd.simplediary.domain.model.Task

class ApiTask {

    private val httpClient = HttpClient()

    suspend fun getTasks(): Response<Task> {
        try {
            val url = ""
            val json = httpClient.get<String>(url)
            val tasks = Json.nonstrict.parse(Task.serializer(), json)

            return Response.Success(tasks)
        } catch (e: Exception) {
            return Response.Error(e)
        }
    }
}