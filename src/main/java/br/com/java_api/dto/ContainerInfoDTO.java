package br.com.java_api.dto;

public class ContainerInfoDTO {
    public String containerName;
    public String ipAddress;
    public String osName;
    public String appVersion;
    public long uptimeMillis;

    public ContainerInfoDTO (String containerName, String ipAddress, String osName, String appVersion, long uptimeMillis) {
        this.containerName = containerName;
        this.ipAddress = ipAddress;
        this.osName = osName;
        this.appVersion = appVersion;
        this.uptimeMillis = uptimeMillis;
    }
}
