package com.juliangonzalez.registroconsumo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// ==========================================
// PANTALLA 1: FORMULARIO DE REGISTRO
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistro(viewModel: MedicionViewModel, onRegistroExitoso: () -> Unit) {
    // 1. Variables de Estado: Guarda temporalmente lo que el usuario escribe en pantalla
    var valorText by remember { mutableStateOf("") }
    var fechaText by remember { mutableStateOf("") }
    var tipoSeleccionado by remember { mutableStateOf("Agua") }

    // Calendario
    var mostrarCalendario by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // Opciones vinculadas a recursos para soportar multi-idioma
    val opciones = listOf(
        stringResource(R.string.op_agua),
        stringResource(R.string.op_luz),
        stringResource(R.string.op_gas)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.titulo_registro), fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

        OutlinedTextField(
            value = valorText,
            onValueChange = { valorText = it },
            label = { Text(stringResource(R.string.label_valor)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // --- CAMPO DE FECHA CON CALENDARIO ---
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = fechaText,
                onValueChange = { }, // vacío porque el usuario no escribirá con el teclado
                readOnly = true,     // Bloquea la escritura manual
                label = { Text(stringResource(R.string.label_fecha)) },
                placeholder = { Text("DD-MM-AA") },
                modifier = Modifier.fillMaxWidth(),
                // ícono de calendario al final del campo
                trailingIcon = {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                }
            )
            // Capa invisible para detectar clics
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { mostrarCalendario = true }
            )
        }

        // --- LÓGICA DEL POP-UP DEL CALENDARIO ---
        if (mostrarCalendario) {
            DatePickerDialog(
                onDismissRequest = { mostrarCalendario = false },
                confirmButton = {
                    TextButton(onClick = {
                        mostrarCalendario = false
                        // Transformamos la fecha seleccionada a texto
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formateador = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                            formateador.timeZone = TimeZone.getTimeZone("UTC") // Solución de zona horaria
                            fechaText = formateador.format(Date(millis))
                        }
                    }) {
                        Text(stringResource(R.string.btn_aceptar))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarCalendario = false }) {
                        Text(stringResource(R.string.btn_cancelar))
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = stringResource(R.string.label_medidor), modifier = Modifier.align(Alignment.Start))

        opciones.forEach { opcion ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                RadioButton(
                    selected = (tipoSeleccionado == opcion),
                    onClick = { tipoSeleccionado = opcion }
                )
                Text(text = opcion)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val valorDouble = valorText.toDoubleOrNull() ?: 0.0
                viewModel.registrarMedicion(tipoSeleccionado, valorDouble, fechaText)
                onRegistroExitoso()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.btn_registrar))
        }
    }
}

// ==========================================
// PANTALLA 2: LISTADO DE MEDICIONES
// ==========================================
@Composable
fun PantallaListado(viewModel: MedicionViewModel, onNavegarRegistro: () -> Unit) {
    // Observa el StateFlow del ViewModel
    val mediciones by viewModel.listaMediciones.collectAsState()

    // Scaffold permite colocar el botón flotante (+) en la esquina
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNavegarRegistro) {
                Text("+", fontSize = 24.sp)
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.titulo_listado),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold // Pone el texto en negrita
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.leyenda_agregar),
                    fontSize = 14.sp,
                    color = Color.Gray // Le da un tono más suave al texto explicativo
                )
                Text(
                    text = stringResource(R.string.leyenda_borrar),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            HorizontalDivider()

            // LazyColumn para listas dinámicas eficientes
            LazyColumn {
                items(mediciones) { medicion ->
                    ItemMedicion(
                        medicion = medicion,
                        onDelete = { viewModel.borrarMedicion(medicion) }
                    )
                }
            }
        }
    }
}

// ==========================================
// DISEÑO INDIVIDUAL DE CADA FILA (ITEM)
// ==========================================
@Composable
fun ItemMedicion(medicion: Medicion, onDelete: () -> Unit) {
    val mostrarConfirmacion = remember { mutableStateOf(false) }

    // Mensaje de confirmación
    if (mostrarConfirmacion.value) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacion.value = false },
            title = { Text(stringResource(R.string.confirmar_borrado_titulo)) },
            text = { Text(stringResource(R.string.confirmar_borrado_mensaje)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        mostrarConfirmacion.value = false
                    }
                ) {
                    Text(stringResource(R.string.btn_eliminar), color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmacion.value = false }) {
                    Text(stringResource(R.string.btn_cancelar))
                }
            }
        )
    }

    // Asigna el icono según el texto en la base de datos
    val icono = when (medicion.tipo) {
        stringResource(R.string.op_agua) -> R.drawable.ic_agua
        stringResource(R.string.op_luz) -> R.drawable.ic_luz
        else -> R.drawable.ic_gas
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Uso de recursos drawables
        Icon(
            painter = painterResource(id = icono),
            contentDescription = medicion.tipo,
            modifier = Modifier.size(40.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(16.dp))

        // Datos del medidor
        Column(modifier = Modifier.weight(1f)) {
            Text(text = medicion.tipo, fontSize = 18.sp)
            Text(text = medicion.fecha, color = Color.Gray)
        }
        
        Column(horizontalAlignment = Alignment.End) {
            // Formateo inteligente: muestra como entero si no tiene decimales significativos
            val valorTexto = if (medicion.valor % 1 == 0.0) {
                medicion.valor.toInt().toString()
            } else {
                medicion.valor.toString()
            }
            
            Text(text = valorTexto, fontSize = 18.sp)
            IconButton(onClick = { mostrarConfirmacion.value = true }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Borrar",
                    tint = Color.Gray
                )
            }
        }
    }
    HorizontalDivider() // Línea que separa elementos
}