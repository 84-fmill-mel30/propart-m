package com.propart.diagnostic.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Base de datos técnica bidireccional para diagramas, módulos y información técnica
 * Permite agregar información personalizada sobre vehículos
 */
public class TechnicalDatabaseHelper extends SQLiteOpenHelper {
    
    private static final String DATABASE_NAME = "technical_database.db";
    private static final int DATABASE_VERSION = 1;
    
    // Tabla de módulos/componentes
    private static final String TABLE_MODULES = "modules";
    private static final String COLUMN_MODULE_ID = "id";
    private static final String COLUMN_MODULE_NAME = "name";
    private static final String COLUMN_MODULE_TYPE = "type"; // ECU, BCM, TCM, ABS, etc.
    private static final String COLUMN_MODULE_BRAND = "brand";
    private static final String COLUMN_MODULE_MODEL = "model";
    private static final String COLUMN_MODULE_YEAR = "year";
    private static final String COLUMN_MODULE_DESCRIPTION = "description";
    private static final String COLUMN_MODULE_IMAGE_PATH = "image_path";
    private static final String COLUMN_MODULE_CREATED = "created_at";
    
    // Tabla de diagramas
    private static final String TABLE_DIAGRAMS = "diagrams";
    private static final String COLUMN_DIAGRAM_ID = "id";
    private static final String COLUMN_DIAGRAM_MODULE_ID = "module_id";
    private static final String COLUMN_DIAGRAM_TITLE = "title";
    private static final String COLUMN_DIAGRAM_TYPE = "type"; // Eléctrico, Hidráulico, Mecánico
    private static final String COLUMN_DIAGRAM_IMAGE_PATH = "image_path";
    private static final String COLUMN_DIAGRAM_PDF_PATH = "pdf_path";
    private static final String COLUMN_DIAGRAM_NOTES = "notes";
    
    // Tabla de pines/conectores
    private static final String TABLE_PINS = "pins";
    private static final String COLUMN_PIN_ID = "id";
    private static final String COLUMN_PIN_MODULE_ID = "module_id";
    private static final String COLUMN_PIN_NUMBER = "pin_number";
    private static final String COLUMN_PIN_NAME = "pin_name";
    private static final String COLUMN_PIN_COLOR = "wire_color";
    private static final String COLUMN_PIN_FUNCTION = "function";
    private static final String COLUMN_PIN_VOLTAGE = "voltage";
    private static final String COLUMN_PIN_SIGNAL_TYPE = "signal_type"; // Digital, Analógico, PWM, etc.
    
    // Tabla de códigos DTC personalizados
    private static final String TABLE_DTC_INFO = "dtc_info";
    private static final String COLUMN_DTC_CODE = "dtc_code";
    private static final String COLUMN_DTC_DESCRIPTION = "description";
    private static final String COLUMN_DTC_CAUSES = "possible_causes";
    private static final String COLUMN_DTC_SOLUTIONS = "solutions";
    private static final String COLUMN_DTC_SEVERITY = "severity"; // Bajo, Medio, Alto, Crítico
    
    // Tabla de procedimientos
    private static final String TABLE_PROCEDURES = "procedures";
    private static final String COLUMN_PROC_ID = "id";
    private static final String COLUMN_PROC_MODULE_ID = "module_id";
    private static final String COLUMN_PROC_TITLE = "title";
    private static final String COLUMN_PROC_STEPS = "steps"; // JSON con pasos
    private static final String COLUMN_PROC_TOOLS_NEEDED = "tools_needed";
    private static final String COLUMN_PROC_TIME_ESTIMATED = "time_estimated";
    
    public TechnicalDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear tabla de módulos
        String createModulesTable = "CREATE TABLE " + TABLE_MODULES + " (" +
            COLUMN_MODULE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_MODULE_NAME + " TEXT NOT NULL, " +
            COLUMN_MODULE_TYPE + " TEXT, " +
            COLUMN_MODULE_BRAND + " TEXT, " +
            COLUMN_MODULE_MODEL + " TEXT, " +
            COLUMN_MODULE_YEAR + " TEXT, " +
            COLUMN_MODULE_DESCRIPTION + " TEXT, " +
            COLUMN_MODULE_IMAGE_PATH + " TEXT, " +
            COLUMN_MODULE_CREATED + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ")";
        db.execSQL(createModulesTable);
        
