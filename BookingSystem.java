import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Map;

/**
* A class with tools for managing the system
*/
public class BookingSystem implements CsvTools {
	private TreeMap<String, ArrayList<Reservation>> reservations; //Stores a list of reservations per hotel
	private TreeMap<String, TreeMap<Room, Integer>> allRooms;
	
	/**
	* Constructs a BookingSystem object
	*/
	public BookingSystem() {
		this.reservations = new TreeMap<String, ArrayList<Reservation>>();
		this.getRooms();
		this.reinitialise();
	}
	
	/**
	* Reads hotel info from a hotel information CSV file and returns the Rooms and Room count for each hotel. Example: If you had a file with 3 hotels,
	* 5-star: Deluxe Single 45
	*         Deluxe Double 10
	* 4-star: Executive Single 40
	*		  Executive Double 30
	* 3-star: Classic Family 10
	*		  Classic Single 20
	* It would return a TreeMap with the hotel name e.g. 5-star mapped to a treemap with eg. Deluxe Single and the Integer value 45
	* @return the non-null TreeMap containing hotel names and the room and number of rooms that hotel has
	*/
	public TreeMap<String,TreeMap<Room, Integer>> getRooms() {
		this.allRooms = new TreeMap<String, TreeMap<Room, Integer>>();
		File f = new File(System.getProperty("user.dir") + "\\l4Hotels.csv");
		try (Scanner in = new Scanner(f)) {
			String line;
			String[] values;
			Room room;
			int lineNum = 0;
			String hotel = "";
			TreeMap<Room, Integer> rooms = null;
			while (in.hasNext()) {
				line = in.nextLine();
				if (lineNum > 1) {
					boolean newHotel = false;
					values = line.split(","); //csv values are separated by comma
					if (!values[0].equals("")) {
						newHotel = true;
						hotel = values[0];
						rooms = new TreeMap<Room, Integer>();
					}
					String roomType = values[1];
					int numberOfRooms = Integer.parseInt(values[2]);
					int[] occupancy = new int[4];
					String[] occ = values[3].split("\\+");
					occupancy[0] = Integer.parseInt(occ[0]);
					occupancy[1] = Integer.parseInt(occ[1]);
				    occ = values[4].split("\\+");
				    occupancy[2] = Integer.parseInt(occ[0]);
				    occupancy[3] = Integer.parseInt(occ[1]);
				    int[] rates = new int[7];
				    int index = 5;
				    for (int i = 0; i < rates.length; i++) {
				    	rates[i] = Integer.parseInt(values[index]);
				    	index++;
				    }
				    
				    room = new Room(roomType, occupancy, rates);
				    rooms.put(room, numberOfRooms);
					if (newHotel && !hotel.equals("")) {
						allRooms.put(hotel, rooms);
					}
				}
				lineNum++;
			}
		} catch (FileNotFoundException e) {
			
		}
		return allRooms;
	}
	
	public Room getRoom(String hotelName, String roomName) {
		for (Room r : this.allRooms.get(hotelName).keySet()) {
			if (r.getType().equals(roomName)) {
				return r;
			}
		}
		return null;
	}
	
	public TreeMap<String, TreeMap<Room, Integer>> getCurrentRooms() {
		return this.allRooms; //Just here for testing until we save room info to csv file
	}
	
	/**
	 * Equivalent to for example y <= x in maths but for dates
	 * @param d1 the variable to check thats greater than or equals to
	 * @param compare the variable to compare d1 against
	 * @return whether the date d1 is greater than or equals to compare
	 */
	private boolean dateGreaterThanEquals(LocalDate d1, LocalDate compare) {
		return d1.isEqual(compare) || d1.isAfter(compare);
	}
	
	/**
	 * Equivalent to for example x <= y in maths but for dates
	 * @param d1 the variable to check thats greater than or equals to
	 * @param compare the variable to compare d1 against
	 * @return whether the date d1 is greater than or equals to compare
	 */
	private boolean dateLessThanEquals(LocalDate d1, LocalDate compare) {
		return d1.isEqual(compare) || d1.isBefore(compare);
	}
	
	/**
	 * Checks if the specified reservation is between the given time period, i.e if the reservation checkIn date + number of nights infringes on the time period
	 * @param from the starting date of the time period
	 * @param to the ending date of the time period
 	 * @param reservation the reservation to check
	 * @return if the reservation is in the time period
	 */ 
	private boolean isInTimePeriod(LocalDate from, LocalDate to, Reservation reservation) {
		LocalDate checkIn = reservation.getCheckinDate();
		LocalDate checkOut = reservation.getCheckoutDate();
		for (LocalDate date = checkIn; !date.isEqual(checkOut); date = date.plusDays((long)1)) {
			if (dateGreaterThanEquals(date, from) && dateLessThanEquals(date, to)) {
				return true;
			}
		}
		return false;
	}
	
