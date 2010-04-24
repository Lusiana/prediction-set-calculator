import java.util.ArrayList;
import java.util.HashSet;

public class ProductionRule {

	private int id;
	private Element LHS;
	private boolean ruleDerivesEmpty;
	private ArrayList<Element> rhs = new ArrayList<Element>();
	private ArrayList<HashSet<Element>> firstSet = new ArrayList<HashSet<Element>>();
	private ArrayList<HashSet<Element>> followSet = new ArrayList<HashSet<Element>>();
	private ArrayList<Element> predictSet = new ArrayList<Element>();

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

	public void printRuleInfo() {
		System.out.print("ID: " + this.id + " \t>> ");
		System.out.print("LHS: " + this.LHS + " \t>> ");
		System.out.print("DerivesEmpty?: " + this.ruleDerivesEmpty + " \t>> ");
		System.out.print("RHS: " + this.rhs + " \t>> ");
		System.out.print("FirstSet: " + this.firstSet + " \t>> ");
		System.out.println("FollowSet: " + this.followSet);
		// System.out.println("PredictSet: " + this.PredictSet);
	}

}
