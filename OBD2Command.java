package com.propart.diagnostic.obd;

/**
 * Comandos OBD2 PIDs (Parameter IDs) para lectura de sensores
 * Incluye fórmulas de conversión para datos reales
 */
public enum OBD2Command {
    
    // MOTOR
    RPM("010C", "RPM", "RPM", "((A*256)+B)/4", 0, 16383),
    SPEED("010D", "Velocidad", "km/h", "A", 0, 255),
    ENGINE_LOAD("0104", "Carga Motor", "%", "A*100/255", 0, 100),
    THROTTLE("0111", "Acelerador (TPS)", "%", "A*100/255", 0, 100),
    
    // TEMPERATURAS
    COOLANT_TEMP("0105", "Temp. Refrigerante (ECT)", "°C", "A-40", -40, 215),
    INTAKE_TEMP("010F", "Temp. Aire Admisión (IAT)", "°C", "A-40", -40, 215),
    OIL_TEMP("015C", "Temp. Aceite", "°C", "A-40", -40, 210),
    
    // PRESIONES
    FUEL_PRESSURE("010A", "Presión Combustible", "kPa", "A*3", 0, 765),
    INTAKE_PRESSURE("010B", "Presión Admisión (MAP)", "kPa", "A", 0, 255),
    BAROMETRIC_PRESSURE("0133", "Presión Barométrica", "kPa", "A", 0, 255),
    
    // COMBUSTIBLE
    FUEL_LEVEL("012F", "Nivel Combustible", "%", "A*100/255", 0, 100),
    SHORT_FUEL_TRIM_1("0106", "Corrección Comb. Corto B1", "%", "(A-128)*100/128", -100, 99.2),
    LONG_FUEL_TRIM_1("0107", "Corrección Comb. Largo B1", "%", "(A-128)*100/128", -100, 99.2),
    SHORT_FUEL_TRIM_2("0108", "Corrección Comb. Corto B2", "%", "(A-128)*100/128", -100, 99.2),
    LONG_FUEL_TRIM_2("0109", "Corrección Comb. Largo B2", "%", "(A-128)*100/128", -100, 99.2),
    AIR_FUEL_RATIO("0144", "Relación Aire/Combustible", "λ", "((A*256)+B)/32768", 0, 2),
    
    // SENSORES OXÍGENO
    O2_VOLTAGE_B1S1("0114", "Sensor O2 B1S1", "V", "A/200", 0, 1.275),
    O2_VOLTAGE_B1S2("0115", "Sensor O2 B1S2", "V", "A/200", 0, 1.275),
    
    // SISTEMA DE ENCENDIDO
    TIMING_ADVANCE("010E", "Avance Encendido", "°", "(A-128)/2", -64, 63.5),
    
    // VOLTAJE
    CONTROL_MODULE_VOLTAGE("0142", "Voltaje Módulo", "V", "((A*256)+B)/1000", 0, 65.535),
    
    // OTROS
    RUN_TIME("011F", "Tiempo Encendido", "min", "(A*256)+B", 0, 65535),
    DISTANCE_MIL("0121", "Distancia con MIL", "km", "(A*256)+B", 0, 65535),
    WARM_UPS("0130", "Calentamientos", "count", "A", 0, 255),
    DISTANCE_SINCE_CLEAR("0131", "Dist. desde borrado", "km", "(A*256)+B", 0, 65535),
    
    // BIDIRECCIONAL (comandos especiales)
    EVAP_PURGE("011E", "Purga EVAP", "%", "A*100/255", 0, 100),
    COMMANDED_EGR("012C", "EGR Comandada", "%", "A*100/255", 0, 100),
    EGR_ERROR("012D", "Error EGR", "%", "(A-128)*100/128", -100, 99.2),
    
    // CATALIZADOR
    CATALYST_TEMP_B1S1("013C", "Temp. Catalizador B1S1", "°C", "((A*256)+B)/10-40", -40, 6513.5),
    
    // MAF (Medidor flujo aire masivo)
    MAF_FLOW("0110", "Flujo MAF", "g/s", "((A*256)+B)/100", 0, 655.35);
    
    private final String command;
    private final String description;
    private final String unit;
    private final String formula;
    private final double minValue;
    private final double maxValue;
    
