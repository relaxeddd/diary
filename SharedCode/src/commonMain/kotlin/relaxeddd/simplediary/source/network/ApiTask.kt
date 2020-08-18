package relaxeddd.simplediary.source.network

import io.ktor.client.HttpClient
import kotlinx.coroutines.delay
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.parseList
import relaxeddd.simplediary.domain.Response
import relaxeddd.simplediary.domain.model.Task

class ApiTask {

    private val httpClient = HttpClient()

    @OptIn(ImplicitReflectionSerializer::class, UnstableDefault::class)
    suspend fun requestTasks() = try {
        //val url = ""
        //val json = httpClient.get<String>(url)
        val responseJson = "[" +
                "{\"id\":\"1\", \"title\":\"Отправить показания\", \"desc\":\"Показания за электричество, воду, отопление\"}, " +
                "{\"id\":\"2\", \"title\":\"Заплатить за квартиру\", \"desc\":\"Квитанции за квартиру, электричество, отопление\"}, " +
                "{\"id\":\"3\", \"title\":\"Заплатить за интернет\", \"desc\":\"Номер договора 54523423\", \"isCompleted\":\"true\"}" +
                "]"
        val tasks = Json.nonstrict.parseList<Task>(responseJson)
        delay(2000)

        //throw Exception("Test exception")

        Response(tasks)
    } catch (e: Exception) {
        Response<List<Task>>(exception = e)
    }
}
