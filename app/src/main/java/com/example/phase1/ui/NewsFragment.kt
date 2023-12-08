package com.example.phase1.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.phase1.adapter.LoadMoreAdapter
import com.example.phase1.databinding.FragmentNewsBinding
import com.example.phase1.paging.NewsPagingAdapter
import com.example.phase1.viewmodel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class NewsFragment : Fragment() {

    private val viewModel: NewsViewModel by viewModels()

    private lateinit var binding: FragmentNewsBinding

    @Inject
    lateinit var newsAdapter: NewsPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        observeNewsData()

        binding.apply {
            newsAdapter.setOnItemClickListener {
                val action = NewsFragmentDirections.actionNewsFragmentToWebViewFragment(it.url)
                findNavController().navigate(action)
            }



            searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    // Handle search submission if needed
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    // Trigger search when text changes
                    lifecycleScope.launch {
                        // Delay search to avoid unnecessary API calls on each keystroke
                        delay(300)
                        viewModel.searchNews(newText.orEmpty())
                    }
                    return true
                }
            })
        }

        lifecycleScope.launch {
            newsAdapter.loadStateFlow.collect {
                val state = it.refresh
                binding.progressBar.isVisible = state is LoadState.Loading
            }
        }

        binding.recyclerview.adapter = newsAdapter.withLoadStateFooter(
            LoadMoreAdapter {
                newsAdapter.retry()
            }
        )
    }

    private fun observeNewsData() {
        lifecycleScope.launch {
            viewModel.news.collect { pagingData ->
                newsAdapter.submitData(pagingData)
            }
        }
    }

    private fun setupViews() {
        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = newsAdapter
        }
    }
}
