package mina;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class MyTextLineDecoder implements ProtocolDecoder {

	@Override
	public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out)
			throws Exception {
		int startPosition = in.position();	//开始读取的位置
		while (in.hasRemaining()) {	//当iobuffer中还有字节可以读取时
			byte b = in.get();	//读字节
			if (b == '\n') {
				int currentPositoin = in.position();	//记录当前读取的位置
				int limit = in.limit();	//当前总长度
				in.position(startPosition);	//定向到起始位置
				in.limit(currentPositoin);	//终点为当前position
				IoBuffer buf = in.slice();	//slice截取
				byte [] dest = new byte[buf.limit()];
				buf.get(dest);
				String str = new String(dest);
				out.write(str);
				in.position(currentPositoin);	//还原到当前position
				in.limit(limit);	//还原到原来的limit
			}
		}
	}

	@Override
	public void dispose(IoSession arg0) throws Exception {

	}

	@Override
	public void finishDecode(IoSession arg0, ProtocolDecoderOutput arg1)
			throws Exception {

	}

}
