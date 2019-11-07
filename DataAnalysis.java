import java.time.LocalDate;
import java.util.TreeMap;


public class DataAnalysis extends BookingSystem {
	
	public void getFinancialInfo(LocalDate start, LocalDate end) {
		
		Object[][] temp = readDataFromFile("");
		TreeMap<LocalDate, Double> A = new TreeMap<LocalDate, Double>();
		int count = 0;
		
		// add if statement to get data from time period between start and end including start and end
		while(count < temp.length) {
			
			if(((LocalDate)temp[count][4]).compareTo(end) >= 0 || ((LocalDate)temp[count][4]).compareTo(start) <= 0) {
				A.put((LocalDate)temp[count][4],  (double)temp[count][9]);
			}
		}
		
	}

}
