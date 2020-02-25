package semicolon.com.seniordesignapp.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.AsyncTask;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

import semicolon.com.seniordesignapp.R;

import static android.bluetooth.BluetoothAdapter.STATE_CONNECTED;

public class BleAdapter {

    private static final UUID CADENCE_SERVICE_UUID = convertFromInteger(0x1111);
    private static final UUID CADENCE_DATA_CHAR_UUID = convertFromInteger(0x2222);

    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothDevice pairedDevice;

    private BluetoothGatt gatt;
    private BluetoothGattCharacteristic characteristic;

    private Context context;

    //private static ArrayList<byte[]> valuesBuffer = new ArrayList<>();

    private static boolean gattChanged = false;
    private static byte[] gattValue;

    public BleAdapter (Context context, @NotNull BluetoothAdapter bluetoothAdapter) {

        this.context = context;

        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        AsyncTask.execute(new Runnable() {

            @Override
            public void run() {
                bluetoothLeScanner.startScan(leScanCallback);
            }
        });
    }

    public void enableNotifications() {

        if (characteristic == null)
            return;

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

        gatt.writeDescriptor(descriptor);
    }

    public void disableNotifications() {

        if (characteristic == null)
            return;

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
        descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);

        gatt.writeDescriptor(descriptor);
    }

    private ScanCallback leScanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, @NotNull ScanResult result) {

            if (pairedDevice != null)
                return;

            if (result.getDevice() == null)
                return;

            BluetoothDevice device = result.getDevice();
            String address = device.getAddress();

            if (address != null && address.equals(context.getString(R.string.controller_mac_addr))) {

                System.out.println("Device Paired!");

                gatt = device.connectGatt(context, true, gattCallback);
                pairedDevice = device;
            }
        }
    };

    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            if (newState == STATE_CONNECTED)
                gatt.discoverServices();
        }

        @Override
        public void onServicesDiscovered(@NotNull BluetoothGatt gatt, int status) {

            characteristic = gatt.getService(CADENCE_SERVICE_UUID).getCharacteristic(CADENCE_DATA_CHAR_UUID);
            gatt.setCharacteristicNotification(characteristic, true);

            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

            gatt.writeDescriptor(descriptor);

            bluetoothLeScanner.stopScan(leScanCallback);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, @NotNull BluetoothGattCharacteristic characteristic) {

            if (!gattChanged) {

                gattValue = characteristic.getValue();
                gattChanged = true;
            }
        }
    };

    public Float getNextGattValue() {

        if (gattValue == null)
            return null;

        float value = ByteBuffer.wrap(gattValue).order(ByteOrder.LITTLE_ENDIAN).getFloat();

        gattChanged = false;

        return value;
    }

    @NotNull
    @Contract("_ -> new")
    private static UUID convertFromInteger(int i) {

        final long MSB = 0x0000000000001000L;
        final long LSB = 0x800000805f9b34fbL;
        long value = i & 0xFFFFFFFF;

        return new UUID(MSB | (value << 32), LSB);
    }
}
