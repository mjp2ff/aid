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

package edu.virginia.aid.visitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

/**
 * ASTVisitor for building up a ControlFlowGraph
 *
 * @author Anders Hessellund (modified by Matt Pearson-Beck & Jeff Principe)
 */
public class ControlFlowGraphVisitor extends ASTVisitor {
	
	/** statements in method */
	private Set<Statement> statements;
	/** statement -> Set of successors statements */
	private Map<Statement, Set<Statement>> relations;
	private Statement init, last;
	private Map<Statement, Set<Statement>> predecessors, successors;
	
	public ControlFlowGraphVisitor() {
		statements = new HashSet<Statement>();
		relations = new HashMap<Statement, Set<Statement>>();
		predecessors = new HashMap<Statement, Set<Statement>>();
		successors = new HashMap<Statement, Set<Statement>>();
	}
	
	public Statement getInit() {
		return init;
	}

	public Statement getLast() {
		return last;
	}

	public Map<Statement, Set<Statement>> getPredecessors() {
		return predecessors;
	}

	public Map<Statement, Set<Statement>> getSuccessors() {
		return successors;
	}

	@Override public boolean visit(MethodDeclaration node) {
        if (node.getBody() == null) {
            return false;
        }
		List<?> statements = node.getBody().statements();
		last = node.getBody(); // use method's body as virtual last
		if (statements.size() == 0) {
			init = last;
			return false; // empty body
		}
		init = (Statement) statements.get(0);
		addEdge(last(statements), last);
		return true;
	}
	
	@Override public boolean visit(Block node) {
        List<?> statements = node.statements();
        int size = statements.size();
        for (int i = 0; i < size - 1; i++) {
            Statement statement = (Statement) statements.get(i);
            // temporarily add edge for if-statement
            // addEdge doesn't really add if s is a return statement
            //   and its successor is not equal to last
            addEdge(statement, (Statement) statements.get(i + 1));
        }
        
        // could be done faster (this loop is different from the one above)
        if(isInTryBlock()) {
        	for (int i = 0; i < size ; i++) {
        		Statement statement = (Statement) statements.get(i);
        		if (!(statement instanceof TryStatement)) {
        			handleTryCatchFinally(statement);
        		}
            }	
        }
        
        return true;
    }

	/** find sequential next in case the statement is in a try-catch-block */
	private Statement findNextStatementInParentList(ASTNode node) {
		assert node != null;
		Statement next;
		ASTNode parent = node.getParent();
    	assert parent != null;
    	StructuralPropertyDescriptor location = node.getLocationInParent();
    	assert location != null;
    	if (location.isChildProperty()) {
    		next = findNextStatementInParentList(parent);
    	} else {
    		assert location.isChildListProperty();
    		List<?> l = (List<?>)parent.getStructuralProperty(location);
        	int index = l.indexOf(node);
        	if (index+1<l.size()) {
        		if (l.get(index+1) instanceof Statement) {
        			next = (Statement) l.get(index+1);
        		} else {
        			assert l.get(index+1) instanceof MethodDeclaration;
        			next = last;
        		}
        	} else {
        		next = findNextStatementInParentList(parent.getParent());
        	}
    	}
    	return next;
	}
	
    @Override public boolean visit(IfStatement node) {
        Set<Statement> set = getStatements(relations, node);
        Statement next;
        if (set.size() > 1) {
       		next = findNextStatementInParentList(node);
        } else if (set.size() == 1) {
            next = set.iterator().next();
        } else {
            return true;
        }
        assert next!=null;
        relations.remove(node); // remove temporary edge for if-statement
        // THEN
        if (!(node.getThenStatement() instanceof Block)) {
        	addEdge(node,node.getThenStatement());
        	addEdge(node.getThenStatement(),next);
        } else {
        	List<?> thenList = ((Block) node.getThenStatement()).statements();
            if (thenList.isEmpty()) {
                addEdge(node, next);
            } else {
                addEdge(node, first(thenList));
                addEdge(last(thenList), next);
            }
        }
        // ELSE
        if(node.getElseStatement()==null){
        	addEdge(node,next);
        	return true;
        }
        if (!(node.getElseStatement() instanceof Block)) {
        	addEdge(node,node.getElseStatement());
        	addEdge(node.getElseStatement(),next);
        } else {
        	List<?> elseList = ((Block) node.getElseStatement()).statements();
            if (elseList.isEmpty()) {
                addEdge(node, next);
            } else {
                addEdge(node, first(elseList));
                addEdge(last(elseList), next);
            }
        }            
        return true;
    }

    @Override public boolean visit(WhileStatement node) {
    	if (!(node.getBody() instanceof Block)) {
    		addEdge(node,node.getBody());
    		addEdge(node.getBody(),node);
    		return true;
    	}
        List<?> statements = ((Block) node.getBody()).statements();
        if (statements.isEmpty()) {
            addEdge(node, node);
        } else {
            addEdge(node, first(statements));
            addEdge(last(statements), node);
        }
        return true;
    }

