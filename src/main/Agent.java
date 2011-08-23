package main;

import java.util.HashMap;
import java.util.Map;

import main.Market.Ask;
import main.Market.Bid;
import enums.GOOD_TYPE;

public class Agent {
	
	private static enum URGENCY{
		HI, MED, LO;
	}	
	
	/*
	 * Parameters
	 */
	private HashMap<GOOD_TYPE, Integer> 	rates_of_consumption;	
	public GOOD_TYPE 						PRODUCTION_TYPE;
	private int 							rate_of_production;
	private final int 						HI_URGENCY;
	private final int 						MED_URGENCY;
	private HashMap<GOOD_TYPE, Market> 		markets;
	
	public boolean 							followFlag;
	
	/*
	 * Time-dependent variables
	 */
	private HashMap<GOOD_TYPE, Integer> 	current_stocks;
	private float 							cash;		
	public boolean 							depleted;	
	
	
	/*
	 * (Constructor used for testing)
	 */
	public Agent(){
		this.MED_URGENCY = 4;
		this.HI_URGENCY = 2;
	}
	
	
	
	public Agent(	HashMap<GOOD_TYPE, Market> 	markets,
					int 						hi_urg,
					int 						me_urg,
					GOOD_TYPE 					prod_type,
					int 						prod_rate,
					float						cash,
					HashMap<GOOD_TYPE, Integer> consume_rates){
		
		this.markets = markets;
		this.HI_URGENCY = hi_urg;
		this.MED_URGENCY = me_urg;
		this.PRODUCTION_TYPE = prod_type;
		this.rate_of_production = prod_rate;
		this.rates_of_consumption = consume_rates;
		
		this.followFlag = false;
		
		this.current_stocks = new HashMap<GOOD_TYPE, Integer>();
		current_stocks.put(GOOD_TYPE.A, 6);
		current_stocks.put(GOOD_TYPE.B, 6);
		current_stocks.put(GOOD_TYPE.C, 6);
		this.cash = cash;
		this.depleted = false;		
	}
	
	public void setFollow(){
		this.followFlag = true;
	}
	
	
	public void buy(){
		
		/* Bidding rules: 
		 * 1. URGENT: Bid at lowest existing ask or all money or market default
		 * 2. MEDIUM: Bid at median bid
		 * 3. LOW: 	Bid at 75%ile bid. (or maybe decrease bid by x%) 
		 */
		
		/*
		 * Remove all existing bids from market for this agent
		 */
		clear_bids();
		
		float bidded_cash = 0;		

		/*
		 * Place bids until run out of cash. Limit to 10 cycles
		 */
		int cntr = 0;
		while(cash - bidded_cash > 0 && cntr < 10){
			cntr++;
			
			/*
			 * Find type of stock to run out soonest
			 */			
			GOOD_TYPE lowest_type = GOOD_TYPE.A;
			int lowest_timeleft = 1000000;
			for (Map.Entry<GOOD_TYPE, Integer> entry : current_stocks.entrySet()){
				GOOD_TYPE type = entry.getKey();				
				int amt = entry.getValue();
				int time = 0;
				if(amt > 0){
					time = amt / rates_of_consumption.get(type);
				}
				
				if(time < lowest_timeleft){
					lowest_type = type;
					lowest_timeleft = time;
				}
			}
			
			
			/*
			 * Get the market for lowest good type
			 */
			Market market = markets.get(lowest_type);
			
			/*
			 * Determine urgency based on least time-till-exhaustion
			 */
			URGENCY urgency = urgency_map(lowest_timeleft);		
			
			/*
			 * Determine bid price based on urgency level then place bid 		
			 */		
			float price = 0;
			
			switch(urgency){
				case HI:
					price = market.get_lowest_ask();
					break;
				case MED:
					price = market.get_median_bid();
					break;
				case LO:
					price = 0.75f * market.get_median_bid();
					break;			
			}
			
			if(price > cash) price = cash;
			if(price < 0.01) price = 0.01f;
			
			Bid bid = new Bid(this, price, lowest_type);
			
			/*
			 * If bid not accepted increment bidded cash counter 
			 */
			if(!market.place_bid(bid)){				
				bidded_cash += price;	
			}
				
			
		}
	}
	
