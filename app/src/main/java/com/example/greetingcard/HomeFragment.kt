// file: com/example/greetingcard/HomeFragment.kt
package com.example.greetingcard

import android.os.Bundle
import android.util.Log
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
        // 1. 初始化新的 Adapter，并传入长按删除的逻辑
        adapter = PostAdapter { postToDelete -> viewModel.deletePost(postToDelete) }
        layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        // 2. 添加我们创建的 ItemDecoration
        val spacingInPixels =
            resources.getDimensionPixelSize(R.dimen.grid_spacing) // 假设你在 dimens.xml 定义了间距
        recyclerView.addItemDecoration(GridSpacingItemDecoration(2, spacingInPixels, true))
    }

    private fun setupListeners() {
        // 下拉刷新监听
        swipeRefreshLayout.setOnRefreshListener { viewModel.loadInitialPosts() }
        // 上滑加载更多监听
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) { // 仅在向下滑动时检查
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItems = IntArray(layoutManager.spanCount)
                    layoutManager.findLastVisibleItemPositions(firstVisibleItems)

                    val lastVisibleItem = firstVisibleItems.maxOrNull() ?: 0
                    if (visibleItemCount + lastVisibleItem >= totalItemCount - 3) { // 预加载
                        viewModel.loadMorePosts()
                    }
                }
            }
        })
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    // 1. 更新列表数据
                    adapter.submitList(uiState.items)
                    // 2. 更新下拉刷新指示器
                    swipeRefreshLayout.isRefreshing = uiState.isRefreshing
                    // 3. 更新“加载更多”的 UI
                    // 你可以在 RecyclerView 底部添加一个 ProgressBar
                    // 并根据 uiState.isLoadingMore 来控制其显示和隐藏
                    // 这里我们暂时只打印日志来确认状态
                    if (uiState.isLoadingMore) {
                        Log.d("HomeFragment", "正在加载更多...")
                    } else {
                        Log.d("HomeFragment", "加载更多完成。")
                    }
                }
            }
        }
    }

}
