@file:Suppress("unused")
package relaxeddd.simplediary.common

const val URL_FIREBASE = "https://us-central1-push-english-79db7.cloudfunctions.net/"

const val FUNC_INIT = "init/"
const val FUNC_UPDATE_USER = "updateUser/"
const val FUNC_VERIFY_PURCHASE = "verifyPurchase/"
const val FUNC_SEND_FEEDBACK = "sendFeedback/"

const val THEME_STANDARD = 0
const val THEME_BLUE = 1
const val THEME_BLACK = 2

const val APP_THEME = "appTheme"
const val CANCELLED_RATE_DIALOG = "cancelledRateDialog15"
const val ITEM_IX = "itemIx"
const val LAUNCH_COUNT = "launchCount"
const val LOGIN_DATA = "loginData"
const val NOTIFICATIONS_CHANNEL_REMIND = "notificationsChannelRemind"
const val PRIVACY_POLICY_CONFIRMED = "privacyPolicyConfirmed"
const val PRODUCT_ID = "productId"
const val PRODUCT_TYPE = "productType"
const val PUSH_TOKEN = "pushToken"
const val SELECTED_ITEM = "selectedItem"
const val TASKS = "tasks"
const val USER_EMAIL = "userEmail"

const val ERROR_TOKEN_NOT_INIT = "User token not init"
const val ERROR_NOT_AUTHORIZED = "User not authorized"
const val ERROR_USER_NOT_FOUND = "User not found"
const val ERROR_APP_INFO = "App info not found"

const val DATABASE_NAME = "databaseRelaxedddMyDiary"

const val RESULT_UNDEFINED = 0
const val RESULT_ERROR_INTERNET = 17
const val RESULT_ERROR_UNAUTHORIZED = 403

const val RESULT_OK = 700

const val RESULT_ERROR_USER_NOT_FOUND = 771
const val RESULT_ERROR_APP_INIT = 774

const val RESULT_PURCHASE_NOT_VERIFIED = 1401
const val RESULT_PURCHASE_ALREADY_RECEIVED = 1402
const val RESULT_PURCHASE_VERIFIED_ERROR = 1403

const val RESULT_ERROR_ADD_PUSH_TOKEN = 1501
const val RESULT_ERROR_SEND_FEEDBACK = 1601
const val RESULT_ERROR_FEEDBACK_TOO_SHORT = 1602
const val RESULT_LOCAL_ERROR = 5101
const val RESULT_ERROR_NETWORK = 5102

const val RESULT_ERROR_NO_SUBSCRIPTION = 6401
