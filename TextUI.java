import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * A class for the textual user interface for the user to use the system
 */
public class TextUI {
	private Scanner in;
	private BookingSystem system;
	private User user;
	private String hotelName;

	/**
	 * Initializes the Text UI by creating a new instance of scanner and starting
	 * the Booking System
	 */
	public TextUI() {
		in = new Scanner(System.in);
		this.system = new BookingSystem();
	}

	/*
	 * Checks if the date supplied is in the form dd/mm/yyyy
	 * 
	 * @param dateInput date the date to be checked
	 * 
	 * @return if the date is of the correct format
	 */
	private boolean isCorrectDateFormat(String dateInput) {
		String[] date = dateInput.split("/");
		if (date.length != 3 || dateInput.indexOf("/") == -1) {
			return false;
		} else if (Integer.parseInt(date[1]) > 12) { // If the mm value is greater than twelve, it may have been
														// confused to be the dd value
			return false;
		} else {
			int[] requiredLengths = { 2, 2, 4 };
			for (int i = 0; i < date.length; i++) {
				if (date[i].length() != requiredLengths[i]) {
					return false;
				}
			}
			return true;
		}
	}

	/*
	 * Returns a date selected by the user
	 * 
	 * @param futureDate if true, the user will only be allowed choose dates in the
	 * future
	 * 
	 * @return the date chosen
	 */
	private LocalDate getDate(boolean futureDate) {
		while (true) {
			String dateInput = in.nextLine();
			if (!isCorrectDateFormat(dateInput)) {
				System.out.println("Date is in incorrect format, please try again: ");
			} else {
				DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				LocalDate dateChosen = LocalDate.parse(dateInput, dateFormat);
				if (!dateChosen.isAfter(LocalDate.now()) && futureDate) {
					System.out.println("Please choose a date that is in the future: ");
				} else {
					return dateChosen;
				}
			}
		}
	}

	/*
	 * Allows a supervisor to choose the start and end dates for the data analysis
	 * functions
	 * 
	 * @return an array of the 2 LocalDate objects
	 */
	private LocalDate[] getDatesForDataAnalysis() {
		LocalDate[] dates = new LocalDate[2];
		System.out.println("Please enter the start date(dd/mm/yy): ");
		LocalDate d1 = getDate(false); // allow previous dates also as may want to do analysis on historical data
		System.out.println("Please enter the end date(dd/mm/yy): ");
		LocalDate d2 = getDate(false);

		if (d1.isAfter(d2)) {
			LocalDate temp = LocalDate.of(d1.getYear(), d1.getMonthValue(), d1.getDayOfMonth());
			d1 = LocalDate.of(d2.getYear(), d2.getMonthValue(), d2.getDayOfMonth());
			d2 = LocalDate.of(temp.getYear(), temp.getMonthValue(), temp.getDayOfMonth());
		}

		dates[0] = d1;
		dates[1] = d2;
		return dates;
	}

