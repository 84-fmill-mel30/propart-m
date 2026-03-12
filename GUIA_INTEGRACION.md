# 🚗 GUÍA DE INTEGRACIÓN - SISTEMA OBD2 COMPLETO
## ProPart System - Conexión Real ELM327 vLink BT 4.0

---

## 📋 TABLA DE CONTENIDOS

1. [Resumen del Sistema](#resumen)
2. [Archivos Incluidos](#archivos)
3. [Instalación Paso a Paso](#instalacion)
4. [Configuración del Proyecto](#configuracion)
5. [Integración con tu App](#integracion)
6. [Botones de Administrador](#botones-admin)
7. [Base de Datos Técnica](#base-datos)
8. [Pruebas y Depuración](#pruebas)
9. [Solución de Problemas](#problemas)

---

## 📦 RESUMEN DEL SISTEMA <a name="resumen"></a>

Este paquete incluye TODO lo necesario para conectar tu app con un dispositivo **ELM327 vLink Bluetooth 4.0** (chino) y obtener **datos reales del vehículo**:

✅ **Conexión Bluetooth ELM327 real** - Compatible con chips chinos
✅ **Lectura de DTCs reales** - Códigos de falla del vehículo
✅ **Datos en vivo** - RPM, temperatura, TPS, MAP, etc.
✅ **Gráficas dinámicas** - 2-3 sensores simultáneos intercambiables
✅ **Base de datos técnica** - Diagramas, módulos, procedimientos
✅ **Sistema bidireccional** - Leer y escribir datos

---

## 📁 ARCHIVOS INCLUIDOS <a name="archivos"></a>

```
📦 Paquete OBD2 ProPart System
├── 📄 ELM327Manager.java          - Gestor de conexión Bluetooth
├── 📄 OBD2Command.java             - Todos los comandos OBD2 PIDs
├── 📄 DatosVivosActivity.java     - Activity de datos en vivo
├── 📄 TechnicalDatabaseHelper.java - Base de datos técnica
├── 📄 BluetoothPermissionHelper.java - Permisos Bluetooth
├── 📄 activity_datos_vivos.xml    - Layout de datos en vivo
├── 📄 build.gradle                - Dependencias
├── 📄 AndroidManifest.xml         - Permisos
└── 📄 GUIA_INTEGRACION.md         - Este archivo
```

---

## 🔧 INSTALACIÓN PASO A PASO <a name="instalacion"></a>

### PASO 1: Agregar Archivos Java

Copia los archivos `.java` a tu proyecto en las siguientes rutas:

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

**⚠️ IMPORTANTE:** Ajusta el **package name** en cada archivo:
- Busca: `package com.propart.diagnostic`
- Reemplaza con el package de tu app (ejemplo: `com.tuempresa.tuapp`)

### PASO 2: Agregar Layout XML

Copia el archivo `activity_datos_vivos.xml` a:

```
app/src/main/res/layout/activity_datos_vivos.xml
```

### PASO 3: Configurar build.gradle

#### A) Agregar JitPack (settings.gradle o build.gradle del proyecto):

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }  // ← AGREGAR ESTA LÍNEA
    }
}
```

#### B) Agregar dependencias (build.gradle del módulo app):

```gradle
dependencies {
    // ... tus dependencias existentes ...
    
    // MPAndroidChart para gráficas
    implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'
    
    // CardView (si no la tienes)
    implementation 'androidx.cardview:cardview:1.0.0'
}
```

#### C) Sincronizar proyecto:

```
Build → Clean Project
Build → Rebuild Project
```

### PASO 4: Configurar AndroidManifest.xml

Agrega estos permisos en tu `AndroidManifest.xml`:

```xml
<!-- Bluetooth clásico -->
<uses-permission android:name="android.permission.BLUETOOTH" 
    android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" 
    android:maxSdkVersion="30" />

<!-- Bluetooth Android 12+ -->
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" 
    android:usesPermissionFlags="neverForLocation" />

<!-- Localización (necesario para Bluetooth) -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

<!-- Feature Bluetooth -->
<uses-feature 
    android:name="android.hardware.bluetooth" 
    android:required="true" />
```

Y registra la Activity:

```xml
<activity
    android:name=".activities.DatosVivosActivity"
    android:exported="false"
    android:screenOrientation="portrait"
    android:label="Datos en Vivo OBD2" />
```

---

## ⚙️ CONFIGURACIÓN DEL PROYECTO <a name="configuracion"></a>

### Java Version

Asegúrate de tener Java 8+ en tu `build.gradle`:

```gradle
android {
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
```

### R8/ProGuard (si usas minify)

Si tienes `minifyEnabled true`, agrega estas reglas en `proguard-rules.pro`:

```proguard
# MPAndroidChart
-keep class com.github.mikephil.charting.** { *; }

# Bluetooth
-keep class android.bluetooth.** { *; }

# Tu app
-keep class com.propart.diagnostic.** { *; }
```

---

## 🔌 INTEGRACIÓN CON TU APP <a name="integracion"></a>

### Opción 1: Botón "SCANNER OBD2" → Datos en Vivo

En tu actividad donde está el botón SCANNER:

```java
Button btnScanner = findViewById(R.id.btnScanner);
btnScanner.setOnClickListener(v -> {
    Intent intent = new Intent(this, DatosVivosActivity.class);
    startActivity(intent);
});
```

### Opción 2: Agregar Tab "DATOS VIVOS" en Scanner

Modifica tu `ScannerActivity.java` para agregar el tab:

```java
// En tu TabLayout
TabLayout tabLayout = findViewById(R.id.tabLayout);
tabLayout.addTab(tabLayout.newTab().setText("DIAGNÓSTICO"));
tabLayout.addTab(tabLayout.newTab().setText("DATOS VIVOS"));  // ← NUEVO
tabLayout.addTab(tabLayout.newTab().setText("BIDIRECCIONAL"));

// En el listener
tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                // Mostrar diagnóstico
                break;
            case 1:
                // NUEVO: Mostrar datos en vivo
                Intent intent = new Intent(ScannerActivity.this, DatosVivosActivity.class);
                startActivity(intent);
                break;
            case 2:
                // Bidireccional
                break;
        }
    }
    
    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}
    
    @Override
    public void onTabReselected(TabLayout.Tab tab) {}
});
```

### Opción 3: Conectar Automáticamente al Abrir

Si quieres conectar automáticamente cuando se carga el Scanner:

```java
public class DatosVivosActivity extends AppCompatActivity {
    
    private ELM327Manager elm327Manager;
    private BluetoothPermissionHelper permissionHelper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ... código existente ...
        
        // Auto-conectar
        permissionHelper = new BluetoothPermissionHelper(this);
        permissionHelper.requestBluetoothPermissions(new BluetoothPermissionHelper.PermissionCallback() {
            @Override
            public void onPermissionsGranted() {
                connectToELM327();
            }
            
            @Override
            public void onPermissionsDenied(List<String> deniedPermissions) {
                Toast.makeText(DatosVivosActivity.this, 
                    "Permisos Bluetooth necesarios", Toast.LENGTH_LONG).show();
            }
        });
    }
}
```

---

## 🔧 BOTONES DE ADMINISTRADOR <a name="botones-admin"></a>

### Agregar Teléfonos de Contacto

En tu `AdministradorActivity.java`:

```java
public class AdministradorActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador);
        
        // Botón de soporte técnico 1
        Button btnSoporte1 = findViewById(R.id.btnSoporte1);
        btnSoporte1.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:7776838196"));
            startActivity(intent);
        });
        
        // Botón de soporte técnico 2
        Button btnSoporte2 = findViewById(R.id.btnSoporte2);
        btnSoporte2.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:2217565392"));
            startActivity(intent);
        });
    }
}
```

### Layout XML para Administrador (ejemplo):

```xml
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="16dp">
    
    <Button
        android:id="@+id/btnSoporte1"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="📞 Soporte: 777-683-8196"
        android:textSize="16sp"
        android:padding="12dp"/>
    
    <Button
        android:id="@+id/btnSoporte2"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="📞 Soporte: 221-756-5392"
        android:textSize="16sp"
        android:padding="12dp"
        android:layout_marginTop="8dp"/>
