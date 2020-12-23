package com.mystrica.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mystrica.R
import com.mystrica.activity.DashboardActivity


class HistoryFragment : Fragment() {

    private var rootview : View ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootview= inflater.inflate(R.layout.fragment_history, container, false)

        (activity as DashboardActivity?)!!.HeadetTitle("History")
        (activity as DashboardActivity?)!!.visibleBackButton()

        return  rootview
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            HistoryFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}