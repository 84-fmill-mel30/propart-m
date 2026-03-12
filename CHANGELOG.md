# 📝 Changelog

Todos los cambios notables en este proyecto serán documentados en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/),
y este proyecto adhiere a [Semantic Versioning](https://semver.org/lang/es/).

---

## [1.0.0] - 2026-02-21

### 🎉 Lanzamiento Inicial

Primera versión estable del sistema ProPart OBD2 con todas las características principales implementadas.

### ✨ Agregado

#### Conexión Bluetooth
- Gestor completo de conexión ELM327 (`ELM327Manager.java`)
- Auto-detección de dispositivos Bluetooth emparejados
- Soporte para chips chinos (vLink, OBDII, ELM327, etc.)
- Inicialización automática con comandos AT
- Selección automática de protocolo OBD2
- Manejo de reconexiones automáticas
- Sistema robusto de manejo de errores

#### Lectura de Datos
- Implementación completa de 30+ comandos OBD2 PIDs
- Fórmulas de conversión precisas para cada sensor
- Lectura de:
  - RPM (Revoluciones por minuto)
  - Velocidad del vehículo
  - Temperatura del motor (ECT)
  - Temperatura de admisión (IAT)
  - Temperatura de aceite
  - Posición del acelerador (TPS)
  - Presión del múltiple (MAP)
  - Flujo de aire (MAF)
  - Sensores de oxígeno (O2)
  - Carga del motor
  - Nivel de combustible
  - Correcciones de combustible (Fuel Trim)
  - Voltaje de la batería
  - Y 18 sensores más

#### Visualización
- Activity completa de datos en vivo (`DatosVivosActivity.java`)
- Sistema de 3 gráficas simultáneas con MPAndroidChart
- Sensores intercambiables en tiempo real mediante Spinners
- Actualización automática cada 500ms (2Hz)
- Colores distintivos por sensor:
  - Naranja (#FF5722) para sensor 1
  - Azul (#2196F3) para sensor 2
  - Verde (#4CAF50) para sensor 3
- Zoom y desplazamiento en gráficas
- Historial de 60 puntos de datos (~30 segundos)
- Animaciones suaves y transiciones fluidas
- Indicadores de valor actual en tiempo real

#### Diagnóstico
- Lectura completa de códigos DTC (P, C, B, U)
- Parser inteligente de respuestas hexadecimales
- Conversión automática a códigos legibles
- Función de borrado de códigos
- Detección de códigos permanentes vs. pendientes

#### Base de Datos
- Sistema completo SQLite (`TechnicalDatabaseHelper.java`)
- 5 tablas relacionales:
  1. **modules** - Módulos y componentes del vehículo
  2. **diagrams** - Diagramas técnicos (eléctricos, hidráulicos, mecánicos)
  3. **pins** - Información de pines y conectores
  4. **dtc_info** - Información detallada de códigos DTC
  5. **procedures** - Procedimientos de reparación paso a paso
- CRUD completo para todas las entidades
- Sistema de búsqueda inteligente
- Relaciones foráneas entre tablas
- Datos de ejemplo pre-cargados para DTCs comunes

#### Permisos
- Helper completo para permisos Bluetooth (`BluetoothPermissionHelper.java`)
- Compatible con Android 6.0 - 14+ (API 23-34)
- Manejo diferenciado por versión de Android:
  - Android 6-9: ACCESS_COARSE_LOCATION
  - Android 10-11: ACCESS_FINE_LOCATION
  - Android 12+: BLUETOOTH_CONNECT + BLUETOOTH_SCAN
- Callbacks claros para permisos concedidos/denegados
- Verificación automática de Bluetooth habilitado

#### UI/UX
- Layout profesional con Material Design
- Diseño oscuro optimizado (#1E1E1E, #2A2A2A)
- CardViews con elevation
- Botones con estados (enabled/disabled)
- Indicadores de estado de conexión
- Mensajes de error claros y concisos
- Instrucciones contextuales para el usuario

#### Documentación
- README completo con badges y capturas
- Guía de integración exhaustiva en español
- Ejemplo de Activity con implementación completa
- Comentarios inline en código
- JavaDoc para métodos públicos
- Diagramas de flujo de datos
- Guía de solución de problemas

### 🔒 Seguridad
- Verificación de permisos antes de operaciones Bluetooth
- Validación de respuestas del ELM327
- Manejo seguro de excepciones
- Timeouts configurables
- Limpieza de recursos al cerrar conexiones

### 🐛 Corregido
- N/A (Primera versión)

### 🗑️ Deprecado
- N/A (Primera versión)

### ⚠️ Removido
- N/A (Primera versión)

### 📊 Estadísticas de la Versión

- **Archivos Java:** 6
- **Layouts XML:** 1
- **Líneas de código:** ~2,500
- **Sensores soportados:** 30+
- **Protocolos OBD2:** 5 (ISO9141, ISO14230, ISO15765, SAE J1850)
- **Versiones Android:** API 21-34 (Android 5.0 - 14+)

---

## [Unreleased] - Próximas Características

### 🚧 En Desarrollo

#### Versión 1.1.0
- [ ] Grabación de sesiones de diagnóstico
- [ ] Exportación de datos a CSV/Excel
- [ ] Modo oscuro/claro configurable
- [ ] Alertas personalizables por sensor
- [ ] Comparación de valores entre sesiones
- [ ] Historial de lecturas con timestamps
- [ ] Dashboard personalizable
- [ ] Widgets de pantalla principal

#### Versión 2.0.0
- [ ] Funciones bidireccionales avanzadas
- [ ] Control de actuadores (EVAP, EGR, ventiladores)
- [ ] Test de componentes
- [ ] Calibración de sensores
- [ ] Sincronización en la nube
- [ ] Modo multi-vehículo
- [ ] Perfiles de usuario
- [ ] Integración con API REST

### 💡 Características Propuestas
- Integración con servicios de nube (Firebase)
- Modo offline completo
- Compatibilidad con tablets
- Soporte para OBD1 (adaptadores especiales)
- Integración con CAN Bus avanzado
- Análisis predictivo con IA
- Reconocimiento de patrones de fallas
- Sugerencias automáticas de mantenimiento

---

## Convenciones de Versionado

Este proyecto usa [Semantic Versioning](https://semver.org/):

- **MAJOR** (X.0.0): Cambios incompatibles con versiones anteriores
- **MINOR** (1.X.0): Nuevas características compatibles
- **PATCH** (1.0.X): Correcciones de bugs compatibles

---

## Tipos de Cambios

- **Agregado**: Nuevas características
- **Cambiado**: Cambios en funcionalidad existente
- **Deprecado**: Características que se eliminarán pronto
- **Removido**: Características eliminadas
- **Corregido**: Corrección de bugs
- **Seguridad**: Vulnerabilidades corregidas

---

## Enlaces

- [Repositorio GitHub](https://github.com/tu-usuario/propart-obd2-system)
- [Issues](https://github.com/tu-usuario/propart-obd2-system/issues)
- [Pull Requests](https://github.com/tu-usuario/propart-obd2-system/pulls)
- [Releases](https://github.com/tu-usuario/propart-obd2-system/releases)

---

**Última actualización:** 21 de Febrero de 2026  
**Mantenido por:** Equipo ProPart System
