package hello.typeconverter.type;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode //@EqualsAndHashCode 때문에 다른 IpPort 객체여도, ip랑 port 값이 서로 같으면 같은 객체라고 처리
public class IpPort {
    private String ip;
    private int port;

    public IpPort(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
