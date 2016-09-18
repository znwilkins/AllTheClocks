/*
 * This solution was written in Java, using Java SDK 1.8.
 * 
 * Also note, to avoid consuming an excessive amount of
 * lines, I have elected to not provide Javadoc info for
 * methods and fields in this class.
 */

import java.io.*;
import java.net.URL;
import java.util.*;
import com.google.gson.*;

/**
 * This class contains methods and nested classes to
 * calculate the total price of purchasing every clock
 * and watch in the Shopicruit store.
 * <p>
 * This solution is predicated on an individual purchasing
 * every variety of every product that is either a clock
 * or a watch.
 * <p>
 * In order to compile, Google's Gson (v2.7) is required.
 * It can be downloaded from github.com/google/gson.
 * Gson is provided and used under the Apache 2.0 license.
 * <p>
 * Thanks for considering this solution!
 * 
 * @author Zachary Wilkins
 */
public class ClockCalc {
	
	// These fields contain the sales tax for each province/territory.
	public final static double GST = 1.05;
	public final static double SASK = 1.1;
	public final static double BC = 1.12;
	public final static double ONMB = 1.13;
	public final static double PEI = 1.14;
	public final static double QC = 1.14975;
	public final static double MARI = 1.15;
	public final static double NA = 1.0;
	
	public class Products{
		private String title;
		private String product_type;
		private ArrayList<Variant> variants;

		public String getProduct_type(){
			return product_type;
		}
		
		public ArrayList<Variant> getVariants(){
			return variants;
		}
		
		public String toString(){
			return title;
		}
	}
	
	public class StorePage{
		private LinkedList<Products> products;
		
		public LinkedList<Products> getProducts(){
			return products;
		}
	}
	
	public class Variant{
		private String title;
		private boolean requires_shipping;
		private boolean taxable;
		private boolean available;
		private double price;
		
		public boolean canPurchase(){
			if(requires_shipping && available)
				return true;
			else
				return false;
		}
		
		public boolean isTaxable(){
			return taxable;
		}
		
		public double getPrice(){
			return price;
		}
		
		public String toString(){
			return title;
		}
	}
	
	public static void main(String[] args) throws Exception {
		// This list contains all time-keeping devices in the store.
		LinkedList<Products> timeKeepers = checkAndConsolidate(sweepStore());
		
		System.out.println("Please enter your province/territory of residence:");
		System.out.println("AB, BC, MB, NL, NT, NS, NU, ON, PE, QC, SK, YT");
		// Take user input. If an invalid response is given, no tax is calculated.
		Scanner keyboard = new Scanner(System.in);
		String prov = keyboard.next().toUpperCase();
		keyboard.close();
		
		double taxRate = 1.0;
		
		switch(prov){
			case "AB":	taxRate = GST;
						break;
			case "BC":	taxRate = BC;
						break;
			case "MB":	taxRate = ONMB;
						break;
			case "NB":	taxRate = MARI;
						break;
			case "NL":	taxRate = MARI;
						break;
			case "NT":	taxRate = GST;
						break;
			case "NS":	taxRate = MARI;
						break;
			case "NU":	taxRate = GST;
						break;
			case "ON":	taxRate = ONMB;
						break;
			case "PE":	taxRate = PEI;
						break;
			case "QC":	taxRate = QC;
						break;
			case "SK":	taxRate = SASK;
						break;
			case "YT":	taxRate = GST;
						break;
		}
		
		// Calculate cost of products and round to two decimal places.
		double cost = calcCost(timeKeepers, taxRate);
		cost = Math.round(cost * 100);
		cost = cost / 100;
		
		System.out.println("The total cost is $" + cost);
	}
	
	private static double calcCost(LinkedList<Products> prods, double taxRate){
		double cost = 0.0;
		
		// This for loop works through products...
		for(int i = 0; i < prods.size(); ++i){
			Products currProd = prods.get(i);
			ArrayList<Variant> vars = currProd.getVariants();
			double currProdCost = 0.0;
			
			// ...and this for loops works through variations.
			for(int j = 0; j < vars.size(); ++j){
				Variant curVar = vars.get(j);
				double curVarCost = 0.0;
				
				if(curVar.canPurchase()){
					if(curVar.isTaxable())
						curVarCost = curVar.getPrice() * taxRate;
					else
						curVarCost = curVar.getPrice();
				}
				else
					System.out.println(curVar.title + currProd.title 
							+ " is unavailable for order.");
				
				currProdCost += curVarCost;
			}
			
			cost += currProdCost;
		}
		
		return cost;
	}
	
	private static LinkedList<Products> checkAndConsolidate(LinkedList<StorePage> pgs){
		LinkedList<Products> timeKeepers = new LinkedList<>();
		
		// Only select watches and clocks for preservation.
		for(int i = 0; i < pgs.size(); ++i){
			Iterator<Products> iterator = pgs.get(i).getProducts().iterator();
			while(iterator.hasNext()){
				Products currProd = iterator.next();
				if(currProd.getProduct_type().equals("Watch") ||
						currProd.getProduct_type().equals("Clock")){
					timeKeepers.add(currProd);
				}
			}
		}
		return timeKeepers;
	}
	
	private static LinkedList<StorePage> sweepStore() throws Exception{
		Gson gson = new Gson();
		int count = 1;
    	String emptyPage = "\"products\":[]";
    	boolean looper = true;
    	BufferedReader in = null;
    	LinkedList<StorePage> pgs = new LinkedList<>();

    	// Keep working through store pages until nothing is returned.
    	while(looper){
	        URL shopURL = new URL("http://shopicruit.myshopify.com/products.json?page=" + count);
	        in = new BufferedReader(new InputStreamReader(shopURL.openStream()));
	        String current = in.readLine();
	        
	        if(current.contains(emptyPage))
	        	looper = false;
	        else{
	    		StorePage pg = gson.fromJson(current, StorePage.class);
	        	pgs.add(pg);
	        	count++;
	        }
    	}
		return pgs;
	}
}