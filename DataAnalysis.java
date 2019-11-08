import java.time.LocalDate;
import java.util.TreeMap;


public class DataAnalysis  {
	
	public TreeMap<LocalDate, Double> getFinancialInfo(LocalDate start, LocalDate end, Object[][] data) {
		
		TreeMap<LocalDate, Double> dateTotalCost = new TreeMap<LocalDate, Double>();
		int count = 0;
		
		// add if statement to get data from time period between start and end including start and end
		while(count < data.length) {
			
			if(((LocalDate)data[count][4]).compareTo(end) >= 0 || ((LocalDate)data[count][4]).compareTo(start) <= 0) {
				dateTotalCost.put((LocalDate)data[count][4],  (double)data[count][9]);
			}
		}
		return dateTotalCost;
	}
	
	public double getAverageCostPerRoom(LocalDate start, LocalDate end, Object[][] data) {
		TreeMap<LocalDate, Double> dateTotalCost = getFinancialInfo(start, end, data);
		int average = 0;
		for(double totalCost :dateTotalCost.values()) {
			average += totalCost;
		}
		average = average/dateTotalCost.size();
		return average;
	}
	
	public double getTotalEarned(LocalDate start, LocalDate end, Object[][] data) {
		TreeMap<LocalDate, Double> dateTotalCost = getFinancialInfo(start, end, data);
		int total = 0;
		for(double totalCost :dateTotalCost.values()) {
			total += totalCost;
		}
		return total;
	}
}