</LinearLayout>
```

---

## 💾 BASE DE DATOS TÉCNICA <a name="base-datos"></a>

### Usar la Base de Datos

```java
// Inicializar
TechnicalDatabaseHelper dbHelper = new TechnicalDatabaseHelper(context);

// Agregar módulo
long moduleId = dbHelper.addModule(
    "ECU Motor",           // nombre
    "ECU",                 // tipo
    "Nissan",             // marca
    "Versa",              // modelo
    "2020",               // año
    "Módulo de control del motor", // descripción
    "/path/imagen.jpg"    // ruta imagen
);

// Agregar diagrama
dbHelper.addDiagram(
    (int) moduleId,       // ID del módulo
    "Diagrama Eléctrico", // título
    "Eléctrico",          // tipo
    "/path/diagrama.jpg", // imagen
    "/path/diagrama.pdf", // PDF
    "Diagrama completo del sistema eléctrico" // notas
);

// Buscar módulos
List<TechnicalDatabaseHelper.Module> modules = dbHelper.searchModules("Nissan");

// Obtener diagramas de un módulo
List<TechnicalDatabaseHelper.Diagram> diagrams = dbHelper.getDiagramsByModule((int) moduleId);

// Agregar información de DTC
dbHelper.addDTCInfo(
    "P0300",                                    // código
    "Falla de encendido aleatoria",            // descripción
    "Bujías, bobinas, inyectores",             // causas
    "Revisar sistema de encendido",            // soluciones
    "Alto"                                      // severidad
);

