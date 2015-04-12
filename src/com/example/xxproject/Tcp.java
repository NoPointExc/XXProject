package com.example.xxproject;
/**
 * 在Server设计下使用，在你的设计中没用
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class Tcp {
	private int port;

	private OutputStream mOutputStream;
	private InputStream mInputStream;
	private InputStreamReader mInputStreamReader;

	private Socket mSocket;
	private ServerSocket mServerSocket;
	private BufferedReader mBufferedReader;
	private String mPassword;

	public Tcp(int port, String password) throws IOException {
		this.port = port;
		this.mPassword=password;
		mServerSocket = new ServerSocket(this.port);
	}

	public boolean accept() throws IOException {
		mSocket = mServerSocket.accept();
		mOutputStream = mSocket.getOutputStream();
		mOutputStream.write(mPassword.getBytes("utf-8"));
		mInputStream = mSocket.getInputStream();
		mInputStreamReader = new InputStreamReader(mInputStream);
		mBufferedReader = new BufferedReader(mInputStreamReader);
		String meString = mBufferedReader.readLine();
		return meString.equalsIgnoreCase("OK");
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
