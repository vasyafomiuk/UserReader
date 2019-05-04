package app;

import java.util.List;
import java.util.Scanner;

import app.resources.user.User;
import app.resources.utils.Utils;

public class Application {
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		Utils utils = new Utils();
		utils.displayOption();
		utils.setPathToTheFile(scan);
		List<User> users = utils.parseData();
		utils.findRelatedPeople(users);
		utils.displayFinalMessage();
	}
}
