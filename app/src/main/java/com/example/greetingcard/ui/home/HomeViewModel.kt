// file: HomeViewModel.kt
package com.example.greetingcard.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.greetingcard.data.model.ListItem
import com.example.greetingcard.data.model.Post
import com.example.greetingcard.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class HomeUiState(
    val items: List<ListItem> = emptyList(),
    val isRefreshing: Boolean = false, // 专用于下拉刷新
    val isLoadingMore: Boolean = false, // 专用于加载更多
    val currentPage: Int = 1, // 新增：当前页码
    val canLoadMore: Boolean = true // 新增：是否还能加载更多
)

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private val repository = PostRepository
    private val postsPerPage = 12 // 定义每页加载的数量

    init {
        // ViewModel 创建时，如果列表为空，则加载初始数据
        if (_uiState.value.items.isEmpty()) {
            loadInitialPosts()
        }
    }

    fun loadInitialPosts() {
        // 防止在已有操作时重复触发
        if (_uiState.value.isRefreshing || _uiState.value.isLoadingMore) return
        viewModelScope.launch {
            // 更新状态，表明“下拉刷新”开始了
            val randomInitialPage = Random.nextInt(1, 51)
            _uiState.update { it.copy(isRefreshing = true, currentPage = randomInitialPage, canLoadMore = true) }
            val newPosts = repository.getPosts(page = randomInitialPage, count = postsPerPage)
            // 更新状态，设置新列表并结束“下拉刷新”
            _uiState.update {
                it.copy(
                    items = newPosts.map { post -> ListItem.PostItem(post) },
                    isRefreshing = false
                )
            }
        }
    }

    fun loadMorePosts() {
        // 防止在已有操作时重复触发
        if (!_uiState.value.canLoadMore || _uiState.value.isRefreshing || _uiState.value.isLoadingMore) return
        viewModelScope.launch {
            val nextPage = _uiState.value.currentPage + 1
            _uiState.update { currentState ->
                currentState.copy(
                    items = currentState.items + ListItem.LoadingItem,
                    isLoadingMore = true
                )
            }
            val morePosts = repository.getPosts(page = nextPage, count = postsPerPage)
            _uiState.update { currentState ->
                currentState.copy(
                    items = currentState.items.dropLast(1) + morePosts.map { post -> ListItem.PostItem(post) },
                    isLoadingMore = false,
                    currentPage = nextPage,
                    canLoadMore = morePosts.isNotEmpty()
                )
            }
        }
    }

    fun deletePost(postToDelete: Post) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    items = currentState.items.filterNot {
                        it is ListItem.PostItem && it.post === postToDelete
                    }
                )
            }
            repository.deletePost(postToDelete)
        }
    }

    fun onLikeClicked(post: Post) {
        // 更新 post 对象的点赞状态
        post.isLiked = !post.isLiked
        post.likeCount++
        // 为了让 StateFlow 能够检测到变化，我们需要创建一个新的列表
        // 因为仅仅修改列表内对象的属性，StateFlow 是无法感知的
        _uiState.update { currentState ->
            currentState.copy(
                items = currentState.items.map { listItem ->
                    if (listItem is ListItem.PostItem && listItem.post.id == post.id) {
                        // 创建一个新的 PostItem，包含更新后的 post 对象
                        ListItem.PostItem(post.copy())
                    } else {
                        listItem
                    }
                }
            )
        }
    }
}
