package relaxeddd.simplediary.ui.todo_list

import relaxeddd.simplediary.R
import relaxeddd.simplediary.ui.FragmentBase
import org.koin.androidx.viewmodel.ext.android.viewModel
import relaxeddd.simplediary.databinding.FragmentTodoListBinding

class FragmentTodoList : FragmentBase<ViewModelTodoList, FragmentTodoListBinding>() {

    override fun getLayoutResId() = R.layout.fragment_todo_list

    override val viewModel: ViewModelTodoList by viewModel()

    override fun configureBinding() {
        super.configureBinding()
        binding.viewModel = viewModel
    }
}