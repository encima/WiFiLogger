import java.io.*;
import java.util.*;
import java.text.*;

public class WiFiLogger extends Thread{
	
	static Runtime rt;
	static Process proc;
	static PrintWriter pw;
	static String now;
	static String ssid;
	
	public WiFiLogger(String time, String name) {
		try{
			rt = Runtime.getRuntime();
			proc = rt.exec(new String[] {"sudo", "date", "-s", time});
			getOutput("date");
		}catch(Exception e) {
			e.printStackTrace();
		}
	        long logDate = System.currentTimeMillis();
            DateFormat df = new SimpleDateFormat("ddMMyyyy-HH:mm:ss");
            Date today = new Date(logDate);
            now = df.format(today);
            ssid = name;
	}
	
	public void run() {
		while(true) {
			try {
				sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
				Vector<String> scanResults = getOutput("iwlist wlan1 scanning");
				//writeToFile(scanResults, true);
		}
	}
	
	public static void main(String[] args) {
		WiFiLogger wl = new WiFiLogger(args[0], args[1]);
		//wl.start();
		Scanner in = new Scanner(System.in);
		String read;
			do {
				System.out.println("Please enter a location: ");
				read = in.nextLine();
				Vector<String> scan = getOutput("iwlist wlan1 scan");
				runScan(read, scan,  5);
			}while(!read.equals("q"));
		

	}
	
   public static boolean writeToFile(String loc, Vector<String> data, boolean append) {
        try {
            if(append) {
                    pw = new PrintWriter(new FileWriter("Logs/scanlog" + now + ".txt", true));
            }else{
                    pw = new PrintWriter(new FileWriter("Logs/scanlog" + now + ".txt"));
            }

            Vector<String> date = getOutput("date");
            pw.println(date.get(0) + ". Location: " + loc);
			System.out.println(data.size());
            	if(!data.isEmpty()) {
					for(int i = 0; i < data.size(); i++) {
                   		 if(data.get(i).contains(ssid) && !data.get(i).contains(ssid)) {
                        	if(i+8<data.size()) {
								for(int j = i-1; j < 8 + i; j++) {
										System.out.println(data.get(j));
										pw.println(data.get(j));
								}
							}else{
								pw.println("Some weird results here, not writing...");
							}
            	        }
                	}
			}else{
				pw.println("No Networks to write");
			}
            pw.println("-----------------");
            pw.close();
        }catch(FileNotFoundException ex) {
                return false;
        }catch(IOException e) {
                return false;
        }
        return true;
    }
	
	public static Vector<String> getOutput(String cmd) {
		Vector<String> out = null;
			try {
				Process pr = rt.exec(cmd);
				String line = null;
				out = new Vector<String>();
				BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				String str = null;	
					while((str = in.readLine()) != null) {
						out.add(str);
					}
				in.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		return out;
	}

    public static void runScan(String read, Vector<String> scan, int period) {
        long time0, time1;
        time0 = System.currentTimeMillis();
			for(int i = 0; i<5; i++) {
            	do{
                	time1 = System.currentTimeMillis();
            	}while((time1-time0) < (period*1000));
            	writeToFile(read, scan, true);
			}
    }

}
		
