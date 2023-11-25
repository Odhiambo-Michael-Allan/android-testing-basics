package com.odesa.todo.tasks

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.odesa.todo.EventObserver
import com.odesa.todo.R
import com.odesa.todo.TodoApplication
import com.odesa.todo.data.source.DefaultTasksRepository
import com.odesa.todo.databinding.FragmentTasksBinding
import com.odesa.todo.util.setupRefreshLayout
import com.odesa.todo.util.setupSnackbar
import timber.log.Timber

/**
 * Display a grid of [Task]s. User can choose to view all, active or completed tasks.
 */
class TasksFragment : Fragment() {

    private lateinit var binding: FragmentTasksBinding
    private val viewModel by viewModels<TasksViewModel> {
        TasksViewModelFactory( ( requireContext().applicationContext as TodoApplication )
            .tasksRepository )
    }
    private val args: TasksFragmentArgs by navArgs()
    private lateinit var listAdapter: TasksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTasksBinding.inflate( inflater, container, false )
            .apply { viewmodel = viewModel }
        setHasOptionsMenu( true )
        return binding.root
    }

    override fun onViewCreated( view: View, savedInstanceState: Bundle? ) {
        // Set the lifecycle owner to the lifecycle of the view
        binding.lifecycleOwner = this.viewLifecycleOwner
        setupSnackbar()
        setupListAdapter()
        setupRefreshLayout( binding.refreshLayout, binding.tasksList )
        setupNavigation()
        setupFab()
    }

    private fun setupSnackbar() {
        view?.setupSnackbar( this, viewModel.snackbarText, Snackbar.LENGTH_SHORT )
        arguments?.let {
            viewModel.showEditResultMessage( args.userMessage )
        }
    }

    private fun setupListAdapter() {
        val viewModel = binding.viewmodel
        Timber.w( "View Model: ${binding.viewmodel}" )
        if ( viewModel != null ) {
            listAdapter = TasksAdapter( viewModel )
            binding.tasksList.adapter = listAdapter
        } else
            Timber.w( "ViewModel not initialized when attempting to set up adapter" )
    }

    private fun setupNavigation() {
        viewModel.openTaskEvent.observe( viewLifecycleOwner, EventObserver {
            openTaskDetails( it )
        } )
        viewModel.newTaskEvent.observe( viewLifecycleOwner, EventObserver {
            navigateToAddNewTask()
        } )
    }

    private fun openTaskDetails( taskId: String ) {
        val action = TasksFragmentDirections.actionTasksFragmentToTaskDetailFragment( taskId )
        findNavController().navigate( action )
    }

    private fun setupFab() {
        binding.addTaskFab.setOnClickListener {
            navigateToAddNewTask()
        }
    }

    private fun navigateToAddNewTask() {
        val action = TasksFragmentDirections
            .actionTasksFragmentToAddEditTaskFragment(
                null, resources.getString( R.string.add_task )
            )
        findNavController().navigate( action )
    }

    @Deprecated( "Deprecated in Java" )
    override fun onOptionsItemSelected(item: MenuItem ) =
        when ( item.itemId ) {
            R.id.menu_clear -> {
                viewModel.clearCompletedTasks()
                true
            }
            R.id.menu_filter -> {
                showFilteringPopUpMenu()
                true
            }
            R.id.menu_refresh -> {
                viewModel.loadTasks( true )
                true
            }
            else -> false
        }

    private fun showFilteringPopUpMenu() {
        val view = activity?.findViewById<View>( R.id.menu_filter ) ?: return
        PopupMenu( requireContext(), view ).run {
            menuInflater.inflate( R.menu.filter_tasks, menu )
            setOnMenuItemClickListener {
                viewModel.setFiltering(
                    when ( it.itemId ) {
                        R.id.active -> TasksFilterType.ACTIVE_TASKS
                        R.id.completed -> TasksFilterType.COMPLETED_TASKS
                        else -> TasksFilterType.ALL_TASKS
                    }
                )
                true
            }
            show()
        }
    }

    @Deprecated( "Deprecated in Java" )
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater ) {
        inflater.inflate( R.menu.tasks_fragment_menu, menu )
    }

    companion object {

        @JvmStatic
        fun newInstance( param1: String, param2: String ) =
            TasksFragment().apply {}

    }
}