package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import datarecord.DataRecorder;
import enums.GOOD_TYPE;

public class Market {
	
	private GOOD_TYPE type;
	

	public static class Ask implements Comparable<Ask>{
		protected final Agent seller;
		protected final float price;
		protected final GOOD_TYPE type;
		
		public Ask(Agent agent, float price, GOOD_TYPE type){
			this.seller = agent;
			this.price = price;
			this.type = type;
		}
		@Override
		public int compareTo(Ask o) {
			if(o.price > this.price)
				return 1;			
			else if(o.price == this.price)
				return 0;
			else return -1;			
		}
		@Override
		public String toString(){
			return type + " Ask@ $" + price;// + " " + seller + " $" + seller.cash();
		}
	}
	
	
	public static class Bid implements Comparable<Bid>{
		protected final Agent buyer;
		protected final float price;
		protected final GOOD_TYPE type;
		
		public Bid(Agent agent, float price, GOOD_TYPE type){
			this.buyer = agent;
			this.price = price;
			this.type = type;
		}
		@Override
		public int compareTo(Bid o) {
			if(o.price > this.price)
				return 1;			
			else if(o.price == this.price)
				return 0;
			else return -1;			
		}
		@Override
		public String toString(){
			return type + " Bid@ $" + price;// + " " + buyer + " $" + buyer.cash();
		}
	}
	
	private ArrayList<Bid> bids;
	private ArrayList<Ask> asks;
	
	private final float default_price = 2; 
	
	private final DataRecorder datarecorder;
	
	public Market(GOOD_TYPE type, DataRecorder datarecorder){
		this.bids = new ArrayList<Market.Bid>();
		this.asks = new ArrayList<Market.Ask>();
		this.type = type;
		this.datarecorder = datarecorder;
	}
	
	public boolean place_bid(Bid bid){

		/*
		 * If there are currently no asks, store bid
		 */
		if(asks.isEmpty()){			
			this.bids.add(bid);
			Collections.sort(this.bids);			
			return false;
		}
		
		/*
		 * Check if bid price is higher than least ask
		 */
		Ask least_ask = asks.get(asks.size()-1);
		
		if(bid.price >= least_ask.price){
			/*
			 * If bid >= lowest ask then do transxn:
			 * 1. Increment seller's cash by ask price
			 * 2. Decrement buyer's cash by ask price
			 * 3. Decrement seller's product by a unit
			 * 4. Increment buyer's product by a unit
			 * 5. Remove the bid and ask from the market 
			 */			
			Agent buyer = bid.buyer;
			Agent seler = least_ask.seller;
			
			buyer.add_cash(-least_ask.price);
			seler.add_cash(least_ask.price);
			
			buyer.add_prod(bid.type, 1);
			seler.add_prod(bid.type, -1);
			
			asks.remove(least_ask);		
			
			datarecorder.store_transxn(bid.type, least_ask.price);
			
			return true;
		}
		else{
			/*
			 * If bid doesn't clear (i.e. lower then least ask), store it
			 */			
			this.bids.add(bid);
			Collections.sort(this.bids);
			return false;
		}
		
	}
	
	public boolean place_ask(Ask ask){	

		/*
		 * If there are currently no asks, store ask
		 */
		if(bids.isEmpty()){			
			this.asks.add(ask);
			Collections.sort(this.asks);			
			return false;
		}
		
		/*
		 * Check if ask price is lower than highest bid
		 */		
		if(ask.price <= get_highest_bid()){
			//System.out.println("### ask " + ask.toString() + " " + bids.get(0).toString());
			/*
			 * If ask <= highest bid then do transxn:
			 * 1. Increment seller's cash by ask price
			 * 2. Decrement buyer's cash by ask price
			 * 3. Decrement seller's product by a unit
			 * 4. Increment buyer's product by a unit
			 * 5. Remove the bid and ask from the market 
			 */			
			Bid bid = bids.get(0); //highest bid
			Agent buyer = bid.buyer;
			Agent seler = ask.seller;			
			
			buyer.add_cash(-ask.price);
			seler.add_cash(ask.price);
			
			buyer.add_prod(ask.type, 1);
			seler.add_prod(ask.type, -1);
			
			bids.remove(bid);			
			
			datarecorder.store_transxn(ask.type, ask.price);
			
			return true;
		}
		else{
			/*
			 * If ask doesn't clear, store it
			 */
			this.asks.add(ask);
			Collections.sort(this.asks);			
			return false;
		}
		
	}
	
	public void clear_bids(Agent a){
		
		Iterator<Bid> bid_iter = bids.iterator();
		while(bid_iter.hasNext()){
			if(bid_iter.next().buyer == a){
				bid_iter.remove();
			}
		}		
	}
		
	public void clear_asks(Agent a){
		
		Iterator<Ask> ask_iter = asks.iterator();
		while(ask_iter.hasNext()){
			if(ask_iter.next().seller == a){
				ask_iter.remove();
			}
		}
	}	
	
	public String bids_toString(){
		if(bids.size() <= 10){
			return bids.toString();
		}
		else
			return  "hi " + bids.subList(0,5).toString() + "\n" +
					"lo " + bids.subList(bids.size()-6, bids.size()-1).toString();
	}
	
	public String asks_toString(){
		if(asks.size() <= 10){
			return asks.toString();
		}
		else
			return  "hi " + asks.subList(0,5).toString() + "\n" +
					"lo " + asks.subList(asks.size()-6, asks.size()-1).toString();
			//return asks.subList(0,5).toString();
			
	}
	
	
	public float get_median_ask(){
		if(asks.isEmpty()){
			return default_price;
		}
		int med_index = asks.size() / 2;
		return asks.get(med_index).price;
	}
	
	public float get_median_bid(){
		if(bids.isEmpty()){
			return default_price;
		}
		int med_index = bids.size() / 2;
		return bids.get(med_index).price;
		
	}
	
	public float get_highest_ask(){
		if(asks.isEmpty()){
			return default_price;
		}
		return asks.get(0).price;
		
	}
	
	public float get_highest_bid(){
		if(bids.isEmpty()){
			return default_price;
		}
		return bids.get(0).price;
		
	}
	
	public float get_lowest_ask(){
		if(asks.isEmpty()){
			return default_price;
		}
		return asks.get(asks.size()-1).price;
		
	}

	public float get_lowest_bid(){
		if(bids.isEmpty()){
			return default_price;
		}
		return bids.get(bids.size()-1).price;
		
	}
	
	public int asks_size(){
		return this.asks.size();
	}
	
	public int bids_size(){
		return this.bids.size();
	}

}
