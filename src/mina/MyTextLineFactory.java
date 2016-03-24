package mina;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class MyTextLineFactory implements ProtocolCodecFactory {
	
//	private MyTextLineDecoder mDecoder;
	
	private MyTextLineCumulativeDecoder mCumulativeDecoder;
	
	private MyTextLineEncoder mEncoder;
	
	public MyTextLineFactory () {
//		mDecoder = new MyTextLineDecoder();
		mEncoder = new MyTextLineEncoder();
		mCumulativeDecoder = new MyTextLineCumulativeDecoder();
	}

	//解密
	@Override
	public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
		return mCumulativeDecoder;
		//return mDecoder;
	}

	//加密
	@Override
	public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
		return mEncoder;
	}

}