package com.calendar.calendar.view.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.calendar.calendar.databinding.ItemUserBinding
import com.calendar.calendar.model.User

class UserAdapter(
    private val context: Context,
    private val userSelected: (
        user: User
    ) -> Unit,
    private val callSelected: (
        numberPhone: String
    ) -> Unit,
    private val gallerySelected: (
        curp: String
    ) -> Unit,
    private val type: String
) :
    ListAdapter<User, UserViewHolder>(TaskDiffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder =
        UserViewHolder(
            ItemUserBinding.inflate(LayoutInflater.from(context), parent, false),
            ::userClicked,
            ::callClicked,
            ::galleryClicked,
            type
        )

    private fun userClicked(user: User) {
        userSelected.invoke(user)
    }

    private fun callClicked(numberPhone: String) {
        callSelected.invoke(numberPhone)
    }

    private fun galleryClicked(curp: String) {
        gallerySelected.invoke(curp)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) =
        holder.bind(getItem(position), context)

    object TaskDiffCallBack : DiffUtil.ItemCallback<User>() {
        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
            oldItem == newItem

        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean =
            oldItem.curp == newItem.curp
    }

}