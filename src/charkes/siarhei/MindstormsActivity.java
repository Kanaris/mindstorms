package charkes.siarhei;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import charkes.siarhei.bluetooth.BluetoothConnector;
import charkes.siarhei.bluetooth.exception.BluetoothException;
import charkes.siarhei.mindstorms.Mindstorms;
import charkes.siarhei.mindstorms.exception.MindstormsException;
import charkes.siarhei.view.BatteryStatus;
import charkes.siarhei.view.ConnectionStatus;

public class MindstormsActivity extends Activity {
	public static final int REQUEST_ENABLE_BT = 1;
	public static final String NXT_NAME = "NXT";
	
    private Map<String, String> mPairedDevicesArrayAdapter = new HashMap<String, String>();
    private BluetoothConnector connector = new BluetoothConnector();
    private Mindstorms mindstorms = new Mindstorms(connector);
	private List<Button>controlButtons = new ArrayList<Button>();
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		try {

	       ((Button)findViewById(R.id.connectBtn)).setOnClickListener(buttonClicker);
	       ((Button)findViewById(R.id.disconnectBtn)).setOnClickListener(buttonClicker);
	       controlButtons.add((Button)findViewById(R.id.forwardBtn));
	       controlButtons.add((Button)findViewById(R.id.backwardBtn));
	       controlButtons.add((Button)findViewById(R.id.leftBtn));
	       controlButtons.add((Button)findViewById(R.id.rightBtn));
	       controlButtons.add((Button)findViewById(R.id.startProgramBtn));
	       controlButtons.add((Button)findViewById(R.id.stopProgramBtn));
	       for(Button button: controlButtons) {
	    	   button.setOnClickListener(buttonClicker);
	       }
	       
	       
		   	if (!BluetoothConnector.isSupported()) {
		   		addToTextView("Sorry. BlueTooth is not supported");
		   		finish();
		   	} else {
		   		addToTextView("BlueTooth is supported");
		   		addToTextView("Name: " + BluetoothConnector.getAdapterName());
		   	}
	        
	        mPairedDevicesArrayAdapter = BluetoothConnector.getBondedDevices();
	        if (mPairedDevicesArrayAdapter.size() > 0) {
	        	Iterator<String>it = mPairedDevicesArrayAdapter.keySet().iterator();
	        	while(it.hasNext()) {
	            	addToTextView("Paired device: " + it.next());
	        	}
	        } else {
	        	addToTextView("No paired device");
	        }
   		} catch (BluetoothException e){
   			printStackTrace(e);
   		}
	}    

	@Override
	protected void onStart() {
		super.onStart();
		try {
			if (BluetoothConnector.isEnabled()) {
		   		addToTextView("BlueTooth is enabled");
		   	} else {
		   		addToTextView("BlueTooth is not enabled");
		   	}
		} catch (BluetoothException e) {
			printStackTrace(e);
		}
	}

    @Override
	protected void onDestroy() {
    	super.onDestroy();    	
    	try {
    		connector.disconnect();
		} catch (BluetoothException e) {}
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		MenuItem menuItem = menu.add("Exit");
		menuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem arg0) {
				finish();
				return true;
			}
		});
		return true;
	}
    /**************************************************
     * Button handler
     * ***********************************************/  	
	private OnClickListener buttonClicker = new OnClickListener() {
       	public void onClick(View v) {
   			updateBattery();
   			Button button = (Button)v;
   			try {
   				switch(button.getId()) {
   					//connection
   					case R.id.connectBtn:
   						Map<String, String> temp = new HashMap<String, String>();
   						temp.putAll(mPairedDevicesArrayAdapter);
   						
   						String nxtKey = temp.get(NXT_NAME);
   						if (nxtKey != null) {
							connector.connect(nxtKey);
							setConnected(true);
				            updateBattery();
   						}	
   					break;
   					case R.id.disconnectBtn:
						connector.disconnect();
						setConnected(false);
   					break;
   					//moments
   					case R.id.forwardBtn:
   						mindstorms.move((byte)100, (byte)0xFF);
   					break;
   					case R.id.leftBtn:
   						mindstorms.move((byte)100, (byte)0x02);
   					break;
   					case R.id.rightBtn:
   						mindstorms.move((byte)100, (byte)0x00);
   					break;  				
   					case R.id.backwardBtn:
   						mindstorms.move((byte)-100, (byte)0xFF);
   					break;
   					//sub programs
   					case R.id.startProgramBtn:
   						mindstorms.runProgram("Minder1.rxe");
   					break;
   					case R.id.stopProgramBtn:
   						mindstorms.stopProgram("Minder1.rxe");
   					break;
   				}
   			} catch (Exception e) {
				printStackTrace(e);
			}
       	}		
	};
    /**************************************************
     * Discovery
     * ***********************************************/  
    private void setConnected(boolean isConnected) {
        ConnectionStatus connectionStatus = (ConnectionStatus)findViewById(R.id.connectionStatus1);
        connectionStatus.setConnected(isConnected);
	    for(Button button: controlButtons) {
	    	button.setEnabled(isConnected);
	    }
    }
    private void updateBattery() {
        BatteryStatus batteryStatus = (BatteryStatus)findViewById(R.id.batteryStatus1);
        float voltage = 0;
        try {
        	voltage = (float)mindstorms.getBattery()/1000;
        } catch (Exception e) {
        	voltage = 0;
        }
        batteryStatus.setVoltage(voltage);
    }
	/**************************************************
     * Util methods
     * ***********************************************/
    private void printStackTrace(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		addToTextView("StackTrace: " + sw.toString());	
    }
    private void addToTextView(String text) {
    	TextView tv = getTextView();
    	tv.setText(tv.getText() + text + "\n");
    }
    private TextView getTextView() {
    	return (TextView)findViewById(R.id.textView1);
    }	
}