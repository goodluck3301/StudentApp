package com.example.studentapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.studentapp.R
import com.example.studentapp.accountFragments.AccountPageFragment
import com.example.studentapp.accountFragments.Books
import com.example.studentapp.accountFragments.HomePageFragment
import com.example.studentapp.accountFragments.QuizPageFragment
import com.example.studentapp.databinding.FragmentGeneralBinding
import kotlinx.coroutines.DelicateCoroutinesApi


class GeneralFragment : Fragment() {

    lateinit var binding: FragmentGeneralBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGeneralBinding.inflate(inflater)
        return binding.root
        //return inflater.inflate(R.layout.fragment_general, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val menu = view.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.menuLine)

        val homeFragment = HomePageFragment()
        val books = Books()
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
                    openFragment(books)
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