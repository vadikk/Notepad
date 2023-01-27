package com.example.notepad.ui.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class BaseVM<S: State, E: Event, V: Effect>: ViewModel() {

    private val initialState: S by lazy { createInitialState() }

    private val _viewState: MutableStateFlow<S> = MutableStateFlow(initialState)
    val viewState = _viewState.asStateFlow()

    val currentState: S
        get() = viewState.value

    private val _event : MutableSharedFlow<E> = MutableSharedFlow()
    private val event = _event.asSharedFlow()

    private val _effect : Channel<V> = Channel()
    val effect = _effect.receiveAsFlow()

    protected abstract fun createInitialState(): S
    protected abstract fun handleEvents(event: E)

    init {
        subscribeToEvents()
    }

    fun setEvent(event: E) {
        viewModelScope.launch { _event.emit(event) }
    }

    protected fun setState(reducer: S.() -> S) {
        _viewState.update(reducer)
    }

    protected fun setEffect(builder: () -> V) {
        val effectValue = builder()
        viewModelScope.launch { _effect.send(effectValue) }
    }

    private fun subscribeToEvents() {
        viewModelScope.launch {
            event.collect {
                handleEvents(it)
            }
        }
    }
}