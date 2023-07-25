package com.example.shoppinglist.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppinglist.data.ShopListRepositoryImpl
import com.example.shoppinglist.domain.DeleteShopItemUseCase
import com.example.shoppinglist.domain.EditShopItemUseCase
import com.example.shoppinglist.domain.GetShopListUseCase
import com.example.shoppinglist.domain.ShopItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainViewModel(application: Application): AndroidViewModel(application) {

    private val repository = ShopListRepositoryImpl(application)

    private val getShopListUseCase = GetShopListUseCase(repository)
    private val deleteShopItemUseCase = DeleteShopItemUseCase(repository)
    private val editShopItemUseCase = EditShopItemUseCase(repository)

    //region Пояснение
    /*
    В CoroutineScope нужно передать контекст (это явление ещё не разбирали, поэтому можно почитать)
    Один из вариантов контекста: передать поток, на котором будет выполняться Coroutine.
    Имеет несколько вариантов:
        - Dispatchers.Main      - главный поток;
        - Dispatchers.Default   - будет создано столько потоков, сколько есть ядер у машины. Используется для сложных вычислений;
        - Dispatchers.IO        - Input/Output (используется для операций чтения и записи);

    * При использовании scope необходимого для работы Coroutine важно не забывать его отменять:

        private val scope = CoroutineScope(Dispatchers.Main)
        override fun onCleared() {
            super.onCleared()
            scope.cancel()
        }

    * Но в случае viewModel гуру андроида придумали viewModelScope, который нахуй не нужно отменять.

        *** Я малец потерял нить, почему мы обратно поменяли на MAIN
         Урок - https://stepik.org/lesson/709336/step/1?unit=709899
    */
    //endregion
    private val scope = CoroutineScope(Dispatchers.Main)

    var shopList = getShopListUseCase.getShopList()

    fun deleteShopItem(shopItem: ShopItem) {
        viewModelScope.launch {
            deleteShopItemUseCase.deleteShopItem(shopItem)
        }
    }

    fun changeEnableState(shopItem: ShopItem) {
        viewModelScope.launch {
            val newItem = shopItem.copy(enabled = !shopItem.enabled)
            editShopItemUseCase.editShopItem(newItem)
        }
    }

}