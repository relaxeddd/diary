package relaxeddd.simplediary.presentation.task.list

import relaxeddd.simplediary.domain.Task
import relaxeddd.simplediary.domain.observable.Observable
import relaxeddd.simplediary.presentation.task.IViewModelTask

interface IViewModelTaskList : IViewModelTask {

    val tasks: Observable<List<Task>>
    val isVisibleTextNoItems: Observable<Boolean>
    val isVisibleTaskList: Observable<Boolean>

    fun load()
    fun deleteTask(id: String)
}
