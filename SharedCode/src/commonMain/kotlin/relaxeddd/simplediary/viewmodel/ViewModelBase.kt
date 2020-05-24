package relaxeddd.simplediary.viewmodel

import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import relaxeddd.simplediary.domain.model.NavigationEvent

open class ViewModelBase : ViewModel() {

    val navigateEvent = MutableLiveData<NavigationEvent?>(null)
}