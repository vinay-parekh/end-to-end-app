package com.example.end2endapp.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public String getClient(Model model) {
        model.addAttribute("clients", clientService.getAllClients());
        return "clients";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Optional<Client> client = clientService.findById(id);
        model.addAttribute("client", client.get());
        return "update-client";
    }

    @PostMapping("/update/{id}")
    public String updateClient(@PathVariable("id") Long id, Client client) {
        clientService.updateClient(id, client.getFirstName(), client.getLastName(), client.getEmail());
        return "redirect:/clients?update_success";
    }

    @GetMapping("/delete/{id}")
    public String deleteClient(@PathVariable("id") Long id) {
        clientService.deleteClient(id);
        return "redirect:/clients?delete_success";
    }


}
