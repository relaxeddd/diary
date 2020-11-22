package relaxeddd.simplediary.source.network

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.builtins.ListSerializer
import relaxeddd.simplediary.domain.Response
import kotlinx.serialization.json.*
import relaxeddd.simplediary.domain.model.ResultInit
import relaxeddd.simplediary.domain.model.Task
import relaxeddd.simplediary.utils.TOKEN_PREFIX

class ApiHelper {

    suspend fun requestInit(tokenId: String, uid: String, email: String, pushToken: String) : ResultInit {
        val answerString = HttpClient().get<String>("https://us-central1-my-todo-list-36185.cloudfunctions.net/init") {
            headers {
                append("Authorization", TOKEN_PREFIX + tokenId)
            }
            parameter("userId", uid)
            parameter("email", email)
            parameter("pushToken", pushToken)
        }

        return Json { ignoreUnknownKeys = true }.decodeFromString(ResultInit.serializer(), answerString)
    }

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