// Consultar información de un DTC
TechnicalDatabaseHelper.DTCInfo dtcInfo = dbHelper.getDTCInfo("P0300");
```

### Botón para Agregar Información

En tu layout principal (activity_main.xml):

```xml
<Button
    android:id="@+id/btnBaseDatos"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="📚 BASE DE DATOS TÉCNICA"
    android:background="#9C27B0"
    android:textColor="#FFFFFF" />
```

En tu MainActivity.java:

```java
Button btnBaseDatos = findViewById(R.id.btnBaseDatos);
btnBaseDatos.setOnClickListener(v -> {
    // Abrir activity de base de datos
    Intent intent = new Intent(this, BaseDatosActivity.class);
    startActivity(intent);
});
```

---

## 🧪 PRUEBAS Y DEPURACIÓN <a name="pruebas"></a>

### 1. Verificar Permisos Bluetooth

Antes de conectar:

```java
BluetoothPermissionHelper helper = new BluetoothPermissionHelper(this);
if (!helper.hasBluetoothPermissions()) {
    helper.requestBluetoothPermissions(/* callback */);
} else {
    // OK, conectar
}
```

### 2. Probar Conexión ELM327

```java
ELM327Manager elm327 = new ELM327Manager();

elm327.setConnectionListener(new ELM327Manager.ConnectionListener() {
    @Override
    public void onConnected() {
        Log.d("TEST", "✅ Conectado exitosamente");
    }
    
    @Override
    public void onError(String error) {
        Log.e("TEST", "❌ Error: " + error);
    }
});

List<BluetoothDevice> devices = elm327.findELM327Devices();
if (!devices.isEmpty()) {
    elm327.connect(devices.get(0));
}
```

### 3. Leer Comando Manualmente

```java
// Leer RPM
String response = elm327.sendCommandWithResponse("010C");
Log.d("TEST", "Respuesta RPM: " + response);

// Calcular valor
OBD2Command rpmCommand = OBD2Command.RPM;
double rpm = rpmCommand.calculateValue(response);
Log.d("TEST", "RPM calculado: " + rpm);
```

### 4. Verificar DTCs

```java
List<String> dtcs = elm327.readDTCs();
for (String dtc : dtcs) {
    Log.d("TEST", "DTC encontrado: " + dtc);
    
    // Buscar en base de datos
    TechnicalDatabaseHelper.DTCInfo info = dbHelper.getDTCInfo(dtc);
    if (info != null) {
        Log.d("TEST", "Descripción: " + info.getDescription());
    }
}
```

---

## ❗ SOLUCIÓN DE PROBLEMAS <a name="problemas"></a>

### Problema 1: "No se encuentra dispositivo ELM327"

**Solución:**
1. Verifica que el ELM327 esté emparejado en Configuración → Bluetooth
2. El nombre debe ser algo como: OBDII, OBD2, vLink, ELM327
3. Agrega más nombres a `DEVICE_NAMES` en `ELM327Manager.java`:

```java
private static final String[] DEVICE_NAMES = {
    "OBDII", "OBD2", "ELM327", "vLinker", "vLink", "V-LINK",
    "CHX", "VGATE", "KONNWEI", "VEEPEAK",
    "TU_NOMBRE_AQUI"  // ← Agrega el nombre de tu dispositivo
};
```

### Problema 2: "ERROR" o "NO DATA" en respuestas

**Causas comunes:**
- Vehículo apagado o contacto no puesto
- ELM327 no está conectado al puerto OBD2
- Protocolo incorrecto

**Solución:**
1. Enciende el vehículo (al menos contacto)
2. Verifica conexión física del ELM327
3. En `ELM327Manager.java`, cambia el protocolo:

```java
// En lugar de:
sendCommand("ATSP0");  // Auto

