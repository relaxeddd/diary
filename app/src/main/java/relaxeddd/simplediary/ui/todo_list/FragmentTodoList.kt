package relaxeddd.simplediary.ui.todo_list

import androidx.lifecycle.Observer
import relaxeddd.simplediary.R
import relaxeddd.simplediary.ui.FragmentBase
import org.koin.androidx.viewmodel.ext.android.viewModel
import relaxeddd.simplediary.common.OnItemClickListener
import relaxeddd.simplediary.common.Task
import relaxeddd.simplediary.databinding.FragmentTodoListBinding
import relaxeddd.simplediary.ui.AdapterTasks

class FragmentTodoList : FragmentBase<ViewModelTodoList, FragmentTodoListBinding>() {

    private lateinit var adapterTasks: AdapterTasks

    override fun getLayoutResId() = R.layout.fragment_todo_list

    override val viewModel: ViewModelTodoList by viewModel()

    override fun configureBinding() {
        super.configureBinding()

        adapterTasks = AdapterTasks(object: OnItemClickListener<Task> {

            override fun onItemClick(item: Task) {

            }
        })

        binding.recyclerViewTodoList.adapter = adapterTasks
        binding.viewModel = viewModel

        viewModel.listTasks.observe(viewLifecycleOwner, Observer { items ->
            items?.let { adapterTasks.submitList(it) }
        })
    }
}