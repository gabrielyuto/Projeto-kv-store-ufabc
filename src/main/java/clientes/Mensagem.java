package clientes;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Mensagem implements Serializable {
    private String request;
    private String key;
    private String value;
    private String status;
    private LocalDateTime timestampServer;
    private LocalDateTime timestampClient;
    private String table;
    private String replication;
    private String ipClient;
    private String ipServerClientRequest;
    private String ipServerMaster;
    private String ipServerOthers;
    private int portClient;
    private int portServerClientRequest;
    private int portServerMaster;
    private int portServerOthers;

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

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getReplication() {
        return replication;
    }

    public void setReplication(String replication) {
        this.replication = replication;
    }

    public String getIpClient() {
        return ipClient;
    }

    public void setIpClient(String ipClient) {
        this.ipClient = ipClient;
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

    public String getIpServerOthers() {
        return ipServerOthers;
    }

    public void setIpServerOthers(String ipServerOthers) {
        this.ipServerOthers = ipServerOthers;
    }

    public int getPortClient() {
        return portClient;
    }

    public void setPortClient(int portClient) {
        this.portClient = portClient;
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

    public int getPortServerOthers() {
        return portServerOthers;
    }

    public void setPortServerOthers(int portServerOthers) {
        this.portServerOthers = portServerOthers;
    }
}