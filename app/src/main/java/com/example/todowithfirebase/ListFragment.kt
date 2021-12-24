package com.example.todowithfirebase

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ListFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    lateinit var database: DatabaseReference
    var toDoItemList = LinkedList<Item>()
    lateinit var adapter: MyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().reference
        adapter = MyAdapter(toDoItemList)
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)

        recyclerView.adapter = adapter
        database.addListenerForSingleValueEvent(itemListener)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.setHasFixedSize(true)

        val addButton = view.findViewById<FloatingActionButton>(R.id.add_button)
        addButton.setOnClickListener {
            val alertDialog = AlertDialog.Builder(view.context)
            val textEditText = EditText(view.context)
            alertDialog.setTitle("Добавить новое дело")
            alertDialog.setMessage("Введите дело")
            alertDialog.setView(textEditText)
            alertDialog.setPositiveButton("Добавить") { dialog, i->
                val currentDate = Date()
                val dateFormat: DateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                val dateText: String = dateFormat.format(currentDate)
                val item = Item.createItem()
                item.case = textEditText.text.toString()
                item.date = dateText
                item.done = false

                val newItemData = database.child(auth.currentUser!!.uid).child("todo").push()
                item.UID = newItemData.key
                newItemData.setValue(item)
                toDoItemList.add(item)
                adapter.notifyItemInserted(toDoItemList.size-1)
                recyclerView.smoothScrollToPosition(toDoItemList.size-1)

                dialog.dismiss()
            }
            alertDialog.setNegativeButton("Отмена"){dialog, i->
                dialog.dismiss()
            }

            alertDialog.show()
        }
        return view
    }

    var itemListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI
            addDataToList(dataSnapshot)
        }
        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Item failed, log a message
            Log.w("ListFragment", "loadItem:onCancelled", databaseError.toException())
        }
    }

    private fun addDataToList(dataSnapshot: DataSnapshot) {
        val items = dataSnapshot.children.iterator()
        Log.e("CURRENT", auth.currentUser!!.uid)
        //Check if current database contains any collection
        if (items.hasNext()) {
            val toDoListIndex = dataSnapshot.child(auth.currentUser!!.uid)
            val itemsIterator = toDoListIndex.child("todo").children.iterator()
            //check if the collection has any to do items or not
            while (itemsIterator.hasNext()) {
                val currentItem = itemsIterator.next()
                Log.e("hfgjk", currentItem.toString())
                val map = currentItem.getValue() as HashMap<String, Any>
                val item = Item.createItem()

                item.UID = currentItem.key
                item.case = map.get("case") as String?
                item.date = map.get("date") as String?
                item.done = map.get("done") as Boolean?
                toDoItemList!!.add(item);
            }
        }
        //alert adapter that has changed
        adapter.notifyDataSetChanged()
    }
}

