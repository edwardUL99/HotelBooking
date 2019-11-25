import java.time.LocalDate;

/**
 * A class to represent the real-life object of a bill
 *
 */
public class Bill {
	private String billName;
	private LocalDate billedDate;
	private double amountDue;
	
	/**
	 * Creates a default Bill object with name as null, the current date and 0.00 amountDue
	 */
	public Bill() {
		this(null, LocalDate.now(), 0.00);
	}
	
	/**
	 * Creates a Bill object with the specified parameters
	 * @param billName the name of the bill
	 * @param billedDate the date of the bill
	 * @param amountDue the amount due
	 */
	public Bill(String billName, LocalDate billedDate, double amountDue) {
		this.billName = billName;
		this.billedDate = billedDate;
		this.amountDue = amountDue;
	}
	
	/**
	 * Creates a bill object where the amountDue is not known yet
	 * @param billName name of the bill
	 * @param billedDate the date of the bill
	 */
	public Bill(String billName, LocalDate billedDate) {
		this(billName, billedDate, 0.00);
	}
	
	/**
	 * Gets the name of this bill
	 * @return the name of the bill
	 */
	public String getBillName() {
		return this.billName;
	}
	
	/**
	 * Gets the date at which the bill was created
	 * @return the date this bill was created
	 */
	public LocalDate getBilledDate() {
		return this.billedDate;
	}
	
	/**
	 * Gets the amount that this bill is billed for
	 * @return the amount due of this bill
	 */
	public double getAmountDue() {
		return this.amountDue;
	}
	
	/**
	 * Sets the amount due of the bill with the new value
	 * @param amountDue the amount that this bill is charging
	 */
	public void setAmountDue(double amountDue) {
		this.amountDue = amountDue;
	}
	
	/**
	 * Returns a string representation for this bill object
	 * @return string representation of this bill
	 */
	@Override
	public String toString() {
		return "Name: " + billName + "\n" + "Date: " + billedDate + "\n" + "Total amount due: "  + String.format("€%.02f", amountDue);
	}

}
