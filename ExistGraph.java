import java.util.*;
import java.io.*;


public class ExistGraph{
	public static void main(String[] args) throws IOException{
		inputTokenizer = new StreamTokenizer(new BufferedReader(new InputStreamReader(System.in)));
    	inputTokenizer.wordChars(' ','\127');
    	inputTokenizer.wordChars('[','[');
    	inputTokenizer.wordChars(']',']');
    	System.out.print("Input graph:");
    	inputTokenizer.nextToken();
    	String graph = inputTokenizer.sval;
    	System.out.print("show all steps?");
    	inputTokenizer.lowerCaseMode(true);
    	inputTokenizer.nextToken();
    	String answer = inputTokenizer.sval;
    	decompose(graph,answer.charAt(0));
	}
	
	public static void decompose(String str,char ans){
		ExistNode q,tempNode;
		q = new ExistNode(str);
		System.out.println("New             :"+q.toString());
		boolean test;  
		boolean temp;
		Stack s = new Stack();
		ExistNode cur = q;
		int ct =0;
		int splitCt=0;
		do{
			do{
				test = false;
				temp = q.doubleCutRemoveAll();
				test = test || temp;
				if (temp == true && ans == 'y'){
					System.out.println("Step-Doublecut  :"+q.toString()+"/"+q.getAssumptionList());
				}
				temp = q.deiterateAll();
				test = test || temp;
				if (temp == true && ans == 'y'){
					System.out.println("Step-Deiteration:"+q.toString()+"/"+q.getAssumptionList());
				}
				temp = q.removeTaut();
				test = test || temp;
				if (temp == true && ans == 'y'){
					System.out.println("Step-Tautology  :"+q.toString()+"/"+q.getAssumptionList());
				}
			}while (test == true);
			char ch = q.getNewChar();
			if(q.isContradiction() == false){
				if(ch != '\0'){
					s.push(q.split(ch));
					test = true;
					if (ans == 'y')
						System.out.println("Step-Var Split  :"+q.toString()+"/"+q.getAssumptionList());
					splitCt++;
					}
				if(test == false && q.orSplittable() == true){
					s.push(q.orSplit());
					test = true;
					if (ans == 'y')
						System.out.println("Step-OR Split   :"+q.toString()+"/"+q.getAssumptionList());
					splitCt++;
				}
				if(test == false){
					System.out.println("Step-Open Branch:"+q.toString()+"/"+q.getAssumptionList());
					if(s.empty() == false){
						test = true;
						q = (ExistNode)s.pop();
						splitCt--;
						if (ans == 'y')
							System.out.println("Step-Next Option:"+q.toString()+"/"+q.getAssumptionList());
					}
				}
			}
			else{
				System.out.println("Step-CloseBranch:"+q.toString()+"/"+q.getAssumptionList());
				if(s.empty() == false){
					test = true;
					q = (ExistNode)s.pop();
					splitCt--;
					if (ans == 'y')
						System.out.println("Step-Next Option:"+q.toString()+"/"+q.getAssumptionList());
				}
			}
		}while(test);
	}
	
	static StreamTokenizer inputTokenizer;
}