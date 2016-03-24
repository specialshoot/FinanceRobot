package mina;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import socket.OnConnectListener;

public class MyServerHandler extends IoHandlerAdapter {

	public OnMinaConnectListener mlistener;

	public void setOnMinaConnectListener(OnMinaConnectListener listener) {
		// TODO Auto-generated method stub
		this.mlistener = listener;
	}

	// 网络连接出现异常
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		System.out.println("exceptionCaught");
		if (mlistener != null) {
			mlistener.setConnect(false);
		}
	}

	// 收到消息进入此方法
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		String s =message.toString();
		System.out.println("messageReceived: " + s);
		if (mlistener != null) {
			mlistener.setSendMessage(s);
		}
		session.write(s); // 返回消息给客户端
	}

	// 发送消息会进入此方法
	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		System.out.println("messageSent");
	}

	// 客户端与服务器会话断开连接时进入此方法
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		System.out.println("sessionClosed");
		if (mlistener != null) {
			mlistener.setConnect(false);
		}
	}

	// 客户端与服务器会话创建时进入此方法
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		System.out.println("sessionCreated");
		if (mlistener != null) {
			mlistener.setConnect(true);
		}
	}

	// 会话进入空闲状态进入此方法
	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		// System.out.println("sessionIdle");
	}

	// 会话打开进入此方法
	@Override
	public void sessionOpened(IoSession session) throws Exception {
		System.out.println("sessionOpened");
	}

}