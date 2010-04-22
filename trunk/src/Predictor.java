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
 * 1) isSymbolDerivesEmpty property is held in the object where in LHSList for 
 *    each nonterminal.
 * =============================================================================
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Predictor {

	/* Global Variables */
	public static String newline = System.getProperty("line.separator");
	private ArrayList<Element> elements = new ArrayList<Element>();
	private ArrayList<Element> terminals = new ArrayList<Element>();
	private ArrayList<Element> nonterminals = new ArrayList<Element>();
	private HashMap<Element, ProductionRule> allProductionRules = 
								new HashMap<Element, ProductionRule>();
	private ArrayList<Element> LHSList = new ArrayList<Element>();
	private HashSet<HashSet<Element>> answerSet = new HashSet<HashSet<Element>>();
	private String grammar = "";
	private int numberOfRules = 0;

	/*
	 * main: Main procedure of the program
	 */
	public static void main(String[] args) {
		Predictor p = new Predictor();
		try {
			p.parseInput();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println("elementslist: " + p.elements);
		p.constructTerminalNonterminalList();
		//System.out.println("ter: " + p.terminals);
		//System.out.println("nonter: " + p.nonterminals);
		p.computeFirstSet();
		System.out.println("final answerset: " + p.answerSet);

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
		System.out.println(grammar);
	}

	/*
	 * constructElementList: Constructs the element list using pattern search
	 * over the line buffer
	 * 
	 * @line: line buffer from stdin
	 */
	public void constructElementList(String line) {
		Pattern identifierPattern = Pattern.compile("[A-Za-z_$][A-Za-z0-9_$]*");
		Pattern stringPattern = Pattern.compile("'([^\\\\']+|\\\\([btnfr\"'\\\\]|[0-3]?[0-7]{1,2}|u[0-9a-fA-F]{4}))*'|\"([^\\\\\"]+|\\\\([btnfr\"'\\\\]|[0-3]?[0-7]{1,2}|u[0-9a-fA-F]{4}))*\"");
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
			elements.add(e);
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
				if ((!nonterminals.contains(elem)) && (!elem.equals(replacer)))
					nonterminals.add(elem);
			} else {
				if (!nonterminals.contains(elem))
					terminals.add(elem);
			}
		}
	}

	/*
	 * computeFirstSet: Triggers the computation of the first set of the each
	 * grammar rule return a set; i.e. we do not want any duplicate inside
	 */
	public void computeFirstSet() {
		Element keyLHS = null;

		for (Element i : nonterminals)
			i.setVisitedFirst(false);
		
		createProductionRules();
		setSymbolsDeriveEmpty();
		
		for (int i = 0; i < allProductionRules.size(); i++) {
			keyLHS = LHSList.get(i);
			// pr = allProductionRules.get(keyLHS);
			// pr.printRuleInfo();
			answerSet.add(internalFirst(keyLHS));
		}
	}

	/*
	 * createProdutionRules Initializes a ProductionRule instance for each
	 * grammar rule in the grammar input and put each one in a HashMap with a
	 * unique key
	 */
	public void createProductionRules() {
		BufferedReader in = new BufferedReader(new StringReader(grammar));
		Element LHS = null;
		Element RHSRule = null;
		String[] splitted = null;
		String line = null;
		int splitTimes = 0;
		int id = 0;

		try {
			while (((line = in.readLine()) != null)) {
				splitted = line.split("[:|;]");
				splitTimes = splitted.length;
				LHS = new Element(splitted[0].trim());
				LHSList.add(LHS);
				ProductionRule rule = new ProductionRule(id);
				allProductionRules.put(LHS, rule);

				for (int i = 1; i < splitTimes; i++) {
					if (!splitted[i].contains(";")) {
						if (Pattern.matches("[\\s]+", splitted[i]))
							RHSRule = new Element(splitted[i]);
						else
							RHSRule = new Element(splitted[i].trim());
						rule.initLHS(LHS);
						rule.addToRHSList(RHSRule);
					}
				}

				id++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * internalFirst:
	 * Computes the first set of specific grammar rule
	 * @XB: the each side of the production rule; this should be tokenized so
	 * then X is the first token of the string, the rest is B
	 */
	public HashSet<Element> internalFirst(Element XB) {
		HashSet<Element> hs = new HashSet<Element>();
		String[] tokenizedXB = null;
		String rest = "";
		int len = 0;

		tokenizedXB = tokenize(XB);
		len = tokenizedXB.length;
		
		for (int i=1; i<len; i++) {
			rest += tokenizedXB[i];
			rest += " ";
		}
		
		//if (len > 1)
		//	B = new Element(rest);

		/* Case-1: XB is empty */
		System.out.println("XB: " + XB.getElement());
		
		System.out.print("Step10 --> ");
		if (len == 0 || Pattern.matches("[\\s]+", XB.getElement())) {
			hs.add(null);
			answerSet.add(hs);
			return hs;
		}
		
		System.out.print("Step11 --> ");
		/* Case-2: X is a terminal */
		Element X = new Element(tokenizedXB[0]);
		Element B = new Element(rest);
		//System.out.println("X: " + X);
		//System.out.println("B: " + B);
		if (terminals.contains(X)) {
			System.out.println(terminals.contains(X));
			hs.add(X);
			answerSet.add(hs);
			return hs;
		}

		/* Case-3: X is a nonterminal */
		System.out.print("Step12 --> ");
		hs.clear();
		if (!X.isVisitedFirst()) {
			System.out.print("Step13 --> ");
			X.setVisitedFirst(true);
			System.out.println("X: " + X.getElement());
			if (LHSList.contains(X)) {
				ProductionRule pr = allProductionRules.get(X);
				ListIterator<Element> li = pr.getRHSList().listIterator();
				while(li.hasNext()) {
					Element rhs = li.next();
					System.out.println("rhs: " +  rhs + " $ ");
					System.out.print("Step14 --> ");
					answerSet.add(internalFirst(rhs));
				}
			}
		}
		if (LHSList.contains(X)) {
			int x = LHSList.indexOf(X);
			//System.out.println("lhsget: " + LHSList.get(x));
			System.out.print("Step15 --> ");
			if (LHSList.get(x).isSymbolDerivesEmpty()) {
				System.out.println("B: " + B);
				answerSet.add(internalFirst(B));
				System.out.println("answerset: " + answerSet);
			}
		}
		
		System.out.print("Step16 --> ");
		return hs;
	}

	public String[] tokenize(Element e) {
		String[] tokenized = e.getElement().split(" ");
		return tokenized;
	}

	public void setSymbolsDeriveEmpty() {
		for (int i = 0; i < LHSList.size(); i++) {
			ProductionRule pr = allProductionRules.get(LHSList.get(i));
			ListIterator<Element> li = pr.getRHSList().listIterator();
			
			while(li.hasNext()) {
				String next = li.next().getElement();
				if(Pattern.matches("[\\s]+", next)) {
					//System.out.println("true: " + next);
					LHSList.get(i).setSymbolDerivesEmpty(true);
				} else  {
					//System.out.println("false: " + next);
					LHSList.get(i).setSymbolDerivesEmpty(false);
				}
			}
		}
	}

}
