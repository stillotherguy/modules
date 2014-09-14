package protocol;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * Created by zhangjing on 14-9-14.
 */
public class Protocol {
    private Header header;
    private Object body;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString(){
        return MoreObjects.toStringHelper(getClass()).add("header", header).add("body", body).toString();
    }
}
