package datarecord;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import main.Agent;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;
import enums.GOOD_TYPE;


public class DataRecorder {
	
	private Element run;
	private int freq;
	
	private int current_timestep = 0;
	
	public DataRecorder(int freq){
		run = new Element("run");
		run.addAttribute(new Attribute("date", new Date().toString()));
		this.freq = freq;
	}
	
	/*
	 * Grabs the population count timeseries
	 */	
	public void update_0(ArrayList<Agent> popn, int iter){
		if(iter % freq == 0){
			Element iteration = new Element("popn");
			
			/*
			 * Create freq map
			 */
			HashMap<GOOD_TYPE, Integer> freqs = new HashMap<GOOD_TYPE, Integer>();
			freqs.put(GOOD_TYPE.A, 0);
			freqs.put(GOOD_TYPE.B, 0);
			freqs.put(GOOD_TYPE.C, 0);
			
			for(Agent a : popn){
				switch(a.PRODUCTION_TYPE){
				case A:
					freqs.put(GOOD_TYPE.A, freqs.get(GOOD_TYPE.A)+1);
					break;
				case B:
					freqs.put(GOOD_TYPE.B, freqs.get(GOOD_TYPE.B)+1);
					break;
				case C:
					freqs.put(GOOD_TYPE.C, freqs.get(GOOD_TYPE.C)+1);
					break;					
				}
			}
			
			/*
			 * add value and code
			 */
			iteration.addAttribute(new Attribute("iter", Integer.toString(iter)));
			iteration.addAttribute(new Attribute("A", Integer.toString(freqs.get(GOOD_TYPE.A))));
			iteration.addAttribute(new Attribute("B", Integer.toString(freqs.get(GOOD_TYPE.B))));
			iteration.addAttribute(new Attribute("C", Integer.toString(freqs.get(GOOD_TYPE.C))));
			
			run.appendChild(iteration);
		}
	}
	
	/*
	 * Grabs price timeseries
	 */
	public void store_transxn(GOOD_TYPE type, float price){
		Element txn = new Element("price");
		txn.addAttribute(new Attribute("type", type.toString()));
		txn.addAttribute(new Attribute("price", Float.toString(price)));
		run.appendChild(txn);
				
	}
	
	public void update_time(int i){
		this.current_timestep = i;
	}
	
	
	/*
	 * Either prints current document to stdout or saves it to file
	 */
	public void print(boolean save){
		Document doc = new Document(run);
		if(!save){
			try {			
				Serializer serializer = new Serializer(System.out, "ISO-8859-1");
				serializer.setIndent(4);
				serializer.setMaxLength(64);
				serializer.write(doc);
			} catch (IOException ex) {
				System.err.println(ex);
			}
		}
		
		if(save){
			try {
				String fileName = "price sim " + new Date().toString().replace(":", " ") + ".xml";
				File file = new File(fileName);
				OutputStream out = new BufferedOutputStream(new FileOutputStream(file));				
				Serializer serializer = new Serializer(out, "ISO-8859-1");
				serializer.setIndent(4);
				serializer.setMaxLength(64);
				serializer.write(doc);
				System.out.println("Data output to: " + fileName);
			} catch (IOException ex) {
				System.err.println(ex);
			}
		}		
	}
}