    @Override public boolean visit(ReturnStatement node) {
        addEdge(node, last);
        return false;
    }

    @Override public boolean visit(DoStatement node) {
    	if (!(node.getBody() instanceof Block)) {
    		addEdge(node,node.getBody());
    		addEdge(node.getBody(),node);
    		return true;
    	}
        List<?> statements = ((Block) node.getBody()).statements();
        if (statements.isEmpty()) {
            addEdge(node, node);
        } else {
            addEdge(node, first(statements));
            addEdge(last(statements), node);
        }
        return true;
	}
    
	@Override public boolean visit(EnhancedForStatement node) {
    	if (!(node.getBody() instanceof Block)) {
    		addEdge(node,node.getBody());
    		addEdge(node.getBody(),node);
    		return true;
    	}
        List<?> statements = ((Block) node.getBody()).statements();
        if (statements.isEmpty()) {
            addEdge(node, node);
        } else {
            addEdge(node, first(statements));
            addEdge(last(statements), node);
        }
        return true;
	}
	
	@Override public boolean visit(ForStatement node) {
    	if (!(node.getBody() instanceof Block)) {
    		addEdge(node,node.getBody());
    		addEdge(node.getBody(),node);
    		return true;
    	}
        List<?> statements = ((Block) node.getBody()).statements();
        if (statements.isEmpty()) {
            addEdge(node, node);
        } else {
            addEdge(node, first(statements));
            addEdge(last(statements), node);
        }
        return true;
	}
	
	/** TODO: handle <i>continue</i>*/
	@Override public boolean visit(SwitchStatement node) {
		Set<Statement> set = getStatements(relations, node);
        assert set.size() == 1;
        
        // Hack fix to prevent errors on next() call.
        if (!set.iterator().hasNext()) return false;
       
        Statement next = set.iterator().next();
        
        // switch stmt can not be skipped if there is a default branch
        for(Object stmt : node.statements()) {
        	if (stmt instanceof SwitchCase) {
				if (((SwitchCase)stmt).getExpression()==null) {
					relations.remove(node);
				}
        	}
        }
        
		Statement previous = null;
		for(Object obj : node.statements()) {
			Statement stmt = (Statement) obj;
			if (previous!=null) {
				addEdge(previous,stmt);
			}
			if (stmt instanceof SwitchCase) {
				addEdge(node,stmt);
			}
			if (stmt instanceof BreakStatement) {
				addEdge(stmt,next);
				previous = null;
			} else {
				previous = stmt;
			}
		}
		addEdge(last(node.statements()),next);
		return true;
	}

	private List<TryStatement> tryStack = new ArrayList<TryStatement>();
	private Map<Statement,Statement> try2Successor = new HashMap<Statement, Statement>();
	
	@Override public boolean visit(TryStatement node) {
		// push on stack
		tryStack.add(node);
		// add edges to try-body, remove temporary node similar to IFs
		if (!node.getBody().statements().isEmpty()) {
			Set<Statement> set = getStatements(relations, node);
            if (set.size() > 0) {
                Statement next = set.iterator().next();
                // remember successors
                try2Successor.put(node, next);
                relations.remove(node);
                addEdge(node, first(node.getBody().statements()));
                // finally-block requires an extra indirection
                if (node.getFinally()!=null && !node.getFinally().statements().isEmpty()) {
                    addEdge(last(node.getBody().statements()),first(node.getFinally().statements()));
                    addEdge(last(node.getFinally().statements()),next);
                } else {
                    addEdge(last(node.getBody().statements()),next);
                }
            }
		}
		
		return true;
	}   
	
	@Override public boolean visit(CatchClause node) {
		// pop from stack when the first catch/finally is met
		if (isInTryBlock()) {
			TryStatement tryStmt = tryStack.get(tryStack.size()-1);
			if (tryStmt.catchClauses().size() > 0 && tryStmt.catchClauses().get(0).equals(node)) {
				tryStack.remove(tryStmt);
			}
		}
		return super.visit(node);
	}
	
	
	
