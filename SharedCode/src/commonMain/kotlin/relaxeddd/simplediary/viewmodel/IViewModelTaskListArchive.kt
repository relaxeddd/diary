package relaxeddd.simplediary.viewmodel

interface IViewModelTaskListArchive : IViewModelTaskList {

    fun restoreTask(id: String)
}
