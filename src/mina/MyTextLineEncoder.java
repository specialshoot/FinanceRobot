package mina;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class MyTextLineEncoder implements ProtocolEncoder {

	@Override
	public void dispose(IoSession arg0) throws Exception {

	}

	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out)
			throws Exception {
		String s =null;
		if (message instanceof String) {
			//判断是否message是否为字符串
			s = (String) message;
		}
		if (s != null) {
			//转码操作
			CharsetEncoder charsetEndoer = (CharsetEncoder)session.getAttribute("encoder");
			if (charsetEndoer == null) {
				charsetEndoer = Charset.defaultCharset().newEncoder();
				session.setAttribute("encoder", charsetEndoer);
			}
			IoBuffer ioBuffer = IoBuffer.allocate(s.length());	//开辟内存
			ioBuffer.setAutoExpand(true);	//设置内存自动扩展
			ioBuffer.putString(s, charsetEndoer);	//将字符串放入ioBuffer中
			ioBuffer.flip();
			out.write(ioBuffer);
		}
	}

}
