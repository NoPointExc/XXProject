package com.example.xxproject;
/**
 * 在Server设计下使用，在你的设计中没用
 */
import java.io.IOException;

import android.R.integer;
import android.os.Handler;
import android.os.Message;

public class TcpThread extends Thread {

	private Tcp mTcp;
	private static Handler inHandler;
	// lable for handler
	private final int NOT_ACCEPTED = 0;
	private final int ACCEPTED=3;
	private final int IO_EXCEPTION = 1;
	private final int ON_RECEIVED = 2;

	public TcpThread(Tcp mTcp, Handler in) {
		this.mTcp = mTcp;
		this.inHandler = in;
	}

	@Override
	public void run() {
		Message message = new Message();
		try {
			
			// 1.try to accept a socket
			if (mTcp.accept()) {
				// socket accepted
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
