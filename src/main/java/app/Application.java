package app;

import java.util.List;

import app.resources.user.User;
import app.resources.utils.ConfigurationReader;
import app.resources.utils.Utils;

public class Application {
	public static void main(String[] args) {
		Utils utils = new Utils();
		List<User> users = utils.readCSV(ConfigurationReader.getProperty("file_path"));
		utils.findRelatedPeople(users);
	}
}
