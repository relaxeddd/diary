package relaxeddd.simplediary.domain

class Response<out T>(val data: T? = null, val exception: Throwable? = null) {

    val isValid: Boolean = exception == null
}