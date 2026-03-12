# 🚗 PAQUETE COMPLETO OBD2 - PROPART SYSTEM

## 📦 Contenido del Paquete

Este paquete incluye **TODO** lo necesario para implementar conexión real ELM327 con datos en vivo y gráficas en tu app Android ProPart System.

### ✅ LO QUE OBTIENES:

1. **Conexión Bluetooth ELM327 Real**
   - Compatible con vLink BT 4.0 (chips chinos)
   - Auto-detección de dispositivos
   - Inicialización automática del chip
   - Manejo de errores robusto

2. **Lectura de Datos Reales del Vehículo**
   - DTCs (códigos de falla)
   - RPM, velocidad, temperatura
   - TPS, MAP, MAF, O2
   - 30+ sensores disponibles

3. **Gráficas en Tiempo Real**
   - 2-3 gráficas simultáneas
   - Sensores intercambiables
   - Actualización cada 500ms
   - Visualización profesional con MPAndroidChart

4. **Base de Datos Técnica Bidireccional**
   - Almacenamiento de módulos
   - Diagramas eléctricos
   - Información de DTCs
   - Procedimientos de reparación

---

## 📋 ARCHIVOS INCLUIDOS

### Código Java (8 archivos):
1. **ELM327Manager.java** - Gestor de conexión Bluetooth
2. **OBD2Command.java** - Comandos PIDs y fórmulas de conversión
3. **DatosVivosActivity.java** - Activity principal con gráficas
4. **TechnicalDatabaseHelper.java** - Base de datos SQLite
5. **BluetoothPermissionHelper.java** - Manejo de permisos
6. **MainActivityExample.java** - Ejemplo de integración completa

### Recursos Android (3 archivos):
7. **activity_datos_vivos.xml** - Layout de datos en vivo
8. **AndroidManifest.xml** - Permisos necesarios
9. **build.gradle** - Dependencias del proyecto

### Documentación (2 archivos):
10. **GUIA_INTEGRACION.md** - Guía completa paso a paso
11. **README.md** - Este archivo

---

## 🚀 INICIO RÁPIDO (5 PASOS)

### 1️⃣ Copia los archivos Java
```
app/src/main/java/com/propart/diagnostic/
├── bluetooth/ELM327Manager.java
├── obd/OBD2Command.java
├── activities/DatosVivosActivity.java
├── database/TechnicalDatabaseHelper.java
└── utils/BluetoothPermissionHelper.java
```

### 2️⃣ Copia el layout
```
app/src/main/res/layout/activity_datos_vivos.xml
```

### 3️⃣ Agrega dependencias
En tu `build.gradle`:
```gradle
implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'
```

Y en `settings.gradle`:
```gradle
maven { url 'https://jitpack.io' }
```

### 4️⃣ Agrega permisos
Copia los permisos de `AndroidManifest.xml` incluido

### 5️⃣ Conecta con tu botón Scanner
```java
Button btnScanner = findViewById(R.id.btnScanner);
btnScanner.setOnClickListener(v -> {
    Intent intent = new Intent(this, DatosVivosActivity.class);
    startActivity(intent);
});
```

---

## 🎯 CARACTERÍSTICAS PRINCIPALES

### ✨ Conexión ELM327
- ✅ Auto-detección de dispositivos emparejados
- ✅ Soporta nombres: OBDII, OBD2, vLink, ELM327
- ✅ Inicialización automática con comandos AT
- ✅ Selección automática de protocolo
- ✅ Reconexión automática

### 📊 Datos en Vivo
- ✅ 30+ sensores disponibles (RPM, Temp, TPS, MAP, MAF, etc.)
- ✅ Lectura cada 500ms (2Hz)
- ✅ Valores en unidades reales (rpm, °C, %, kPa, etc.)
- ✅ Fórmulas de conversión incluidas

### 📈 Gráficas
- ✅ 2-3 gráficas simultáneas
- ✅ Sensores intercambiables en tiempo real
- ✅ Colores distintivos
- ✅ Zoom y pan
- ✅ Últimos 60 puntos (~30 segundos)

### 💾 Base de Datos
- ✅ SQLite con 5 tablas
- ✅ Módulos y componentes
- ✅ Diagramas (imagen + PDF)
- ✅ Información de DTCs
- ✅ Procedimientos de reparación
- ✅ Búsqueda inteligente

---

## 📱 REQUISITOS

