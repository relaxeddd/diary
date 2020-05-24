package relaxeddd.simplediary.source.network

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.parseList
import relaxeddd.simplediary.domain.Response
import relaxeddd.simplediary.domain.model.Task

class ApiTask {

    private val httpClient = HttpClient()

    @UseExperimental(ImplicitReflectionSerializer::class)
    suspend fun requestTasks() = try {
        //val url = ""
        //val json = httpClient.get<String>(url)
        val responseJson = "[" +
                "{\"id\":\"1\", \"title\":\"Отправить показания\", \"description\":\"Показания за электричество, воду, отопление\"}, " +
                "{\"id\":\"2\", \"title\":\"Заплатить за квартиру\", \"description\":\"Квитанции за квартиру, электричество, отопление\"}, " +
                "{\"id\":\"3\", \"title\":\"Заплатить за интернет\", \"description\":\"Номер договора 54523423\"}" +
                "]"
        val tasks = Json.nonstrict.parseList<Task>(responseJson)

         Response(tasks)
    } catch (e: Exception) {
         Response<List<Task>>(exception = e)
    }
}