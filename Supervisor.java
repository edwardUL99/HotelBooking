import java.time.LocalDate;
import java.util.TreeMap;

/**
 * A class representing the Supervisor user of the system
 */
public class Supervisor extends DeskClerk {
	private DataAnalysis analyzer;

	/**
	 * Creates a Supervisor object
	 * 
	 * @param hotelName the name of the hotel the Supervisor is "working" at
	 * @param system    the system the Supervisor is working on
	 */
	public Supervisor(String hotelName, BookingSystem system) {
		super(hotelName, system);
		this.analyzer = new DataAnalysis(this.hotelName, this.system.getHotelStays().get(this.hotelName));
	}

	/**
	 * Applies a discount to the reservation specified by the given details
	 * 
	 * @param discount          the discount in terms of decimal 0.00 - 0.99 or in
	 *                          percentage i.e 1 - 100
	 * @param reservationNumber the number of the reservation
	 * @return true if the discount was applied successfully
	 */
	public boolean applyDiscount(double discount, int reservationNumber) {
		discount = discount > 1 ? discount / 100 : discount;
		Reservation r = this.system.getReservation(hotelName, reservationNumber);
		if (r == null) {
			return false;
		}
		if (r.getTotalCost().getAmountDue() != 0.00) { // discounts can only be applied on checkin so if it's not 0 they have checked in and the desk clerk has calculated their bill
			r.getTotalCost().setAmountDue(r.getTotalCost().getAmountDue() * (1 - discount));
			this.system.updateFiles("Reservations");
			this.system.updateFiles("Stays");
			return true;
		}
		return false;
	}

	/**
	 * Requests all room income information (i.e. billing) for all the dates in the
	 * date range and stores it in a file
	 * 
	 * @param start the start date of the period
	 * @param end   the end date of the period
	 * @return the filename at which the analysis was stored
	 */
	public String requestRoomIncomeInformation(LocalDate start, LocalDate end) {
		return analyzer.requestIncomeInformation(start, end);
	}

	/**
	 * Requests all room income information (i.e. billing) for the days specified
	 * between the date range and stores it in a file
	 * 
	 * @param start the start date of the period
	 * @param end   the end date of the period
	 * @param days  the days of the date range to show
	 * @return the filename at which the analysis was stored
	 */
	public String requestRoomIncomeInformation(LocalDate start, LocalDate end, java.util.ArrayList<LocalDate> days) {
		return analyzer.requestIncomeInformation(start, end, days);
	}

	/**
	 * Requests all room Occupancy information (i.e. billing) for all the dates in
	 * the date range and stores it in a file
	 * 
	 * @param start           the start date of the period
	 * @param end             the end date of the period
	 * @param hotelRooms      rooms of the hotel, leave nul if occupantNumbers is
	 *                        true
	 * @param occupantNumbers true if you want to retrieve occupant numbers, false
	 *                        if you want to retrieve room numbers
	 * @return the filename at which the analysis was stored
	 */
	public String requestRoomOccupantsInformation(LocalDate start, LocalDate end, TreeMap<Room, Integer> hotelRooms,
			boolean occupantNumbers) {
		return analyzer.requestOccupantInformation(start, end, hotelRooms, occupantNumbers);
	}

	/**
	 * Requests all room Occupancy information (i.e. billing) for the days specified
	 * between the date range and stores it in a file
	 * 
	 * @param start           the start date of the period
	 * @param end             the end date of the period
	 * @param hotelRooms      rooms of the hotel, leave null if occupantNumbers is
	 *                        true
	 * @param occupantNumbers true if you want to retrieve occupant numbers, false
	 *                        if you want to retrieve room numbers
	 * @param days            the days of the date range to show
	 * @return the filename at which the analysis was stored
	 */
	public String requestRoomOccupantsInformation(LocalDate start, LocalDate end, TreeMap<Room, Integer> hotelRooms,
			boolean occupantNumbers, java.util.ArrayList<LocalDate> days) {
		return analyzer.requestOccupantInformation(start, end, days, hotelRooms, occupantNumbers);
	}
}
