package com.example.iyunga_tech;

import java.net.URI;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.iyunga_tech.Entity.User;
import com.example.iyunga_tech.Repository.UserRepository;

@SpringBootApplication
public class IyungaTechApplication {

	public static void main(String[] args) {
		applyDatabaseUrl();
		SpringApplication.run(IyungaTechApplication.class, args);
	}

	private static void applyDatabaseUrl() {
		String raw = System.getenv("DATABASE_URL");
		if (raw == null || raw.isBlank() || System.getenv("DB_URL") != null) {
			return;
		}
		try {
			URI uri = new URI(raw.trim().replaceFirst("^postgres://", "postgresql://"));
			int port = uri.getPort() == -1 ? 5432 : uri.getPort();
			String query = uri.getQuery();
			if (query == null && uri.getHost().contains(".")) {
				query = "sslmode=require";
			}
			String jdbc = "jdbc:postgresql://" + uri.getHost() + ":" + port + uri.getPath()
					+ (query == null ? "" : "?" + query);
			System.setProperty("DB_URL", jdbc);
			if (uri.getUserInfo() != null) {
				String[] info = uri.getUserInfo().split(":", 2);
				System.setProperty("DB_USERNAME", info[0]);
				if (info.length > 1) {
					System.setProperty("DB_PASSWORD", info[1]);
				}
			}
		} catch (Exception e) {
			System.err.println("DATABASE_URL could not be read: " + e.getMessage());
		}
	}

	@Bean
	CommandLineRunner seedFirstStudent(UserRepository repo) {
		return args -> {
			if (repo.count() == 0) {
				User student = new User();
				student.setFirstName("Daud");
				student.setSecondName("Kisulo");
				student.setEmail("daudki044@gmail.com");
				student.setLevel("Form 6");
				repo.save(student);
			}
		};
	}
}
