import java.time.LocalDate;

/**
* A class representing the Supervisor user of the system
*/
public class Supervisor extends DeskClerk {
	private DataAnalysis analyzer;
	
	public Supervisor(String hotelName, BookingSystem system) {
		super(hotelName, system);
		this.analyzer = new DataAnalysis(this.system.getReservations().get(this.hotelName));
	}
	
	/** allows supervisor to apply any discount to any reservation */
	public void applyDiscount(double discount, int reservationNumber ) {
		
		for(int i = 0; i < this.system.getReservations().get(hotelName).size(); i++) {
			Reservation r = this.system.getReservations().get(hotelName).get(i);
			if(r.getNumber() == reservationNumber) {
				r.getTotalCost().setAmountDue(r.getTotalCost().getAmountDue()*(1 - discount));
				break;
			}
		}
	}
	
	/*
	//methods requesting data Analysis
	//gets data analysis of financial information between two dates
	public void getFinancialDataAnalysis(LocalDate start, LocalDate end, String filePath) {
		analyzer.getFinancialInfo(start, end, this.system.readDataFromFile(filePath));
	}*/
	
	public double getAverageIncome(LocalDate start, LocalDate end) {
		return analyzer.getAverageIncome(start, end);
	}
	
	public double getTotalEarnedAmount(LocalDate start, LocalDate end) {
		return analyzer.getTotalEarned(start, end);
	}
	
	//gets data analysis of what rooms are occupied between two dates
	public void getOccupancyInfo(LocalDate start, LocalDate end) {
		
	}
}
