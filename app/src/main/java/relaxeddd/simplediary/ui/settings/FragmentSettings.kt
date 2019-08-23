package relaxeddd.simplediary.ui.settings

import org.koin.androidx.viewmodel.ext.android.viewModel
import relaxeddd.simplediary.R
import relaxeddd.simplediary.databinding.FragmentSettingsBinding
import relaxeddd.simplediary.ui.FragmentBase

class FragmentSettings : FragmentBase<ViewModelSettings, FragmentSettingsBinding>() {

    override fun getLayoutResId() = R.layout.fragment_settings

    override val viewModel: ViewModelSettings by viewModel()

    override fun configureBinding() {
        super.configureBinding()
        binding.viewModel = viewModel
    }
}