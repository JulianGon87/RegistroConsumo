package com.juliangonzalez.registroconsumo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// El ViewModel separa la lógica de la base de datos de la interfaz visual
class MedicionViewModel(private val dao: MedicionDao) : ViewModel() {

    // Obtiene la lista de mediciones y la convierte a StateFlow para la UI
    // Esto cumple con el requisito de usar "FlowState" o similar en el ViewModel
    val listaMediciones: StateFlow<List<Medicion>> = dao.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Función para guardar una nueva medición usando una Corrutina
    fun registrarMedicion(tipo: String, valor: Double, fecha: String) {
        viewModelScope.launch {
            val nuevaMedicion = Medicion(tipo = tipo, valor = valor, fecha = fecha)
            withContext(Dispatchers.IO) {
                dao.insert(nuevaMedicion)
            }
        }
    }

    // Función para borrar una medición
    fun borrarMedicion(medicion: Medicion) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dao.delete(medicion)
            }
        }
    }
}