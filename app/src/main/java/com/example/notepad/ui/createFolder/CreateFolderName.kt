package com.example.notepad.ui.createFolder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.notepad.data.utils.collectWithLifecycleState
import com.example.notepad.databinding.FolderCreateLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateFolderName : BottomSheetDialogFragment() {

    private val createFolderNameVM: CreateFolderNameVM by viewModels()
    private var binding: FolderCreateLayoutBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FolderCreateLayoutBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.collectWithLifecycleState(createFolderNameVM.effect){
            when(it) {
                is CreateFolderEffect.CloseScreen -> findNavController().popBackStack()
            }
        }

        binding?.applyBtn?.setOnClickListener {
            createFolderNameVM.createFolder(
                binding?.folderNameEdit?.text?.toString().orEmpty().trim()
            )
        }
        binding?.cancelBtn?.setOnClickListener { findNavController().navigateUp() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}