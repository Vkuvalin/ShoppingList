package com.example.shoppinglist.presentation

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.shoppinglist.R
import com.example.shoppinglist.domain.ShopItem
import com.google.android.material.textfield.TextInputLayout

//region fun onCreate (парочка моментов в использовании) - оставлю как напоминание.
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        // Тк. onCreate вызывается до onViewCreated, то тут с view нельзя работать.
//        // Все методы переносим выше во onViewCreated.
//    }
//endregion
//region Почему никогда нельзя передавать какие-то параметры в конструкторе фрагмента
/*
При первом созданнии он будет проинициализирован нормально, но при:
- перевороте
- смене языка
- и тп.
Андроид его пересоздаст, вызвав при этом пустой конструктор.
Тогда для передачи необходимых параметров во фрагмент нужно использовать "arguments" типа Bundle?
*/
//endregion
class ShopItemFragment : Fragment() {

    companion object {

        private const val SCREEN_MODE = "extra_mode"
        private const val SHOP_ITEM_ID = "extra_shop_item_id"
        private const val MODE_EDIT = "mode_edit"
        private const val MODE_ADD = "mode_add"
        private const val MODE_UNKNOWN = ""

        private var screenMode: String = MODE_UNKNOWN
        private var shopItemId: Int = ShopItem.UNDEFINED_ID

        fun newInstanceAddItem(): ShopItemFragment {
            //region Тоже самое но типа в стиле java, а ниже в стиле kotlin.
            /*
            val args = Bundle()
            args.putString(SCREEN_MODE, MODE_ADD)
            val fragment = ShopItemFragment()
            fragment.arguments = args
            return fragment
            */
            //endregion
            //region Сокращение_1
            /*
            val args = Bundle().apply {
                putString(SCREEN_MODE, MODE_ADD)
            }
            val fragment = ShopItemFragment().apply {
                arguments = args
            }
            return fragment
            */
            //endregion
            return ShopItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_ADD)
                }
            }
        }

        fun newInstanceEditItem(shopItemId: Int): ShopItemFragment {
            return ShopItemFragment().apply {
                arguments = Bundle().apply {
                    putString(SCREEN_MODE, MODE_EDIT)
                    putInt(SHOP_ITEM_ID, shopItemId)
                }
            }
        }
    }

    /* ####################################### ПЕРЕМЕННЫЕ ####################################### */
    //region ВАЖНО!
        /* Т.к. при перевороте и иных действиях фрагмент автоматически пересоздается, а значения
        всех переменных обнуляется, что очень важно учитывать. Т.е. нельзя снаружи что-то присваивать
        Нельзя допускать создание вот таких переменных:
            var onEditingFinishedListener: OnEditingFinishedListener? = null

        Как вариант данный интерфейс можно реализовать прямо в Activity
        */
    //endregion
    private lateinit var viewModel: ShopItemViewModel
    private lateinit var onEditingFinishedListener: OnEditingFinishedListener

    private lateinit var tilName: TextInputLayout
    private lateinit var tilCount: TextInputLayout
    private lateinit var etName: EditText
    private lateinit var etCount: EditText
    private lateinit var buttonSave: Button



    /* ################################### ФУНКЦИИ (основные) ################################### */

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnEditingFinishedListener){
            onEditingFinishedListener = context
        } else {
            //region Комментарий
            /* Исключение здесь необходимо, чтобы при использовании данного фрагмента другим
            * разработчиком (мной же) в других активити, не была забыта какая-то ключевая реализация
            * Конечно, если данный интерфейс важен для работы фрагмента. Без него будут баги и тп
            */
            //endregion
            throw RuntimeException("Activity must implement interface OnEditingFinishedListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shop_item, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseParams()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState) // Сюда прилетает view из onCreateView

        viewModel = ViewModelProvider(this)[ShopItemViewModel::class.java]
        initViews(view)
        addTextChangeListeners()
        launchRightMode()
        observeViewModel()
    }



    private fun observeViewModel() {
        //region Почему в observe нельзя передавать this в случае работы с фрагментом
        /* Потому что его жизненный цикл отличается от цикла view, где может произойти ситуация,
        *  что fragment ещё жив,но view'хе пиздец. Тогда приложение упадет.
        * Тогда логично, что мы должны быть привязаны к view, что создаем в onCreateView
        * Тогда и заряжаем сюда viewLifecycleOwner!
        */
        //endregion
        viewModel.errorInputName.observe(viewLifecycleOwner) {
            val message = if (it) {
                getString(R.string.error_input_name)
            } else {
                null
            }
            tilName.error = message
        }
        viewModel.errorInputCount.observe(viewLifecycleOwner) {
            val message = if (it) {
                getString(R.string.error_input_count)
            } else {
                null
            }
            tilCount.error = message
        }
        viewModel.shouldCloseScreen.observe(viewLifecycleOwner) {
            //region Первый вариант реализации с комментариями
            //region requireActivity() - нюанс. Почему выбрали activity?
            /*
            Т.к. мы можем обратиться к активити из фрагмента после её удаления, тут лучше вызывать
            обращаться к ней именно с помощью activity, а не requireActivity().
            Во фрагментах много похожих методов ввиду выше описаных нюансов работы.
            Например context и requireContext.
            */
            //endregion
//            activity?.onBackPressed() // Первый вариант
            //region Нюанс с фрагментом -> addToBackStack()
            /*
            Работая с фрагментом в горизонтальном положении, где открыты: 1я Активити и Фрагмент.
            Приложение нахуй вырубится или (в каком-то ином случае) сразу закроются обе, поэтому
            для решения данной проблемы нужно добавлять фрагмент в BackStack.
            Данное будет реализовано в MainActivity -> launchFragment() -> addToBackStack()

            Важно!
            Если нет необходимости, чтобы все фрагменты хранились в BackStack, то нужно удалять
            старый фрагмент, перед добавлением нового методом popBackStack()
            */
            //endregion
            //endregion
            onEditingFinishedListener.onEditingFinished()
        }
    }


    private fun launchAddMode() {
        buttonSave.setOnClickListener {
            viewModel.addShopItem(etName.text?.toString(), etCount.text?.toString())
        }
    }

    private fun launchEditMode() {
        viewModel.getShopItem(shopItemId)
        viewModel.shopItem.observe(viewLifecycleOwner) {
            etName.setText(it.name)
            etCount.setText(it.count.toString())
            buttonSave.setOnClickListener {
                viewModel.editShopItem(etName.text?.toString(), etCount.text?.toString())
            }
        }
    }

    //region addTextChangeListeners() - установка слушателей текста
    private fun addTextChangeListeners() {

        etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorInputName()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        etCount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorInputCount()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }
    //endregion

    private fun launchRightMode() {
        when (screenMode) {
            MODE_EDIT -> launchEditMode()
            MODE_ADD -> launchAddMode()
        }
    }


    /* ############################### ФУНКЦИИ (вспомогательные) ################################ */

    //region initViews(view: View)
    fun initViews(view: View) {
        tilName = view.findViewById(R.id.til_name)
        tilCount = view.findViewById(R.id.til_count)
        etName = view.findViewById(R.id.et_name)
        etCount = view.findViewById(R.id.et_count)
        buttonSave = view.findViewById(R.id.save_button)
    }
    //endregion
    //region parseParams() - Обработка исключений
    private fun parseParams() {

        //region requireActivity()
        /*
        Этот метод возвращает ссылку на Activity, к которой прикреплен данный фрагмент.
        В данном случае вообще неправильно организовывать запуск через intent
        Как вариант -> передать всё в конструкторе
        */
        //endregion
        //region Старая реализация
//        if (!requireActivity().intent.hasExtra(SCREEN_MODE)) {
//            throw RuntimeException("Param screen mode absent")
//        }
//        val mode = intent.getStringExtra(SCREEN_MODE)
//        if (mode != MODE_ADD && mode != MODE_EDIT) {
//            throw RuntimeException("Unknown screen mode $mode")
//        }
//
//        screenMode = mode
//
//        if (screenMode == MODE_EDIT) {
//            if (!intent.hasExtra(SHOP_ITEM_ID)) {
//                throw RuntimeException("Param shop item id is absent")
//            }
//            shopItemId = intent.getIntExtra(SHOP_ITEM_ID, ShopItem.UNDEFINED_ID)
//        }
        //endregion

        val args = requireArguments()
        if (!args.containsKey(SCREEN_MODE)) {
            throw RuntimeException("Param screen mode absent")
        }
        val mode = args.getString(SCREEN_MODE)
        if (mode != MODE_ADD && mode != MODE_EDIT) {
            throw RuntimeException("Unknown screen mode $mode")
        }
        screenMode = mode
        if (screenMode == MODE_EDIT) {
            if (!args.containsKey(SHOP_ITEM_ID)) {
                throw RuntimeException("Param shop item id is absent")
            }
            shopItemId = args.getInt(SHOP_ITEM_ID, ShopItem.UNDEFINED_ID)
        }
    }
    //endregion



    /* ####################################### ИНТЕРФЕЙСЫ ####################################### */
    interface OnEditingFinishedListener {
        fun onEditingFinished()
    }
}











