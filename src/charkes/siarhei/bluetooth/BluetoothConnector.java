package charkes.siarhei.bluetooth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import charkes.siarhei.bluetooth.exception.BluetoothException;
import charkes.siarhei.bluetooth.exception.ConnectionBluetoothException;
import charkes.siarhei.bluetooth.exception.NotSupportedBluetoothException;

public class BluetoothConnector {
	private static BluetoothAdapter mBluetoothAdapter;
	
	private BluetoothSocket socket;	
	private DataOutputStream outStream;
	private DataInputStream inputStream;

	public BluetoothConnector() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	}
	public synchronized void connect(String address) throws BluetoothException {
		if (!isSupported())
			throw new NotSupportedBluetoothException();
		if (isConnected())
			return;
		
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		try {
			Method m = device.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
			socket = (BluetoothSocket) m.invoke(device, 1);
			socket.connect();
			
        	outStream = new DataOutputStream(socket.getOutputStream());
        	inputStream = new DataInputStream(socket.getInputStream());
	    } catch (Exception e) {
	    	socket = null;
			throw new ConnectionBluetoothException(e);
	    }
	}
	public void write(byte[]command) throws BluetoothException, IOException {
		if (!isSupported())
			throw new NotSupportedBluetoothException();
		if (outStream != null) {
			outStream.write(command);
			outStream.flush();
		} else {
			throw new ConnectionBluetoothException("Not connected");
		}
	}
	public int read(byte[]response) throws BluetoothException, IOException {
		if (!isSupported())
			throw new NotSupportedBluetoothException();
		
		int result;
		if (inputStream != null) {
			result = inputStream.read(response);
		} else {
			throw new ConnectionBluetoothException("Not connected");
		}
		return result;
	}
	public synchronized void disconnect() throws BluetoothException {
		if (!isSupported())
			throw new NotSupportedBluetoothException();
		
		cancelDiscovery();

		if (outStream != null) {
			try {
				outStream.close();
			} catch (IOException e) {}				
		}
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {}				
		}		
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {}	
		}
		socket = null;
	}
	public boolean isConnected() {
		return socket != null;
	}
	/***************************************************************
	 * 	Static methods
	 ***************************************************************/
	/**
	 * get pared devices
	 * @return devices
	 * @throws BluetoothException
	 */
	public static Map<String, String> getBondedDevices()
		throws BluetoothException {
		if (!isSupported())
			throw new NotSupportedBluetoothException();
		
		Map<String, String> mPairedDevicesArrayAdapter = new HashMap<String, String>();
		
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.put(device.getName(), device.getAddress());
            }
        }
        return mPairedDevicesArrayAdapter;
	}
	/**
	 * Cancel discovery
	 */
	public static void cancelDiscovery() 
		throws BluetoothException {
		if (!isSupported())
			throw new NotSupportedBluetoothException();	
		
		if (getBluetoothAdapter().isDiscovering()) {
			getBluetoothAdapter().cancelDiscovery();			
		}
	}
	/**
	 * Start discovery
	 * @return value
	 * @throws BluetoothException
	 */
	public static boolean startDiscovery() 
		throws BluetoothException {
		if (!isSupported())
			throw new NotSupportedBluetoothException();	
		
		if (getBluetoothAdapter().isDiscovering()) {
			getBluetoothAdapter().cancelDiscovery();			
		}
		return getBluetoothAdapter().startDiscovery();
	}
	/**
	 * Get Adapter name
	 * @return name
	 * @throws BluetoothException
	 */
	public static String getAdapterName()
		throws BluetoothException {
		if (!isSupported())
			throw new NotSupportedBluetoothException();
		return getBluetoothAdapter().getName();
	}
	/**
	 * Is bluetooth supported
	 * @return support state
	 */
	public static boolean isSupported() {
		if (getBluetoothAdapter() == null)
			return false;
		return true;
	}
	/**
	 * Is bluetooth enabled
	 * @return enable state
	 * @throws BluetoothException
	 */
	public static boolean isEnabled() 
			throws BluetoothException {
		if (!isSupported())
			throw new NotSupportedBluetoothException();
		return getBluetoothAdapter().isEnabled();
	}
	/**
	 * get bluetooth adapter
	 * @return adapter
	 */
	public static BluetoothAdapter getBluetoothAdapter() {
		if (mBluetoothAdapter == null) {
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		}
		return mBluetoothAdapter;
	}
}
