package robindecroon.homeviz.room;

import robindecroon.homeviz.exceptions.ParseXMLException;
import robindecroon.homeviz.util.Amount;
import robindecroon.homeviz.util.Period;
import android.util.Log;

public abstract class Consumer {

	private String name;
	private static Amount kwhPrice;
	private static Amount waterPrice;


	private int watt;
	private double averageHoursOn;
	
	private double liter;

	/**
	 * @return the liter
	 */
	public double getLiter() {
		return liter;
	}

	/**
	 * @param liter the liter to set
	 */
	public void setLiter(double liter) {
		this.liter = liter;
	}
	
	/**
	 * @return the watt
	 */
	public int getWatt() {
		return watt;
	}

	/**
	 * @param watt the watt to set
	 */
	public void setWatt(int watt) {
		this.watt = watt;
	}

	/**
	 * @return the averageMinOn
	 */
	public double getAverageHoursOn() {
		return averageHoursOn;
	}

	/**
	 * @param averageMinOn the averageMinOn to set
	 */
	public void setAverageHoursOn(int averageMinOn) {
		this.averageHoursOn = (double) averageMinOn / 60;
	}
	/**
	 * @return the kwhPrice
	 */
	public static Amount getKwhPrice() {
		if (kwhPrice == null) {
			Log.e("Light","Belgian prices are used!");
			return new Amount(0.2289);
		}
		return kwhPrice;
	}

	/**
	 * @param kwhPrice the kwhPrice to set
	 */
	public static void setKwhPrice(Amount kwhPrice) {
		Consumer.kwhPrice = kwhPrice;
	}
	
	/**
	 * @return the waterPrice
	 */
	public static Amount getWaterPrice() {
		if (waterPrice == null) {
			Log.e("Light","Belgian prices are used!");
			return new Amount(0.0014);
		}
		return waterPrice;
	}

	/**
	 * @param waterPrice the waterPrice to set
	 */
	public static void setWaterPrice(Amount waterPrice) {
		Consumer.waterPrice = waterPrice;
	}

	/**
	 * @return the id
	 */
	public String getName() {
		if(name == null) {
			throw new ParseXMLException(this);
		}
		return name;
	}

	/**
	 * @param name the id to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public abstract Amount getPrice(Period currentPeriod);
	
}