	private void handleTryCatchFinally(Statement stmt) {
		// does this statement throw any checked exceptions?
		final Set<ITypeBinding> exceptions = new HashSet<ITypeBinding>();
		stmt.accept(new ASTVisitor(){
			@Override public boolean visit(MethodInvocation node) {
				IMethodBinding binding = node.resolveMethodBinding();
                if (binding != null) {
                    for(ITypeBinding itb : binding.getExceptionTypes()) {
                        if (isCheckedException(itb)) {
                            exceptions.add(itb);
                        }
                    }
                }
				return true;
			}
		});
		// if this statement does not throw any exceptions, 
		// no extra edges are necessary
		if (exceptions.size()==0) return;
		// move from top to bottom of stack and remove exceptions
		// as they are caught by catch clauses. Add edges on the way
		// while observing a ny intermediate finally-clauses
		int index = tryStack.size()-1;
		//List<Block> finallyBlocks = new ArrayList<Block>();
		while(index>=0) {
			TryStatement tryStmt = tryStack.get(index);
			for(int j = 0; j<tryStmt.catchClauses().size(); j++) {
				CatchClause clause = (CatchClause) tryStmt.catchClauses().get(j);
				SingleVariableDeclaration svd = clause.getException();
				ITypeBinding caught = svd.getType().resolveBinding();
				// ignore unchecked exceptions
				if (!isCheckedException(caught)) continue; 
				Set<ITypeBinding> caughtExceptions = new HashSet<ITypeBinding>();
				// iterate through thrown exceptions and 
				// check if they catch this exception
				for(ITypeBinding thrown : exceptions) {
					if (thrown.isSubTypeCompatible(caught)) {
						// note this exception, so it can be filtered out on next iteration
						caughtExceptions.add(thrown);
						// what succeeds this try-stmt
						Statement next = try2Successor.get(tryStmt);
						// add extra edges for intermediate finally-blocks
						Statement beginning = stmt;
						for(int k = tryStack.size()-1; k>index; k--) {
							TryStatement intermediate = tryStack.get(k);
							if (intermediate.getFinally()!=null && !intermediate.getFinally().statements().isEmpty()) {
								addEdge(beginning,first(intermediate.getFinally().statements()));
								beginning = last(intermediate.getFinally().statements());
							}
						}
						// add edge into catch clause
						if (!clause.getBody().statements().isEmpty()) {
							addEdge(beginning,first(clause.getBody().statements()));
							// add edge into finally clause
							if (tryStmt.getFinally()!= null && !tryStmt.getFinally().statements().isEmpty()) {
								// case 1: non-empty catch and non-empty finally
								addEdge(last(clause.getBody().statements()),first(tryStmt.getFinally().statements()));
								addEdge(last(tryStmt.getFinally().statements()),next);
							} else {
								// case 2: non-empty catch and empty finally
								addEdge(last(clause.getBody().statements()),next);
							}
						} else {
							// add edge into finally clause
							if (tryStmt.getFinally()!= null && !tryStmt.getFinally().statements().isEmpty()) {
								// case 3: empty catch and non-empty finally
								addEdge(beginning,first(tryStmt.getFinally().statements()));
								addEdge(last(tryStmt.getFinally().statements()),next);
							} else {
								// case 4: empty catch and empty finally
								addEdge(beginning,next);
							}
						}
						
						
					}
				}
				// remove exceptions as they are caught
				// cannot be done inside loop
				exceptions.removeAll(caughtExceptions);
			}
			index--;
		}
	}
	
	private boolean isInTryBlock() { return tryStack.size()>0; }
	
	private boolean isCheckedException(ITypeBinding binding) {
		while(binding != null) {
			if (binding.getQualifiedName().equals("java.lang.RuntimeException")) {
				return false;
			}
			if (binding.getQualifiedName().equals("java.lang.Exception")) {
				return true;
			}
			binding = binding.getSuperclass();
		}
		return false;
	}
    
    //-- end visit
    
	@Override public void endVisit(MethodDeclaration node) {
        Set<Statement> reachableSet = new HashSet<Statement>();
        computeSuccsPreds(reachableSet, init);
    }
    
    //-- utility methods

    /** recursively initialize the predecessor and successor fields */
    private void computeSuccsPreds(Set<Statement> set, Statement statement) {
        if (set.contains(statement)) { return; }
        set.add(statement);
        for (Statement successor : getStatements(relations, statement)) {
            getStatements(successors, statement).add(successor);
            getStatements(predecessors, successor).add(statement);
            computeSuccsPreds(set, successor);
        }
    }
	private Statement first(List<?> statements) {
		assert !statements.isEmpty();
		// Hack fix to prevent error on empty list.
		if (statements.size() == 0) return null;
		return (Statement) statements.get(0);
	}

	private Statement last(List<?> statements) {
		assert !statements.isEmpty();
		// Hack fix to prevent error on empty list.
		if (statements.size() == 0) return null;
		return (Statement) statements.get(statements.size() - 1);
	}
	/** get the set corresponding to the statement
	 *  and if no set exists return a new empty set */
	private Set<Statement> getStatements(
			Map<Statement, Set<Statement>> map, Statement statement) {
		Set<Statement> set = map.get(statement);
		if (set == null) {
			set = new HashSet<Statement>();
			map.put(statement, set);
		}
		return set;
	}
	
	private void addEdge(Statement s1, Statement s2) {
		if (s1 instanceof ReturnStatement && s2 != last) {
			return;
		}
		statements.add(s1);
		statements.add(s2);
		getStatements(relations, s1).add(s2);
	}
}