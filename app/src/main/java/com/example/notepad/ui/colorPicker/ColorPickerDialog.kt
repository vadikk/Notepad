package com.example.notepad.ui.colorPicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.notepad.data.utils.collectWithLifecycleState
import com.example.notepad.databinding.FragmentColorPickerDialogBinding
import com.example.notepad.ui.colorPicker.adapter.ColorAdapter
import com.example.notepad.ui.colorPicker.adapter.ColorItemDecoration

class ColorPickerDialog : DialogFragment() {

    private var adapter: ColorAdapter? = null
    private var binding: FragmentColorPickerDialogBinding? = null
    private val colorPickerVM: ColorPickerVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentColorPickerDialogBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ColorAdapter { selectColor(it) }
        binding?.colorRV?.also {
            it.layoutManager = GridLayoutManager(context, 5)
            it.adapter = adapter
            it.addItemDecoration(ColorItemDecoration())
        }

        viewLifecycleOwner.collectWithLifecycleState(colorPickerVM.colorFlow) {
            adapter?.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun selectColor(color: Int) {
        setFragmentResult(SELECT_COLOR, bundleOf(COLOR_ITEM to color))
        findNavController().navigateUp()
    }

    companion object {
        const val SELECT_COLOR = "SELECT_COLOR"
        const val COLOR_ITEM = "COLOR_ITEM"
    }
}