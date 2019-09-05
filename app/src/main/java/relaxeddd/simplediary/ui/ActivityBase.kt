package relaxeddd.simplediary.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTheme()

        binding = DataBindingUtil.setContentView(this, getLayoutResId())
        configureBinding()
        binding.lifecycleOwner = this
        binding.executePendingBindings()

        setupThemeColors()
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

    fun FragmentActivity.openWebPrivacyPolicy() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/pushenglish"))
        browserIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        this.startActivity(browserIntent)
    }

    fun FragmentActivity.openWebApplication() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=relaxeddd.englishnotify"))
        browserIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        this.startActivity(browserIntent)
    }

    @CallSuper
    protected open fun configureBinding() {
        viewModel.navigateEvent.observe(this, Observer {
            it.getTypeIfNotHandled()?.let {type ->
                onNavigationEvent(type, it?.args)
            }
        })
    }

    protected fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    protected fun getPrimaryColorResId() = when (SharedHelper.getAppThemeType(this)) {
        THEME_BLUE -> R.color.colorPrimary2
        THEME_BLACK -> R.color.colorPrimary3
        else -> R.color.colorPrimary
    }

    protected fun getPrimaryDarkColorResId() = when (SharedHelper.getAppThemeType(this)) {
        THEME_BLUE -> R.color.colorPrimaryDark2
        THEME_BLACK -> R.color.colorPrimary3
        else -> R.color.colorPrimaryDark
    }

    protected fun getAccentColorResId() = when (SharedHelper.getAppThemeType(this)) {
        THEME_BLUE -> R.color.colorAccent2
        THEME_BLACK -> R.color.colorPrimary3
        else -> R.color.colorPrimary
    }

    private fun setupTheme() {
        when (SharedHelper.getAppThemeType(this)) {
            THEME_STANDARD -> setTheme(R.style.AppTheme)
            THEME_BLUE -> setTheme(R.style.AppTheme2)
            THEME_BLACK -> setTheme(R.style.AppTheme3)
        }
    }
}
