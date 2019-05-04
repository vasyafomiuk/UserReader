package app.resources.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import app.resources.user.User;

public class Utils {
	private static final Logger logger = LogManager.getLogger();
	private String pathFile = ConfigurationReader.getProperty("file_path");

	public List<User> readCSV() {
		ArrayList<User> values = new ArrayList<>();
		// provide a path to the file
		try (Scanner scan = new Scanner(new File(pathFile));) {
			int row = 0;
			while (scan.hasNextLine()) {
				List<String> list = Arrays.asList(scan.nextLine().replace("\"", "").split(","));
//				logger.debug(String.format("Reading  row %d: %s", row, list));
				System.out.println(list);
				if (isValidName(list.get(0)) 
					&& isValidName(list.get(1))
					&& verifyRowIsValid(list)
					&& isValidEmailAddress(list.get(9))
					) {
					//&& 
					values.add(new User(list.get(0), list.get(1)));
				} else {
					logger.debug(String.format("Row %d has invalid data: %s", row, list));
				}
				row++;
			}
		} catch (FileNotFoundException e) {
			logger.debug(e.getMessage());
			throw new RuntimeException(e);
		}
		return values;
	}

	public boolean verifyRowIsValid(List<String> list) {
		boolean match = list.stream().allMatch(p -> p.length() < 256);
		if(match) {
			return true;
		}
		logger.debug("Unable to proccess row. Some values are too long.");
		return false;
	}

	public boolean isValidName(String name) {
		boolean match = name.matches("^[A-Za-z]+-??[A-Za-z]+$");
		if(match) {
			return true;
		}
		logger.debug(name+" is not valid");
		return false;
	}

	public boolean isValidEmailAddress(String email) {
		boolean match = EmailValidator.getInstance().isValid(email);
		if(match) {
			return true;
		}
		logger.debug(email+" is not valid");
		return false;
	}

	public void findRelatedPeople(List<User> data) {
		File file = new File(ConfigurationReader.getProperty("output_path"));
		try {
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			if (!file.exists()) {
				file.createNewFile();
			}
			for (User u : data) {
				String output = u.toString() + " : ";
				for (User u2 : data) {
					if (u.compareTo(u2) != 0 && u.isRelated(u2)) {
						output += u2.toString() + ",";
					}

				}
				output = output.replaceAll(",$", "").trim();
				output+="\n";
				bw.write(output);
			}
			bw.close();
		} catch (IOException e) {
			logger.debug(e.getMessage());
			throw new RuntimeException(e);
		}
	}
	
	public void displayOption() {
		System.out.println("******************************************");
		System.out.println("*Plese select source for reading a file: *");
		System.out.println("* 1. Default (from properties file).     *");
		System.out.println("* 2. Other.							     *");
		System.out.println("******************************************");
	}
	
	public void displayFilePathMessage() {
		System.out.println("******************************************");
		System.out.println("* Please enter path to the file:		 *");
		System.out.println("******************************************");
	}
	
	public void setPathToTheFile(Scanner scan) {
		String option = scan.nextLine();
		if(option.equalsIgnoreCase("other")) {
			displayFilePathMessage();
			pathFile = scan.nextLine();
		}
	}
}
