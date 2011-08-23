package tests;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import main.Agent;
import main.Market;
import main.Market.Ask;
import main.Market.Bid;

import org.junit.Test;

import enums.GOOD_TYPE;


public class MarketTest {
	/*
	@Test
	public void low_ask(){
		Market market = new Market(GOOD_TYPE.A);
		Agent agent = new Agent();
		
		market.place_ask(new Ask(agent, 15.0f, GOOD_TYPE.A));
		market.place_ask(new Ask(agent, 20.0f, GOOD_TYPE.A));
		market.place_ask(new Ask(agent, 8.0f, GOOD_TYPE.A));
		
		assertEquals(8f, market.get_lowest_ask(), 0);		
	}
	
	@Test
	public void hi_ask(){
		Market market = new Market(GOOD_TYPE.A);
		Agent agent = new Agent();
		
		market.place_ask(new Ask(agent, 15.0f, GOOD_TYPE.A));
		market.place_ask(new Ask(agent, 20.0f, GOOD_TYPE.A));
		market.place_ask(new Ask(agent, 8.0f, GOOD_TYPE.A));
		
		assertEquals(20f, market.get_highest_ask(), 0);		
	}
	
	@Test
	public void clear_market(){
		Market market = new Market(GOOD_TYPE.A);
		Agent agent1 = new Agent(	new HashMap<GOOD_TYPE, Market>(),
									5,2,
									GOOD_TYPE.B, 4, 10,
									new HashMap<GOOD_TYPE, Integer>());
		Agent agent2 = new Agent(	new HashMap<GOOD_TYPE, Market>(),
									5,2,
									GOOD_TYPE.B, 4, 10,
									new HashMap<GOOD_TYPE, Integer>());
		
		market.place_ask(new Ask(agent1, 15.00f, GOOD_TYPE.A));
		market.place_bid(new Bid(agent2, 15.00f, GOOD_TYPE.A));
		
		assertEquals(3, market.get_highest_ask(), 0);		
	}
	
	@Test
	public void clear_offers(){
		Market market = new Market(GOOD_TYPE.A);
		
		Agent agent1 = new Agent(	new HashMap<GOOD_TYPE, Market>(),
									5,2,
									GOOD_TYPE.B, 4, 10,
									new HashMap<GOOD_TYPE, Integer>());
		
		Agent agent2 = new Agent(	new HashMap<GOOD_TYPE, Market>(),
									5,2,
									GOOD_TYPE.B, 4, 10,
									new HashMap<GOOD_TYPE, Integer>());

		market.place_ask(new Ask(agent1, 15.00f, GOOD_TYPE.A));
		market.place_ask(new Ask(agent1, 15.00f, GOOD_TYPE.A));
		market.place_ask(new Ask(agent1, 15.00f, GOOD_TYPE.A));
		
		market.place_ask(new Ask(agent2, 10.00f, GOOD_TYPE.A));
		
		
		assertEquals(4, market.asks_size(), 0);
		
		//market.clear_offers(agent1);
		
		//assertEquals(1, market.asks_size(), 0);
		
		market.place_bid(new Bid(agent1, 2.00f, GOOD_TYPE.A));
		market.place_bid(new Bid(agent1, 5.00f, GOOD_TYPE.A));
		market.place_bid(new Bid(agent1, 5.00f, GOOD_TYPE.A));
		
		assertEquals(3, market.bids_size(), 0);
	}
	*/
}
