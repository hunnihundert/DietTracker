package com.hooni.diettracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.hooni.diettracker.databinding.FragmentInputBinding
import com.hooni.diettracker.ui.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddStatFragment : DialogFragment() {

    private lateinit var binding: FragmentInputBinding
    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInputBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels
        dialog!!.window?.setLayout(width,WRAP_CONTENT)
    }
}