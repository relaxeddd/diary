package relaxeddd.simplediary.ui.todo_list

import relaxeddd.simplediary.R
import relaxeddd.simplediary.ui.FragmentBase
import relaxeddd.simplediary.common.OnItemClickListener
import relaxeddd.simplediary.common.visibleOrGone
import relaxeddd.simplediary.databinding.FragmentTaskListBinding
import relaxeddd.simplediary.domain.Task
import relaxeddd.simplediary.ui.AdapterTasks
import relaxeddd.simplediary.presentation.task.list.ViewModelTaskListActual

class FragmentTaskList : FragmentBase<ViewModelTaskListActual, FragmentTaskListBinding>() {

    private lateinit var adapterTasks: AdapterTasks

    override fun getLayoutResId() = R.layout.fragment_task_list

    override val viewModel by lazy { ViewModelTaskListActual() }

    override fun onStart() {
        super.onStart()
        viewModel.onFill()

        viewModel.isVisibleProgressBar.addObserver {
            binding.progressBarTaskList.visibleOrGone(it)
        }
        viewModel.isVisibleTextNoItems.addObserver {
            binding.textTaskListNoItems.visibleOrGone(it)
        }
        viewModel.isVisibleTaskList.addObserver {
            binding.recyclerViewTaskList.visibleOrGone(it)
        }
        viewModel.tasks.addObserver {
            adapterTasks.submitList(it)
        }
    }

    override fun onStop() {
        super.onStop()
        if (activity == null || activity?.isFinishing == true) {
            viewModel.onCleared()
        }
    }

    override fun configureBinding() {
        super.configureBinding()

        adapterTasks = AdapterTasks(object: OnItemClickListener<Task> {

            override fun onItemClick(item: Task) {

            }
        })

        binding.recyclerViewTaskList.adapter = adapterTasks
        binding.viewModel = viewModel

        viewModel.load()
    }
}
