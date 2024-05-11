package com.example.todo

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class adapter_for_search(var context: Context, var list_search: ArrayList<Model_class>) :
    RecyclerView.Adapter<adapter_for_search.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        @SuppressLint("InflateParams") val view = LayoutInflater.from(
            context
        ).inflate(R.layout.recycle_view_model, null, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = list_search[position].title
        holder.description.text = list_search[position].description
        holder.date.text = list_search[position].date
        holder.keyValue.text = list_search[position].key
    }

    override fun getItemCount(): Int {
        return list_search.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView
        var description: TextView
        var date: TextView
        var update: TextView
        var delete: TextView
        var keyValue: TextView

        init {
            title = itemView.findViewById(R.id.title_t)
            description = itemView.findViewById(R.id.description)
            date = itemView.findViewById(R.id.due_date)
            update = itemView.findViewById(R.id.for_update)
            delete = itemView.findViewById(R.id.for_delete)
            keyValue = itemView.findViewById(R.id.key_number)
        }
    }
}