	/*
	 * Provides the interface to the user to choose the rooms for a reservation
	 * being created
	 * 
	 * @param numberOfRooms the number of rooms the user wants to reserve
	 * 
	 * @param from the date from when the reservation starts
	 * 
	 * @param to the date at which the reservation ends
	 * 
	 * @return an ArrayList of room bookings for the rooms selected
	 */
	private ArrayList<RoomBooking> chooseRooms(int numberOfRooms, LocalDate from, LocalDate to) {
		TreeMap<Room, Integer> map = new TreeMap<Room, Integer>(this.system.getCurrentRooms(this.hotelName, from, to));
		ArrayList<Room> allRooms = new ArrayList<Room>(map.keySet());
		ArrayList<RoomBooking> rooms = new ArrayList<RoomBooking>(numberOfRooms);
		System.out.println("Room type");
		while (rooms.size() != numberOfRooms) {
			char ch = 'A';
			ArrayList<Character> notAvailable = new ArrayList<Character>(5); // Keeps track of rooms that are not
																				// available i.e. their rooms available
																				// count is 0
			for (int i = 0; i < allRooms.size(); i++) {
				Room r = allRooms.get(i);
				if (map.get(r) > 0) {
					System.out.println(ch + ")" + r.getType() + " " + map.get(r)
							+ " available. Maximum adult occupancy: " + r.occupancy(true, false)
							+ ". Maximum children occupancy: " + r.occupancy(false, false) + ".");
				} else {
					notAvailable.add(ch);
					System.out.println(ch + ")" + r.getType() + " is booked out for these dates");
				}
				ch++;
			} // This is not how it will be implemented, just until the other methods are
				// developed
			String input = in.nextLine();
			while (input.equals("") || input.equals(" ")) {
				System.out.println("Please enter your choice: ");
				input = in.nextLine();
			}
			if (notAvailable.contains(input.toUpperCase().charAt(0))) { // If notAvailable contains the choice, the user
																		// tried to choose a room that has been booked
																		// out
				System.out.println("You cannot choose this room, as it is booked out. Please choose another.");
			} else {
				int n = input.toUpperCase().charAt(0) - 'A';
				System.out.println("Please enter how many adults and children are staying per room (number of Adults,number of Children): ");
				input = in.nextLine();
				while (input.equals("") || input.equals(" ") || input.split(",").length != 2) {
					System.out.println("Please enter your choice: ");
					input = in.nextLine();
				}
				String[] occupancy = input.split(",");
				if (occupancy.length < 2) {
					System.out.println("Please enter occupancy as: number of adults,number of children (with the comma)");
				} else {
					try {
						int adults = Integer.parseInt(occupancy[0]);
						int children = Integer.parseInt(occupancy[1]);
						if (n >= 0 && n < allRooms.size()) {
							Room choice = allRooms.get(n);
							map.put(choice, map.get(choice) - 1); // decrement as this room was chosen
							if ((adults >= choice.occupancy(true, true) && adults <= choice.occupancy(true, false))
									&& (children >= choice.occupancy(false, true)
											&& children <= choice.occupancy(false, false))) {
								boolean run = true;
								while (run) {
									System.out.println("Would you like breakfast included with the room? (Yes/No)");
									String option = in.nextLine();
									option = option.substring(0, 1).toUpperCase() + option.substring(1).toLowerCase();
									RoomBooking booking = new RoomBooking(choice, adults, children);
									if (option.equals("Yes")) {
										booking.setBreakfastIncluded(true);
										rooms.add(booking);
										run = false;
									} else if (option.equals("No")) {
										booking.setBreakfastIncluded(false);
										rooms.add(booking);
										run = false;
									} else {
										System.out.println("Please choose yes/no");
									}
								}
							}
						} else {
							System.out.println("The room chosen is not suitable for the number of adults or children you entered, please try again");
						}
					} catch (NumberFormatException e) {
						System.out.println("You can only enter a number for the number of adults or number of children");
					}
				}
			}
		}
		return rooms;
	}

	// Calculates the total cost of the proposed reservation
	private double totalCost(ArrayList<RoomBooking> rooms, LocalDate checkin, LocalDate checkout) {
		double totalCost = 0;
		double breakfastCost = 14.00;
		double calculatedBreakfast = 0;
		for (RoomBooking rb : rooms) {
			Room r = rb.getRoom();
			for (LocalDate date = checkin; !date.equals(checkout); date = date.plusDays(1)) {
				if (rb.isBreakfastIncluded()) {
					calculatedBreakfast += breakfastCost * (rb.getOccupancy()[0] + rb.getOccupancy()[1]);
				}
				int dayOfWeek = date.getDayOfWeek().getValue() - 1;
				totalCost += r.getRate(dayOfWeek);
			}
		}
		totalCost += calculatedBreakfast;
		return totalCost + 75.00;
	}

