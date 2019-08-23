package relaxeddd.simplediary.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import relaxeddd.simplediary.R
import relaxeddd.simplediary.common.*

abstract class ActivityBase<VM : ViewModelBase, B : ViewDataBinding> : AppCompatActivity(), LifecycleOwner {

    private var listenerHomeMenuButton: () -> Unit = {}
    protected lateinit var binding: B
    abstract val viewModel: VM
    var isActivityResumed = false
        private set

    @LayoutRes
    abstract fun getLayoutResId() : Int
    protected open fun setupThemeColors() {}
    protected open fun setLoadingVisible(isVisible: Boolean) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, getLayoutResId())
        configureBinding()
        binding.lifecycleOwner = this
        binding.executePendingBindings()
    }

    override fun onResume() {
        super.onResume()
        isActivityResumed = true
        viewModel.onViewResume()
    }

    override fun onPause() {
        super.onPause()
        isActivityResumed = false
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            listenerHomeMenuButton.invoke()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    open fun onNavigationEvent(type: EventType, args: Bundle? = null) {
        if (type == EventType.PRESS_BACK) {
            onBackPressed()
        }
    }

    fun configureMenu(isShowHomeMenuButton: Boolean = false, homeMenuButtonIconResId: Int, clickListener: () -> Unit, elevation: Float) {
        supportActionBar?.setDisplayHomeAsUpEnabled(isShowHomeMenuButton)
        supportActionBar?.setHomeAsUpIndicator(homeMenuButtonIconResId)
        supportActionBar?.elevation = elevation
        listenerHomeMenuButton = clickListener
    }

    @CallSuper
    protected open fun configureBinding() {
        viewModel.navigateEvent.observe(this, Observer {
            it.getTypeIfNotHandled()?.let {type ->
                onNavigationEvent(type, it?.args)
            }
        })
    }

    private fun setupTheme() {
        when (SharedHelper.getAppThemeType(this)) {
            THEME_STANDARD -> setTheme(R.style.AppTheme)
            THEME_BLUE -> setTheme(R.style.AppTheme2)
            THEME_BLACK -> setTheme(R.style.AppTheme3)
        }
    }
}
