package com.odesa.todo.statistics

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.odesa.todo.R
import com.odesa.todo.databinding.FragmentStatisticsBinding
import com.odesa.todo.util.setupRefreshLayout

class StatisticsFragment : Fragment() {

    private lateinit var binding: FragmentStatisticsBinding
    private val viewModel by viewModels<StatisticsViewModel>()

    override fun onCreate( savedInstanceState: Bundle? ) {
        super.onCreate( savedInstanceState )

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStatisticsBinding.inflate( inflater, container, false )
        return binding.root
    }

    override fun onViewCreated( view: View, savedInstanceState: Bundle? ) {
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
        this.setupRefreshLayout( binding.refreshLayout )
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StatisticsFragment()
    }
}