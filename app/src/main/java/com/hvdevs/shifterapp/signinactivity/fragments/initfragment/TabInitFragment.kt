package com.hvdevs.shifterapp.signinactivity.fragments.initfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hvdevs.shifterapp.signinactivity.utilities.ChangePage
import com.hvdevs.shifterapp.databinding.FragmentTabInitBinding

class TabInitFragment : Fragment() {
    private var _binding: FragmentTabInitBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTabInitBinding.inflate(inflater, container, false)

        binding.login.setOnClickListener {
            (activity as? ChangePage)?.pageSelect(1) //Cambiamos las paginas
        }

        binding.createAccount.setOnClickListener {
            (activity as? ChangePage)?.pageSelect(2) //Cambiamos las paginas
        }

        return binding.root
    }

}