package com.amirmuhsin.listinghelper.core_views.base.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amirmuhsin.listinghelper.core_views.events.command.Command
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel: ViewModel() {

    private val _flLoading = MutableStateFlow(false)
    val flLoading = _flLoading.asStateFlow()

    private val _flSuccessSnackbar = MutableSharedFlow<String>(replay = 0)
    val flSuccessSnackbar = _flSuccessSnackbar.asSharedFlow()

    private val _flErrorSnackbar = MutableSharedFlow<String>(replay = 0)
    val flErrorSnackbar = _flErrorSnackbar.asSharedFlow()

    private val _fCommand = MutableSharedFlow<Command>(replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val flCommand = _fCommand.asSharedFlow()

    open fun sendCommand(command: Command) {
        _fCommand.tryEmit(command)
    }

    protected fun showProgressDialog() {
        _flLoading.tryEmit(true)
    }

    protected fun hideProgressDialog() {
        _flLoading.tryEmit(false)
    }

    protected fun showSuccessSnackbar(message: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _flSuccessSnackbar.emit(message)
        }
    }

    protected fun showErrorSnackbar(message: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _flErrorSnackbar.emit(message)
        }
    }
}