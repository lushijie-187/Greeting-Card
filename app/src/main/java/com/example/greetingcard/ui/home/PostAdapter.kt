package com.example.greetingcard.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.greetingcard.R
import com.example.greetingcard.data.model.ListItem
import com.example.greetingcard.data.model.Post
import android.net.Uri // 导入 Uri
import com.facebook.drawee.view.SimpleDraweeView

private const val ITEM_VIEW_TYPE_POST = 0
private const val ITEM_VIEW_TYPE_LOADING = 1

class PostAdapter(
    private val viewModel: HomeViewModel
) : ListAdapter<ListItem, RecyclerView.ViewHolder>(ListItemDiffCallback()) {

    // 1. ViewHolder: 缓存一个卡片布局中所有的子视图
    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postImageView: SimpleDraweeView = itemView.findViewById(R.id.post_image)
        val postTitle: TextView = itemView.findViewById(R.id.post_title)
        val userAvatarImageView: SimpleDraweeView = itemView.findViewById(R.id.user_avatar)
        val userName: TextView = itemView.findViewById(R.id.user_name)
        val likeCount: TextView = itemView.findViewById(R.id.like_count)

        fun bind(post: Post) {
            // 将 post 数据绑定到 holder 的视图上
            postImageView.setImageURI(Uri.parse(post.imageUrl))
            userAvatarImageView.setImageURI(Uri.parse(post.userAvatarUrl))
            postTitle.text = post.title
            userName.text = post.userName
            likeCount.text = post.likeCount.toString()
            // 4. 设置长按监听器
            itemView.setOnLongClickListener {
                viewModel.deletePost(post)
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
