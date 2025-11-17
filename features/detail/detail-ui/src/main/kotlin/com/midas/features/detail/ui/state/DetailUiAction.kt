package com.midas.features.detail.ui.state

sealed interface DetailUiAction {
    data object RefreshDetail : DetailUiAction
    data object ToggleFavorite : DetailUiAction
    data object DismissError : DetailUiAction
}
