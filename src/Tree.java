//Stejara Dinulescu
//Implements the tree structure based on PSTNode class
//Main functions of the program are listed here

import java.util.ArrayList; //imports ArrayList
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Tree<E> extends PSTNode<E>{
	private PSTNode<E> root = new PSTNode<E>("", 0);
	private ArrayList<String> passes = new ArrayList<String>(); //contains all nodes that pass r threshold
	private ArrayList<Character> singleMotives = new ArrayList<Character>(); //holds single motive chars in string
	private ArrayList<Double> probabilities = new ArrayList<Double>();
	private ArrayList<Integer> counts = new ArrayList<Integer>();
	private ArrayList<Integer> tallies = new ArrayList<Integer>();
	
	String generated = "";
	
	Tree() {} //default constructor 
	
	String getGeneratedString() {
		return generated;
	}
	
	void search(String input, int LValue) { //searches input string for motives
		if(LValue <= 0 || input.length() < LValue) { return; } //boundary checks
		String sub = null; //creates a string variable, sets to null
		if(LValue > 1) {
			search(input, LValue-1); //call it recursively, subtracting the LValue each time
		}
		for(int i = 0; i <= input.length() - LValue; i++) {
			sub = input.substring(0 + i, LValue + i);
			addToTree(sub);
			//root.search(sub); //search based on substring (which is based on LValue)
		}
	}
	
	void addToTree(String sub) { //adds children
		root.search(sub);
	}
	
	void list() { //lists out all nodes and children
		root.listChildren(0);
	}
	
	void eliminateEmpirical(String input, double pMinValue) { //eliminates nodes based on pMin Value
		for (int i = 0; i < root.getChildren().size(); i++) {
			root.getChildren().get(i).calculateEmpProbs(input, pMinValue); //calls the calculate probability function in node class
		}
		for (int i = 0; i < root.getChildren().size(); i++) { //removes null values from tree structure
			if (root.getChildren().get(i).getString() == null) {
				root.getChildren().remove(i);
			}
			if (root.getChildren().get(i).hasChildren()) {
				for (int a = 0; a < root.getChildren().get(i).getChildren().size(); a++) {
					if (root.getChildren().get(i).getChildren().get(a).getString() == null) {
						root.getChildren().get(i).getChildren().remove(a);
					}
				}
			}
		}
	}
	
	void calcConditionalPasses(String input, int rValue) {
		int condCount = 0; //counts the number of times a higher order motive goes to a specific "next" node
		int singleCount = 0; //counts the number of times a single character motive goes to a specific "next" node
		int condTotal = 0; //counts the number of times a higher order motive appears in the string (goes to any node)
		int singleTotal = 0; //counts the number of times a single character motive appears in the string (goes to any node)
		double condProb; //stores value for conditional probability for higher order motive (numerator)
		double singleProb; //stores value for conditional probability for single order motive (denominator)
		for (int i = 0; i < input.length() - rValue; i++) { //loops through 
			String motive = input.substring(i, i+rValue); //higher order motive
			char single = motive.charAt(motive.length()-1); //single character motive
			char next = input.charAt(i+rValue); //next char in the input string
			String comp = motive + next; //higher order comparison string
			String compSingle = "" + single + next; //single character comparison string
			for (int a = 0; a < input.length() - rValue; a++) { //loops through entire string to find which higher order motives pass
				if (input.substring(a, a+rValue+1).equals(comp)) { //if comp is found in the input string, increment the count
					condCount++;
				}
				if (input.substring(a, a+rValue).equals(motive)){ //if the motive is found (with any next), increment the total
					condTotal++;
				}
			}
			for (int a = 0; a < input.length() - 1; a++) { //loops through entire string to find which single character motives pass
				if (input.substring(a, a+2).equals(compSingle)) { //if compSingle is found in the input string, increment the count
					singleCount++;
				} 
				if (input.charAt(a) == single) { //if the motive is found (with any next), increment the total
					singleTotal++;
				}
			}
			condProb = (double)condCount / condTotal; //calculate numerator of conditional probability
			singleProb = (double)singleCount / singleTotal; //calculate denominator of conditional probability
			if (condProb/singleProb < rValue) { //if conditional probability is below rValue threshold
			} else { 
//				System.out.println("PASSES: " + motive + " " + condProb/singleProb);
				if(passes.contains(motive)) {
				} else {
					passes.add(motive); //adds to passes (an arraylist)
				}
			}
			//set counts back to 0 for next iterations
			condCount = 0;
			singleCount = 0;
			condTotal = 0;
			singleTotal = 0;
		}
		
	}
	
	void eliminateConditional(String input, int rValue) { //eliminates nodes based on conditional probability
		calcConditionalPasses(input, rValue);
		passes = root.eliminateCondProbs(input, rValue, passes);
		for (int i = 0; i < root.getChildren().size(); i++) { //removes null values from tree structure
			if (root.getChildren().get(i).hasChildren()) {
				for (int a = 0; a < root.getChildren().get(i).getChildren().size(); a++) {
					if (passes.contains(root.getChildren().get(i).getChildren().get(a).getString()) == false) {
						root.getChildren().get(i).getChildren().remove(a);
					}
				}
			}
		}
		printPasses();
	}
	
	void printPasses() {
		System.out.println("Nodes that pass: " + passes);
		for (int i = 0; i < root.getChildren().size(); i++) {
			for (int j = 0; j < root.getChildren().get(i).getChildren().size(); j++) { //only check children's children, since single motives pass
				if (passes.contains(root.getChildren().get(i).getChildren().get(j).getString())) { 
//					System.out.println("PASSES");
				} else {
					System.out.println("Child removed: " + root.getChildren().get(i).getChildren().get(j).getString());
					root.getChildren().get(i).getChildren().remove(j);
				}
			}
		}
	}
	
	void setupCalcs(String input) { //sets up structures needed for probability calculations
		for (int i = 0; i < input.length() - 1; i++) { //calculates number of single motives for next probability calcs
			if (singleMotives.contains(input.charAt(i))) {
				counts.set(singleMotives.indexOf(input.charAt(i)), counts.get(singleMotives.indexOf(input.charAt(i))) + 1);
			} 
			else {
				singleMotives.add(input.charAt(i));
				counts.add(1);
				tallies.add(0);
				probabilities.add(0.0);
			}
		}		
//		System.out.println("Single Motives: " + singleMotives); //holds (a, b, r, c, d)
//		System.out.println("Probabilities: " + probabilities); //holds probability array
//		System.out.println("Counts: " + counts); //holds counts 
	}
	
	void calcNextProbs(String input) { //calculate probabilities based on next
		setupCalcs(input);
		rootCalc(input);
		for (int i = 0; i < root.getChildren().size(); i++) {
			root.getChildren().get(i).calcs(input, singleMotives, tallies, probabilities, counts);
		}
		
	}
	
	void rootCalc(String input) { //calculates probabilities for the root
		for (int i = 0; i < singleMotives.size(); i++) { //loops through a, b, r, c, d
			probabilities.set(i, (double)counts.get(i) / input.length());
		}
		root.setNextProbs(probabilities);
	}
	
	void printProbs() { //prints out the probabilities in the tree structure
		root.printProbabilities();
	}
	
	void generateString(int lvalue) { //generates a string
		lvalue--;
		String genStr = "";
		String matchStr = "";
	
		//if you have a string longer than L, send last two to generate of PST node
		//if tree sends back an empty string, send the single
		//if single doesn't work: go back to the empty string (just do a loop)

		for (int i = 0; i < 40 - generated.length(); i++) {
			if (generated.length() > lvalue) {
				matchStr = generated.substring(generated.length() - lvalue, generated.length());
//				System.out.println("matchString1: " + matchStr);
				loopGenerate(matchStr, genStr, lvalue);
			} else {
				//take care of generated.length() - 1 here
				if (generated.length() > 0) {
					matchStr = generated.substring(generated.length()-1, generated.length());
//					System.out.println("MatchStr: " + matchStr);
					loopGenerate(matchStr, genStr, lvalue);
				} else {
					genStr = root.generate("", singleMotives, lvalue);
					if (genStr.equals("")) {
//						System.out.println("Empty string returned oh noooooooooooo");
						genStr = root.generate(genStr, singleMotives, lvalue); //go back to empty string and re-generate?
					}
					generated = generated + genStr;
				}
			}
		}
	}
	void loopGenerate(String matchStr, String genStr, int lvalue) { //searches through root's children to try to match matchStr to the name of the child
		for (int j = 0; j < root.getChildren().size(); j++) {
			if (matchStr.equals(root.getChildren().get(j).getString())) {
//				System.out.println("FOUND IN CHILDREN!");
				genStr = root.getChildren().get(j).generate(matchStr, singleMotives, lvalue);
				if (genStr.equals("")) {
//					System.out.println("genStr when re-generating from root: " + genStr);
					//DECIDE BETWEEN EMPTY STRING OR BACKTRACKING
					genStr = solveEmptyContext(genStr, lvalue, j);
//					System.out.println("Empty string returned oh noooooooooooo");
				}
			} else { 
//				System.out.println("Not found in children oops" );
				genStr = root.getChildren().get(j).generate(matchStr, singleMotives, lvalue);
//				System.out.println("genStr if not found: " + genStr);
				if (genStr.equals("")) {
//					System.out.println("genStr when re-generating from root: " + genStr);
					genStr = solveEmptyContext(genStr, lvalue, j);
//					System.out.println("Empty string returned oh noooooooooooo");
				}
//				System.out.println("MatchStr: " + matchStr + " GenStr: " + genStr);
			}
			generated = generated + genStr;
		}
	}
	
	String solveEmptyContext(String genStr, int lvalue, int j) {
		double var = Math.random(); 
		if (0 <= var && var <= 0.3) { 
			//System.out.println("Generate from empty string");
			genStr = root.generate(genStr, singleMotives, lvalue); //go back to empty string and re-generate
//			System.out.println("GenStr = " + genStr);
			return genStr;
		} else { //roll back
			//System.out.println("Roll back and generate");
			String curr = generated.substring(generated.length() - 1, generated.length());
//			System.out.println("Curr = " + curr);
			genStr = root.getChildren().get(j).generate(curr, singleMotives, lvalue);
//			System.out.println("GenStr = " + genStr);
			return genStr;
		}
	}
	
	void implementSmoothing(double gVal) { //implements smoothing
//		System.out.println("IMPLEMENT SMOOTHING");
		//TODO look up formula for smoothing and implement
		//calculate a random number g
		for (int i = 0; i < root.getChildren().size(); i++) {
			root.getChildren().get(i).calcSmoothing(gVal);
			if (root.getChildren().get(i).hasChildren()) {
				for (int j = 0; j < root.getChildren().get(i).getChildren().size(); j++) {
					root.getChildren().get(i).getChildren().get(j).calcSmoothing(gVal);
				}
			}
		}
	}
	
	void infiniteLoop(int countBack, int times) {
		int newCountBack = countBack;
		while (newCountBack != 0) {
			solveInfiniteLooping(newCountBack, countBack, times);
			newCountBack--;
		}
	}
	
	void solveInfiniteLooping(int countBack, int oldCountBack, int times) { 
		//System.out.println("SolveInfiniteLooping Called!");
		if ( countBack == 2 ) {
			times = 3;
		} else if ( countBack == 1 ) {
			times = 5;
		}
		//check through generated string
		ArrayList<String> strings = new ArrayList<String>();
		strings = createStrings(strings, countBack);
		
		//count number of times it loops infinitely
		int counter = 0;
		int primaryIndex = 0;
		int finalIndex = countBack;
		boolean isLooping = false;
		int index = 0;
		
		for (int i = 0; i < strings.size() - countBack; i++) {
			counter = 1;
			while ( (i+counter < strings.size()) && strings.get(i).equals(strings.get(i+counter)) ) {
				primaryIndex = i;
				counter++;
			}
			if (counter >= times) {
				finalIndex = primaryIndex+counter;
				isLooping = true;
				break;
			}
		}
		
		//System.out.println("Primary index: " + primaryIndex + " Final index: " + finalIndex);
		//System.out.println("Counter: " + counter + " Times: " + times);
		if (isLooping == true) { //if infinite looping occurs for a number past the number "times"
			//System.out.println("primary: " + primaryIndex + " " + strings.get(primaryIndex) + " final: " + finalIndex + " " + strings.get(finalIndex));
			String correctSubstring = "";
			for (int i = 0; i < primaryIndex; i++) {
				correctSubstring = correctSubstring + strings.get(i);
			}
			System.out.println("correctSubstring: " + correctSubstring);
			generated = correctSubstring;
			generateString(countBack);
			System.out.println("Generated: " + generated);
			strings.clear();
			infiniteLoop(oldCountBack, times);
		} else { System.out.println("No infinite looping occurs"); }
	}
	
	ArrayList<String> createStrings(ArrayList<String> strings, int countBack) {
		int index = 0;
		while (index < generated.length() - countBack) {
			strings.add(generated.substring(index, index + countBack));
			index += countBack;
		}
		System.out.println("Strings: " + strings);
		return strings;
	}
}

