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

/**
 * A container class for all the BLE stuff. This class is responsible for:
 *  - Findind and bonding with the correct bluetooth device
 *  - Extracting the relevant information about the bluetooth device's GATT (Generic ATTribute) stuff
 *  - Reading data from/Writing data to the bluetooth in specific ways
 */
public class BleAdapter {

    // The UUIDs of the pre-defined service and it's associated characteristic in the Arduino code
    private static final UUID CADENCE_SERVICE_UUID = convertFromInteger(0x1111);
    private static final UUID CADENCE_DATA_CHAR_UUID = convertFromInteger(0x2222);
    private static final UUID NTF_DESCRIPTOR_UUID = convertFromInteger(0x2902);//UUID.fromString("00002902–0000–1000-8000-00805f9b34fb");

    /**
     * Scanner that is used to manage callbacks for identifying/bonding to our
     * bluetooth device
     */
    private BluetoothLeScanner bluetoothLeScanner;

    /**
     * Retains the reference to our bluetooth device, once it has been properly
     * identified and bonded
     */
    private BluetoothDevice pairedDevice;

    /**
     * The GATT object that is created to identify/write to our device's services and
     * their corresponding characteristics
     */
    private BluetoothGatt gatt;

    /**
     * The buitlin descriptor of our bluetooth device that enables/disables the device to
     * notify our app that GATT data has been changed
     */
    private BluetoothGattDescriptor descriptor;

    /**
     * Reference to the parent of our MainActivity
     */
    private Context context;

    /**
     * Retains the four bytes that are received onCharacteristicChanged (made static because reasons)
     */
    private byte[] gattValue;

    private boolean gattChanged;

    /** The main constructor that initializes bonding and identifying the device and it's services.
     * USE THIS CONSTRUCTOR IN MAIN ACTIVITY
     *
     * @param context context
     * @param bluetoothAdapter bluetoothAdapter
     */
    public BleAdapter (Context context, @NotNull BluetoothAdapter bluetoothAdapter) {

        this.context = context;
        this.bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        this.gattChanged = false;

        // Start scanning for our device as a background task
        AsyncTask.execute(new Runnable() {

            @Override
            public void run() {
                bluetoothLeScanner.startScan(leScanCallback);
            }
        });
    }

    /**
     * USE FOR UNIT TESTS ONLY (bypasses pairing callbacks)
     *
     * @param device device
     */
    BleAdapter (Context context, @NotNull BluetoothDevice device) {

        this.pairedDevice = device;
        device.connectGatt(context, true, gattCallback);
    }

    /**
     * Send a code to enable/disable the bluetooth device to send data via GATT
     */
    public void enableNotifications(boolean enable) {

        if (descriptor == null)
            return;

        byte[] descriptorValue;

        if (enable)
            descriptorValue = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;

        else descriptorValue = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;

        descriptor.setValue(descriptorValue);
        gatt.writeDescriptor(descriptor);
    }

    /**
     * @return true if device has been found and paired successfully
     */
    public boolean hasPairedDevice() {

        return pairedDevice != null;
    }

    /**
     * Callback to scan for the correct bluetooth device to bond with
     */
    private ScanCallback leScanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, @NotNull ScanResult result) {

            // Don't continue to scan if the device has been bonded
            if (BleAdapter.this.pairedDevice != null)
                return;

            // Don't attempt to bond if the device isn't valid
            if (result.getDevice() == null)
                return;

            BluetoothDevice device = result.getDevice();
            String address = device.getAddress();

            // Make sure the MAC address is valid and that it matches the address of our bluetooth device
            if (address != null && address.equals(context.getString(R.string.ble_device_mac_addr))) {

                System.out.println("Device Paired!");

                // Begin the process of extracting GATT stuff (services/characteristics/descriptors)
                BleAdapter.this.gatt = device.connectGatt(context, true, gattCallback);
                BleAdapter.this.pairedDevice = device;
            }
        }
    };

    /**
     * Callback for when the GATT stuff is extracted from the bonded device
     */
    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            // System.out.println("ON CONNECTION STATE CHANGE");

            if (newState == STATE_CONNECTED)
                gatt.discoverServices();
        }

        @Override
        public void onServicesDiscovered(@NotNull BluetoothGatt gatt, int status) {

            // System.out.println("ON SERVICES DISCOVERED");

            BluetoothGattCharacteristic characteristic = gatt.getService(CADENCE_SERVICE_UUID).getCharacteristic(CADENCE_DATA_CHAR_UUID);
            gatt.setCharacteristicNotification(characteristic, true);

            BleAdapter.this.descriptor = characteristic.getDescriptor(NTF_DESCRIPTOR_UUID);

            enableNotifications(true);

            try {
                BleAdapter.this.bluetoothLeScanner.stopScan(BleAdapter.this.leScanCallback);
            } catch (NullPointerException ignored) {}
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, @NotNull BluetoothGattCharacteristic characteristic) {

            // System.out.println("ON CHARACTERISTIC CHANGED");

            if (!BleAdapter.this.gattChanged) {

                BleAdapter.this.gattValue = characteristic.getValue();
                BleAdapter.this.gattChanged = true;
            }
        }
    };

    /**
     * AGAIN, USED FOR UNIT TESTS ONLY
     *
     * @return gattCallback
     */
    BluetoothGattCallback getGattCallback () {
        return gattCallback;
    }

    /**
     * @return The most recent value received from the GATT characterstic
     */
    public Float getNextGattValue() {


        // Transform the four bytes that were read by from the GATT cahracteristic into a float
        if (gattChanged && gattValue != null) {

            this.gattChanged = false;
            return ByteBuffer.wrap(gattValue).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        }

        // This happens when the GATT hasn't updated in time
        return null;
    }

    /**
     * It works. Just trust it and leave it alone
     *
     * @param i i
     * @return uuid
     */
    @NotNull
    @Contract("_ -> new")
    private static UUID convertFromInteger(int i) {

        final long MSB = 0x0000000000001000L;
        final long LSB = 0x800000805f9b34fbL;
        long value = i & 0xFFFFFFFF;

        return new UUID(MSB | (value << 32), LSB);
    }
}
