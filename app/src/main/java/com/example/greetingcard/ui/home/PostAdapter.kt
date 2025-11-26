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
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import com.airbnb.lottie.LottieAnimationView

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
        private val likeButton: LottieAnimationView = itemView.findViewById(R.id.like_button)

        fun bind(post: Post) {
            // 将 post 数据绑定到 holder 的视图上
            postImageView.setImageURI(Uri.parse(post.imageUrl))
            userAvatarImageView.setImageURI(Uri.parse(post.userAvatarUrl))
            postTitle.text = post.title
            userName.text = post.userName
            likeCount.text = post.likeCount.toString()
            // 4. 设置长按监听器
            postImageView.setOnLongClickListener {
                viewModel.deletePost(post)
                true // 返回 true 表示消费了此事件
            }
            likeButton.setOnClickListener {
                viewModel.onLikeClicked(post)
                Log.i("Post", "Like Clicked!")
                true
            }
            updateLikeStatus(post)
        }

        private fun updateLikeStatus(post: Post) {
            if (post.isLiked) {
                // 如果是已点赞状态，直接将动画设置到最终帧
                likeButton.progress = 1.0f
            } else {
                // 如果是未点赞状态，直接将动画设置到起始帧
                likeButton.progress = 0f
            }
        }
    }

    private var lastAnimatedPosition = -1

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

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            // 如果没有 payload，正常完整绑定
            super.onBindViewHolder(holder, position, payloads)
        } else {
            // 如果有 payload，说明是局部刷新
            if (holder is PostViewHolder && payloads[0] == true) {
                val postItem = getItem(position) as ListItem.PostItem
                // 只播放动画，不重新绑定其他视图
                if (postItem.post.isLiked) {
                    holder.itemView.findViewById<LottieAnimationView>(R.id.like_button).playAnimation()
                } else {
                    // 如果需要取消点赞的动画，可以在这里处理
                    // 这里我们简单地直接回到 0 帧
                    holder.itemView.findViewById<LottieAnimationView>(R.id.like_button).progress = 0f
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PostViewHolder -> {
                val postItem = getItem(position) as ListItem.PostItem
                holder.bind(postItem.post)
                runEnterAnimation(holder.itemView, position)
            }

            is LoadingViewHolder -> {
            }
        }
    }

    private fun runEnterAnimation(view: View, position: Int) {
        // 如果这个 item 已经播放过动画，或者正在向上滚动，则不播放
        if (position <= lastAnimatedPosition) {
            return
        }
        // 记录下当前播放动画的 item 位置
        lastAnimatedPosition = position
        // 动画1: 从下方 100px 的位置移动上来
        val translationY = ObjectAnimator.ofFloat(view, "translationY", 100f, 0f)

        // 动画2: 从透明变为不透明
        val alpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
        // 使用 AnimatorSet 来组合动画
        val animatorSet = AnimatorSet().apply {
            // 两个动画同时执行
            playTogether(translationY, alpha)
            // 设置动画时长
            duration = 500 // 500毫秒
            // 设置插值器，先加速后减速，效果更自然
            interpolator = AccelerateDecelerateInterpolator()
        }

        animatorSet.start()
    }

    // 当列表数据被完全替换时（例如下拉刷新），需要重置动画记录
    override fun submitList(list: List<ListItem>?) {
        // 当新列表的第一个元素不是旧列表的第一个元素时，我们认为是刷新操作
        if (list != null && currentList.isNotEmpty() && list[0].id != currentList[0].id) {
            lastAnimatedPosition = -1
        }
        super.submitList(list)
    }

    // 在 ViewHolder 被回收时，清除动画状态，确保再次出现时能正确执行动画
    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.clearAnimation()
    }
}

private class ListItemDiffCallback : DiffUtil.ItemCallback<ListItem>() {
    override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
        // 如果都是 PostItem，并且内容相同，返回 true
        return if (oldItem is ListItem.PostItem && newItem is ListItem.PostItem) {
            oldItem.post == newItem.post
        } else {
            // 对于 LoadingItem 或类型不同的情况
            oldItem == newItem
        }
    }

    // 关键：实现 getChangePayload
    override fun getChangePayload(oldItem: ListItem, newItem: ListItem): Any? {
        if (oldItem is ListItem.PostItem && newItem is ListItem.PostItem) {
            // 如果只有 isLiked 状态改变了
            if (oldItem.post.isLiked != newItem.post.isLiked) {
                // 返回一个非空的 payload，这里用 true 作为信号
                return true
            }
        }
        return null // 其他变化，返回 null，执行全量刷新
    }
}
