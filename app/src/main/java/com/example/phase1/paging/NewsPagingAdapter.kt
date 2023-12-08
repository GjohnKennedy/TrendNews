package com.example.phase1.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.phase1.R
import com.example.phase1.databinding.ItemNewsBinding
import com.example.phase1.model.Article
import javax.inject.Inject

class NewsPagingAdapter @Inject constructor() :
    PagingDataAdapter<Article, NewsPagingAdapter.MyViewHolder>(NewsDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { holder.bind(it) }
    }

    inner class MyViewHolder(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Article) {
            binding.apply {
                newsTitle.text = item.title
                newsDescription.text = shortenString(item.summary)
                Glide.with(itemView.context)
                    .load(item.imageUrl)
                    .error(R.drawable.ic_news)
                    .placeholder(R.drawable.ic_news)
                    .into(newsImage)

                readMoreButton.setOnClickListener {
                    onItemClickListener?.let {
                        it(item)
                    }
                }
            }
        }
    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

    object NewsDiffCallback : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    fun shortenString(input: String?): String {
        if (input != null) {
            return if (input.length > 100) {
                val shortenedText = input.substring(0, 100)
                val lastSpaceIndex = shortenedText.lastIndexOf(' ')
                if (lastSpaceIndex != -1) {
                    shortenedText.substring(0, lastSpaceIndex) + " ..."
                } else {
                    "$shortenedText ..."
                }
            } else {
                input
            }
        } else {
            return ""
        }
    }
}
