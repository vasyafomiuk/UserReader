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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import app.resources.user.User;

public class Utils {
	public static final Logger logger = LogManager.getLogger();

	public List<User> readCSV(String filePath) {
		ArrayList<User> values = new ArrayList<>();
		// provide a path to the file
		try (Scanner scan = new Scanner(new File(filePath));) {
			int row = 0;
			while (scan.hasNextLine()) {
				List<String> list = Arrays.asList(scan.nextLine().replace("\"", "").split(","));
				logger.debug(String.format("Reading  row %d: %s", row, list));
				if (isValidName(list.get(0)) && isValidName(list.get(1)) && verifyRowIsValid(list)
						&& isValidName(list.get(0)) && isValidName(list.get(1))) {
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
		return list.stream().allMatch(p -> p.length() < 256);
	}

	public boolean isValidName(String name) {
		return name.matches("^[A-Za-z]+-??[A-Za-z]+$");
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
					if (u.compareTo(u2) != 0) {
						output += u2.toString() + ", ";
					}

				}
				output = output.replaceAll(",$", "").trim();
				bw.write(output);
			}

			bw.close();
		} catch (IOException e) {
			logger.debug(e.getMessage());
			throw new RuntimeException(e);
		}
	}
}
