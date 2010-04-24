/* =============================================================================
 * 
 * Name                   : Predictor.java
 * 			    ProductionRule.java + Element.java
 * Author                 : Hakki Caner KIRMIZI
 * Description            : A java program which can evaluate an input grammar 
 * 			    and computes the first and follow set of it. Then, 
 * 			    it constructs a table of this result along with the 
 * 			    prediction set for each specific production rule.
 * Environment            : Windows-7 Entreprise
 * Java IDE               : Eclipse Build id: 20100218-1602
 * Compiler               : javac 1.6.0_16
 * Version Control        : TortoiseSVN 1.6.7, Build 18415 - 32 Bit
 * Project Hosting        : http://code.google.com/p/prediction-set-calculator/
 * License                : GNU General Public License v3
 * 
 * =============================================================================
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class PredictSet {

	/* Global Variables */
	public static String newline = System.getProperty("line.separator");
	private ArrayList<Element> elements = new ArrayList<Element>();
	private ArrayList<Element> terminals = new ArrayList<Element>();
	private ArrayList<Element> nonterminals = new ArrayList<Element>();
	private ArrayList<Element> symbolDerivesEmptyList = new ArrayList<Element>();
	private static ArrayList<ProductionRule> allProductionRules = 
								new ArrayList<ProductionRule>();
	
	private String grammar = "";
	
	/*
	 * main: Main procedure of the program
	 */
	public static void main(String[] args) {
		HashSet<Element> ans = new HashSet<Element>();
		PredictSet ps = new PredictSet();
		
		try {
			ps.parseInput();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ps.constructTerminalNonterminalList();
		ps.createProductionRules();
		
		// compute the predict set for this rule
		for (ProductionRule pr : allProductionRules) {
			ans = ps.computePredictSet(pr);
			pr.addToPredictSet(ans);
			pr.printRuleInfo();
		}
		
		
	}
	
	/*
	 * parseInput: Parse the input grammar sequences from stdin
	 */
	public void parseInput() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String line = null;

		while ((line = in.readLine()) != null && line.length() != 0) {
			constructElementList(line);
			grammar += line;
			if (line.contains(";")) {
				grammar += newline;
			}
		}
	}
	
	
	/*
	 * constructElementList: Constructs the element list using pattern search
	 * over the line buffer
	 * @line: line buffer from stdin
	 */
	public void constructElementList(String line) {
		Pattern identifierPattern = Pattern.compile("[A-Za-z_$][A-Za-z0-9_$]*");
		Pattern stringPattern = Pattern
				.compile("'([^\\\\']+|\\\\([btnfr\"'\\\\]|[0-3]?[0-7]{1,2}|u[0-9a-fA-F]{4}))*'|\"([^\\\\\"]+|\\\\([btnfr\"'\\\\]|[0-3]?[0-7]{1,2}|u[0-9a-fA-F]{4}))*\"");
		Matcher stringMatcher = stringPattern.matcher(line);
		Matcher identifierMatcher = identifierPattern.matcher(line);
		Element e = null;

		// First add all tokens to terminal list
		while (identifierMatcher.find()) {
			e = new Element(identifierMatcher.group());
			elements.add(e);
		}
		while (stringMatcher.find()) {
			e = new Element(stringMatcher.group());
			terminals.add(e);
		}
	}
	
	
	/*
	 * constructTerminalNonTerminalList: Constructs two lists: 'terminals' and
	 * 'nonterminals' checking for any repeats of the element in the elements
	 * list and comparing that does the element appears on the RHS also appears
	 * on the LHS 
	 */
	public void constructTerminalNonterminalList() {
		Element elem = null;
		Element replacer = new Element("_");
		int found = -1;
		int size = elements.size();

		for (int i = 0; i < size; i++) {
			elem = elements.get(i);
			elements.set(i, replacer);
			if ((found = elements.indexOf(elem)) != -1) {
				elements.set(found, replacer);
				if ((!nonterminals.contains(elem)) && 
						(!elem.equals(replacer)) && (symbolDerivesEmptyList.contains(elem)))
					nonterminals.add(elem);
			} else {
				if (!nonterminals.contains(elem))
					terminals.add(elem);
			}
		}
	}
	
	
	/*
	 * createProdutionRules: Initializes a ProductionRule instance for each
	 * grammar rule in the grammar input and put each one in an ArrayList of 
	 * 'allProductionRules'
	 */
	public void createProductionRules() {
		HashSet<Element> ans = new HashSet<Element>();
		BufferedReader in = new BufferedReader(new StringReader(grammar));
		ProductionRule pr = null;
		Element lhs = null, rhsRule = null;
		String[] splitted = null;
		String line = null;
		boolean isStart = true;
		int splitTimes = 0;
		
		//System.out.println(grammar);

		try {
			
			// each line represents a whole production rule
			// e.g. E : Prefix "(" E ")" | v Tail ;
			while (((line = in.readLine()) != null)) {
				splitted = line.split("[:|;]");
				splitTimes = splitted.length;
				
				// create (splitTimes-1) ProductionRule for this line
				// actually lhs of these are going to be all same
				for (int i=0; i<splitTimes-1; i++) {
					pr = new ProductionRule(i);
					lhs = new Element(splitted[0].trim());
					if (isStart) {
						lhs.setIsStartSymbol(true);
						nonterminals.add(lhs);
						isStart = false;
					} else {
						lhs.setIsStartSymbol(false);
					}
					
					// initialize the lhs of this rule
					pr.initLHS(lhs);
					
					// add it to the list of all production rules with lhs key
					allProductionRules.add(pr);
					
					// initialize a rhs rule
					// if the rule derives empty initialize a null value
					if (!splitted[i+1].contains(";")) {
						if (Pattern.matches("[\\s]+", splitted[i+1])) {
							lhs.setSymbolDerivesEmpty(true);
							if (!symbolDerivesEmptyList.contains(lhs))
								symbolDerivesEmptyList.add(lhs);
							pr.setRuleDerivesEmpty(true);
							rhsRule = new Element("null");
							pr.addRHS(rhsRule);
							ans.add(new Element("null"));
							pr.addToFirstSet(ans);
						} else {
							rhsRule = new Element(splitted[i+1].trim());
							pr.addRHS(rhsRule);
						}
					}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	/*
	 * computePredictSet: Computes the prediction set of the given Production
	 * Rule either to run First() or Follow() algorithm
	 * @pr: The Production Rule whose prediction set needs to be computed
	 * returns: A HashSet of Elements which contains the result
	 */
	public HashSet<Element> computePredictSet(ProductionRule pr) {
		HashSet<Element> ans = new HashSet<Element>();
		ans = First(pr.getRHS().get(0));
		pr.addToFirstSet(ans);
		if (ruleDerivesEmpty(pr)) {
			for (Element e : Follow(pr.getLHS()))
				ans.add(e);
			pr.addToFollowSet(ans);
			//pr.printRuleInfo();
		}
		
		pr.printRuleInfo();
		return ans;
	}
	
	
	/*
	 * First: Triggers the computation of the first set
	 * @alpha: The LHS element of a Production Rule
	 * returns: A HashSet of Elements which contains the result
	 */
	public HashSet<Element> First(Element alpha) {
		HashSet<Element> ans = new HashSet<Element>();

		for (Element A : nonterminals)
			A.setVisitedFirst(false);
		ans = internalFirst(alpha);
	
		return ans;
	}
	
	
	/*
	 * internalFirst: Computes the first set of specific grammar rule
	 * @XB: The Element whose first set needs to be computed
	 * returns: A HashSet of Elements which contains the result
	 */
	public HashSet<Element> internalFirst(Element XB) {
		HashSet<Element> ans = new HashSet<Element>();
		String[] tokenizedXB = null;
		String rest = "";
		int len = 0;

		tokenizedXB = tokenize(XB);
		len = tokenizedXB.length;

		for (int i = 1; i < len; i++) {
			rest += tokenizedXB[i];
			rest += " ";
		}

		// Case-1: XB is empty
		//System.out.println("XB: " + XB.getElement() + " $");
		if (len == 0) {
			return null;
		}

		// Case-2: X is a terminal
		Element X = new Element(tokenizedXB[0]);
		Element B = new Element(rest);
		
		if (terminals.contains(X)) {
			// X.printElemInfo();
			ans.add(X);
			return ans;
		}
		
		/* Case-3: X is a nonterminal */
		ans.clear();
		if (!X.visitedFirst()) {
			X.setVisitedFirst(true);
			//System.out.println("X: " + X.getElement() + " $");
			for (ProductionRule pr : allProductionRules) {
				if (pr.getLHS().equals(X)) {
					for (Element e : internalFirst(pr.getRHS().get(0)))
						ans.add(e);
				}
			}
		}
		
		// check symbolDerivesEmptyList which is a hashset for the symbol derives empty or not
		if (symbolDerivesEmptyList.contains(X)) {
			int x = symbolDerivesEmptyList.indexOf(X);
			if (symbolDerivesEmptyList.get(x).symbolDerivesEmpty()) {
				//System.out.println("B: " + B + " $");
				for (Element e : internalFirst(B))
					ans.add(e);
			}
		}
		return ans;
	}
	
	
	/*
	 * tokenize: Tokenizes any LHS or RHS part of the production rule according 
	 * to white space check.
	 * @e: An element version of the LHS or RHS
	 * returns: A String array (splitted version) of the argument
	 */
	public String[] tokenize(Element e) {
		String[] tokenized = e.getElement().split(" ");
		return tokenized;
	}
	
	
	/* 
	 * ruleDerivesEmpty: Checks whether the given rule makes any derivation
	 * either directly (it has a LHS which derives lambda directly) or
	 * indirectly (all RHS Elements of this rule derives empty one by one)
	 * @pr: Production rule to check whether it derives empty or not
	 * returns: true, if argument pr derives empty anyhow; false otherwise
	 */
	public boolean ruleDerivesEmpty(ProductionRule pr) {
		String[] splitted = null;
		int count = 0;
		
		splitted = tokenize(pr.getRHS().get(0));
		
		for (Element e : symbolDerivesEmptyList) {
			for (String s : splitted) {
				if (s.equals(e.getElement())) {
					count += 1;
				}
			}
		}
		
		if ((count == splitted.length) || (pr.ruleDerivesEmpty())) {
			pr.setRuleDerivesEmpty(true);
			return true;
		} else {
			return false;
		}
	}

	
	/*
	 * Follow: Triggers the computation of the follow set
	 * @A: The LHS element of a Production Rule
	 * returns: A HashSet of Elements which contains the result
	 */
	public HashSet<Element> Follow(Element A) {
		HashSet<Element> ans = new HashSet<Element>();
		
		for (Element e : nonterminals)
			e.setVisitedFollow(false);
		ans = internalFollow(A);
	
		return ans;
	}
	
	
	/*
	 * internalFollow: Computes the follow set of specific grammar rule
	 * @A: The Element whose follow set need to be computed
	 * returns: A HashSet of Elements which contains the result
	 */
	public HashSet<Element> internalFollow(Element A) {
		ArrayList<Element> occurrences = new ArrayList<Element>();
		HashSet<Element> ans = new HashSet<Element>();
		
		ans.clear();
		//System.out.println("what came: " + A + " visited? " + A.visitedFollow());
		if (!A.visitedFollow()) {
			
			A.setVisitedFollow(true);
			//System.out.println("in: " + A.visitedFollow());
			//System.out.println("A: " + A);
			occurrences = findOccurrences(A);
			//System.out.println("occ: " + occurrences);
			
			for (Element o: occurrences) {
				//System.out.println("tail: " + Tail(o, A));
				for (Element e : internalFirst(tail(o, A)))
					ans.add(e);

				//System.out.println("ans1: " + ans);
				//System.out.println("tail: " + "$" + Tail(o, A) + "$");
				//System.out.println("allderive: " + allDeriveEmpty((Tail(o, A))));
				if (allDeriveEmpty((tail(o, A)))) {
					//System.out.println("im in");
					Element LHS = findLHSOfProduction(o);
					//System.out.println("lhs: " + LHS + " of " + o);
					for (Element e : internalFollow(LHS))
						ans.add(e);
					//System.out.println("ans2: " + ans);
				}
			}
			
		}
		return ans;
	}
	
	
	/* 
	 * findOccurences: Find any RHS occurrences of given Element
	 * @A: The Element whose RHS occurrences need to be found
	 * returns: An ArrayList which contains all the RHS occurrences of argument A
	 */
	public ArrayList<Element> findOccurrences(Element A) {
		ArrayList<Element> occurrences = new ArrayList<Element>();
		String[] tokenized = null;
		
		for (ProductionRule pr : allProductionRules) {
			Element e = pr.getRHS().get(0);
			tokenized = tokenize(e);
			for (String s : tokenized) {
				if (s.equals(A.getElement()))
					if (!occurrences.contains(e))
						 occurrences.add(e);
			}
		}
		
		return occurrences;
	}

	
	/*
	 * tail: Finds the following Element(s) of a given Element (actually, we
	 * should say element sets), e.g. tail(y) returns 'B C' for the grammar 
	 * rule A : a y B C
	 * @o: The Element (sets) which is going to be searched for tail
	 * @A: The Element which is going to truncate argument o
	 * returns: The part left (tail) after truncate
	 */
	public Element tail(Element o, Element A) {
		Element tail = null;
		String s = "";
		int save = -1;
		String[] tokenized = tokenize(o);
		
		for (int i=0; i<tokenized.length; i++) {
			if (tokenized[i].equals(A.getElement()))
				save = i;
		}
		save += 1;
		for (int i=save; i<tokenized.length; i++) {
			s += tokenized[i];
			s += " ";
		}
		
		tail = new Element(s.trim());
		return tail;
	}
	
	
	/*
	 * allDeriveEmpty: Checks whether given element is a white space or derives
	 * empty or is a terminal
	 * @e: The element whose evaluation needs to be done
	 * returns: true, if it suffices the check arguments; false, otherwise
	 */
	public boolean allDeriveEmpty(Element e) {
		String[] tokenized = tokenize(e);
		
		for (String X : tokenized) {
			Element token = new Element(X);
			if (token.getElement().length() > 0)
				if ((!symbolDerivesEmptyList.contains(token)) || (terminals.contains(token)))
					return false; 
		}
		return true;
	}

	
	/*
	 * findLHSOfProduction: Finds the left hand side of the given element
	 * @e: The element whose LHS needs to be found
	 * returns: the LHS Element of e
	 */
	public Element findLHSOfProduction(Element e) {
		Element found = null;

		for (ProductionRule pr : allProductionRules) {
			if (pr.getRHS().contains(e))
				found = pr.getLHS();
		}
		return found;
	}
	
	
	/*
	 * printAllLists: Prints the all lists initialized before computation
	 */
	public void printAllLists() {
		System.out.println("All-Elements-List: " + elements);
		System.out.println("Terminals-List: " + terminals);
		System.out.println("Nonterminals-List: " + nonterminals);
		System.out.println("Symbol-Derives-Empty-List: " + symbolDerivesEmptyList);
	}
	
}
