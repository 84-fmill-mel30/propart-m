package com.propart.diagnostic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.propart.diagnostic.activities.DatosVivosActivity;
import com.propart.diagnostic.database.TechnicalDatabaseHelper;
import com.propart.diagnostic.utils.BluetoothPermissionHelper;

import java.util.List;

/**
 * EJEMPLO DE INTEGRACIÓN COMPLETA
 * MainActivity con botones para todas las funcionalidades
 */
public class MainActivityExample extends AppCompatActivity {
    
    private BluetoothPermissionHelper permissionHelper;
    private TechnicalDatabaseHelper dbHelper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Inicializar helpers
        permissionHelper = new BluetoothPermissionHelper(this);
        dbHelper = new TechnicalDatabaseHelper(this);
        
        setupButtons();
    }
    
    private void setupButtons() {
        
        // ==================== BOTÓN SCANNER OBD2 ====================
        Button btnScanner = findViewById(R.id.btnScanner);
        btnScanner.setOnClickListener(v -> {
            // Solicitar permisos y abrir datos en vivo
            permissionHelper.requestBluetoothPermissions(new BluetoothPermissionHelper.PermissionCallback() {
                @Override
                public void onPermissionsGranted() {
                    // Permisos OK, abrir datos en vivo
                    Intent intent = new Intent(MainActivityExample.this, DatosVivosActivity.class);
                    startActivity(intent);
                }
                
                @Override
                public void onPermissionsDenied(List<String> deniedPermissions) {
                    Toast.makeText(MainActivityExample.this, 
                        "Permisos Bluetooth necesarios para conectar con ELM327", 
                        Toast.LENGTH_LONG).show();
                }
            });
        });
        
        // ==================== BOTÓN NUEVO REPORTE ====================
        Button btnNuevoReporte = findViewById(R.id.btnNuevoReporte);
        btnNuevoReporte.setOnClickListener(v -> {
            // Tu código existente para nuevo reporte
            // Intent intent = new Intent(this, ReporteActivity.class);
            // startActivity(intent);
        });
        
        // ==================== BOTÓN ADMINISTRADOR ====================
        Button btnAdministrador = findViewById(R.id.btnAdministrador);
        btnAdministrador.setOnClickListener(v -> {
            // Tu código existente
            // Intent intent = new Intent(this, AdministradorActivity.class);
            // startActivity(intent);
        });
        
        // ==================== BOTÓN BASE DE DATOS TÉCNICA ====================
        Button btnBaseDatos = findViewById(R.id.btnBaseDatos);
        btnBaseDatos.setOnClickListener(v -> {
            // Mostrar opciones de base de datos
            showDatabaseOptions();
        });
        
        // ==================== BOTONES DE SOPORTE ====================
        Button btnSoporte1 = findViewById(R.id.btnSoporte1);
        btnSoporte1.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:7776838196"));
            startActivity(intent);
        });
        
        Button btnSoporte2 = findViewById(R.id.btnSoporte2);
        btnSoporte2.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:2217565392"));
            startActivity(intent);
        });
    }
    
    /**
     * Muestra opciones de base de datos técnica
     */
    private void showDatabaseOptions() {
        androidx.appcompat.app.AlertDialog.Builder builder = 
            new androidx.appcompat.app.AlertDialog.Builder(this);
        
        builder.setTitle("Base de Datos Técnica");
        builder.setItems(new String[]{
            "📚 Ver Módulos",
            "📊 Ver Diagramas",
            "🔍 Buscar DTC",
            "➕ Agregar Módulo",
            "➕ Agregar Diagrama"
        }, (dialog, which) -> {
            switch (which) {
                case 0:
                    showModules();
                    break;
                case 1:
                    showDiagrams();
                    break;
                case 2:
                    searchDTC();
                    break;
                case 3:
                    addModule();
                    break;
                case 4:
                    addDiagram();
                    break;
            }
        });
        
        builder.show();
    }
    
    /**
     * Muestra lista de módulos
     */
    private void showModules() {
        List<TechnicalDatabaseHelper.Module> modules = dbHelper.getAllModules();
        
        if (modules.isEmpty()) {
            Toast.makeText(this, "No hay módulos en la base de datos", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Convertir a array de strings
        String[] moduleNames = new String[modules.size()];
        for (int i = 0; i < modules.size(); i++) {
            TechnicalDatabaseHelper.Module module = modules.get(i);
            moduleNames[i] = module.getBrand() + " " + module.getModel() + 
                            " (" + module.getYear() + ") - " + module.getName();
        }
        
        androidx.appcompat.app.AlertDialog.Builder builder = 
            new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Módulos Disponibles");
        builder.setItems(moduleNames, (dialog, which) -> {
            TechnicalDatabaseHelper.Module selectedModule = modules.get(which);
            showModuleDetails(selectedModule);
        });
        builder.setNegativeButton("Cerrar", null);
        builder.show();
    }
    
    /**
     * Muestra detalles de un módulo
     */
    private void showModuleDetails(TechnicalDatabaseHelper.Module module) {
        String details = "Tipo: " + module.getType() + "\n" +
                        "Marca: " + module.getBrand() + "\n" +
                        "Modelo: " + module.getModel() + "\n" +
                        "Año: " + module.getYear() + "\n\n" +
                        "Descripción:\n" + module.getDescription();
        
        androidx.appcompat.app.AlertDialog.Builder builder = 
            new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(module.getName());
        builder.setMessage(details);
        builder.setPositiveButton("Ver Diagramas", (dialog, which) -> {
            showModuleDiagrams(module.getId());
        });
        builder.setNegativeButton("Cerrar", null);
        builder.show();
    }
    
    /**
     * Muestra diagramas de un módulo
     */
    private void showModuleDiagrams(int moduleId) {
        List<TechnicalDatabaseHelper.Diagram> diagrams = dbHelper.getDiagramsByModule(moduleId);
        
        if (diagrams.isEmpty()) {
            Toast.makeText(this, "No hay diagramas para este módulo", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String[] diagramNames = new String[diagrams.size()];
        for (int i = 0; i < diagrams.size(); i++) {
            diagramNames[i] = diagrams.get(i).getTitle() + " - " + diagrams.get(i).getType();
        }
        
        androidx.appcompat.app.AlertDialog.Builder builder = 
            new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Diagramas");
        builder.setItems(diagramNames, (dialog, which) -> {
            TechnicalDatabaseHelper.Diagram selectedDiagram = diagrams.get(which);
            showDiagramDetails(selectedDiagram);
        });
        builder.setNegativeButton("Cerrar", null);
        builder.show();
    }
    
    /**
     * Muestra detalles de un diagrama
     */
    private void showDiagramDetails(TechnicalDatabaseHelper.Diagram diagram) {
        String details = "Tipo: " + diagram.getType() + "\n\n" +
                        "Notas:\n" + diagram.getNotes() + "\n\n" +
                        "Imagen: " + (diagram.getImagePath() != null ? "✓" : "✗") + "\n" +
                        "PDF: " + (diagram.getPdfPath() != null ? "✓" : "✗");
        
        androidx.appcompat.app.AlertDialog.Builder builder = 
            new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(diagram.getTitle());
        builder.setMessage(details);
        builder.setPositiveButton("Abrir", (dialog, which) -> {
            // Aquí implementarías la apertura del diagrama
            Toast.makeText(this, "Abriendo diagrama...", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Cerrar", null);
        builder.show();
    }
    
    /**
     * Muestra todos los diagramas disponibles
     */
    private void showDiagrams() {
        // Obtener todos los módulos y sus diagramas
        List<TechnicalDatabaseHelper.Module> modules = dbHelper.getAllModules();
        
        if (modules.isEmpty()) {
            Toast.makeText(this, "No hay módulos con diagramas", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Por simplicidad, mostrar módulos y luego sus diagramas
        showModules();
    }
    
    /**
     * Busca información de un código DTC
     */
    private void searchDTC() {
        android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("Ej: P0300");
        
        androidx.appcompat.app.AlertDialog.Builder builder = 
            new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Buscar Código DTC");
        builder.setView(input);
        
        builder.setPositiveButton("Buscar", (dialog, which) -> {
            String dtcCode = input.getText().toString().trim().toUpperCase();
            
            if (dtcCode.isEmpty()) {
                Toast.makeText(this, "Ingresa un código DTC", Toast.LENGTH_SHORT).show();
                return;
            }
            
            TechnicalDatabaseHelper.DTCInfo info = dbHelper.getDTCInfo(dtcCode);
            
            if (info != null) {
                showDTCInfo(info);
            } else {
                Toast.makeText(this, "Código DTC no encontrado: " + dtcCode, Toast.LENGTH_LONG).show();
            }
        });
        
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }
    
    /**
     * Muestra información de un DTC
     */
    private void showDTCInfo(TechnicalDatabaseHelper.DTCInfo info) {
        String message = "Código: " + info.getCode() + "\n\n" +
                        "Descripción:\n" + info.getDescription() + "\n\n" +
                        "Causas Posibles:\n" + info.getCauses() + "\n\n" +
                        "Soluciones:\n" + info.getSolutions() + "\n\n" +
                        "Severidad: " + info.getSeverity();
        
        androidx.appcompat.app.AlertDialog.Builder builder = 
            new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Información DTC");
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.show();
    }
    
    /**
     * Formulario para agregar módulo
     */
    private void addModule() {
        // Crear un formulario simple
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);
        
        android.widget.EditText inputName = new android.widget.EditText(this);
        inputName.setHint("Nombre del módulo");
        layout.addView(inputName);
        
        android.widget.EditText inputType = new android.widget.EditText(this);
        inputType.setHint("Tipo (ECU, BCM, TCM, etc.)");
        layout.addView(inputType);
        
        android.widget.EditText inputBrand = new android.widget.EditText(this);
        inputBrand.setHint("Marca");
        layout.addView(inputBrand);
        
        android.widget.EditText inputModel = new android.widget.EditText(this);
        inputModel.setHint("Modelo");
        layout.addView(inputModel);
        
        android.widget.EditText inputYear = new android.widget.EditText(this);
        inputYear.setHint("Año");
        layout.addView(inputYear);
        
        android.widget.EditText inputDescription = new android.widget.EditText(this);
        inputDescription.setHint("Descripción");
        inputDescription.setMinLines(3);
        layout.addView(inputDescription);
        
        androidx.appcompat.app.AlertDialog.Builder builder = 
            new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Agregar Módulo");
        builder.setView(layout);
        
        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String name = inputName.getText().toString();
            String type = inputType.getText().toString();
            String brand = inputBrand.getText().toString();
            String model = inputModel.getText().toString();
            String year = inputYear.getText().toString();
            String description = inputDescription.getText().toString();
            
            if (name.isEmpty() || brand.isEmpty() || model.isEmpty()) {
                Toast.makeText(this, "Completa los campos obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }
            
            long id = dbHelper.addModule(name, type, brand, model, year, description, null);
            
            if (id > 0) {
                Toast.makeText(this, "Módulo agregado exitosamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al agregar módulo", Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }
    
    /**
     * Formulario para agregar diagrama
     */
    private void addDiagram() {
        // Primero seleccionar el módulo
        List<TechnicalDatabaseHelper.Module> modules = dbHelper.getAllModules();
        
        if (modules.isEmpty()) {
            Toast.makeText(this, "Primero agrega un módulo", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String[] moduleNames = new String[modules.size()];
        for (int i = 0; i < modules.size(); i++) {
            moduleNames[i] = modules.get(i).getName() + " - " + 
                            modules.get(i).getBrand() + " " + modules.get(i).getModel();
        }
        
        androidx.appcompat.app.AlertDialog.Builder builder = 
            new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Selecciona Módulo");
        builder.setItems(moduleNames, (dialog, which) -> {
            showAddDiagramForm(modules.get(which).getId());
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }
    
    private void showAddDiagramForm(int moduleId) {
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);
        
        android.widget.EditText inputTitle = new android.widget.EditText(this);
        inputTitle.setHint("Título del diagrama");
        layout.addView(inputTitle);
        
        android.widget.EditText inputType = new android.widget.EditText(this);
        inputType.setHint("Tipo (Eléctrico, Hidráulico, etc.)");
        layout.addView(inputType);
        
        android.widget.EditText inputNotes = new android.widget.EditText(this);
        inputNotes.setHint("Notas");
        inputNotes.setMinLines(3);
        layout.addView(inputNotes);
        
        androidx.appcompat.app.AlertDialog.Builder builder = 
            new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Agregar Diagrama");
        builder.setView(layout);
        
        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String title = inputTitle.getText().toString();
            String type = inputType.getText().toString();
            String notes = inputNotes.getText().toString();
            
            if (title.isEmpty()) {
                Toast.makeText(this, "Ingresa un título", Toast.LENGTH_SHORT).show();
                return;
            }
            
            long id = dbHelper.addDiagram(moduleId, title, type, null, null, notes);
            
            if (id > 0) {
                Toast.makeText(this, "Diagrama agregado exitosamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al agregar diagrama", Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }
    
    // ==================== MANEJO DE PERMISOS ====================
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.handlePermissionsResult(requestCode, permissions, grantResults);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        permissionHelper.handleEnableBluetoothResult(requestCode, resultCode);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
