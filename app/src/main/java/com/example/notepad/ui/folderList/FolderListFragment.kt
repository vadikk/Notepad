package com.example.notepad.ui.folderList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.notepad.R
import com.example.notepad.data.utils.collectWithLifecycleState
import com.example.notepad.databinding.FolderListLayoutBinding
import com.example.notepad.ui.folderList.adapter.FolderAdapter
import com.example.notepad.ui.folderList.adapter.FolderItemDecoration
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FolderListFragment : BottomSheetDialogFragment() {

    private val folderListVM: FolderListVM by viewModels()
    private var folderAdapter: FolderAdapter? = null
    private var binding: FolderListLayoutBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FolderListLayoutBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        folderAdapter = FolderAdapter(
            { findNavController().navigate(R.id.action_folderListFragment_to_createFolderName) },
            { folderListVM.selectFolder(it) { findNavController().popBackStack() } }
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
}