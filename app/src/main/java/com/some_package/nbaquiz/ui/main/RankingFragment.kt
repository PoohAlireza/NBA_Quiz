package com.some_package.nbaquiz.ui.main

import android.animation.Animator
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.some_package.nbaquiz.R
import com.some_package.nbaquiz.adapters.RecyclerRankingAdapter
import com.some_package.nbaquiz.custom_view.CustomLoading
import com.some_package.nbaquiz.model.User
import com.some_package.nbaquiz.util.DataState
import com.some_package.nbaquiz.util.StaticHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RankingFragment : Fragment(R.layout.fragment_ranking) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var username_1_TV: TextView
    private lateinit var username_2_TV: TextView
    private lateinit var username_3_TV: TextView
    private lateinit var points_1_TV: TextView
    private lateinit var points_2_TV: TextView
    private lateinit var points_3_TV: TextView
    private lateinit var avatar_1_IV: ImageView
    private lateinit var avatar_2_IV: ImageView
    private lateinit var avatar_3_IV: ImageView
    private lateinit var team_1_IV:ImageView
    private lateinit var team_2_IV:ImageView
    private lateinit var team_3_IV:ImageView
    private lateinit var card_1_CL: ConstraintLayout
    private lateinit var card_2_CL: ConstraintLayout
    private lateinit var card_3_CL: ConstraintLayout
    private lateinit var rank_1_RL: RelativeLayout
    private lateinit var rank_2_RL: RelativeLayout
    private lateinit var rank_3_RL: RelativeLayout
    private lateinit var progress:CustomLoading

    private val viewModel:MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        observeRanks()
        getRanks()
    }

    private fun init(view: View){
        username_1_TV = view.findViewById(R.id.tv_username_rank_1_ranking_fragment)
        username_2_TV = view.findViewById(R.id.tv_username_rank_2_ranking_fragment)
        username_3_TV = view.findViewById(R.id.tv_username_rank_3_ranking_fragment)
        points_1_TV = view.findViewById(R.id.tv_points_rank_1_ranking_fragment)
        points_2_TV = view.findViewById(R.id.tv_points_rank_2_ranking_fragment)
        points_3_TV = view.findViewById(R.id.tv_points_rank_3_ranking_fragment)
        avatar_1_IV = view.findViewById(R.id.iv_rank_1_ranking_fragment)
        avatar_2_IV = view.findViewById(R.id.iv_rank_2_ranking_fragment)
        avatar_3_IV = view.findViewById(R.id.iv_rank_3_ranking_fragment)
        card_1_CL = view.findViewById(R.id.cl_rank_1)
        card_2_CL = view.findViewById(R.id.cl_rank_2)
        card_3_CL = view.findViewById(R.id.cl_rank_3)
        rank_1_RL = view.findViewById(R.id.view_rank_1_corner)
        rank_2_RL = view.findViewById(R.id.view_rank_2_corner)
        rank_3_RL = view.findViewById(R.id.view_rank_3_corner)
        team_1_IV = view.findViewById(R.id.iv_team_rank_1)
        team_2_IV = view.findViewById(R.id.iv_team_rank_2)
        team_3_IV = view.findViewById(R.id.iv_team_rank_3)
        recyclerView = view.findViewById(R.id.rv_ranking_fragment)
        progress = view.findViewById(R.id.loading_ranking)
        /////
        /////
        card_1_CL.alpha = 0f
        card_2_CL.alpha = 0f
        card_3_CL.alpha = 0f
        rank_1_RL.alpha = 0f
        rank_2_RL.alpha = 0f
        rank_3_RL.alpha = 0f
        /////
    }

    private fun setupRecyclerRank(users:List<User>){
        recyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        recyclerView.adapter = RecyclerRankingAdapter(requireContext(),users)
    }

    private fun getRanks(){
        viewModel.getRanks()
    }

    private fun setTopGuysProperties(users: List<User>){
        username_1_TV.text = users[0].username
        username_2_TV.text = users[1].username
        username_3_TV.text = users[2].username
        points_1_TV.text = "${users[0].points}"
        points_2_TV.text = "${users[1].points}"
        points_3_TV.text = "${users[2].points}"
        avatar_1_IV.setImageDrawable(ContextCompat.getDrawable(requireContext(),StaticHolder.avatars[users[0].avatar!!]))
        avatar_2_IV.setImageDrawable(ContextCompat.getDrawable(requireContext(),StaticHolder.avatars[users[1].avatar!!]))
        avatar_3_IV.setImageDrawable(ContextCompat.getDrawable(requireContext(),StaticHolder.avatars[users[2].avatar!!]))
        team_1_IV.setImageDrawable(ContextCompat.getDrawable(requireContext(),StaticHolder.team_logo[users[0].team!!]))
        team_2_IV.setImageDrawable(ContextCompat.getDrawable(requireContext(),StaticHolder.team_logo[users[1].team!!]))
        team_3_IV.setImageDrawable(ContextCompat.getDrawable(requireContext(),StaticHolder.team_logo[users[2].team!!]))
    }

    private fun observeRanks(){
        viewModel.dataStateRanks.observe(viewLifecycleOwner, Observer {
            when(it){
                is DataState.Success ->{
                    progress.visibility = View.GONE
                    if (it.data!!.size>3){
                        val usersList = it.data.subList(3,it.data.size)
                        setupRecyclerRank(usersList)
                    }
                    setTopGuysProperties(users = it.data)
                    animCards()
                }
                is DataState.Error ->{

                }
                is DataState.Loading ->{
                    progress.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun animCards() {
        val observer = rank_3_RL.viewTreeObserver
        observer.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                observer.removeOnGlobalLayoutListener(this)
                doAnim(card_1_CL, rank_1_RL)
                doAnim(card_2_CL, rank_2_RL)
                doAnim(card_3_CL, rank_3_RL)
            }
        })
    }
    private fun doAnim(cl: ConstraintLayout, rank: RelativeLayout) {
        val location = calculateCorner(cl, rank)
        rank.animate().translationX(location[0]).translationY(location[1]).setDuration(1)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    cl.animate().alpha(1f).duration = 500
                    rank.animate().alpha(1f).duration = 500
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
    }
    private fun calculateCorner(cl: ConstraintLayout, rl: RelativeLayout): FloatArray {
        val location = FloatArray(2)
        val xCenterRL = ((rl.left + rl.right) / 2).toFloat()
        val yCenterRL = ((rl.top + rl.bottom) / 2).toFloat()
        val xPoint = cl.right - xCenterRL
        val yPoint = cl.top - yCenterRL
        location[0] = xPoint
        location[1] = yPoint
        return location
    }


}