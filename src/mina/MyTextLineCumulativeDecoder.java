package mina;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

//MyTextLineCumulativeDecoder与MyTextLineDecoder差不多,这里返回一个boolean类型,用此Decoder可以防止数据丢失
public class MyTextLineCumulativeDecoder extends CumulativeProtocolDecoder {

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		int startPosition = in.position();
		while (in.hasRemaining()) {
			byte b = in.get();
			if (b == '\n') {
				int currentPositoin = in.position();
				int limit = in.limit();
				in.position(startPosition);
				in.limit(currentPositoin);
				IoBuffer buf = in.slice();
				byte [] dest = new byte[buf.limit()];
				buf.get(dest);
				String str = new String(dest);
				out.write(str);
				in.position(currentPositoin);
				in.limit(limit);
				return true;
			}
		}
		in.position(startPosition);	//如果没有\n换行符,保持position在初始位置
		return false;
	}

}
