import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class DataAnalysis {
	private String hotelName;
	private ArrayList<HotelStay> stays;

	/**
	 * Creates a DataAnalysis class hotel stays.
	 * 
	 * @param hotelName the name of the hotel this DataAnalysis object is working in
	 * @param stays the list of HotelStays in the hotel
	 */
	public DataAnalysis(String hotelName, ArrayList<HotelStay> stays) {
		this.hotelName = hotelName;
		this.stays = stays;
	}

	/*
	 * Writes billing info to a file each per date period specified
	 * 
	 * @param start the start date of the period
	 * 
	 * @param end the end date of the period
	 * 
	 * @param dailyTotals the total income for each room at each day of the range
	 * between start and end
	 * 
	 * @param averages the TreeMap of average income per room
	 * 
	 * @param totals the TreeMap of total income per room
	 * 
	 * @param days the dates to include
	 * 
	 * @param returns the filename
	 */
	private String writeBillingInfoToFile(LocalDate start, LocalDate end, TreeMap<Room, ArrayList<Double>> dailyTotals,
			TreeMap<Room, Double> averages, TreeMap<Room, Double> totals, ArrayList<LocalDate> days) {
		String fileName = String.format("/data/dataAnalysis/%s_billing_%s_to_%s.csv", this.hotelName, start.toString(),
				end.toString());
		String filePath = System.getProperty("user.dir") + fileName;
		String[] attributes = { "Hotel Name", "Rooms", "Number of Rooms Booked in Period", "Average Income Per Room",
				"Total Income Per Room" };
		int numDays = days.size();
		Object[][] data = new Object[averages.size() + 2][attributes.length + numDays];
		int aIndex = 0;
		int dateIndex = 0;
		TreeMap<Room, Double> recordedTotals = new TreeMap<Room, Double>();
		for (int i = 0; i < attributes.length + numDays; i++) {
			if (i <= 1 || i > numDays + 1) {
				data[0][i] = attributes[aIndex++];
			} else {
				data[0][i] = days.get(dateIndex++).toString();
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
			data[row][lastIndex + 1] = String.format("€%.02f", e.getValue());
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
		data[row][lastIndex + 1] = "Total Average: €" + String.format("%.02f", total / (double) recordedTotals.size());
		data[1][0] = this.hotelName;
		writeDataToFile(filePath, data);
		return fileName;
	}

	/*
	 * Takes a TreeMap of total income mapped to to rooms and returns it as one
	 * summed up total
	 * 
	 * @param totals the TreeMap containing all the totals
	 * 
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

	/*
	 * Writes the data Object 2D array to the csv file specified by the file path
	 * 
	 * @param filePath the path of the file to write to
	 * 
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
				line = line.substring(0, line.length() - 1); // removes the last ","
				writer.println(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Writes occupancy info for the specified date period to a file
	 * 
	 * @param start the start date of the date period
	 * 
	 * @param end the end date of the date period
	 * 
	 * @param dailyTotals the total number of occupants or rooms per day
	 * 
	 * @param averages the average numbers per room
	 * 
	 * @param averages the average numbers per room per day
	 * 
	 * @param totals the total numbers per room
	 * 
	 * @param days the days to include in the analysis
	 * 
	 * @return the filename the info was written to
	 */
	private String writeOccupantNumbersToFile(LocalDate start, LocalDate end,
			TreeMap<Room, ArrayList<Integer>> dailyTotals, TreeMap<Room, Double> averages,TreeMap<Room, Double> averagesPerDay,
			TreeMap<Room, Integer> totals, ArrayList<LocalDate> days) {
		String fileName = String.format("/data/dataAnalysis/%s_occupancy__occupant_numbers_%s_to_%s.csv",
				this.hotelName, start.toString(), end.toString());
		String filePath = System.getProperty("user.dir") + fileName;
		String[] attributes = { "Hotel Name", "Rooms", "Number of Rooms Booked in Period", "Average Occupants Per Room",
				"Average Occupants Per Room Per Day","Total Occupants Per Room" };
		int numDays = days.size();
		Object[][] data = new Object[averages.size() + 2][attributes.length + numDays];
		int aIndex = 0;
		int dateIndex = 0;
		TreeMap<Room, Integer> recordedTotals = new TreeMap<Room, Integer>();
		for (int i = 0; i < attributes.length + numDays; i++) {
			if (i <= 1 || i > numDays + 1) {
				data[0][i] = attributes[aIndex++];
			} else {
				data[0][i] = days.get(dateIndex++).toString();
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
			data[row][lastIndex + 1] = String.format("%.02f", e.getValue());
			row++;
		}
		
		row = 1;
		lastIndex += 1;
		for (Entry<Room, Double> e : averagesPerDay.entrySet()) {
			data[row][0] = "";
			data[row][lastIndex] = this.getNumberOfRooms(e.getKey(), start, end, days);
			data[row][lastIndex + 1] = String.format("%.02f", e.getValue());
			row++;
		}

		row = 1;
		lastIndex += 2;
		for (Map.Entry<Room, Integer> e : totals.entrySet()) {
			data[row][lastIndex] = e.getValue();
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
		data[row][lastIndex] = "Total: " + total;
		data[row][lastIndex + 1] = "Total Average: " + String.format("%.02f", total / (double) recordedTotals.size());
		data[1][0] = this.hotelName;
		writeDataToFile(filePath, data);
		return fileName;

	}

	private String writeOccupancyRoomNumbersToFile(LocalDate start, LocalDate end,
			TreeMap<Room, ArrayList<Integer>> dailyCounts, TreeMap<Room, Integer> totals, ArrayList<LocalDate> days,
			TreeMap<Room, Integer> hotelRooms) {
		String fileName = String.format("/data/dataAnalysis/%s_occupancy_room_numbers_%s_to_%s.csv", this.hotelName,
				start.toString(), end.toString());
		String filePath = System.getProperty("user.dir") + fileName;
		String[] attributes = { "Hotel Name", "Room", "Total Number Of Rooms Booked", "Ratio of Rooms Booked" };
		int numDays = days.size();
		Object[][] data = new Object[totals.size() + 1][attributes.length + numDays];
		int aIndex = 0;
		int dateIndex = 0;
		for (int i = 0; i < attributes.length + numDays; i++) {
			if (i <= 1 || i > numDays + 1) {
				data[0][i] = attributes[aIndex++];
			} else {
				data[0][i] = days.get(dateIndex++).toString();
			}
		}
		int lastIndex = 1;
		int row = 1;
		for (Map.Entry<Room, ArrayList<Integer>> e : dailyCounts.entrySet()) {
			lastIndex = 1;
			data[row][lastIndex++] = e.getKey();
			int ind = 0;
			for (int i = 0; i < numDays; i++) {
				if (i < e.getValue().size()) {
					data[row][lastIndex++] = e.getValue().get(ind++);
				} else {
					data[row][lastIndex++] = 0;
				}
			}
			row++;
		}

		row = 1;
		for (Map.Entry<Room, Integer> e : totals.entrySet()) {
			data[row][0] = "";
			data[row++][lastIndex] = e.getValue();
		}

		lastIndex++;
		row = 1;
		for (Map.Entry<Room, Integer> e : totals.entrySet()) {
			data[row++][lastIndex] = String.format("%d\\%d", e.getValue(), hotelRooms.get(e.getKey()));
		}
		data[1][0] = this.hotelName;
		this.writeDataToFile(filePath, data);
		return fileName;
	}

	private TreeMap<Room, ArrayList<Integer>> getDailyRoomCount(LocalDate start, LocalDate end, ArrayList<LocalDate> days) {
		TreeMap<Room, ArrayList<Integer>> dailyTotals = new TreeMap<Room, ArrayList<Integer>>();
		if (start.equals(end)) {
			end = end.plusDays(1);
		} 
		for (LocalDate date = start; !date.equals(end); date = date.plusDays(1)) {
			if (days.contains(date)) {
				for (Map.Entry<Room, Integer> e : getAllTotalRoomCount(date, date, days).entrySet()) {
					if (!dailyTotals.containsKey(e.getKey())) {
						dailyTotals.put(e.getKey(), new ArrayList<Integer>());
					}
					dailyTotals.get(e.getKey()).add(e.getValue());
				}
			}
		}
		return dailyTotals;
	}

	/*
	 * Totals up the number of times a room was booked over the period of time i.e
	 * if 1 room was booked for 7 days that room would be mapped to 7 This method is
	 * mostly just used for the dailyRoomCount method
	 */
	private TreeMap<Room, Integer> getAllTotalRoomCount(LocalDate start, LocalDate end, ArrayList<LocalDate> days) {
		TreeMap<Room, Integer> totals = new TreeMap<Room, Integer>();
		for (HotelStay stay : this.stays) {
			Reservation r = stay.getReservation();
			ArrayList<LocalDate> reservationDates = this.dateRangesForReservation(r);
			ArrayList<RoomBooking> roomBookings = r.getRooms();
			for (RoomBooking rb : roomBookings) {
				Room rm = rb.getRoom();
				if (start.equals(end)) {
					if (reservationDates.contains(start)) {
						if (!totals.containsKey(rm)) {
							totals.put(rm, 1);
						} else {
							totals.put(rm, totals.get(rm) + 1);
						}
					}
				} else {
					for (LocalDate date = start; !date.equals(end); date = date.plusDays(1)) {
						if (reservationDates.contains(date) && days.contains(date)) {
							if (!totals.containsKey(rm)) {
								totals.put(rm, 1);
							} else {
								totals.put(rm, totals.get(rm) + 1);
							}
						}
					}
				}
			}
		}
		return totals;
	}

	private TreeMap<Room, Integer> getTotalRoomCountPerPeriod(LocalDate start, LocalDate end, ArrayList<LocalDate> days) {
		TreeMap<Room, Integer> counts = new TreeMap<Room, Integer>();
		for (Room r : this.getAllTotalRoomCount(start, end, days).keySet()) {
			counts.put(r, this.getNumberOfRooms(r, start, end, days));
		}
		return counts;
	}

	/*
	 * Returns a list of dates over which this reservation spans from check in date
	 * up to but not including, the checkout date
	 * 
	 * @param r the reservation to get the dates spanned
	 * 
	 * @return an ArrayList of the range of dates the reservation spans over
	 */
	private ArrayList<LocalDate> dateRangesForReservation(Reservation r) {
		ArrayList<LocalDate> range = new ArrayList<LocalDate>();
		for (LocalDate date = r.getCheckinDate(); !date.equals(r.getCheckoutDate()); date = date.plusDays(1)) {
			range.add(date);
		}
		return range;
	}

	/*
	 * Returns a TreeMap of total income per room per day in the days list in the
	 * range of start and end
	 * 
	 * @param start the start date of the period
	 * 
	 * @param end the end date of the period
	 * 
	 * @param days the days to calculate the total for
	 * 
	 * @return a TreeMap with a list of daily totals per room
	 */
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

	/*
	 * Returns all the days between that period, including end
	 * 
	 * @param start the start date of the period
	 * 
	 * @param end the end date of the period
	 * 
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

	/*
	 * Returns all the totals per room booked in the date range without summing them
	 * up or averaging them i.e. if there are 5 rooms of the same type, it will have
	 * all 5 totals in the array list referenced by the room
	 * 
	 * @param start the start date of the date period
	 * 
	 * @param end the end date of the date period
	 * 
	 * @param days the days to specify for the total
	 * 
	 * @return a TreeMap of the room and an ArrayList of all totals for that room in
	 * the date period not totalled or averaged
	 */
	private TreeMap<Room, ArrayList<Double>> getAllTotalsPerRoom(LocalDate start, LocalDate end,
			ArrayList<LocalDate> days) {
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
	 * Requests the average and total income per room for all the days of the date
	 * period to be written to a file
	 * 
	 * @param start the start date of the date period
	 * @param end   the end date of the date period
	 * @return the name of the file the information was stored to
	 */
	public String requestIncomeInformation(LocalDate start, LocalDate end) {
		return requestIncomeInformation(start, end, getAllDaysInPeriod(start, end));
	}

	/**
	 * Requests the average and total income per room for the days specified in the
	 * date range
	 * 
	 * @param start the start date of the date period
	 * @param end the end date of the date period
	 * @param days the days of the date period to display total incomes for
	 * @return the file name of where the analysis was saved to
	 */
	public String requestIncomeInformation(LocalDate start, LocalDate end, ArrayList<LocalDate> days) {
		TreeMap<Room, Double> averages = getAverageIncomePerRoom(start, end, days);
		TreeMap<Room, Double> totals = getTotalIncomePerRoom(start, end, days);
		TreeMap<Room, ArrayList<Double>> allTotals = this.totalsPerRoomPerDay(start, end, days);
		String fileName = this.writeBillingInfoToFile(start, end, allTotals, averages, totals, days);
		return fileName.split("/")[fileName.split("/").length - 1];
	}

	/*
	 * Returns the number of rooms rm booked in the specified date period by
	 * checking how much totals belong to that room from the getAllTotalsPerRoom()
	 * method
	 * 
	 * @param rm the room to find the count of
	 * 
	 * @param start the start date of the date period
	 * 
	 * @param end the end date of the date period
	 * 
	 * @param days the days to restrict however much rooms
	 * 
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

	/*
	 * Returns for each room the average income that room generated on the days
	 * specified during the date period
	 * 
	 * @param start the start date of the time period
	 * @param end   the end date of the time period
	 * @param days  the days to calculate the average for
	 * @return a TreeMap with the average income generated mapped to that room
	 *         during the date period
	 */
	private TreeMap<Room, Double> getAverageIncomePerRoom(LocalDate start, LocalDate end, ArrayList<LocalDate> days) {
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

	/*
	 * Like the getAverageIncomePerRoom() method, but instead maps the total income
	 * generated to the room
	 * 
	 * @param start the start date of the time period
	 * @param end   the end date of the time period
	 * @param days  the days to include the totals for
	 * @return a TreeMap of each room mapped to the total income generated for that
	 *         room in the date period
	 */
	// 11
	private TreeMap<Room, Double> getTotalIncomePerRoom(LocalDate start, LocalDate end, ArrayList<LocalDate> days) {
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

	private TreeMap<Room, Double> getAverageOccupantsPerRoom(LocalDate start, LocalDate end, ArrayList<LocalDate> days) {
		TreeMap<Room, Double> averages = new TreeMap<Room, Double>();
		for (Entry<Room, ArrayList<Integer>> e : this.getAllOccupantsPerRoom(start, end, days).entrySet()) {
			double average = 0;
			for (int d : e.getValue()) {
				average += (double) d;
			}
			average /= (double) (e.getValue()).size();
			averages.put(e.getKey(), (double) average);
		}
		return averages;
	}
	
	private TreeMap<Room, Double> getAverageOccupantsPerRoomPerDay(LocalDate start, LocalDate end, ArrayList<LocalDate> days) {
		TreeMap<Room, Double> averages = new TreeMap<Room, Double>();
		for (Entry<Room, ArrayList<Integer>> e : this.getAllOccupantsPerRoom(start, end, days).entrySet()) {
			double average = 0;
			for (int d : e.getValue()) {
				average += (double) d;
			}
			average /= (double) (e.getValue()).size();
			average /= days.size();
			averages.put(e.getKey(), (double) average);
		}
		return averages;
	}

	/*
	 * Returns a TreeMap of total occupants per room per day in the days lis in the
	 * range of start and end
	 * 
	 * @param start the start date of the period
	 * 
	 * @param end the end date of the period
	 * 
	 * @param days the days to calculate the total for
	 * 
	 * @return a TreeMap with a list of daily totals per room
	 */
	private TreeMap<Room, ArrayList<Integer>> occupantsPerRoomPerDay(LocalDate start, LocalDate end,
			ArrayList<LocalDate> days) {
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
	 * Requests the average and total occupancy information per room for all the
	 * days of the date period to be written to a file
	 * 
	 * @param start             the start date of the date period
	 * @param end               the end date of the date period
	 * @param hotelRooms        the rooms of the hotel and their counts, leave null
	 *                          if numberOfOccupants is true
	 * @param numberOfOccupants true if you want to retrieve number of occupants,
	 *                          false if you want to retrieve number of rooms booked
	 * @return the name of the file the information was stored to
	 */
	// 6
	public String requestOccupantInformation(LocalDate start, LocalDate end, TreeMap<Room, Integer> hotelRooms, boolean numberOfOccupants) {
		return requestOccupantInformation(start, end, getAllDaysInPeriod(start, end), hotelRooms, numberOfOccupants);
	}

	/**
	 * Requests the average and total occupancy information per room for the days
	 * specified in the date range
	 * 
	 * @param start             the start date of the date period
	 * @param end               the end date of the date period
	 * @param days              the days of the date period to display total incomes
	 *                          for
	 * @param hotelRooms        the rooms and the count in the hotel, leave null if
	 *                          numberOfOccupants is true
	 * @param numberOfOccupants true if you want to retrieve number of occupants,
	 *                          false if you want to retrieve number of rooms booked
	 * @return the name of the file the information was stored to
	 */
	public String requestOccupantInformation(LocalDate start, LocalDate end, ArrayList<LocalDate> days,
			TreeMap<Room, Integer> hotelRooms, boolean numberOfOccupants) {
		if (numberOfOccupants) {
			TreeMap<Room, Double> averages = getAverageOccupantsPerRoom(start, end, days);
			TreeMap<Room, Double> averagesPerDay = getAverageOccupantsPerRoomPerDay(start, end, days);
			TreeMap<Room, Integer> totals = getTotalOccupantsPerRoom(start, end, days);
			TreeMap<Room, ArrayList<Integer>> allTotals = this.occupantsPerRoomPerDay(start, end, days);
			String fileName = this.writeOccupantNumbersToFile(start, end, allTotals, averages,averagesPerDay, totals, days);
			return fileName.split("/")[fileName.split("/").length - 1];
		} else {
			TreeMap<Room, Integer> totals = this.getTotalRoomCountPerPeriod(start, end, days);
			TreeMap<Room, ArrayList<Integer>> allTotals = this.getDailyRoomCount(start, end, days);
			if (hotelRooms != null) {
				String fileName = this.writeOccupancyRoomNumbersToFile(start, end, allTotals, totals, days, hotelRooms);
				return fileName.split("/")[fileName.split("/").length - 1];
			}
			return "File not written: hotelRooms is null";
		}
	}

	/*
	 * Like the getAverageOccupantsPerRoom() method, but instead maps the total
	 * occupants per room
	 * 
	 * @param start the start date of the time period
	 * @param end   the end date of the time period
	 * @param days  the days to include the totals for
	 * @return a TreeMap of each room mapped to the total income generated for that
	 *         room in the date period
	 */
	// 11
	private TreeMap<Room, Integer> getTotalOccupantsPerRoom(LocalDate start, LocalDate end, ArrayList<LocalDate> days) {
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
}