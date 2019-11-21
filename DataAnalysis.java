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
		private ArrayList<Reservation> reservations; //Could probably use booking system reservations when given a hotel name the supervisor is signed in as
		
		public DataAnalysis(ArrayList<Reservation> reservations) {
			this.reservations = reservations;
		}
		
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
		
		public void writeFinacialInfoToFile(LocalDate start, LocalDate end,TreeMap<String, ArrayList<Reservation>> reservations, String hotelName) {
			TreeMap<LocalDate,ArrayList<Double>> financialInfo = getFinancialInfo(start, end);
			
			int rows = financialInfo.size();
			int columns = 2;
			
			Object[][] data = new String[rows][columns];
			String[] attributes = {"date","Total Cost"};
			
			data[0][1] = attributes[0]; 
			data[0][2] = attributes[1];
			
			int row = 1;
					
			for (Entry<LocalDate, ArrayList<Double>> e : financialInfo.entrySet()) {
				data[1][0] = e.getKey();
				for (Double amount : e.getValue()) {
					data[row][1] = e.getKey();  
					data[row][2] = amount;
					row++;
				}
			}

		String fileName = "/data/dataAnalysis/FinanacialInfo.csv";
		String path = System.getProperty("user.dir") + fileName;
		writeDataToFile(path, data);
	}
		
	public void writeOccupancyInfoToFile() {
		
	}

	// use reInitialize and take treeMap from bookingSystem? 
	// 
	public TreeMap<LocalDate,ArrayList<Double>> getFinancialInfo(LocalDate start, LocalDate end) {
		
		TreeMap<LocalDate, ArrayList<Double>> dateBalance = new TreeMap<LocalDate, ArrayList<Double>>();
		for(Reservation r : reservations) {
			
			if(r.getCheckinDate().compareTo(start) >= 0 && r.getCheckinDate().compareTo(end) <= 0) {
				LocalDate checkin = r.getCheckinDate();
				if (!dateBalance.containsKey(checkin)) {
					dateBalance.put(r.getCheckinDate(), new ArrayList<Double>());
				}
				dateBalance.get(r.getCheckinDate()).add(r.getTotalCost().getAmountDue());
			}
		}
		return dateBalance;
	}
	
	public double getAverageIncomePerRoom(LocalDate start, LocalDate end) {
		TreeMap<LocalDate, ArrayList<Double>> dateTotalCost = getFinancialInfo(start, end); //maybe a method here to read reservations from file and put them in arraylist? and pass to the method with hotelName also
	 	double average = 0;
	 	int numOfEarnings = 0; //Each arraylist is a list of reservations of that date, so dateTotalCost().size() just returns number of key-value mappings, not amount of payments
		for(Map.Entry<LocalDate, ArrayList<Double>> e : dateTotalCost.entrySet()) {
			numOfEarnings += e.getValue().size();
			for (double d : e.getValue()) {
				average += d;
			}
		}
		average = average/(double)numOfEarnings; 
		return average; 
	}
	
	public double getTotalEarned(LocalDate start, LocalDate end) {
		TreeMap<LocalDate, ArrayList<Double>> dateTotalCost = getFinancialInfo(start, end);
		int total = 0;
		for(ArrayList<Double> totalCost : dateTotalCost.values()) {
			for(double amount : totalCost) {
				total += amount;
			}
		}
		return total;
	}
	
	//
	public TreeMap<Room,Integer> getOccupancyInfo(LocalDate start, LocalDate end) {

		TreeMap<Room, Integer> roomOccupants = new TreeMap<Room, Integer>();
		
		for(Reservation R : reservations) {
			
			if(R.getCheckinDate().compareTo(start) >= 0 && R.getCheckinDate().compareTo(end) <= 0) {
				
			}
		}
		return roomOccupants;
	}

}