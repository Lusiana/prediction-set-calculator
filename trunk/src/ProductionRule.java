import java.util.ArrayList;

public class ProductionRule {
	
	private int id;
	private Element LHS;
	private ArrayList<Element> RHSList = new ArrayList<Element>();
	private ArrayList<Element> FirstSet = new ArrayList<Element>();
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
	
	public void printRuleInfo(){
		System.out.print("ID: " + this.id + " --> ");
		System.out.print("LHS: " + this.LHS + " --> ");
		System.out.println("RHS: " + this.RHSList + " size: " + this.RHSList.size());
		//System.out.println("FirstSet: " + FirstSet);
		//System.out.println("FollowSet: " + FollowSet);
		//System.out.println("PredictSet: " + PredictSet);
	}
	
}
