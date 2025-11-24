package com.example.greetingcard
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlin.toString

// 1. Adapter 继承自 ListAdapter，而不是 RecyclerView.Adapter
class PostAdapter(
    // 3. 添加一个长按事件的回调函数作为构造参数
    private val onPostLongClicked: (Post) -> Unit
) : ListAdapter<Post, PostAdapter.PostViewHolder>(PostDiffCallback()) {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position) // 使用 ListAdapter 提供的 getItem() 方法获取数据
        holder.bind(post)
    }
}

private class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    /**
     * 判断两个对象是否是同一个 item。
     * 通常比较它们的唯一 ID。由于我们的 Post 没有唯一 ID，这里暂时用对象引用本身来判断。
     * 在真实项目中，应该比较 post.id。
     */
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem === newItem
    }
    /**
     * 判断两个 item 的内容是否相同。
     * 只有在 areItemsTheSame() 返回 true 时，此方法才会被调用。
     * data class 的 equals() 方法会比较所有属性，非常适合用在这里。
     */
    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}
