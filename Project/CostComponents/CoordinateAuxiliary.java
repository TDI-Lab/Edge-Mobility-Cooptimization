package EdgeEPOS.CostComponents;

import java.util.ArrayList;
import java.util.List;

import org.fog.entities.MobileDevice;
import org.fog.localization.Coordinate;

import EdgeEPOS.Setting.Constants;

public class CoordinateAuxiliary {
	
	public int currPeriodNum;
	public Coordinate baseCoor;
	public Coordinate nextCoor;
	public int sampleInterval;//time in second: 15sec
	public MobileDevice smartThing;
	
	

	public CoordinateAuxiliary(int currPeriodNum, int samplinginterval, MobileDevice smartthing) {
		
		this.sampleInterval = samplinginterval;
		this.smartThing = smartthing;
		this.currPeriodNum = currPeriodNum;
		initializeCoordinate();
	}
	
	private void initializeCoordinate() {
		// TODO Auto-generated method stub
		 setCurFirstCoordinate();
		 setLastCoordinate();
	}

	public void setLastCoordinate() {
		ArrayList<String[]> path = smartThing.getPath();
		String [] coodinates = path.get(getCurrPeriodNum()+1);
		//System.out.println("in coor0 "+(int) Double.parseDouble(coodinates[2])+" "+(int) Double.parseDouble(coodinates[3]));
	   			
		nextCoor = stringToCoordinate(coodinates);
		
	}
	public String [] setLastCoordinateString() {
		ArrayList<String[]> path = smartThing.getPath();
		String [] coodinates = path.get(getCurrPeriodNum()+1);
		//System.out.println("last coor "+(int) Double.parseDouble(coodinates[2])+" "+(int) Double.parseDouble(coodinates[3]));
	   			
		return coodinates;
		
	}
	public void setCurFirstCoordinate() {
		ArrayList<String[]> path = smartThing.getPath();
		String [] coodinates = path.get(getCurrPeriodNum());
		//System.out.println("init coor0 "+(int) Double.parseDouble(coodinates[2])+" "+(int) Double.parseDouble(coodinates[3]));
	   			
		baseCoor = stringToCoordinate(coodinates);
		//System.out.println("init coor1 "+baseCoor.getCoordX()+" "+baseCoor.getCoordY());
	   	
	}
	
	public String [] getCurFirstCoordinateString() {
		ArrayList<String[]> path = smartThing.getPath();
		String [] coodinates = path.get(getCurrPeriodNum());
		//System.out.println("in coor "+(int) Double.parseDouble(coodinates[2])+" "+(int) Double.parseDouble(coodinates[3]));
	   			
		return coodinates;
		
	}
	/**
	 * @param baseCoordinate next coordinate at the end of this interval
	 * @param sampleCoordinates next coordinates between current position and baseCoordinate
	 * @param sampleInterval forward time in terms of second 
	 * we assume that each mobility coordinate is for 1 min = 60 second 
	 */
	//String [] next_coodinate = Coordinate.getNextCoordinate(sourceThing, periodNum+1);
	//System.out.println("next c: "+next_coord.getCoordX()+" "+next_coord.getCoordY());
	
	public void getSampleCoords(List<Coordinate> SampleCoordinates) {
		
		SampleCoordinates.add(getBaseCoor());
			
			int sampleTime = sampleInterval;
		   while (sampleTime < (Constants.TAU)){
			   if (Coordinate.newSampleCoordinate(getCurFirstCoordinateString(), SampleCoordinates, sampleTime)) {
			   	//System.out.println("here...in cgetSamplePositions"+sampleTime+" "+sampleInterval);
			   	sampleTime += sampleInterval;
			   }
			   else {
				  // System.out.println("here... break"+sampleTime+" "+samplingInterval);
				   break;
				   //SampleCoordinates.add(nextCoordinate);
			   }
			   
			   }
		   SampleCoordinates.add(getNextCoor());
		
	}

	
	
	public Coordinate stringToCoordinate(String[] coodinates) {
		Coordinate next_coord = new Coordinate();
			next_coord.setCoordX((int) Double.parseDouble(coodinates[2]));
			next_coord.setCoordY((int) Double.parseDouble(coodinates[3]));
		return next_coord;
	}

	public int getCurrPeriodNum() {
		return currPeriodNum;
	}

	public void setCurrPeriodNum(int currPeriodNum) {
		this.currPeriodNum = currPeriodNum;
	}

	public Coordinate getBaseCoor() {
		return baseCoor;
	}

	public Coordinate getNextCoor() {
		return nextCoor;
	}
}