	private boolean confirmBooking(String hotelName, String name, String type, LocalDate checkinDate,
			int numberOfNights, int numberOfPeople, int numberOfRooms, ArrayList<RoomBooking> rooms) {
		type = type.equals("S") ? "Standard Booking" : "Advanced Purchase";
		System.out.println("Please confirm your reservation below: ");
		System.out.println("Hotel: L4-Hotels " + hotelName + "\nName: " + name + "\nType: " + type + "\nCheck-in Date: "
				+ checkinDate.toString() + "\nNumber of Nights: " + numberOfNights + "\nNumber of People: "
				+ numberOfPeople + "\nNumber of Rooms: " + numberOfRooms + "\nRooms: " + roomsAsString(rooms)
				+ "\nTotal Cost (incl. deposit): " + String.format("$%.02f",
						this.totalCost(rooms, checkinDate, checkinDate.plusDays(numberOfNights))));
		while (true) {
			System.out.println("Would you like to create your reservation or cancel it? (Create/Cancel)");
			String choice = in.nextLine().toUpperCase();
			while (choice.equals("") || choice.equals(" ")) {
				System.out.println("Please enter your choice: ");
				choice = in.nextLine();
			}
			if (choice.equals("CREATE")) {
				return true;
			} else if (choice.equals("CANCEL")) {
				return false;
			} else {
				System.out.println("Input not recognised, please try again");
			}
		}
	}

	private String roomsAsString(ArrayList<RoomBooking> rooms) {
		String rm = "\n";
		for (RoomBooking rb : rooms) {
			String breakIncluded = rb.isBreakfastIncluded() ? " Breakfast Included" : " Breakfast Not Included";
			rm += rb.getRoom().toString() + breakIncluded + "\n";
		}
		return rm;
	}

	/*
	 * Provides the user with the interface to create a reservation and all users
	 * can use this interface.
	 */
	private void makeReservation() {
		System.out.println("Do you want to make a S)tandard booking or A)dvanced Purchase?");
		String input = in.nextLine();
		while (input.equals("") || input.equals(" ")) {
			System.out.println("Please enter your choice: ");
			input = in.nextLine();
		}
		char choice = input.toUpperCase().charAt(0);
		String type;
		if (choice == 'S') {
			type = "S";
		} else {
			type = "AP";
		}
		LocalDate checkin = null;
		System.out.println("Please enter your check-in date(dd/mm/yyyy): ");
		checkin = getDate(true);
		System.out.println("Please enter the number of nights you wish to stay: ");
		try {
			input = in.nextLine();
			while (input.equals("") || input.equals(" ")) {
				System.out.println("Please enter the number of nights: ");
				input = in.nextLine();
			}
			int numNights = Integer.parseInt(input);
			System.out.println("Please enter the number of people staying: ");
			input = in.nextLine();
			while (input.equals("") || input.equals(" ")) {
				System.out.println("Please enter the number of people staying: ");
				input = in.nextLine();
			}
			int numPeople = Integer.parseInt(input);
			System.out.println("Please enter the number of rooms you wish to book: ");
			input = in.nextLine();
			while (input.equals("") || input.equals(" ")) {
				System.out.println("Please enter the number of rooms: ");
				input = in.nextLine();
			}
			int numRooms = Integer.parseInt(input);
			ArrayList<RoomBooking> rooms = chooseRooms(numRooms, checkin, checkin.plusDays(numNights));
			if (this.confirmBooking(this.hotelName, this.user.name, type, checkin, numNights, numPeople, numRooms,rooms)) {
				boolean created = this.user.createReservation(this.hotelName, this.user.name, type, checkin, numNights,numPeople, numRooms, rooms);
				if (created) {
					System.out.println("Reservation created successfully");
				} else {
					System.out.println("The reservation was unsuccessful");
				}
			} else {
				System.out.println("Reservation cancelled");
			}
		} catch (NumberFormatException e) {
			System.out.println("You can only enter numbers for number of people or number of rooms");
			makeReservation();
		}
	}

