package com.example.todo

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity() : AppCompatActivity() {
    var toolbar: Toolbar? = null
    var adapter: recycle_view_adapter? = null
    var adapter_search: adapter_for_search? = null
    var list: ArrayList<Model_class>? = null
    var list_search: ArrayList<Model_class>? = null
    var title: TextView? = null
    var description: TextView? = null
    var date: TextView? = null
    var key: TextView? = null
    var recyclerView: RecyclerView? = null
    var recyclerView_search: RecyclerView? = null
    var imageButton: ImageButton? = null
    var db: FirebaseDatabase? = null
    var reference: DatabaseReference? = null
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //        toolbar=findViewById(R.id.first_layout);
//        setSupportActionBar(toolbar);
        title = findViewById(R.id.title_t)
        description = findViewById(R.id.description)
        date = findViewById(R.id.due_date)
        key = findViewById(R.id.key_number)
        // search button functionality
        imageButton = findViewById(R.id.search_button)
        imageButton?.setOnClickListener(View.OnClickListener {
            val dialog = Dialog(this@MainActivity)
            dialog.setContentView(R.layout.searching_screen)
            val search_text = dialog.findViewById<EditText>(R.id.search_bar)
            val button = dialog.findViewById<ImageButton>(R.id.search_button_two)
            button.setOnClickListener {
                val searchText = search_text.text.toString()
                if (!searchText.isEmpty()) {
                    if (recyclerView_search == null) {
                        recyclerView_search =
                            dialog.findViewById(R.id.recycle_view_for_searching_result)
                        recyclerView_search?.setLayoutManager(LinearLayoutManager(this@MainActivity))
                        list_search = ArrayList()
                        adapter_search = adapter_for_search(this@MainActivity, list_search!!)
                        recyclerView_search?.setAdapter(adapter_search)
                    }
                    search_data_fetch(searchText)
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "please enter your list number",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            dialog.show()
        })
        val button = findViewById<FloatingActionButton>(R.id.floating_button)
        button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val dialog = Dialog(this@MainActivity)
                dialog.setContentView(R.layout.custom_layout)
                val edit_title = dialog.findViewById<EditText>(R.id.update_title)
                val edit_description = dialog.findViewById<EditText>(R.id.update_description)
                val edit_date = dialog.findViewById<EditText>(R.id.update_date)
                val edit_key = dialog.findViewById<EditText>(R.id.key_value)
                val add_button = dialog.findViewById<Button>(R.id.add_button)
                add_button.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View) {
                        val title: String
                        val description: String
                        val date: String
                        val key_value: String
                        if (edit_title.text.toString() != "" && edit_description.text.toString() != "" && edit_date.text.toString() != "" && edit_key.text.toString() != "") {
                            title = edit_title.text.toString()
                            description = edit_description.text.toString()
                            date = edit_date.text.toString()
                            key_value = edit_key.text.toString()
                            val modelClass = Model_class(title, description, date, key_value)
                            //firebase start
                            db = FirebaseDatabase.getInstance()
                            reference = db!!.getReference("User")
                            //firebase success method
                            reference!!.child(key_value).setValue(modelClass).addOnCompleteListener(
                                OnCompleteListener { //call the gate method
                                    addData_in_recyclerview(key_value)
                                    Toast.makeText(
                                        this@MainActivity,
                                        "your data success fully added",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                })
                            dialog.dismiss()
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Please fill all the section",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
                dialog.show()
            }
        })
        initRecycleView()
    }

    public override fun onResume() {
        super.onResume()
        add_all_data_in_recyclerview()
    }

    // when we add our one data in recycle view
    fun addData_in_recyclerview(key: String?) {
        db = FirebaseDatabase.getInstance()
        reference = db!!.getReference("User")
        reference!!.child((key)!!).get()
            .addOnCompleteListener(object : OnCompleteListener<DataSnapshot> {
                override fun onComplete(task: Task<DataSnapshot>) {
                    if (task.isSuccessful) {
                        if (task.result.exists()) {
                            val dataSnapshot = task.result
                            val description = dataSnapshot.child("description").value.toString()
                            val date = dataSnapshot.child("date").value.toString()
                            val title = dataSnapshot.child("title").value.toString()
                            val key = dataSnapshot.child("key").value.toString()
                            list!!.add(Model_class(title, description, date, key))
                            adapter!!.notifyItemInserted(list!!.size - 1)
                            recyclerView!!.scrollToPosition(list!!.size - 1)
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "your data are null",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "your data are not fetch",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
    }

    // when we want our whole in our screen when we open our app
    fun add_all_data_in_recyclerview() {
        db = FirebaseDatabase.getInstance()
        reference = db!!.getReference("User")
        reference!!.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    //here we use foreach loop
                    for (snapshot: DataSnapshot in dataSnapshot.children) {
                        val description = snapshot.child("description").value.toString()
                        val date = snapshot.child("date").value.toString()
                        val title = snapshot.child("title").value.toString()
                        val key = snapshot.child("key").value.toString()
                        list!!.add(Model_class(title, description, date, key))
                    }
                    adapter!!.notifyDataSetChanged() // Notify adapter after adding all items
                } else {
                    Toast.makeText(this@MainActivity, "No data found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@MainActivity, "Failed to retrieve data", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    @SuppressLint("SuspiciousIndentation")
    fun initRecycleView() {
        recyclerView = findViewById(R.id.recycle_view)
        list = ArrayList()
        list!!.add(Model_class("Math", "solved", "12/05.24", "23"))
        recyclerView?.setLayoutManager(LinearLayoutManager(this))
        adapter = recycle_view_adapter(list!!, this)
        recyclerView?.setAdapter(adapter)
    }

    //public void Second_Recyclerview(){
    //
    //        recyclerView_search=findViewById(R.id.recycle_view_for_searching_result);
    //        list_search=new ArrayList<>();
    //
    //    list_search.add(new Model_class("Math","solved","12/05.24","23"));
    //    recyclerView_search.setLayoutManager(new LinearLayoutManager(this));
    //
    //        adapter_search=new adapter_for_search(this,list_search);
    //        recyclerView_search.setAdapter(adapter_search);
    //
    //
    //
    //}
    fun search_data_fetch(number: String?) {
        db = FirebaseDatabase.getInstance()
        reference = db!!.getReference("User")
        val query = db!!.reference.orderByChild("title").equalTo(number)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {}
            override fun onCancelled(error: DatabaseError) {}
        })
        reference!!.child((number)!!).get()
            .addOnCompleteListener(object : OnCompleteListener<DataSnapshot> {
                override fun onComplete(task: Task<DataSnapshot>) {
                    if (task.isSuccessful) {
                        if (task.result.exists()) {
                            val dataSnapshot = task.result
                            val title = dataSnapshot.child("title").value.toString()
                            val description = dataSnapshot.child("description").toString()
                            val date = dataSnapshot.child("date").value.toString()
                            val key = dataSnapshot.child("key").value.toString()
                            list_search!!.add(Model_class(title, description, date, key))
                            adapter_search!!.notifyItemInserted(list_search!!.size - 1)
                            recyclerView_search!!.scrollToPosition(list_search!!.size - 1)
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Failed to retrieve data",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        // Toast.makeText(MainActivity.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                    }
                }
            })
    }
}
