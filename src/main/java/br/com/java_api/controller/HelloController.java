package br.com.java_api.controller;

import br.com.java_api.dto.ContainerInfoDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.nio.file.Files;

@RestController
public class HelloController {

    @Operation(summary = "Hello Basico")
    @GetMapping("/api/hello")
    public String hello() {
        return "Ola, Mundo da API!";
    }

    @Operation(summary = "Retorna o nome (Hostname) do container")
    @GetMapping("/api/getContainerName")
    public String getContainerName() {
        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            return "Container name (hostname): " + hostname;
        } catch (Exception e) {
            return "Erro ao obter o nome do container" + e.getMessage();
        }
    }

    @Operation(summary = "Exibe informações gerais do container")
    @GetMapping("/api/info")
    public ContainerInfoDTO getContainerInfo() {
        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            String ip = InetAddress.getLocalHost().getHostAddress();
            String os = System.getProperty("os.name");
            String version = "1.0.0";
            long uptime = ManagementFactory.getRuntimeMXBean().getUptime();

            return new ContainerInfoDTO(hostname, ip, os, version, uptime);
        } catch (Exception e) {
            return new ContainerInfoDTO("unknown", "unknown", "unknown", "unknown", 0);
        }
    }


    @Operation(summary = "Retorna JSON estático do resources/static")
    @GetMapping(value = "/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getJsonFromStatic() {
        try {
            ClassPathResource resource = new ClassPathResource("static/device.json");
            String json = new String(resource.getInputStream().readAllBytes());
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("{\"error\": \"Erro ao carregar JSON: " + e.getMessage() + "\"}");
        }
    }

}