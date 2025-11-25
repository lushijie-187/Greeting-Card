// file: HomeViewModel.kt
package com.example.greetingcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    // 私有的、可变的 StateFlow，仅在 ViewModel 内部使用
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    // 对外暴露的、不可变的 StateFlow，供 UI 订阅
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val repository = PostRepository

    init {
        // ViewModel 创建时，如果列表为空，则加载初始数据
        if (_posts.value.isEmpty()) {
            loadInitialPosts()
        }
    }

    fun loadInitialPosts() {
        // 防止重复加载
        if (_isLoading.value) return

        // 使用 viewModelScope 启动一个协程，它与 ViewModel 的生命周期绑定
        viewModelScope.launch {
            _isLoading.value = true
            val newPosts = repository.getPosts(page = 1, count = 12)
            _posts.value = newPosts // 直接替换为新数据
            _isLoading.value = false
        }
    }

    fun loadMorePosts() {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            val morePosts = repository.getPosts(page = 2, count = 6) // page 暂时写死
            // 在现有列表基础上追加新数据
            _posts.value = _posts.value + morePosts
            _isLoading.value = false
        }
    }

    fun deletePost(postToDelete: Post) {
        viewModelScope.launch {
            // 立即在 UI 上反映删除
            _posts.update { currentList ->
                currentList.filterNot { it === postToDelete }
            }
            // 然后在后台调用仓库执行实际的删除操作
            repository.deletePost(postToDelete)
        }
    }
}
