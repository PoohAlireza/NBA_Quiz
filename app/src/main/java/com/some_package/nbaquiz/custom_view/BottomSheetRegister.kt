package com.some_package.nbaquiz.custom_view

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.some_package.nbaquiz.R
import com.some_package.nbaquiz.adapters.RecyclerAvatarAdapter
import com.some_package.nbaquiz.interfaces.OnAvatarSelected
import com.some_package.nbaquiz.model.User
import com.some_package.nbaquiz.util.StaticHolder
import java.util.*

class BottomSheetRegister(private val onAvatarSelected: OnAvatarSelected, private val onTeamSelected: OnAvatarSelected) : BottomSheetDialogFragment() {

    private lateinit var avatarRV: RecyclerView
    private lateinit var teamRV: RecyclerView
    private lateinit var avatarsList: ArrayList<Drawable?>
    private lateinit var teamsList: ArrayList<Drawable?>

    private var avatarIndex:Int = 0
    private var teamIndex:Int = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_bottom_register,null)
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
        initLists()
        setupRecycler()
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
        avatarRV.adapter = RecyclerAvatarAdapter(requireContext(), avatarsList, avatarIndex,onAvatarSelected)

        teamRV.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        teamRV.adapter = RecyclerAvatarAdapter(requireContext(),teamsList,teamIndex,onTeamSelected)

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

    fun avatarIndexChanged(avatarIndex:Int){
        this.avatarIndex = avatarIndex
    }

    fun teamIndexChanged(teamIndex:Int){
        this.teamIndex = teamIndex
    }

}