    OBD2Command(String command, String description, String unit, String formula, double minValue, double maxValue) {
        this.command = command;
        this.description = description;
        this.unit = unit;
        this.formula = formula;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    
    public String getCommand() {
        return command;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public String getFormula() {
        return formula;
    }
    
    public double getMinValue() {
        return minValue;
    }
    
    public double getMaxValue() {
        return maxValue;
    }
    
    /**
     * Calcula el valor real desde la respuesta hexadecimal
     */
    public double calculateValue(String response) {
        try {
            // Limpiar respuesta
            response = response.replace(" ", "").replace(">", "").replace("\r", "").replace("\n", "");
            
            // Remover eco del comando si existe
            if (response.startsWith(command.replace(" ", ""))) {
                response = response.substring(command.replace(" ", "").length());
            }
            
            // Remover código de respuesta (41 para modo 01)
            if (response.startsWith("41")) {
                response = response.substring(2);
            }
            
            // Remover PID
            String pid = command.substring(2);
            if (response.startsWith(pid)) {
                response = response.substring(pid.length());
            }
            
            if (response.length() < 2) {
                return 0;
            }
            
            // Extraer bytes
            int A = Integer.parseInt(response.substring(0, 2), 16);
            int B = 0;
            if (response.length() >= 4) {
                B = Integer.parseInt(response.substring(2, 4), 16);
            }
            
            // Aplicar fórmula
            double value = applyFormula(A, B);
            
            // Limitar a rango
            if (value < minValue) value = minValue;
            if (value > maxValue) value = maxValue;
            
            return value;
            
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
     * Aplica la fórmula de conversión
     */
    private double applyFormula(int A, int B) {
        String f = formula;
        
        // Reemplazar variables
        f = f.replace("A", String.valueOf(A));
        f = f.replace("B", String.valueOf(B));
        
        try {
            // Evaluador simple de expresiones
            return evaluateExpression(f);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
     * Evaluador simple de expresiones matemáticas
     */
    private double evaluateExpression(String expr) {
        try {
            // Usar ScriptEngine si está disponible
            javax.script.ScriptEngineManager manager = new javax.script.ScriptEngineManager();
            javax.script.ScriptEngine engine = manager.getEngineByName("rhino");
            if (engine != null) {
                Object result = engine.eval(expr);
                return ((Number) result).doubleValue();
            }
        } catch (Exception e) {
            // Fallback: evaluación manual
        }
        
        // Evaluación manual básica
        return evaluateManual(expr);
    }
    
    /**
     * Evaluador manual de expresiones básicas
     */
    private double evaluateManual(String expr) {
        try {
            expr = expr.trim();
            
            // Paréntesis
            while (expr.contains("(")) {
                int start = expr.lastIndexOf('(');
                int end = expr.indexOf(')', start);
                String subExpr = expr.substring(start + 1, end);
                double subResult = evaluateManual(subExpr);
                expr = expr.substring(0, start) + subResult + expr.substring(end + 1);
            }
            
            // División
            if (expr.contains("/")) {
                String[] parts = expr.split("/", 2);
                return evaluateManual(parts[0]) / evaluateManual(parts[1]);
            }
            
            // Multiplicación
            if (expr.contains("*")) {
                String[] parts = expr.split("\\*", 2);
                return evaluateManual(parts[0]) * evaluateManual(parts[1]);
            }
            
            // Suma
            if (expr.contains("+") && !expr.startsWith("+")) {
                String[] parts = expr.split("\\+", 2);
                return evaluateManual(parts[0]) + evaluateManual(parts[1]);
            }
            
            // Resta
            if (expr.contains("-") && !expr.startsWith("-") && expr.lastIndexOf('-') > 0) {
                int lastMinus = expr.lastIndexOf('-');
                String part1 = expr.substring(0, lastMinus);
                String part2 = expr.substring(lastMinus + 1);
                return evaluateManual(part1) - evaluateManual(part2);
            }
            
            // Número simple
            return Double.parseDouble(expr);
            
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Obtiene comando por su descripción
     */
    public static OBD2Command getByDescription(String description) {
        for (OBD2Command cmd : values()) {
            if (cmd.getDescription().equals(description)) {
                return cmd;
            }
        }
        return null;
    }
    
    /**
     * Obtiene comandos para gráficas (más comunes)
     */
    public static OBD2Command[] getGraphCommands() {
        return new OBD2Command[]{
            RPM,
            SPEED,
            COOLANT_TEMP,
            THROTTLE,
            ENGINE_LOAD,
            INTAKE_PRESSURE,
            MAF_FLOW,
            TIMING_ADVANCE,
            FUEL_LEVEL,
            SHORT_FUEL_TRIM_1,
            INTAKE_TEMP,
            O2_VOLTAGE_B1S1
        };
    }
}
