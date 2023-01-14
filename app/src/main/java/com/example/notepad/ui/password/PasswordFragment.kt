package com.example.notepad.ui.password

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.notepad.R
import com.example.notepad.data.utils.collectWithLifecycleState
import com.example.notepad.databinding.PasswordLayoutBinding
import com.example.notepad.domain.usecase.PasswordType
import com.example.notepad.domain.usecase.mapToPasswordType
import com.example.notepad.ui.view.KeyBoardBtnListener
import com.example.notepad.ui.view.KeyboardBtnEnum

class PasswordFragment: Fragment(), KeyBoardBtnListener {

    private val passwordVM by viewModels<PasswordVM>()
    private var binding: PasswordLayoutBinding? = null
    private var originalPassword = ""
    private var passwordType = PasswordType.IDLE
    private var noteUid = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.apply {
            originalPassword = getString(PASSWORD, "").orEmpty()
            passwordType = getInt(PASSWORD_TYPE, -1).mapToPasswordType()
            noteUid = getString(NOTE_UID, "")
        }
        passwordVM.passOriginalPassword(originalPassword)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = PasswordLayoutBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.cancelBtn?.setOnClickListener { findNavController().popBackStack() }

        binding?.keyboardView?.setKeyboardBtnListener(this)
        binding?.passwordBtn?.text =
            when(passwordType){
                PasswordType.APPLY -> "Apply password"
                PasswordType.CONFIRM -> "Confirm password"
                PasswordType.REMOVE -> "Remove password"
                else -> ""
            }

        binding?.passwordBtn?.setOnClickListener {
            if (passwordType == PasswordType.APPLY || passwordType == PasswordType.REMOVE) {
                val password = if (passwordType == PasswordType.APPLY) passwordVM.passwordText else ""
                setFragmentResult(SET_PASSWORD, bundleOf(PASSWORD to password))
                findNavController().popBackStack()
            }else {
                val action = PasswordFragmentDirections.actionPasswordFragmentToCreateNoteFragment(
                    noteUid
                )
                findNavController().navigate(action)
            }

        }

        collectWithLifecycleState(passwordVM.passwordFlow) {
            changeCircleUi(passwordVM.passwordText.length)
            binding?.passwordBtn?.isEnabled = it.isEnableBtn
            binding?.errorPassword?.isVisible = it.isShowError
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onKeyboardClick(keyboardBtnEnum: KeyboardBtnEnum) {
        passwordVM.setPassword(keyboardBtnEnum)
    }

    private fun changeCircleUi(passwordLength: Int) {
        when(passwordLength){
            4 -> binding?.imageviewCircle4?.setImageResource(R.drawable.circle2)
            3 -> {
                binding?.imageviewCircle4?.setImageResource(R.drawable.circle)
                binding?.imageviewCircle3?.setImageResource(R.drawable.circle2)
            }
            2 -> {
                binding?.imageviewCircle3?.setImageResource(R.drawable.circle)
                binding?.imageviewCircle2?.setImageResource(R.drawable.circle2)
            }
            1 -> {
                binding?.imageviewCircle2?.setImageResource(R.drawable.circle)
                binding?.imageviewCircle1?.setImageResource(R.drawable.circle2)
            }
            0 -> binding?.imageviewCircle1?.setImageResource(R.drawable.circle)
        }
    }

    companion object {
        const val PASSWORD = "PASSWORD"
        const val SET_PASSWORD = "SET_PASSWORD"
        const val PASSWORD_TYPE = "PASSWORD_TYPE"
        private const val NOTE_UID = "NOTE_UID"

        fun bundle(uid: String, password: String, type: Int) = bundleOf(
            NOTE_UID to uid,
            PASSWORD to password,
            PASSWORD_TYPE to type
        )

        fun bundle(password: String, type: Int) = bundleOf(
            PASSWORD to password,
            PASSWORD_TYPE to type
        )
    }
}