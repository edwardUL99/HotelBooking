import java.time.LocalDate;
import java.util.ArrayList;

public class Test {
	public static void main(String[] args) {
		Reservation r = new Reservation("Eddy Lynch", "S", LocalDate.of(2019, 12, 25), 5, 3);
		ArrayList<Room> rooms = r.getRooms();
		for (Room room : rooms) {
			System.out.println(room.getRate(3));
			System.out.print(room.getType() + ": \t");
			for (int i : room.getRates()) {
				System.out.print("\t" + i);
			}
			System.out.println();
		}
	}
}