	/*
	 * Allows any user to cancel a reservation provided that the reservation exists
	 * in the first place
	 */
	private void cancelReservation() {
		Reservation reservation = this.getReservation();
		if (reservation == null) {
			System.out.println("The reservation could not be found");
		} else {
			if (this.user.cancelReservation(hotelName, reservation)) {
				System.out.println("The booking was cancelled and the booking cost was refunded");
			} else {
				System.out.println("The booking was cancelled but the booking cost was not refunded as it was either cancelled in less than 24 hours before, or it was an Advance Purchase Reservation");
			}
		}
	}

	/*
	 * Retrieves reservations from the booking system when given the check-in date,
	 * booking number and customer id
	 * 
	 * @return
	 */
	private Reservation getReservation() {
		System.out.println("Please enter the checkIn date(dd/mm/yyyy): ");
		LocalDate checkIn = getDate(true);
		System.out.println("Please enter the booking number: ");
		try {
			int number = Integer.parseInt(in.nextLine()); // using in.nextInt() cause no next line errors
			return this.system.getReservation(this.hotelName, this.user.name, checkIn, number);
		} catch (NumberFormatException e) {
			System.out.println("The booking number provided was not a number");
		}
		return null;
	}

	/*
	 * Provides the user with an interface to view booking details, for example the
	 * rooms booked etc
	 */
	private void viewReservation() {
		Reservation reservation = this.getReservation();
		if (reservation == null) {
			System.out.println("The reservation could not be found");
		} else {
			System.out.println("Hotel: " + "L4-Hotels " + this.hotelName);
			System.out.println(reservation.format());
		}
	}

	/*
	 * Provides the interface to the user to check in a customer
	 */
	private void checkInUser() {
		// call getTotalCostCalculated() for a reservation either here or at checkout
		System.out.println("Please enter the check-in date(dd/mm/yyyy): "); //leave as LocalDate.now()??
		LocalDate checkin = getDate(true);
		System.out.println("Please choose a reservation to check in: ");
		Reservation r = (Reservation)this.getChoice(this.system.reservationsOnDate(this.hotelName, checkin, true));
		if (r != null) {
			if (this.user instanceof DeskClerk) {
				DeskClerk clerk = (DeskClerk) this.user;
				if (clerk.checkIn(r)) {
					System.out.println("Booking #" + r.getNumber() + " checked in successfully");
					} else {
					System.out.println("Booking #" + r.getNumber() + " was not checked in successfully");
					}
			}
		}
	}

	/*
	 * Provides the interface for checking out a user
	 */
	private void checkOutUser() {
     // may replace with LocalDate.now() as if this was a checkout it would be current time. Same with checkin and check is it the right date for the checkout to occur
		System.out.println("Please enter check-out date(dd/mm/yyyy): ");
		LocalDate checkout = getDate(true);
		System.out.println("Please choose a reservation to check out: ");
		Reservation r = (Reservation)this.getChoice(this.system.reservationsOnDate(this.hotelName, checkout, false));
		if (r != null) {
			if (this.user instanceof DeskClerk) {
				DeskClerk clerk = (DeskClerk) this.user;
				if (clerk.checkOut(r)) {
					System.out.println("Booking #" + r.getNumber() + " checked out");
				} else {
					System.out.println("Booking #" + r.getNumber() + " could not be checked out");
				}
			}
		}
	}

	/*
	 * Provides the user with the option to use the check in or checkout services
	 */
	private void checkinServices() {
		System.out.println("Would you like to A)Check In or B)Check Out?");
		String input = in.nextLine();
		while (input.equals("") || input.equals(" ")) {
			System.out.println("Please enter your choice: ");
			input = in.nextLine();
		}
		char choice = input.toUpperCase().charAt(0);
		if (choice == 'A') {
			checkInUser();
		} else if (choice == 'B') {
			checkOutUser();
		}
	}

