package com.example.shoppinglist.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.shoppinglist.R
import com.example.shoppinglist.domain.ShopItem

class ShopListAdapterVersion1 : ListAdapter<ShopItem, ShopItemViewHolder>(ShopItemDiffCallback()) {

    companion object {
        const val VIEW_TYPE_ENABLED = 1
        const val VIEW_TYPE_DISABLED = 2
        const val MAX_POOL_SIZE = 15
    }

    // Вариант №1
    /* Данный callback
    1. Является заменой notifyDataSetChanged()
    2. Нужен для более точной работы recycleView -> более точной перерисовки элементов
    3. diffResult.dispatchUpdatesTo(this) - сообщает адаптеру нужные места изменений

    Минусы: calculateDiff(callback) - работает лишь на главном потоке, что не ахти.
    По факту является уже целевым решением во многих проектах. Может отыграть на слыбых телефонах.

    class ShopListAdapter : RecyclerView.Adapter<ShopListAdapter.ShopItemViewHolder>()
    */
//    var shopList = listOf<ShopItem>()
//        set(value) {
//
//
//            val callback = ShopListDiffCallback(shopList, value)
//            val diffResult = DiffUtil.calculateDiff(callback)
//            diffResult.dispatchUpdatesTo(this)
//            field = value
//        }

    // Вариант №2
    /*
    Главное отличае в том, что теперь мы наследуемся не от:
    RecyclerView.Adapter<ShopListAdapter.ShopItemViewHolder>()
    А от:
    class ShopListAdapter : ListAdapter<ShopItem, ShopListAdapter.ShopItemViewHolder>(ShopItemDiffCallback())
    */

    var onShopItemLongClickListener: ((ShopItem) -> Unit)? = null
    var onShopItemClickListener: ((ShopItem) -> Unit)? = null


    /* ############################## Методы Recycler View ############################## */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopItemViewHolder {
        val layout = when (viewType) {
            VIEW_TYPE_ENABLED -> R.layout.item_shop_enabled
            VIEW_TYPE_DISABLED -> R.layout.item_shop_disabled
            else -> throw RuntimeException("Unknown view type: $viewType")
        }
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ShopItemViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ShopItemViewHolder, position: Int) {

//        val shopItem = shopList[position]         // Вариант 1
        val shopItem = getItem(position)  // Вариант 2
        viewHolder.tvName.text = shopItem.name
        viewHolder.tvCount.text = shopItem.count.toString()

        viewHolder.view.setOnLongClickListener {
            onShopItemLongClickListener?.invoke(shopItem)
            true
        }

        viewHolder.view.setOnClickListener {
            onShopItemClickListener?.invoke(shopItem)
        }

    }

    override fun getItemViewType(position: Int): Int {

//        if (shopList[position].enabled) {   // Вариант 1
        if (getItem(position).enabled) {      // Вариант 2
            return VIEW_TYPE_ENABLED
        } else {
            return VIEW_TYPE_DISABLED
        }
    }

//    override fun getItemCount(): Int { return shopList.size}  // Вариант 1
//    Вариант 2 - Тк. вся работа со списком теперь скрыта, то метод можно вовсе не переопределять
}