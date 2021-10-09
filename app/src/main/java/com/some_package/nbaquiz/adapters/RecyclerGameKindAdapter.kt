package com.some_package.nbaquiz.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.some_package.nbaquiz.R
import com.some_package.nbaquiz.interfaces.OnKindSelected
import com.some_package.nbaquiz.util.StaticHolder

class RecyclerGameKindAdapter(private val context: Context, private val listener: OnKindSelected) : RecyclerView.Adapter<RecyclerGameKindAdapter.GameKindItemHolder>() {

    private val navigationId:Array<Int> = arrayOf(R.id.action_mainPageFragment_to_findRivalFragment)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameKindItemHolder {
        return GameKindItemHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_recycler_main_page,
                null
            )
        )
    }

    override fun onBindViewHolder(holder: GameKindItemHolder, position: Int) {
        if (position == 0) {
            holder.gameKindTV.text = "Find a rival"
        }
        if (position == navigationId.size) {
            holder.imageView.visibility = View.GONE
            holder.gameKindTV.visibility = View.GONE
            holder.comingSoonTV.visibility = View.VISIBLE
        }
        holder.itemView.setOnClickListener {
            // or use switch case
            var id = -1
            var kind = -1

            if (position == 0) {
                id =  navigationId[position]
                kind = StaticHolder.KIND_FIND_RIVAL
            }

            if (id != -1 && kind != -1) listener.onSelected(kind, id, holder.itemView)
        }
    }

    override fun getItemCount(): Int {
        return navigationId.size+1
    }


    class GameKindItemHolder(view: View):RecyclerView.ViewHolder(view){
        val gameKindTV: TextView = view.findViewById(R.id.tv_game_kind_list)
        val comingSoonTV: TextView = view.findViewById(R.id.tv_coming_soon)
        val imageView: ImageView = view.findViewById(R.id.iv_game_kind_list)
    }

}