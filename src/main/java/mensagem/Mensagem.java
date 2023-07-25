package mensagem;

import java.io.Serializable;
import java.time.LocalDateTime;

//  Aqui temos a mensagem que trafega durante toda a comunicação entre cliente e servidores, junto aos métodos Getters e Setters dos atributos da classe.
public class Mensagem implements Serializable {
    private String request;
    private String key;
    private String value;
    private String status;
    private LocalDateTime timestampServer;
    private LocalDateTime timestampClient;
    private String ipFrom;
    private String ipServerClientRequest;
    private String ipServerMaster;
    private String ipServerOne;
    private String ipServerTwo;
    private int portFrom;
    private int portServerClientRequest;
    private int portServerMaster;
    private int portServerOne;
    private int portServerTwo;

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestampServer() {
        return timestampServer;
    }

    public void setTimestampServer(LocalDateTime timestampServer) {
        this.timestampServer = timestampServer;
    }

    public LocalDateTime getTimestampClient() {
        return timestampClient;
    }

    public void setTimestampClient(LocalDateTime timestampClient) {
        this.timestampClient = timestampClient;
    }

    public String getIpFrom() {
        return ipFrom;
    }

    public void setIpFrom(String ipFrom) {
        this.ipFrom = ipFrom;
    }

    public String getIpServerClientRequest() {
        return ipServerClientRequest;
    }

    public void setIpServerClientRequest(String ipServerClientRequest) {
        this.ipServerClientRequest = ipServerClientRequest;
    }

    public String getIpServerMaster() {
        return ipServerMaster;
    }

    public void setIpServerMaster(String ipServerMaster) {
        this.ipServerMaster = ipServerMaster;
    }

    public String getIpServerOne() {
        return ipServerOne;
    }

    public void setIpServerOne(String ipServerOne) {
        this.ipServerOne = ipServerOne;
    }

    public String getIpServerTwo() {
        return ipServerTwo;
    }

    public void setIpServerTwo(String ipServerTwo) {
        this.ipServerTwo = ipServerTwo;
    }

    public int getPortFrom() {
        return portFrom;
    }

    public void setPortFrom(int portFrom) {
        this.portFrom = portFrom;
    }

    public int getPortServerClientRequest() {
        return portServerClientRequest;
    }

    public void setPortServerClientRequest(int portServerClientRequest) {
        this.portServerClientRequest = portServerClientRequest;
    }

    public int getPortServerMaster() {
        return portServerMaster;
    }

    public void setPortServerMaster(int portServerMaster) {
        this.portServerMaster = portServerMaster;
    }

    public int getPortServerOne() {
        return portServerOne;
    }

    public void setPortServerOne(int portServerOne) {
        this.portServerOne = portServerOne;
    }

    public int getPortServerTwo() {
        return portServerTwo;
    }

    public void setPortServerTwo(int portServerTwo) {
        this.portServerTwo = portServerTwo;
    }
}