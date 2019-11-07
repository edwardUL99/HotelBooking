import java.time.LocalDate;
import java.util.TreeMap;


public class DataAnalysis extends BookingSystem {
	
	public void getFinancialInfo(String timeFrame) {
		
		Object[][] temp = readDataFromFile("");
		TreeMap<LocalDate, Double> A = new TreeMap<LocalDate, Double>();
		int count = 0;
		
		// add if statement to get data from correct time period date > current date - day/month/year
		while(count < temp.length) {
			A.put((LocalDate)temp[count][4],  (double)temp[count][9]);
		}
		
	}

}
