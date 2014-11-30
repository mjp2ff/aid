/**
 * Copyright 2008 Anders Hessellund 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0 
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * $Id: ControlFlowGraph.java,v 1.1 2008/01/17 18:48:18 hessellund Exp $
 *
 * Modified by Matt Pearson-Beck and Jeff Principe, 2014
 */

package edu.virginia.aid.util;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;

import edu.virginia.aid.visitors.ControlFlowGraphVisitor;

class ControlFlowGraph {
	/** the method which we build the control flow graph for */
	private final MethodDeclaration method;
	/** maps a {@link Statement} to its predecessors */
	private final Map<Statement, Set<Statement>> predecessors;
	/** maps a {@link Statement} to its successors */
	private final Map<Statement, Set<Statement>> successors;
	/**
	 * Holds the start {@link Statement} of the CFG. If the method body is empty
	 * then, the start {@link Statement} is equal to the end {@link Statement}.
	 */
	private final Statement start;
	/**
	 * Holds the end {@link Statement} of the CFG. The end {@link Statement} is
	 * always the method block body.
	 */
	private final Statement end;

	ControlFlowGraph(MethodDeclaration method) {
		this.method = method;
		ControlFlowGraphVisitor controlFlowVisitor = new ControlFlowGraphVisitor();
		method.accept(controlFlowVisitor);
		this.predecessors = controlFlowVisitor.getPredecessors();
		this.successors = controlFlowVisitor.getSuccessors();
		this.start = controlFlowVisitor.getInit();
		this.end = controlFlowVisitor.getLast();
	}
	
	@Override public String toString() {
		StringBuilder sb = new StringBuilder();
		TreeSet<Statement> sorted = 
			new TreeSet<Statement>(new Comparator<Statement>(){
			public int compare(Statement o1, Statement o2) {
				if (o1.getStartPosition()>o2.getStartPosition()) 
					return 1;
				return -1;
			}});
		sorted.addAll(successors.keySet());
		for (Statement statement : sorted) {
			sb.append("Statement: "+ getFirstLine(statement)+"\n");
			for (Statement succ : successors.get(statement)) {
				sb.append("\t"+(succ==end?"virtual last\n": getFirstLine(succ)+"\n"));
			}
		}
		return sb.toString();
	}
	
	/**
	 * Returns the first line of an object's {@link String} representation
	 * @param o The object.
	 * @return The first line of the given object's {@link String} representation
	 */
	public static String getFirstLine(Object o) {
		assert o != null;
		String nText = o.toString();
		int index = nText.indexOf('\n');
		String result = index >= 0 ? nText.substring(0, index) : nText;
		return result;
	}
}