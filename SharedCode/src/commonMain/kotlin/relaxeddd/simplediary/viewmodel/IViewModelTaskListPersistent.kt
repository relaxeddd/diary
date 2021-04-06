package relaxeddd.simplediary.viewmodel

interface IViewModelTaskListPersistent : IViewModelTaskList {

    fun onClickedCompleteTask(id: String)
    fun onClickedRestoreTask(id: String)
}