- **Android Studio:** Arctic Fox o superior
- **minSdk:** 21 (Android 5.0)
- **targetSdk:** 34 (Android 14)
- **Java:** 8+
- **Bluetooth:** Clásico (SPP)

### Hardware:
- Dispositivo ELM327 vLink Bluetooth 4.0
- Vehículo con puerto OBD2 (1996+)

---

## 🔧 SOPORTE TÉCNICO

### Botones de Administrador Incluidos:
```java
Tel: 777-683-8196
Tel: 221-756-5392
```

### Taller:
**Fernando Millán - Técnico Certificado PROPART**
Cuernavaca, México

---

## 📖 DOCUMENTACIÓN COMPLETA

Lee el archivo **GUIA_INTEGRACION.md** para:
- Instalación detallada paso a paso
- Ejemplos de código
- Solución de problemas comunes
- Configuración avanzada
- Tips y mejores prácticas

---

## 🎓 EJEMPLO DE USO

```java
// 1. Crear manager
ELM327Manager elm327 = new ELM327Manager();

// 2. Configurar listener
elm327.setConnectionListener(new ELM327Manager.ConnectionListener() {
    @Override
    public void onConnected() {
        // ¡Conectado!
    }
});

// 3. Buscar dispositivos
List<BluetoothDevice> devices = elm327.findELM327Devices();

// 4. Conectar
elm327.connect(devices.get(0));

// 5. Leer datos
String response = elm327.sendCommandWithResponse("010C"); // RPM
double rpm = OBD2Command.RPM.calculateValue(response);
```

---

## ⚡ CARACTERÍSTICAS AVANZADAS

### Comandos Bidireccionales
- Control de EVAP
- Control de EGR
- Ajuste de ralentí
- Purga de canister

### Protocolos Soportados
- ISO 9141-2
- ISO 14230-4 (KWP2000)
- ISO 15765-4 (CAN)
- SAE J1850 PWM
- SAE J1850 VPW

### Funciones Especiales
- Freeze Frame
- Monitores de emisiones
- Información del vehículo (VIN)
- Verificación de catalizador
- Presión EVAP

---

## 🏆 VENTAJAS DE ESTE SISTEMA

1. **Plug & Play** - Solo copiar archivos e integrar
2. **Código Limpio** - Bien documentado y organizado
3. **Robusto** - Manejo de errores completo
4. **Profesional** - UI moderna y funcional
5. **Completo** - Incluye TODO lo necesario
6. **Actualizable** - Fácil de extender y personalizar

---

## 📝 NOTAS IMPORTANTES

⚠️ **Emparejar ELM327 primero**: Antes de usar la app, empareja el dispositivo ELM327 en Configuración → Bluetooth

⚠️ **Vehículo encendido**: El vehículo debe estar con contacto encendido o motor en marcha

⚠️ **Permisos**: La app solicitará permisos Bluetooth automáticamente

⚠️ **Protocolo**: Si hay errores, el sistema detecta el protocolo automáticamente

---

## 🔄 ACTUALIZACIONES FUTURAS

Próximas características planeadas:
- [ ] Grabación de sesiones
- [ ] Exportar datos a CSV
- [ ] Comparación de valores
- [ ] Alertas personalizables
- [ ] Modo oscuro/claro
- [ ] Más sensores y PIDs

---

## 📞 CONTACTO

**ProPart System**
Sistema de Diagnóstico Automotriz Profesional

Tel: 777-683-8196
Tel: 221-756-5392

Taller Fernando Millán
Cuernavaca, México

---

## 📄 LICENCIA

Este código es para uso de **ProPart System** y está diseñado específicamente para la app de diagnóstico automotriz.

**Versión:** 1.0  
**Fecha:** Febrero 2026  
**Compatible con:** ELM327 vLink BT 4.0 (chips chinos)

---

## ✅ CHECKLIST DE INSTALACIÓN

- [ ] Descargué todos los archivos
- [ ] Copié archivos Java a las carpetas correctas
- [ ] Ajusté los package names
- [ ] Agregué dependencia MPAndroidChart
- [ ] Configuré JitPack
- [ ] Agregué permisos en Manifest
- [ ] Registré DatosVivosActivity
- [ ] Conecté botón Scanner
- [ ] Emparejé ELM327
- [ ] Probé la conexión

---

**¡Listo para usar!** 🎉

Lee la **GUIA_INTEGRACION.md** para instrucciones detalladas.
