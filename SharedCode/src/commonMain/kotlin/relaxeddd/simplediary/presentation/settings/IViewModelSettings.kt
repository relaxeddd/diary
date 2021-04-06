package relaxeddd.simplediary.presentation.settings

import relaxeddd.simplediary.presentation.base.IViewModelBase

interface IViewModelSettings : IViewModelBase {

    fun onClickedSaveTasks()
    fun onClickedLoadTasks()
    fun onConfirmedSave()
    fun onConfirmedLoad()
    fun onClickedLogout()
}
