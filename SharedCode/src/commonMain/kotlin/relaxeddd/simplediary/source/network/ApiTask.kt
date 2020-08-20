package relaxeddd.simplediary.source.network

import kotlinx.coroutines.delay
import relaxeddd.simplediary.domain.Response
import relaxeddd.simplediary.domain.model.Task

class ApiTask {

    //private val httpClient = HttpClient()

    //@OptIn(ImplicitReflectionSerializer::class, UnstableDefault::class)
    suspend fun requestTasks() = try {
        //val url = ""
        //val json = httpClient.get<String>(url)
        /*val responseJson = "[" +
                "{\"id\":\"1\", \"title\":\"Отправить показания\", \"desc\":\"Показания за электричество, воду, отопление\"}, " +
                "{\"id\":\"2\", \"title\":\"Заплатить за квартиру\", \"desc\":\"Квитанции за квартиру, электричество, отопление\"}, " +
                "{\"id\":\"3\", \"title\":\"Заплатить за интернет\", \"desc\":\"Номер договора 54523423\", \"isCompleted\":\"true\"}" +
                "]"
        val tasks = Json.nonstrict.parseList<Task>(responseJson)*/
        val tasks = listOf(
            Task(0, "Отправить показания", "Показания за электричество, воду, отопление", isCompleted = true),
            Task(1, "Заплатить за квартиру", "Квитанции за квартиру, электричество, отопление"),
            Task(2, "Заплатить за интернет", "Номер договора 54523423")
        )
        delay(2000)

        //throw Exception("Test exception")

        Response(tasks)
    } catch (e: Exception) {
        Response<List<Task>>(exception = e)
    }
}
