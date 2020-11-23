package relaxeddd.simplediary.source.network

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.*
import relaxeddd.simplediary.domain.model.Result
import relaxeddd.simplediary.domain.model.ResultInit
import relaxeddd.simplediary.domain.model.ResultTasks
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

    suspend fun requestSaveTasks(tokenId: String, uid: String, tasks: List<Task>) : Result {
        val answerString = HttpClient().post<String>("https://us-central1-my-todo-list-36185.cloudfunctions.net/saveTasks") {
            headers {
                append("Authorization", TOKEN_PREFIX + tokenId)
            }
            parameter("userId", uid)
            body = Json.encodeToString(ListSerializer(Task.serializer()), tasks)
        }

        return Json { ignoreUnknownKeys = true }.decodeFromString(Result.serializer(), answerString)
    }

    suspend fun requestLoadTasks(tokenId: String, uid: String) : ResultTasks {
        val answerString = HttpClient().get<String>("https://us-central1-my-todo-list-36185.cloudfunctions.net/loadTasks") {
            headers {
                append("Authorization", TOKEN_PREFIX + tokenId)
            }
            parameter("userId", uid)
        }

        return Json { ignoreUnknownKeys = true }.decodeFromString(ResultTasks.serializer(), answerString)
    }
}
