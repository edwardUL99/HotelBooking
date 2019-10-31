/**
* A class representing the Customer user of the system
*/
public class Customer extends User {
	private String name;
	
	public Customer(String name, BookingSystem system) {
		//The system will be provided by the text ui maybe
		super(system);
		this.name = name;
	}
}
