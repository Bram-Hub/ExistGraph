import java.util.*;

public class ExistNode{
	public ExistNode(){
		assumptions = new TreeSet();
		parent = null;
		level = 0;
		childNodes = new Vector();
		childVars = new Vector();
	}
	
	public ExistNode(String initial){
		this();
		formNode(initial,null);
	}
	
	public ExistNode(ExistNode old){
			
	}
	
	public boolean equals(Object compare){
		return toString().equals(compare.toString());
	}
	
	//inserts a double cut to the graph
	public void doubleCut(){
		formNode("(())",this);
	}
	
	//removes all doublecuts from the graph
	public boolean doubleCutRemoveAll(){
		boolean ret = false;
		boolean tempBool = false;
		for(int Ct=0; Ct < childNodes.size(); Ct++){
			tempBool = ((ExistNode)(childNodes.get(Ct))).doubleCutRemoveAll();
			ret = ret || tempBool;
		}
		for(int Ct2=0; Ct2 < childNodes.size(); Ct2++){
			tempBool = doubleCutRemove(Ct2);
			ret = ret || tempBool;
			if (tempBool ==true)
				Ct2--;
		}
		return ret;
	}
	
	//removes a double cut, if nodeID is the outer cut
	public boolean doubleCutRemove(int nodeID){
		if (nodeID >= childNodes.size())
			return false;
		ExistNode temp = ((ExistNode)childNodes.get(nodeID));
		if(temp.childVars.size() > 0 || temp.childNodes.size() != 1)
			return false;
		temp = (ExistNode)temp.childNodes.get(0);
		childVars.addAll(temp.childVars);
		for(int ct=0;ct<temp.childNodes.size();ct++)
			((ExistNode)temp.childNodes.get(ct)).parent = this;
		childNodes.addAll(temp.childNodes);
		childNodes.remove(nodeID);
		return true;
	}
	
	//iterates a graph in nodeID position to destGraph, if destGraph is a subgraph and is not in nodeID position.
	public boolean iterateNode(int nodeID,ExistNode destGraph){
		if(isAncestor(destGraph) == false)
			return false;
		if((ExistNode)childNodes.get(nodeID) == destGraph)
			return false;
		ExistNode temp = new ExistNode();
		String temp2 = (((ExistNode)childNodes.get(nodeID)).toString());
		temp.formNode(temp2,destGraph);
		destGraph.childNodes.add(temp);
		return true;
	}
	
	//iterates a variable in varID position to destGraph, if destGraph is a subgraph
	public boolean iterateVar(int varID,ExistNode destGraph){
		if(isAncestor(destGraph) == false)
			return false;
		destGraph.childVars.add(new Character(((Character)childVars.get(varID)).charValue()));
		return true;
	}
	
	//deiterates all instances of all variables.
	public boolean deiterateAll(){
		boolean ret = false;
		int ct;
		//if (ct < 1)
		//	return false;
		for(int ct3=0; ct3<childNodes.size(); ct3++){
			ret = ret || deiterateNodeAll(ct3);
		}
		for(int ct2=0; ct2<childVars.size();ct2++){
			ret = ret || deiterateVarAll(ct2);
		}
		for(ct=0; ct<childNodes.size();ct++){
			ret = ret || getNode(ct).deiterateAll();
		}
		return ret;
	}
	
	
	//Returns for either an empty sheet or an empty cut.
	public boolean isEmpty(){
		return(childVars.isEmpty() && childNodes.isEmpty());
	}
	
	//Returns true if the selected node has only a single variable
	public boolean isNegetion(int nodeID){
		return(getNode(nodeID).childVars.size() == 1 && getNode(nodeID).childNodes.isEmpty());
	}
	
	//Returns true if the selected node has an empty node.
	public boolean isTautology(int nodeID){
		boolean ret = false;
		for (int ct=0;ct<getNode(nodeID).childNodes.size();ct++){
			ret = ret || getNode(nodeID).getNode(ct).isEmpty();
		}
		return ret;
	}
	
	//returns true if the current node has an empty node
	public boolean isContradiction(){
		boolean ret = false;
		for(int ct=0; ct<childNodes.size();ct++){
			ret = ret || getNode(ct).isEmpty();
		}
		return ret;
	}
	
	//Removes all simple tautologies; 
	public boolean removeTaut(){
		boolean ret = false;
		for(int ct = 0;ct<childNodes.size();ct++){
			if(isTautology(ct)==true){
				childNodes.remove(ct);
				ct--;
				ret = true;
				continue;
			}
			boolean temp = getNode(ct).removeTaut();
			ret = ret || temp;
		}
		return ret;
	}
	
	//creates two graphs... one with c, another with ~c, or (c).  Adds to the current one 'c', and to the new one '(c)'
	public ExistNode split(char c){
		ExistNode ret = new ExistNode(toString());
		formNode(""+c+"",this);
		ret.assumptions = new TreeSet(assumptions);
		ret.formNode("("+c+")",ret);
		assumptions.add(""+c+"");
		ret.assumptions.add("("+c+")");
		return ret;
	}
	
	//creates two graphs.  If one starts with (ab)c, then one will have (a)c and the other will have (b)c.
	//Returns null if no subgraphs are able to take it on.
	public ExistNode orSplit (){
		for (int ct=0;ct<childNodes.size();ct++){
			if(orSplittable()){
				ExistNode ret = new ExistNode(toString());
				if(getNode(ct).getVarSize()>=1){
					Character c = getNode(ct).getVar(0);
					getNode(ct).childVars.remove(c);
					ret.getNode(ct).childVars.clear();
					ret.getNode(ct).childNodes.clear();
					ret.getNode(ct).childVars.add(c);
					return ret;
				}
				else  //node.childVars must have >=2 nodes.
				{
					ExistNode n = getNode(ct).getNode(0);
					getNode(ct).childNodes.remove(n);
					ret.getNode(ct).childVars.clear();
					ret.getNode(ct).childNodes.clear();
					ret.getNode(ct).formNode("("+n.toString()+")",ret);
					return ret;
				}
			}
		}
		return null;
	}
	
