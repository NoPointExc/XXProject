package com.example.xxproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;

import android.R.integer;

public class TcpClient {
	private int port;
	// 此处Ip被写死
	private final String IP = "192.168.1.199";
	private OutputStream mOutputStream;
	private InputStream mInputStream;
	private InputStreamReader mInputStreamReader;
	private Socket mSocket;
	private BufferedReader mBufferedReader;
	private String mPassword;

	public TcpClient(int port, String password) throws IOException {
		this.port = port;
		this.mPassword = password;
		mSocket = new Socket(IP, port);
		mOutputStream = mSocket.getOutputStream();
		mInputStream = mSocket.getInputStream();
	}

	public boolean confirmPassword() {
		try {
			mOutputStream.write(mPassword.getBytes("utf-8"));
			mBufferedReader = new BufferedReader(mInputStreamReader);
			String meString = mBufferedReader.readLine();
			return meString.equalsIgnoreCase("OK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				mBufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}

	}


	public InputStream getInputStream() {
		return mInputStream;
	}

	public OutputStream getOutputStream() {
		return mOutputStream;
	}

	public void write(String msg) throws IOException {
		mOutputStream.write(msg.getBytes("utf-8"));
	}

	public String read() throws IOException {

		return mBufferedReader.readLine();
	}

}
