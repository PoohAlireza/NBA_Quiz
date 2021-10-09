package com.some_package.nbaquiz.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.some_package.nbaquiz.R
import com.some_package.nbaquiz.adapters.RecyclerGameKindAdapter
import com.some_package.nbaquiz.interfaces.OnKindSelected
import com.some_package.nbaquiz.util.StaticHolder


class MainPageFragment : Fragment(R.layout.fragment_main_page) {

    private lateinit var recyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    private fun init(view: View){
        recyclerView = view.findViewById(R.id.rv_main_page_fragment)
        recyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        recyclerView.adapter = RecyclerGameKindAdapter(requireContext(), object : OnKindSelected {
            override fun onSelected(kind: Int, layoutId: Int, clickedItem: View) {
                Navigation.findNavController(clickedItem).navigate(layoutId)
                StaticHolder.onGameKindPageSelected!!.onSelected(kind)
            }

        })
    }


}