package com.some_package.nbaquiz.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.some_package.nbaquiz.ui.main.FindRivalFragment
import com.some_package.nbaquiz.ui.main.MainPageFragment
import com.some_package.nbaquiz.ui.main.ProfileFragment
import com.some_package.nbaquiz.ui.main.RankingFragment
import com.some_package.nbaquiz.ui.register.ChooseAvatarFragment
import com.some_package.nbaquiz.ui.register.ChooseNameFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class MyFragmentFactory  : FragmentFactory() {



    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className){

            ChooseNameFragment::javaClass.name -> ChooseNameFragment()
            ChooseAvatarFragment::javaClass.name -> ChooseAvatarFragment()

            MainPageFragment::javaClass.name -> MainPageFragment()
            RankingFragment::javaClass.name -> RankingFragment()
            ProfileFragment::javaClass.name -> ProfileFragment()
            FindRivalFragment::javaClass.name -> FindRivalFragment()

            else ->super.instantiate(classLoader, className)
        }

    }

}