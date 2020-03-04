package semicolon.com.seniordesignapp.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.UUID;

import semicolon.com.seniordesignapp.BuildConfig;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)

public class BleAdapterTest {

    @Mock
    BluetoothDevice mockDevice;

    @Mock
    BluetoothGatt mockGatt;

    @Mock
    BluetoothGattService mockService;

    @Mock
    BluetoothGattCharacteristic mockCharacteristic;

    @Mock
    BluetoothGattDescriptor mockDescriptor;

    @Mock
    Context mockContext;

    private byte[] testGattValue = new byte[]{
            0x42, 0x58, 0x00, 0x00
    };

    private BleAdapter testBleAdapter;

    @Before
    public void createMockBleDevice () {

        MockitoAnnotations.initMocks(this);

        when(mockDevice.getAddress()).thenReturn("CC:ED:12:C6:04:E9");
        when(mockDevice.connectGatt(mockContext, true, any(BluetoothGattCallback.class))).thenReturn(mockGatt);

        when(mockGatt.getService(any(UUID.class))).thenReturn(mockService);
        when(mockService.getCharacteristic(any(UUID.class))).thenReturn(mockCharacteristic);
        when(mockCharacteristic.getDescriptor(any(UUID.class))).thenReturn(mockDescriptor);

        when(mockCharacteristic.getValue()).thenReturn(testGattValue);

        testBleAdapter = new BleAdapter(mockContext, mockDevice);
    }

    @Test
    public void testGatt () {

        testBleAdapter.getGattCallback().onCharacteristicChanged(mockGatt, mockCharacteristic);
        assertNotNull(testBleAdapter.getNextGattValue());
    }
}