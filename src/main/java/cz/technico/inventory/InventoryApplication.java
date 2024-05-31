package cz.technico.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

import java.nio.file.Path;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@SpringBootApplication
public class InventoryApplication {
	public static final String CLIENT_URL = "http://localhost:3000";
	public static final String PRIMARY_EMAIL = "skrifix@seznam.cz";
	public static final int REFRESH_TOKEN_DURATION_SECONDS = (int) Duration.of(8, ChronoUnit.MINUTES).toSeconds();
	public static final int ACCESS_TOKEN_DURATION_SECONDS = (int) Duration.of(2, ChronoUnit.MINUTES).toSeconds();
	public static final int EMAIL_EXPIRATION_MINUTES = 45;
	public static final Path PROFILE_IMAGE_ROOT = Path.of(System.getProperty("user.dir"), "images", "profiles");
	public static final Path SPREADSHEET_ROOT = Path.of(System.getProperty("user.dir"), "spreadsheets");
	public static final String SPREADSHEET_DISCRIMINANT = "Práce";
	public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(cz.technico.inventory.InventoryApplication.class);
		springApplication.addListeners(new ApplicationPidFileWriter());
		springApplication.run(args);
	}

}
