/**
* A class representing the Customer user of the system
*/
public class Customer extends User {
	
	/**
	 * Creates a customer of the chosen name in the hotel chosen and the system this customer will run on
	 * @param name the name of the customer
	 * @param hotelName the name of the hotel at which the user is a customer of
	 * @param system the system the customer will operate on
	 */
	public Customer(String name, String hotelName, BookingSystem system) {
		super(hotelName, system);
		super.name = name;
	}
}
