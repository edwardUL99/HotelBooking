import java.time.LocalDate;
import java.util.ArrayList;
import java.util.TreeMap;


public class DataAnalysis  {
	
	public TreeMap<LocalDate,ArrayList<Double>> getFinancialInfo(LocalDate start, LocalDate end, Object[][] data) {
		
		TreeMap<LocalDate, ArrayList<Double>> dateBalance = new TreeMap<LocalDate, ArrayList<Double>>();
		int count = 0;
		
		// add if statement to get data from time period between start and end including start and end
		while(count < data.length) {
			if(((LocalDate)data[count][4]).compareTo(end) <= 0 && ((LocalDate)data[count][4]).compareTo(start) >= 0 ) {
				dateBalance.put(((LocalDate)data[count][4]), new ArrayList<Double>());
				dateBalance.get((LocalDate)data[count][4]).add((double)data[count][9]);
			}
		}
		return dateBalance;
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