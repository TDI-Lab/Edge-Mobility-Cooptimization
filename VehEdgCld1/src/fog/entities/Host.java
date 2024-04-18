
package fog.entities;

import java.util.List;




public class Host {

	private int id;
	private long storage;
	private double ram;
	private double bw;

	private List<? extends Pe> peList;

	private double totalMips;

	private double idlePower;
	private double activePower;
	
	public Host(int id,	int ramProvisioner,	long bwProvisioner,	long storage,List<? extends Pe> peList, double sC_MaxPow, double sC_IdlePow)	{
		setId(id);
		setRamProvisioner(ramProvisioner);
		setBwProvisioner(bwProvisioner);
		setStorage(storage);
		setPeList(peList);
		this.idlePower = sC_IdlePow;
		this.activePower = sC_MaxPow;
		
		
	}
	
	
	protected void setBwProvisioner(double bwProvisioner) {
		this.bw = bwProvisioner;
	}

	
	
	/**
	 * Gets the machine bw.
	 * 
	 * @return the machine bw
	 */
	public double getBw() {
		return bw;
	}

	
	/**
	 * @return MB
	 */
	public double getRam() {
		return ram;
	}

	/**
	 * Gets the machine storage.
	 * 
	 * @return the machine storage
	 */
	public long getStorage() {
		return storage;
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
	 * Sets the id.
	 */
	protected void setId(int id) {
		this.id = id;
	}

	/**
	 * Gets the ram provisioner.
	 * 
	 * @return the ram provisioner
	 */
	public double getRamProvisioner() {
		return ram;
	}

	/**
	 * Sets the ram provisioner.
	 * 
	 * @param ramProvisioner
	 *        the new ram provisioner
	 */
	protected void setRamProvisioner(double ramProvisioner) {
		this.ram = ramProvisioner;
	}

	/**
	 * Gets the bw provisioner.
	 * 
	 * @return the bw provisioner
	 */
	public double getBwProvisioner() {
		return bw;
	}


	@SuppressWarnings("unchecked")
	public <T extends Pe> List<T> getPeList() {
		return (List<T>) peList;
	}

	protected <T extends Pe> void setPeList(List<T> peList) {
		this.peList = peList;
		for (Pe pe : peList)
			this.totalMips += pe.getPower();

	}

	protected void setStorage(long storage) {
		this.storage = storage;
	}
	
	public double getTotalMips() {
		return this.totalMips;
	}
	
	public int getNumberOfPes() {
		return getPeList().size();
	}


	/**
	 * @return watt
	 */
	public double getMaxPower() {
		return activePower;
	}


	/**
	 * @return watt
	 */
	public double getIdlePower() {
		return idlePower;
	}

}
