package com.odesa.todo.addedittask

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.odesa.todo.EventObserver
import com.odesa.todo.R
import com.odesa.todo.databinding.FragmentAddEditTaskBinding
import com.odesa.todo.tasks.ADD_EDIT_RESULT_OK
import com.odesa.todo.util.setupRefreshLayout
import com.odesa.todo.util.setupSnackbar

class AddEditTaskFragment : Fragment() {

    private lateinit var binding: FragmentAddEditTaskBinding
    private val args: AddEditTaskFragmentArgs by navArgs()
    private val viewModel by viewModels<AddEditTaskViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddEditTaskBinding.inflate( inflater, container, false )
        binding.apply {
            viewmodel = viewModel
            lifecycleOwner = this@AddEditTaskFragment.viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated( view: View, savedInstanceState: Bundle? ) {
        setupSnackbar()
        setupNavigation()
        this.setupRefreshLayout( binding.refreshLayout )
        viewModel.start( args.taskId )
    }

    private fun setupSnackbar() {
        view?.setupSnackbar( this, viewModel.snackbarText, Snackbar.LENGTH_SHORT )
    }

    private fun setupNavigation() {
        viewModel.taskUpdatedEvent.observe( viewLifecycleOwner, EventObserver {
            val action = AddEditTaskFragmentDirections
                .actionAddEditTaskFragmentToTasksFragment( ADD_EDIT_RESULT_OK )
            findNavController().navigate( action )
        } )
    }

    companion object {
        @JvmStatic
        fun newInstance( param1: String, param2: String ) =
            AddEditTaskFragment().apply {}
    }
}