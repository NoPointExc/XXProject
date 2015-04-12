package com.example.xxproject;

import java.io.IOException;

import android.os.Handler;
import android.os.Message;

public class TcpClientThread extends Thread {

	private TcpClient mTcp;
	private static Handler inHandler;
	// lable for handler
	private final int NOT_ACCEPTED = 0;
	private final int ACCEPTED=3;
	private final int IO_EXCEPTION = 1;
	private final int ON_RECEIVED = 2;

	public TcpClientThread(TcpClient mTcpClient, Handler in) {
		this.mTcp = mTcpClient;
		this.inHandler = in;
	}

	@Override
	public void run() {
		Message message = new Message();
		try {
			
			// 1.try to accept a socket
			if (mTcp.confirmPassword()) {
				// password is confirmed
				message.what = ACCEPTED;
				message.obj = "";
				// 2.read from socket
				String msg = null;
				msg = mTcp.read();
				if (msg != null) {
					// 读取消息，并发送给MainActivity

					message.what = ON_RECEIVED;
					message.obj = msg;

				} else {
					// socket not accepted or password wrong
					message.what = NOT_ACCEPTED;
					message.obj = "";
				}

				inHandler.sendMessage(message);
			}
		} catch (IOException e) {
			//
			message.what = IO_EXCEPTION;
			message.obj = "";
			inHandler.sendMessage(message);
			e.printStackTrace();
		}

	}

}
