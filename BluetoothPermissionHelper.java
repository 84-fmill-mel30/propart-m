package com.propart.diagnostic.utils;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilidad para manejar permisos Bluetooth en todas las versiones de Android
 * Incluye compatibilidad con Android 12+ (API 31+)
 */
public class BluetoothPermissionHelper {
    
    private static final int REQUEST_CODE_BLUETOOTH_PERMISSIONS = 100;
    private static final int REQUEST_CODE_ENABLE_BT = 101;
    
    private Activity activity;
    private PermissionCallback callback;
    
    public interface PermissionCallback {
        void onPermissionsGranted();
        void onPermissionsDenied(List<String> deniedPermissions);
    }
    
    public BluetoothPermissionHelper(Activity activity) {
        this.activity = activity;
    }
    
    /**
     * Solicita todos los permisos necesarios según la versión de Android
     */
    public void requestBluetoothPermissions(PermissionCallback callback) {
        this.callback = callback;
        
        List<String> permissionsToRequest = new ArrayList<>();
        
        // Android 12+ (API 31+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // BLUETOOTH_CONNECT es obligatorio para conectar
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) 
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT);
            }
            
            // BLUETOOTH_SCAN es necesario para buscar dispositivos
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_SCAN) 
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN);
            }
        }
        
        // Android 10-11 (API 29-30)
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }
        
        // Android 6-9 (API 23-28)
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
        }
        
        // Si hay permisos por solicitar
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(
                activity,
                permissionsToRequest.toArray(new String[0]),
                REQUEST_CODE_BLUETOOTH_PERMISSIONS
            );
        } else {
            // Ya tenemos todos los permisos
            checkBluetoothEnabled();
        }
    }
    
    /**
     * Verifica si Bluetooth está habilitado, si no, solicita habilitarlo
     */
    private void checkBluetoothEnabled() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
        if (bluetoothAdapter == null) {
            Toast.makeText(activity, "Este dispositivo no soporta Bluetooth", Toast.LENGTH_LONG).show();
            if (callback != null) {
                List<String> denied = new ArrayList<>();
                denied.add("Bluetooth no disponible");
                callback.onPermissionsDenied(denied);
            }
            return;
        }
        
        if (!bluetoothAdapter.isEnabled()) {
            // Solicitar habilitar Bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) 
                    == PackageManager.PERMISSION_GRANTED) {
                    activity.startActivityForResult(enableBtIntent, REQUEST_CODE_ENABLE_BT);
                }
            } else {
                activity.startActivityForResult(enableBtIntent, REQUEST_CODE_ENABLE_BT);
            }
        } else {
            // Bluetooth habilitado, permisos OK
            if (callback != null) {
                callback.onPermissionsGranted();
            }
        }
    }
    
    /**
     * Maneja el resultado de la solicitud de permisos
     * Llamar desde onRequestPermissionsResult() de tu Activity
     */
    public void handlePermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_BLUETOOTH_PERMISSIONS) {
            List<String> deniedPermissions = new ArrayList<>();
            
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(permissions[i]);
                }
            }
            
            if (deniedPermissions.isEmpty()) {
                // Todos los permisos concedidos
                checkBluetoothEnabled();
            } else {
                // Algunos permisos denegados
                if (callback != null) {
                    callback.onPermissionsDenied(deniedPermissions);
                }
                
                Toast.makeText(activity, 
                    "Permisos Bluetooth necesarios para conectar con ELM327", 
                    Toast.LENGTH_LONG).show();
            }
        }
    }
    
    /**
     * Maneja el resultado de habilitar Bluetooth
     * Llamar desde onActivityResult() de tu Activity
     */
    public void handleEnableBluetoothResult(int requestCode, int resultCode) {
        if (requestCode == REQUEST_CODE_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth habilitado
                if (callback != null) {
                    callback.onPermissionsGranted();
                }
            } else {
                // Usuario rechazó habilitar Bluetooth
                if (callback != null) {
                    List<String> denied = new ArrayList<>();
                    denied.add("Bluetooth no habilitado");
                    callback.onPermissionsDenied(denied);
                }
                
                Toast.makeText(activity, 
                    "Bluetooth debe estar habilitado para usar el scanner OBD2", 
                    Toast.LENGTH_LONG).show();
            }
        }
    }
    
    /**
     * Verifica si todos los permisos están concedidos
     */
    public boolean hasBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) 
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_SCAN) 
                == PackageManager.PERMISSION_GRANTED;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }
    
    /**
     * Ejemplo de uso en tu Activity:
     * 
     * private BluetoothPermissionHelper permissionHelper;
     * 
     * @Override
     * protected void onCreate(Bundle savedInstanceState) {
     *     super.onCreate(savedInstanceState);
     *     setContentView(R.layout.activity_main);
     *     
     *     permissionHelper = new BluetoothPermissionHelper(this);
     *     
     *     btnConnect.setOnClickListener(v -> {
     *         permissionHelper.requestBluetoothPermissions(new BluetoothPermissionHelper.PermissionCallback() {
     *             @Override
     *             public void onPermissionsGranted() {
     *                 // Permisos OK, conectar al ELM327
     *                 connectToELM327();
     *             }
     *             
     *             @Override
     *             public void onPermissionsDenied(List<String> deniedPermissions) {
     *                 // Permisos denegados
     *                 Toast.makeText(MainActivity.this, "Permisos necesarios", Toast.LENGTH_SHORT).show();
     *             }
     *         });
     *     });
     * }
     * 
     * @Override
     * public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
     *     super.onRequestPermissionsResult(requestCode, permissions, grantResults);
     *     permissionHelper.handlePermissionsResult(requestCode, permissions, grantResults);
     * }
     * 
     * @Override
     * protected void onActivityResult(int requestCode, int resultCode, Intent data) {
     *     super.onActivityResult(requestCode, resultCode, data);
     *     permissionHelper.handleEnableBluetoothResult(requestCode, resultCode);
     * }
     */
}
