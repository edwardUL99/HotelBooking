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
		private String hotelName;
		private ArrayList<HotelStay> stays;
		
		/**
		 * Creates a DataAnalysis class hotel stays.
		 * @param hotelName the name of the hotel this DataAnalysis object is working in
		 * @param stays
		 */
		public DataAnalysis(String hotelName, ArrayList<HotelStay> stays) {
			this.hotelName = hotelName;
			this.stays = stays;
		}
		
		/**
		 * Writes billing info to a file each per date period specified
		 * @param start the start date of the period
		 * @param end the end date of the period
		 * @param averages the TreeMap of average income per room
		 * @param totals the TreeMap of total income per room
		 * @param returns the filename
		 */
		private String writeBillingInfoToFile(LocalDate start, LocalDate end, TreeMap<Room, Double> averages, TreeMap<Room, Double> totals) {
			String fileName = String.format("/data/dataAnalysis/%s_billing_%s_to_%s.csv", this.hotelName, start.toString(), end.toString());
			String filePath = System.getProperty("user.dir") + fileName;
			String[] attributes = {"Hotel Name", "Rooms", "Average Income Per Room", "Total Income Per Room"};
			Object[][] data = new Object[averages.size() + 2][attributes.length];
			for (int i = 0; i < attributes.length; i++) {
				data[0][i] = attributes[i];
			}
			int row = 1;
			data[1][0] = this.hotelName;
			for (Map.Entry<Room, Double> e : averages.entrySet()) {
				data[row][0] = "";
				data[row][1] = e.getKey();
				data[row][2] = String.format("€%.02f", e.getValue());
				row++;
			}
			row = 1;
			for (Map.Entry<Room, Double> e : totals.entrySet()) {
				data[row][0] = "";
				data[row][3] = String.format("€%.02f", e.getValue());
				row++;
			}
			data[row][0] = "";
			data[row][1] = "";
			data[row][2] = "Total: €" + this.getTotalForAllRooms(start, end);
			data[row][3] = "Total Average: €" + this.getAverageForAllRooms(start, end);
			writeDataToFile(filePath, data);
			return fileName;
		}
		
		/**
		 * Writes the data Object 2D array to the csv file specified by the file path 
		 * @param filePath the path of the file to write to
		 * @param data the 2D array of the data
		 */
		private void writeDataToFile(String filePath, Object[][] data) {
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
		
		public void writeFinancialInfoToFile(LocalDate start, LocalDate end,TreeMap<String, ArrayList<Reservation>> reservations, String hotelName) {
			TreeMap<LocalDate,ArrayList<Double>> financialInfo = getFinancialInfo(start, end);
			
			String[] attributes = {"Date","Deposit","Reservation Cost","Total Cost"};
			
			int rows = financialInfo.size();
			int columns = attributes.length;
			
			Object[][] data = new String[rows][columns];
			
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
	
	/**
	 * Writes occupancy info for the specified date period to a file
	 * @param start the start date of the date period
	 * @param end the end date of the date period
	 */
	public void writeOccupancyInfoToFile(LocalDate start, LocalDate end) {
		TreeMap<Room,Integer> occupancyInfo = getOccupancyInfo(start, end);

		String[] attributes = {"RoomType, numberOfOccupants"};
		
		int rows = occupancyInfo.size();
		int columns = attributes.length;
		
		Object[][] data = new String[rows][columns];
		
		data[0][0] = attributes[0]; 
		data[0][1] = attributes[1];
		
		int row = 1;
				
		for (Entry<Room, Integer> e : occupancyInfo.entrySet()) {
				data[row][1] = e.getKey().getType();  
				data[row][2] = e.getValue();
		}

	String fileName = "/data/dataAnalysis/OccupancyInfo.csv";
	String path = System.getProperty("user.dir") + fileName;
	writeDataToFile(path, data);
	}

	public TreeMap<LocalDate,ArrayList<Double>> getFinancialInfo(LocalDate start, LocalDate end) {
		
		TreeMap<LocalDate, ArrayList<Double>> dateBalance = new TreeMap<LocalDate, ArrayList<Double>>();
		for (HotelStay stay : stays) {
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
	
	/* may not be needed
	private boolean dateLessThan(LocalDate x, LocalDate y) {
		return x.isBefore(y);
	}
	
	private boolean dateGreaterThan(LocalDate x, LocalDate y) {
		return x.isAfter(y);
	}
	*/
	
	/**
	 * Checks if a reservation is made in the date period specified by the start and end dates. i.e.
	 * If the whole reservation is in the date period, it is trivially in range
	 * For example: 
	 * 
	 * 	Reservation r1 = 20/08/2020 - 25/08/2020 and start = 20/08/2020 end = 25/08/2020
	 * 	r1 is trivially in the range
	 *  returns true
	 * However, if only part of the reservation lies in range it is still in this period
	 * For Example: 
	 * 	Reservation 21 = 15/08/2020 - 24/08/2020 and start = 20/08/2020 end = 25/08/2020
	 * 	returns true
	 * @param r the reservation to check in the date period
	 * @param start the start date of the period
	 * @param end the end date of the period
	 * @return if the reservation is in range
	 */
	private boolean isReservationInDateRange(Reservation r, LocalDate start, LocalDate end) {
		ArrayList<LocalDate> range = this.dateRangesForReservation(r);
		if (start.equals(end)) {
			if (range.contains(start)) {
				return true;
			}
		} else {
			for (LocalDate date = start; !date.equals(end); date = date.plusDays(1)) {
				if (range.contains(date)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns a list of dates over which this reservation spans from check in date up to but not including, the checkout date
	 * @param r the reservation to get the dates spanned 
	 * @return an ArrayList of the range of dates the reservation spans over
	 */
	private ArrayList<LocalDate> dateRangesForReservation(Reservation r) {
		ArrayList<LocalDate> range = new ArrayList<LocalDate>();
		for (LocalDate date = r.getCheckinDate(); !date.equals(r.getCheckoutDate()); date = date.plusDays(1)) {
			range.add(date);
		}
		return range;
	}
	
	/**
	 * Returns the total income over the date period for all rooms that have been booked for this date period
	 * @param start the start date of the date period
	 * @param end the end date for the date period
	 * @return the toal income generated by all rooms in the date period
	 */
	private double getTotalForAllRooms(LocalDate start, LocalDate end) {
		double total = 0;
		for (Map.Entry<Room, Double> e : this.getTotalIncomePerRoom(start, end).entrySet()) {
			total += e.getValue();
		}
		return total;
	}
	
	/**
	 * Returns the average income earned by all rooms that have been booked for this date period
	 * @param start the start date of the time period
	 * @param end the end date of the time period
	 * @return the average income generated by all rooms in the date period
	 */
	private double getAverageForAllRooms(LocalDate start, LocalDate end) {
		TreeMap<Room, Double> totals = this.getTotalIncomePerRoom(start, end);
		return getTotalForAllRooms(start, end) / (double)totals.size();
	}
	
	/**
	 * Returns all the totals per room booked in the date range without summing them up or averaging them
	 * i.e. if there are 5 rooms of the same type, it will have all 5 totals in the array list referenced by the room 
	 * @param start the start date of the date period 
	 * @param end the end date of the date period
	 * @return a TreeMap of the room and an ArrayList of all totals for that room in the date period not totalled or averaged
	 */
	private TreeMap<Room, ArrayList<Double>> getAllTotalsPerRoom(LocalDate start, LocalDate end) {
		TreeMap<Room, ArrayList<Double>> allTotals = new TreeMap<Room, ArrayList<Double>>();
		for (HotelStay stay : this.stays) {
			Reservation r = stay.getReservation();
			ArrayList<LocalDate> reservationDates = this.dateRangesForReservation(r);
			if (this.isReservationInDateRange(r, start, end)) {
				ArrayList<RoomBooking> roomBookings = r.getRooms();
				for (RoomBooking rb : roomBookings) {
					double totalRate = 0;
					Room rm = rb.getRoom();
					if (start.equals(end)) {
						if (reservationDates.contains(start)) {
							totalRate += rm.getRate(start.getDayOfWeek().getValue() - 1);
						}
					} else {
						for (LocalDate date = start; !date.equals(end); date = date.plusDays(1)) {
							if (reservationDates.contains(date)) {
								totalRate += rm.getRate(date.getDayOfWeek().getValue() - 1);
							}
						}
					}
					if (totalRate != 0) {
						if (!allTotals.containsKey(rm)) {
							allTotals.put(rm, new ArrayList<Double>());
						} 
						allTotals.get(rm).add(totalRate);
					}
				}
			}	
		}
		return allTotals;
	 }
	
	/**
	 * Requests the average and total income per room for the date period to be written to a file
	 * @param start the start date of the date period
	 * @param end the end date of the date period
	 * @return the name of the file the information was stored to
	 */
	public String requestIncomeInformation(LocalDate start, LocalDate end) {
		TreeMap<Room, Double> averages = getAverageIncomePerRoom(start, end);
		TreeMap<Room, Double> totals = getTotalIncomePerRoom(start, end);
		String fileName = this.writeBillingInfoToFile(start, end, averages, totals);
		return fileName.split("/")[fileName.split("/").length - 1];
	}
	
	/**
	 * Returns for each room the average income that room generated during the date period 
	 * @param start the start date of the time period
	 * @param end the end date of the time period 
	 * @return a TreeMap with the average income generated mapped to that room during the date period
	 */
	public TreeMap<Room, Double> getAverageIncomePerRoom(LocalDate start, LocalDate end) {
		TreeMap<Room, Double> averages = new TreeMap<Room, Double>();
		for (Map.Entry<Room, ArrayList<Double>> e : this.getAllTotalsPerRoom(start, end).entrySet()) {
			double average = 0;
			for (double d : e.getValue()) {
				average += d;
			}
			average /= e.getValue().size();
			averages.put(e.getKey(), average);
		}
		return averages;
	}
	
	/**
	 * Like the getAverageIncomePerRoom() method, but instead maps the total income generated to the room
	 * @param start the start date of the time period
	 * @param end the end date of the time period
	 * @return a TreeMap of each room mapped to the total income generated for that room in the date period
	 */
	public TreeMap<Room, Double> getTotalIncomePerRoom(LocalDate start, LocalDate end) {
		TreeMap<Room, Double> totals = new TreeMap<Room, Double>();
		for (Map.Entry<Room, ArrayList<Double>> e : this.getAllTotalsPerRoom(start, end).entrySet()) {
			double total = 0;
			for (double d : e.getValue()) {
				total += d;
			}
			totals.put(e.getKey(), total);
		}
		return totals;
	}
	
	//What's this method doing? do we need it?
	public double getTotalEarned(LocalDate start, LocalDate end) {
		TreeMap<LocalDate, ArrayList<Double>> dateTotalCost = getFinancialInfo(start, end);
		int total = 0;
		for (ArrayList<Double> totalCost : dateTotalCost.values()) {
			for (double amount : totalCost) {
				total += amount;
			}
		}
		return total;
	}
	
	/**
	 * Returns the occupancy of a room in the date period
	 * @param start the start date of the period
	 * @param end the end date of the period
	 * @return a TreeMap with the number of occupants in the room 
	 */
	public TreeMap<Room, Integer> getOccupancyInfo(LocalDate start, LocalDate end) {
		//may need to change to an arraylist of integer as many of the same room can be booked
		TreeMap<Room, Integer> roomOccupants = new TreeMap<Room, Integer>();
		
		for (HotelStay stay : stays) {
			Reservation r = stay.getReservation();
			if(r.getCheckinDate().compareTo(start) >= 0 && r.getCheckinDate().compareTo(end) <= 0) {
				for (RoomBooking rm: r.getRooms()) {
					if (!roomOccupants.containsKey(rm.getRoom())) {
						roomOccupants.put(rm.getRoom(), (rm.getOccupancy()[0] + rm.getOccupancy()[1]));
					} else {
						roomOccupants.put(rm.getRoom(), roomOccupants.get(rm.getRoom()) + rm.getOccupancy()[0] + rm.getOccupancy()[1]); //be careful not to overwrite rooms in the treemap
					}
				}
			}
		}
		return roomOccupants;
	}

}