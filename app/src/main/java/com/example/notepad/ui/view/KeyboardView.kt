package com.example.notepad.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.notepad.R

class KeyboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private var keyBoardBtnListener: KeyBoardBtnListener? = null
    private val buttons = mutableListOf<View>()

    init {
        LayoutInflater.from(context).inflate(R.layout.keyboard_layout, this, true)
        initKeyboardBtn()
    }

    override fun onClick(p0: View?) {
        if (keyBoardBtnListener == null) return

        when(p0?.id){
            R.id.one -> keyBoardBtnListener?.onKeyboardClick(KeyboardBtnEnum.BTN_1)
            R.id.two -> keyBoardBtnListener?.onKeyboardClick(KeyboardBtnEnum.BTN_2)
            R.id.three -> keyBoardBtnListener?.onKeyboardClick(KeyboardBtnEnum.BTN_3)
            R.id.four -> keyBoardBtnListener?.onKeyboardClick(KeyboardBtnEnum.BTN_4)
            R.id.five -> keyBoardBtnListener?.onKeyboardClick(KeyboardBtnEnum.BTN_5)
            R.id.six -> keyBoardBtnListener?.onKeyboardClick(KeyboardBtnEnum.BTN_6)
            R.id.seven -> keyBoardBtnListener?.onKeyboardClick(KeyboardBtnEnum.BTN_7)
            R.id.eight -> keyBoardBtnListener?.onKeyboardClick(KeyboardBtnEnum.BTN_8)
            R.id.nine -> keyBoardBtnListener?.onKeyboardClick(KeyboardBtnEnum.BTN_9)
            R.id.zero -> keyBoardBtnListener?.onKeyboardClick(KeyboardBtnEnum.BTN_0)
            R.id.clear -> keyBoardBtnListener?.onKeyboardClick(KeyboardBtnEnum.BTN_CLEAR)
            else -> return
        }
    }

    fun setKeyboardBtnListener(keyBoardBtnListener: KeyBoardBtnListener) {
        this.keyBoardBtnListener = keyBoardBtnListener
    }

    private fun initKeyboardBtn(){
        buttons.add(findViewById(R.id.one))
        buttons.add(findViewById(R.id.two))
        buttons.add(findViewById(R.id.three))
        buttons.add(findViewById(R.id.four))
        buttons.add(findViewById(R.id.five))
        buttons.add(findViewById(R.id.six))
        buttons.add(findViewById(R.id.seven))
        buttons.add(findViewById(R.id.eight))
        buttons.add(findViewById(R.id.nine))
        buttons.add(findViewById(R.id.zero))
        buttons.add(findViewById(R.id.clear))

        for (v in buttons){
            v.setOnClickListener(this)
        }
    }
}

interface KeyBoardBtnListener{
    fun onKeyboardClick(keyboardBtnEnum: KeyboardBtnEnum)
}

enum class KeyboardBtnEnum(val value: Int){
    BTN_0(0),
    BTN_1(1),
    BTN_2(2),
    BTN_3(3),
    BTN_4(4),
    BTN_5(5),
    BTN_6(6),
    BTN_7(7),
    BTN_8(8),
    BTN_9(9),
    BTN_CLEAR(-1)
}