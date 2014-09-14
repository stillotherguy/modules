package codec;

import io.netty.buffer.ByteBuf;

import javax.xml.bind.Marshaller;

/**
 * Created by zhangjing on 14-9-14.
 */
public class MarshallingEncoder {
    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];
    Marshaller marshaller;

    public MarshallingEncoder(){
        //marshaller = MarshallingCodecFactory.buildMarshalling();
    }

    protected void encode(Object msg, ByteBuf out) throws Exception{
        try{
            int lengthPos = out.writerIndex();

        }finally {

        }
    }
}
