// file: com/example/greetingcard/HomeFragment.kt
package com.example.greetingcard

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
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostAdapter
    private lateinit var layoutManager: StaggeredGridLayoutManager
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 加载 Fragment 自己的布局
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    /**
     * 这个回调在 onCreateView() 之后、视图完全创建好时被调用。
     * 是进行视图初始化（findViewById、设置监听器等）的最佳位置。
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)
        setupListeners()
        observeViewModel() // 新增：开始观察 ViewModel
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
            // repeatOnLifecycle 会在 Fragment 进入 STARTED 状态时执行块内的代码，
            // 并在 STOPPED 时挂起，这是订阅 UI 更新的安全方式。
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 启动一个新的协程来订阅帖子列表
                launch {
                    viewModel.posts.collect { posts ->
                        // 当 posts StateFlow 有新值时，这里会执行
                        adapter.submitList(posts)
                    }
                }
                // 启动另一个协程来订阅加载状态
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        // 当 isLoading StateFlow 有新值时，这里会执行
                        swipeRefreshLayout.isRefreshing = isLoading
                    }
                }
            }
        }
    }

}
