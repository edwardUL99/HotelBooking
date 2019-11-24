public interface CsvTools {
	/**
	 * Writes data stored in an object matrix to a file
	 * @param filePath the path to the file
	 * @param data the data matrix
	 */
	public abstract void writeDataToFile(String filePath, Object[][] data);
	
	/**
	 * Reads info form a file into a string matrix
	 * @param filePath the path to the file containing the data
	 * @return a string data matrix
 	 */
	public abstract String[][] readDataFromFile(String filePath);
}
