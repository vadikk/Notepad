package com.example.notepad.ui.folderChoose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.notepad.data.utils.collectWithLifecycleState
import com.example.notepad.databinding.FolderChooseBinding
import com.example.notepad.ui.folderList.adapter.FolderAdapter
import com.example.notepad.ui.folderList.adapter.FolderItemDecoration
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderChooseFragment : BottomSheetDialogFragment() {

    private val folderListVM: FolderChooseVM by viewModels()
    private var folderAdapter: FolderAdapter? = null
    private var binding: FolderChooseBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FolderChooseBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        folderAdapter = FolderAdapter(
            { },
            {
                setFragmentResult(FOLDER_CHOOSE, bundleOf(FOLDER_UID to it.toString()))
                findNavController().popBackStack()
            }
        )

        binding?.folderRV?.also {
            it.layoutManager = GridLayoutManager(context, 3)
            it.adapter = folderAdapter
            it.addItemDecoration(FolderItemDecoration())
        }
        binding?.cancelBtn?.setOnClickListener { findNavController().navigateUp() }
        viewLifecycleOwner.collectWithLifecycleState(folderListVM.folderEntities) {
            folderAdapter?.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        const val FOLDER_CHOOSE = "FOLDER_CHOOSE"
        const val FOLDER_UID = "FOLDER_UID"
    }
}