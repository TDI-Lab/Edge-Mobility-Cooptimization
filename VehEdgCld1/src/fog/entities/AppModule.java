package fog.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.util.Pair;

import fog.entities.*;

/**
 * Class representing an application module, the processing elements of the
 * application model of iFogSim.
 * 
 * @author Harshit Gupta
 */
public class AppModule {

	private String name;
	private String appId;
	/** The id. */
	private int id;

	/** The user id. */
	private int userId;

	/** The uid. */
	private String uid;

	/** The size. */
	private long size;

	/** The MIPS. */
	private double mips;

	/** The number of PEs. */
	private int numberOfPes;

	/** The ram. */
	private int ram;

	/** The bw. */
	private long bw;

	/** The vmm. */
	private String vmm;

	/**
	 * Mapping from tupleType emitted by this AppModule to Actuators subscribing
	 * to that tupleType
	 */
	private Map<String, List<Integer>> actuatorSubscriptions;

	public AppModule(
		int id,
		String name,
		String appId,
		int userId,
		double mips,
		int ram,
		long bw,
		long size,
		String vmm) {
		//super(id, userId, mips, 1, ram, bw, size, 1, vmm, 300);
		setName(name);
		setId(id);
		setAppId(appId);
		setUserId(userId);
		setMips(mips);
		setRam(ram);
		setBw(bw);
		setSize(size);
		setVmm(vmm);
		
	
	
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * Get unique string identificator of the VM.
	 * 
	 * @return string uid
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * Generate unique string identificator of the VM.
	 * 
	 * @param userId
	 *        the user id
	 * @param vmId
	 *        the vm id
	 * @return string uid
	 */
	public static String getUid(int userId, int vmId) {
		return userId + "-" + vmId;
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
	 * 
	 * @param id
	 *        the new id
	 */
	protected void setId(int id) {
		this.id = id;
	}

	/**
	 * Sets the user id.
	 * 
	 * @param userId
	 *        the new user id
	 */
	protected void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * Gets the ID of the owner of the VM.
	 * 
	 * @return VM's owner ID
	 * @pre $none
	 * @post $none
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * Gets the mips.
	 * 
	 * @return the mips
	 */
	public double getMips() {
		return mips;
	}

	/**
	 * Sets the mips.
	 * 
	 * @param mips
	 *        the new mips
	 */
	protected void setMips(double mips) {
		this.mips = mips;
	}

	/**
	 * Gets the number of pes.
	 * 
	 * @return the number of pes
	 */
	public int getNumberOfPes() {
		return numberOfPes;
	}

	/**
	 * Sets the number of pes.
	 * 
	 * @param numberOfPes
	 *        the new number of pes
	 */
	protected void setNumberOfPes(int numberOfPes) {
		this.numberOfPes = numberOfPes;
	}

	/**
	 * Gets the amount of ram.
	 * 
	 * @return amount of ram
	 * @pre $none
	 * @post $none
	 */
	public int getRam() {
		return ram;
	}

	/**
	 * Sets the amount of ram.
	 * 
	 * @param ram
	 *        new amount of ram
	 * @pre ram > 0
	 * @post $none
	 */
	public void setRam(int ram) {
		this.ram = ram;
	}

	/**
	 * Gets the amount of bandwidth.
	 * 
	 * @return amount of bandwidth
	 * @pre $none
	 * @post $none
	 */
	public long getBw() {
		return bw;
	}

	/**
	 * Sets the amount of bandwidth.
	 * 
	 * @param bw
	 *        new amount of bandwidth
	 * @pre bw > 0
	 * @post $none
	 */
	public void setBw(long bw) {
		this.bw = bw;
	}

	/**
	 * Gets the amount of storage.
	 * 
	 * @return amount of storage
	 * @pre $none
	 * @post $none
	 */
	public long getSize() {
		return size;
	}

	/**
	 * Sets the amount of storage.
	 * 
	 * @param size
	 *        new amount of storage
	 * @pre size > 0
	 * @post $none
	 */
	public void setSize(long size) {
		this.size = size;
	}

	/**
	 * Gets the VMM.
	 * 
	 * @return VMM
	 * @pre $none
	 * @post $none
	 */
	public String getVmm() {
		return vmm;
	}

	/**
	 * Sets the VMM.
	 * 
	 * @param vmm
	 *        the new VMM
	 */
	protected void setVmm(String vmm) {
		this.vmm = vmm;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public Map<String, List<Integer>> getActuatorSubscriptions() {
		return actuatorSubscriptions;
	}

	public void setActuatorSubscriptions(Map<String, List<Integer>> actuatorSubscriptions) {
		this.actuatorSubscriptions = actuatorSubscriptions;
	}

	@Override
	public String toString() {
		return "AppModule [name=" + name + ", appId=" + appId + ", actuatorSubscriptions=" + actuatorSubscriptions + "]";
	}
}
