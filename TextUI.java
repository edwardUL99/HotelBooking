import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

public class TextUI {
	private Scanner in;
	private BookingSystem system;
	private User user;
	private String hotelName;

	public TextUI() {
		in = new Scanner(System.in);
	}
	
	/**
	 * Checks if the date supplied is in the form dd/mm/yyyy 
	 * @param dateInput date the date to be checked
	 * @return if the date is of the correct format
	 */
	private boolean isCorrectDateFormat(String dateInput) {
		String[] date = dateInput.split("/");
		if (date.length != 3 || dateInput.indexOf("/") == -1) {
			return false;
		} else if (Integer.parseInt(date[1]) > 12) { //If the mm value is greater than twelve, it may have been confused to be the dd value
			return false;
		} else {
			int[] requiredLengths = {2, 2, 4};
			for (int i = 0; i < date.length; i++) {
				if (date[i].length() != requiredLengths[i]) {
					return false;
				}
			}
			return true;
		}
	}
	
	/**
	 * Returns a date selected by the user
	 * @param futureDate if true, the user will only be allowed choose dates in the future
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
	
	private LocalDate[] getDatesForDataAnalysis() {
		LocalDate[] dates = new LocalDate[2];
		System.out.println("Please enter the start date: ");
		LocalDate d1 = getDate(false); //allow previous dates also as may want to do analysis on historical data
		System.out.println("Please enter the end date: ");
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
	
	private ArrayList<RoomBooking> chooseRooms(int numberOfRooms, LocalDate from, LocalDate to) {
		//Maybe instead of checking if the rooms are available after choosing them in the BookingSystem class, have the BookingSystem getRooms only return rooms that are available for the numberOfRooms specified
		TreeMap<Room, Integer> map = this.system.getCurrentRooms(this.hotelName, from, to);
		//For getCurrentRooms() method maybe supply two date parameters, indicating a time period to populate the rooms available map with the amount of rooms free at that period
		ArrayList<Room> allRooms = new ArrayList<Room>(map.keySet());
		ArrayList<RoomBooking> rooms = new ArrayList<RoomBooking>(numberOfRooms);
		System.out.println("Room type(Rooms Available)(Maximum Adult occupancy)(Maximum Child occupancy)"); //Have a way to check number of rooms available for the date chosen in reservation
		while (rooms.size() != numberOfRooms) {
			char ch = 'A';
			ArrayList<Character> notAvailable = new ArrayList<Character>(5);
			for (int i = 0; i < allRooms.size(); i++) {
				Room r = allRooms.get(i);
				if (map.get(r) > 0) {
					System.out.println(ch + ")" + r.getType() + "(" + map.get(r) + ")" + "(" + r.occupancy(true, false) + ")" + "(" + r.occupancy(false, false) + ")");
				} else {
					notAvailable.add(ch);
					System.out.println(ch + ")" + r.getType() + " is booked out for these dates");
				}
				ch++;
			} //This is not how it will be implemented, just until the other methods are developed
			String input = in.nextLine();
			if (notAvailable.contains(input.toUpperCase().charAt(0))) {
				System.out.println("You cannot choose this room, as it is booked out. Please choose another.");
			} else {
				int n = input.toUpperCase().charAt(0) - 'A';
				System.out.println("Please enter how many adults and children are staying per room (number of Adults,number of Children): ");
				String[] occupancy = in.nextLine().split(",");
				int adults = Integer.parseInt(occupancy[0]);
				int children = Integer.parseInt(occupancy[1]);
				if (n >= 0 && n < allRooms.size()) {
					Room choice = allRooms.get(n);
					if ((adults >= choice.occupancy(true, true) && adults <= choice.occupancy(true, false)) && (children >= choice.occupancy(false, true) && children <= choice.occupancy(false,  false))) {
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
					} else {
						System.out.println("The room chosen is not suitable for the number of adults or children you entered, please try again");
					}
				}
			}
		}
		return rooms;
	}
	
	
	private void makeReservation() {
		System.out.println("Do you want to make a S)tandard booking or A)dvanced Purchase?");
		char choice = in.nextLine().toUpperCase().charAt(0);
		String type;
		if (choice == 'S') {
			type = "S";
		} else {
			type = "AP";
		}
		boolean run = true;
		LocalDate checkin = null;
		while (run) {
			System.out.println("Please enter your check-in date(dd/mm/yyyy): ");
			checkin = getDate(true);
			if (!this.system.onlyBookingOnCheckInDate(this.hotelName, this.user.name, checkin)) {
				System.out.println("You already have a reservation on this date, please try again");
			} else {
				run = false;
			}
		}
		System.out.println("Please enter the number of nights you wish to stay: ");
		int numNights = Integer.parseInt(in.nextLine()); //Prevents line not found errors as nextInt() does not skip to nextLine
		System.out.println("Please enter the number of people staying: ");
		int numPeople = Integer.parseInt(in.nextLine());
		System.out.println("Please enter the number of rooms you wish to book: ");
		int numRooms = Integer.parseInt(in.nextLine()); //Prevents line not found errors as nextInt() does not skip to nextLine
		ArrayList<RoomBooking> rooms = chooseRooms(numRooms, checkin, checkin.plusDays(numNights));
		this.user.createReservation(this.hotelName, this.user.name, type, checkin, numNights, numPeople, numRooms, rooms);
	}

	private void cancelReservation() {
		Reservation reservation = this.getReservation();
		if (reservation == null) {
			System.out.println("The reservation could not be found");
		} else {
			this.user.cancelReservation(hotelName, reservation);
		}
	}
	
	private Reservation getReservation() {
		System.out.println("Please enter the checkIn date(dd/mm/yyyy): ");
		LocalDate checkIn = getDate(true);
        return this.system.getReservation(this.hotelName, this.user.name, checkIn);
	}
	
	private void viewReservation() {
		Reservation reservation = this.getReservation();
		if (reservation == null) {
			System.out.println("The reservation could not be found");
		} else {
			System.out.println(reservation.format());
		}
	}

	public void checkInUser() {
		boolean run = true;
		while (run) {
			//call getTotalCostCalculated() for a reservation either here or at checkout
			System.out.println("Please enter the customer name: ");
			String name = in.nextLine();
			System.out.println("Please enter the check-in date(dd/mm/yyyy): ");
			LocalDate checkin = getDate(true);
			if (this.user instanceof DeskClerk) {
				DeskClerk clerk = (DeskClerk)this.user;
				clerk.checkIn(name, checkin); //may replace with LocalDate.now() as checkin and check is it the right date for the ckeckin to occur, i.e if the reservation for this checkin date is null, it doesnt exist or is the wrong day to check in
			}
			run = false;
		}
	}
	
	public void checkOutUser() {
		boolean run = true;
		while (run) {
			System.out.println("Please enter the customer name: ");
			String name = in.nextLine();
			System.out.println("Please enter the check-in date(dd/mm/yyyy): ");
			LocalDate checkin = getDate(true);
			System.out.println("Please enter the check-out date(dd/mm/yyyy): "); //may replace with LocalDate.now() as if this was a checkout it would be current time. Same with checkin and check is it the right date for the checkin to occur
			LocalDate checkout = getDate(true);
			if (this.user instanceof DeskClerk) {
				DeskClerk clerk = (DeskClerk)this.user;
				clerk.checkOut(name, checkin, checkout); //check if it was successful, if not print out an error message
			}
			run = false;
		}
	}
	
	private void checkinServices() {
		boolean run = true;
		while (run) {
			System.out.println("Would you like to A)Check In or B)Check Out?");
			char choice = in.nextLine().toUpperCase().charAt(0);
			if (choice == 'A') {
				checkInUser();
				run = false;
			} else if (choice == 'B') {
				checkOutUser();
				run = false;
			}
		}
	}
	
	private void applyDiscountToReservation() {
		System.out.println("Please enter the customer name: ");
		this.user.name = in.nextLine();
		Reservation reservation = this.getReservation();
		if (reservation == null) {
			System.out.println("The reservation could not be found and a discount cannot applied");
		} else {
			System.out.println("Please enter the percentage discount (0-100): ");
			double percentageDiscount = Double.parseDouble(in.nextLine());
			Supervisor temp = (Supervisor)this.user;
			if (temp.applyDiscount(percentageDiscount, reservation.getNumber())) {
				System.out.println("The discount was applied successfully");
			}
		}
	}
	
	private void dataAnalyticsServices() {
		Supervisor temp = (Supervisor)this.user;
		//choose data analytic method
		System.out.println("would you like to \n1)view all room purchases between dates"
				+ "\n2)view average earnings for each room over chosen period"
				+ "\n3)view Total earnings for each room over chosen period"
				+ "\n4)request all income information for rooms over chosen period? (writes to file)?"
				+ "\n5)view number of occupants that stayed in each room over chosen period");
		
		char command = in.nextLine().toUpperCase().charAt(0);
		if (command == '1') {
			LocalDate start = LocalDate.of(2020, 8, 20); //hard coded for testing purposes
			LocalDate end = LocalDate.of(2020, 8, 20);
		} else if (command == '2') {
			LocalDate start = LocalDate.of(2020, 8, 20); //hard coded for testing purposes
			LocalDate end = LocalDate.of(2020, 8, 20);
			System.out.println(temp.getAverageIncomePerRoom(start, end));
		} else if (command == '3') {
			LocalDate start = LocalDate.of(2020, 8, 20); //hard coded for testing purposes
			LocalDate end = LocalDate.of(2020, 8, 20);
			System.out.println(temp.getTotalIncomePerRoom(start, end));
		} else if (command == '4') {
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
		}else if(command == '5') {
			LocalDate start = LocalDate.of(2020, 8, 20); //hard coded for testing purposes
			LocalDate end = LocalDate.of(2020, 8, 20);
		}
	}
	
	private void runAsCustomer() {
		boolean loggedIn = true;
		System.out.println("Please enter your name: ");
		this.user = new Customer(in.nextLine(), this.hotelName, system);
		while (loggedIn) {
			System.out.println("\nHotel: " + this.hotelName);
			System.out.println("\nWould you like to M)ake a reservation, C(ancel a reservation, V)iew a reservation or L)ogout?");
			char command = in.nextLine().toUpperCase().charAt(0);
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
	
	private void runAsDeskClerk() {
		boolean loggedIn = true;
		this.user = new DeskClerk(hotelName, system);
		while (loggedIn) {
			System.out.println("\nHotel: " + this.hotelName);
			System.out.println("\nWould you like to access R)eservations, C)heck-in/out or L)ogout?");
			char command = in.nextLine().toUpperCase().charAt(0);
			if (command == 'R') {
				System.out.println("Would you like to M)ake a reservation, C(ancel a reservation or V)iew a reservation?");
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
	
	private void runAsSupervisor() {
		boolean loggedIn = true;
		this.user = new Supervisor(this.hotelName, system);
		while (loggedIn) {
			System.out.println("\nHotel: " + this.hotelName);
			
			System.out.println("\nWould you like to access \n1)reservations \n2)check-in/out \n3)apply discounts \n4)data analytics or \n5)Logout");
			char command = in.nextLine().toUpperCase().charAt(0);
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
			}else if (command == '4') {
				this.dataAnalyticsServices();
			} else if (command == '5') {
				loggedIn = false;
			}
			/*
			} else if (command == '3') {
				//enter reservation discount applies to and size of discount
			} else if (command == '4') {
				//choose data analytic method
				System.out.println("would you like to \n1)get all room purchases between dates. \n2)get average earnings across all rooms over chosen period \n3)get Total earnings over chosen period");
				command = in.nextLine().toUpperCase().charAt(0);
				if (command == '1') {
			
				} else if (command1 == '2') {
			
				} else if (command1 == '3') {
			
				}
			
			}
		 	
			 */
		}
	}
	
	public void run() {
		boolean run = true;
		String choice;
		this.system = new BookingSystem();
		java.util.Set<String> hotels = system.getRoomsFromFile().keySet();
		String[] hotelNames = new String[hotels.size()];
		int i = 0;
		for (String n : hotels) {
			hotelNames[i++] = n;
		}	
		System.out.println("Please choose a hotel: ");
		this.hotelName = (String)getChoice(hotelNames);
		while (run) {
			System.out.println("\nWould you like to L)ogin or C)hange hotel or Q)uit?");
			char ch = in.nextLine().toUpperCase().charAt(0);
			if (ch == 'L') {
				System.out.println("Please choose which user to login as: ");
				String[] users = {"Customer", "Desk Clerk", "Supervisor"};
				choice = (String)getChoice(users);	
				if (choice.equals("Customer")) {
					runAsCustomer();
				} else if (choice.equals("Desk Clerk")) {
					runAsDeskClerk();
				} else if (choice.equals("Supervisor")) {
					runAsSupervisor();
				}
			} else if (ch == 'C') {
				System.out.println("Please choose a hotel: ");
				this.hotelName = (String)getChoice(hotelNames);
			} else if (ch == 'Q') {
				run = false;
			}
		} 
	}
	
	private Object getChoice(Object[] objs) {
		while (true) {
			char ch = 'A';
			for (Object o : objs) {
				System.out.println(ch + ")" + o.toString());
				ch++;
			}
			
			char input = in.nextLine().toUpperCase().charAt(0);
			int index = input - 'A';
			if (index >= 0 && index < objs.length) {
				return objs[index];
			}
		}
	}
	
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
			char input = in.nextLine().toUpperCase().charAt(0);
			if (input == 'Q' || days.size() == allDays.size()) {
				return days;
			}
			int index = input - 'A';
			if (index >= 0 && index <= allDays.size()) {
				LocalDate choice = allDays.get(index);
				if (!days.contains(choice)) {
					days.add(choice);
				}
			}
		}
	}
}
