package relaxeddd.simplediary.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import relaxeddd.simplediary.R
import relaxeddd.simplediary.common.OnItemClickListener
import relaxeddd.simplediary.domain.Task

class AdapterTasks(private val clickListener: OnItemClickListener<Task>): ListAdapter<Task, AdapterTasks.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_item_task, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val textTitle = view.findViewById<TextView>(R.id.text_task_title)
        private val textDescription = view.findViewById<TextView>(R.id.text_task_description)

        fun bind(task: Task) {
            textTitle.text = task.title
            textDescription.text = task.desc
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Task>() {

        override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem.title == newItem.title
                && oldItem.desc == newItem.desc
    }
}
