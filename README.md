<div align="center">

# PagoFlex - Android

Aplicación móvil de **PagoFlex** desarrollada con Kotlin, Jetpack Compose y Material 3, conectada a un backend de microservicios mediante API REST.

<p>
  <img alt="Kotlin 2.4.21" src="https://img.shields.io/badge/Kotlin-2.4.21-555555?style=for-the-badge&amp;logo=kotlin&amp;logoColor=white&amp;labelColor=7F52FF">
  <img alt="Jetpack Compose" src="https://img.shields.io/badge/Jetpack-Compose-555555?style=for-the-badge&amp;logo=jetpackcompose&amp;logoColor=white&amp;labelColor=4285F4">
  <img alt="Android" src="https://img.shields.io/badge/Android-API%2024%2B-555555?style=for-the-badge&amp;logo=android&amp;logoColor=white&amp;labelColor=3DDC84">
</p>

<p>
  <img alt="Gradle 9.6.0" src="https://img.shields.io/badge/Gradle-9.6.0-555555?style=for-the-badge&amp;logo=gradle&amp;logoColor=white&amp;labelColor=02303A">
  <img alt="Material 3" src="https://img.shields.io/badge/Material-3-555555?style=for-the-badge&amp;logo=materialdesign&amp;logoColor=white&amp;labelColor=757575">
  <img alt="Retrofit 3.0.0" src="https://img.shields.io/badge/Retrofit-3.0.0-555555?style=for-the-badge&amp;labelColor=3E7C59">
</p>

<p><strong>Integrantes</strong></p>

<p>
  <a href="https://github.com/Bruno0416">Bruno Valladares</a>
  &nbsp;·&nbsp;
  <a href="https://github.com/Adonysjr">Adonys Maceo</a>
</p>

<p>
  <a href="#estructura-del-repositorio"><strong>Estructura</strong></a> ·
  <a href="#estado-de-las-funcionalidades"><strong>Estado</strong></a> ·
  <a href="#requisitos-previos"><strong>Requisitos</strong></a> ·
  <a href="#conectar-el-backend"><strong>Backend</strong></a> ·
  <a href="#ejecutar-la-aplicación"><strong>Ejecución</strong></a> ·
  <a href="#tests"><strong>Tests</strong></a>
</p>

</div>

---

## Estructura del repositorio

```text
pagoflex/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── kotlin/com/pagoflex/app/
│   │   │   │   ├── core/
│   │   │   │   │   ├── data/                # persistencia local y utilidades remotas
│   │   │   │   │   ├── navigation/          # rutas y grafos por perfil
│   │   │   │   │   ├── network/             # Retrofit, sesiones e interceptores
│   │   │   │   │   ├── rules/               # reglas y cálculos de negocio
│   │   │   │   │   ├── ui/                  # componentes compartidos y tema
│   │   │   │   │   └── validators/          # validaciones reutilizables
│   │   │   │   ├── features/
│   │   │   │   │   ├── auth/                # inicio de sesión, registro y recuperación
│   │   │   │   │   ├── home/                # resumen y compromisos vigentes
│   │   │   │   │   ├── detalle/             # detalle de compromisos
│   │   │   │   │   ├── pago/                # cotización y confirmación de pagos
│   │   │   │   │   ├── historial/           # pagos, anulaciones y comprobantes
│   │   │   │   │   ├── cumplimiento/        # indicador de cumplimiento
│   │   │   │   │   ├── reconocimientos/     # logros del usuario final
│   │   │   │   │   ├── perfil/              # cuenta, avatar y contraseña
│   │   │   │   │   ├── cobranza/            # gestión de compromisos de la empresa
│   │   │   │   │   ├── reportes/            # reportes de problemas de cobro
│   │   │   │   │   └── mensajeria/          # conversación entre agente y ejecutivo
│   │   │   │   └── MainActivity.kt          # punto de entrada de la aplicación
│   │   │   ├── res/                        # recursos Android
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                           # pruebas unitarias
│   │   └── androidTest/                    # pruebas instrumentadas
│   └── build.gradle.kts
├── gradle/
│   ├── libs.versions.toml                  # catálogo de versiones y dependencias
│   └── wrapper/                            # Gradle Wrapper
├── build.gradle.kts                        # configuración Gradle raíz
├── settings.gradle.kts
├── gradlew
└── gradlew.bat
```

La aplicación utiliza una arquitectura MVVM organizada por funcionalidades. Cada feature separa el acceso a datos (`data`) de la presentación (`presentation`), con repositorios, ViewModels y estados de interfaz reactivos mediante Coroutines y StateFlow. Los componentes visuales, la navegación y la infraestructura de red se comparten desde `core`.

## Estado de las funcionalidades

