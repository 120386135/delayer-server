package client;
import com.menkor.BinaryUtil;
import com.alibaba.fastjson.JSON;
/**
 * Created by zp on 17/5/8.
 */
public class Stub {
    private String method;
    private String param;

    public Stub(){

    }

    public Stub(String method, String param) {
        this.method = method;
        this.param = param;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public static byte[] toBytes(String method, String param){
        return BinaryUtil.toBytes(JSON.toJSONString(new Stub(method,param)));
    }

    public static Stub fromBytes(byte[] bytes){
        return JSON.parseObject(BinaryUtil.toString(bytes),Stub.class);
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
