package socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import jdbc.Mysql;

public class SocketServer {

	public Mysql mysql = new Mysql();

	public OnConnectListener mConnectListener;

	BufferedWriter writer = null;

	public void startServer() {
		ServerSocket serverSocket = null;
		Socket socket = null;
		try {
			serverSocket = new ServerSocket(9898);
			System.out.println("server started..");
			if (mConnectListener != null) {
				mConnectListener.setConnect(true);
			}
			while (true) {
				System.out.println("while");
				socket = serverSocket.accept();
				manageConnection(socket);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (mConnectListener != null) {
				mConnectListener.setConnect(false);
			}
		} finally {
			try {
				socket.close();
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (mConnectListener != null) {
				mConnectListener.setConnect(false);
			}
		}
	}

	public void manageConnection(final Socket socket) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				BufferedReader reader = null;
				writer = null;
				try {
					System.out.println("client " + socket.hashCode() + " connedted");

					reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
					// 测试:心跳检测，测试服务器随时给客户端发送消息是否客户端可以收到 new
					new Timer().schedule(new TimerTask() {

						@Override
						public void run() { // TODO Auto-generated
							try {
								System.out.println("heart beat once ...");
								writer.write("heart\n");// 这里writer必须设成全局变量
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}, 3000); // 每隔3s发送一次

					String receivedMsg;
					while ((receivedMsg = reader.readLine()) != null) {
						System.out.println("client " + socket.hashCode() + ": " + receivedMsg);
						if (receivedMsg.equals("ok")) {
							if (mConnectListener != null) {
								mConnectListener.setConnect(true);
							}
						} else {
							try {
								mysql.addData(receivedMsg);
								System.out.println("数据添加成功");
								if (mConnectListener != null) {
									mConnectListener.setSendMessage(receivedMsg);
								}
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								System.out.println("数据添加失败");
							}

							writer.write("server reply: " + receivedMsg + "\n"); // 收到数据后发送回客户端
							writer.flush();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						reader.close();
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public void setOnConnectListener(OnConnectListener onConnectListener) {
		// TODO Auto-generated method stub
		this.mConnectListener = onConnectListener;
	}
}