| Funcionalidad | Estado | Alcance |
|---|---|---|
| Autenticación | Implementado | Inicio de sesión, registro por perfil, recuperación de contraseña y gestión de sesión JWT con renovación de tokens. |
| Inicio y compromisos | Implementado | Resumen mensual, próximos vencimientos, compromisos vigentes y consulta de montos, estados y recargos. |
| Pagos | Implementado | Cotización y pago simulado, selección de medio de pago, confirmación y cancelación del flujo; validación de orden y estado mediante el backend. |
| Historial y comprobantes | Implementado | Consulta de pagos y anulaciones, filtros por empresa y año, y detalle de comprobantes. |
| Cumplimiento | Implementado | Indicador de cumplimiento histórico, categoría e historial insuficiente. |
| Reconocimientos | Implementado | Consulta de logros calculados por el backend a partir del historial de pagos. |
| Perfil | Implementado | Consulta y edición de datos personales, gestión de avatar y cambio de contraseña. |
| Reportes | Implementado | Creación y seguimiento de reportes por el usuario final; asignación, atención y resolución por el agente. |
| Cobranza | Implementado | Listado, detalle, registro y anulación de compromisos de la empresa, filtros y resumen mensual de cobranza para el ejecutivo. |
| Mensajería | Implementado | Conversación por reporte entre agente y ejecutivo, historial paginado, envío de mensajes, lecturas y actualizaciones mediante SSE. |

La navegación distingue tres perfiles: usuario final, agente PagoFlex y ejecutivo de empresa cliente. Los permisos y las operaciones de negocio se validan en el backend. **Los pagos son simulados y no se solicitan datos bancarios reales.**

El proyecto conserva componentes de Room 3 y SQLite del prototipo local; los flujos integrados consultan el backend mediante Retrofit y OkHttp. La base local no sustituye al servidor como fuente de los datos de negocio.

## Requisitos previos

- Android Studio compatible con Android Gradle Plugin 9.4.1.
- Android SDK 37 y Android SDK Platform-Tools (`adb`).
- JDK 25 para el daemon de Gradle.
- Emulador o dispositivo Android 7.0 (API 24) o superior.
- [PagoFlex - Backend](https://github.com/Bruno0416/pagoflex-backend) en ejecución, con su entorno y servicios configurados.

El proyecto incluye Gradle Wrapper 9.6.0, Kotlin 2.4.21 y Compose BOM 2026.09.00. Las versiones de las dependencias se administran desde `gradle/libs.versions.toml`. El SDK mínimo es 24 y el SDK objetivo es 37.

## Conectar el backend

La aplicación consume la API del gateway de PagoFlex. Primero, sigue las instrucciones del [repositorio del backend](https://github.com/Bruno0416/pagoflex-backend) para preparar MySQL y levantar sus servicios.

Comprueba que el gateway responde desde el equipo de desarrollo:

```bash
curl http://localhost:8080/actuator/health
```

La URL base configurada en `app/src/main/kotlin/com/pagoflex/app/core/network/RetrofitClient.kt` es:

```text
http://127.0.0.1:8080/api/v1/
```

Si hay varios dispositivos conectados, selecciona el correspondiente con `adb -s <serial> reverse tcp:8080 tcp:8080`. Repite la redirección si se pierde al reiniciar o desconectar el dispositivo.

El uso de HTTP sin cifrar está limitado a las direcciones de desarrollo permitidas en `network_security_config.xml`. Esta configuración está destinada al entorno local, no a un despliegue productivo.

## Ejecutar la aplicación

Abre el proyecto raíz en Android Studio, sincroniza Gradle, selecciona un emulador o dispositivo y ejecuta la configuración `app`.

También es posible compilar e instalar la aplicación desde la terminal:

```bash
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

La APK de depuración se genera en `app/build/outputs/apk/debug/app-debug.apk`.

Para ejecutar las órdenes de Gradle en Windows, utiliza `gradlew.bat` en lugar de `./gradlew`.

## Tests

Para compilar la aplicación, ejecutar las pruebas unitarias y revisar Android Lint:

```bash
./gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
```

Para ejecutar únicamente las pruebas unitarias:

```bash
./gradlew :app:testDebugUnitTest
```

Para ejecutar las pruebas instrumentadas, con un emulador o dispositivo conectado:

```bash
./gradlew :app:connectedDebugAndroidTest
```

Los informes de pruebas unitarias y Android Lint se generan en `app/build/reports/tests/testDebugUnitTest/index.html` y `app/build/reports/lint-results-debug.html`, respectivamente.

Si una compilación falla al generar archivos DEX intermedios, detén las compilaciones activas del IDE y prueba una compilación limpia aislada:

```bash
./gradlew --stop
./gradlew clean --no-configuration-cache
./gradlew :app:assembleDebug --no-build-cache --no-configuration-cache --no-watch-fs --no-parallel --max-workers=1 --rerun-tasks
```

Estos parámetros solo afectan a la ejecución indicada y no modifican la configuración del proyecto.
