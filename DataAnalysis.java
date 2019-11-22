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
		private ArrayList<HotelStay> stays;
		
		/**
		 * Creates a DataAnalysis class hotel stays.
		 * @param stays
		 */
		public DataAnalysis(ArrayList<HotelStay> stays) {
			this.stays = stays;
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
			int columns = 5;
			
			Object[][] data = new String[rows][columns];
			String[] attributes = {"Date","Deposit","Reservation Cost","Total Cost"};
			
			data[0][1] = attributes[0]; 
			data[0][2] = attributes[1];
			data[0][3] = attributes[2];
			data[0][4] = attributes[3];
			
			int row = 1;
					
			for (Entry<LocalDate, ArrayList<Double>> e : financialInfo.entrySet()) {
				data[1][0] = e.getKey();
				for (Double amount : e.getValue()) {
					data[row][1] = e.getKey();  
					data[row][2] = 75;
					data[row][3] = amount - 75;
					data[row][4] = amount;
					row++;
				}
			}

		String fileName = "/data/dataAnalysis/FinanacialInfo.csv";
		String path = System.getProperty("user.dir") + fileName;
		writeDataToFile(path, data);
	}
		
	public void writeOccupancyInfoToFile(LocalDate start, LocalDate end) {
		TreeMap<String,Integer> occupancyInfo = getOccupancyInfo(start, end);

		int rows = occupancyInfo.size();
		int columns = 3;
		
		Object[][] data = new String[rows][columns];
		String[] attributes = {"RoomType, numberOfOccupants"};
		
		data[0][0] = attributes[0]; 
		data[0][1] = attributes[1];
		
		int row = 1;
				
		for (Entry<String, Integer> e : occupancyInfo.entrySet()) {
				data[row][1] = e.getKey();  
				data[row][2] = e.getValue();
		}

	String fileName = "/data/dataAnalysis/OccupancyInfo.csv";
	String path = System.getProperty("user.dir") + fileName;
	writeDataToFile(path, data);
	}

	// use reInitialize and take treeMap from bookingSystem? 
	// 
	public TreeMap<LocalDate,ArrayList<Double>> getFinancialInfo(LocalDate start, LocalDate end) {
		
		TreeMap<LocalDate, ArrayList<Double>> dateBalance = new TreeMap<LocalDate, ArrayList<Double>>();
		for(HotelStay stay : stays) {
			Reservation r = stay.getReservation();
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
	
	public TreeMap<Room, Double> getAverageIncomePerRoom(LocalDate start, LocalDate end) {
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
	
	//returns a TreeMap with Room as a key linked to the number of occupants who stayed in the room between start and end.
	public TreeMap<String, Integer> getOccupancyInfo(LocalDate start, LocalDate end) {

		TreeMap<String, Integer> roomOccupants = new TreeMap<String, Integer>();
		
		for(HotelStay H : stays) {
			if(H.getStayStart().compareTo(start) >= 0 && H.getStayEnd().compareTo(end) <= 0) {
				for(RoomBooking r: (H.getReservation()).getRooms()) {
					roomOccupants.put(r.getRoom().getType(), (r.getOccupancy()[0] + r.getOccupancy()[1]) );
				}
			}
		}
		return roomOccupants;
	}

}