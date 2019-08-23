package relaxeddd.simplediary.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import relaxeddd.simplediary.common.EventType
import relaxeddd.simplediary.ui.main.ActivityMain

abstract class FragmentBase<VM : ViewModelBase, B : ViewDataBinding> : Fragment() {

    protected lateinit var binding: B
    abstract val viewModel: VM

    @LayoutRes
    abstract fun getLayoutResId() : Int
    protected open fun isVisibleStatusBar() = true
    protected open fun getStatusBarColor() = android.R.color.white

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutResId(), null, false)

        configureBinding()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.executePendingBindings()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.onViewResume()
    }

    protected open fun onNavigationEvent(type: EventType, args: Bundle? = null) {
        if (activity is ActivityMain) {
            (activity as ActivityMain).onNavigationEvent(type, args)
        }
    }

    @CallSuper
    protected open fun configureBinding() {
        viewModel.navigateEvent.observe(this, Observer {
            it.getTypeIfNotHandled()?.let {type ->
                onNavigationEvent(type, it?.args)
            }
        })
    }
}