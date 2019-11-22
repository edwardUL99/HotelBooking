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
	
	private boolean dateLessThan(LocalDate x, LocalDate y) {
		return x.isBefore(y);
	}
	
	private boolean dateGreaterThan(LocalDate x, LocalDate y) {
		return x.isAfter(y);
	}
	
	
	private boolean isReservationInDateRange(Reservation r, LocalDate start, LocalDate end) {
		LocalDate checkIn = r.getCheckinDate();
		LocalDate checkOut = r.getCheckoutDate();
		return dateLessThan(start, checkIn) || start.equals(checkIn) && dateGreaterThan(end, checkOut) || end.equals(checkOut);
	}
	
	private ArrayList<LocalDate> dateRangesForReservation(Reservation r) {
		ArrayList<LocalDate> range = new ArrayList<LocalDate>();
		for (LocalDate date = r.getCheckinDate(); !date.equals(r.getCheckoutDate()); date = date.plusDays((long)1)) {
			range.add(date);
		}
		return range;
	}
	
	private TreeMap<Room, Double> calculateAverage(TreeMap<Room, ArrayList<Double>> allRates) {
		TreeMap<Room, Double> averages = new TreeMap<Room, Double>();
		for (Map.Entry<Room, ArrayList<Double>> e : allRates.entrySet()) {
			double average = 0;
			for (double d : e.getValue()) {
				average += d;
			}
			average /= e.getValue().size();
			averages.put(e.getKey(), average);
		}
		return averages;
	}
	
	public TreeMap<Room, Double> getAverageIncomePerRoom(LocalDate start, LocalDate end) {
		TreeMap<Room, ArrayList<Double>> tempAverages = new TreeMap<Room, ArrayList<Double>>();
		for (HotelStay stay : this.stays) {
			Reservation r = stay.getReservation();
			if (isReservationInDateRange(r, start, end)) {
				ArrayList<RoomBooking> roomBookings = r.getRooms();
				ArrayList<LocalDate> reservationDates = this.dateRangesForReservation(r);
				for (RoomBooking rb : roomBookings) {
					Room rm = rb.getRoom();
					for (LocalDate date = start; !date.equals(end); date = date.plusDays((long)1)) {
						if (reservationDates.contains(date)) {
							double rate = rm.getRate(date.getDayOfWeek().getValue() - 1);
							if (!tempAverages.containsKey(rm)) {
								tempAverages.put(rm, new ArrayList<Double>());
							} 
							tempAverages.get(rm).add(rate);
						}
					}
				}
			}
		}
		return calculateAverage(tempAverages);
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
		
		for(HotelStay stay : stays) {
			Reservation r = stay.getReservation();
			if(r.getCheckinDate().compareTo(start) >= 0 && r.getCheckinDate().compareTo(end) <= 0) {
				for(RoomBooking rm: r.getRooms()) {
					roomOccupants.put(rm.getRoom().getType(), (rm.getOccupancy()[0] + rm.getOccupancy()[1]) );
				}
			}
		}
		return roomOccupants;
	}

}