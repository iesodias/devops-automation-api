package br.com.java_api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.lang.management.ManagementFactory;

@Controller
public class StatusController {

    @GetMapping("/status")
    public String statusPage(Model model) {
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        model.addAttribute("uptime", uptime);
        model.addAttribute("status", "UP");
        return "status";
    }
}
