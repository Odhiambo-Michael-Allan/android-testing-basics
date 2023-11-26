package com.odesa.todo.taskdetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.odesa.todo.EventObserver
import com.odesa.todo.R
import com.odesa.todo.TodoApplication
import com.odesa.todo.data.source.DefaultTasksRepository
import com.odesa.todo.databinding.FragmentTaskDetailBinding
import com.odesa.todo.tasks.DELETE_RESULT_OK
import com.odesa.todo.tasks.TasksViewModelFactory
import com.odesa.todo.util.setupRefreshLayout
import com.odesa.todo.util.setupSnackbar

/**
 * Main UI for the task detail screen.
 */
class TaskDetailFragment : Fragment() {

    private lateinit var binding: FragmentTaskDetailBinding
    private val args: TaskDetailFragmentArgs by navArgs()
    private val viewModel by viewModels<TaskDetailViewModel> {
        TaskDetailViewModelFactory( ( requireContext().applicationContext as TodoApplication )
            .tasksRepository )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTaskDetailBinding.inflate( inflater, container, false )
            .apply {
                viewmodel = viewModel
            }
        binding.lifecycleOwner = this.viewLifecycleOwner
        viewModel.start( args.taskId )
        setHasOptionsMenu( true )
        return binding.root
    }

    override fun onViewCreated( view: View, savedInstanceState: Bundle? ) {
        super.onViewCreated( view, savedInstanceState )
        setupFab()
        view.setupSnackbar( this, viewModel.snackbarText, Snackbar.LENGTH_SHORT )
        setupNavigation()
        this.setupRefreshLayout( binding.refreshLayout )
    }

    private fun setupFab() {
        binding.editTaskFab.setOnClickListener {
            viewModel.editTask()
        }
    }

    private fun setupNavigation() {
        viewModel.deleteTaskEvent.observe( viewLifecycleOwner, EventObserver {
            val action = TaskDetailFragmentDirections
                .actionTaskDetailFragmentToTasksFragment( DELETE_RESULT_OK )
            findNavController().navigate( action )
        } )
        viewModel.editTaskEvent.observe( viewLifecycleOwner, EventObserver {
            val action = TaskDetailFragmentDirections
                .actionTaskDetailFragmentToAddEditTaskFragment(
                    args.taskId,
                    resources.getString( R.string.edit_task )
                )
            findNavController().navigate( action )
        } )
    }

    @Deprecated( "Deprecated in Java" )
    override fun onCreateOptionsMenu( menu: Menu, inflater: MenuInflater ) {
        inflater.inflate( R.menu.taskdetail_fragment_menu, menu )
    }

    @Deprecated( "Deprecated in Java" )
    override fun onOptionsItemSelected( item: MenuItem ): Boolean {
        return when ( item.itemId ) {
            R.id.menu_delete -> {
                viewModel.deleteTask()
                true
            }
            else -> false
        }
    }

    companion object {
        @JvmStatic
        fun newInstance( param1: String, param2: String ) =
            TaskDetailFragment()
    }
}