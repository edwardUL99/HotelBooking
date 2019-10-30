import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.TreeMap;

/**
* A class with tools for managing the system
*/
public class BookingSystem {
	
	/**
	* Private constructor prevents instantiation of BookingSystem obejcts
	*/
	private BookingSystem() {
		
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
	public static TreeMap<String,TreeMap<Room, Integer>> getRooms() {
		TreeMap<String,TreeMap<Room, Integer>> allRooms = new TreeMap<String, TreeMap<Room, Integer>>();
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
}
	
