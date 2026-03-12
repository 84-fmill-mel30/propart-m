package com.propart.diagnostic.activities;

import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import com.propart.diagnostic.bluetooth.ELM327Manager;
import com.propart.diagnostic.obd.OBD2Command;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Activity para visualización de datos OBD2 en vivo
 * Soporta 2-3 gráficas simultáneas con sensores intercambiables
 */
public class DatosVivosActivity extends AppCompatActivity {
    
    // UI Components
    private TextView tvConnectionStatus;
    private Button btnConnect, btnDisconnect;
    private LinearLayout layoutGraphs;
    
    // Gráficas
    private LineChart chart1, chart2, chart3;
    private Spinner spinnerSensor1, spinnerSensor2, spinnerSensor3;
    
    // TextViews para valores actuales
    private TextView tvValue1, tvValue2, tvValue3;
    
    // Bluetooth Manager
    private ELM327Manager elm327Manager;
    private boolean isConnected = false;
    private boolean isReading = false;
    
    // Sensores seleccionados
    private OBD2Command sensor1, sensor2, sensor3;
    
    // Datos de gráficas (últimos 60 puntos = ~30 segundos)
    private LinkedList<Entry> dataChart1 = new LinkedList<>();
    private LinkedList<Entry> dataChart2 = new LinkedList<>();
    private LinkedList<Entry> dataChart3 = new LinkedList<>();
    private int dataPointCounter = 0;
    private static final int MAX_DATA_POINTS = 60;
    
