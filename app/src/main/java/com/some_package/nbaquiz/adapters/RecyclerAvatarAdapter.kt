package com.some_package.nbaquiz.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.some_package.nbaquiz.R
import com.some_package.nbaquiz.interfaces.OnAvatarSelected

class RecyclerAvatarAdapter(private val context: Context, private val data: ArrayList<Drawable?>, private var index: Int?, private val onAvatarSelected: OnAvatarSelected) : RecyclerView.Adapter<RecyclerAvatarAdapter.AvatarHolder>() {

    private lateinit var previousIV:ImageView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarHolder {
        return AvatarHolder(LayoutInflater.from(context).inflate(R.layout.item_recycler_avatar, null))
    }

    override fun onBindViewHolder(holder: AvatarHolder, position: Int) {
        if (position == index) {
            previousIV = holder.imageView
            holder.imageView.background = ContextCompat.getDrawable(context, R.drawable.shape_circle_selected)
        } else {
            holder.imageView.background = ContextCompat.getDrawable(context, R.drawable.shape_circle_unselected)
        }

        holder.imageView.setImageDrawable(data[position])
        holder.imageView.setOnClickListener {
            if (holder.imageView != previousIV) {
                previousIV.background = ContextCompat.getDrawable(context, R.drawable.shape_circle_unselected)
                holder.imageView.background = ContextCompat.getDrawable(context, R.drawable.shape_circle_selected)
                previousIV = holder.imageView
                index = position
                onAvatarSelected.onSelected(data[position], position)
            }
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }



    class AvatarHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        var imageView:ImageView
        init {
            imageView = view.findViewById(R.id.iv_item_list_avatar)
        }

    }


}