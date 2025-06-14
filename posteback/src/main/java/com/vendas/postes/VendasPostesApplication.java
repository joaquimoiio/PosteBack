package com.vendas.postes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin(origins = "*")
public class VendasPostesApplication {

	public static void main(String[] args) {
		SpringApplication.run(VendasPostesApplication.class, args);
		System.out.println("🚀 Sistema de Vendas de Postes iniciado!");
		System.out.println("📱 Frontend: Abra o arquivo index.html no navegador");
		System.out.println("🔗 Backend API: http://localhost:8080/api");
	}
}