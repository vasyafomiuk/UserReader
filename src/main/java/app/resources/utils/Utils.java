package app.resources.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import app.resources.user.User;

public class Utils {
	private static final Logger logger = LogManager.getLogger();
	private String pathFile = ConfigurationReader.getProperty("file_path");

	// this method will filter data and create list of users with 2 params only
	public List<User> parseData() {
		ArrayList<User> values = new ArrayList<>();
		try {
			int i = 0;
			for (String[] row : readAllDataAtOnce()) {
				logger.debug("Row " + ++i + " : " + Arrays.toString(row));
				if (isValidName(row[0]) && isValidName(row[1]) && verifyRowIsValid(row)
						&& isValidEmailAddress(row[9])) {
					values.add(new User(row[0].trim(), row[1].trim()));
				} else {
					logger.error(String.format("Row %d has invalid data: %s", i, Arrays.toString(row)));
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}
		return values;
	}

	// this method will verify if no one exceeds 256 characters limit
	public boolean verifyRowIsValid(String[] arr) {
		boolean match = Arrays.asList(arr).stream().allMatch(p -> p.length() <= 256);
		if (match) {
			return true;
		}
		logger.error("Unable to proccess row. Some values are too long.");
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
				String output = u.toString() + " : ";
				for (User u2 : data) {
					// compare 2 users, and if it's no the same user in order to avoid duplicates
					if ((!u.equals(u2)) && u.isRelated(u2)) {
						output += u2.toString() + ", ";
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

	public List<String[]> readAllDataAtOnce() {
		List<String[]> allData = new ArrayList<>();
		try {
			// Create an object of file reader
			// class with CSV file as a parameter.
			FileReader filereader = new FileReader(pathFile);

			// create csvReader object and skip first Line
			CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
			allData = csvReader.readAll();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}
		return allData;
	}

}
