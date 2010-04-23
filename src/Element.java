public class Element {
	
	private String element;
	private boolean isStartSymbol;
	private boolean visitedFirst;
	private boolean symbolDerivesEmpty;
	
	Element(String element) {
		this.element = element;
	}
	
	public String getElement() {
		return this.element;
	}
	
	public void setIsStartSymbol(boolean b) {
		this.isStartSymbol = b;
	}
	
	public boolean isStartSymbol() {
		return this.isStartSymbol;
	}
	
	public void setVisitedFirst(boolean b) {
		this.visitedFirst = b;
	}
	
	public boolean isVisitedFirst() {
		return this.visitedFirst;
	}
	
	public void setSymbolDerivesEmpty(boolean b) {
		this.symbolDerivesEmpty = b;
	}
	
	public boolean isSymbolDerivesEmpty() {
		return this.symbolDerivesEmpty;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o.equals(this.element))
			return true;
		else
			return false;
	}
	
	@Override
	public String toString(){
		return this.element;
	}
	
	@Override
	public int hashCode() {
	    return this.element.hashCode();
	} 
	
	public void printElemInfo(){
		System.out.print("name: " + this.element + " --> ");
		System.out.print("visitedFirst: " + this.visitedFirst + " ");
		System.out.print("symbolDerivesEmpty: " + this.symbolDerivesEmpty + " ");
		System.out.println("isStart: " + this.isStartSymbol + " ");
		//System.out.println("FirstSet: " + FirstSet);
		//System.out.println("FollowSet: " + FollowSet);
		//System.out.println("PredictSet: " + PredictSet);
	}
	
}
