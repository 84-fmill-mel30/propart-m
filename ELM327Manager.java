package com.propart.diagnostic.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Gestor de conexión Bluetooth con ELM327 (vLink compatible)
 * Soporta Bluetooth 4.0 y chips chinos genéricos
 */
public class ELM327Manager {
    private static final String TAG = "ELM327Manager";
    
    // UUID estándar para dispositivos serie Bluetooth (SPP)
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    
    // Nombres comunes de dispositivos ELM327/vLink chinos
    private static final String[] DEVICE_NAMES = {
        "OBDII", "OBD2", "ELM327", "vLinker", "vLink", "V-LINK",
        "CHX", "VGATE", "KONNWEI", "VEEPEAK"
    };
    
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private Thread readThread;
    private boolean isConnected = false;
    private boolean isInitialized = false;
    
    private ConnectionListener connectionListener;
    private DataListener dataListener;
    private Handler mainHandler;
    
    public interface ConnectionListener {
        void onConnecting();
        void onConnected();
        void onDisconnected();
        void onError(String error);
    }
    
    public interface DataListener {
        void onDataReceived(String data);
    }
    
    public ELM327Manager() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mainHandler = new Handler(Looper.getMainLooper());
    }
    
    public void setConnectionListener(ConnectionListener listener) {
        this.connectionListener = listener;
    }
    
    public void setDataListener(DataListener listener) {
        this.dataListener = listener;
    }
    
    /**
     * Busca y retorna dispositivos ELM327 emparejados
     */
    public List<BluetoothDevice> findELM327Devices() {
        List<BluetoothDevice> elm327Devices = new ArrayList<>();
        
        if (bluetoothAdapter == null) {
            Log.e(TAG, "Bluetooth no disponible");
            return elm327Devices;
        }
        
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        
        for (BluetoothDevice device : pairedDevices) {
            String deviceName = device.getName();
            if (deviceName != null) {
                for (String name : DEVICE_NAMES) {
                    if (deviceName.toUpperCase().contains(name.toUpperCase())) {
                        elm327Devices.add(device);
                        Log.d(TAG, "Dispositivo ELM327 encontrado: " + deviceName);
                        break;
                    }
                }
            }
        }
        
        return elm327Devices;
    }
    
    /**
     * Conecta con el dispositivo ELM327
     */
    public void connect(BluetoothDevice device) {
        if (isConnected) {
            Log.w(TAG, "Ya conectado");
            return;
        }
        
        notifyConnecting();
        
        new Thread(() -> {
            try {
                // Crear socket Bluetooth
                bluetoothSocket = device.createRfcommSocketToServiceRecord(SPP_UUID);
                
                // Cancelar descubrimiento para mejorar velocidad
                bluetoothAdapter.cancelDiscovery();
                
                // Conectar
                bluetoothSocket.connect();
                
                // Obtener streams
                outputStream = bluetoothSocket.getOutputStream();
                inputStream = bluetoothSocket.getInputStream();
                
                isConnected = true;
                notifyConnected();
                
                // Inicializar ELM327
                initializeELM327();
                
                // Iniciar lectura continua
                startReadThread();
                
            } catch (IOException e) {
                Log.e(TAG, "Error al conectar: " + e.getMessage());
                notifyError("Error de conexión: " + e.getMessage());
                disconnect();
            }
        }).start();
    }
    
    /**
     * Inicializa el chip ELM327 con comandos AT
     */
    private void initializeELM327() {
        try {
            Thread.sleep(1000); // Esperar estabilización
            
            // Reset del dispositivo
            sendCommand("ATZ");
            Thread.sleep(1500);
            
            // Desactivar eco
            sendCommand("ATE0");
            Thread.sleep(200);
            
            // Desactivar espacios
            sendCommand("ATS0");
            Thread.sleep(200);
            
            // Desactivar cabeceras
            sendCommand("ATH0");
            Thread.sleep(200);
            
            // Seleccionar protocolo automático
            sendCommand("ATSP0");
            Thread.sleep(200);
            
            // Configurar timeout (rápido para datos en vivo)
            sendCommand("ATST32");
            Thread.sleep(200);
            
            // Verificar conexión con el vehículo
            String response = sendCommandWithResponse("0100");
            
            if (response != null && !response.contains("ERROR") && !response.contains("NO DATA")) {
                isInitialized = true;
                Log.i(TAG, "ELM327 inicializado correctamente");
            } else {
                notifyError("No se detecta conexión con el vehículo");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error en inicialización: " + e.getMessage());
            notifyError("Error al inicializar: " + e.getMessage());
        }
    }
    
    /**
     * Envía un comando al ELM327
     */
    public void sendCommand(String command) {
        if (!isConnected || outputStream == null) {
            Log.e(TAG, "No conectado");
            return;
        }
        
        try {
            String cmd = command.trim() + "\r";
            outputStream.write(cmd.getBytes());
            outputStream.flush();
            Log.d(TAG, "Comando enviado: " + command);
        } catch (IOException e) {
            Log.e(TAG, "Error al enviar comando: " + e.getMessage());
            notifyError("Error al enviar comando");
        }
    }
    
    /**
     * Envía comando y espera respuesta sincronizada
     */
    public String sendCommandWithResponse(String command) {
        if (!isConnected) return null;
        
        try {
            // Limpiar buffer
            while (inputStream.available() > 0) {
                inputStream.read();
            }
            
            // Enviar comando
            String cmd = command.trim() + "\r";
            outputStream.write(cmd.getBytes());
            outputStream.flush();
            
            // Esperar respuesta
            Thread.sleep(100);
            
            // Leer respuesta
            StringBuilder response = new StringBuilder();
            byte[] buffer = new byte[1024];
            int timeout = 0;
            
            while (timeout < 20) { // 2 segundos timeout
                if (inputStream.available() > 0) {
                    int bytes = inputStream.read(buffer);
                    response.append(new String(buffer, 0, bytes));
                    
                    String resp = response.toString();
                    if (resp.contains(">") || resp.contains("OK")) {
                        break;
                    }
                } else {
                    Thread.sleep(100);
                    timeout++;
                }
            }
            
            String result = response.toString()
                .replace("\r", "")
                .replace("\n", "")
                .replace(">", "")
                .replace(command, "")
                .trim();
            
            Log.d(TAG, "Respuesta: " + result);
            return result;
            
        } catch (Exception e) {
            Log.e(TAG, "Error en comando con respuesta: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Thread de lectura continua
     */
    private void startReadThread() {
        readThread = new Thread(() -> {
            byte[] buffer = new byte[1024];
            StringBuilder currentData = new StringBuilder();
            
            while (isConnected) {
                try {
                    if (inputStream.available() > 0) {
                        int bytes = inputStream.read(buffer);
                        String data = new String(buffer, 0, bytes);
                        currentData.append(data);
                        
                        // Si termina con >, tenemos respuesta completa
                        if (data.contains(">")) {
                            final String completeData = currentData.toString();
                            currentData.setLength(0);
                            
                            // Notificar en hilo principal
                            mainHandler.post(() -> {
                                if (dataListener != null) {
                                    dataListener.onDataReceived(completeData);
                                }
                            });
                        }
                    }
                    Thread.sleep(50);
                } catch (Exception e) {
                    if (isConnected) {
                        Log.e(TAG, "Error en lectura: " + e.getMessage());
                    }
                    break;
                }
            }
        });
        readThread.start();
    }
    
    /**
     * Lee códigos DTC (Diagnostic Trouble Codes)
     */
    public List<String> readDTCs() {
        List<String> dtcs = new ArrayList<>();
        
        if (!isInitialized) {
            Log.e(TAG, "ELM327 no inicializado");
            return dtcs;
        }
        
        try {
            // Solicitar DTCs
            String response = sendCommandWithResponse("03");
            
            if (response != null && !response.contains("NO DATA")) {
                // Parsear respuesta
                String[] bytes = response.split(" ");
                
                for (int i = 1; i < bytes.length; i += 2) {
                    if (i + 1 < bytes.length) {
                        String dtc = parseDTC(bytes[i], bytes[i + 1]);
                        if (dtc != null && !dtc.equals("P0000")) {
                            dtcs.add(dtc);
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error al leer DTCs: " + e.getMessage());
        }
        
        return dtcs;
    }
    
    /**
     * Convierte bytes a código DTC
     */
    private String parseDTC(String byte1, String byte2) {
        try {
            int b1 = Integer.parseInt(byte1, 16);
            int b2 = Integer.parseInt(byte2, 16);
            
            // Determinar prefijo
            char prefix;
            int prefixCode = (b1 >> 6) & 0x03;
            switch (prefixCode) {
                case 0: prefix = 'P'; break;
                case 1: prefix = 'C'; break;
                case 2: prefix = 'B'; break;
                case 3: prefix = 'U'; break;
                default: prefix = 'P';
            }
            
            // Construir código
            int digit1 = (b1 >> 4) & 0x03;
            int digit2 = b1 & 0x0F;
            int digit3 = (b2 >> 4) & 0x0F;
            int digit4 = b2 & 0x0F;
            
            return String.format("%c%d%X%X%X", prefix, digit1, digit2, digit3, digit4);
            
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Borra códigos DTC
     */
    public boolean clearDTCs() {
        if (!isInitialized) return false;
        
        String response = sendCommandWithResponse("04");
        return response != null && !response.contains("ERROR");
    }
    
    /**
     * Desconecta del ELM327
     */
    public void disconnect() {
        isConnected = false;
        isInitialized = false;
        
        try {
            if (readThread != null && readThread.isAlive()) {
                readThread.interrupt();
            }
            
            if (inputStream != null) {
                inputStream.close();
            }
            
            if (outputStream != null) {
                outputStream.close();
            }
            
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
            }
            
        } catch (IOException e) {
            Log.e(TAG, "Error al desconectar: " + e.getMessage());
        }
        
        notifyDisconnected();
    }
    
    public boolean isConnected() {
        return isConnected;
    }
    
    public boolean isInitialized() {
        return isInitialized;
    }
    
    // Métodos de notificación
    private void notifyConnecting() {
        mainHandler.post(() -> {
            if (connectionListener != null) {
                connectionListener.onConnecting();
            }
        });
    }
    
    private void notifyConnected() {
        mainHandler.post(() -> {
            if (connectionListener != null) {
                connectionListener.onConnected();
            }
        });
    }
    
    private void notifyDisconnected() {
        mainHandler.post(() -> {
            if (connectionListener != null) {
                connectionListener.onDisconnected();
            }
        });
    }
    
    private void notifyError(String error) {
        mainHandler.post(() -> {
            if (connectionListener != null) {
                connectionListener.onError(error);
            }
        });
    }
}
