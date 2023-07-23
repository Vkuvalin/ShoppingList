package com.example.shoppinglist.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.shoppinglist.R
import com.example.shoppinglist.domain.ShopItem

class ShopItemActivity : AppCompatActivity(), ShopItemFragment.OnEditingFinishedListener {

    companion object {

        private const val EXTRA_SCREEN_MODE = "extra_mode"
        private const val EXTRA_SHOP_ITEM_ID = "extra_shop_item_id"
        private const val MODE_EDIT = "mode_edit"
        private const val MODE_ADD = "mode_add"
        private const val MODE_UNKNOWN = ""

        fun newIntentAddItem(context: Context): Intent {
            val intent = Intent(context, ShopItemActivity::class.java)
            intent.putExtra(EXTRA_SCREEN_MODE, MODE_ADD)
            return intent
        }

        fun newIntentEditItem(context: Context, shopItemId: Int): Intent {
            val intent = Intent(context, ShopItemActivity::class.java)
            intent.putExtra(EXTRA_SCREEN_MODE, MODE_EDIT)
            intent.putExtra(EXTRA_SHOP_ITEM_ID, shopItemId)
            return intent
        }
    }

    /* ####################################### ПЕРЕМЕННЫЕ ####################################### */

    private var screenMode = MODE_UNKNOWN
    private var shopItemId = ShopItem.UNDEFINED_ID


    /* ################################### ФУНКЦИИ (основные) ################################### */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_item)

        parseIntent()
        //region Нюанс с множественным созданием фрагментов. Автоматическое пересоздание Activity
        /*
        Т.к. система самостоятельно пересоздает fragment при необходимости (например при перевороте),
        то launchRightMode() нужно вызывать лишь 1 раз - при первом создании Activity.
        */
        //endregion
        if (savedInstanceState == null) {
            launchRightMode()
        }
    }


    private fun launchRightMode() {
        val fragment = when (screenMode) {
            MODE_EDIT -> ShopItemFragment.newInstanceEditItem(shopItemId)
            MODE_ADD -> ShopItemFragment.newInstanceAddItem()
            else -> throw RuntimeException("Unknown screen mode")
        }
        supportFragmentManager.beginTransaction()
            //region Нюанс с множественным созданием фрагментов - метод .add
            /*
            Какой есть нюанс с методом .add?
            Если переворачивать экран, то onCreate будет вызываться по несколько раз, просто
            накладывая фрагменты друг на друга. Для решения данного момента, можно вместо
            .add(R.id.shop_item_container, fragment)
            воспользоваться методом .replace(R.id.shop_item_container, fragment)
            */
            //endregion
            .replace(R.id.shop_item_container, fragment) // 1 - FragmentContainerView из Activity
            .commit() // Запускает транзакцию на выполнение
    }

    /* ############################### ФУНКЦИИ (вспомогательные) ################################ */

    private fun parseIntent() {
        if (!intent.hasExtra(EXTRA_SCREEN_MODE)) {
            throw RuntimeException("Param screen mode absent")
        }
        val mode = intent.getStringExtra(EXTRA_SCREEN_MODE)
        if (mode != MODE_ADD && mode != MODE_EDIT) {
            throw RuntimeException("Unknown screen mode $mode")
        }

        screenMode = mode

        if (screenMode == MODE_EDIT) {
            if (!intent.hasExtra(EXTRA_SHOP_ITEM_ID)) {
                throw RuntimeException("Param shop item id is absent")
            }
            shopItemId = intent.getIntExtra(EXTRA_SHOP_ITEM_ID, ShopItem.UNDEFINED_ID)
        }
    }


    /* ####################################### ИНТЕРФЕЙСЫ ####################################### */
    override fun onEditingFinished() {
        finish()
    }
}