	/** 
	 * Returns a list of reservations that are booked in the period between the date period specified
	 * @param hotelName the name of the hotel
	 * @param from the date the period starts at
	 * @param to the date the period ends at
	 * @return the list of matching reservations
	 */
	private ArrayList<Reservation> bookingsInTimePeriod(String hotelName, LocalDate from, LocalDate to) {
		ArrayList<Reservation> allBookings = this.reservations.get(hotelName);
		if (allBookings != null) {
			ArrayList<Reservation> bookings = new ArrayList<Reservation>();
			for (Reservation r : allBookings) {
				if (isInTimePeriod(from, to, r)) {
					bookings.add(r);
				}
			}
			return bookings;
		}
		return null;
	}
	
	private int numberOfTimesRoomIsBookedAtDate(String hotelName, Room room, LocalDate from, LocalDate to) {
		ArrayList<Reservation> bookings = bookingsInTimePeriod(hotelName, from, to);
		if (bookings != null) {
			int count = 0;
			for (Reservation r : bookings) {
				for (Room roomBooked : r.getRooms()) {
					if (roomBooked.equals(room)) {
						count++;
					}
				}
			}
			return count;
		}
		return -1;
	}
	
	/**
	 * Returns a tree map of the rooms and their available number for the time period specified by the from and to parameters
	 * @param hotelName the name of the hotel in question
	 * @param from the date at the start of the time period
	 * @param to the date at the end of the time period
	 * @return a TreeMap with a rooms and their corresponding number of rooms available in the hotel during this period
	 */
	public TreeMap<Room, Integer> getCurrentRooms(String hotelName, LocalDate from, LocalDate to) {
		TreeMap<Room, Integer> hotelRooms = this.getRooms().get(hotelName);
		if (hotelRooms != null) {
			TreeMap<Room, Integer> availableRooms = new TreeMap<Room, Integer>(hotelRooms); //Initialise it with allRooms i.e the total number of rooms in the hotel regardless of if booked or not
			for (Room r : hotelRooms.keySet()) {
				int numberTimesBooked = this.numberOfTimesRoomIsBookedAtDate(hotelName, r, from, to);
				if (numberTimesBooked != -1) {
					availableRooms.put(r, availableRooms.get(r) - numberTimesBooked);
				};
			}
			return availableRooms;
		}
		//N.B. At the moment this code doesn't take into account rooms booked during the date period that overflow as such the period with the number of nights staying. will have to fix this in the methods used by this method
		return null;
	}
	
	/**
	 * Returns a TreeMap all reservations in each hotel
	 * @return the TreeMap with hotel names and the reservations in that hotel
	 */
	public TreeMap<String, ArrayList<Reservation>> getReservations() {
		return this.reservations;
	}
	
	/**
	 * Returns a single reservation specified by the name and the checkIn date
	 * @param hotelName the name of the hotel
	 * @param name the name of the customer who owns the reservation
	 * @param checkIn the check in date
	 * @return a reservation if the map of reservations contains the reservation, null if not
	 */
	public Reservation getReservation(String hotelName, String name, java.time.LocalDate checkIn) {
		for (Reservation r : this.reservations.get(hotelName)) {
			if (r.getName().equals(name) && r.getCheckinDate().equals(checkIn)) {
				return r;
			}
		}
		return null;
	}
	
	/**
	 * Returns a Tree Map with the room type and the number of rooms in the reservation for each room
	 * @param hotelName the name of the hotel
	 * @param reservation the reservation
	 * @return a treemap with the room types mapped to the number of rooms booked for each type in reservation
	 */
	private TreeMap<String, Integer> numberOfRoomsBooked(Reservation reservation) {
		TreeMap<String, Integer> roomNumbers = new TreeMap<String, Integer>();
		for (Room r : reservation.getRooms()) {
			String type = r.getType();
			if (roomNumbers.containsKey(type)) {
				roomNumbers.put(type, roomNumbers.get(type) + 1);
			} else {
				roomNumbers.put(type, 1);
			}
		}
		return roomNumbers;
	}
	
