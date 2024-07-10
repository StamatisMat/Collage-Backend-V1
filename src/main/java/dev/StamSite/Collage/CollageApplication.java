package dev.StamSite.Collage;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@RestController
public class CollageApplication {
	@Autowired
	private CollageController collageController;

	public static void main(String[] args) {
		SpringApplication.run(CollageApplication.class, args);
	}

}
