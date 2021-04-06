package relaxeddd.simplediary.presentation.task.list

interface IViewModelTaskListArchive : IViewModelTaskList {

    fun restoreTask(id: String)
}
