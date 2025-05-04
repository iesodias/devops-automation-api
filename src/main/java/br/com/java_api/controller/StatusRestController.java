package br.com.java_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.*;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class StatusRestController {

    @Operation(summary = "Status detalhado da API em JSON")
    @GetMapping("/api/status")
    public Map<String, Object> apiStatus() {
        Map<String, Object> status = new HashMap<>();

        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            String ip = InetAddress.getLocalHost().getHostAddress();
            String javaVersion = System.getProperty("java.version");
            String os = System.getProperty("os.name");

            long uptimeMs = ManagementFactory.getRuntimeMXBean().getUptime();
            long uptimeSec = uptimeMs / 1000;

            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            MemoryUsage heap = memoryBean.getHeapMemoryUsage();
            MemoryUsage nonHeap = memoryBean.getNonHeapMemoryUsage();

            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();

            String timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new Date());

            Map<String, Object> memory = new HashMap<>();
            memory.put("heap_used_MB", heap.getUsed() / (1024 * 1024));
            memory.put("heap_max_MB", heap.getMax() / (1024 * 1024));
            memory.put("non_heap_used_MB", nonHeap.getUsed() / (1024 * 1024));

            status.put("status", "UP");
            status.put("hostname", hostname);
            status.put("ip", ip);
            status.put("uptime_sec", uptimeSec);
            status.put("java_version", javaVersion);
            status.put("os", os);
            status.put("cpu_load_avg", osBean.getSystemLoadAverage());
            status.put("memory", memory);
            status.put("timestamp", timestamp);

        } catch (Exception e) {
            status.put("status", "ERROR");
            status.put("error", e.getMessage());
        }

        return status;
    }
}
