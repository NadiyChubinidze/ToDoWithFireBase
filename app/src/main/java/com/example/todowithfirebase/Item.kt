package com.example.todowithfirebase


class Item{

    companion object Factory{
        fun createItem(): Item = Item()
    }
    var UID: String? = null
    var case: String? = null
    var date: String? = null
    var done: Boolean? = false


}