import java.util.ArrayList;
import java.util.HashSet;

public class ProductionRule {

	private int id;
	private Element LHS;
	private boolean ruleDerivesEmpty;
	private ArrayList<Element> rhs = new ArrayList<Element>();
	private ArrayList<HashSet<Element>> firstSet = new ArrayList<HashSet<Element>>();
	private ArrayList<HashSet<Element>> followSet = new ArrayList<HashSet<Element>>();
	private ArrayList<HashSet<Element>> predictSet = new ArrayList<HashSet<Element>>();

	ProductionRule(int id) {
		this.id = id;
	}

	public void initLHS(Element elem) {
		this.LHS = elem;
	}
	
	public Element getLHS() {
		return this.LHS;
	}
	
	public void setRuleDerivesEmpty(boolean b) {
		this.ruleDerivesEmpty = b; 
	}
	
	public boolean ruleDerivesEmpty() {
		return this.ruleDerivesEmpty; 
	}

	public void addRHS(Element elem) {
		this.rhs.add(elem);
	}

	public ArrayList<Element> getRHS() {
		return this.rhs;
	}

	public void addToFirstSet(HashSet<Element> set) {
		this.firstSet.add(set);
	}
	
	public ArrayList<HashSet<Element>> getFirstSet() {
		return this.firstSet;
	}

	public void addToFollowSet(HashSet<Element> set) {
		this.followSet.add(set);
	}
	
	public ArrayList<HashSet<Element>> getFollowSet() {
		return this.followSet;
	}
	
	public void addToPredictSet(HashSet<Element> set) {
		this.predictSet.add(set);
	}
	
	public ArrayList<HashSet<Element>> getPredictSet() {
		return this.predictSet;
	}

	public void printRuleInfo() {
		System.out.print("ID: " + this.id + "\t>> ");
		System.out.print("LHS: " + this.LHS + "\t>> ");
		System.out.print("Deriv.Emp?: " + this.ruleDerivesEmpty + "\t>> ");
		System.out.print("RHS: " + this.rhs + "\t>> ");
		System.out.print("First: " + this.firstSet + "\t>> ");
		System.out.println("Follow: " + this.followSet + "\t>>");
		System.out.println("Predict: " + this.predictSet);
	}

}
