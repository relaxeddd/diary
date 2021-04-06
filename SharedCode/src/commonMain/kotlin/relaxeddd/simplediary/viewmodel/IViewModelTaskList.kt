package relaxeddd.simplediary.viewmodel

import relaxeddd.simplediary.domain.model.Task
import relaxeddd.simplediary.utils.observable.Observable

interface IViewModelTaskList : IViewModelTask {

    val tasks: Observable<List<Task>>
    val isVisibleTextNoItems: Observable<Boolean>
    val isVisibleTaskList: Observable<Boolean>

    fun load()
    fun deleteTask(id: String)
}
