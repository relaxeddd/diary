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
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LifecycleOwner
import relaxeddd.simplediary.common.*

abstract class ActivityBase<B : ViewDataBinding> : AppCompatActivity(), LifecycleOwner {

    private var listenerHomeMenuButton: () -> Unit = {}
    protected lateinit var binding: B
    var isActivityResumed = false
        private set

    @LayoutRes
    abstract fun getLayoutResId() : Int
    protected open fun setupThemeColors() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setupTheme()

        binding = DataBindingUtil.setContentView(this, getLayoutResId())
        configureBinding()
        binding.lifecycleOwner = this
        binding.executePendingBindings()

        setupThemeColors()
    }

    override fun onResume() {
        super.onResume()
        isActivityResumed = true
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

    fun openWebPrivacyPolicy() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/pushenglish"))
        browserIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        this.startActivity(browserIntent)
    }

    fun openWebApplication() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=relaxeddd.englishnotify"))
        browserIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        this.startActivity(browserIntent)
    }

    @CallSuper
    protected open fun configureBinding() {}

    protected fun showDialog(dialog: DialogFragment) {
        if (isActivityResumed) {
            dialog.show(supportFragmentManager, dialog.javaClass.simpleName)
        }
    }

    protected fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun setupTheme() {
        //setTheme(viewModel.appTheme)
    }
}
