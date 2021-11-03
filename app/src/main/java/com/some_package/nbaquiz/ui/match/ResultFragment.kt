package com.some_package.nbaquiz.ui.match

import android.animation.Animator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.some_package.nbaquiz.R
import com.some_package.nbaquiz.util.StaticHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ResultFragment : Fragment(R.layout.fragment_result) {

    private lateinit var yourUsernameTV:TextView
    private lateinit var rivalUsernameTV:TextView
    private lateinit var yourPointTV:TextView
    private lateinit var rivalPointTV:TextView
    private lateinit var yourTimeTV:TextView
    private lateinit var rivalTimeTV:TextView
    private lateinit var yourCupIV:ImageView
    private lateinit var rivalCupIV:ImageView
    private lateinit var yourAvatarIV:ImageView
    private lateinit var rivalAvatarIV:ImageView
    private lateinit var yourTeamIV:ImageView
    private lateinit var rivalTeamIV:ImageView
    private lateinit var yourCardCL:ConstraintLayout
    private lateinit var rivalCardCL:ConstraintLayout
    private lateinit var yourDetailCL:ConstraintLayout
    private lateinit var rivalDetailCL:ConstraintLayout
    private lateinit var finishBTN:Button
    private var imWinner = false
    private var clickAllowed = false


    private val viewModel:ResultViewModel by viewModels()



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        observeMyInfo()
        getMyInfo()
        setData()
        showData()
        setupFinishButton()
    }

    private fun init(view: View){
        yourUsernameTV = view.findViewById(R.id.tv_username_you_result)
        rivalUsernameTV = view.findViewById(R.id.tv_username_rival_result)
        yourPointTV = view.findViewById(R.id.tv_point_you_result)
        rivalPointTV = view.findViewById(R.id.tv_point_rival_result)
        yourTimeTV = view.findViewById(R.id.tv_time_you_result)
        rivalTimeTV = view.findViewById(R.id.tv_time_rival_result)
        yourCupIV = view.findViewById(R.id.iv_cup_you)
        rivalCupIV = view.findViewById(R.id.iv_cup_rival)
        yourAvatarIV = view.findViewById(R.id.iv_avatar_you_result)
        rivalAvatarIV = view.findViewById(R.id.iv_avatar_rival_result)
        yourTeamIV = view.findViewById(R.id.iv_team_you_result)
        rivalTeamIV = view.findViewById(R.id.iv_team_rival_result)
        yourCardCL = view.findViewById(R.id.cl_result_you)
        rivalCardCL = view.findViewById(R.id.cl_result_rival)
        yourDetailCL = view.findViewById(R.id.cl_parent_result_you)
        rivalDetailCL = view.findViewById(R.id.cl_parent_result_rival)
        finishBTN = view.findViewById(R.id.btn_finish_result)
    }

    private fun getMyInfo(){
        viewModel.initUserData()
    }
    private fun observeMyInfo(){
        viewModel.userData.observe(viewLifecycleOwner, Observer {
            yourUsernameTV.text = it.username
            yourAvatarIV.setImageDrawable(ContextCompat.getDrawable(requireContext(),StaticHolder.avatars[it.avatar!!]))
            yourTeamIV.setImageDrawable(ContextCompat.getDrawable(requireContext(),StaticHolder.team_logo[it.team!!]))
        })
    }
    private fun setData(){
        val bundle = requireArguments()
        val yourPoint = bundle.getInt("point_you")
        val rivalPoint = bundle.getInt("point_rival")
        val yourTime = bundle.getInt("time_you")
        val rivalTime = bundle.getInt("time_rival")

        yourPointTV.text = "Points : $yourPoint"
        yourTimeTV.text = "Time : ${120-yourTime}"

        rivalUsernameTV.text = bundle.getString("username_rival")
        rivalPointTV.text = "Points : $rivalPoint"
        rivalTeamIV.setImageDrawable(ContextCompat.getDrawable(requireContext(),StaticHolder.team_logo[bundle.getInt("team_rival")]))
        rivalAvatarIV.setImageDrawable(ContextCompat.getDrawable(requireContext(),StaticHolder.avatars[bundle.getInt("avatar_rival")]))
        rivalTimeTV.text = "Time :${120-rivalTime}"

        if (yourPoint == rivalPoint){
            if (yourTime > rivalTime){
                yourCupIV.visibility = View.VISIBLE
                imWinner = true
            }
            if (rivalTime > yourTime){
                rivalCupIV.visibility = View.VISIBLE
            }
            if (yourTime == rivalTime){
                rivalCupIV.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_handshake))
                rivalCupIV.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_handshake))
                rivalCupIV.visibility = View.VISIBLE
                yourCupIV.visibility = View.VISIBLE
            }
        }
        if (yourPoint > rivalPoint){
            yourCupIV.visibility = View.VISIBLE
            imWinner = true
        }
        if (rivalPoint > yourPoint){
            rivalCupIV.visibility = View.VISIBLE
        }

        if (imWinner) viewModel.incrementWin()
        viewModel.incrementPoint(yourPoint)

    }
    private fun showData(){
        rivalUsernameTV.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                rivalUsernameTV.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val position1 = getNewImagePosition(yourAvatarIV,yourCardCL)
                yourAvatarIV.animate().translationX(position1[0]).translationY(position1[1]).setDuration(1).start()

                val position2 = getNewImagePosition(rivalAvatarIV,rivalCardCL)
                rivalAvatarIV.animate().translationX(position2[0]).translationY(position2[1]).setDuration(1).start()

                yourDetailCL.animate().alpha(1f).setDuration(1000).start()
                rivalDetailCL.animate().alpha(1f).setDuration(1000).setListener(object : Animator.AnimatorListener{
                    override fun onAnimationStart(animation: Animator?) {

                    }

                    override fun onAnimationEnd(animation: Animator?) {
                       clickAllowed = true
                    }

                    override fun onAnimationCancel(animation: Animator?) {

                    }

                    override fun onAnimationRepeat(animation: Animator?) {

                    }

                }).start()

            }

        })
    }

    private fun getNewImagePosition(iv: ImageView,cl: ConstraintLayout):FloatArray{
        val positions = FloatArray(2)
        val yCenterCL = (cl.top+cl.bottom)/2
        val yCenterIV = (iv.top+iv.bottom)/2
        val xCenterIV = (iv.left+iv.right)/2
        val xPoint = cl.left - xCenterIV
        val yPoint = yCenterCL - yCenterIV
        positions[0] = xPoint.toFloat()
        positions[1] = yPoint.toFloat()
        return positions
    }

    private fun setupFinishButton(){
        finishBTN.setOnClickListener{
            if (clickAllowed) requireActivity().finish()
        }
    }



}