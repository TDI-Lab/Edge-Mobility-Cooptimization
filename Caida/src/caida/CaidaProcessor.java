package caida;

/*
 * This program preprocesses three Internet data files from Caida Internet Topology Data and generate 
 * corresponding data for a specific continent, country, state, and city.
 * Caida files format can be found in the following web page: 
 * https://www.caida.org/data/internet-topology-data-kit/release-2019-04.xml 
 * 
 * format of input params to run the program is as follows:
 * src_directory dest_directory processing_level "continent" "country[optional]" "state[optional]" "city[optional]"
 * 
 * two examples for input params:
 * C:\src\ C:\des\ 2 "North America" "United States" "Maryland" "Annapolis"
 * C:\src\ C:\des\ 2 "Europe" "Germany" "Bavaria" "Eichstaett"
 * C:\src\ C:\des\ 1 "Europe" "Germany"
 * C:\src\ C:\des\ 0 "Europe"
 *  
 * Current params:
 * C:\Users\znb_n\eclipse-workspace\Caida\files\input\ C:\Users\znb_n\eclipse-workspace\Caida\files\test 2 "Europe" "Germany" "Bavaria" "Munich"
 * 
 * if you face errors run on a system with higher memory space and check the configuration of your VM size.Should be at least -Xms512M
 * -Xmx2024M
 *
 */
public class CaidaProcessor {
	
	static String Spath;
	static String Dpath;
	static int Level;
	
	public static void main(String[] args) {
		
		
		String continent = "";
		String country = "";
		String state = "";
		String city = "";
		double elapsedTime;
		
		System.out.println();
		
		try {
			setSpath(args[0]);//source path to the input files 
			setDpath(args[1]);//destination path for the output files
			setLevel(Integer.parseInt(args[2]));//level of processing (0: continent), (1: country), (2: city)
			
			switch(Level) {
			  case 2://continent, country, state, and city
				  continent = args[3];
				  country = args[4];
				  state = args[5];
				  city = args[6];
			    break;
			  case 1://continent and country
				  continent = args[3];
				  country = args[4];
				  break;
			  case 0: //continent
				  continent = args[3];
				  
			}	
			
			System.out.println(args[0]+"\n"+args[1]+"\n"+continent+" "+country+" "+state+" "+city+" "+Level);
			
		}
		catch(ArrayIndexOutOfBoundsException e){
			System.err.println("invalid input arguments");
		  	System.exit(0);
		}
		
		Processor p1 = new Processor(Spath, Dpath);
		
		if (Level == 0)
			elapsedTime = (double) p1.processInput(continent);
		else if (Level == 1)
			elapsedTime = (double) p1.processInput(continent, country);
		else
			elapsedTime = (double) p1.processInput(continent, country, state, city);
		
		double elapsedTimeInSecond = (double) elapsedTime / 1_000_000_000;
		System.out.println("file processing finished in "+elapsedTimeInSecond+" seconds");
			
	}
	
	public static int getLevel() {
		return Level;
	}
	public static String getSpath() {
		return Spath;
	}
	public static String getDpath() {
		return Dpath;
	}
	public static void setDpath(String dpath) {
		Dpath = dpath;
	}
	public static void setSpath(String spath) {
		Spath = spath;
	}
	public static void setLevel(int level) {
		Level = level;
	}
	

}

