//Stejara Dinulescu
//Class that describes properties and functionality of each node in the tree structure

import java.util.ArrayList; //imports Java's ArrayList structure


public class PSTNode<E> {
	private int index = 0;
	private String stringMotives; //string stored for each object
	private int count; //counts the number of instances that character is in the input string
	private ArrayList<PSTNode<E>> children = new ArrayList<PSTNode<E>>(); //next node
	private double empiricalProbability; //stores empirical probability
	private double conditionalProbability; //stores conditional probability
	//private ArrayList<PSTNode<E>> temp = new ArrayList<PSTNode<E>>(); //previous node
	private PSTNode<E> node = this;
	
	private ArrayList<Double> nextProbs = new ArrayList<Double>(); //holds probabilities of what follows the node
	
	
	public PSTNode() {	} //default constructor
	public PSTNode(String stringMotives, int count) { //overloaded constructor to create a node with a String and a count
		this.stringMotives = stringMotives;
		this.count = count;
	}
	
//getters and setters so that private variables can be accessed
	public PSTNode<E> getNode() {
		return this;
	}
	public void setString(String stringMotives) {
		this.stringMotives = stringMotives;
	}
	public String getString() {
		return stringMotives;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getCount() {
		return count;
	}
	public void setEmpProb(double empiricalProbability) {
		this.empiricalProbability = empiricalProbability;
	}
	public double getEmpProb() {
		return empiricalProbability;
	}
	public void setCondProb(double conditionalProbability) {
		this.conditionalProbability = conditionalProbability;
	}
	public double getCondProb() {
		return conditionalProbability;
	}
	public void setNextProbs(ArrayList<Double> next) {
		for (int i = 0; i < next.size(); i++) {
			nextProbs.add(next.get(i));
		}
	}
	public ArrayList<Double> getNextProbs() {
		return nextProbs;
	}
	public void addChildren(PSTNode<E> node) {//adds the node in the parameter to the arrayList pertaining to that motive
		//System.out.println(node.stringMotives + " " + node.count);
		children.add(node);
	}
	public ArrayList<PSTNode<E>> getChildren() { //gets the arrayList pertaining to that motive
		return children;
	}
	public boolean hasChildren() {
		if (children.size() != 0) {
			return true;
		} else { return false; }
	}

	//functionality of the nodes
	
	public void listChildren(int level) {
		System.out.println(getString() + " " + level);
		for (int i = 0; i < children.size(); i++) {
			children.get(i).listChildren(level+1);
		}
	}
	
	public boolean search(String input) {
		if (getString().equals(input)) {
			count++;
			return true;
		} else if (input.length() > getString().length()) {
			//if the suffix matches
			if (input.substring(input.length()-getString().length(), input.length()).equals(getString()) || 
					(getString().equals(""))) //handles empty string case (root node)
			{
				int index =0; 
				boolean found = false;
				while(!found && index < children.size()) { //searches through children
					found = children.get(index).search(input); 
					index++;
				}
				if(!found) { //add if node hasn't been found
					PSTNode<E> child = new PSTNode<E>(input, 1);
					children.add(child);
				} return true; 
			} else { return false; }
		} else return false; 
	}
	
	public double calculateEmpProbs(String input, double pMinValue) { //calculates and sets empirical probability for each node
		int countCount = count;
		double empProb = countCount/(double)input.length(); //calculates empirical probability
		//System.out.println(getString() + " " + empProb);
		if (empProb < pMinValue && getString()!="") { //handles base case, removes node if probability is below pMin cutoff
			//System.out.println("Remove " + getString());
			//set all attributes to null
			empProb = 0.0;
			stringMotives = null;
			countCount = 0;
			int index = 0;
			while (stringMotives == null && index < children.size()) { //if a motive is null, remove the children
				children.remove(index);
				node = null; //should remove null? How do I delete the single node within this class?
			}
		} else { empiricalProbability = empProb; }
		
		for (int i = 0; i < children.size(); i++) { //do this for children as well
			children.get(i).calculateEmpProbs(input, pMinValue);
		}
		
		return empProb;
	}
	
	public ArrayList<String> eliminateCondProbs(String input, int rValue, ArrayList<String> passes) {
		if (passes.contains(stringMotives) == false && stringMotives.length()>1) { //handles base case, removes node if probability is below pMin cutoff
			//System.out.println("Remove " + getString());
			//set all attributes to null
			stringMotives = null;
			count = 0;
			int index = 0;
			while (stringMotives == null && index < children.size()) { //if a motive is null, remove the children
				children.remove(index);
				node = null; //should remove null? How do I delete the single node within this class?
			}
		} else {  }
		if (hasChildren() == true) {
			for (int i = 0; i < children.size(); i++) {
				//System.out.println(stringMotives);
				if (children.get(i).stringMotives != null) {
					children.get(i).eliminateCondProbs(input, rValue, passes);
				}
			}
		}
		return passes;
	}
	
	public void calcs(String input, ArrayList<Character> motives, ArrayList<Integer> tally, ArrayList<Double> probabs, ArrayList<Integer> counts) { //calculates probabilities in the tree based on next
		char next;
		int length = 0;
		int probCount = 0; 
//		System.out.println("StringMotives: " + stringMotives);
		if (stringMotives != null) {
			if (stringMotives.equals(input.substring(input.length()-stringMotives.length()))) {  probCount = (count - 1);  }
			else {  probCount = count;  }
			if (stringMotives.length() != 0) {  length = stringMotives.length();  } 
			else { return; }
//			System.out.println("String length: " + length);
			for (int a = 0; a < input.length() - length; a++) { //loops through input string to calculate tallies
				if (input.substring(a, a+length).equals(stringMotives)) { //if input contains root
//					System.out.println("Input: " + stringMotives);
					next = input.charAt(a+length);
//					System.out.println("Next: " + next); //next char
					if (motives.contains(next)) {
//						System.out.println("Set: " + next + " to: " + (tally.get(motives.indexOf(next)) + 1));
						tally.set(motives.indexOf(next), tally.get(motives.indexOf(next)) + 1);
					}
				}
			}
//			System.out.println("Motives: " + motives + " Tallies: " + tally);
			for (int j = 0; j < probabs.size(); j++) {
//				System.out.println("Tallies " + tally.get(j) + " Counts " + probCount);
				probabs.set(j, (double)tally.get(j)/probCount);
//				System.out.println("Prob calc: " + probabs.get(j));
			}
			setNextProbs(probabs);
			for (int t = 0; t < tally.size(); t++) {
				tally.set(t, 0);
				probabs.set(t,  0.0);
			}
			if (hasChildren()) {
				for (int i = 0; i < children.size(); i++) {
					children.get(i).calcs(input, motives, tally, probabs, counts);
				}
			}
		}
	}
	
	public void printProbabilities() { //prints conditional probabilities
		System.out.println(stringMotives + ": " + getNextProbs());
		if (hasChildren()) {
			for (int i = 0; i < children.size(); i++) {
				children.get(i).printProbabilities();
			}
		}
	}

	public void calcSmoothing(double gval) { //smoothing algorithm
		//System.out.println(getNextProbs());
		for (int i = 0; i < nextProbs.size(); i++) {
			nextProbs.set(i, ((1 - (gval * count))*getNextProbs().get(i) + gval)) ;
		}
		//System.out.println("New probs: " + nextProbs);
	}
	
	public String generate(String curr, ArrayList<Character> singleMotives, int lvalue) { //generate string in PST node class based on probability
		double rand = 0.0;
		double prob = 0;
		double nextProb = 0;
		boolean found = false;
		//takes in a string as input to decide on probabilities
		//look backwards in generated string
		String generatedStr = ""; //instantiate generatedString
		if (curr.equals(stringMotives)) { //if the input equals the stringMotive
			found = true;
			//System.out.println("Found");
			nextProb = nextProbs.get(0);
			//calculate based on probability what comes next
			rand = Math.random(); //generates random number
			for (int i = 0; i < nextProbs.size() - 1; i++) { //check probability ranges
//				System.out.println("Prob: " + prob + " Rand: " + rand + " nextProb: " + nextProb); //probability ranges
				if (prob < rand && rand < nextProb) {
					generatedStr = singleMotives.get(i) + "";
//					System.out.println("Char added: " + generatedStr);
					return generatedStr;
				} else { 
					prob = nextProbs.get(i);
					nextProb = nextProb + nextProbs.get(i+1);
				}
			} 
		} else { //search through children until it is found
			int index = 0;
			while (!found && index < children.size()) {
//				System.out.println("Not found");
				children.get(index).generate(curr, singleMotives, lvalue); //search children
				index++;
			}
			if (found) { 
				//System.out.println("HELLLLLOOOOOOOOO"); 
				return  generatedStr;   
			}
			else { 
				//System.out.println("Generating from empty"); 
				return "";  
			}
		}
		return generatedStr;
	}
}
