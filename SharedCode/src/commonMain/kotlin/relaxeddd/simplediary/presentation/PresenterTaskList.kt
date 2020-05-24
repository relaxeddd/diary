package relaxeddd.simplediary.presentation

import kotlinx.coroutines.launch
import relaxeddd.simplediary.ApplicationDispatcher
import relaxeddd.simplediary.data.TaskModel
import relaxeddd.simplediary.domain.Response
import relaxeddd.simplediary.domain.usecase.task.UseCaseTaskGetList
import kotlin.coroutines.CoroutineContext

class PresenterTaskList(private var useCaseTaskGetList: UseCaseTaskGetList,
                        coroutineContext: CoroutineContext = ApplicationDispatcher)
        : BasePresenter<ViewTaskList>(coroutineContext) {

    override fun onViewAttached(view: ViewTaskList) {

    }

    /*fun getTasksList() {
        view?.showHideLoading(true)
        scope.launch {
            val response = useCaseTaskGetList.execute()
            if (response is Response.Success) {
                view?.onSuccessGetTasksList(response.data)
            } else if (response is Response.Error){
                view?.onErrorGetTasksList(response.exception)
            }
            view?.showHideLoading(false)
        }
    }*/
}

interface ViewTaskList {

    fun onSuccessGetTasksList(tasks: List<TaskModel>)
    fun onErrorGetTasksList(throwable: Throwable)

    fun showHideLoading(isVisible: Boolean)
}