	/*
	 * Provides the interface for a supervisor to apply a discount to a reservation
	 */
	private void applyDiscountToReservation() {
		System.out.println("Please enter the customer name: ");
		String input = in.nextLine();
		while (input.equals("") || input.equals(" ")) {
			System.out.println("Please enter your a name: ");
			input = in.nextLine();
		}
		this.user.name = input;
		Reservation reservation = this.getReservation();
		if (reservation == null) {
			System.out.println("The reservation could not be found and a discount cannot applied");
		} else {
			try {
				System.out.println("Please enter the percentage discount (0-100): ");
				double percentageDiscount = Double.parseDouble(in.nextLine());
				Supervisor temp = (Supervisor) this.user;
				if (temp.applyDiscount(percentageDiscount, reservation.getNumber())) {
					System.out.println("The discount was applied successfully");
				}
			} catch (NumberFormatException e) {
				System.out.println("The percentage provided was not a number, discount was not applied");
			}
		}
	}

	/*
	 * Provides the interface for the supervisor to access the data analytics
	 */
	private void dataAnalyticsServices() {
		Supervisor temp = (Supervisor) this.user;
		System.out.println("Would you like to \n1) access billing analysis" + "\n2) access occupancy analysis");
		String input = in.nextLine();
		while (input.equals("") || input.equals(" ")) {
			System.out.println("Please enter your choice: ");
			input = in.nextLine();
		}
		char analysisType = input.toUpperCase().charAt(0);
		if (analysisType == '1') {
			LocalDate[] dates = this.getDatesForDataAnalysis();
			System.out.println("Would you like to choose days over the period to only include?(Yes/No)");
			String choice = in.nextLine().toUpperCase();
			String fileName;
			if (choice.equals("YES")) {
				System.out.println("Please choose from the list of days: ");
				ArrayList<LocalDate> days = this.getDaysChoice(dates[0], dates[1]);
				fileName = temp.requestRoomIncomeInformation(dates[0], dates[1], days);
				System.out.println("Information saved to: " + fileName);
			} else if (choice.equals("NO")) {
				fileName = temp.requestRoomIncomeInformation(dates[0], dates[1]);
				System.out.println("Information saved to: " + fileName);
			} else {
				System.out.println("Input not recognised, analysis not saved to file");
			}
		} else if (analysisType == '2') {
			LocalDate[] dates = this.getDatesForDataAnalysis();
			System.out.println("Would you like to choose days over the period to only include?(Yes/No)");
			input = in.nextLine();
			while (input.equals("") || input.equals(" ")) {
				System.out.println("Please enter your choice: ");
				input = in.nextLine();
			}
			String choice = input.toUpperCase();
			System.out.println("Would you like A)occupant numbers information or B)room count information?");
			boolean numbers = true;
			TreeMap<Room, Integer> hotelRooms = null;
			input = in.nextLine();
			while (input.equals("") || input.equals(" ")) {
				System.out.println("Please enter your choice: ");
				input = in.nextLine();
			}
			char choice1 = input.toUpperCase().charAt(0);
			if (choice1 == 'A') {
				numbers = true;
				hotelRooms = null;
			} else if (choice1 == 'B') {
				numbers = false;
				hotelRooms = this.system.getAllRooms().get(this.hotelName);
			}
			String fileName;
			if (choice.equals("YES")) {
				System.out.println("Please choose from the list of days: ");
				ArrayList<LocalDate> days = this.getDaysChoice(dates[0], dates[1]);
				fileName = temp.requestRoomOccupantsInformation(dates[0], dates[1], hotelRooms, numbers, days);
				System.out.println("Information saved to: " + fileName);
			} else if (choice.equals("NO")) {
				fileName = temp.requestRoomOccupantsInformation(dates[0], dates[1], hotelRooms, numbers);
				System.out.println("Information saved to: " + fileName);
			} else {
				System.out.println("Input not recognised, analysis not saved to file");
			}
		}
	}

