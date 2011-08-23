package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import datarecord.DataRecorder;
import enums.GOOD_TYPE;

public class Run {
	
	private static final Random rand = new Random();

	
	public static void main(String[] args) {		
		
		/*
		 * Simulation params
		 */
		final int INIT_POP_SIZE = 	200;
		final int TIME_STEPS = 		100;
		final int RECORD_FREQ = 	1;
		
		
		/*
		 * Initialize data recorder
		 */
		DataRecorder datarecorder = new DataRecorder(RECORD_FREQ);
				
		
		/*
		 * Initialize markets
		 */
		HashMap<GOOD_TYPE, Market> markets = new HashMap<GOOD_TYPE, Market>();
		markets.put(GOOD_TYPE.A, new Market(GOOD_TYPE.A, datarecorder));
		markets.put(GOOD_TYPE.B, new Market(GOOD_TYPE.B, datarecorder));
		markets.put(GOOD_TYPE.C, new Market(GOOD_TYPE.C, datarecorder));
		
				
		/*
		 * Initialize agents
		 */
		ArrayList<Agent> popn = new ArrayList<Agent>();		
		GOOD_TYPE[] types = GOOD_TYPE.values();
		for (int i = 0; i < INIT_POP_SIZE; i++) {
			HashMap<GOOD_TYPE, Integer> consumes = new HashMap<GOOD_TYPE, Integer>();
			consumes.put(GOOD_TYPE.A, 1);
			consumes.put(GOOD_TYPE.B, 1);
			consumes.put(GOOD_TYPE.C, 1);
			
			popn.add(new Agent(	markets,							//Markets
								7,									//High Urgency
								18,									//Med Urgency
								//types[rand.nextInt(3)],  			//Production type
								types[i%3],							//Production type
								7,									//Production rate
								20,									//Initial cash
								consumes));							//Consumption rate
		}		
		
		
		/*		
		 * Create new simulation using population and datarecorder, and run it
		 */
		new Simulation(	TIME_STEPS,
						popn,
						datarecorder).run();

	}

}
