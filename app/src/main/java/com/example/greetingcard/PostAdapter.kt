package com.example.greetingcard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlin.toString

private const val ITEM_VIEW_TYPE_POST = 0
private const val ITEM_VIEW_TYPE_LOADING = 1

class PostAdapter(
    private val onPostLongClicked: (Post) -> Unit
) : ListAdapter<ListItem, RecyclerView.ViewHolder>(ListItemDiffCallback()) {

    // 1. ViewHolder: 缓存一个卡片布局中所有的子视图
    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postImage: ImageView = itemView.findViewById(R.id.post_image)
        val postTitle: TextView = itemView.findViewById(R.id.post_title)
        val userAvatar: ImageView = itemView.findViewById(R.id.user_avatar)
        val userName: TextView = itemView.findViewById(R.id.user_name)
        val likeCount: TextView = itemView.findViewById(R.id.like_count)

        fun bind(post: Post) {
            // 将 post 数据绑定到 holder 的视图上
            postImage.setImageResource(post.imageResId)
            postTitle.text = post.title
            userAvatar.setImageResource(post.userAvatarResId)
            userName.text = post.userName
            likeCount.text = post.likeCount.toString()
            // 4. 设置长按监听器
            itemView.setOnLongClickListener {
                onPostLongClicked(post) // 调用传入的回调函数
                true // 返回 true 表示消费了此事件
            }
        }
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // 这个 ViewHolder 不需要 bind 方法，因为它只是一个静态的 ProgressBar
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ListItem.PostItem -> ITEM_VIEW_TYPE_POST
            is ListItem.LoadingItem -> ITEM_VIEW_TYPE_LOADING
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM_VIEW_TYPE_POST -> {
                val view = inflater.inflate(R.layout.list_item_post, parent, false)
                PostViewHolder(view)
            }

            ITEM_VIEW_TYPE_LOADING -> {
                val view = inflater.inflate(R.layout.list_item_loading, parent, false)
                // 让加载项占据整个宽度
                val layoutParams = view.layoutParams
                if (layoutParams is StaggeredGridLayoutManager.LayoutParams) {
                    layoutParams.isFullSpan = true
                }
                LoadingViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PostViewHolder -> {
                val postItem = getItem(position) as ListItem.PostItem
                holder.bind(postItem.post)
            }

            is LoadingViewHolder -> {
            }
        }
    }
}

private class ListItemDiffCallback : DiffUtil.ItemCallback<ListItem>() {
    override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
        return oldItem == newItem
    }
}