    // Handler para actualización periódica
    private Handler updateHandler = new Handler();
    private Runnable updateRunnable;
    private static final int UPDATE_INTERVAL = 500; // 500ms = 2Hz
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_vivos);
        
        initViews();
        initELM327Manager();
        setupSpinners();
        setupCharts();
        setupButtons();
    }
    
    private void initViews() {
        tvConnectionStatus = findViewById(R.id.tvConnectionStatus);
        btnConnect = findViewById(R.id.btnConnect);
        btnDisconnect = findViewById(R.id.btnDisconnect);
        layoutGraphs = findViewById(R.id.layoutGraphs);
        
        // Gráficas
        chart1 = findViewById(R.id.chart1);
        chart2 = findViewById(R.id.chart2);
        chart3 = findViewById(R.id.chart3);
        
        // Spinners de sensores
        spinnerSensor1 = findViewById(R.id.spinnerSensor1);
        spinnerSensor2 = findViewById(R.id.spinnerSensor2);
        spinnerSensor3 = findViewById(R.id.spinnerSensor3);
        
        // Valores actuales
        tvValue1 = findViewById(R.id.tvValue1);
        tvValue2 = findViewById(R.id.tvValue2);
        tvValue3 = findViewById(R.id.tvValue3);
    }
    
    private void initELM327Manager() {
        elm327Manager = new ELM327Manager();
        
        elm327Manager.setConnectionListener(new ELM327Manager.ConnectionListener() {
            @Override
            public void onConnecting() {
                runOnUiThread(() -> {
                    tvConnectionStatus.setText("Conectando...");
                    tvConnectionStatus.setTextColor(Color.YELLOW);
                });
            }
            
            @Override
            public void onConnected() {
                runOnUiThread(() -> {
                    isConnected = true;
                    tvConnectionStatus.setText("✓ CONECTADO");
                    tvConnectionStatus.setTextColor(Color.GREEN);
                    btnConnect.setEnabled(false);
                    btnDisconnect.setEnabled(true);
                    layoutGraphs.setVisibility(View.VISIBLE);
                    startDataReading();
                });
            }
            
            @Override
            public void onDisconnected() {
                runOnUiThread(() -> {
                    isConnected = false;
                    tvConnectionStatus.setText("DESCONECTADO");
                    tvConnectionStatus.setTextColor(Color.RED);
                    btnConnect.setEnabled(true);
                    btnDisconnect.setEnabled(false);
                    layoutGraphs.setVisibility(View.GONE);
                    stopDataReading();
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(DatosVivosActivity.this, error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void setupSpinners() {
        OBD2Command[] commands = OBD2Command.getGraphCommands();
        List<String> sensorNames = new ArrayList<>();
        
        for (OBD2Command cmd : commands) {
            sensorNames.add(cmd.getDescription());
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this, 
            android.R.layout.simple_spinner_item, 
            sensorNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        // Configurar spinners
        spinnerSensor1.setAdapter(adapter);
        spinnerSensor2.setAdapter(adapter);
        spinnerSensor3.setAdapter(adapter);
        
        // Selección predeterminada
        spinnerSensor1.setSelection(0); // RPM
        spinnerSensor2.setSelection(2); // Temp. Refrigerante
        spinnerSensor3.setSelection(3); // TPS
        
        // Listeners
        spinnerSensor1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sensor1 = commands[position];
                dataChart1.clear();
                dataPointCounter = 0;
                setupChart(chart1, sensor1, Color.rgb(255, 87, 34)); // Naranja
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        spinnerSensor2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sensor2 = commands[position];
                dataChart2.clear();
                dataPointCounter = 0;
                setupChart(chart2, sensor2, Color.rgb(33, 150, 243)); // Azul
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        spinnerSensor3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sensor3 = commands[position];
                dataChart3.clear();
                dataPointCounter = 0;
                setupChart(chart3, sensor3, Color.rgb(76, 175, 80)); // Verde
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        // Inicializar sensores
        sensor1 = commands[0];
        sensor2 = commands[2];
        sensor3 = commands[3];
    }
    
    private void setupCharts() {
        setupChart(chart1, sensor1, Color.rgb(255, 87, 34));
        setupChart(chart2, sensor2, Color.rgb(33, 150, 243));
        setupChart(chart3, sensor3, Color.rgb(76, 175, 80));
    }
    
    private void setupChart(LineChart chart, OBD2Command sensor, int color) {
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.setDrawGridBackground(false);
        chart.setBackgroundColor(Color.rgb(30, 30, 30));
        
        // Configurar eje X
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(6);
        
        // Configurar eje Y izquierdo
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.DKGRAY);
        leftAxis.setAxisMinimum((float) sensor.getMinValue());
        leftAxis.setAxisMaximum((float) sensor.getMaxValue());
        
        // Desactivar eje Y derecho
        chart.getAxisRight().setEnabled(false);
        
        // Configurar leyenda
        Legend legend = chart.getLegend();
        legend.setEnabled(true);
        legend.setTextColor(Color.WHITE);
        legend.setTextSize(12f);
        
        // Inicializar datos vacíos
        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);
        chart.setData(data);
    }
    
    private void setupButtons() {
        btnConnect.setOnClickListener(v -> connectToELM327());
        btnDisconnect.setOnClickListener(v -> disconnectFromELM327());
        
        btnDisconnect.setEnabled(false);
    }
    
    private void connectToELM327() {
        List<BluetoothDevice> devices = elm327Manager.findELM327Devices();
        
        if (devices.isEmpty()) {
            Toast.makeText(this, "No se encontró dispositivo ELM327. Verifica emparejamiento.", Toast.LENGTH_LONG).show();
            return;
        }
        
        // Conectar al primer dispositivo encontrado
        BluetoothDevice device = devices.get(0);
        Toast.makeText(this, "Conectando a " + device.getName(), Toast.LENGTH_SHORT).show();
        elm327Manager.connect(device);
    }
    
    private void disconnectFromELM327() {
        elm327Manager.disconnect();
    }
    
    private void startDataReading() {
        isReading = true;
        
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                if (isReading && isConnected) {
                    readSensorData();
                    updateHandler.postDelayed(this, UPDATE_INTERVAL);
                }
            }
        };
        
        updateHandler.post(updateRunnable);
    }
    
    private void stopDataReading() {
        isReading = false;
        if (updateRunnable != null) {
            updateHandler.removeCallbacks(updateRunnable);
        }
    }
    
    private void readSensorData() {
        new Thread(() -> {
            try {
                // Leer sensor 1
                if (sensor1 != null) {
                    String response1 = elm327Manager.sendCommandWithResponse(sensor1.getCommand());
                    if (response1 != null && !response1.contains("ERROR")) {
                        double value1 = sensor1.calculateValue(response1);
                        updateChart(chart1, dataChart1, value1, sensor1, tvValue1, Color.rgb(255, 87, 34));
                    }
                }
                
                // Leer sensor 2
                if (sensor2 != null) {
                    String response2 = elm327Manager.sendCommandWithResponse(sensor2.getCommand());
                    if (response2 != null && !response2.contains("ERROR")) {
                        double value2 = sensor2.calculateValue(response2);
                        updateChart(chart2, dataChart2, value2, sensor2, tvValue2, Color.rgb(33, 150, 243));
                    }
                }
                
                // Leer sensor 3
                if (sensor3 != null) {
                    String response3 = elm327Manager.sendCommandWithResponse(sensor3.getCommand());
                    if (response3 != null && !response3.contains("ERROR")) {
                        double value3 = sensor3.calculateValue(response3);
                        updateChart(chart3, dataChart3, value3, sensor3, tvValue3, Color.rgb(76, 175, 80));
                    }
                }
                
                dataPointCounter++;
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private void updateChart(LineChart chart, LinkedList<Entry> dataList, double value, 
                            OBD2Command sensor, TextView valueText, int color) {
        runOnUiThread(() -> {
            // Actualizar valor en pantalla
            String valueStr = String.format(Locale.getDefault(), "%.1f %s", value, sensor.getUnit());
            valueText.setText(valueStr);
            
            // Agregar nuevo punto
            dataList.add(new Entry(dataPointCounter, (float) value));
            
            // Limitar cantidad de puntos
            if (dataList.size() > MAX_DATA_POINTS) {
                dataList.removeFirst();
                
                // Reajustar índices X
                for (int i = 0; i < dataList.size(); i++) {
                    dataList.get(i).setX(dataPointCounter - MAX_DATA_POINTS + i);
                }
            }
            
            // Actualizar gráfica
            LineDataSet dataSet = new LineDataSet(new ArrayList<>(dataList), sensor.getDescription());
            dataSet.setColor(color);
            dataSet.setLineWidth(2f);
            dataSet.setDrawCircles(false);
            dataSet.setDrawValues(false);
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            dataSet.setCubicIntensity(0.2f);
            
            LineData lineData = new LineData(dataSet);
            chart.setData(lineData);
            chart.notifyDataSetChanged();
            chart.invalidate();
            
            // Ajustar viewport
            chart.setVisibleXRangeMaximum(MAX_DATA_POINTS);
            chart.moveViewToX(dataPointCounter);
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopDataReading();
        if (elm327Manager != null && elm327Manager.isConnected()) {
            elm327Manager.disconnect();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        stopDataReading();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (isConnected) {
            startDataReading();
        }
    }
}
