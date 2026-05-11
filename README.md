# Registro de Consumo

Registro de Consumo es una solución móvil nativa diseñada para centralizar el monitoreo de suministros básicos. La aplicación facilita el control de gastos mediante una interfaz intuitiva, permitiendo a los usuarios mantener un historial preciso y persistente de sus lecturas de medidores.

## Características

*   **Gestión Integral:** Registro detallado de lecturas de agua, electricidad y gas.
*   **Interfaz Moderna:** Desarrollado íntegramente con **Jetpack Compose** y componentes de **Material 3**.
*   **Persistencia Robusta:** Almacenamiento local mediante **Room Database** para garantizar la disponibilidad de los datos sin conexión.
*   **Arquitectura de Vanguardia:** Implementación del patrón **MVVM** (Model-View-ViewModel) y gestión de estados mediante **StateFlow**.
*   **Localización:** Soporte nativo para los idiomas **español e inglés**.
*   **Experiencia Fluida:** Procesamiento asíncrono utilizando **Corrutinas de Kotlin** para operaciones de entrada/salida eficientes.

## Stack Técnico

*   **Lenguaje:** [Kotlin](https://kotlinlang.org/)
*   **UI:** Jetpack Compose
*   **Database:** Room (SQLite)
*   **Gestión de Estado:** StateFlow / ViewModel
*   **Navegación:** Jetpack Navigation Component
*   **Dependency Management:** Version Catalog (libs.versions.toml)

## Requisitos de Instalación

1.  Clonar el repositorio:
    ```bash
    git clone https://github.com/JulianGon87/RegistroConsumo.git
    ```
2.  Abrir el proyecto en **Android Studio (Ladybug o superior)**.
3.  Sincronizar el proyecto con los archivos de Gradle.
4.  Ejecutar en un dispositivo físico o emulador con **API 26 (Android 8.0)** o superior.

## Vista de la Aplicación

La aplicación se estructura en dos módulos principales:
1.  **Dashboard de Consumos:** Un listado dinámico que resume los registros almacenados, permitiendo la eliminación selectiva de entradas.
2.  **Módulo de Entrada:** Formulario optimizado con selectores de fecha inteligentes y categorización por iconos visuales.