        // Crear tabla de diagramas
        String createDiagramsTable = "CREATE TABLE " + TABLE_DIAGRAMS + " (" +
            COLUMN_DIAGRAM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_DIAGRAM_MODULE_ID + " INTEGER, " +
            COLUMN_DIAGRAM_TITLE + " TEXT NOT NULL, " +
            COLUMN_DIAGRAM_TYPE + " TEXT, " +
            COLUMN_DIAGRAM_IMAGE_PATH + " TEXT, " +
            COLUMN_DIAGRAM_PDF_PATH + " TEXT, " +
            COLUMN_DIAGRAM_NOTES + " TEXT, " +
            "FOREIGN KEY(" + COLUMN_DIAGRAM_MODULE_ID + ") REFERENCES " + 
            TABLE_MODULES + "(" + COLUMN_MODULE_ID + ")" +
            ")";
        db.execSQL(createDiagramsTable);
        
        // Crear tabla de pines
        String createPinsTable = "CREATE TABLE " + TABLE_PINS + " (" +
            COLUMN_PIN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_PIN_MODULE_ID + " INTEGER, " +
            COLUMN_PIN_NUMBER + " TEXT NOT NULL, " +
            COLUMN_PIN_NAME + " TEXT, " +
            COLUMN_PIN_COLOR + " TEXT, " +
            COLUMN_PIN_FUNCTION + " TEXT, " +
            COLUMN_PIN_VOLTAGE + " TEXT, " +
            COLUMN_PIN_SIGNAL_TYPE + " TEXT, " +
            "FOREIGN KEY(" + COLUMN_PIN_MODULE_ID + ") REFERENCES " + 
            TABLE_MODULES + "(" + COLUMN_MODULE_ID + ")" +
            ")";
        db.execSQL(createPinsTable);
        
        // Crear tabla de DTCs
        String createDTCTable = "CREATE TABLE " + TABLE_DTC_INFO + " (" +
            COLUMN_DTC_CODE + " TEXT PRIMARY KEY, " +
            COLUMN_DTC_DESCRIPTION + " TEXT, " +
            COLUMN_DTC_CAUSES + " TEXT, " +
            COLUMN_DTC_SOLUTIONS + " TEXT, " +
            COLUMN_DTC_SEVERITY + " TEXT" +
            ")";
        db.execSQL(createDTCTable);
        
        // Crear tabla de procedimientos
        String createProceduresTable = "CREATE TABLE " + TABLE_PROCEDURES + " (" +
            COLUMN_PROC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_PROC_MODULE_ID + " INTEGER, " +
            COLUMN_PROC_TITLE + " TEXT NOT NULL, " +
            COLUMN_PROC_STEPS + " TEXT, " +
            COLUMN_PROC_TOOLS_NEEDED + " TEXT, " +
            COLUMN_PROC_TIME_ESTIMATED + " TEXT, " +
            "FOREIGN KEY(" + COLUMN_PROC_MODULE_ID + ") REFERENCES " + 
            TABLE_MODULES + "(" + COLUMN_MODULE_ID + ")" +
            ")";
        db.execSQL(createProceduresTable);
        
