/* Naruse-Newton Coefficient Calculator
 * 
 * Program by Andrew Cai
 * 1/2021
 * 
 * Input the non-empty set I, output Naruse-Newton coefficients corresponding to I
 * Can check unimodality
 * Can print if each coefficient is greater, less, or equal than the next
 * Can print rounded ratios between i and (i+1)th Naruse-Newton coefficients
 * 
 * Can also compute Naruse-Newton coefficients of all I such that |I|<=7
 * 
 * Bypasses int/long limit
 * Time complexity is O(prod(i : I)^2/max(I)^2) -- square of the product of all elements of I except the largest
 */

import java.util.*;
import java.math.*;

public class coefficientCalc {
	static BigInteger[] naruse; // stores the naruse-newton coefficients of I
	static int s; // represents s as in the paper
	static Scanner input;
	
	public static void main(String[] args) {
		input = new Scanner(System.in);
		
		oneSeq();
		
		// printCoeffSmall();
		printCoeffLarge();
		printRatios();
		printOrder();
		isUnimodal();
		
		// smallSets();
	}
	
	public static void smallSets() { // computes coefficients for all I such that |I|<=7
		int[] elements;
		System.out.println("Naruse-Newton Coefficients for |I|<=7: ");
		for (int i=1; i<=127; i++) {
			ArrayList<Integer> temp = new ArrayList<Integer>();
			String s = Integer.toBinaryString(i);
			s = ("00000000" + s).substring(s.length()+1);
			//System.out.println(s);
			
			for (int j=0; j<s.length(); j++) {
				if (s.charAt(s.length()-j-1)=='1') temp.add(j+1);
			}
			elements = new int[temp.size()];
			for (int j=0; j<temp.size(); j++) {
				elements[j] = temp.get(j);
			}
			System.out.println("I = " + Arrays.toString(elements));
			computeCoeff(elements);
			printCoeffSmall();
		}
	}
	
	public static void oneSeq() { // input elements of I
		System.out.println("Please enter |I|: ");
		int size = input.nextInt(); // cardinality of non-empty I
		System.out.println("Please enter the elements of I, space-separated: ");
		int[] elements = new int[size]; // elements of I (positive integers, increasing order)
		for (int i=0; i<size; i++) elements[i] = input.nextInt();
		Arrays.sort(elements);
		
		computeCoeff(elements);
	}
	
	public static void computeCoeff(int[] elements) { // compute naruse-newton coeff. corresponding to I
		int size = elements.length;
		int[] neglengths = new int[size]; // size of each row * -1, neglengths[0] represents the second row etc. -- it's negative so binary search works
		
		for (int i=0; i<size; i++) {
			neglengths[i] = -elements[size-1-i]+size-i-1;
		}
		
		s = -neglengths[0]-1;
		naruse = new BigInteger[s+1]; 
		long[][] hooks = new long[size][s+1]; // hook lengths of each cell w.r.t. the ribbon corresponding to I, hooks[0][0] represents the leftmost cell of the second row
		
		for (int i=0; i<size; i++) {
			for (int j=0; j<-neglengths[i]; j++) {
				int over = Arrays.binarySearch(neglengths, -j-1); 
				if (over<0) over = -over-2;
				else {
					while (over+1<size && neglengths[over+1]==neglengths[over]) over++;
				}
				hooks[i][j] = -neglengths[i]-j+over-i;
			}
		}
		
		// |I| = 1 case
		if (size==1) {
			naruse[0] = BigInteger.valueOf(1);
			for (int i=1; i<elements[0]; i++) {
				naruse[i] = naruse[i-1].multiply(BigInteger.valueOf(i));
			}
			return;
		}
		
		// casework through excited diagrams
		int[] config = new int[size-1]; // details number of "un-moved" cells in each row in the current excited diagram configuration, config[0] corresponds to second row
		for (int i=0; i<=s; i++) {
			naruse[i] = new BigInteger("0");
			boolean start = true;
			
			for (int j=0; j<size-1; j++) {
				config[j] = Math.min(-neglengths[j+1]-1, s-i);
			}
			
			do {
				if (!start) {
					int nonzero = size-2;
					
					while (config[nonzero]==0) nonzero--;
					config[nonzero]--;
					for (int j=nonzero+1; j<size-1; j++) {
						config[j] = Math.min(config[nonzero], -neglengths[j+1]-1);
					}
				}
				else start = false;
				BigInteger currprod = new BigInteger("1");
				for (int j=0; j<size-1; j++) {
					for (int k=0; k<config[j]; k++) {
						currprod = currprod.multiply(BigInteger.valueOf(hooks[j][k]));
					}
					for (int k=config[j]; k<-neglengths[j+1]-1; k++) {
						currprod = currprod.multiply(BigInteger.valueOf(hooks[j+1][k+1]));
					}
				}
				naruse[i] = naruse[i].add(currprod);
			} while (config[0]>0);
			
			for (int j=0; j<i; j++) {
				naruse[i]= naruse[i].multiply(BigInteger.valueOf(hooks[0][-neglengths[0]-j-1]));
			}
		}
	}
	
	public static void printCoeffSmall() { // print coefficient sequence, better for small numbers
		System.out.print("Coefficients: ");
		for (int i=0; i<s+1; i++) {
			System.out.print(naruse[i] + " ");
		}
		System.out.println();
		System.out.println();
	}
	
	public static void printCoeffLarge() { // print coefficient sequence, better for large numbers
		System.out.println();
		for (int i=0; i<s+1; i++) {
			String str = String.format("%02d", i);
			System.out.println("C_" + str + " = " + naruse[i]);
		}
	}
	
	public static void printOrder() { // print if each coefficient is greater, less, equal to the next
		ArrayList<Integer> GreaterThanNext = new ArrayList<Integer>(); // holds the indices of the coefficients that are greater than that of the next index
		if (s==0) return;
		
		System.out.println();
		System.out.print("C_00");
		for (int i=1; i<s+1; i++) {
			int res = naruse[i-1].compareTo(naruse[i]);
			if (res==0) System.out.print("=");
			else if (res<0) System.out.print("<");
			else {
				System.out.print(">");
				GreaterThanNext.add(i-1);
			}
			
			String str = String.format("%02d", i);
			System.out.print("C_" + str);
		}
		System.out.println();
		System.out.println();
		
		/*//prints indices of coefficients greater than that of the next index
		for (int i=0; i<GreaterThanNext.size(); i++) {
			System.out.println(GreaterThanNext.get(i) + ">" + (GreaterThanNext.get(i)+1));
		}
		*/
	}
	
	public static void printRatios() { // print ratios between i and (i+1)th coefficients
		if (s==0) return;
		
		System.out.println();
		BigDecimal[] narusedec = new BigDecimal[s+1];
		for (int i=0; i<s+1; i++) {
			narusedec[i] = new BigDecimal(naruse[i]);
		}
		
		for (int i=0; i<s; i++) {
			String str1 = String.format("%02d", i);
			String str2 = String.format("%02d", i+1);
			System.out.println("C_" + str1 + "/C_" + str2+ " = " + narusedec[i].divide(narusedec[i+1], 12, RoundingMode.HALF_UP));
		}
	}
	
	public static void isUnimodal() { // check unimodality
		boolean ret = true;
		for (int i=1; i<s; i++) {
			if (naruse[i].compareTo(naruse[i-1])<0 && naruse[i].compareTo(naruse[i+1])<0) ret = false;
		}
		if (ret) System.out.print("The sequence is unimodal.");
		else System.out.print("The sequence is NOT unimodal.");
	}
}
