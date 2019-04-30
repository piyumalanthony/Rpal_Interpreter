package interpreter;

import java.util.ArrayList;

import nodeObjects.*;

public class CseMachine {
    private ArrayList<TreeNodeObject> controlArray;
    private ArrayList<TreeNodeObject> stackArray;
    private ArrayList<Environment> environmentArray;

    public CseMachine(ArrayList<TreeNodeObject> control, ArrayList<TreeNodeObject> stack, ArrayList<Environment> environment) {
        this.setControl(control);
        this.setStack(stack);
        this.setEnvironment(environment);
    }  
    
    public void setControl(ArrayList<TreeNodeObject> control) {
        this.controlArray = control;
    }
    
    public void setStack(ArrayList<TreeNodeObject> stack) {
        this.stackArray = stack;
    }
    
    public void setEnvironment(ArrayList<Environment> environment) {
        this.environmentArray = environment;
    }
    
    public void compile() {
        Environment envVariable = this.environmentArray.get(0);
        int j = 1;
        while (!controlArray.isEmpty()) {
           
            TreeNodeObject nodeObjectRunTime = controlArray.get(controlArray.size()-1);
            controlArray.remove(controlArray.size()-1);   
            
            //the following instruction for implementing cse machine rules 1 to 13
            
            
            // rule 1
            if (nodeObjectRunTime instanceof Id) {
                this.stackArray.add(0, envVariable.fetchData((Id) nodeObjectRunTime));
            // rule2
            } else if (nodeObjectRunTime instanceof LambdaNode) {
                LambdaNode lambdaNode= (LambdaNode) nodeObjectRunTime;
                lambdaNode.setEnvironment(envVariable.getIndex());
                this.stackArray.add(0, lambdaNode);
            // rule 3,4,10-13
            } else if (nodeObjectRunTime instanceof GammaNode) {
                TreeNodeObject nextNode = this.stackArray.get(0);
                this.stackArray.remove(0);
                // rules related to lambdas
                if (nextNode instanceof LambdaNode) {
                    LambdaNode lambda = (LambdaNode) nextNode;
                    Environment env = new Environment(j++);
                    if (lambda.identifiers.size() == 1) {
                        env.values.put(lambda.identifiers.get(0), this.stackArray.get(0));
                        this.stackArray.remove(0);
                    } else {
                        Tuple tup = (Tuple) this.stackArray.get(0);
                        this.stackArray.remove(0);
                        int i = 0;
                        for (Id id: lambda.identifiers) {
                            env.values.put(id, tup.nodeOjects.get(i++));
                        }
                    }
                    for (Environment environment: this.environmentArray) {
                        if (environment.getIndex() == lambda.getEnvironment()) {
                            env.setParentenv(environment);
                        }
                    }        
                    envVariable = env;
                    this.controlArray.add(env);
                    this.controlArray.add(lambda.getDelta());
                    this.stackArray.add(0, env);
                    this.environmentArray.add(env);
                // rule 10
                } else if (nextNode instanceof Tuple) {
                    Tuple tup = (Tuple) nextNode;
                    int i = Integer.parseInt(this.stackArray.get(0).getInfo());
                    this.stackArray.remove(0);
                    this.stackArray.add(0, tup.nodeOjects.get(i-1));
                // rule 12
                } else if (nextNode instanceof Ystar) {
                    LambdaNode lambda = (LambdaNode) this.stackArray.get(0);
                    this.stackArray.remove(0);
                    Eta eta = new Eta();
                    eta.setIndex(lambda.getIndex());
                    eta.setEnvironment(lambda.getEnvironment());
                    eta.setIdentifier(lambda.identifiers.get(0));
                    eta.setLambda(lambda);
                    this.stackArray.add(0, eta);
                // rule 13
                } else if (nextNode instanceof Eta) {
                    Eta eta = (Eta) nextNode;
                    LambdaNode lambda = eta.getLambda();
                    this.controlArray.add(new GammaNode());
                    this.controlArray.add(new GammaNode());
                    this.stackArray.add(0, eta);
                    this.stackArray.add(0, lambda);
                //Rpal built in functions
                } else {
                    if ("Print".equals(nextNode.getInfo())) {
                    } else if ("Stem".equals(nextNode.getInfo())) {
                        TreeNodeObject s = this.stackArray.get(0);
                        this.stackArray.remove(0);
                        s.setData(s.getInfo().substring(0, 1));
                        this.stackArray.add(0, s);
                    } else if ("Stern".equals(nextNode.getInfo())) {
                        TreeNodeObject s = this.stackArray.get(0);
                        this.stackArray.remove(0);
                        s.setData(s.getInfo().substring(1));
                        this.stackArray.add(0, s);
                    } else if ("Conc".equals(nextNode.getInfo())) {
                        TreeNodeObject s1 = this.stackArray.get(0);
                        TreeNodeObject s2 = this.stackArray.get(1);
                        this.stackArray.remove(0);
                        this.stackArray.remove(0);
                        s1.setData(s1.getInfo() + s2.getInfo());
                        this.stackArray.add(0, s1);
                    } else if ("Isstring".equals(nextNode.getInfo())) {
                        this.stackArray.remove(0);
                        BooleanObjects b = new BooleanObjects("true");
                        this.stackArray.add(0, b);
                    } else if ("Order".equals(nextNode.getInfo())) {
                        Tuple tup = (Tuple) this.stackArray.get(0);
                        this.stackArray.remove(0);
                        IntegerObject n = new IntegerObject(Integer.toString(tup.nodeOjects.size()));
                        this.stackArray.add(0, n);
                    }
                }
            // rule 5
            } else if (nodeObjectRunTime instanceof Environment) {                 
                this.stackArray.remove(1);
                this.environmentArray.get(((Environment) nodeObjectRunTime).getIndex()).setIsRemoved(true);
                int y = this.environmentArray.size();
                while (y > 0) {
                    if (!this.environmentArray.get(y-1).checkIsRemoved()) {
                        envVariable = this.environmentArray.get(y-1);
                        break;
                    } else {
                        y--;
                    }
                }
            // rule 6 and 7
            } else if (nodeObjectRunTime instanceof Operator) {
                if (nodeObjectRunTime instanceof UnaryOperator) {
                    TreeNodeObject rator = nodeObjectRunTime;
                    TreeNodeObject rand = this.stackArray.get(0);
                    this.stackArray.remove(0);
                    stackArray.add(0, this.applyUnaryOperation(rator, rand));
                }
                if (nodeObjectRunTime instanceof BinaryOperator) {
                    TreeNodeObject rator = nodeObjectRunTime;
                    TreeNodeObject rand1 = this.stackArray.get(0);
                    TreeNodeObject rand2 = this.stackArray.get(1);
                    this.stackArray.remove(0);
                    this.stackArray.remove(0);
                    this.stackArray.add(0, this.applyBinaryOperation(rator, rand1, rand2));
                }
            // rule 8
            } else if (nodeObjectRunTime instanceof Beta) {
                if (Boolean.parseBoolean(this.stackArray.get(0).getInfo())) {
                    this.controlArray.remove(controlArray.size()-1); 
                } else {
                    this.controlArray.remove(controlArray.size()-2); 
                }
                this.stackArray.remove(0);
            // rule9
            } else if (nodeObjectRunTime instanceof Tau) {
                Tau tau = (Tau) nodeObjectRunTime;
                Tuple tup = new Tuple();
                for (int i = 0; i < tau.getN(); i++) {
                    tup.nodeOjects.add(this.stackArray.get(0));
                    this.stackArray.remove(0);
                }
                this.stackArray.add(0, tup);
            } else if (nodeObjectRunTime instanceof Delta) {
                this.controlArray.addAll(((Delta) nodeObjectRunTime).nodeObjects);
            } else if (nodeObjectRunTime instanceof B) {
                this.controlArray.addAll(((B) nodeObjectRunTime).nodeObjects);
            } else {
                this.stackArray.add(0, nodeObjectRunTime);
            }
        }   
    }
    
