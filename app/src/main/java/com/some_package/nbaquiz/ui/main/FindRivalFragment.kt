package com.some_package.nbaquiz.ui.main

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.some_package.nbaquiz.R
import com.some_package.nbaquiz.adapters.RecyclerSearchAdapter
import com.some_package.nbaquiz.interfaces.OnInviteClicked
import com.some_package.nbaquiz.model.User
import com.some_package.nbaquiz.util.DataState
import com.some_package.nbaquiz.util.StaticHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class FindRivalFragment : Fragment(R.layout.fragment_find_rival) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchAdapter: RecyclerSearchAdapter
    private lateinit var resultLL: LinearLayout
    private lateinit var searchET: EditText
    private lateinit var findBTN: Button


    private val viewModel:MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        observeSearch()
        setupFindButton()
        setupSearchEditText()
    }

    private fun init(view: View){
        recyclerView = view.findViewById(R.id.rv_search_fragment_find_rival)
        resultLL = view.findViewById(R.id.ll_visibility_recycler_search)
        searchET = view.findViewById(R.id.et_search_fragment_find_rival)
        findBTN = view.findViewById(R.id.btn_find_rival_fragment_rival)
    }

    private fun setupFindButton() {
        findBTN.setOnClickListener {

        }
    }

    private fun setupSearchEditText() {
        searchET.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_NULL) {
                getSearchedUser(searchET.text.toString().trim())
                //hide keyBoard
                ViewCompat.getWindowInsetsController(searchET)!!.hide(WindowInsetsCompat.Type.ime())
                StaticHolder.fullScreen(activity)
                // TODO: 12/09/2021 when user close keyboard with android navigation bar , android navigation bar does not collapse
            }
            false
        }
    }

    private fun getSearchedUser(searchedName: String){
        viewModel.searchUser(username = searchedName)
    }

    private fun setupRecycler(users: List<User>){
        if (users.isEmpty())  return
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        searchAdapter = RecyclerSearchAdapter(requireContext(),users,object : OnInviteClicked{
            override fun onInviteClicked(user: User, position: Int) {
                //go to waiting frag
            }

        })
        recyclerView.adapter = searchAdapter
    }

    private fun observeSearch(){
        viewModel.dataStateSearchedUser.observe(viewLifecycleOwner, Observer {
            when (it) {
                is DataState.Success -> {
                    setupRecycler(it.data!!)
                }
                is DataState.Loading -> {

                }
                is DataState.Error -> {

                }
            }
        })
    }

}