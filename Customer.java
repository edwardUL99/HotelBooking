/**
* A class representing the Customer user of the system
*/
public class Customer extends User {
	
	public Customer(String name, String hotelName, BookingSystem system) {
		//The system will be provided by the text ui maybe
		super(hotelName, system);
		super.name = name;
	}
}