// Prueba con protocolos específicos:
sendCommand("ATSP6");  // CAN 11bit 500kb
sendCommand("ATSP7");  // CAN 29bit 500kb
sendCommand("ATSP8");  // CAN 11bit 250kb
sendCommand("ATSP9");  // CAN 29bit 250kb
```

### Problema 3: Gráficas no se actualizan

**Solución:**
1. Verifica que `isReading` sea `true`
2. Aumenta el intervalo de actualización:

```java
private static final int UPDATE_INTERVAL = 1000; // 1 segundo en lugar de 500ms
```

3. Reduce cantidad de sensores (prueba con solo 1 gráfica primero)

### Problema 4: App se cierra al conectar

**Solución:**
1. Revisa Logcat para ver el error exacto
2. Verifica que tienes todos los permisos en AndroidManifest.xml
3. Confirma que solicitaste permisos en tiempo de ejecución:

```java
permissionHelper.requestBluetoothPermissions(/* callback */);
```

### Problema 5: "SLOW INIT" o "SEARCHING..."

**Solución:**
El ELM327 está buscando el protocolo. Es normal en la primera conexión. Espera 5-10 segundos.

Si persiste:
```java
// En initializeELM327(), después de ATZ:
Thread.sleep(3000); // Aumentar a 3 segundos
```

### Problema 6: Valores incorrectos en sensores

**Solución:**
Algunos vehículos usan fórmulas diferentes. Ajusta en `OBD2Command.java`:

```java
// Ejemplo: Si tu temperatura está mal
COOLANT_TEMP("0105", "Temp. Refrigerante", "°C", "A-40", -40, 215),

// Prueba con:
COOLANT_TEMP("0105", "Temp. Refrigerante", "°C", "A-50", -50, 205),
// o
COOLANT_TEMP("0105", "Temp. Refrigerante", "°C", "A-30", -30, 225),
```

---

## 📞 SOPORTE TÉCNICO

**Fernando Millán - Técnico Certificado PROPART**
- Tel: 777-683-8196
- Tel: 221-756-5392

**Taller:** Fernando Millán - Cuernavaca

---

## ✅ CHECKLIST DE INTEGRACIÓN

- [ ] Copié todos los archivos .java
- [ ] Ajusté los package names
- [ ] Agregué dependencia MPAndroidChart
- [ ] Configuré JitPack en repositories
- [ ] Agregué permisos en AndroidManifest.xml
- [ ] Registré DatosVivosActivity en Manifest
- [ ] Conecté botón Scanner con la Activity
- [ ] Probé solicitud de permisos
- [ ] Emparejé ELM327 en Bluetooth
- [ ] Conecté ELM327 al vehículo
- [ ] Probé conexión y lectura de datos
- [ ] Verifiqué que las gráficas se actualizan
- [ ] Agregué botones de administrador
- [ ] Implementé base de datos técnica

---

## 🎉 ¡LISTO!

Tu app ProPart System ahora tiene:
✅ Conexión real con ELM327
✅ Datos en vivo con gráficas
✅ Lectura de DTCs
✅ Base de datos técnica bidireccional

**Próximos pasos:**
1. Personalizar colores y tema
2. Agregar más sensores según tus necesidades
3. Llenar la base de datos con diagramas
4. Implementar funciones bidireccionales avanzadas

---

**Versión:** 1.0
**Fecha:** Febrero 2026
**Autor:** Sistema ProPart - Diagnóstico Profesional
