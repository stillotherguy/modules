package bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by zhangjing on 14-9-14.
 */
public class RandomAccess {
    @Test
    public void testRandomWrite(){
        ByteBuf b = Unpooled.buffer();
        b.setByte(100,12);
        Assert.assertEquals(b.writerIndex(), 0);
        Assert.assertEquals(b.getByte(100), 12);
    }

    @Test
    public void testCopy(){

    }
}
