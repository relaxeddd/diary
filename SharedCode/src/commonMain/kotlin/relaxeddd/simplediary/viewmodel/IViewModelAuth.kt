package relaxeddd.simplediary.viewmodel

import relaxeddd.simplediary.utils.observable.Observable

interface IViewModelAuth : IViewModelBase {

    val isRegistrationView: Observable<Boolean>
    val isAuthorized: Observable<Boolean>
    val errorAuth: Observable<String>
    val textEmail: Observable<String>
    val textPassword: Observable<String>
    val textRepeatPassword: Observable<String>

    fun onChangedEmail(value: String)
    fun onChangedPassword(value: String)
    fun onChangedRepeatPassword(value: String)
    fun onClickedHaveNotAccount()
    fun onClickedAlreadyHaveAccount()
    fun onClickedLogin()
    fun onClickedCreate()
}
