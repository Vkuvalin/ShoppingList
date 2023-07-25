package com.example.shoppinglist.data

import com.example.shoppinglist.domain.ShopItem

//region Пояснение
/*
Данный вид классов называются Mappers. Т.к. нам нельзя, чтобы Domain слой от чего-то зависел
и "знал" о других слоях, то мапер нужно создавать в дата слое.
Он служит для преобразования сущности domain слоя в модель базы данных (ShopItemDbModel).
*/
//endregion
class ShopListMapper {

    fun mapEntityToDbModel(shopItem: ShopItem) = ShopItemDbModel(
        id = shopItem.id,
        name = shopItem.name,
        count = shopItem.count,
        enabled = shopItem.enabled
    )

    fun mapDbModelToEntity(shopItemDbModel: ShopItemDbModel) = ShopItem(
        id = shopItemDbModel.id,
        name = shopItemDbModel.name,
        count = shopItemDbModel.count,
        enabled = shopItemDbModel.enabled
    )


    fun mapListDbModelToListEntity(list: List<ShopItemDbModel>) = list.map {
        mapDbModelToEntity(it)
    }
}