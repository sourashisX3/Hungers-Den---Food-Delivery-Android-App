package com.firstapp.hungersden.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firstapp.hungersden.R
import com.firstapp.hungersden.adapter.MenuAdapter
import com.firstapp.hungersden.databinding.FragmentSearchBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: MenuAdapter
    private lateinit var database: FirebaseDatabase
    private val originalMenuItems = mutableListOf<com.firstapp.hungersden.model.MenuItem>()

    //    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        // Setup SearchView theme colors
        setupSearchViewTheme()

        // Retrieve menu items form database
        retrieveMenuItem()

        //setup for search view
        setupSearchView()

        return binding.root
    }

    private fun setupSearchViewTheme() {
        // Get the SearchView's EditText to set text colors
        val searchEditText = binding.searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText?.apply {
            // Set text color based on theme
            setTextColor(ContextCompat.getColor(requireContext(), R.color.textPrimary))
            setHintTextColor(ContextCompat.getColor(requireContext(), R.color.textHint))
        }
    }

    private fun retrieveMenuItem() {
        // Get database reference
        database = FirebaseDatabase.getInstance()
        // reference to the Menu node
        val foodReference: DatabaseReference = database.reference.child("menu")
        foodReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    val menuItem = foodSnapshot.getValue(com.firstapp.hungersden.model.MenuItem::class.java)
                    menuItem?.let {
                        originalMenuItems.add(it)
                    }
                }
                showAllMenu()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun showAllMenu() {
        val filteredMenuItems = ArrayList(originalMenuItems)
        setAdapter(filteredMenuItems)
    }

    private fun setAdapter(filteredMenuItem: List<com.firstapp.hungersden.model.MenuItem>) {
        adapter = MenuAdapter(filteredMenuItem, requireContext())
        binding.menuRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.menuRecyclerView.adapter = adapter
    }


    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener( /*listener =*/ object :
            SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                filterMenuItems(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterMenuItems(newText)
                return true
            }
        })
    }

    private fun filterMenuItems(query: String) {
        val filteredMenuItems = originalMenuItems.filter {
            it.foodName?.contains(query, ignoreCase = true) == true
        }
        setAdapter(filteredMenuItems)
    }

    companion object {

    }
}