	//returns true if graph can be or-splitted
	public boolean orSplittable(){
		for (int ct=0;ct<childNodes.size();ct++){
			if((getNode(ct).getNodeSize()+getNode(ct).getVarSize()) >=2)
				return true;
		}
		return false;
	}
	
	//returns the first character that is used by the graph, but not in it's assumptionlist
	public char getNewChar(){
		String base = toString();
		for(int ct=0;ct<base.length();ct++){
			char temp = base.charAt(ct);
			Character tempCh = new Character(temp);
			if(temp != ')' && temp != '('){
				if((assumptions.contains(""+temp) != true) && (assumptions.contains("("+temp+")") != true))
					if(childVars.contains(tempCh) != true){
						boolean negCheck = false;
						for(int ct2 =0; ct2<childNodes.size();ct2++){
							if(isNegetion(ct2)){
								negCheck = negCheck || (tempCh.equals((Character)getNode(ct2).childVars.get(0)));
							}
						}
						if(negCheck == false)
							return temp;
					}
			}
		}
		return '\0';
	}
	
	//deiterates all nodes equal to the node at nodeID
	public boolean deiterateNodeAll(int nodeID){
		boolean ret = false;
		boolean temp;
		//if (nodeID >= childNodes.size())
		//	return false;
		ExistNode n = new ExistNode();
		n.formNode(getNode(nodeID).toString(),this);
		for(int ct=0;ct<childNodes.size();ct++){
			if (ct != nodeID)
			{
				temp = getNode(ct).deiterateNode(n);
				ret = ret || temp;
			}
		}
		int ct2=0;
		while(childNodes.remove(n))
			ct2++;
		if(ct2>1)
			ret = true;
		if(ct2>0)
			childNodes.insertElementAt(n,nodeID);
		return ret;		
	}
	
	//removes all nodes equal to node.  Subroutine.
	private boolean deiterateNode(ExistNode node){
		boolean ret = false;
		boolean temp;
		for(int ct=0;ct<childNodes.size();ct++){
			temp = getNode(ct).deiterateNode(node);
			ret = ret || temp;
		}
		while(childNodes.remove(node))
			ret = true;
		return ret;
	}
	
	
	//deiterates all instances of the variable in varID location
	public boolean deiterateVarAll(int varID){
		boolean ret = false;
		if (varID >= childVars.size())
			return false;
		Character c = new Character(getVar(varID).charValue());
		for(int ct=0;ct<childNodes.size();ct++){
			boolean temp = getNode(ct).deiterateChar(c);
			ret = ret || temp;
		}
		int ct2=0;
		while(childVars.remove(c))
			ct2++;
		if(ct2>1)
			ret = true;
		if(ct2>0)
			childVars.insertElementAt(c,varID);
		return ret;
	}
	
	//Removes all instances of the variable c.  Subroutine.
	private boolean deiterateChar(Character c){
		boolean ret = false;
		for(int ct=0;ct<childNodes.size();ct++){
			boolean temp = getNode(ct).deiterateChar(c);
			ret = ret || temp;
		}
		while(childVars.remove(c))
			ret = true;
		return ret;
	}
	
	
	//returns the string representation of the node.
	public String toString(){
		String ret="";
		for(int Ct=0; Ct < childVars.size(); Ct++){
			ret+=((Character)childVars.get(Ct)).charValue();
		}
		for(int Ct2=0; Ct2 < childNodes.size(); Ct2++){
			ret+="("+childNodes.get(Ct2).toString()+")";
		}
		return ret;
	}
	
	//checks if current node is an ancestor to node test.
	public boolean isAncestor(ExistNode test){
		if (test.parent == null)
			return false;
		if (test.parent == this)
			return true;
		return isAncestor(test.parent);
	}
	
	//returns the node that is in the ID number position in the nodelist
	public ExistNode getNode(int node){
		return((ExistNode)childNodes.get(node));
	}
	
	//returns the var that is in the ID number position in the nodelist
	public Character getVar(int var){
		return((Character)childVars.get(var));
	}
	
	//returns the size of the var vector
	public int getVarSize(){
		return(childVars.size());
	}
	
	//returns the size of the node vector
	public int getNodeSize(){
		return(childNodes.size());
	}
	
	//returns the assumption list in string order
	public String getAssumptionList(){
		String ret = "";
		Iterator iter = assumptions.iterator();
		while(iter.hasNext()){
			ret += iter.next();
		}
		return ret;
	}
	
	
	//creates a node (and children nodes) based off of a string.
	//parentheses indicate cuts
	private String formNode(String info,ExistNode prev){
		parent = prev;
		String rest="";
		if (parent == null)
			level = 0;
		else
			level = parent.level + 1;
		while (info.length()>0)
		{
			char first = info.charAt(0);
			rest = info.substring(1);
			ExistNode temp;
			switch(first)
			{
				case '(':
				case '[':
				case '{':
					temp = new ExistNode();
					rest = temp.formNode(rest,this);
					childNodes.add(temp);
					break;
				case ')':
				case ']':
				case '}':
					return rest;
				case ' ':
					break;
				default:
					childVars.add(new Character(first));
			}
			info = new String(rest);
		}
		return rest;
	}
	
	Vector childNodes; //each has a seperate cut around it.
	Vector childVars;
	ExistNode parent;
	TreeSet assumptions;  //used in ATP, list of already used fork assumptions.  Set of strings
	int level;
}