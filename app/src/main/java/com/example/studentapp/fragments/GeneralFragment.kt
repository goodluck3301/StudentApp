package com.example.studentapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.studentapp.R
import com.example.studentapp.accountFragments.AccountPageFragment
import com.example.studentapp.accountFragments.Material
import com.example.studentapp.accountFragments.HomePageFragment
import com.example.studentapp.accountFragments.QuizPageFragment
import com.example.studentapp.databinding.FragmentGeneralBinding


class GeneralFragment : Fragment() {

    private lateinit var binding: FragmentGeneralBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGeneralBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val homeFragment = HomePageFragment()
        val material = Material()
        val quiz = QuizPageFragment()
        val account = AccountPageFragment()

//ctrl shift space
        openFragment(homeFragment)

        binding.menuLine.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    openFragment(homeFragment)
                }
                R.id.lybrary -> {
                    openFragment(material)
                }
                R.id.quizz -> {
                    openFragment(quiz)
                }
                R.id.account -> {
                    openFragment(account)
                }
            }
            true
        }
    }

    private fun openFragment(fragment: Fragment) {
        fragmentManager?.beginTransaction()?.apply {
            replace(R.id.fragmentContainerView2, fragment)
            commit()
        }
    }

}