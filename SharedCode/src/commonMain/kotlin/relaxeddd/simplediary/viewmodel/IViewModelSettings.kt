package relaxeddd.simplediary.viewmodel

interface IViewModelSettings : IViewModelBase {

    fun onClickedSaveTasks()
    fun onClickedLoadTasks()
    fun onConfirmedSave()
    fun onConfirmedLoad()
    fun onClickedLogout()
}
