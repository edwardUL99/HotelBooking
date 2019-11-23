import java.time.LocalDate;
import java.util.TreeMap;

/**
* A class representing the Supervisor user of the system
*/
public class Supervisor extends DeskClerk {
	private DataAnalysis analyzer;
	
	public Supervisor(String hotelName, BookingSystem system) {
		super(hotelName, system);
		this.analyzer = new DataAnalysis(this.hotelName, this.system.getHotelStays().get(this.hotelName));
	}
	
	/** allows supervisor to apply any discount to any reservation */
	public boolean applyDiscount(double discount, int reservationNumber ) {
		discount /= 100;
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
	
	public TreeMap<Room, Double> getAverageIncomePerRoom(LocalDate start, LocalDate end) {
		return analyzer.getAverageIncomePerRoom(start, end);
	}
	
	public TreeMap<Room, Double> getTotalIncomePerRoom(LocalDate start, LocalDate end) {
		return analyzer.getTotalIncomePerRoom(start, end);
	}
	
	public String requestRoomIncomeInformation(LocalDate start, LocalDate end) {
		return analyzer.requestIncomeInformation(start, end);
	}
	
	public String requestRoomIncomeInformation(LocalDate start, LocalDate end, java.util.ArrayList<LocalDate> days) {
		return analyzer.requestIncomeInformation(start, end, days);
	}
	
	//gets data analysis of what rooms are occupied between two dates
	public void getOccupancyInfo(LocalDate start, LocalDate end) {
		
	}
}