        // Insertar datos de ejemplo
        insertSampleData(db);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MODULES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DIAGRAMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PINS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DTC_INFO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROCEDURES);
        onCreate(db);
    }
    
    // ==================== MÓDULOS ====================
    
    public long addModule(String name, String type, String brand, String model, 
                          String year, String description, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_MODULE_NAME, name);
        values.put(COLUMN_MODULE_TYPE, type);
        values.put(COLUMN_MODULE_BRAND, brand);
        values.put(COLUMN_MODULE_MODEL, model);
        values.put(COLUMN_MODULE_YEAR, year);
        values.put(COLUMN_MODULE_DESCRIPTION, description);
        values.put(COLUMN_MODULE_IMAGE_PATH, imagePath);
        
        long id = db.insert(TABLE_MODULES, null, values);
        db.close();
        return id;
    }
    
    public List<Module> getAllModules() {
        List<Module> modules = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_MODULES, null, null, null, null, null, 
                                 COLUMN_MODULE_NAME + " ASC");
        
        if (cursor.moveToFirst()) {
            do {
                Module module = new Module();
                module.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MODULE_ID)));
                module.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MODULE_NAME)));
                module.setType(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MODULE_TYPE)));
                module.setBrand(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MODULE_BRAND)));
                module.setModel(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MODULE_MODEL)));
                module.setYear(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MODULE_YEAR)));
                module.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MODULE_DESCRIPTION)));
                module.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MODULE_IMAGE_PATH)));
                
                modules.add(module);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return modules;
    }
    
    public List<Module> searchModules(String query) {
        List<Module> modules = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selection = COLUMN_MODULE_NAME + " LIKE ? OR " + 
                          COLUMN_MODULE_BRAND + " LIKE ? OR " + 
                          COLUMN_MODULE_MODEL + " LIKE ?";
        String[] selectionArgs = {"%" + query + "%", "%" + query + "%", "%" + query + "%"};
        
        Cursor cursor = db.query(TABLE_MODULES, null, selection, selectionArgs, 
                                null, null, COLUMN_MODULE_NAME + " ASC");
        
        if (cursor.moveToFirst()) {
            do {
                Module module = new Module();
                module.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MODULE_ID)));
                module.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MODULE_NAME)));
                module.setType(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MODULE_TYPE)));
                module.setBrand(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MODULE_BRAND)));
                module.setModel(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MODULE_MODEL)));
                
                modules.add(module);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return modules;
    }
    
    // ==================== DIAGRAMAS ====================
    
    public long addDiagram(int moduleId, String title, String type, 
                          String imagePath, String pdfPath, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_DIAGRAM_MODULE_ID, moduleId);
        values.put(COLUMN_DIAGRAM_TITLE, title);
        values.put(COLUMN_DIAGRAM_TYPE, type);
        values.put(COLUMN_DIAGRAM_IMAGE_PATH, imagePath);
        values.put(COLUMN_DIAGRAM_PDF_PATH, pdfPath);
        values.put(COLUMN_DIAGRAM_NOTES, notes);
        
        long id = db.insert(TABLE_DIAGRAMS, null, values);
        db.close();
        return id;
    }
    
    public List<Diagram> getDiagramsByModule(int moduleId) {
        List<Diagram> diagrams = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selection = COLUMN_DIAGRAM_MODULE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(moduleId)};
        
        Cursor cursor = db.query(TABLE_DIAGRAMS, null, selection, selectionArgs, 
                                null, null, COLUMN_DIAGRAM_TITLE + " ASC");
        
        if (cursor.moveToFirst()) {
            do {
                Diagram diagram = new Diagram();
                diagram.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DIAGRAM_ID)));
                diagram.setModuleId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DIAGRAM_MODULE_ID)));
                diagram.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIAGRAM_TITLE)));
                diagram.setType(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIAGRAM_TYPE)));
                diagram.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIAGRAM_IMAGE_PATH)));
                diagram.setPdfPath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIAGRAM_PDF_PATH)));
                diagram.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIAGRAM_NOTES)));
                
                diagrams.add(diagram);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        return diagrams;
    }
    
    // ==================== DTCs ====================
    
    public long addDTCInfo(String code, String description, String causes, 
                          String solutions, String severity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(COLUMN_DTC_CODE, code);
        values.put(COLUMN_DTC_DESCRIPTION, description);
        values.put(COLUMN_DTC_CAUSES, causes);
        values.put(COLUMN_DTC_SOLUTIONS, solutions);
        values.put(COLUMN_DTC_SEVERITY, severity);
        
        long result = db.insertWithOnConflict(TABLE_DTC_INFO, null, values, 
                                               SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return result;
    }
    
    public DTCInfo getDTCInfo(String code) {
        SQLiteDatabase db = this.getReadableDatabase();
        DTCInfo dtcInfo = null;
        
        String selection = COLUMN_DTC_CODE + " = ?";
        String[] selectionArgs = {code};
        
        Cursor cursor = db.query(TABLE_DTC_INFO, null, selection, selectionArgs, 
                                null, null, null);
        
        if (cursor.moveToFirst()) {
            dtcInfo = new DTCInfo();
            dtcInfo.setCode(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DTC_CODE)));
            dtcInfo.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DTC_DESCRIPTION)));
            dtcInfo.setCauses(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DTC_CAUSES)));
            dtcInfo.setSolutions(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DTC_SOLUTIONS)));
            dtcInfo.setSeverity(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DTC_SEVERITY)));
        }
        
        cursor.close();
        db.close();
        return dtcInfo;
    }
    
    // ==================== DATOS DE EJEMPLO ====================
    
    private void insertSampleData(SQLiteDatabase db) {
        // DTCs comunes
        ContentValues dtc = new ContentValues();
        
        dtc.put(COLUMN_DTC_CODE, "P0300");
        dtc.put(COLUMN_DTC_DESCRIPTION, "Falla de encendido aleatoria");
        dtc.put(COLUMN_DTC_CAUSES, "Bujías defectuosas, bobinas dañadas, inyectores obstruidos");
        dtc.put(COLUMN_DTC_SOLUTIONS, "Revisar bujías, verificar bobinas, limpiar inyectores");
        dtc.put(COLUMN_DTC_SEVERITY, "Alto");
        db.insert(TABLE_DTC_INFO, null, dtc);
        
        dtc.clear();
        dtc.put(COLUMN_DTC_CODE, "P0171");
        dtc.put(COLUMN_DTC_DESCRIPTION, "Sistema muy pobre (Banco 1)");
        dtc.put(COLUMN_DTC_CAUSES, "Fuga de vacío, sensor MAF sucio, inyectores obstruidos");
        dtc.put(COLUMN_DTC_SOLUTIONS, "Buscar fugas, limpiar MAF, revisar inyectores");
        dtc.put(COLUMN_DTC_SEVERITY, "Medio");
        db.insert(TABLE_DTC_INFO, null, dtc);
        
        dtc.clear();
        dtc.put(COLUMN_DTC_CODE, "P0420");
        dtc.put(COLUMN_DTC_DESCRIPTION, "Eficiencia catalizador bajo umbral");
        dtc.put(COLUMN_DTC_CAUSES, "Catalizador degradado, sensores O2 defectuosos");
        dtc.put(COLUMN_DTC_SOLUTIONS, "Reemplazar catalizador, verificar sensores O2");
        dtc.put(COLUMN_DTC_SEVERITY, "Medio");
        db.insert(TABLE_DTC_INFO, null, dtc);
    }
    
    // ==================== CLASES DE DATOS ====================
    
    public static class Module {
        private int id;
        private String name;
        private String type;
        private String brand;
        private String model;
        private String year;
        private String description;
        private String imagePath;
        
        // Getters y Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getBrand() { return brand; }
        public void setBrand(String brand) { this.brand = brand; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public String getYear() { return year; }
        public void setYear(String year) { this.year = year; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getImagePath() { return imagePath; }
        public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    }
    
    public static class Diagram {
        private int id;
        private int moduleId;
        private String title;
        private String type;
        private String imagePath;
        private String pdfPath;
        private String notes;
        
        // Getters y Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public int getModuleId() { return moduleId; }
        public void setModuleId(int moduleId) { this.moduleId = moduleId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getImagePath() { return imagePath; }
        public void setImagePath(String imagePath) { this.imagePath = imagePath; }
        public String getPdfPath() { return pdfPath; }
        public void setPdfPath(String pdfPath) { this.pdfPath = pdfPath; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }
    
    public static class DTCInfo {
        private String code;
        private String description;
        private String causes;
        private String solutions;
        private String severity;
        
        // Getters y Setters
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getCauses() { return causes; }
        public void setCauses(String causes) { this.causes = causes; }
        public String getSolutions() { return solutions; }
        public void setSolutions(String solutions) { this.solutions = solutions; }
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
    }
}
