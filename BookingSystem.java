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
	private TreeMap<String, ArrayList<Reservation>> reservations; // Stores a list of reservations per hotel
	private TreeMap<String, TreeMap<Room, Integer>> allRooms;
	private TreeMap<String, ArrayList<Reservation>> cancellations; // Stores a list of cancellations from the system per
																	// hotel
	private TreeMap<String, ArrayList<HotelStay>> stays; // Stores a list of hotel stays per hotel

	/**
	 * Constructs a BookingSystem object
	 */
	public BookingSystem() {
		this.reservations = new TreeMap<String, ArrayList<Reservation>>();
		this.cancellations = new TreeMap<String, ArrayList<Reservation>>();
		this.stays = new TreeMap<String, ArrayList<HotelStay>>(); // will have to save and restore too?
		this.getRoomsFromFile();
		this.reinitialise(true, false); // reinitialises reservations
		this.reinitialise(false, false); // reinitialises cancellations
		this.reinitialise(true, true); // reinitialises stays
	}

	/*
	 * Reads hotel info from a hotel information CSV file and returns the Rooms and
	 * Room count for each hotel. Example: If you had a file with 3 hotels, 5-star:
	 * Deluxe Single 45 Deluxe Double 10 4-star: Executive Single 40 Executive
	 * Double 30 3-star: Classic Family 10 Classic Single 20 It would return a
	 * TreeMap with the hotel name e.g. 5-star mapped to a treemap with eg. Deluxe
	 * Single and the Integer value 45
	 * 
	 * @return the non-null TreeMap containing hotel names and the room and number
	 *         of rooms that hotel has
	 */
	private TreeMap<String, TreeMap<Room, Integer>> getRoomsFromFile() {
		this.allRooms = new TreeMap<String, TreeMap<Room, Integer>>();
		File f = new File(System.getProperty("user.dir") + "/data/hotels/l4Hotels.csv");
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
					values = line.split(","); // csv values are separated by comma
					if (!values[0].equals("")) {
						newHotel = true;
						hotel = values[0];
						rooms = new TreeMap<Room, Integer>();
					}
					String roomType = values[1];
					int numberOfRooms = Integer.parseInt(values[2]);
					int[] occupancy = new int[4];
					String[] occ = values[3].split("\\+"); //+ is a special character in regex, so need to escape it
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

	/**
	 * Retrieves hotel Name and the room identified by the roomType
	 * 
	 * @param hotelName the hotel name
	 * @param roomType the room type
	 * @return types of room in the hotel named
	 */
	public Room getRoom(String hotelName, String roomType) {
		for (Room r : this.allRooms.get(hotelName).keySet()) {
			if (r.getType().equals(roomType)) {
				return r;
			}
		}
		return null;
	}

	/**
	 * Returns all the rooms that have been read into the system
	 * 
	 * @return a TreeMap of the rooms and their amounts per hotel
	 */
	public TreeMap<String, TreeMap<Room, Integer>> getAllRooms() {
		return this.allRooms; // Just here for testing until we save room info to csv file
	}

	/*
	 * Equivalent to for example y <= x in maths but for dates
	 * 
	 * @param d1 the variable to check thats greater than or equals to
	 * 
	 * @param compare the variable to compare d1 against
	 * 
	 * @return whether the date d1 is greater than or equals to compare
	 */
	private boolean dateGreaterThanEquals(LocalDate d1, LocalDate compare) {
		return d1.isEqual(compare) || d1.isAfter(compare);
	}

	/*
	 * Equivalent to for example x <= y in maths but for dates
	 * 
	 * @param d1 the variable to check thats greater than or equals to
	 * 
	 * @param compare the variable to compare d1 against
	 * 
	 * @return whether the date d1 is greater than or equals to compare
	 */
	private boolean dateLessThan(LocalDate d1, LocalDate compare) {
		return d1.isBefore(compare);
	}

	/*
	 * Checks if the specified reservation is between the given time period, i.e if
	 * the reservation checkIn date + number of nights infringes on the time period
	 * 
	 * @param from the starting date of the time period
	 * 
	 * @param to the ending date of the time period
	 * 
	 * @param reservation the reservation to check
	 * 
	 * @return if the reservation is in the time period
	 */
	private boolean isInTimePeriod(LocalDate from, LocalDate to, Reservation reservation) {
		LocalDate checkIn = reservation.getCheckinDate();
		LocalDate checkOut = reservation.getCheckoutDate();
		for (LocalDate date = checkIn; !date.isEqual(checkOut); date = date.plusDays(1)) {
			if (dateGreaterThanEquals(date, from) && dateLessThan(date, to)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * Returns a list of reservations that are booked in the period between the date
	 * period specified
	 * 
	 * @param hotelName the name of the hotel
	 * 
	 * @param from the date the period starts at
	 * 
	 * @param to the date the period ends at
	 * 
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

	/*
	 * Returns the number of rooms booked between certain dates
	 * 
	 * @param hotelName returns the name of the Hotel
	 * 
	 * @param room specifies the type of room
	 * 
	 * @param from the date the period begins
	 * 
	 * @param to the date the period ends
	 * 
	 * @return the number of rooms in that time period that are booked
	 */
	private int numberOfTimesRoomIsBookedAtDate(String hotelName, Room room, LocalDate from, LocalDate to) {
		ArrayList<Reservation> bookings = bookingsInTimePeriod(hotelName, from, to);
		if (bookings != null) {
			int count = 0;
			for (Reservation r : bookings) {
				for (RoomBooking rb : r.getRooms()) {
					Room roomBooked = rb.getRoom();
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
	 * Returns a tree map of the rooms and their available number for the time
	 * period specified by the from and to parameters
	 * 
	 * @param hotelName the name of the hotel in question
	 * @param from      the date at the start of the time period
	 * @param to        the date at the end of the time period
	 * @return a TreeMap with a rooms and their corresponding number of rooms
	 *         available in the hotel during this period
	 */
	public TreeMap<Room, Integer> getCurrentRooms(String hotelName, LocalDate from, LocalDate to) {
		TreeMap<Room, Integer> hotelRooms = new TreeMap<Room, Integer>(this.getAllRooms().get(hotelName)); //deepCopy to prevent the method from altering the rooms in the hotel
		if (this.reservations.get(hotelName) == null) { //no reservations made so all rooms are available
			return hotelRooms;
		}
		TreeMap<Room, Integer> availableRooms = new TreeMap<Room, Integer>(hotelRooms); // Initialise it with allRooms i.e the total number of rooms in the hotel regardless of if booked or not
		for (Room r : hotelRooms.keySet()) {
			int numberTimesBooked = this.numberOfTimesRoomIsBookedAtDate(hotelName, r, from, to);
			availableRooms.put(r, availableRooms.get(r) - numberTimesBooked);
		}
		return availableRooms;
	}

	/**
	 * Returns a TreeMap all reservations in each hotel
	 * 
	 * @return the TreeMap with hotel names and the reservations in that hotel
	 */
	public TreeMap<String, ArrayList<Reservation>> getReservations() {
		return this.reservations;
	}

	/**
	 * Returns all the hotel stays for each hotel
	 * 
	 * @return the treemap with stays for each hotel
	 */
	public TreeMap<String, ArrayList<HotelStay>> getHotelStays() {
		return this.stays;
	}

	/**
	 * Returns a single reservation specified by the name and the checkIn date 
	 * @param hotelName the name of the hotel
	 * @param name      the name of the customer who owns the reservation
	 * @param checkIn   the check in date
	 * @param number    the booking number
	 * @return a reservation if the map of reservations contains the reservation,
	 *         null if not
	 */
	public Reservation getReservation(String hotelName, String name, LocalDate checkIn, int number) {
		ArrayList<Reservation> reservations = this.reservations.get(hotelName);
		if (reservations != null) {
			for (Reservation r : this.reservations.get(hotelName)) {
				if ((r.getName().equals(name) && r.getCheckinDate().equals(checkIn)) || r.getNumber() == number) {
					return r;
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns a reservation based on the reservation number in that hotel
	 * @param hotelName the hotel name
	 * @param number the reservation number
	 * @return the reservation matching the number
	 */
	public Reservation getReservation(String hotelName, int number) {
		return this.getReservation(hotelName, "", LocalDate.now(), number); //have local date as a placeholder
	}

	/*
	 * Returns a Tree Map with the room type and the number of rooms in the
	 * reservation for each room
	 * 
	 * @param reservation the reservation
	 * 
	 * @return a treemap with the room types mapped to the number of rooms booked
	 * for each type in reservation
	 */
	private TreeMap<String, Integer> numberOfRoomsBooked(Reservation reservation) {
		TreeMap<String, Integer> roomNumbers = new TreeMap<String, Integer>();
		for (RoomBooking rb : reservation.getRooms()) {
			Room r = rb.getRoom();
			String type = r.getType();
			if (roomNumbers.containsKey(type)) {
				roomNumbers.put(type, roomNumbers.get(type) + 1);
			} else {
				roomNumbers.put(type, 1);
			}
		}
		return roomNumbers;
	}

	/*
	 * Checks if there is enough rooms free in the hotel chosen to add the rooms
	 * chosen in the reservation. Take the following reservation for example: Deluxe
	 * Double - 3 rooms Deluxe Single - 2 rooms And the hotel has 6 Deluxe Double
	 * rooms left and 1 Deluxe Single left While there is enough Deluxe Double left
	 * there is not enough Deluxe Single so the method would return false
	 * 
	 * @param hotelName the name of the hotel
	 * 
	 * @param reservation the reservation
	 * 
	 * @return true if there is enough rooms in the hotel available to book in all
	 * the rooms booked in reservation
	 */
	private boolean hasEnoughRoomsFree(String hotelName, Reservation reservation) {
		TreeMap<String, Integer> roomNumbers = numberOfRoomsBooked(reservation);
		TreeMap<Room, Integer> rooms = this.getAllRooms().get(hotelName);
		if (rooms != null) {
			for (Map.Entry<Room, Integer> e : rooms.entrySet()) {
				String type = e.getKey().getType();
				int numRoomsBooked = roomNumbers.get(type) == null ? -1 : roomNumbers.get(type);
				if (numRoomsBooked > e.getValue() && numRoomsBooked != -1) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	/*
	 * Checks if the hotel that is being provided exists in the system
	 * 
	 * @param hotelName The name of the hotel of the chain
	 * 
	 * @return if the hotel is in the system
	 */
	private boolean containsHotel(String hotelName) {
		return this.allRooms.containsKey(hotelName);
	}

	/**
	 * Checks if there is no other reservation for the same checkin date for the
	 * same person on the system
	 * @param hotelName the name of the hotel
	 * @param name the name of the person making the reservation
	 * @param checkin the checkin date for the reservation
	 * @return if this is the only reservation for this person for this checkin date
	 */
	public boolean onlyBookingOnCheckInDate(String hotelName, String name, LocalDate checkin) {
		ArrayList<Reservation> reservations = this.reservations.get(hotelName);
		if (reservations != null) {
			for (Reservation r : reservations) {
				if (r.getName().equals(name) && r.getCheckinDate().isEqual(checkin)) {
					return false;
				}
			}
		}
		return true;
	}

	/*
	 * Checks if the specific hotel has the reservation in the system
	 * 
	 * @param hotelName the name of the hotel
	 * 
	 * @param reservation the reservation being queried
	 * 
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
	 * Removes the reservation from the system for the hotel provided both the hotel
	 * name and reservation exist
	 * 
	 * @param hotelName    the name of the hotel to which the reservation belongs
	 * @param reservation  the reservation to be cancelled
	 * @param cancellation true if it's a cancellation, false if its being removed
	 *                     from the system
	 * @return true if the reservation was removed successfully
	 */
	public boolean removeReservation(String hotelName, Reservation reservation, boolean cancellation) {
		if (containsReservation(hotelName, reservation)) {
			if (cancellation) {
				if (!this.cancellations.containsKey(hotelName)) {
					this.cancellations.put(hotelName, new ArrayList<Reservation>());
				}
				boolean refunded = false;
				if (!reservation.getCheckinDate().equals(LocalDate.now().minusDays((long)1))) {
					if (reservation.getType().equals("S")) {
						reservation.setTotalCost(0.00);
						reservation.setDeposit(0.00); //refunded money
						refunded = true;
					} else if (reservation.getType().equals("AP")) {
						if (reservation.getTotalCost().getAmountDue() == 0) {
							//didn't check in yet so charge them
							reservation.getTotalCostCalculated();
							reservation.setDeposit(75);
							System.out.println("You have been charged:\n" + reservation.getTotalCost());
						}
					}
				} else {
					if (reservation.getTotalCost().getAmountDue() == 0) {
						//didn't check in yet so charge them
						reservation.getTotalCostCalculated();
						reservation.getTotalCost().setBilledDate(LocalDate.now()); //billed for the date it was cancelled
						reservation.setDeposit(75);
						reservation.getDeposit().setBilledDate(LocalDate.now());
						System.out.println("You have been charged:\n" + reservation.getTotalCost());
					}
				}
				this.cancellations.get(hotelName).add(reservation);
				this.reservations.get(hotelName).remove(reservation);
				this.updateFiles("Reservations");
				this.updateFiles("Cancellations");
				return refunded;
			} else {
				if (LocalDate.now().isAfter(reservation.getCheckoutDate().plusDays(30))
						&& (this.cancellations.get(hotelName).contains(reservation) || // if cancelled or is a hotel
																						// stay, it has been processed
								this.stays.get(hotelName).contains(new HotelStay(reservation)))) {
					this.reservations.get(hotelName).remove(reservation);
					this.updateFiles("Reservations");
				} else {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Returns an array of reservations that are on this check in date for the desk clerk to choose
	 * @param hotelName the name of the hotel
	 * @param date check in date if checkin is true, check-out if checkin is false
	 * @param checkin true if you want to get reservations for checkin, false for checkout
	 * @return an object array of the reservations
	 */
	public Object[] reservationsOnDate(String hotelName, LocalDate date, boolean checkin) {
		ArrayList<Reservation> rList = this.reservations.get(hotelName);
		if (rList != null) {
			Object[] reservations = new Object[rList.size()];
			int i = 0;
			for (Reservation r : rList) {
				LocalDate dateToCheck = checkin ? r.getCheckinDate():r.getCheckoutDate();
				if (dateToCheck.equals(date)) {
					if (!checkin) {
						HotelStay stay = this.getHotelStay(hotelName, r); 
						if (stay != null && stay.isCheckedIn()) {
							reservations[i++] = r; //if checkout make sure its checked in first
						}
					} else {
						reservations[i++] = r;
					}
				}
			}
			
			return java.util.Arrays.copyOf(reservations, i);
		}
		return null;
	}
	/**
	 * Adds a new reservation to the list of reservations for the particular hotel
	 * 
	 * @param hotelName   the name of the hotel owned by the chain e.g 5-star
	 * @param reservation the reservation to be added
	 * @return the reservation created if the reservation was successfully added or null if not
	 */
	public Reservation addReservation(String hotelName, Reservation reservation) {
		if (hasEnoughRoomsFree(hotelName, reservation)) { 
			if (!this.reservations.containsKey(hotelName)) {
				this.reservations.put(hotelName, new ArrayList<Reservation>());
			}
			this.reservations.get(hotelName).add(reservation);
			reservation.getTotalCostCalculated();
			this.updateFiles("Reservations");
			return reservation;
		}
		return null;
	}

	/*
	 * After each check in, this method scans for if any reservation was a no show
	 * and cancels it with no refund
	 * 
	 * @param hotelName the name of the hotel to check for no shows in
	 */
	private void scanForNoShows(String hotelName) {
		ArrayList<Reservation> reservations = this.reservations.get(hotelName);
		if (reservations != null) {
			LocalDate current = LocalDate.now();
			for (Reservation r : reservations) {
				if ((r.getCheckinDate().isBefore(current) || r.getCheckinDate().equals(current))
						&& !this.isStayed(hotelName, r)) {
					r.getTotalCostCalculated();
					this.cancellations.get(hotelName).add(r); // if its a no show cancel it with no refund
					this.reservations.get(hotelName).remove(r);
					this.updateFiles("Reservations");
					this.updateFiles("Cancellations");
				}
			}
		}
	}

	/**
	 * Adds a hotel stay to the system
	 * 
	 * @param hotelName the name of the hotel
	 * @param stay      the hotel stay to add
	 * @return if the system does not have the hotel, you cannot possibly have a
	 *         hotel stay for a new hotel, if no reservation was made first, so will
	 *         return false
	 */
	public boolean addHotelStay(String hotelName, HotelStay stay) {
		if (this.containsHotel(hotelName)) {
			this.scanForNoShows(hotelName);
			if (!this.stays.containsKey(hotelName)) {
				this.stays.put(hotelName, new ArrayList<HotelStay>());
			}

			if (this.stays.get(hotelName).contains(stay)) {
				return false; // already checkedin
			}
			this.stays.get(hotelName).add(stay);
			this.updateFiles("Stays");
			return true;
		}
		return false;
	}

	/**
	 * Gets a particular hotel stay
	 * 
	 * @param hotelName the name of the hotel
	 * @param r         the reservation concerned with a hotel stay
	 * @return the hotel stay if present, null otherwise
	 */
	public HotelStay getHotelStay(String hotelName, Reservation r) {
		ArrayList<HotelStay> stays = this.stays.get(hotelName);
		if (stays != null) {
			for (HotelStay stay : stays) {
				if (stay.getReservation().equals(r)) {
					return stay;
				}
			}
		}
		return null;
	}

	/**
	 * Removes a hotelStay from the system
	 * 
	 * @param hotelName the name of the hotel to remove the stay from
	 * @param stay      the stay to remove
	 * @return true if the stay exists and it was successfully removed
	 */
	public boolean removeHotelStay(String hotelName, HotelStay stay) {
		if (this.containsHotel(hotelName)) {
			if (this.stays.get(hotelName).contains(stay)
					&& LocalDate.now().isAfter(stay.getReservation().getCheckinDate().plusYears(7))) {
				this.stays.get(hotelName).remove(stay);
				this.updateFiles("Stays");
				return true;
			}
		}
		return false;
	}

	/*
	 * Searches through all reservations and for the reservation with the most rooms
	 * booked, returns the room count
	 * 
	 * @param reservationOrCancellation returns highest number of rooms booked in
	 * one reservation from reservations if true, cancellations if false
	 * 
	 * @return the room count of the reservation with the most rooms booked
	 */
	private int largestRoomCountBooked(boolean reservationOrCancellation) {
		int largestRoomCount = 0;
		TreeMap<String, ArrayList<Reservation>> reservations = reservationOrCancellation ? this.reservations
				: this.cancellations;
		for (Map.Entry<String, ArrayList<Reservation>> e : reservations.entrySet()) {
			for (Reservation r : e.getValue()) {
				if (r.getNumberOfRooms() > largestRoomCount) {
					largestRoomCount = r.getNumberOfRooms();
				}
			}
		}
		return largestRoomCount;
	}

	/*
	 * Gets the number of rows for a reservation data matrix
	 * 
	 * @param reservationOrCancellation returns number of rows for reservations if
	 * true, cancellations if false
	 * 
	 * @param hotelStay true if you want to find number of rows for hotel stays
	 * 
	 * @return the number of rows required
	 */
	private int getNumberOfRows(boolean reservationOrCancellation, boolean hotelStay) {
		int rows = 0;
		if (!hotelStay) {
			TreeMap<String, ArrayList<Reservation>> reservations = reservationOrCancellation ? this.reservations
					: this.cancellations;
			for (Map.Entry<String, ArrayList<Reservation>> e : reservations.entrySet()) {
				rows += e.getValue().size();
			}
		} else if (reservationOrCancellation) {
			for (Map.Entry<String, ArrayList<HotelStay>> e : stays.entrySet()) {
				rows += e.getValue().size();
			}
		}

		return rows;
	}

	/**
	 * Checks if the reservation has been processed to a hotel stay
	 * 
	 * @param hotelName the name of the hotel
	 * @param r         the reservation
	 * @return if the reservation has been processed to a stay
	 */
	public boolean isStayed(String hotelName, Reservation r) {
		return this.getHotelStay(hotelName, r) != null;
	}

	/**
	 * Writes reservations or cancellations to a file
	 * 
	 * @param reservationOrCancellation if true, reservations will be written to the
	 *                                  file, if false, cancellations
	 * @param hotelStay                 true if writing a hotel stay to file, false
	 *                                  if not. Note reservationOrCancellation must
	 *                                  be true also
	 */
	public void writeReservationsToFile(boolean reservationOrCancellation, boolean hotelStay) {
		TreeMap<String, ArrayList<Reservation>> reservations = reservationOrCancellation ? this.reservations
				: this.cancellations;
		int largestRoomCount = largestRoomCountBooked(reservationOrCancellation);
		int rows = getNumberOfRows(reservationOrCancellation, hotelStay) + 1;
		int columns = 10 + largestRoomCount;
		columns = hotelStay ? columns + 3 : columns;
		Object[][] data = new Object[rows][columns];
		String[] attributes = { "Hotel", "Number", "Name", "Type", "Check-in Date", "Number of Nights",
				"Number of People", "Number Of Rooms", "Total Cost", "Deposit", "Checked In", "Stay Start",
				"Stay End" };
		int row = 1;
		int index = 0;
		for (int col = 0; col < columns; col++) {
			if (col == 8) {
				data[0][col] = "Rooms(Breakfast Included_Adult Occupancy_Child Occupancy)";
			} else if (col > 8 && col < 8 + largestRoomCount) {
				data[0][col] = "";
			} else {
				if (index < attributes.length) {
					data[0][col] = attributes[index++];
				}
			}
		}
		for (Map.Entry<String, ArrayList<Reservation>> e : reservations.entrySet()) {
			data[row][0] = e.getKey();
			boolean hotelNamed = true;
			for (Reservation reservation : e.getValue()) {
				boolean canAddReservation = true;
				if (hotelStay) {
					canAddReservation = this.isStayed(e.getKey(), reservation);
				}
				if (canAddReservation) { // if reservation has not been procesed to a stay and hotel stay is true, don't
											// bother writing it
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
					data[row][6] = Integer.valueOf(reservation.getNumberOfPeople()).toString();
					data[row][7] = Integer.valueOf(reservation.getNumberOfRooms()).toString();
					int lastIndex = 8;
					int roomsPrinted = 0;
					for (RoomBooking rb : reservation.getRooms()) {
						Room r = rb.getRoom();
						data[row][lastIndex++] = r.getType() + "(" + rb.isBreakfastIncluded() + "_"
								+ rb.getOccupancy()[0] + "_" + rb.getOccupancy()[1] + ")";
						roomsPrinted++;
					}
					if (roomsPrinted < largestRoomCount) {
						for (int i = roomsPrinted; i < largestRoomCount; i++) {
							data[row][lastIndex++] = "";
						}
					}
					data[row][lastIndex++] = String.format("$%.02f", reservation.getTotalCost().getAmountDue());														// checkout
					data[row][lastIndex++] = String.format("$%.02f", reservation.getDeposit().getAmountDue());
					if (hotelStay) {
						HotelStay stay = getHotelStay(e.getKey(), reservation);
						if (stay != null) {
							data[row][lastIndex++] = stay.isCheckedIn();
							data[row][lastIndex++] = stay.getStayStart().toString();
							data[row][lastIndex] = stay.getStayEnd().toString();
						}
					}
					row++;
				}
			}
		}
		String fileName;
		if (reservationOrCancellation && !hotelStay) {
			fileName = "/data/bookingInfo/reservations.csv";
		} else if (!reservationOrCancellation && !hotelStay) {
			fileName = "/data/bookingInfo/cancellations.csv";
		} else {
			fileName = "/data/bookingInfo/stays.csv";
		}
		String path = System.getProperty("user.dir") + fileName;
		this.writeDataToFile(path, data);
	}

	/**
	 * Writes data into a file
	 * 
	 * @param filePath location of the file
	 * @param data     is the data being put into the file
	 */
	@Override
	public void writeDataToFile(String filePath, Object[][] data) {
		File file = new File(filePath);
		try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
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
	 * Gets the number of data rows in a file
	 * 
	 * @param filePath the path of the file
	 * 
	 * @return the number of data rows in the file
	 */
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

	/*
	 * Gets the number of columns in a file
	 * 
	 * @param filePath the path to the file
	 * 
	 * @return the number of columns
	 */
	private int getCols(String filePath) {
		int count = 0;
		try (Scanner in = new Scanner(new File(filePath))) {
			while (in.hasNextLine()) {
				int nextCount = in.nextLine().split(",").length;
				if (nextCount > count) {
					count = in.nextLine().split(",").length;
				}
			}
		} catch (FileNotFoundException e) {
		}
		return count;
	}

	/**
	 * Reads in data from the specified filePath and reads it into an Object matrix
	 * 
	 * @param filePath the path to the file
	 * @return the data found
	 */
	@Override
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

	/**
	 * Updates the specified file
	 * 
	 * @param type type can be Reservations, Cancellations or Stays
	 */
	public void updateFiles(String type) {
		type = type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase();
		if (type.equals("Reservations")) {
			this.writeReservationsToFile(true, false);
		} else if (type.equals("Cancellations")) {
			this.writeReservationsToFile(false, false);
		} else if (type.equals("Stays")) {
			this.writeReservationsToFile(true, true);
		}
	}

	/*
	 * Reinitialises the hotel system with reservations, cancellations or stays
	 * 
	 * @param reservationOrCancellation true if it is a reservation, false if
	 * cancellation
	 * 
	 * @param hotelStay true if its a hotelStay, but since a hotelStay is based on a
	 * reservation, reservationOrCancellation should also be true
	 */
	private void reinitialise(boolean reservationOrCancellation, boolean hotelStay) {
		String fileName;
		if (reservationOrCancellation && !hotelStay) {
			fileName = "/data/bookingInfo/reservations.csv";
		} else if (!reservationOrCancellation && !hotelStay) {
			fileName = "/data/bookingInfo/cancellations.csv";
		} else {
			fileName = "/data/bookingInfo/stays.csv";
		}
		String[][] data = readDataFromFile(System.getProperty("user.dir") + fileName);
		if (data != null) {
			int row;
			String hotelName = "", name, type;
			int number, numOfNights, numOfPeople, numOfRooms;
			Reservation r;
			ArrayList<RoomBooking> rooms;
			for (row = 1; row < data.length; row++) {
				String[] dataRow = data[row];
				if (!dataRow[0].equals("")) {
					hotelName = dataRow[0];
				}
				number = Integer.parseInt(dataRow[1]);
				name = dataRow[2];
				type = dataRow[3];
				String[] date = dataRow[4].split("-");
				LocalDate checkin = LocalDate.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]),
						Integer.parseInt(date[2]));
				numOfNights = Integer.parseInt(dataRow[5]);
				numOfPeople = Integer.parseInt(dataRow[6]);
				numOfRooms = Integer.parseInt(dataRow[7]);
				rooms = new ArrayList<RoomBooking>(numOfRooms);
				int lastCol = 8;
				while (lastCol < numOfRooms + 8) {
					String room = dataRow[lastCol++];
					String roomName = room.substring(0, room.indexOf("("));
					String[] roomInfo = room.substring(room.indexOf("(") + 1, room.length() - 1).split("_");
					boolean breakfastIncluded = Boolean.parseBoolean(roomInfo[0]);
					int adults = Integer.parseInt(roomInfo[1]);
					int children = Integer.parseInt(roomInfo[2]);
					if (!roomName.equals("")) {
						Room rm = this.getRoom(hotelName, roomName);
						RoomBooking rb = new RoomBooking(rm, adults, children);
						rb.setBreakfastIncluded(breakfastIncluded);
						if (rm != null) {
							rooms.add(rb);
						}
					}
				}
				if (!hotelStay) {
					r = new Reservation(number, name, type, checkin, numOfNights, numOfPeople, numOfRooms, rooms);
				} else if (reservationOrCancellation) {
					r = this.getReservation(hotelName, name, checkin, number);
				} else {
					r = new Reservation(number, name, type, checkin, numOfNights, numOfPeople, numOfRooms, rooms);
					Reservation.setLastBookingNumber(Reservation.getLastBookingNumber() - 1);
				}
				while (dataRow[lastCol].equals("")) {
					lastCol++;
				}
				if (r != null) {
					r.setTotalCost(Double.parseDouble(dataRow[lastCol++].substring(1)));
					r.setDeposit(Double.parseDouble(dataRow[lastCol].substring(1)));
					TreeMap<String, ArrayList<Reservation>> reservations = reservationOrCancellation ? this.reservations
							: this.cancellations;
					if (!reservations.containsKey(hotelName)) {
						reservations.put(hotelName, new ArrayList<Reservation>());
					}
					if (!reservations.get(hotelName).contains(r)) {
						reservations.get(hotelName).add(r);
					}
					if (reservationOrCancellation && hotelStay) {
						lastCol++;
						boolean checkedIn = Boolean.parseBoolean(dataRow[lastCol++]);
						String[] start = dataRow[lastCol++].split("-");
						LocalDate startDate = LocalDate.of(Integer.parseInt(start[0]), Integer.parseInt(start[1]),
								Integer.parseInt(start[2]));
						String[] end = dataRow[lastCol].split("-");
						LocalDate endDate = LocalDate.of(Integer.parseInt(end[0]), Integer.parseInt(end[1]),
								Integer.parseInt(end[2]));
						HotelStay stay = new HotelStay(r);
						stay.setCheckedIn(checkedIn);
						stay.setStayStart(startDate);
						stay.setStayEnd(endDate);
						if (!stays.containsKey(hotelName)) {
							stays.put(hotelName, new ArrayList<HotelStay>());
						}
						stays.get(hotelName).add(stay);
					}
				}
			}
		}
	}
}
