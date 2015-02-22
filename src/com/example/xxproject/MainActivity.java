package com.example.xxproject;

import java.io.IOException;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author lenovo
 * 
 */
public class MainActivity extends Activity {
	// Layout and Views
	private Dialog alert;
	private EditText mEditText;
	private TextView mStateTextView;
	private TextView mIpTextView;
	private TextView mTempTextView;
	private Button mButton1;
	private Button mButton2;
	// Values
	private String mPassword;
	private boolean LED1On = false;
	private boolean LED2On = false;
	// port is set to a constant 31020
	private final int PORT = 31020;
	// lable for handler
	private final int NOT_ACCEPTED = 0;
	private final int ACCEPTED = 3;
	private final int IO_EXCEPTION = 1;
	private final int ON_RECEIVED = 2;

	private Tcp mTcp;
	private TcpThread mTcpThread;
	private static Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mStateTextView = (TextView) findViewById(R.id.textview_state);
		mIpTextView = (TextView) findViewById(R.id.textview_ip);
		mTempTextView = (TextView) findViewById(R.id.textview_temp);
		mButton1 = (Button) findViewById(R.id.button_LED1);
		mButton2 = (Button) findViewById(R.id.button_LED2);

		showIp();

		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// handle message from TcpThread

				switch (msg.what) {
				case NOT_ACCEPTED:
					// no socket accepted or password wrong
					Toast.makeText(getApplicationContext(),
							"No Socket Accepted or Password Wrong",
							Toast.LENGTH_LONG).show();
					mStateTextView.setText("Disconnected");
					break;

				case ACCEPTED:
					// socket accepted
					Toast.makeText(getApplicationContext(),
							"Success Connected", Toast.LENGTH_LONG).show();
					mStateTextView.setText("Connected");
					break;

				case IO_EXCEPTION:
					// IoException
					Toast.makeText(getApplicationContext(),
							"Please Ensure the Network is Well-Setted",
							Toast.LENGTH_LONG).show();
					mStateTextView.setText("Disconnected");
					break;
				case ON_RECEIVED:
					// message received from socket
					onReceived(msg.obj.toString());
					break;
				}

			}

		};

	}

	/**
	 * TEMPTURE: 'T'+2 bit int, for example T50, means 0 (50-50) degree; T40
	 * means -10 degree() ; LED: ‘L’+1 bit int, for example L1 means the first
	 * LED on, L2 means first LED off, L3 means second LED on, L4 means second
	 * LED off
	 * 
	 * @param string
	 */
	private void onReceived(String string) {
		switch (string.charAt(0)) {
		case 'T':
			String temString = string.substring(1, string.length() + 1);
			mTempTextView.setText(temString);
			break;
		case 'L':
			switch (string.charAt(1)) {
			case '1':
				// LED 1 on
				mButton1.setBackgroundColor(Color.parseColor("#009966"));
				LED1On = true;
				break;
			case '2':
				// LED 1 off
				mButton1.setBackgroundColor(Color.parseColor("#CC0033"));
				LED1On = false;
				break;

			case '3':
				// LED 2 on
				mButton2.setBackgroundColor(Color.parseColor("#009966"));
				LED2On = true;
				break;
			case '4':
				// LED 2 off
				mButton2.setBackgroundColor(Color.parseColor("#CC0033"));
				LED2On = false;
				break;

			}

			break;

		}

	}

	private void showIp() {
		// get Wifi Service
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		// ensure wifi is enabled
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String ip = intToIp(ipAddress);
		mIpTextView.setText(ip);

	}

	/**
	 * parse int to ip
	 * 
	 * @param i
	 * @return
	 */
	private String intToIp(int i) {

		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + (i >> 24 & 0xFF);
	}

	public void onLaunchbtClicked(View view) {
		// Log.e("TAG", "CLICKED");
		// PasswordDialog mPasswordDialog=new PasswordDialog();
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
		// mBuilder.setTitle("Input Password Here");
		alert();

	}

	/**
	 * TEMPTURE: 'T'+2 bit int, for example T50, means 0 (50-50) degree; T40
	 * means -10 degree() ; LED: ‘L’+1 bit int, for example L1 means the first
	 * LED on, L2 means first LED off, L3 means second LED on, L4 means second
	 * LED off
	 * 
	 * @param view
	 */
	public void onLEDButtonClicked(View view) {
		try {
			switch (view.getId()) {
			case R.id.button_LED1:
				if (mTcp != null && mTcp.getOutputStream() != null) {

					if (LED1On) {
						// turn off LED1
						mTcp.write("L2");
						LED1On = false;
						mButton1.setBackgroundColor(Color.parseColor("#CC0033"));// red
					} else {
						// turn on LED1
						mTcp.write("L1");
						LED1On = true;
						mButton1.setBackgroundColor(Color.parseColor("#009966"));// green
					}

				}
				break;
			case R.id.button_LED2:

				if (mTcp != null && mTcp.getOutputStream() != null) {

					if (LED2On) {
						// turn off LED2
						mTcp.write("L4");
						LED2On = false;
						mButton2.setBackgroundColor(Color.parseColor("#CC0033"));// red
					} else {
						// turn on LED2
						mTcp.write("L3");
						LED1On = true;
						mButton2.setBackgroundColor(Color.parseColor("#009966"));// green
					}

					break;

				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * On temp + clicked
	 * 
	 * @param view
	 */
	public void onTempAdd(View view) {
		if (mTcp != null && mTcp.getOutputStream() != null) {
			try {
				String tempString = mTempTextView.getText().toString();
				int tempInt = Integer.parseInt(tempString);
				if (tempInt > 0 && tempInt < 100) {
					tempInt++;
				}
				tempString = String.valueOf(tempInt);
				mTempTextView.setText(tempString);
				mTcp.write(tempString);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * On temp - clicked
	 * 
	 * @param view
	 */
	public void onTempDec(View view) {
		if (mTcp != null && mTcp.getOutputStream() != null) {
			try {
				String tempString = mTempTextView.getText().toString();
				int tempInt = Integer.parseInt(tempString);
				if (tempInt > 0 && tempInt < 100) {
					tempInt--;
				}
				tempString = String.valueOf(tempInt);
				mTempTextView.setText(tempString);
				mTcp.write(tempString);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void alert() {
		alert = new Dialog(this);
		alert.setTitle("Input Your Password");
		// alert = new Dialog.Builder(this).create();
		alert.show();
		// alert.getWindow().setLayout(width, height);

		// set layout
		alert.getWindow().setContentView(R.layout.password_dialog);
		mEditText = (EditText) alert.getWindow().findViewById(
				R.id.edittext_password);
		Button mconfrimButton = (Button) alert.getWindow().findViewById(
				R.id.button_confirm);
		mconfrimButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// on confirm button clicked
				mPassword = mEditText.getText().toString();
				Log.e("TAG", "password:" + mPassword);
				if (!mPassword.equalsIgnoreCase("")) {
					// if password inputed
					connect(mPassword);
					alert.cancel();
				}
			}
		});
		Button mcancelButton = (Button) alert.getWindow().findViewById(
				R.id.button_cancel);
		mcancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// on confirm button clicked
				Log.e("TAG", "CLICKED");
				alert.cancel();

			}
		});

	}

	private void connect(String password) {

		try {
			mTcp = new Tcp(PORT, password);
			mTcpThread = new TcpThread(mTcp, mHandler);
			mTcpThread.start();
		} catch (IOException e) {
			//
			e.printStackTrace();
		}

	}

}
