// file: com/example/greetingcard/HomeFragment.kt
package com.example.greetingcard

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class HomeFragment : Fragment() {
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostAdapter
    private lateinit var layoutManager: StaggeredGridLayoutManager
    private var isLoading = false

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
        // 首次加载数据
        // 使用 adapter.currentList 检查列表是否为空
        if (adapter.currentList.isEmpty()) {
            loadInitialData()
        }
    }

    private fun setupViews(view: View) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        recyclerView = view.findViewById(R.id.post_recycler_view)
        // 1. 初始化新的 Adapter，并传入长按删除的逻辑
        adapter = PostAdapter { postToDelete ->
            // 这里是长按事件触发后的逻辑
            handlePostDeletion(postToDelete)
        }
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
        swipeRefreshLayout.setOnRefreshListener {
            loadInitialData()
        }
        // 上滑加载更多监听
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val lastVisibleItemPositions = layoutManager.findLastVisibleItemPositions(null)
                val lastVisibleItemPosition = lastVisibleItemPositions.maxOrNull() ?: 0
                val itemCount = adapter.itemCount // 从 adapter 获取
                if (!isLoading && itemCount > 0 &&
                    (lastVisibleItemPosition + layoutManager.spanCount) >= itemCount
                ) {
                    loadMoreData()
                }
            }
        })
    }

    private fun handlePostDeletion(postToDelete: Post) {
        // 从当前列表中创建一个新的可变列表
        val currentList = adapter.currentList.toMutableList()
        // 从新列表中移除要删除的项
        currentList.remove(postToDelete)
        // 将新列表提交给 adapter。DiffUtil 会自动计算差异并执行删除动画！
        adapter.submitList(currentList)
        Toast.makeText(requireContext(), "已删除: ${postToDelete.title}", Toast.LENGTH_SHORT).show()
    }

    private fun loadInitialData() {
        swipeRefreshLayout.isRefreshing = true
        Handler(Looper.getMainLooper()).postDelayed({
            // 4. 使用 submitList 更新数据
            val newPosts = PostGenerator.generateRandomPosts(12)
            adapter.submitList(newPosts)
            swipeRefreshLayout.isRefreshing = false
        }, 1000)
    }

    private fun loadMoreData() {
        isLoading = true
        Handler(Looper.getMainLooper()).postDelayed({
            // 4. 使用 submitList 更新数据
            val currentList = adapter.currentList.toMutableList()
            val morePosts = PostGenerator.generateRandomPosts(6)
            currentList.addAll(morePosts)
            adapter.submitList(currentList)
            isLoading = false
        }, 1000)
    }

}
