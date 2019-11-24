public interface CsvTools {
	/**
	 * 
	 * @param filePath
	 * @param data
	 */
	public abstract void writeDataToFile(String filePath, Object[][] data);
	/**
	 * 
	 * @param filePath
	 * @return
	 */
	public abstract String[][] readDataFromFile(String filePath);
}
