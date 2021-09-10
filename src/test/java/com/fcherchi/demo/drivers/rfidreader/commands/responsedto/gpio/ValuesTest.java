package com.fcherchi.demo.drivers.rfidreader.commands.responsedto.gpio;

import com.fcherchi.demo.drivers.rfidreader.commands.responsedto.gpio.Values;
import org.junit.Assert;
import org.junit.Test;

public class ValuesTest {

	@Test
	public void testParse() {
		
		//when value is 0 all signals are false
		Values allOff = Values.parse(0);
		
		Assert.assertFalse(allOff.getValueOne());
		Assert.assertFalse(allOff.getValueTwo());
		Assert.assertFalse(allOff.getValueThree());

		//when value is 1 = 001 so, only signal 1 is on
		Values one = Values.parse(1);
		
		Assert.assertTrue(one.getValueOne());
		Assert.assertFalse(one.getValueTwo());
		Assert.assertFalse(one.getValueThree());

		//when value is 2 = 010 so, only signal 2 is on
		Values two = Values.parse(2);
		
		Assert.assertFalse(two.getValueOne());
		Assert.assertTrue(two.getValueTwo());
		Assert.assertFalse(two.getValueThree());
		
		//when value is 3 = 011 so, only signal 1 and 2 are on
		Values three = Values.parse(3);
		
		Assert.assertTrue(three.getValueOne());
		Assert.assertTrue(three.getValueTwo());
		Assert.assertFalse(three.getValueThree());
		
		//when value is 4 = 100 
		Values four = Values.parse(4);
		
		Assert.assertFalse(four.getValueOne());
		Assert.assertFalse(four.getValueTwo());
		Assert.assertTrue(four.getValueThree());
		
		//when value is 5 = 101
		Values five = Values.parse(5);
		
		Assert.assertTrue(five.getValueOne());
		Assert.assertFalse(five.getValueTwo());
		Assert.assertTrue(five.getValueThree());
		
		//when value is 6 = 110 
		Values six = Values.parse(6);
		
		Assert.assertFalse(six.getValueOne());
		Assert.assertTrue(six.getValueTwo());
		Assert.assertTrue(six.getValueThree());
		
		//when value is 7 = 111 
		Values seven = Values.parse(7);
		
		Assert.assertTrue(seven.getValueOne());
		Assert.assertTrue(seven.getValueTwo());
		Assert.assertTrue(seven.getValueThree());

	}
}