	/*
	 * Provides the interface visible to the customer
	 */
	private void runAsCustomer() {
		boolean loggedIn = true;
		System.out.println("Please enter your name: ");
		String name = in.nextLine();
		while (name.equals("") || name.equals(" ")) {
			System.out.println("Please enter your name: ");
			name = in.nextLine();
		}
		this.user = new Customer(name, this.hotelName, system);
		while (loggedIn) {
			System.out.println("\nHotel: " + this.hotelName);
			System.out.println(
					"\nWould you like to M)ake a reservation, C(ancel a reservation, V)iew a reservation or L)ogout?");
			String input = in.nextLine();
			while (input.equals("") || input.equals(" ")) {
				System.out.println("Please enter your choice: ");
				input = in.nextLine();
			}
			char command = input.toUpperCase().charAt(0);
			if (command == 'M') {
				makeReservation();
			} else if (command == 'C') {
				cancelReservation();
			} else if (command == 'V') {
				viewReservation();
			} else if (command == 'L') {
				loggedIn = false;
			}
		}
	}

	/*
	 * Provides the interface visible to the desk clerk
	 */
	private void runAsDeskClerk() {
		String password = "deskAdmin";
		System.out.println("Enter Password: ");
		boolean run = true;
		while (run) {
			boolean loggedIn = false;
			String input = in.nextLine();
			while (input.equals("")) {
				System.out.println("Please enter a password: ");
				password = in.nextLine();
			}
			if (input.equals(password)) {
				System.out.println("Correct Password! Welcome!");
				loggedIn = true;
				run = false;
			} else if (input.equals("C") || input.equals("c")) {
				run = false;
			} else {
				System.out.println("Incorrect Password. Please Try Again or Cancel (C)");
			}
			this.user = new DeskClerk(hotelName, system);
			while (loggedIn) {
				System.out.println("\nHotel: " + this.hotelName);
				System.out.println("\nWould you like to access R)eservations, C)heck-in/out or L)ogout?");
				input = in.nextLine();
				while (input.equals("") || input.equals(" ")) {
					System.out.println("Please enter your choice: ");
					input = in.nextLine();
				}
				char command = input.toUpperCase().charAt(0);
				if (command == 'R') {
					System.out.println(
							"Would you like to M)ake a reservation, C(ancel a reservation or V)iew a reservation?");
					command = in.nextLine().toUpperCase().charAt(0);
					if (command == 'M') {
						System.out.println("Please enter the customer name: ");
						this.user.name = in.nextLine();
						makeReservation();
					} else if (command == 'C') {
						System.out.println("Please enter the customer name: ");
						this.user.name = in.nextLine();
						cancelReservation();
					} else if (command == 'V') {
						System.out.println("Please enter the customer name: ");
						this.user.name = in.nextLine();
						viewReservation();
					}
				} else if (command == 'C') {
					this.checkinServices();
				} else if (command == 'L') {
					loggedIn = false;
				}
			}
		}
	}

	/*
	 * Provides the interface visible to the supervisor
	 */
	private void runAsSupervisor() {
		boolean loggedIn = false;
		String password = "admin";
		boolean run = true;
		while (run) {
			System.out.println("Please enter your password: ");
			String input = in.nextLine();
			while (input.equals("")) {
				System.out.println("Please enter a password: ");
				input = in.nextLine();
			}
			if (password.equals(input)) {
				System.out.println("You have been logged in!");
				loggedIn = true;
				run = false;
			} else if (input.equals("C") || input.equals("c")) {
				run = false;
			} else {
				System.out.println("Password incorrect. Please try again or cancel(C)");
			}
		}
		this.user = new Supervisor(this.hotelName, system);
		while (loggedIn) {
			System.out.println("\nHotel: " + this.hotelName);

			System.out.println(
					"\nWould you like to access \n1)reservations \n2)check-in/out \n3)apply discounts \n4)data analytics or \n5)Logout");
			String input = in.nextLine();
			while (input.equals("") || input.equals(" ")) {
				System.out.println("Please enter your choice: ");
				input = in.nextLine();
			}
			char command = input.toUpperCase().charAt(0);
			if (command == '1') {

				System.out.println("Would you like to M)ake a reservation or C(ancel a reservation? ");
				command = in.nextLine().toUpperCase().charAt(0);
				System.out.println("Please enter the customer name: ");
				this.user.name = in.nextLine();
				if (command == 'M') {
					makeReservation();
				} else if (command == 'C') {
					cancelReservation();
				}

			} else if (command == '2') {
				this.checkinServices();
			} else if (command == '3') {
				this.applyDiscountToReservation();
			} else if (command == '4') {
				this.dataAnalyticsServices();
			} else if (command == '5') {
				loggedIn = false;
			}
		}
	}

