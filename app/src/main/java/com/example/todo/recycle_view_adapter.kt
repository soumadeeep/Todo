package com.example.todo

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class recycle_view_adapter(var list: ArrayList<Model_class>, var context: Context) :
    RecyclerView.Adapter<recycle_view_adapter.ViewHolder>() {
    var db: FirebaseDatabase? = null
    var reference: DatabaseReference? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        @SuppressLint("InflateParams") val view = LayoutInflater.from(
            context
        ).inflate(R.layout.recycle_view_model, null, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.title.text = list[position].title
        holder.description.text = list[position].description
        holder.date.text = list[position].date
        holder.keyValue.text = list[position].key
        // if you want to do long press then you want to delete yor list this time you use 'setonLongClickListener'
        holder.delete.setOnClickListener { //this is the alert section all method connected each other through '.'
            val builder = AlertDialog.Builder(context)
                .setTitle("Delete List")
                .setMessage("Are you sure ?")
                .setIcon(R.drawable.deleteicon)
                .setPositiveButton("Yes") { dialog, which ->
                    val value = list[position].key
                    list.removeAt(position)
                    notifyItemRemoved(position)
                    deleteData(value)
                }
                .setNegativeButton("No") { dialog, which -> }
            builder.show()
        }
        holder.update.setOnClickListener {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.custom_layout)
            val edit_title = dialog.findViewById<EditText>(R.id.update_title)
            val edit_description = dialog.findViewById<EditText>(R.id.update_description)
            val edit_date = dialog.findViewById<EditText>(R.id.update_date)
            val edit_KeyValue = dialog.findViewById<EditText>(R.id.key_value)
            val add_button = dialog.findViewById<Button>(R.id.add_button)
            add_button.text = "Update"
            edit_title.setText(list[position].title)
            edit_date.setText(list[position].date)
            edit_KeyValue.setText(list[position].key)
            add_button.setOnClickListener {
                val title: String
                val description: String
                val date: String
                val key: String
                if (edit_title.text.toString() != "" && edit_description.text.toString() != "" && edit_date.text.toString() != "") {
                    title = edit_title.text.toString()
                    description = edit_description.text.toString()
                    date = edit_date.text.toString()
                    key = edit_KeyValue.text.toString()
                    if (key == list[position].key) {
                        UpdateData(title, description, date, key)
                        list[position] = Model_class(title, description, date, key)
                        notifyItemChanged(position)
                        dialog.dismiss()
                    } else {
                        Toast.makeText(context, "Please don't change the key", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(context, "Please fill all the section", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return list.size
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

    fun UpdateData(title: String, description: String, date: String, key: String?) {
        val updateUser = HashMap<String, Any>()
        updateUser["title"] = title
        updateUser["description"] = description
        updateUser["date"] = date
        db = FirebaseDatabase.getInstance()
        reference = db!!.getReference("User")
        reference!!.child(key!!).updateChildren(updateUser)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Data successfully updated
                    Toast.makeText(context, "Your data successfully updated", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    // Failed to update data
                    Toast.makeText(context, "Failed to update data", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun deleteData(value: String?) {
        db = FirebaseDatabase.getInstance()
        reference = db!!.getReference("User")
        reference!!.child(value!!).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Your Data success fully deleted", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(context, "Your Data not deleted", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
