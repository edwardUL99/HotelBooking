import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

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
		if (date.length < 3 || date.length > 3 || dateInput.indexOf("/") == -1) {
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
	
	private LocalDate getDate() {
		while (true) {
			String dateInput = in.nextLine();
			if (!isCorrectDateFormat(dateInput)) {
				System.out.println("Date is in incorrect format, please try again: ");
			} else {
				String[] date = dateInput.split("/");
				LocalDate dateChosen = LocalDate.of(Integer.parseInt(date[2]), Integer.parseInt(date[1]), Integer.parseInt(date[0]));
				if (!dateChosen.isAfter(LocalDate.now())) {
					System.out.println("Please choose a date that is in the future: ");
				} else {
					return dateChosen;
				}
			}
		}
	}
	
	
	
	private ArrayList<Room> chooseRooms(int numberOfRooms, LocalDate from, LocalDate to) {
		//Maybe instead of checking if the rooms are available after choosing them in the BookingSystem class, have the BookingSystem getRooms only return rooms that are available for the numberOfRooms specified
		TreeMap<Room, Integer> map = this.system.getCurrentRooms(this.hotelName, from, to);
		//For getCurrentRooms() method maybe supply two date parameters, indicating a time period to populate the rooms available map with the amount of rooms free at that period
		ArrayList<Room> allRooms = new ArrayList<Room>(map.keySet());
		ArrayList<Room> rooms = new ArrayList<Room>(numberOfRooms);
		System.out.println("Room type(Rooms Available)(Maximum Adult occupancy)(Maximum Child occupancy)"); //Have a way to check number of rooms available for the date chosen in reservation
		while (rooms.size() != numberOfRooms) {
			char ch = 'A';
			for (int i = 0; i < allRooms.size(); i++) {
				Room r = allRooms.get(i);
				if (map.get(r) > 0) {
					System.out.println(ch + ")" + r.getType() + "(" + map.get(r) + ")" + "(" + r.occupancy(true, false) + ")" + "(" + r.occupancy(false, false) + ")");
					ch++;
				}
			} //This is not how it will be implemented, just until the other methods are developed
			String input = in.nextLine();
			int n = input.toUpperCase().charAt(0) - 'A';
			System.out.println("Please enter how many adults and children are staying per room (number of Adults,number of Children): ");
			String[] occupancy = in.nextLine().split(",");
			int adults = Integer.parseInt(occupancy[0]);
			int children = Integer.parseInt(occupancy[1]);
			if (n >= 0 && n < allRooms.size()) {
				Room choice = allRooms.get(n);
				if ((adults >= choice.occupancy(true, true) && adults <= choice.occupancy(true, false)) && (children >= choice.occupancy(false, true) && children <= choice.occupancy(false,  false))) {
					rooms.add(choice);
				} else {
					System.out.println("The room chosen is not suitable for the number of adults or children you entered, please try again");
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
		System.out.println("Please enter your check-in date(dd/mm/yyyy): ");
		LocalDate checkin = getDate();
		System.out.println("Please enter the number of nights you wish to stay: ");
		int numNights = Integer.parseInt(in.nextLine()); //Prevents line not found errors as nextInt() does not skip to nextLine
		System.out.println("Please enter the number of rooms you wish to book: ");
		int numRooms = Integer.parseInt(in.nextLine()); //Prevents line not found errors as nextInt() does not skip to nextLine
		ArrayList<Room> rooms = chooseRooms(numRooms, checkin, checkin.plusDays((long)numNights));
		this.user.createReservation(this.hotelName, this.user.name, type, checkin, numNights, numRooms, rooms);
	}

	private void cancelReservation() {
		System.out.println("Please enter the checkIn date(dd/mm/yyyy)");
		LocalDate checkIn = getDate();
		Reservation reservation = this.system.getReservation(this.hotelName, this.user.name, checkIn);
		if (reservation == null) {
			System.out.println("The reservation could not be found");
		} else {
			this.user.cancelReservation(hotelName, reservation);
		}
	}
	
	private void viewReservation() {
		System.out.println("Please enter the checkIn date(dd/mm/yyy)");
		LocalDate checkIn = getDate();
		Reservation reservation = this.system.getReservation(this.hotelName, this.user.name, checkIn);
		if (reservation != null) {
			System.out.println(reservation);
		} else {
			System.out.println("The reservation could not be found");
		}
	}

	public void checkInUser() {
		
	}
	
	public void checkOutUser() {
		
	}
	
	public void applyDiscountToReservation() {
		
	}
	
	private void runAsCustomer() {
		boolean loggedIn = true;
		System.out.println("Please enter your name: ");
		this.user = new Customer(in.nextLine(), this.hotelName, system);
		while (loggedIn) {
			System.out.println("Would you like to M)ake a reservation, C(ancel a reservation, V)iew a reservation or L)ogout?");
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
		while (loggedIn) {
			this.user = new DeskClerk(hotelName, system);
			System.out.println("Would you like to access R)eservations, C)heck-in/out or L)ogout?");
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
				
			} else if (command == 'L') {
				loggedIn = false;
			}
		}
	}
	
	private void runAsSupervisor() {
		boolean loggedIn = true;
		while (loggedIn) {
			this.user = new Supervisor(this.hotelName, system);
			System.out.println("Would you like to access \n1)reservations \n2)check-in/out \n3)apply discounts \n4)data analytics or \n5)Logout");
			char command = in.nextLine().toUpperCase().charAt(0);
			if (command == '1') {
				
				System.out.println("Would you like to M)ake a reservation or C(ancel a reservation? ");
				command = in.nextLine().toUpperCase().charAt(0);
				if (command == 'M') {
					makeReservation();
				} else if (command == 'C') {
				cancelReservation();
				}
		
			} else if (command == '5') {
				loggedIn = false;
			}/* else if (command == '2') 
			
				System.out.println("Would you like to I)check-in or O)check-out? ");
				command = in.nextLine().toUpperCase().charAt(0);
				if (command == 'I') {
					checkIn();
				} else if (command == 'O') {
					checkOut();
				}
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
		java.util.Set<String> hotels = system.getRooms().keySet();
		String[] hotelNames = new String[hotels.size()];
		int i = 0;
		for (String n : hotels) {
			hotelNames[i++] = n;
		}	
		System.out.println("Please choose a hotel: ");
		this.hotelName = (String)getChoice(hotelNames);
		while (run) {
			System.out.println("Would you like to L)ogin or C)hange hotel or Q)uit?");
			char ch = in.nextLine().toUpperCase().charAt(0);
			if (ch == 'L') {
				System.out.println("Please choose which user to login as: ");
				String[] users = {"Customer", "Desk Clerk", "Supervisor"};
				choice = (String)getChoice(users);	
				if (choice.equals("Customer")) {
					runAsCustomer();
				} else if (choice.equals("DeskClerk")) {
					runAsDeskClerk();
				} else if (choice.equals("Supervisor")) {
					runAsSupervisor();
				}
			} else if (ch == 'C') {
				System.out.println("The current hotel is: " + this.hotelName);
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
}