    public void printControl() {
        System.out.print("Control: ");
        for (TreeNodeObject symbol: this.controlArray) {
            System.out.print(symbol.getInfo());
            if (symbol instanceof LambdaNode) {
                System.out.print(((LambdaNode) symbol).getIndex());
            } else if (symbol instanceof Delta) {
                System.out.print(((Delta) symbol).getIndex());
            } else if (symbol instanceof Environment) {
                System.out.print(((Environment) symbol).getIndex());
            } else if (symbol instanceof Eta) {
                System.out.print(((Eta) symbol).getIndex());
            }
            System.out.print(",");
        }
        System.out.println();
    }
    //to print stack
    public void printStack() {
        System.out.print("Stack: ");
        for (TreeNodeObject symbol: this.stackArray) {
            System.out.print(symbol.getInfo());
            if (symbol instanceof LambdaNode) {
                System.out.print(((LambdaNode) symbol).getIndex());
            } else if (symbol instanceof Delta) {
                System.out.print(((Delta) symbol).getIndex());
            } else if (symbol instanceof Environment) {
                System.out.print(((Environment) symbol).getIndex());
            } else if (symbol instanceof Eta) {
                System.out.print(((Eta) symbol).getIndex());
            }
            System.out.print(",");
        }
        System.out.println();
    }
    //to print the environmnet array
    public void printEnvironment() {
        for (TreeNodeObject nodeObject: this.environmentArray) {
            System.out.print("e"+((Environment) nodeObject).getIndex()+ " --> ");
            if (((Environment) nodeObject).getIndex()!=0) {
                System.out.println("env"+((Environment) nodeObject).getParentenv().getIndex());
            } else {
                System.out.println();
            }
        }
    }
    //to apply unary operation
    public TreeNodeObject applyUnaryOperation(TreeNodeObject operator, TreeNodeObject operand) {
        if ("neg".equals(operator.getInfo())) {
            int val = Integer.parseInt(operand.getInfo());
            return new IntegerObject(Integer.toString(-1*val));
        } else if ("not".equals(operator.getInfo())) {
            boolean val = Boolean.parseBoolean(operand.getInfo());
            return new BooleanObjects(Boolean.toString(!val));
        } else {
            return new Err();
        }
    }
    
