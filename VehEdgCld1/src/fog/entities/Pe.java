
package fog.entities;


public class Pe {

	private int id;
	private double procespower;

	
	public Pe(int id, double power) {
		setId(id);
		setMips(power);
	}

	/**
	 * Sets the id.
	 * 
	 * @param id
	 *        the new id
	 */
	protected void setId(int id) {
		this.id = id;
	}

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the MIPS Rating of this Pe.
	 * 
	 * @param d
	 *        the mips
	 * @pre mips >= 0
	 * @post $none
	 */
	public void setMips(double d) {
		procespower = d;
	}

	

	

	/**
	 * Gets the Pe provisioner.
	 * 
	 * @return the Pe provisioner
	 */
	public double getPower() {
		return procespower;
	}

}
