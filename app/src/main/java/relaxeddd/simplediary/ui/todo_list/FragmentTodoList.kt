package relaxeddd.simplediary.ui.todo_list

import androidx.lifecycle.ViewModelProviders
import relaxeddd.simplediary.R
import relaxeddd.simplediary.ui.FragmentBase
import relaxeddd.simplediary.common.OnItemClickListener
import relaxeddd.simplediary.databinding.FragmentTodoListBinding
import relaxeddd.simplediary.domain.model.Task
import relaxeddd.simplediary.ui.AdapterTasks
import relaxeddd.simplediary.viewmodel.LoadingTaskListState
import relaxeddd.simplediary.viewmodel.SuccessTaskListState
import relaxeddd.simplediary.viewmodel.ViewModelTaskList

class FragmentTodoList : FragmentBase<ViewModelTaskList, FragmentTodoListBinding>() {

    private lateinit var adapterTasks: AdapterTasks

    override fun getLayoutResId() = R.layout.fragment_todo_list

    override val viewModel: ViewModelTaskList by lazy {
        ViewModelProviders.of(this).get(ViewModelTaskList::class.java)
    }

    override fun configureBinding() {
        super.configureBinding()

        adapterTasks = AdapterTasks(object: OnItemClickListener<Task> {

            override fun onItemClick(item: Task) {

            }
        })

        binding.recyclerViewTodoList.adapter = adapterTasks
        binding.viewModel = viewModel

        viewModel.listTasks.addObserver { state ->
            when(state) {
                is LoadingTaskListState -> {

                }
                is SuccessTaskListState -> {
                    adapterTasks.submitList(state.response.data)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadTasks()
    }
}