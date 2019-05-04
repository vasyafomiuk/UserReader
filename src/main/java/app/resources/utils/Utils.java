package app.resources.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.CsvToBeanFilter;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;

import app.resources.user.User;

public class Utils {
	private static final Logger logger = LogManager.getLogger();
	private String pathFile = ConfigurationReader.getProperty("file_path");

	// this method will verify if no one exceeds 256 characters limit
	public boolean verifyValueIsIsNotTooLong(String str) {
		if (str.length() < 256) {
			return true;
		}
		logger.error("Unable to proccess. Value is too long.");
		return false;
	}

	// this method verifies name pattern
	public boolean isValidName(String name) {
		boolean match = name.matches("^[A-Za-z ]+-??[ A-Za-z]+$");
		if (match) {
			return true;
		}
		logger.error(name + " is not valid name");
		return false;
	}

	// this method verifies if email address has valid format
	public boolean isValidEmailAddress(String email) {
		boolean match = EmailValidator.getInstance().isValid(email);
		if (match) {
			return true;
		}
		logger.error(email + " is not valid email");
		return false;
	}

	// this method will find related people and will write result to the txt file.
	// output path is set from properties file
	public void findRelatedPeople(List<User> data) {
		File file = new File(ConfigurationReader.getProperty("output_path") + "output_" + LocalDateTime.now() + ".txt");
		try {
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			// create a new file if it doesn't exists
			if (!file.exists()) {
				file.createNewFile();
			}

			for (User u : data) {
				String output = u.getFullName() + " : ";
				for (User u2 : data) {
					// compare 2 users, and if it's no the same user in order to avoid duplicates
					if ((!u.equals(u2)) && u.isRelated(u2)) {
						output += u2.getFullName() + ", ";
					}

				}
				output = output.trim().replaceAll(",$", "");
				output += "\n";
				bw.write(output);
			}
			bw.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public void displayOption() {
		System.out.println("******************************************");
		System.out.println("*Plese select source for reading a file: *");
		System.out.println("* 1. Default (from properties file).     *");
		System.out.println("* 2. Other.			         *");
		System.out.println("******************************************");
	}

	public void displayFilePathMessage() {
		System.out.println("******************************************");
		System.out.println("* Please enter path to the file:		 *");
		System.out.println("******************************************");
	}

	public void displayFinalMessage() {
		System.out.println("******************************************");
		System.out.println("*    File was processed successfully!	 *");
		System.out.println("******************************************");
	}

	public void setPathToTheFile(Scanner scan) {
		String option = scan.nextLine();
		if (option.equalsIgnoreCase("other")) {
			displayFilePathMessage();
			pathFile = scan.nextLine();
		}
	}

	public List<User> readAllDataAtOnce() {
		List<User> allData = new ArrayList<>();
		try {
			// Create an object of file reader
			// class with CSV file as a parameter.
			FileReader filereader = new FileReader(pathFile);
			// set mapping strategy
			Map<String, String> mapping = new HashMap<String, String>();
			mapping.put("first_name", "firstName");
			mapping.put("last_name", "lastName");
			mapping.put("company_name", "companyName");
			mapping.put("address", "address");
			mapping.put("city", "city");
			mapping.put("province", "province");
			mapping.put("postal", "postal");
			mapping.put("phone1", "phone1");
			mapping.put("phone2", "phone2");
			mapping.put("email", "email");
			mapping.put("web", "web");
			HeaderColumnNameTranslateMappingStrategy<User> strategy = new HeaderColumnNameTranslateMappingStrategy<User>();
			strategy.setType(User.class);
			strategy.setColumnMapping(mapping);
			// Parse one row at a time
			final CsvToBean<User> csv = new CsvToBeanBuilder<User>(filereader).withType(User.class)
		            .withOrderedResults(false)
		            .withMappingStrategy(strategy)
		            .build();
			allData = csv.parse();
			allData.removeIf(p -> p.isInvalidUser());
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}
		return allData;
	}

}
