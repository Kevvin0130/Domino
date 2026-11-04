package com.example.domino.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domino.data.game.FirestoreGameRepository
import com.example.domino.data.game.GameRepository
import com.example.domino.domain.model.GameSummary
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistorialUiState(
    val partidas: List<GameSummary> = emptyList(),
    val cargando: Boolean = true,
    val error: String? = null
)

/** B4: alimenta la pantalla de Historial con `users/{uid}/history` (ya existe en el repositorio). */
class HistorialViewModel(
    private val repository: GameRepository = FirestoreGameRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistorialUiState())
    val uiState: StateFlow<HistorialUiState> = _uiState.asStateFlow()

    private var job: Job? = null

    fun cargar(uid: String) {
        if (uid.isBlank()) return
        job?.cancel()
        _uiState.value = HistorialUiState()
        job = viewModelScope.launch {
            repository.observeHistory(uid)
                .catch { e -> _uiState.update { it.copy(cargando = false, error = e.message ?: "No se pudo cargar el historial") } }
                .collect { lista -> _uiState.update { it.copy(partidas = lista, cargando = false, error = null) } }
        }
    }
}
