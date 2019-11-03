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
				System.out.println("Date is in incorrect format");
			} else {
				String[] date = dateInput.split("/");
				return LocalDate.of(Integer.parseInt(date[2]), Integer.parseInt(date[1]), Integer.parseInt(date[0]));
			}
		}
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
	
	private ArrayList<Room> chooseRooms(int numberOfRooms) {
		//Maybe instead of checking if the rooms are available after choosing them in the BookingSystem class, have the BookingSystem getRooms only return rooms that are available for the numberOfRooms specified
		TreeMap<String, TreeMap<Room, Integer>> map = this.system.getCurrentRooms(); //for now, have it an anonymous variable but may change
		//For getCurrentRooms() method maybe supply two date parameters, indicating a time period to populate the rooms avilable map with the amount of rooms free at that period
		ArrayList<Room> allRooms = new ArrayList<Room>(map.get(this.hotelName).keySet());
		ArrayList<Room> rooms = new ArrayList<Room>(numberOfRooms);
		System.out.println("Room type\tRooms Available"); //Have a way to check number of rooms available for the date chosen in reservation
		while (rooms.size() != numberOfRooms) {
			char ch = 'A';
			for (int i = 0; i < allRooms.size(); i++) {
				Room r = allRooms.get(i);
				System.out.println(ch + ")" + r.getType() + "\t\t" + map.get(this.hotelName).get(r));
				ch++;
			} //This is not how it will be implemented, just until the other methods are developed
			String input = in.nextLine();
			int n = input.toUpperCase().charAt(0) - 'A';
			if (n >= 0 && n < allRooms.size()) {
				rooms.add(allRooms.get(n));
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
		System.out.println("Please choose your room(s): ");
		ArrayList<Room> rooms = chooseRooms(numRooms);
		this.user.createReservation(this.hotelName, this.user.name, type, checkin, numNights, numRooms, rooms);
	}
	
	public void run() {
		String tempName;
		this.system = new BookingSystem();
		java.util.Set<String> hotels = system.getRooms().keySet();
		String[] hotelNames = new String[hotels.size()];
		int i = 0;
		for (String name : hotels) {
			hotelNames[i++] = name;
		}
		while (true) {
			System.out.println("Please choose which user to login as: ");
			String[] users = {"Customer", "Desk Clerk", "Supervisor"};
			String choice = (String)getChoice(users);
			if (choice.equals("Customer")) {
				System.out.println("Please enter your name: ");
				tempName = in.nextLine();
				System.out.println("Please choose a hotel: ");
				this.hotelName = (String)getChoice(hotelNames);
				this.user = new Customer(tempName, this.hotelName, system);
				System.out.println("Would you like to M)ake a reservation or C(ancel a reservation? ");
				char command = in.nextLine().toUpperCase().charAt(0);
				if (command == 'M') {
					makeReservation();
				} else if (command == 'C') {
					cancelReservation();
				}
			} else if(choice.equals("Desk Clerk")) {
				System.out.println("Please choose a hotel: ");;
				this.user = new DeskClerk(in.nextLine(), system);
				System.out.println("Would you like to access R)eservations or C)heck-in/out ? ");
				char command = in.nextLine().toUpperCase().charAt(0);
				if (command == 'R') {
					
					System.out.println("Would you like to M)ake a reservation or C(ancel a reservation? ");
					char command1 = in.nextLine().toUpperCase().charAt(0);
					if (command1 == 'M') {
						makeReservation();
					} else if (command1 == 'C') {
						cancelReservation();
					}
				
				} /* else if (command == 'C') {
					
					System.out.println("Would you like to I)check-in or O)check-out? ");
					char command2 = in.nextLine().toUpperCase().charAt(0);
					if (command2 == 'I') {
						checkIn();
					} else if (command2 == 'O') {
						checkOut();
					}
				} */
			} else if(choice.equals("Superviser")) {
				System.out.println("Please choose a hotel: ");;
				this.user = new Supervisor(in.nextLine(), system);
				System.out.println("Would you like to access R)eservations or C)check-in/out or A)pply discounts or D)ata analytics ");
				char command = in.nextLine().toUpperCase().charAt(0);
				if (command == 'R') {
					
					System.out.println("Would you like to M)ake a reservation or C(ancel a reservation? ");
					char command1 = in.nextLine().toUpperCase().charAt(0);
					if (command1 == 'M') {
						makeReservation();
					} else if (command1 == 'C') {
						cancelReservation();
					}
				
				} /* else if (command == 'C') {
					
					System.out.println("Would you like to I)check-in or O)check-out? ");
					char command2 = in.nextLine().toUpperCase().charAt(0);
					if (command2 == 'I') {
						checkIn();
					} else if (command2 == 'O') {
						checkOut();
					}
				} else if (command == 'A') {
					//enter reservation discount applies to and size of discount
				} else if (command == 'D') {
					//choose data analytic method
				}
				 	
				 */
			}
			//Code for other users signing on
			System.out.println(this.system.getCurrentRooms()); //Testing only
			
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
