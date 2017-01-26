package com.redhat.rule.kieserver.test;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import org.junit.Test;
import org.kie.api.runtime.ExecutionResults;

import com.redhat.rule.kieserver.KieServerApi;
import com.redhat.rule.model.Fact;

import static org.junit.Assert.*;
public class KieServerApiTest {
	private Logger logger = Logger.getLogger(KieServerApiTest.class);
	@Test
	public void testAlias() throws InterruptedException{
		KieServerApi api = new KieServerApi();		
		api.createContainer("com.redhat.flight", "ruleFlight", "1.0.0","flight_1","flight_alias");
		Fact flight = new Fact();
		flight.setNamespace("com.redhat.flight.rules");
		flight.setName("Flight");
		flight.getAttributes().put("airport", "LIL");
		flight.setIdentifier("flight");
		
		Fact customer = new Fact();
		customer.setNamespace("com.redhat.flight.rules");
		customer.setName("Customer");
		customer.getAttributes().put("frequent", true);
		customer.getAttributes().put("flight", flight);

		customer.setIdentifier("customer");
		ArrayList<Fact> facts = new ArrayList<Fact>();
		facts.add(flight);
		facts.add(customer);
		ExecutionResults results = api.executeByAlias(facts, "flight_alias");
		assertEquals(api.getAttributeFromHandle(results, "reduction", customer, "flight_1", "customer"), 12);
		ArrayList<Fact> facts2 = new ArrayList<Fact>();
		Fact flight2 = new Fact();
		flight2.setNamespace("com.redhat.flight.rules");
		flight2.setName("Flight");
		flight2.getAttributes().put("airport", "NICE");
		flight2.setIdentifier("flight");
		
		facts2.add(flight2);
		facts2.add(customer);
		api.createContainer("com.redhat.flight", "ruleFlight", "2.0.0","flight_2","flight_alias");
		results = api.executeByAlias(facts2, "flight_alias");
		assertEquals(api.getAttributeFromHandle(results, "reduction", customer, "flight_2", "customer"), 13);
		api.destroy();
	}

}
