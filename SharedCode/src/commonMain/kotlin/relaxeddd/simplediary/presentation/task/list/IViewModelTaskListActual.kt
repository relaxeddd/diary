package relaxeddd.simplediary.presentation.task.list

interface IViewModelTaskListActual : IViewModelTaskList {

    fun onClickedCompleteTask(id: String)
    fun onClickedCompleteChildTask(id: String)
    fun onClickedCompleteParentTask(id: String)
}
