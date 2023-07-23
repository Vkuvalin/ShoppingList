package com.example.shoppinglist.presentation

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.databinding.ItemShopDisabledBinding

//region До использования dataBinding
/*
class ShopItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val tvName = view.findViewById<TextView>(R.id.tv_name)
    val tvCount = view.findViewById<TextView>(R.id.tv_count)
}
*/
//endregion
//region Такова реализация, если viewType один.
/*
class ShopItemViewHolder(val binding: ItemShopDisabledBinding) :
    RecyclerView.ViewHolder(binding.root)
*/
//endregion

class ShopItemViewHolder(
    val binding: ViewDataBinding
    ) :
    RecyclerView.ViewHolder(binding.root)