package com.example.shoppinglist.data

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.shoppinglist.domain.ShopItem
import com.example.shoppinglist.domain.ShopListRepository
import kotlin.random.Random


// Доделать потом здесь базу данных!!!! Ох, в скольких же прилах я смогу трениться!!!!!!!!!
//region Первый вариант реализации репозитория
/*
object ShopListRepositoryImpl: ShopListRepository  {

    private val shopListLD = MutableLiveData<List<ShopItem>>()
    //region sortedSetOf
    // Под капотом он ссылается на TreeSet, а в лямба выражении указывается, по каким парам сортировать.
    // Это короче метод сортиворки массива (потом когда-нибудь почитать)
//    private val shopList = sortedSetOf<ShopItem>(object : Comparator<ShopItem>{
//        override fun compare(p0: ShopItem?, p1: ShopItem?): Int {
//        }
//    })
    //endregion
    private val shopList = sortedSetOf<ShopItem>({ p0, p1 -> p0.id.compareTo(p1.id) })

    private var autoIncrementId = 0

    init {
        for (i in 0 until 10) {
            val item = ShopItem("Name $i", i, Random.nextBoolean())
            addShopItem(item)
        }
    }

    private fun updateList() {
        shopListLD.value = shopList.toList()
    }


    // ############ Реализация репозитория ############
    override fun addShopItem(shopItem: ShopItem) {
        if (shopItem.id == ShopItem.UNDEFINED_ID){
            shopItem.id = autoIncrementId++ // оператор "пост инкремента"
        }
        shopList.add(shopItem)
        updateList()
    }

    override fun deleteShopItem(shopItem: ShopItem) {
        shopList.remove(shopItem)
        updateList()
    }

    override fun editShopItem(shopItem: ShopItem) {
        val oldElement = getShopItem(shopItem.id)
        deleteShopItem(oldElement)
        addShopItem(shopItem)
    }

    override fun getShopItem(shopItemId: Int): ShopItem {
        return shopList.find { it.id == shopItemId } ?: throw RuntimeException("Element with id $shopItemId not found")
    }

    override fun getShopList(): LiveData<List<ShopItem>> {
        return shopListLD
    }

}
*/
//endregion

class ShopListRepositoryImpl(application: Application): ShopListRepository  {

    private val shopListDao = AppDatabase.getInstance(application).shopListDao()
    private val mapper = ShopListMapper()


    // ############ Реализация репозитория ############
    override suspend fun addShopItem(shopItem: ShopItem) {
        shopListDao.addShopItem(mapper.mapEntityToDbModel(shopItem))
    }

    override suspend fun deleteShopItem(shopItem: ShopItem) {
        shopListDao.deleteShopItem(shopItem.id)
    }

    override suspend fun editShopItem(shopItem: ShopItem) {
        shopListDao.addShopItem(mapper.mapEntityToDbModel(shopItem))
    }

    override suspend fun getShopItem(shopItemId: Int): ShopItem {
        val dbModel = shopListDao.getShopItem(shopItemId)
        return mapper.mapDbModelToEntity(dbModel)
    }

    //region MediatorLiveData - крутая штука!
    /*
    Т.к. мы не можем напрямую отдать List<ShopItem> из-за LiveData, то нам нужен снова какой-то
    посредник, что будет преобразовывать одно в другое как в случае с мапером.
    Для этого уже существует некий MediatorLiveData:
        он позволяет перехватывать события из другой LiveData и каким-то образом на них реагировать.
        Можно преобразовывать в другой тип либо реагировать при выполнении как-то условия.

    Когда буду применять на практике, при необходимости как-то повлиять на LiveData, обязательно пересмотреть!!!!!!!!!!
    Ссылка на урок - https://stepik.org/lesson/709333/step/1?unit=709896
    */
    //endregion
    override fun getShopList(): LiveData<List<ShopItem>> = MediatorLiveData<List<ShopItem>>().apply {
        addSource(shopListDao.getShopList()) {
            value = mapper.mapListDbModelToListEntity(it)
        }
    }

}