package charkes.siarhei.mindstorms;

import java.io.IOException;

import android.location.Address;

import charkes.siarhei.bluetooth.BluetoothConnector;
import charkes.siarhei.bluetooth.exception.BluetoothException;
import charkes.siarhei.mindstorms.exception.ConnectionMindstormsException;
import charkes.siarhei.mindstorms.exception.MindstormsException;

public class Mindstorms {
	/***********************************************
	 * Constants
	 */
	/***********************************************
	 * Variables
	 */
	private BluetoothConnector connector;
	
	public Mindstorms(BluetoothConnector connector) {
		this.connector = connector;
	}
	public synchronized int getBattery() throws 
		MindstormsException {
		if(!connector.isConnected()) {
			throw new ConnectionMindstormsException("No connection");
		}

		try {	
			sendCommand(new byte[]{0x00,0x0b});
	        
	        byte[]input = new byte[2];
	        connector.read(input);

	        int size = (int)(input[0]) + (input[1]<<8);
	        if (size > 0) {
	            input = new byte[size];
	            connector.read(input);
	
	            if (size != 5 || input[0] != 0x02 || input[1] != 0x0b || input[2] != 0x00) {
	    			throw new MindstormsException("Battery voltage error (" +
	    					"size: " + size + "," +
	    					"byte 0: " + input[0] + "," + 
	    					"byte 1:" + input[1] + "," +
	    					"byte 2:" + input[2] + ")");
	            }
	            return (int)(input[3]) + (input[4]<<8);
	        }
		} catch (Exception e) {
			throw new MindstormsException(e);
		}
		throw new MindstormsException("Battery voltage error");	        
	}
	public synchronized void move(byte power, byte output)throws 
		MindstormsException {
			if(!connector.isConnected()) {
				throw new ConnectionMindstormsException("No connection");
			}
		
	   		try {
 				sendCommand(new byte[] {
							(byte)0x80,//Start program
						0x04,//
						output,// all
						power,
						0x01,//MOTORON+BRAKE
						0x00,
						0x00,
						0x20,
						(byte)0x80,
						0x00,
						0x00,
						0x00});
	        } catch (Exception e) {
	        	throw new MindstormsException(e);
	        }
	}
	public synchronized void runProgram(String name)throws 
		MindstormsException {
		if(!connector.isConnected()) {
			throw new ConnectionMindstormsException("No connection");
		}
	
   		try {
   			byte[]command = new byte[name.length() + 3];
   			command[0] = (byte)0x80;
   			command[1] = 0x00;
   			command[name.length() + 2] = 0x00;
   			int index = 2;
   			for(byte byteAt: name.getBytes("ASCII")) {
   				command[index++] = byteAt;
   			}
   			sendCommand(command);
        } catch (Exception e) {
        	throw new MindstormsException(e);
        }
	}
	public synchronized void stopProgram(String name)throws 
		MindstormsException {
		if(!connector.isConnected()) {
			throw new ConnectionMindstormsException("No connection");
		}
	
		try {
			byte[]command = new byte[name.length() + 3];
			command[0] = (byte)0x80;
			command[1] = 0x01;
			command[name.length() + 2] = 0x00;
			int index = 2;
			for(byte byteAt: name.getBytes("ASCII")) {
				command[index++] = byteAt;
			}
			sendCommand(command);
	    } catch (Exception e) {
	    	throw new MindstormsException(e);
	    }
	}
	/********************************************************
	 * Helpers
	 * @throws IOException 
	 * @throws BluetoothException 
	 *******************************************************/
	private void sendCommand(byte[]command) throws BluetoothException, IOException {
		byte[]packedCommand = pack(command);
		connector.write(packedCommand);
	}
	private byte[] pack(byte[] command) {
		byte[]packed = new byte[command.length + 2];
		packed[0] = (byte)(command.length&0xFF);
		packed[1] = (byte)((command.length>>8)&0xFF);
	
		for(int i = 0; i < command.length; i++) {
			packed[i+2] = command[i];
		}
		return packed;
	}
}
