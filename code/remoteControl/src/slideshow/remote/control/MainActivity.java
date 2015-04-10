package slideshow.remote.control;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


public class MainActivity extends Activity {
	
	private static final String DISPLAY_LABEL_OF_LIVING_ROOM = "Living Room";
	private static final String DISPLAY_LABEL_OF_STUDIO = "Studio";
	private static final String DISPLAY_LABEL_OF_KITCHEN = "Kitchen";
	
	private static final String IP_OF_LIVING_ROOM = "192.168.1.1";
	private static final String IP_OF_STUDIO = "192.168.1.2";
	private static final String IP_OF_KITCHEN = "192.168.1.3";
	
	private static final int PORT_OF_SERVER = 8888;
	
	private static final String PREPEND_TO_ERROR_LOG = "OUCH!!! ... ";
	private static final String LOG_TAG = "remote";
	
	private Spinner clientDeviceNameSpinner;
	private Button pausePlaySlideShowButton;
	private Button stopSlideShowButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configurePausePlaySlideShowButton();
        configureClientDeviceNameSpinner();
        configureStopSlideShowButton();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    private void configurePausePlaySlideShowButton() {
        this.pausePlaySlideShowButton = (Button)findViewById(R.id.btnPausePlay);
        this.pausePlaySlideShowButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Thread cThread = new Thread(new pausePlaySlideShowThread());
				cThread.start();
			}
		});
	}
    
	private void configureStopSlideShowButton() {
		this.stopSlideShowButton = (Button)findViewById(R.id.btnStopSlideShow);
		this.stopSlideShowButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Thread cThread = new Thread(new stopSlideShowThread());
				cThread.start();
			}
		});
	}

	private void configureClientDeviceNameSpinner() {
        this.clientDeviceNameSpinner = (Spinner)findViewById(R.id.clientDeviceNameSpinner);
        String [] arrClients ={DISPLAY_LABEL_OF_LIVING_ROOM, DISPLAY_LABEL_OF_STUDIO, DISPLAY_LABEL_OF_KITCHEN};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,arrClients);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.clientDeviceNameSpinner.setAdapter(adapter);		
	}

    public class pausePlaySlideShowThread implements Runnable {
    	public void run(){
    		try {
    			sendMessage("1$");
    		} catch (Exception e){
    			Log.d(LOG_TAG, PREPEND_TO_ERROR_LOG + "pausePlaySlideShowThread ... " + e.toString());
    		}
    	}
    }
    
    public class stopSlideShowThread implements Runnable {
    	public void run(){
    		try {
    			sendMessage("2$");
    		} catch (Exception e){
    			Log.d(LOG_TAG, PREPEND_TO_ERROR_LOG + "stopSlideShowThread ... " + e.toString());
    		}
    	}
    }

	public void sendMessage(String message) {
		InetAddress connectingIPAddress = getClientDeviceIp();
		Log.d(LOG_TAG, "Connecting to " + connectingIPAddress.toString() + "...");
		Socket socket = null;
		try {
			socket = new Socket(connectingIPAddress, PORT_OF_SERVER);
		} catch (IOException e) {
			Log.d(LOG_TAG, PREPEND_TO_ERROR_LOG + "sendMessage ... " + e.toString());
			return;
		}
		Log.d(LOG_TAG, "Requesting slide show toggle.");
		PrintWriter out = null;
		try {
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
		} catch (IOException e) {
			Log.d(LOG_TAG, PREPEND_TO_ERROR_LOG + "sendMessage ... " + e.toString());
		}
		// 1 = pause/play slide show
		// 2 = stop slide show
		out.println(message);
	}
	
	public InetAddress getClientDeviceIp() {
		String clientDeviceIpString = null;
		String slectedClientDeviceName = this.clientDeviceNameSpinner.getSelectedItem().toString();
		InetAddress ip = null;
		if (slectedClientDeviceName == DISPLAY_LABEL_OF_LIVING_ROOM){
			clientDeviceIpString = IP_OF_LIVING_ROOM;
		} else if(slectedClientDeviceName == DISPLAY_LABEL_OF_STUDIO) {
			clientDeviceIpString = IP_OF_STUDIO;
		} else if(slectedClientDeviceName == DISPLAY_LABEL_OF_KITCHEN) {
			clientDeviceIpString = IP_OF_KITCHEN;
		}
		try {
			ip = InetAddress.getByName(clientDeviceIpString);
		} catch (UnknownHostException e) {
			Log.d(LOG_TAG, PREPEND_TO_ERROR_LOG + "getClientDeviceIp ... " + e.toString());
		}
		return ip;
	}

}