	/**
	 * Checks if there is enough rooms free in the hotel chosen to add the rooms chosen in the reservation.
	 * Take the following reservation for example:
	 * 		Deluxe Double - 3 rooms
	 * 		Deluxe Single - 2 rooms
	 * And the hotel has 6 Deluxe Double rooms left and 1 Deluxe Single left
	 * While there is enough Deluxe Double left there is not enough Deluxe Single so the method would return false
	 * @param hotelName the name of the hotel
	 * @param reservation thereservation
	 * @return true if there is enough rooms in the hotel available to book in all the rooms booked in reservation
	 */
	private boolean hasEnoughRoomsFree(String hotelName, Reservation reservation) {
		TreeMap<String, Integer> roomNumbers = numberOfRoomsBooked(reservation);
		TreeMap<Room, Integer> rooms = this.getCurrentRooms().get(hotelName);
		if (rooms != null) {
			for (Map.Entry<Room, Integer> e : rooms.entrySet()) {
				String type = e.getKey().getType();
				int numRoomsBooked = roomNumbers.get(type) == null ? -1:roomNumbers.get(type);
				if (numRoomsBooked > e.getValue() && numRoomsBooked != -1) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Adds a new reservation to the list of reservations for the particular hotel
	 * @param hotelName the name of the hotel owned by the chain e.g 5-star
	 * @param reservation the reservation to be added
	 * @return true if the reservation was successfully added
	 */
	public boolean addReservation(String hotelName, Reservation reservation) {
		//Will have to have a way to check if the reservation can be made
		if (hasEnoughRoomsFree(hotelName, reservation)) { //May have this changed so when user is choosing a room they're only shown options that are avlable to book, i.e. if theres enough rooms of that type to book
			if (!this.reservations.containsKey(hotelName)) {
				this.reservations.put(hotelName, new ArrayList<Reservation>());
			}
			this.reservations.get(hotelName).add(reservation);
			TreeMap<Room, Integer> rooms = this.allRooms.get(hotelName);
			for (Room r : reservation.getRooms()) { //This should be changed to only decrement room numbers for a certain date, e.g. there might be 5 rooms booked for january, why say those 5 rooms aren't available in December if they are?
				rooms.put(r, rooms.get(r) - 1); //Decrement for each room booked. Must add a way to check if the number of rooms is not 0(maybe in choose rooms method in reservation)
			}
			writeReservationsToFile();
			return true;
		}
		return false;
	}
	
	/**
	 * Searches through all reservations and for the reservation with the most rooms booked, returns the room count
	 * @return the room count of the reservation with the most rooms booked
	 */
	private int largestRoomCountBooked() {
		int largestRoomCount = 0;
		for (Map.Entry<String, ArrayList<Reservation>> e : this.reservations.entrySet()) {
			for (Reservation r : e.getValue()) {
				if (r.getNumberOfRooms() > largestRoomCount) {
					largestRoomCount = r.getNumberOfRooms();
				}
			}
		}
		return largestRoomCount;
	}
	
	/**
	 * Gets the number of rows for a reservation data matrix
	 * @return the number of rows required
	 */
	private int getNumberOfRows() {
		int rows = 0;
		for (Map.Entry<String, ArrayList<Reservation>> e : this.reservations.entrySet()) {
			rows += e.getValue().size();
		}
		return rows;
	}
	
	private void writeReservationsToFile() {
		int largestRoomCount = largestRoomCountBooked();
		int rows = getNumberOfRows() + 1;
		int columns = 9 + largestRoomCount;
		Object[][] data = new String[rows][columns];
		String[] attributes = {"Hotel", "Number", "Name", "Type", "Check-in Date", "Number of Nights", "Number Of Rooms", "Total Cost", "Deposit"};
		int row = 1;
		int index = 0;
		for (int col = 0; col < columns; col++) {
			if (col == 7) {
				data[0][col] = "Rooms";
			} else if (col > 7 && col < 7 + largestRoomCount) {
				data[0][col] = "";
			} else {
				if (index < attributes.length) { 
					data[0][col] = attributes[index++];
				}
			}
		}
		for (Map.Entry<String, ArrayList<Reservation>> e : this.reservations.entrySet()) {
			data[row][0] = e.getKey();
			boolean hotelNamed = true;
			for (Reservation reservation : e.getValue()) {
				if (hotelNamed) {
					hotelNamed = false;
				} else {
					data[row][0] = "";
				}
				data[row][1] = Integer.valueOf(reservation.getNumber()).toString();
				data[row][2] = reservation.getName();
				data[row][3] = reservation.getType();
				data[row][4] = reservation.getCheckinDate().toString();
				data[row][5] = Integer.valueOf(reservation.getNumberOfNights()).toString();
				data[row][6] = Integer.valueOf(reservation.getNumberOfRooms()).toString();
				int lastIndex = 7;
				int roomsPrinted = 0;
				for (Room r : reservation.getRooms()) {
					data[row][lastIndex++] = r.getType();
					roomsPrinted++;
				}
				if (roomsPrinted < largestRoomCount) {
					for (int i = roomsPrinted; i < largestRoomCount; i++) {
						data[row][lastIndex++] = "";
					}
				}
				data[row][lastIndex++] = String.format("€%.02f", reservation.getTotalCost().getAmountDue());
				data[row][lastIndex] = String.format("€%.02f", reservation.getDeposit().getAmountDue());
				lastIndex = 7;
				row++;
			}
		}
	String path = System.getProperty("user.dir") + "/reservations.csv";
	this.writeDataToFile(path, data);
}
	
	/**
	 * Checks if the hotel that is being provided exists in the system
	 * @param hotelName The name of the hotel of the chain
	 * @return if the hotel is in the system
	 */
	private boolean containsHotel(String hotelName) {
		return this.reservations.containsKey(hotelName);
	}
	
	/**
	 * Checks if the specific hotel has the reservation in the system
	 * @param hotelName the name of the hotel
	 * @param reservation the reservation being queried
	 * @return if the system has a record of the reservation
	 */
	private boolean containsReservation(String hotelName, Reservation reservation) {
		if (!containsHotel(hotelName)) {
			return false;
		} else {
			ArrayList<Reservation> reservationList = this.reservations.get(hotelName);
			return reservationList.contains(reservation);
		}
	}
	
	/**
	 * Removes the reservation from the system for the hotel provided both the hotel name and reservation exist
	 * @param hotelName the name of the hotel to which the reservation belongs
	 * @param reservation the reservation to be cancelled
	 * @return
	 */
	public boolean removeReservation(String hotelName, Reservation reservation) {
		if (containsReservation(hotelName, reservation)) {
			ArrayList<Room> cancelledRooms = reservation.getRooms();
			TreeMap<Room, Integer> rooms = this.allRooms.get(hotelName);
			for (Room r : cancelledRooms) {
				rooms.put(r, rooms.get(r) + 1); //Increments back the rooms available.
			}
			this.reservations.get(hotelName).remove(reservation);
			return true;
		}
		return false;
	}

	@Override
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
	
	private int getRows(String filePath) {
		int count = 0;
		try (Scanner in = new Scanner(new File(filePath))) {
			while (in.hasNextLine()) {
				count++;
				in.nextLine();
			}
		} catch (FileNotFoundException e) {
		}
		return count;
	}
	
	private int getCols(String filePath) {
		int count = 0;
		try (Scanner in = new Scanner(new File(filePath))) {
			if (in.hasNextLine()) {
				count = in.nextLine().split(",").length;
			}
		} catch (FileNotFoundException e) {
		}
		return count;
	}

	@Override
	/**
	 * Reads in data from the specified filePath and reads it into an Object matrix
	 * @param filePath the path to the file
	 */
	public String[][] readDataFromFile(String filePath) {
		if (new File(filePath).exists()) {
			String[][] data = new String[getRows(filePath)][getCols(filePath)];
			int row = 0;
			try (Scanner in = new Scanner(new File(filePath))) {
				while (in.hasNextLine()) {
					String[] values = in.nextLine().split(",");
					for (int col = 0; col < values.length; col++) {
						data[row][col] = values[col];
					}
					row++;
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return data;
		} else {
			return null;
		}
	}
	
	private void reinitialise() {
		String[][] data = readDataFromFile(System.getProperty("user.dir") + "/reservations.csv");
		if (data != null) {
			int row;
			String hotelName = "", name, type;
			int number, numOfNights, numOfRooms;
			Reservation r;
			ArrayList<Room> rooms;
			for (row = 1; row < data.length; row++) {
				String[] dataRow = data[row];
				if (!dataRow[0].equals("")) {
					hotelName = dataRow[0];
				}
				number = Integer.parseInt(dataRow[1]);
				name = dataRow[2];
				type = dataRow[3];
				String[] date = dataRow[4].split("-");
				LocalDate checkin = LocalDate.of(Integer.parseInt(date[0]),  Integer.parseInt(date[1]), Integer.parseInt(date[2]));
				numOfNights = Integer.parseInt(dataRow[5]);
				numOfRooms = Integer.parseInt(dataRow[6]);
				rooms = new ArrayList<Room>(numOfRooms);
				int lastCol = 7;
				while (lastCol < numOfRooms + 7) {
					String roomName = dataRow[lastCol++];
					if (!roomName.equals("")) {
						Room room = this.getRoom(hotelName, roomName);
						if (room != null) {
							rooms.add(room);
						}	
					}
				}
				r = new Reservation(name, type, checkin, numOfNights, numOfRooms, rooms);
				r.setNumber(number);
				if (!this.reservations.containsKey(hotelName)) {
					this.reservations.put(hotelName, new ArrayList<Reservation>());
				}
				this.reservations.get(hotelName).add(r);
			}
		}
	}
}
	
