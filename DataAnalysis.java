import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


public class DataAnalysis  {
	
		public void writeDataToFile(String filePath, Object[][] data) {
			File file = new File(filePath);
			try (PrintWriter writer = new PrintWriter(file)) {
				if (!file.exists()) {
					file.createNewFile();
				}
				for (int row = 0; row < data.length; row++) {
					String line = "";
					for (int col = 0; col < data[row].length; col++) {
						line += data[row][col] + ",";
					}
					line = line.substring(0, line.length() - 1); //removes the last ","
					writer.println(line);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private void writeFinacialInfoToFile(LocalDate start, LocalDate end, Object[][] financialData) {
			
			TreeMap<LocalDate,ArrayList<Double>> financialInfo = getFinancialInfo(start, end, financialData);
			
			int rows = financialInfo.size();
			int columns = 2;
			
			Object[][] data = new String[rows][columns];
			String[] attributes = {"date","Total Cost"};
			
			data[0][1] = attributes[0]; 
			data[0][2] = attributes[1];
			
			int row = 1;
					
			for (Entry<LocalDate, ArrayList<Double>> e : financialInfo.entrySet()) {
				data[1][0] = e.getKey();
				boolean hotelNamed = true;
				for (Double amount : e.getValue()) {
					if (hotelNamed) {
						hotelNamed = false;
					} else {
						data[row][0] = "";
					}
					data[row][1] = e.getKey();  
					data[row][2] = amount;
					row++;
				}
			}
		String fileName = "/FinanacialInfo.csv";
		String path = System.getProperty("user.dir") + fileName;
		this.writeDataToFile(path, data);
	}

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
	/*	TreeMap<LocalDate, ArrayList<Double>> dateTotalCost = getFinancialInfo(start, end, data);
	 	int average = 0;
		for(double totalCost :dateTotalCost.) {
			average += totalCost;
		}
		average = average/dateTotalCost.size(); 
		return average; */
	}
	
	public double getTotalEarned(LocalDate start, LocalDate end, Object[][] data) {
		TreeMap<LocalDate, ArrayList<Double>> dateTotalCost = getFinancialInfo(start, end, data);
		int total = 0;
		for(ArrayList<Double> totalCost :dateTotalCost.values()) {
			for(double amount : totalCost) {
				total += amount;
			}
		}
		return total;
	}
	
	public TreeMap<String,ArrayList<Integer>> getOccupancyInfo(LocalDate start, LocalDate end, Object[][] data) {
		
		return null;
	}
			
}