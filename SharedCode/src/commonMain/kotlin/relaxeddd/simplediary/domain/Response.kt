package relaxeddd.simplediary.domain

class Response<out T>(val data: T? = null, val exception: Exception? = null) {

    val isValid: Boolean = exception == null
}
