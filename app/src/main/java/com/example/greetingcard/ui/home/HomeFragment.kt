package com.example.greetingcard.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.greetingcard.ui.widget.GridSpacingItemDecoration
import com.example.greetingcard.R
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostAdapter
    private lateinit var layoutManager: StaggeredGridLayoutManager
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)
        setupListeners()
        observeViewModel()
    }

    private fun setupViews(view: View) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        recyclerView = view.findViewById(R.id.post_recycler_view)
        adapter = PostAdapter(viewModel)
        layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.grid_spacing)
        recyclerView.addItemDecoration(GridSpacingItemDecoration(2, spacingInPixels, true))
    }

    private fun setupListeners() {
        // 下拉刷新监听
        swipeRefreshLayout.setOnRefreshListener { viewModel.loadInitialPosts() }
        // 上滑加载更多监听
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy <= 0) {
                    return
                }
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItems = IntArray(layoutManager.spanCount)
                layoutManager.findLastVisibleItemPositions(firstVisibleItems)
                val lastVisibleItem = firstVisibleItems.maxOrNull() ?: 0
                if (visibleItemCount + lastVisibleItem >= totalItemCount - 3) {
                    viewModel.loadMorePosts()
                }
            }
        })
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    adapter.submitList(uiState.items)
                    swipeRefreshLayout.isRefreshing = uiState.isRefreshing
                }
            }
        }
    }

}