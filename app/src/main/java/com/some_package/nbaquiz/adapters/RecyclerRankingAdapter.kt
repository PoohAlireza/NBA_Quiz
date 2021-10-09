package com.some_package.nbaquiz.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.some_package.nbaquiz.R
import com.some_package.nbaquiz.model.User
import com.some_package.nbaquiz.util.StaticHolder

class RecyclerRankingAdapter(private val context: Context, private val usersList: List<User>) : RecyclerView.Adapter<RecyclerRankingAdapter.RankItemHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankItemHolder {
        return RankItemHolder(LayoutInflater.from(context).inflate(R.layout.item_recycler_rank, null))
    }

    override fun onBindViewHolder(holder: RankItemHolder, position: Int) {
        val user = usersList[position]
        holder.avatarIV.setImageDrawable(ContextCompat.getDrawable(context, StaticHolder.avatars[user.avatar!!]))
        holder.teamIV.setImageDrawable(ContextCompat.getDrawable(context, StaticHolder.team_logo[user.team!!]))
        holder.rankTV.text = "${position+4}"
        holder.usernameTV.text = user.username
        holder.pointsTV.text = "${user.points}"
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    class RankItemHolder(view: View):RecyclerView.ViewHolder(view){
        val rankTV: TextView = view.findViewById(R.id.tv_rank_rank_list);
        val usernameTV: TextView = view.findViewById(R.id.tv_username_rank_list);
        val pointsTV: TextView = view.findViewById(R.id.tv_points_rank_list);
        val avatarIV: ImageView = view.findViewById(R.id.iv_avatar_rank_list);
        val teamIV: ImageView = view.findViewById(R.id.iv_team_rank_list);
    }

}