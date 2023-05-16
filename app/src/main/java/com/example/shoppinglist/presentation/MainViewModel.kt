package com.example.shoppinglist.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoppinglist.data.ShopListRepositoryImpl
import com.example.shoppinglist.domain.*

class MainViewModel: ViewModel() {

    private val repository = ShopListRepositoryImpl

    private val getShopListUseCase = GetShopListUseCase(ShopListRepositoryImpl)
    private val deleteShopItemUseCase = DeleteShopItemUseCase(ShopListRepositoryImpl)
    private val editShopItemUseCase = EditShopItemUseCase(ShopListRepositoryImpl)

    var shopList = MutableLiveData<List<ShopItem>>()
    fun getShopList() {
        // value - можно вызвать только из главного потока, postvalue - из любого
        shopList.value = getShopListUseCase.getShopList()
    }

    fun deleteShopItem(shopItem: ShopItem) {
        deleteShopItemUseCase.deleteShopItem(shopItem)
        getShopList()
    }

    fun changeEnableState(shopItem: ShopItem) {
        val newItem = shopItem.copy(enabled = !shopItem.enabled)
        editShopItemUseCase.editShopItem(newItem)
        getShopList()
    }

}