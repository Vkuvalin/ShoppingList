package com.example.shoppinglist.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shoppinglist.R

// TODO: Переименуйте аргументы параметров, выберите имена, которые соответствуют
// параметры инициализации фрагмента, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BlankFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BlankFragment : Fragment() {
    // TODO: Переименовывать и изменять типы параметров
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Получение значений этих самых параметров
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Устанавливаем макет для этого фрагмента
        return inflater.inflate(R.layout.fragment_blank, container, false)
    }

    companion object {
        /**
         * Используйте этот фабричный метод для создания нового экземпляра
         * этот фрагмент с использованием предоставленных параметров.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return Новый экземпляр фрагмента BlankFragment.
         */
        // TODO: Переименовывать и изменять типы и количество параметров
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BlankFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}