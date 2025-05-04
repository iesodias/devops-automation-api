package br.com.java_api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Controller
public class CepPageController {

    @GetMapping("/cep")
    public String showForm() {
        return "cep"; // Exibe a página com o formulário
    }

    @PostMapping("/cep")
    public String buscarEndereco(@RequestParam String cep, Model model) {
        String url = "https://viacep.com.br/ws/" + cep + "/json/";
        RestTemplate restTemplate = new RestTemplate();

        try {
            Map response = restTemplate.getForObject(url, Map.class);
            model.addAttribute("endereco", response);
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao buscar o endereço: " + e.getMessage());
        }

        return "cep"; // Mesma página com resultado
    }
}