    public TreeNodeObject applyBinaryOperation(TreeNodeObject Operator, TreeNodeObject firstOperand, TreeNodeObject secondOperand) {
        if ("+".equals(Operator.getInfo())) {
            int firstValue = Integer.parseInt(firstOperand.getInfo());
            int SecondValue = Integer.parseInt(secondOperand.getInfo());
            return new IntegerObject(Integer.toString(firstValue+SecondValue));
        } else if ("-".equals(Operator.getInfo())) {
            int firstValue = Integer.parseInt(firstOperand.getInfo());
            int secondValue = Integer.parseInt(secondOperand.getInfo());
            return new IntegerObject(Integer.toString(firstValue-secondValue));
        } else if ("*".equals(Operator.getInfo())) {
            int firstValue = Integer.parseInt(firstOperand.getInfo());
            int secondValue = Integer.parseInt(secondOperand.getInfo());
            return new IntegerObject(Integer.toString(firstValue*secondValue));
        } else if ("/".equals(Operator.getInfo())) {
            int firstValue = Integer.parseInt(firstOperand.getInfo());
            int secondValue = Integer.parseInt(secondOperand.getInfo());
            return new IntegerObject(Integer.toString(firstValue/secondValue));
        } else if ("**".equals(Operator.getInfo())) {
            int firstValue = Integer.parseInt(firstOperand.getInfo());
            int secondValue = Integer.parseInt(secondOperand.getInfo());
            return new IntegerObject(Integer.toString((int) Math.pow(firstValue, secondValue)));
        } else if ("&".equals(Operator.getInfo())) {            
            boolean firstValue = Boolean.parseBoolean(firstOperand.getInfo());
            boolean secondvalue = Boolean.parseBoolean(secondOperand.getInfo());
            return new BooleanObjects(Boolean.toString(firstValue && secondvalue));
        } else if ("or".equals(Operator.getInfo())) {            
            boolean firstValue = Boolean.parseBoolean(firstOperand.getInfo());
            boolean secondValue = Boolean.parseBoolean(secondOperand.getInfo());
            return new BooleanObjects(Boolean.toString(firstValue || secondValue));
        } else if ("eq".equals(Operator.getInfo())) {            
            String firstValue = firstOperand.getInfo();
            String secondValue = secondOperand.getInfo();
            return new BooleanObjects(Boolean.toString(firstValue.equals(secondValue)));
        } else if ("ne".equals(Operator.getInfo())) {            
            String firstValue = firstOperand.getInfo();
            String secondValue = secondOperand.getInfo();
            return new BooleanObjects(Boolean.toString(!firstValue.equals(secondValue)));
        } else if ("ls".equals(Operator.getInfo())) {            
            int firstValue = Integer.parseInt(firstOperand.getInfo());
            int secondValue = Integer.parseInt(secondOperand.getInfo());
            return new BooleanObjects(Boolean.toString(firstValue < secondValue));
        } else if ("le".equals(Operator.getInfo())) {            
            int firstValue = Integer.parseInt(firstOperand.getInfo());
            int secondValue = Integer.parseInt(secondOperand.getInfo());
            return new BooleanObjects(Boolean.toString(firstValue <= secondValue));
        } else if ("gr".equals(Operator.getInfo())) {            
            int firstValue = Integer.parseInt(firstOperand.getInfo());
            int secondValue = Integer.parseInt(secondOperand.getInfo());
            return new BooleanObjects(Boolean.toString(firstValue > secondValue));
        } else if ("ge".equals(Operator.getInfo())) {            
            int firstValue = Integer.parseInt(firstOperand.getInfo());
            int secondValue = Integer.parseInt(secondOperand.getInfo());
            return new BooleanObjects(Boolean.toString(firstValue >= secondValue));
        } else if ("aug".equals(Operator.getInfo())) {  
            if (secondOperand instanceof Tuple) {
                ((Tuple) firstOperand).nodeOjects.addAll(((Tuple) secondOperand).nodeOjects);
            } else {
                ((Tuple) firstOperand).nodeOjects.add(secondOperand);
            }
            return firstOperand;
        } else {
            return new Err();
        }
    }
    
    public String getTupleValue(Tuple tuple) {
        String var1 = "(";
        for (TreeNodeObject symbol: tuple.nodeOjects) {
            if (symbol instanceof Tuple) {
                var1 = var1 + this.getTupleValue((Tuple) symbol) + ", ";
            } else {
                var1 = var1 + symbol.getInfo() + ", ";
            }            
        }
        var1 = var1.substring(0, var1.length()-2) + ")";
        return var1;
    }
    
    public String getOutput() {
        this.compile();
        if (stackArray.get(0) instanceof Tuple) {
            return this.getTupleValue((Tuple) stackArray.get(0));
        }
        return stackArray.get(0).getInfo();
    }
}
