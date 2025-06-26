package com.vendas.postes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin(origins = "*")
public class VendasPostesApplication {

	public static void main(String[] args) {
		try {
			SpringApplication.run(VendasPostesApplication.class, args);
		} catch (Exception e) {
			System.err.println("❌ Erro ao iniciar aplicação: " + e.getMessage());
			e.printStackTrace();
		}
	}
}