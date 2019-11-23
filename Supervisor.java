import java.time.LocalDate;
import java.util.TreeMap;

/**
* A class representing the Supervisor user of the system
*/
public class Supervisor extends DeskClerk {
	private DataAnalysis analyzer;
	
	/**
	 * Creates a Supervisor object
	 * @param hotelName the name of the hotel the Supervisor is "working" at
	 * @param system the system the Supervisor is working on
	 */
	public Supervisor(String hotelName, BookingSystem system) {
		super(hotelName, system);
		this.analyzer = new DataAnalysis(this.hotelName, this.system.getHotelStays().get(this.hotelName));
	}
	
	/**
	 * Applies a discount to the reservation specified by the given details
	 * @param discount the discount in terms of decimal 0.00 - 0.99 or in percentage i.e 1 - 100
	 * @param reservationNumber the number of the reservation
	 * @return true if the discount was applied successfully
	 */
	public boolean applyDiscount(double discount, int reservationNumber ) {
		discount = discount > 1 ? discount / 100:discount;
		for(int i = 0; i < this.system.getReservations().get(hotelName).size(); i++) {
			Reservation r = this.system.getReservations().get(hotelName).get(i);
			if(r.getNumber() == reservationNumber && r.getTotalCost().getAmountDue() != 0.00) { //discounts can only be applied on checkin so if it's not 0 they have checked in and the desk clerk has calculated their bill
				r.getTotalCost().setAmountDue(r.getTotalCost().getAmountDue()*(1 - discount));
				this.system.updateFiles("Reservations");
				this.system.updateFiles("Stays");
				return true;
			}
		}
		return false;
	}
	
	/*
	//methods requesting data Analysis
	//gets data analysis of financial information between two dates
	public void getFinancialDataAnalysis(LocalDate start, LocalDate end, String filePath) {
		analyzer.getFinancialInfo(start, end, this.system.readDataFromFile(filePath));
	}*/
	
	/**
	 * Returns the TreeMap of average income per room between the specified date period
	 * @param start the start date of the period
	 * @param end the end date of the period
	 * @return the TreeMap with the room mapped to its average income
	 */
	public TreeMap<Room, Double> getAverageIncomePerRoom(LocalDate start, LocalDate end) {
		return analyzer.getAverageIncomePerRoom(start, end);
	}
	
	/**
	 * Returns the TreeMap of total income per room between the specified date period
	 * @param start the start date of the period
	 * @param end the end date of the period
	 * @return the TreeMap with the room mapped to its total income
	 */
	public TreeMap<Room, Double> getTotalIncomePerRoom(LocalDate start, LocalDate end) {
		return analyzer.getTotalIncomePerRoom(start, end);
	}
	
	/**
	 * Requests all room income information (i.e. billing) for all the dates in the date range and stores it in a file
	 * @param start the start date of the period
	 * @param end the end date of the period
	 * @return the filename at which the analysis was stored
	 */
	public String requestRoomIncomeInformation(LocalDate start, LocalDate end) {
		return analyzer.requestIncomeInformation(start, end);
	}
	
	/**
	 * Requests all room income information (i.e. billing) for the days specified between the date range and stores it in a file
	 * @param start the start date of the period
	 * @param end the end date of the period
	 * @param days the days of the date range to show 
	 * @return the filename at which the analysis was stored
	 */
	public String requestRoomIncomeInformation(LocalDate start, LocalDate end, java.util.ArrayList<LocalDate> days) {
		return analyzer.requestIncomeInformation(start, end, days);
	}
	
	//gets data analysis of what rooms are occupied between two dates
	public void getOccupancyInfo(LocalDate start, LocalDate end) {
		
	}
	
	/**
	 * Returns the TreeMap of average occupants per room between the specified date period
	 * @param start the start date of the period
	 * @param end the end date of the period
	 * @return the TreeMap with the room mapped to its average occupants
	 */
	public TreeMap<Room, Double> getAverageOccupantsPerRoom(LocalDate start, LocalDate end) {
		return analyzer.getAverageOccupantsPerRoom(start, end);
	}
	
	/**
	 * Returns the TreeMap of total occupants per room between the specified date period
	 * @param start the start date of the period
	 * @param end the end date of the period
	 * @return the TreeMap with the room mapped to its total occupants
	 */
	public TreeMap<Room, Integer> getTotalOccupantsPerRoom(LocalDate start, LocalDate end) {
		return analyzer.getTotalOccupantsPerRoom(start, end);
	}
	
	/**
	 * Requests all room Occupancy information (i.e. billing) for all the dates in the date range and stores it in a file
	 * @param start the start date of the period
	 * @param end the end date of the period
	 * @return the filename at which the analysis was stored
	 */
	public String requestRoomOccupantsInformation(LocalDate start, LocalDate end) {
		return analyzer.requestOccupantInformation(start, end);
	}
	
	/**
	 * Requests all room Occupancy information (i.e. billing) for the days specified between the date range and stores it in a file
	 * @param start the start date of the period
	 * @param end the end date of the period
	 * @param days the days of the date range to show 
	 * @return the filename at which the analysis was stored
	 */
	public String requestRoomOccupantsInformation(LocalDate start, LocalDate end, java.util.ArrayList<LocalDate> days) {
		return analyzer.requestOccupantInformation(start, end, days);
	}
}
