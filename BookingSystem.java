import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.ArrayList;

/**
* A class with tools for managing the system
*/
public class BookingSystem {
	private TreeMap<String, ArrayList<Reservation>> reservations; //Stores a list of reservations peer hotel
	private TreeMap<String, TreeMap<Room, Integer>> allRooms;
	
	/**
	* Constructs a BookingSystem object
	*/
	public BookingSystem() {
		this.reservations = new TreeMap<String, ArrayList<Reservation>>();
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
	
	public TreeMap<String, TreeMap<Room, Integer>> getCurrentRooms() {
		return this.allRooms; //Just here for testing until we save room info to csv file
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
	 * Adds a new reservation to the list of reservations for the particular hotel
	 * @param hotelName the name of the hotel owned by the chain e.g 5-star
	 * @param reservation the reservation to be added
	 */
	public void addReservation(String hotelName, Reservation reservation) {
		//Will have to have a way to check if the reservation can be made
		if (this.reservations.containsKey(hotelName)) {
			this.reservations.get(hotelName).add(reservation);
		} else {
			this.reservations.put(hotelName, new ArrayList<Reservation>());
			this.reservations.get(hotelName).add(reservation);
		}
		TreeMap<Room, Integer> rooms = this.allRooms.get(hotelName);
		for (Room r : reservation.getRooms()) {
			rooms.put(r, rooms.get(r) - 1); //Decrement for each room booked. Must add a way to check if the number of rooms is not 0(maybe in choose rooms method in reservation)
		}
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
}
	
