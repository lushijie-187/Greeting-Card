package com.example.greetingcard.ui.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.example.greetingcard.R
import com.example.greetingcard.data.model.ListItem
import com.example.greetingcard.data.model.Post
import com.facebook.drawee.view.SimpleDraweeView

private const val ITEM_VIEW_TYPE_POST = 0
private const val ITEM_VIEW_TYPE_LOADING = 1

class PostAdapter(
    private val viewModel: HomeViewModel
) : ListAdapter<ListItem, RecyclerView.ViewHolder>(ListItemDiffCallback()) {
    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postImageView: SimpleDraweeView = itemView.findViewById(R.id.post_image)
        val postTitle: TextView = itemView.findViewById(R.id.post_title)
        val userAvatarImageView: SimpleDraweeView = itemView.findViewById(R.id.user_avatar)
        val userName: TextView = itemView.findViewById(R.id.user_name)
        val likeCount: TextView = itemView.findViewById(R.id.like_count)
        val likeButton: LottieAnimationView = itemView.findViewById(R.id.like_button)

        init {
            likeButton.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    val item = getItem(bindingAdapterPosition)
                    if (item is ListItem.PostItem) {
                        viewModel.onLikeClicked(item.post)
                    }
                }
            }
        }

        fun bind(post: Post) {
            postImageView.setImageURI(Uri.parse(post.imageUrl))
            userAvatarImageView.setImageURI(Uri.parse(post.userAvatarUrl))
            postTitle.text = post.title
            userName.text = post.userName
            likeCount.text = post.likeCount.toString()
            postImageView.setOnLongClickListener {
                viewModel.deletePost(post)
                true
            }
            likeButton.progress = if (post.isLiked) 1.0f else 0f
        }
    }

    private var lastEnterAnimatedPosition = -1

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

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
        holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        }

        val vh = holder as? PostViewHolder ?: return
        if (payloads.any { it == true }) {
            val item = getItem(position) as? ListItem.PostItem ?: return
            vh.likeCount.text = item.post.likeCount.toString()
            if (item.post.isLiked) {
                vh.likeButton.playAnimation()
            } else {
                vh.likeButton.cancelAnimation()
                vh.likeButton.progress = 0f
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val vh = holder as? PostViewHolder ?: return
        val postItem = getItem(position) as ListItem.PostItem
        vh.bind(postItem.post)
        runEnterAnimation(holder.itemView, position)
    }

    private fun runEnterAnimation(view: View, position: Int) {
        if (position <= lastEnterAnimatedPosition) {
            return
        }
        lastEnterAnimatedPosition = position

        val translationY = ObjectAnimator.ofFloat(view, "translationY", 100f, 0f)
        val alpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)

        val animatorSet = AnimatorSet().apply {
            playTogether(translationY, alpha)
            duration = 1000
            interpolator = AccelerateDecelerateInterpolator()
        }
        animatorSet.start()
    }

    fun resetAnimationState() {
        lastEnterAnimatedPosition = -1
    }

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
        return if (oldItem is ListItem.PostItem && newItem is ListItem.PostItem) {
            oldItem.post == newItem.post
        } else {
            true
        }
    }

    override fun getChangePayload(oldItem: ListItem, newItem: ListItem): Any? {
        if (oldItem is ListItem.PostItem && newItem is ListItem.PostItem) {
            if (oldItem.post.isLiked != newItem.post.isLiked) {
                return true
            }
        }
        return null
    }
}
