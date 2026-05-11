package com.juliangonzalez.registroconsumo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Prepara la conexión a la base de datos SQLite (Room)
        val db = Room.databaseBuilder(
            applicationContext,
            MedicionDatabase::class.java, "mediciones-db"
        ).build()
        val dao = db.medicionDao()

        // 2. Factory: para crear el DAO
        val viewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MedicionViewModel(dao) as T
            }
        }
        val viewModel = ViewModelProvider(this, viewModelFactory)[MedicionViewModel::class.java]

        // 3. Renderiza la interfaz visual
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 4. Controlador de Navegación: recuerda en qué pantalla estamos
                    val navController = rememberNavController()

                    // 5. NavHost: define las rutas. Inicia en "lista"
                    NavHost(navController = navController, startDestination = "lista") {

                        // Ruta A: Pantalla de Listado
                        composable("lista") {
                            PantallaListado(
                                viewModel = viewModel,
                                onNavegarRegistro = {
                                    // al presionar "+" navega a "registro"
                                    navController.navigate("registro")
                                }
                            )
                        }

                        // Ruta B: Pantalla de Registro
                        composable("registro") {
                            PantallaRegistro(
                                viewModel = viewModel,
                                onRegistroExitoso = {
                                    // Vuelve a la lista tras el registro
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}