	/**
	 * The only publicly accessible method after created the object, i.e. the only
	 * starting point to the program
	 */
	public void run() {
		boolean run = true;
		String choice;
		java.util.Set<String> hotels = system.getAllRooms().keySet();
		String[] hotelNames = new String[hotels.size()];
		int i = 0;
		for (String n : hotels) {
			hotelNames[i++] = n;
		}
		System.out.println("Please choose a hotel: ");
		this.hotelName = (String) getChoice(hotelNames);
		while (run) {
			System.out.println("\nWould you like to L)ogin or C)hange hotel or Q)uit?");
			String input = in.nextLine();
			while (input.equals("") || input.equals(" ")) {
				System.out.println("Please enter your choice: ");
				input = in.nextLine();
			}
			char ch = input.toUpperCase().charAt(0);
			if (ch == 'L') {
				System.out.println("Please choose which user to login as: ");
				String[] users = { "Customer", "Desk Clerk", "Supervisor" };
				choice = (String) getChoice(users);
				if (choice.equals("Customer")) {
					runAsCustomer();
				} else if (choice.equals("Desk Clerk")) {
					runAsDeskClerk();
				} else if (choice.equals("Supervisor")) {
					runAsSupervisor();
				}
			} else if (ch == 'C') {
				System.out.println("Please choose a hotel: ");
				this.hotelName = (String) getChoice(hotelNames);
			} else if (ch == 'Q') {
				run = false;
			}
		}
	}

	/*
	 * Allows the user choose a single object from the objs array and returns that
	 * object
	 * 
	 * @param objs an array of objects to choose from
	 * 
	 * @return the chosen object
	 */
	private Object getChoice(Object[] objs) {
		while (true) {
			if (objs == null) {
				return null;
			} else if (objs.length == 0) {
				System.out.println("No reservations at this date");
				return null;
			}
			char ch = 'A';
			for (Object o : objs) {
				System.out.println(ch + ")" + o.toString());
				ch++;
			}

			String line = in.nextLine();
			while (line.equals("")) {
				System.out.println("Please enter your choice: ");
				line = in.nextLine();
			}
			char input = line.toUpperCase().charAt(0);
			int index = input - 'A';
			if (index >= 0 && index < objs.length) {
				return objs[index];
			}
		}
	}

	/*
	 * Allow a user to choose days in the range between start and end(including) and
	 * returns them in a list
	 * 
	 * @param start the start date of the period
	 * 
	 * @param end the end date of the period
	 * 
	 * @return an array list of chosen dates
	 */
	private ArrayList<LocalDate> getDaysChoice(LocalDate start, LocalDate end) {
		ArrayList<LocalDate> allDays = new ArrayList<LocalDate>();
		for (LocalDate date = start; !date.equals(end); date = date.plusDays(1)) {
			allDays.add(date);
		}
		ArrayList<LocalDate> days = new ArrayList<LocalDate>();
		while (true) {
			char ch = 'A';
			for (LocalDate d : allDays) {
				System.out.println(ch + ")" + d);
				ch++;
			}
			System.out.println("Q)uit selection");
			String line = in.nextLine();
			while (line.equals("")) {
				System.out.println("Please enter your choice");
				line = in.nextLine();
			}
			char input = line.toUpperCase().charAt(0);
			if (input == 'Q' || days.size() == allDays.size()) {
				return days;
			}
			int index = input - 'A';
			if (index >= 0 && index <= allDays.size()) {
				LocalDate choice = allDays.get(index);
				if (!days.contains(choice)) {
					days.add(choice);
				} else {
					System.out.println("You already chose " + choice);
				}
			}
		}
	}
}
