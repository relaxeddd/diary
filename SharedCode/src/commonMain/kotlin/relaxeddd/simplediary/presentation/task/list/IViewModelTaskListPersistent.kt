package relaxeddd.simplediary.presentation.task.list

interface IViewModelTaskListPersistent : IViewModelTaskList {

    fun onClickedCompleteTask(id: String)
    fun onClickedRestoreTask(id: String)
}
