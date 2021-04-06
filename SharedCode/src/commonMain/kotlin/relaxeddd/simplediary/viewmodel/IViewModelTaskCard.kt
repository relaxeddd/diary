package relaxeddd.simplediary.viewmodel

import relaxeddd.simplediary.utils.observable.Observable

interface IViewModelTaskCard : IViewModelTask {

    val isEnabledButtonSave: Observable<Boolean>
    val taskTitle: Observable<String>
    val taskDesc: Observable<String>
    val taskComment: Observable<String>
    val taskLocation: Observable<String>
    val taskPriority: Observable<Int>
    val taskRepeat: Observable<Int>
    val taskRepeatCount: Observable<Int>
    val taskStart: Observable<Long>
    val taskEnd: Observable<Long>
    val taskUntil: Observable<Long>
    val taskIsPersistent: Observable<Boolean>
    val taskIsCompleted: Observable<Boolean>
    val taskExDates: Observable<List<Long>>
    val taskRemindHours: Observable<List<Int>>

    fun load(editTaskParentId: String?, startDate: Long)
    fun onClickedSave()
    fun onClickedCompleteChildTask()
    fun onClickedCompleteParentTask()
    fun onClickedCancel()
    fun onChangedTitle(value: String)
    fun onChangedDesc(value: String)
    fun onChangedComment(value: String)
    fun onChangedLocation(value: String)
    fun onChangedPriority(value: Int)
    fun onChangedRepeat(value: Int)
    fun onChangedRepeatCount(value: Int)
    fun onChangedStart(value: Long)
    fun onChangedEnd(value: Long)
    fun onChangedUntil(value: Long)
    fun onChangedIsPersistent(value: Boolean)
    fun onChangedIsCompleted(value: Boolean)
    fun onAddRemindHour(value: Int)
    fun onRemoveRemindHour(value: Int)
}
