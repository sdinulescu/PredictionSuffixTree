//Stejara Dinulescu
//Main class to run program

public class Main {
	public static String abracadabra = "abracadabra"; //creates test string abracadabra
	public static String abcccdaadcdaabcad = "abcccdaadcdaabcad"; //creates test string abcccdaadcdaabcad
	public static int L = 3; //L value
	public static double pMin = 0.1;
	public static int r = 2;
	
	public static void main(String[] args) {
		System.out.println("ABRACADABRA");
		Tree<Character> treeStruct = new Tree<Character>(); //creates tree structure for desired list
		treeStruct.search("abracadabra", L); //searches through motives to create tree structure
		System.out.println("Tree structure before elimination: ");
		treeStruct.list();
		System.out.println("-------------------------------------------------------");
		treeStruct.eliminateEmpirical("abracadabra", pMin); //first step: eliminate empirical probabilities based on pMin
		System.out.println("Tree structure after pMin elimination: ");
		treeStruct.list();
		System.out.println("-------------------------------------------------------");
		System.out.println("Conditional Probability Elimination: ");
		treeStruct.eliminateConditional("abracadabra", r);
		System.out.println("Tree structure after r elimination: ");
		treeStruct.list();
		System.out.println("-------------------------------------------------------");
		System.out.println("Probabilities for each next: ");
		treeStruct.calcNextProbs("abracadabra");
		treeStruct.printProbs();
		System.out.println("-------------------------------------------------------");
		System.out.println("Generate: ");
		treeStruct.generateString(L);
		System.out.println("GeneratedString: " + treeStruct.getGeneratedString());
		System.out.println("--------------------------------------------------------------------------------");
		
		System.out.println("ABCCCDAADCDAABCAD");
		Tree<Character> treeStruct2 = new Tree<Character>();
		treeStruct2.search("abcccdaadcdaabcad", L);
		System.out.println("Tree structure before elimination: ");
		treeStruct2.list();
		System.out.println("-------------------------------------------------------");
		treeStruct2.eliminateEmpirical("abcccdaadcdaabcad", pMin); //first step: eliminate empirical probabilities based on pMin
		System.out.println("Tree structure after pMin elimination: ");
		treeStruct2.list();
		System.out.println("-------------------------------------------------------");
		System.out.println("Conditional Probability Elimination: ");
		treeStruct2.eliminateConditional("abcccdaadcdaabcad", r);
		System.out.println("Tree structure after r elimination: ");
		treeStruct2.list();
		System.out.println("-------------------------------------------------------");
		System.out.println("Probabilities for each next: ");
		treeStruct2.calcNextProbs("abcccdaadcdaabcad");
		treeStruct2.printProbs();
		System.out.println("-------------------------------------------------------");
		System.out.println("Generate: ");
		treeStruct2.generateString(L);
		System.out.println("GeneratedString: " + treeStruct2.getGeneratedString());
		System.out.println("--------------------------------------------------------------------------------");
		
	}
}

	
	