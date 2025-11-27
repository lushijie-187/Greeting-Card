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
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val currentPage: Int = 1,
)

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private val repository = PostRepository
    private val postsPerPage = 12

    init {
        if (_uiState.value.items.isEmpty()) {
            loadInitialPosts()
        }
    }

    fun loadInitialPosts() {
        if (_uiState.value.isRefreshing || _uiState.value.isLoadingMore) return
        viewModelScope.launch {
            val randomInitialPage = Random.nextInt(1, 51)
            _uiState.update {
                it.copy(
                    isRefreshing = true,
                    currentPage = randomInitialPage,
                )
            }
            val newPosts = repository.getPosts(page = randomInitialPage, count = postsPerPage)
            _uiState.update {
                it.copy(
                    items = newPosts.map { post -> ListItem.PostItem(post) },
                    isRefreshing = false
                )
            }
        }
    }

    fun loadMorePosts() {
        if (_uiState.value.isRefreshing || _uiState.value.isLoadingMore) return
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
                    items = currentState.items.dropLast(1) + morePosts.map { post ->
                        ListItem.PostItem(
                            post
                        )
                    },
                    isLoadingMore = false,
                    currentPage = nextPage,
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
        val updatedPost = post.copy(
            isLiked = !(post.isLiked),
            likeCount = post.likeCount + (if (post.isLiked) -1 else 1)
        )
        _uiState.update { currentState ->
            currentState.copy(
                items = currentState.items.map { listItem ->
                    if (listItem is ListItem.PostItem && listItem.post.id == post.id) {
                        ListItem.PostItem(updatedPost)
                    } else {
                        listItem
                    }
                }
            )
        }
    }
}
