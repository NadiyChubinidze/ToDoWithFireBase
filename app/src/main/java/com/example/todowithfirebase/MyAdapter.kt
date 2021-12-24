package com.example.todowithfirebase

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.util.*


class MyAdapter(private val list:LinkedList<Item>): RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val caseText: TextView = itemView.findViewById(R.id._case)
        val dateText: TextView = itemView.findViewById(R.id.date)
        val checkDone: CheckBox = itemView.findViewById(R.id.checkDone)

        private var database: DatabaseReference = FirebaseDatabase.getInstance().reference
        private var auth: FirebaseAuth = Firebase.auth
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteItem)
        private var mAdapter: MyAdapter? = null

        constructor(itemView: View, adapter: MyAdapter) : this(itemView) {
            mAdapter = adapter
            deleteButton.setOnClickListener(View.OnClickListener {
                val mPosition = layoutPosition
                val item = adapter.list[mPosition]
                val itemReference = database.child(auth.currentUser!!.uid).child("todo").child(item.UID!!)
                itemReference.removeValue()
                adapter.list.removeAt(mPosition)
                adapter.notifyItemRemoved(mPosition)
            })
            checkDone.setOnClickListener {
                val mPosition = layoutPosition
                adapter.list[mPosition].done = checkDone.isChecked
                val item = adapter.list[mPosition]
                val itemReference = database.child(auth.currentUser!!.uid).child("todo").child(item.UID!!)
                itemReference.child("done").setValue(adapter.list[mPosition].done)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.custom_item,
            parent,false)
        return MyViewHolder(itemView, this)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = list[position]
        holder.caseText.text = currentItem.case
        holder.dateText.text = currentItem.date
        holder.checkDone.isChecked = currentItem.done!!
    }

    override fun getItemCount(): Int {
        return list.size
    }
}