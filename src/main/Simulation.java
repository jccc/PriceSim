package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import datarecord.DataRecorder;
import enums.GOOD_TYPE;

public class Simulation {
	
	private final int 			TIMESTEPS;
	private ArrayList<Agent> 	population;
	private final DataRecorder	datarecorder;
	
	private final Random random;
	
	public Simulation(	int 				timesteps,
						ArrayList<Agent> 	population,
						DataRecorder		datarecorder){
		
		this.TIMESTEPS = timesteps;
		this.population = population;
		this.datarecorder = datarecorder;
		this.random = new Random();		
	}
	
	public void run(){
		for (int i = 0; i < TIMESTEPS; i++) {
			
			
			/*
			 * Remove depeleted agents
			 */
			Iterator<Agent> iter = population.iterator();
			while(iter.hasNext()){
				if(iter.next().depleted){					
					iter.remove();
				}
			}
			
			/*
			 * Do production and consumption
			 */
			for(Agent a : population){
				a.produce();
				a.consume();				
			}
			
			/*
			 * Count production type levels
			 */
			HashMap<GOOD_TYPE, Integer> ct = new HashMap<GOOD_TYPE, Integer>();
			ct.put(GOOD_TYPE.A, 0);
			ct.put(GOOD_TYPE.B, 0);
			ct.put(GOOD_TYPE.C, 0);
			for(Agent a : population){
				int amt = ct.get(a.PRODUCTION_TYPE);
				ct.put(a.PRODUCTION_TYPE, amt+1);
			}
			
			
			/*
			 * Engage in exchange (5 cycles per timestep)
			 */
			int index = 0;
			for (int j = 0; j < 5; j++) {
				for (int k = 0; k < population.size(); k++) {					
					
					index = random.nextInt(population.size());
					
					population.get(index).sell();
					population.get(index).buy();					
				}			
			}
			
			/*
			 * Record data
			 */
			datarecorder.update_0(population, i);
			datarecorder.update_time(i);
			
			
			System.out.println("\n\n===========+ timestep: " + i);
			System.out.println("Pop size: " + population.size());
			System.out.println(ct.toString());			
		}
		datarecorder.print(true);		
	}

}
