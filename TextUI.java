import java.util.Scanner;
import java.util.TreeSet;

public class TextUI {
	private Scanner in;

	public TextUI() {
		in = new Scanner(System.in);
	}
	
	public void run() {
		BookingSystem system = new BookingSystem();
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
				User user = new Customer(in.nextLine(), system);
				System.out.println("Please choose a hotel: ");
				String hotelName = (String)getChoice(hotelNames);
				System.out.println("Would you like to M)ake a reservation or C(ancel a reservation? ");
				char command = in.nextLine().toUpperCase().charAt(0);
				if (command == 'M') {
					//Code to create a reservation, maybe write a method to do this so won't have to keep writing out for the 3 users
				} else if (command == 'C') {
					//Code to cancel, as above comment, create a method to do this
				}
			}
			//Code for other users signing on
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
