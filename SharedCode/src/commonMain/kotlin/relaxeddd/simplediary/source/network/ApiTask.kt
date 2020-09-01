package relaxeddd.simplediary.source.network

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.builtins.ListSerializer
import relaxeddd.simplediary.domain.Response
import kotlinx.serialization.json.*
import relaxeddd.simplediary.domain.model.Task

class ApiTask {

    //private val httpClient = HttpClient()

    suspend fun requestTasks() = try {
        val responseTasks: String = HttpClient().get("https://us-central1-my-todo-list-36185.cloudfunctions.net/test")
        val tasks = Json.decodeFromString(ListSerializer(Task.serializer()), responseTasks)

        //throw Exception("Test exception")

        Response(tasks)
    } catch (e: Exception) {
        print("" + e + "\n")
        Response(exception = e)
    }
}
