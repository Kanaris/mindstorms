package charkes.siarhei.bluetooth.exception;

public class BluetoothException extends Exception {
	public BluetoothException(Throwable throwable) {
		super(throwable);
	}
	public BluetoothException(String message) {
		super(message);
	}
	public BluetoothException() {
	}
}
