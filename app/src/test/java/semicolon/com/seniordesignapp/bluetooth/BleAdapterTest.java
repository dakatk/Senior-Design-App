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

import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * SDRD 3.1, untimed
 */
@RunWith(JUnit4.class)
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
            0x00, 0x00, 0x58, 0x42
            // 0x42, 0x58, 0x00, 0x00
    };

    private BleAdapter testBleAdapter;

    @Before
    public void createMockBleDevice () {

        MockitoAnnotations.initMocks(this);

        when(mockDevice.getAddress()).thenReturn("CC:ED:12:C6:04:E9");
        when(mockDevice.connectGatt(any(Context.class), any(Boolean.class), any(BluetoothGattCallback.class))).thenReturn(mockGatt);

        when(mockGatt.getService(any(UUID.class))).thenReturn(mockService);
        when(mockService.getCharacteristic(any(UUID.class))).thenReturn(mockCharacteristic);
        when(mockCharacteristic.getDescriptor(any(UUID.class))).thenReturn(mockDescriptor);

        when(mockCharacteristic.getValue()).thenReturn(testGattValue);

        testBleAdapter = new BleAdapter(mockContext, mockDevice);
    }

    @Test
    public void testGatt () {

        assertEquals(mockDevice.getAddress(), "CC:ED:12:C6:04:E9");

        testBleAdapter.getGattCallback().onCharacteristicChanged(mockGatt, mockCharacteristic);

        assertNotNull(testBleAdapter.getNextGattValue());
        assertEquals(54.0f, testBleAdapter.getNextGattValue(), 0.0f);
    }
}