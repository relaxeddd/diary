package relaxeddd.simplediary.domain

internal const val BASE_ROUTE = "https://us-central1-my-todo-list-36185.cloudfunctions.net/"

const val ERROR_TEXT = "errorText"
const val ID = "id"
const val TEXT = "text"

const val TIME_SECOND = 1000L
const val TIME_MINUTE = 60 * TIME_SECOND
const val TIME_HOUR = 60 * TIME_MINUTE
const val TIME_DAY = 24 * TIME_HOUR
const val TIME_WEEK = 7 * TIME_DAY

const val TIME_15_MINUTE = 15 * TIME_MINUTE

const val TOKEN_PREFIX = "Bearer "

const val RESULT_UNDEFINED = 0
const val RESULT_ERROR_INTERNET = 17

const val RESULT_ERROR_UNAUTHORIZED = 403

const val RESULT_OK = 700

const val RESULT_ERROR_USER_NOT_FOUND = 771
const val RESULT_ERROR_APP_INIT = 774

const val RESULT_ERROR_UPDATE_USER = 1101

const val RESULT_PURCHASE_NOT_VERIFIED = 1401
const val RESULT_PURCHASE_ALREADY_RECEIVED = 1402
const val RESULT_PURCHASE_VERIFIED_ERROR = 1403

const val RESULT_ERROR_ADD_PUSH_TOKEN = 1501

const val RESULT_ERROR_UNSUBSCRIBED = 1888

const val RESULT_ERROR_SAVE_TASKS = 2011
const val RESULT_ERROR_SAVE_TASKS_EMPTY = 2012
const val RESULT_ERROR_LOAD_TASKS = 2013
const val RESULT_ERROR_LOAD_TASKS_EMPTY = 2014
const val RESULT_ERROR_SAVE_TASKS_TOO_MANY = 2015

const val RESULT_LOCAL_ERROR = 5101
const val RESULT_ERROR_NETWORK = 5102

const val RESULT_ERROR_NO_SUBSCRIPTION = 6401
