package com.some_package.nbaquiz.custom_view

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.some_package.nbaquiz.R
import com.some_package.nbaquiz.adapters.RecyclerAvatarAdapter
import com.some_package.nbaquiz.interfaces.OnApplyProfileEditClicked
import com.some_package.nbaquiz.interfaces.OnAvatarSelected
import com.some_package.nbaquiz.util.StaticHolder
import java.util.*

class BottomSheetEdit(private var avatarIndex:Int , private var teamIndex:Int , private val listener:OnApplyProfileEditClicked) : BottomSheetDialogFragment() {

    private lateinit var avatarRV: RecyclerView
    private lateinit var teamRV: RecyclerView
    private lateinit var avatarsList: ArrayList<Drawable?>
    private lateinit var teamsList: ArrayList<Drawable?>
    private lateinit var applyBTN:Button
    private lateinit var cancelBTN:Button


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_bottom_edit,null)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }



    private fun init(view:View) {
        avatarRV = view.findViewById(R.id.rv_avatars)
        teamRV = view.findViewById(R.id.rv_teams)
        applyBTN = view.findViewById(R.id.btn_apply_edit)
        cancelBTN = view.findViewById(R.id.btn_cancel_edit)

        initLists()
        setupRecycler()
        setupCancelButton()
        setupApplyButton()
    }


    override fun onResume() {
        super.onResume()

        val scrollToUser = if (avatarIndex >2) avatarIndex-2 else avatarIndex
        avatarRV.scrollToPosition(scrollToUser)
        val scrollToTeam = if (teamIndex >2) teamIndex-2 else teamIndex
        teamRV.scrollToPosition(scrollToTeam)
    }

    private fun setupRecycler() {
        avatarRV.layoutManager =LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        avatarRV.adapter = RecyclerAvatarAdapter(requireContext(), avatarsList, avatarIndex,object : OnAvatarSelected{
            override fun onSelected(image: Drawable?, index: Int) {
               avatarIndex = index
            }

        })

        teamRV.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        teamRV.adapter = RecyclerAvatarAdapter(requireContext(),teamsList,teamIndex,object : OnAvatarSelected{
            override fun onSelected(image: Drawable?, index: Int) {
                teamIndex = index
            }

        })

    }

    private fun setupCancelButton(){
        cancelBTN.setOnClickListener {
            dismiss()
        }
    }

    private fun setupApplyButton(){
        applyBTN.setOnClickListener {
            listener.onClicked(avatarIndex,teamIndex)
        }
    }

    private fun initLists(){
        initAvatars()
        initTeams()
    }
    private fun initAvatars(){
        avatarsList = ArrayList()
        for (i in 0..11){
            avatarsList.add(ContextCompat.getDrawable(requireContext(), StaticHolder.avatars[i]))
        }
    }
    private fun initTeams(){
        teamsList = ArrayList()
        for(i in 0..29){
            teamsList.add(ContextCompat.getDrawable(requireContext(), StaticHolder.team_logo[i]))
        }
    }



}