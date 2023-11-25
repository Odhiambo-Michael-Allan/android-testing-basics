package com.odesa.todo.addedittask

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.odesa.todo.R
import com.odesa.todo.databinding.FragmentAddEditTaskBinding

class AddEditTaskFragment : Fragment() {

    private lateinit var binding: FragmentAddEditTaskBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddEditTaskBinding.inflate( inflater, container, false )
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance( param1: String, param2: String ) =
            AddEditTaskFragment().apply {}
    }
}