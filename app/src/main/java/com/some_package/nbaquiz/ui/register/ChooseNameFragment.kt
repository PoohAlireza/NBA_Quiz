package com.some_package.nbaquiz.ui.register

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.some_package.nbaquiz.R
import com.some_package.nbaquiz.util.DataState
import com.some_package.nbaquiz.util.StaticHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ChooseNameFragment : Fragment(R.layout.fragment_choose_name) {

    private lateinit var usernameET: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var errorTV: TextView
    private lateinit var nextBTN: Button
    private val viewModel:RegisterViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        observes()
        setupNextButton()
    }


    private fun init(view: View){
        usernameET = view.findViewById(R.id.et_choose_name)
        progressBar = view.findViewById(R.id.progress_choose_name)
        errorTV = view.findViewById(R.id.tv_error_choose_name)
        nextBTN = view.findViewById(R.id.btn_next_choose_name)
    }
    private fun observes(){
        viewModel.dataStateCheckUsername.observe(viewLifecycleOwner, Observer {
            when(it){
                is DataState.Loading ->{
                    displayProgress(View.VISIBLE)
                }
                is DataState.Error ->{
                    displayProgress(View.GONE)
                    displayError(it.exception.message)
                }
                is DataState.Success ->{
                    displayProgress(View.GONE)
                    letsGoToChooseAvatar(it.data)
                }
            }
        })
    }
    private fun setupNextButton(){
        nextBTN.setOnClickListener {
            if (progressBar.visibility == View.GONE){
                StaticHolder.fullScreen(activity)
                val username:String = usernameET.text.toString()
                viewModel.checkUsername(username)
            }
        }
    }
    private fun displayProgress(display:Int){
        progressBar.visibility = display
    }
    private fun displayError(message:String?){
        errorTV.text = message
        usernameET.setText("")
        usernameET.requestFocus()
        CoroutineScope(Main).launch {
            delay(3000)
            errorTV.text = ""
        }
    }

    private fun letsGoToChooseAvatar(username:String?){
        val bundle = Bundle()
        bundle.putString("username",username)
        Navigation.findNavController(nextBTN).navigate(R.id.action_chooseNameFragment_to_chooseAvatarFragment,bundle)
    }


}