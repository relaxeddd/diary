package relaxeddd.simplediary.domain.usecase.base

interface BaseRequest {
    fun validate(): Boolean
}