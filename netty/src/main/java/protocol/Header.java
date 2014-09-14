package protocol;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Created by zhangjing on 14-9-14.
 */
public class Header {
    private int crcCode = 0xabef0101;
    private int length;
    private long sessionID;
    private byte type;
    private byte priority;
    private Map<String,Object> attachment = Maps.newHashMap();

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public long getSessionID() {
        return sessionID;
    }

    public void setSessionID(long sessionID) {
        this.sessionID = sessionID;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }

    public Map<String, Object> getAttachment() {
        return attachment;
    }

    public void setAttachment(Map<String, Object> attachment) {
        this.attachment = attachment;
    }

    public String toString(){
        return MoreObjects.toStringHelper(getClass())
                .add("crcCode", crcCode)
                .add("length", length)
                .add("sessionID", sessionID)
                .add("type", type)
                .add("priority", priority)
                .add("attachment", attachment)
                .toString();
    }

    public int getCrcCode() {
        return crcCode;
    }
}
