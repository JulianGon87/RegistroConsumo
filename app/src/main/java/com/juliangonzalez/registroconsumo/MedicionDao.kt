
package com.juliangonzalez.registroconsumo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicionDao {
    // registros ordenados por fecha
    // Flow para actualizacion automatica de la lista
    @Query("SELECT * FROM mediciones ORDER BY fecha DESC")
    fun getAll(): Flow<List<Medicion>>

    // Función que guarda medicion
    @Insert
    fun insert(medicion: Medicion): Long

    // Función que elimina una medición
    @Delete
    fun delete(medicion: Medicion)
}