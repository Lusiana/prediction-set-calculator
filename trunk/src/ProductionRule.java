import java.util.ArrayList;
import java.util.HashSet;

public class ProductionRule {
	
	private int id;
	private Element LHS;
	private ArrayList<Element> RHSList = new ArrayList<Element>();
	private ArrayList<HashSet<Element>> FirstSet = 
						new ArrayList<HashSet<Element>>();
	private ArrayList<Element> FollowSet = new ArrayList<Element>();
	private ArrayList<Element> PredictSet = new ArrayList<Element>();

	ProductionRule(int id) {
		this.id = id;
	}
	
	public void initLHS(Element elem) {
		this.LHS = elem;
	}
	
	public void addToRHSList(Element elem) {
		this.RHSList.add(elem);
	}
	
	public ArrayList<Element> getRHSList() {
		return this.RHSList;
	}
	
	public void addToFirstSet(HashSet<Element> set) {
		this.FirstSet.add(set);
	}
	
	public void printRuleInfo(){
		System.out.print("ID: " + this.id + " --> ");
		System.out.print("LHS: " + this.LHS + " --> ");
		System.out.print("RHS: " + this.RHSList + " --> ");
		System.out.println("FirstSet: " + this.FirstSet);
		//System.out.println("FollowSet: " + this.FollowSet);
		//System.out.println("PredictSet: " + this.PredictSet);
	}
	
}
