package com.some_package.nbaquiz.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.some_package.nbaquiz.R
import com.some_package.nbaquiz.interfaces.OnInviteClicked
import com.some_package.nbaquiz.model.User
import com.some_package.nbaquiz.util.StaticHolder

class RecyclerSearchAdapter(private val context: Context , private val data:List<User> , private val listener:OnInviteClicked) : RecyclerView.Adapter<RecyclerSearchAdapter.SearchItemHolder>()  {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemHolder {
        return SearchItemHolder(LayoutInflater.from(context).inflate(R.layout.item_recycler_search,null))
    }

    override fun onBindViewHolder(holder: SearchItemHolder, position: Int) {
        holder.usernameTV.text = data[position].username
        holder.avatarIV.setImageDrawable(ContextCompat.getDrawable(context,StaticHolder.avatars[data[position].avatar!!]))
        holder.teamTV.text = StaticHolder.teams_name[data[position].team!!]
        holder.pointsTV.text = "${data[position].points}"
        holder.inviteBTN.setOnClickListener {
            listener.onInviteClicked(data[position],position)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }




    class SearchItemHolder(view:View):RecyclerView.ViewHolder(view){
        val usernameTV:TextView = view.findViewById(R.id.tv_username_item_search)
        val avatarIV:ImageView = view.findViewById(R.id.iv_avatar_item_search)
        val teamTV:TextView = view.findViewById(R.id.tv_team_item_search)
        val pointsTV:TextView = view.findViewById(R.id.tv_points_item_search)
        val inviteBTN:Button = view.findViewById(R.id.btn_invite_item_search)
    }

}