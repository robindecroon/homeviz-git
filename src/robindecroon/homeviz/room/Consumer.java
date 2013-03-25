package robindecroon.homeviz.room;

import java.util.List;

import robindecroon.homeviz.util.Amount;
import robindecroon.homeviz.util.Period;
import robindecroon.homeviz.xml.Entry;
import robindecroon.homeviz.xml.IEntry;

public abstract class Consumer {

	private String name;
	private static Amount kwhPrice;
	private static Amount waterPrice;
	private static double co2Value;

	private int watt;
	private double averageHoursOn;

	private double liter;
	private List<IEntry> entries;

	public Consumer(String name, int watt) {
		this.name = name;
		this.watt = watt;
	}

	/**
	 * @return the liter
	 */
	public double getLiter() {
		return liter;
	}

	/**
	 * @param liter
	 *            the liter to set
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
	 * @param watt
	 *            the watt to set
	 */
	public void setWatt(int watt) {
		this.watt = watt;
	}

	/**
	 * @return the averageMinOn
	 */
	public double getAverageHoursOn() {
		try {
			long totalMillisOn = 0;
			long first = entries.get(0).getDate();
			boolean on = false;
			for(int i = 1; i < entries.size(); i++) {
				if (on) {
					long date = entries.get(i).getDate();
					totalMillisOn += date - first;
					on = false;
				} else {
					on = true;
				}
			}
			return totalMillisOn /3600000;
		} catch (Exception e) {
			return averageHoursOn;
		}
	}

	/**
	 * @param averageMinOn
	 *            the averageMinOn to set
	 */
	public void setAverageHoursOn(int averageMinOn) {
		this.averageHoursOn = (double) averageMinOn / 60;
	}

	/**
	 * @return the kwhPrice
	 */
	public static Amount getKwhPrice() {
		if (kwhPrice == null) {
			// Log.e("Light","Belgian prices are used!");
			return new Amount(0.2289);
		}
		return kwhPrice;
	}

	/**
	 * @param kwhPrice
	 *            the kwhPrice to set
	 */
	public static void setKwhPrice(Amount kwhPrice) {
		Consumer.kwhPrice = kwhPrice;
	}

	/**
	 * @return the waterPrice
	 */
	public static Amount getWaterPrice() {
		if (waterPrice == null) {
			// Log.e("Light","Belgian prices are used!");
			return new Amount(0.0014);
		}
		return waterPrice;
	}

	/**
	 * @param waterPrice
	 *            the waterPrice to set
	 */
	public static void setWaterPrice(Amount waterPrice) {
		Consumer.waterPrice = waterPrice;
	}

	/**
	 * @return the id
	 */
	public String getName() {
		if (name == null) {
			throw new IllegalStateException("A consumer without name");
		}
		return name;
	}

	/**
	 * @param name
	 *            the id to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public abstract Amount getPrice(Period currentPeriod);

	@Override
	public String toString() {
		return getName();
	}

	public static void setCO2Value(double co2Value) {
		Consumer.co2Value = co2Value;

	}

	public double getKWH() {
		// TODO
		return Math.random() * 10;
	}

	public CO2 getCO2Value() {
		return new CO2(getKWH() * co2Value, WeightUnit.GRAM);
	}

	public Fuel getFuel() {
		return new Fuel(getKWH(), FuelKind.DIESEL);
	}

	public void putEntries(List<IEntry> list) {
		this.entries = list;
	}

}