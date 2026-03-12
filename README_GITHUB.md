# 🚗 ProPart OBD2 System - Conexión ELM327 Real

Sistema completo de diagnóstico automotriz OBD2 con conexión Bluetooth ELM327 real para Android.

![Android](https://img.shields.io/badge/Android-5.0%2B-green)
![Java](https://img.shields.io/badge/Java-8%2B-orange)
![License](https://img.shields.io/badge/License-ProPart-blue)

---

## 📋 Descripción

Sistema profesional de diagnóstico OBD2 que incluye:

- ✅ Conexión Bluetooth con dispositivos ELM327 (vLink BT 4.0)
- ✅ Lectura de datos en vivo del vehículo
- ✅ Gráficas en tiempo real (2-3 sensores simultáneos)
- ✅ Lectura de códigos DTC (Diagnostic Trouble Codes)
- ✅ Base de datos técnica con diagramas y procedimientos
- ✅ Más de 30 sensores OBD2 disponibles

---

## 🎯 Características Principales

### 🔌 Conexión ELM327
- Auto-detección de dispositivos emparejados
- Compatible con chips chinos (vLink, OBDII, ELM327)
- Inicialización automática con comandos AT
- Selección automática de protocolo OBD2
- Manejo robusto de errores

### 📊 Datos en Vivo
- **RPM** - Revoluciones por minuto
- **Velocidad** - km/h
- **Temperatura** - Motor, admisión, aceite
- **TPS** - Posición del acelerador
- **MAP** - Presión del múltiple
- **MAF** - Flujo de aire
- **Sensores O2** - Lambda
- Y muchos más...

### 📈 Visualización
- 3 gráficas simultáneas con MPAndroidChart
- Sensores intercambiables en tiempo real
- Actualización cada 500ms (2Hz)
- Zoom y desplazamiento
- Colores distintivos por sensor

### 💾 Base de Datos Técnica
- Almacenamiento de módulos y componentes
- Diagramas eléctricos (imagen + PDF)
- Información detallada de códigos DTC
- Procedimientos de reparación
- Sistema de búsqueda inteligente

---

## 🚀 Instalación

### Requisitos Previos
- Android Studio Arctic Fox o superior
- SDK mínimo: API 21 (Android 5.0)
- SDK objetivo: API 34 (Android 14)
- Java 8+
- Dispositivo ELM327 Bluetooth

### Pasos de Instalación

1. **Clonar el repositorio:**
```bash
git clone https://github.com/tuusuario/propart-obd2-system.git
cd propart-obd2-system
```

2. **Copiar archivos a tu proyecto Android:**

Estructura de carpetas:
```
app/src/main/java/com/propart/diagnostic/
├── bluetooth/
│   └── ELM327Manager.java
├── obd/
│   └── OBD2Command.java
├── activities/
│   └── DatosVivosActivity.java
├── database/
│   └── TechnicalDatabaseHelper.java
└── utils/
    └── BluetoothPermissionHelper.java
```

3. **Agregar dependencias en build.gradle:**
```gradle
dependencies {
    implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'
    implementation 'androidx.cardview:cardview:1.0.0'
}
```

4. **Configurar repositorios en settings.gradle:**
```gradle
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }  // Para MPAndroidChart
    }
}
```

5. **Agregar permisos en AndroidManifest.xml:**
```xml
<!-- Ver AndroidManifest.xml incluido para permisos completos -->
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
<!-- ... más permisos -->
```

6. **Sincronizar proyecto:**
```
Build → Clean Project
Build → Rebuild Project
```

---

## 📖 Uso Básico

### Conectar con ELM327

```java
// 1. Inicializar el manager
ELM327Manager elm327Manager = new ELM327Manager();

// 2. Configurar listeners
elm327Manager.setConnectionListener(new ELM327Manager.ConnectionListener() {
    @Override
    public void onConnected() {
        Log.d("OBD2", "Conectado exitosamente");
    }
    
    @Override
    public void onError(String error) {
        Log.e("OBD2", "Error: " + error);
    }
    
    @Override
    public void onDisconnected() {
        Log.d("OBD2", "Desconectado");
    }
});

// 3. Buscar dispositivos ELM327
List<BluetoothDevice> devices = elm327Manager.findELM327Devices();

// 4. Conectar al primer dispositivo encontrado
if (!devices.isEmpty()) {
    elm327Manager.connect(devices.get(0));
}
```

### Leer Datos del Vehículo

```java
// Leer RPM
String response = elm327Manager.sendCommandWithResponse(OBD2Command.RPM.getCommand());
double rpm = OBD2Command.RPM.calculateValue(response);
Log.d("OBD2", "RPM: " + rpm);

// Leer temperatura del motor
response = elm327Manager.sendCommandWithResponse(OBD2Command.COOLANT_TEMP.getCommand());
double temp = OBD2Command.COOLANT_TEMP.calculateValue(response);
Log.d("OBD2", "Temperatura: " + temp + "°C");
```

### Leer Códigos DTC

```java
List<String> dtcs = elm327Manager.readDTCs();
for (String dtc : dtcs) {
    Log.d("OBD2", "Código DTC: " + dtc);
    
    // Buscar información del DTC en la base de datos
    TechnicalDatabaseHelper dbHelper = new TechnicalDatabaseHelper(context);
    TechnicalDatabaseHelper.DTCInfo info = dbHelper.getDTCInfo(dtc);
    
    if (info != null) {
        Log.d("OBD2", "Descripción: " + info.getDescription());
        Log.d("OBD2", "Causas: " + info.getCauses());
    }
}
```

### Integrar Activity de Datos en Vivo

```java
// En tu MainActivity o donde tengas el botón Scanner
Button btnScanner = findViewById(R.id.btnScanner);
btnScanner.setOnClickListener(v -> {
    Intent intent = new Intent(this, DatosVivosActivity.class);
    startActivity(intent);
});
```

---

## 📂 Estructura del Proyecto

```
propart-obd2-system/
├── ELM327Manager.java              # Gestor de conexión Bluetooth
├── OBD2Command.java                # Comandos PIDs y fórmulas
├── DatosVivosActivity.java         # Activity con gráficas
├── TechnicalDatabaseHelper.java    # Base de datos SQLite
├── BluetoothPermissionHelper.java  # Manejo de permisos
├── MainActivityExample.java        # Ejemplo de integración
├── activity_datos_vivos.xml        # Layout de UI
├── AndroidManifest.xml             # Permisos y configuración
├── build.gradle                    # Dependencias
├── GUIA_INTEGRACION.md            # Guía detallada en español
├── README.md                       # Este archivo
└── .gitignore                      # Archivos ignorados
```

---

## 🔧 Configuración Avanzada

### Cambiar Intervalo de Actualización

En `DatosVivosActivity.java`:
```java
private static final int UPDATE_INTERVAL = 500; // Cambiar a 1000 para 1 segundo
```

### Agregar Más Sensores

En `OBD2Command.java`:
```java
NEW_SENSOR("01XX", "Nombre Sensor", "unidad", "formula", min, max);
```

### Configurar Protocolos Específicos

En `ELM327Manager.java`, método `initializeELM327()`:
```java
// Protocolo automático (recomendado)
sendCommand("ATSP0");

// O seleccionar protocolo específico:
sendCommand("ATSP6");  // CAN 11bit 500kb
sendCommand("ATSP7");  // CAN 29bit 500kb
```

---

## 🐛 Solución de Problemas

### Problema: No se encuentra el dispositivo ELM327

**Solución:**
1. Verificar que el dispositivo está emparejado en Bluetooth
2. Asegurarse que el nombre contiene: OBDII, OBD2, ELM327, vLink
3. Agregar más nombres en `ELM327Manager.DEVICE_NAMES`

### Problema: ERROR o NO DATA

**Causas comunes:**
- Vehículo apagado
- ELM327 no conectado al puerto OBD2
- Protocolo incorrecto

**Solución:**
1. Encender el vehículo (mínimo contacto)
2. Verificar conexión física
3. Probar diferentes protocolos (ATSP6, ATSP7, ATSP8, ATSP9)

### Problema: App se cierra al conectar

**Solución:**
1. Verificar permisos en AndroidManifest.xml
2. Solicitar permisos en tiempo de ejecución
3. Revisar Logcat para ver el error específico

Ver **GUIA_INTEGRACION.md** para más soluciones.

---

## 🤝 Contribuir

Este proyecto es parte del sistema **ProPart Diagnostic System**. Para contribuciones o soporte:

**Fernando Millán - Técnico Certificado PROPART**
- Tel: 777-683-8196
- Tel: 221-756-5392
- Taller: Cuernavaca, México

---

## 📄 Licencia

Copyright © 2026 ProPart System
Todos los derechos reservados.

Este código es propiedad de **ProPart System** y está diseñado específicamente para aplicaciones de diagnóstico automotriz profesional.

---

## 📚 Documentación

- **GUIA_INTEGRACION.md** - Guía completa en español
- **README.md** - Este archivo
- Comentarios en código - Documentación inline

---

## 🔗 Enlaces Útiles

- [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) - Librería de gráficas
- [ELM327 Commands](https://www.elmelectronics.com/wp-content/uploads/2016/07/ELM327DS.pdf) - Documentación oficial
- [OBD2 PIDs](https://en.wikipedia.org/wiki/OBD-II_PIDs) - Lista de comandos OBD2

---

## 📊 Estado del Proyecto

- ✅ Conexión Bluetooth - **COMPLETO**
- ✅ Lectura de datos en vivo - **COMPLETO**
- ✅ Gráficas múltiples - **COMPLETO**
- ✅ Lectura de DTCs - **COMPLETO**
- ✅ Base de datos técnica - **COMPLETO**
- ⏳ Funciones bidireccionales - **EN DESARROLLO**
- ⏳ Exportar datos - **PLANEADO**

---

## ⭐ Características

Si este proyecto te fue útil, considera:
- Dar una estrella ⭐ al repositorio
- Compartir con otros desarrolladores
- Reportar bugs o sugerir mejoras

---

**Versión:** 1.0.0  
**Última actualización:** Febrero 2026  
**Compatible con:** ELM327 vLink BT 4.0 y compatibles
