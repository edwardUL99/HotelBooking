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
		 * @param dailyTotals the total income for each room at each day of the range between start and end
		 * @param averages the TreeMap of average income per room
		 * @param totals the TreeMap of total income per room
		 * @param days the dates to include
		 * @param returns the filename
		 */
		private String writeBillingInfoToFile(LocalDate start, LocalDate end, TreeMap<Room, ArrayList<Double>> dailyTotals, TreeMap<Room, Double> averages, TreeMap<Room, Double> totals, ArrayList<LocalDate> days) {
			String fileName = String.format("/data/dataAnalysis/%s_billing_%s_to_%s.csv", this.hotelName, start.toString(), end.toString());
			String filePath = System.getProperty("user.dir") + fileName;
			String[] attributes = {"Hotel Name", "Rooms", "Number of Rooms Booked in Period", "Average Income Per Room", "Total Income Per Room"};
			int numDays = days.size();
			Object[][] data = new Object[averages.size() + 2][attributes.length + numDays];
			int aIndex = 0; 
			TreeMap<Room, Double> recordedTotals = new TreeMap<Room, Double>();
			LocalDate date = start;
			for (int i = 0; i < attributes.length + numDays; i++) {
				if (i <= 1 || i > numDays + 1) {
					data[0][i] = attributes[aIndex++];
				} else {
					data[0][i] = date.toString();
					date = date.plusDays(1);
				}
			}			
			int row = 1;
			row = 1;
			int lastIndex = 1;
			for (Map.Entry<Room, ArrayList<Double>> e : dailyTotals.entrySet()) {
				lastIndex = 1;
				data[row][lastIndex++] = e.getKey().getType();
				for (int i = 0; i < numDays; i++) {
					data[row][lastIndex++] = "€" + e.getValue().get(i);
				}
				row++;
			}
			row = 1;
			for (Map.Entry<Room, Double> e : averages.entrySet()) {
				data[row][0] = "";
				data[row][lastIndex] = this.getNumberOfRooms(e.getKey(), start, end, days);
				data[row][lastIndex+1] = String.format("€%.02f", e.getValue());
				row++;
			}
			
			row = 1;
			lastIndex += 2;
			for (Map.Entry<Room, Double> e : totals.entrySet()) {
				data[row][lastIndex] = String.format("€%.02f", e.getValue());
				if (!recordedTotals.containsKey(e.getKey())) {
					recordedTotals.put(e.getKey(), e.getValue());
				}
				row++;
			}
			
			lastIndex -= 1;
			
			for (int i = 0; i < lastIndex; i++) {
				data[row][i] = "";
			}
			double total = this.sumUpTotalIncomes(recordedTotals);
			data[row][lastIndex] = "Total: €" + String.format("%.02f", total);
			data[row][lastIndex+1] = "Total Average: €" + String.format("%.02f", total/(double)recordedTotals.size());
			data[1][0] = this.hotelName;
			writeDataToFile(filePath, data);
			return fileName;
		}
	
		/**
		 * Takes a TreeMap of total income mapped to to rooms and returns it as one summed up total
		 * @param totals the TreeMap containing all the totals 
		 * @return the totals summed up
		 */
		private double sumUpTotalIncomes(TreeMap<Room, Double> totals) {
			double sum = 0;
			for (Map.Entry<Room, Double> e : totals.entrySet()) {
				sum += e.getValue();
			}
			return sum;
		}
		
		private int sumUpTotalOccupants(TreeMap<Room, Integer> totals) {
			int sum = 0;
			for (Entry<Room, Integer> e : totals.entrySet()) {
				sum += e.getValue();
			}
			return sum;
		}
		
		private String writeBillingInfoToFile(LocalDate start, LocalDate end, TreeMap<Room, ArrayList<Double>> dailyTotals, TreeMap<Room, Double> averages, TreeMap<Room, Double> totals) {
			return writeBillingInfoToFile(start, end, dailyTotals, averages, totals, getAllDaysInPeriod(start, end));
		}
		
		private String writeOccupancyInfoToFile(LocalDate start, LocalDate end, TreeMap<Room, ArrayList<Double>> dailyTotals, TreeMap<Room, Double> averages, TreeMap<Room, Double> totals) {
			return writeBillingInfoToFile(start, end, dailyTotals, averages, totals, getAllDaysInPeriod(start, end));
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
	public String writeOccupancyInfoToFile(LocalDate start, LocalDate end, TreeMap<Room, ArrayList<Integer>> dailyTotals, TreeMap<Room, Double> averages, TreeMap<Room, Integer> totals, ArrayList<LocalDate> days) {
		String fileName = String.format("/data/dataAnalysis/%s_occupancy_%s_to_%s.csv", this.hotelName, start.toString(), end.toString());
		String filePath = System.getProperty("user.dir") + fileName;
		String[] attributes = {"Hotel Name", "Rooms", "Number of Rooms Booked in Period", "Average Occupants Per Room", "Total Occupants Per Room"};
		int numDays = days.size();
		Object[][] data = new Object[averages.size() + 2][attributes.length + numDays];
		int aIndex = 0; 
		TreeMap<Room, Integer>recordedTotals = new TreeMap<Room, Integer>();
		LocalDate date = start;
		for (int i = 0; i < attributes.length + numDays; i++) {
			if (i <= 1 || i > numDays + 1) {
				data[0][i] = attributes[aIndex++];
			} else {
				data[0][i] = date.toString();
				date = date.plusDays(1);
			}
		}			
		int row = 1;
		row = 1;
		int lastIndex = 1;
		for (Entry<Room, ArrayList<Integer>> e : dailyTotals.entrySet()) {
			lastIndex = 1;
			data[row][lastIndex++] = e.getKey().getType();
			for (int i = 0; i < numDays; i++) {
				data[row][lastIndex++] = e.getValue().get(i);
			}
			row++;
		}
		row = 1;
		for (Entry<Room, Double> e : averages.entrySet()) {
			data[row][0] = "";
			data[row][lastIndex] = this.getNumberOfRooms(e.getKey(), start, end, days);
			data[row][lastIndex+1] = String.format("%.02f", e.getValue());
			row++;
		}
		
		row = 1;
		lastIndex += 2;
		for (Map.Entry<Room, Integer> e : totals.entrySet()) {
			data[row][lastIndex] = String.format("%d", e.getValue());
			if (!recordedTotals.containsKey(e.getKey())) {
				recordedTotals.put(e.getKey(), e.getValue());
			}
			row++;
		}
		
		lastIndex -= 1;
		
		for (int i = 0; i < lastIndex; i++) {
			data[row][i] = "";
		}
		int total = this.sumUpTotalOccupants(recordedTotals);
		data[row][lastIndex] = "Total: " + String.format("%d", total);
		data[row][lastIndex+1] = "Total Average: " + String.format("%.02f", total/(double)recordedTotals.size());
		data[1][0] = this.hotelName;
		writeDataToFile(filePath, data);
		return fileName;

	}

	public TreeMap<LocalDate,ArrayList<Double>> getFinancialInfo(LocalDate start, LocalDate end) {
		
		TreeMap<LocalDate, ArrayList<Double>> dateBalance = new TreeMap<LocalDate, ArrayList<Double>>();
		for (HotelStay stay : stays) {
			Reservation r = stay.getReservation();
			if(isReservationInDateRange(r, start, end)) {
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
	 * @param the list of days to calculate total for
	 * @return the total income generated by all rooms in the date period
	 */
	// 1
	private double getTotalForAllRooms(LocalDate start, LocalDate end, ArrayList<LocalDate> days) {
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
	 * @param the days of the period to include
	 * @return the average income generated by all rooms in the date period
	 */
	//2
	private double getAverageForAllRooms(LocalDate start, LocalDate end, ArrayList<LocalDate> days) {
		TreeMap<Room, Double> totals = this.getTotalIncomePerRoom(start, end, days);
		return getTotalForAllRooms(start, end, days) / (double)totals.size();
	}
	
	/**
	 * Returns a TreeMap of total income per room per day in the days lis in the range of start and end
	 * @param start the start date of the period
	 * @param end the end date of the period
	 * @param days the days to calculate the total for
	 * @return a TreeMap with a list of daily totals per room
	 */
	//3
	private TreeMap<Room, ArrayList<Double>> totalsPerRoomPerDay(LocalDate start, LocalDate end, ArrayList<LocalDate> days) {
		TreeMap<Room, ArrayList<Double>> totals = new TreeMap<Room, ArrayList<Double>>();
		if (start.equals(end)) {
			end = end.plusDays(1);
		}
		for (LocalDate date = start; !date.equals(end); date = date.plusDays(1)) {
			if (days.contains(date)) {
				for (Map.Entry<Room, Double> e : getTotalIncomePerRoom(date, date, days).entrySet()) {
					if (!totals.containsKey(e.getKey())) {
						totals.put(e.getKey(), new ArrayList<Double>());
					} 
					totals.get(e.getKey()).add(e.getValue());
				}
			}
		}
		return totals;
	}
	
	/**
	 * Returns all the days between that period, including end
	 * @param start the start date of the period
	 * @param end the end date of the period
	 * @return the ArrayList with all the dates in the period
	 */
	private ArrayList<LocalDate> getAllDaysInPeriod(LocalDate start, LocalDate end) {
		ArrayList<LocalDate> days = new ArrayList<LocalDate>();
		if (start.equals(end)) {
			days.add(start);
		} else {
			for (LocalDate date = start; !date.equals(end); date = date.plusDays(1)) {
				days.add(date);
			}
		}
		return days;
	}
	
	/**
	 * Returns the total for the rooms per day in the date period for all days
	 * @param start the start date of the period
	 * @param end the end date of the period
	 * @return a TreeMap with room mapping to the list of totals of the days for the period
	 */
	//4
	private TreeMap<Room, ArrayList<Double>> totalsPerRoomPerDay(LocalDate start, LocalDate end) {
		return this.totalsPerRoomPerDay(start, end, getAllDaysInPeriod(start, end));
	}
	
	/**
	 * Returns all the totals per room booked in the date range without summing them up or averaging them
	 * i.e. if there are 5 rooms of the same type, it will have all 5 totals in the array list referenced by the room 
	 * @param start the start date of the date period 
	 * @param end the end date of the date period
	 * @param days the days to specify for the total
	 * @return a TreeMap of the room and an ArrayList of all totals for that room in the date period not totalled or averaged
	 */
	private TreeMap<Room, ArrayList<Double>> getAllTotalsPerRoom(LocalDate start, LocalDate end, ArrayList<LocalDate> days) {
		TreeMap<Room, ArrayList<Double>> allTotals = new TreeMap<Room, ArrayList<Double>>();
		for (HotelStay stay : this.stays) {
			Reservation r = stay.getReservation();
			ArrayList<LocalDate> reservationDates = this.dateRangesForReservation(r);
			ArrayList<RoomBooking> roomBookings = r.getRooms();
			for (RoomBooking rb : roomBookings) {
				double totalRate = 0;
				Room rm = rb.getRoom();
				if (start.equals(end)) {
					if (reservationDates.contains(start)) {
							totalRate += rm.getRate(start.getDayOfWeek().getValue() - 1);
					} else {
						totalRate += 0;
					}
				} else {
					for (LocalDate date = start; !date.equals(end); date = date.plusDays(1)) {
						if (reservationDates.contains(date) && days.contains(date)) {
							totalRate += rm.getRate(date.getDayOfWeek().getValue() - 1);
						} else {
							totalRate += 0;
						}
					}
				}
				if (!allTotals.containsKey(rm)) {
					allTotals.put(rm, new ArrayList<Double>());
				} 
				allTotals.get(rm).add(totalRate);
			}	
		}
		return allTotals;
	 }
	
	/**
	 * Requests the average and total income per room for all the days of the date period to be written to a file
	 * @param start the start date of the date period
	 * @param end the end date of the date period
	 * @return the name of the file the information was stored to
	 */
	//6
	public String requestIncomeInformation(LocalDate start, LocalDate end) {
		return requestIncomeInformation(start, end, getAllDaysInPeriod(start, end));
	}
	
	/**
	 * Requests the average and total income per room for the days specified in the date range
	 * @param start the start date of the date period
	 * @param end the end date of the date period
	 * @param days the days of the date period to display total incomes for
	 * @return
	 */
	//7
	public String requestIncomeInformation(LocalDate start, LocalDate end, ArrayList<LocalDate> days) {
		TreeMap<Room, Double> averages = getAverageIncomePerRoom(start, end, days);
		TreeMap<Room, Double> totals = getTotalIncomePerRoom(start, end, days);
		TreeMap<Room, ArrayList<Double>> allTotals = this.totalsPerRoomPerDay(start, end, days);
		String fileName = this.writeBillingInfoToFile(start, end, allTotals, averages, totals, days);
		return fileName.split("/")[fileName.split("/").length - 1];
	}
	
	/**
	 * Returns the number of rooms rm booked in the specified date period by checking how much totals belong to that room from the getAllTotalsPerRoom() method
	 * @param rm the room to find the count of
	 * @param start the start date of the date period
	 * @param end the end date of the date period
	 * @param days the days to restrict however much rooms
	 * @return the count of the room booked during the date period
	 */
	private int getNumberOfRooms(Room rm, LocalDate start, LocalDate end, ArrayList<LocalDate> days) {
		TreeMap<Room, ArrayList<Double>> totals = this.getAllTotalsPerRoom(start, end, days);
		for (Map.Entry<Room, ArrayList<Double>> e : totals.entrySet()) {
			if (e.getKey().equals(rm)) {
				return e.getValue().size();
			}
		}
		return 0;
	}
	
	/**
	 * Returns number of rooms rm booked for all days in the date period
	 * @param rm the room to calculate the count for 
	 * @param start the start of the date period
	 * @param end the end of the date period
	 * @return the count 
	 */
	private int getNumberOfRooms(Room rm, LocalDate start, LocalDate end) {
		return getNumberOfRooms(rm, start, end, getAllDaysInPeriod(start, end));
	}
	
	/**
	 * Returns for each room the average income that room generated on the days specified during the date period 
	 * @param start the start date of the time period
	 * @param end the end date of the time period 
	 * @param days the days to calculate the average for
	 * @return a TreeMap with the average income generated mapped to that room during the date period
	 */
	public TreeMap<Room, Double> getAverageIncomePerRoom(LocalDate start, LocalDate end, ArrayList<LocalDate> days) {
		TreeMap<Room, Double> averages = new TreeMap<Room, Double>();
		for (Map.Entry<Room, ArrayList<Double>> e : this.getAllTotalsPerRoom(start, end, days).entrySet()) {
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
	 * Returns average for each room generated for all the days in the date period
	 * @param start the start date of the date period
	 * @param end the end date of the date period
	 * @return a TreeMap of the average income of the room generated over the whole date period mapped to the room
	 */
	public TreeMap<Room, Double> getAverageIncomePerRoom(LocalDate start, LocalDate end) {
		return getAverageIncomePerRoom(start, end, getAllDaysInPeriod(start, end));
	}
	
	/**
	 * Like the getAverageIncomePerRoom() method, but instead maps the total income generated to the room
	 * @param start the start date of the time period
	 * @param end the end date of the time period
	 * @param days the days to include the totals for
	 * @return a TreeMap of each room mapped to the total income generated for that room in the date period
	 */
	//11
	public TreeMap<Room, Double> getTotalIncomePerRoom(LocalDate start, LocalDate end, ArrayList<LocalDate> days) {
		TreeMap<Room, Double> totals = new TreeMap<Room, Double>();
		for (Map.Entry<Room, ArrayList<Double>> e : this.getAllTotalsPerRoom(start, end, days).entrySet()) {
			double total = 0;
			for (double d : e.getValue()) {
				total += d;
			}
			totals.put(e.getKey(), total);
		}
		return totals;
	}
	
	/**
	 * Gets all totals for all days in the period
	 * @param start the start date of the date period
	 * @param end the end date of the date period
	 * @return the TreeMap with the total income for all days between the period mapped to the corresponding room
	 */
	public TreeMap<Room, Double> getTotalIncomePerRoom(LocalDate start, LocalDate end) {
		return getTotalIncomePerRoom(start, end, getAllDaysInPeriod(start, end));
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
			if(isReservationInDateRange(r, start,end)) {
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
	
	/**
	 * 
	 * @param start
	 * @param end
	 * @param days
	 * @return
	 */
	private TreeMap<Room, ArrayList<Integer>> getAllOccupantsPerRoom(LocalDate start, LocalDate end, ArrayList<LocalDate> days) {
		TreeMap<Room, ArrayList<Integer>> allOccupantTotals = new TreeMap<Room, ArrayList<Integer>>();
		for (HotelStay stay : this.stays) {
			Reservation r = stay.getReservation();
			ArrayList<LocalDate> reservationDates = this.dateRangesForReservation(r);
			ArrayList<RoomBooking> roomBookings = r.getRooms();
			for (RoomBooking rb : roomBookings) {
				int totalOccupants = 0;
				Room rm = rb.getRoom();
				if (start.equals(end)) {
					if (reservationDates.contains(start)) {
							totalOccupants = rb.getOccupancy()[0] + rb.getOccupancy()[1];
					} else {
						totalOccupants += 0;
					}
				} else {
					for (LocalDate date = start; !date.equals(end); date = date.plusDays(1)) {
						if (reservationDates.contains(date) && days.contains(date)) {
							totalOccupants += rb.getOccupancy()[0] + rb.getOccupancy()[1];
						} else {
							totalOccupants += 0;
						}
					}
				}
				if (!allOccupantTotals.containsKey(rm)) {
					allOccupantTotals.put(rm, new ArrayList<Integer>());
				} 
				allOccupantTotals.get(rm).add(totalOccupants);
			}	
		}
		return allOccupantTotals;
	 }
	/**
	 * 
	 * @param start
	 * @param end
	 * @param days
	 * @return
	 */
	public TreeMap<Room, Double> getAverageOccupantsPerRoom(LocalDate start, LocalDate end, ArrayList<LocalDate> days) {
		TreeMap<Room, Double> averages = new TreeMap<Room, Double>();
		for (Entry<Room, ArrayList<Integer>> e : this.getAllOccupantsPerRoom(start, end, days).entrySet()) {
			double average = 0;
			for (int d : e.getValue()) {
				average += (double)d;
			}
			average /= (double)e.getValue().size();
			averages.put(e.getKey(), (double)average);
		}
		return averages;
	}
	
	public TreeMap<Room, Double> getAverageOccupantsPerRoom(LocalDate start, LocalDate end) {
		return getAverageOccupantsPerRoom(start, end, getAllDaysInPeriod(start, end));
	}

	/**
	 * Returns the total income over the date period for all rooms that have been booked for this date period
	 * @param start the start date of the date period
	 * @param end the end date for the date period
	 * @param the list of days to calculate total for
	 * @return the total income generated by all rooms in the date period
	 */
	// 1
	private double getTotalOccupantsForAllRooms(LocalDate start, LocalDate end, ArrayList<LocalDate> days) {
		int total = 0;
		for (Entry<Room, Integer> e : this.getTotalOccupantsPerRoom(start, end).entrySet()) {
			total += e.getValue();
		}
		return total;
	}
	
	/**
	 * Returns the average income earned by all rooms that have been booked for this date period
	 * @param start the start date of the time period
	 * @param end the end date of the time period
	 * @param the days of the period to include
	 * @return the average income generated by all rooms in the date period
	 */
	//2
	private double getAverageOccupantsForAllRooms(LocalDate start, LocalDate end, ArrayList<LocalDate> days) {
		TreeMap<Room, Integer> totals = this.getTotalOccupantsPerRoom(start, end, days);
		return getTotalForAllRooms(start, end, days) / totals.size();
	}
	
	/**
	 * Returns a TreeMap of total income per room per day in the days lis in the range of start and end
	 * @param start the start date of the period
	 * @param end the end date of the period
	 * @param days the days to calculate the total for
	 * @return a TreeMap with a list of daily totals per room
	 */
	//3
	private TreeMap<Room, ArrayList<Integer>> occupantsPerRoomPerDay(LocalDate start, LocalDate end, ArrayList<LocalDate> days) {
		TreeMap<Room, ArrayList<Integer>> totals = new TreeMap<Room, ArrayList<Integer>>();
		if (start.equals(end)) {
			end = end.plusDays(1);
		}
		for (LocalDate date = start; !date.equals(end); date = date.plusDays(1)) {
			if (days.contains(date)) {
				for (Map.Entry<Room, Integer> e : getTotalOccupantsPerRoom(date, date, days).entrySet()) {
					if (!totals.containsKey(e.getKey())) {
						totals.put(e.getKey(), new ArrayList<Integer>());
					} 
					totals.get(e.getKey()).add(e.getValue());
				}
			}
		}
		return totals;
	}
	
	/**
	 * Returns the total for the rooms per day in the date period for all days
	 * @param start the start date of the period
	 * @param end the end date of the period
	 * @return a TreeMap with room mapping to the list of totals of the days for the period
	 */
	//4
	private TreeMap<Room, ArrayList<Double>> occupantsPerRoomPerDay(LocalDate start, LocalDate end) {
		return this.totalsPerRoomPerDay(start, end, getAllDaysInPeriod(start, end));
	}
	
	/**
	 * Requests the average and total income per room for all the days of the date period to be written to a file
	 * @param start the start date of the date period
	 * @param end the end date of the date period
	 * @return the name of the file the information was stored to
	 */
	//6
	public String requestOccupantInformation(LocalDate start, LocalDate end) {
		return requestOccupantInformation(start, end, getAllDaysInPeriod(start, end));
	}
	
	/**
	 * Requests the average and total income per room for the days specified in the date range
	 * @param start the start date of the date period
	 * @param end the end date of the date period
	 * @param days the days of the date period to display total incomes for
	 * @return
	 */
	//7
	public String requestOccupantInformation(LocalDate start, LocalDate end, ArrayList<LocalDate> days) {
		TreeMap<Room, Double> averages = getAverageOccupantsPerRoom(start, end, days);
		TreeMap<Room, Integer> totals = getTotalOccupantsPerRoom(start, end, days);
		TreeMap<Room, ArrayList<Integer>> allTotals = this.occupantsPerRoomPerDay(start, end, days);
		String fileName = this.writeOccupancyInfoToFile(start, end, allTotals, averages, totals, days);
		return fileName.split("/")[fileName.split("/").length - 1];
	}
	
	/**
	 * Like the getAverageIncomePerRoom() method, but instead maps the total income generated to the room
	 * @param start the start date of the time period
	 * @param end the end date of the time period
	 * @param days the days to include the totals for
	 * @return a TreeMap of each room mapped to the total income generated for that room in the date period
	 */
	//11
	public TreeMap<Room, Integer> getTotalOccupantsPerRoom(LocalDate start, LocalDate end, ArrayList<LocalDate> days) {
		TreeMap<Room, Integer> totals = new TreeMap<Room, Integer>();
		for (Map.Entry<Room, ArrayList<Integer>> e : this.getAllOccupantsPerRoom(start, end, days).entrySet()) {
			Integer total = 0;
			for (int d : e.getValue()) {
				total += d;
			}
			totals.put(e.getKey(), total);
		}
		return totals;
	}
	
	/**
	 * Gets all totals for all days in the period
	 * @param start the start date of the date period
	 * @param end the end date of the date period
	 * @return the TreeMap with the total income for all days between the period mapped to the corresponding room
	 */
	public TreeMap<Room, Integer> getTotalOccupantsPerRoom(LocalDate start, LocalDate end) {
		return getTotalOccupantsPerRoom(start, end, getAllDaysInPeriod(start, end));
	}
}