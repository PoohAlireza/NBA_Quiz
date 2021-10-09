package com.some_package.nbaquiz.ui.register

import android.content.Context
import androidx.navigation.fragment.NavHostFragment
import com.some_package.nbaquiz.util.MyFragmentFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RegisterNavHostFragment : NavHostFragment(){



    @Inject
    lateinit var myFragmentFactory: MyFragmentFactory

    override fun onAttach(context: Context) {
        super.onAttach(context)
        childFragmentManager.fragmentFactory=myFragmentFactory
    }

}