	public void sell(){		
		
		/*
		 * Selling rules:
		 * 
		 * 1. URGENT: Sell off least-urgent good at highest bid rate until just above urgent
		 * 2. MED: Price at median asking rate for least-urgent good
		 * 3. LO: Price at 110% median asking rate for least-urgent good
		 */
		
		/*
		 * Find type of stock to run out soonest and latest
		 */			
		GOOD_TYPE most_urgent_type = GOOD_TYPE.A;
		GOOD_TYPE least_urgent_type = GOOD_TYPE.A;
		
		int least_timeleft = 1000000;
		int most_timeleft = -100;
		
		for (Map.Entry<GOOD_TYPE, Integer> entry : current_stocks.entrySet()){
			GOOD_TYPE type = entry.getKey();
			int amt = entry.getValue();
			int time = 0;
			if(amt > 0){
				time = amt / rates_of_consumption.get(type);
			}
			
			if(time < least_timeleft){
				most_urgent_type = type;
				least_timeleft = time;
			}
			
			if(time > most_timeleft){
				least_urgent_type = type;
				most_timeleft = time;
			}
		}
		
		/*
		 * Get markets for least and most urgent types
		 */
		Market urgent_market = markets.get(most_urgent_type);
		Market least_urgent_market = markets.get(least_urgent_type);
		
		/*
		 * Remove all existing asks from market for this agent
		 */
		clear_asks();
		
		
		/*
		 * Determine urgency based on least time-till-exhaustion
		 */
		URGENCY urgency = urgency_map(least_timeleft);
		
		float price;	
		
		Ask ask = null;
		
		switch(urgency){
			case HI:
				/*
				 * If urgency is high, determine cash required (based on median rates) to get out of
				 * urgency, then sell surplus item at highest bid rate until cash raised or run out of
				 * item.
				 */
				float cash_required = urgent_market.get_median_ask() * 
										(MED_URGENCY - this.current_stocks.get(most_urgent_type));
				
				int cnter = 0;
				
				while(	cash < cash_required && 
						this.current_stocks.get(least_urgent_type) >= MED_URGENCY + 2 &&
						cnter < 30){
					price = least_urgent_market.get_highest_bid();					
					ask = new Ask(this, price, least_urgent_type);					
					least_urgent_market.place_ask(ask);					
					cnter++;					
				}
				
				break;
			
			case MED:
				
				cnter = 0;
				
				while(this.current_stocks.get(least_urgent_type) > MED_URGENCY + 2 && cnter < 30){
					price = least_urgent_market.get_median_ask();
					ask = new Ask(this, price, least_urgent_type);
					least_urgent_market.place_ask(ask);					
					cnter++;					
				}
				
				break;
	
			case LO:
				
				cnter = 0;
				
				price = 1.1f * least_urgent_market.get_median_ask();
				
				while(this.current_stocks.get(least_urgent_type) > MED_URGENCY + 2 && cnter < 30){
					ask = new Ask(this, price, least_urgent_type);
					least_urgent_market.place_ask(ask);
					cnter++;			
				}
				
				
				break;			
		}								
		
	}
	
	public float cash(){
		return this.cash;
	}
	
	public void produce(){
		int amt = this.current_stocks.get(PRODUCTION_TYPE);
		amt += rate_of_production;
		this.current_stocks.put(PRODUCTION_TYPE, amt);		
	}
	
	public void consume(){
		for(GOOD_TYPE type : rates_of_consumption.keySet()){
			int amt = this.current_stocks.get(type);
			amt -= rates_of_consumption.get(type);
			if(amt <= 0) this.depleted = true;
			this.current_stocks.put(type, amt);	
		}		
	}
	
	public void add_cash(float amt){
		//TODO 0 checking...
		this.cash += amt;
	}
	
	public void add_prod(GOOD_TYPE type, int amt){
		//TODO 0 checking...
		
		int current = this.current_stocks.get(type);
		this.current_stocks.put(type, current + amt);	
	}
	
	public HashMap<GOOD_TYPE, Market> get_markets(){
		return this.markets;
	}
	
		
	private URGENCY urgency_map(int time_left){
		if(time_left <= HI_URGENCY)
			return URGENCY.HI;
		else if(time_left <= MED_URGENCY)
			return URGENCY.MED;
		else
			return URGENCY.LO;		
	}
	
	private void clear_bids(){
		for(Market m : this.markets.values()){
			m.clear_bids(this);
		}
	}
	
	private void clear_asks(){
		for(Market m : this.markets.values()){
			m.clear_asks(this);
		}
	}
	
	
}
