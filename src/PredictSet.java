/* =============================================================================
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
 * Notes:
 * ------
 * 1) isSymbolDerivesEmpty property is held in the object where in symbolDerivesEmptyList for 
 *    each nonterminal.
 * =============================================================================
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ListIterator;
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
	private static int numberOfRules = 0;
	
	/*
	 * main: Main procedure of the program
	 */
	public static void main(String[] args) {
		PredictSet ps = new PredictSet();
		try {
			ps.parseInput();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ps.constructTerminalNonterminalList();
		ps.createProductionRules();
		//System.out.println(allProductionRules.keySet());
		//System.out.println(allProductionRules.toString());
		
		//ps.setSymbolsDeriveEmpty();
		// compute the predict set for this rule
		for (ProductionRule pr : allProductionRules) {
			ps.computePredictSet(pr);
			pr.printRuleInfo();
		}
		
		//System.out.println("elementslist: " + ps.elements);
		//System.out.println("ter: " + ps.terminals);
		//System.out.println("nonter: " + ps.nonterminals);
		//System.out.println("lhs: " + ps.LHSList);
		//ps.computePredictSet();
		//ps.outputResult();
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
				numberOfRules++;
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
	 * list
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
	 * grammar rule in the grammar input and put each one in a HashMap with a
	 * unique key
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

	
	public HashSet<Element> computePredictSet(ProductionRule pr) {
		HashSet<Element> ans = new HashSet<Element>();
		ans = First(pr.getRHS().get(0));
		if (ruleDerivesEmpty(pr)) {
			for (Element e : Follow(pr.getLHS()))
				ans.add(e);
		}
		pr.addToFirstSet(ans);
		return ans;
	}
	
	/*
	 * First: Triggers the computation of the first set of the each
	 * grammar rule.
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
	 * @XB: the each side of the production rule; this should be tokenized so
	 * then X is the first token of the string, the rest is B
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
		if (!X.isVisitedFirst()) {
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
	 * returns: A splitted version of the argument
	 */
	public String[] tokenize(Element e) {
		String[] tokenized = e.getElement().split(" ");
		return tokenized;
	}
	
	public boolean ruleDerivesEmpty(ProductionRule pr) {
		String[] splitted = null;
		splitted = tokenize(pr.getRHS().get(0));
		
		for (Element e : symbolDerivesEmptyList) {
			for (String s : splitted) {
				if ((pr.getLHS().equals(e)) || pr.getLHS().equals(s))
					return true;
			}
		}
		
		return false;
	}
	
}
