package org.Laboratory_4;

import org.Laboratory_3.ECmd;
import org.Laboratory_3.EEntryType;
import org.Laboratory_3.PostfixEntry;
import org.Laboratory_3.SemanticSyntaxAnalyser;

import java.util.*;
import java.util.stream.Collectors;

public class Interpreter {
    private static final List<PostfixEntry> entryList = new ArrayList<>();
    private static final Deque<PostfixEntry> stack = new LinkedList<>();
    private static final Map<String, Integer> varValues = new HashMap<>();

    public static void main(String[] args) {
        SemanticSyntaxAnalyser.main(args);

        entryList.addAll(SemanticSyntaxAnalyser.entries);
        System.out.println();
        setVarValues();

        interpret();
        printValues();
    }

    private static void setVarValues(){
        Set<String> types = entryList.stream()
                .filter(value -> value.getType() == EEntryType.VAR)
                .map(PostfixEntry::getValue)
                .collect(Collectors.toSet());

        Scanner sc = new Scanner(System.in);
        types.forEach(var -> {
            System.out.printf("Введите начальное значение %s: ", var);
            int value = Integer.parseInt(sc.nextLine());
            varValues.put(var, value);
        });
    }

    private static void interpret(){
        int tmp;
        int pos = 0;
        int postfixPos = entryList.size();
        while (pos < postfixPos){
            if (entryList.get(pos).getType() == EEntryType.CMD){
                ECmd cmd = entryList.get(pos).getCmd();
                switch (cmd){
                    case JMP: {
                        pos = popPtrVal();
                        break;
                    }
                    case JZ: {
                        tmp = popPtrVal();
                        if (popVal() == 1)
                            pos++;
                        else pos = tmp;
                        break;
                    }
                    case SET: {
                        setVarAndPop(popVal());
                        pos++;
                        break;
                    }
                    case ADD: {
                        pushVal(popVal() + popVal());
                        pos++;
                        break;
                    }
                    case SUB: {
                        pushVal(-popVal() + popVal());
                        pos++;
                        break;
                    }
                    case AND: {
                        pushVal(popVal() & popVal());
                        pos++;
                        break;
                    }
                    case OR: {
                        pushVal(popVal() | popVal());
                        pos++;
                        break;
                    }
                    case CMPE: {
                        if (popVal() == popVal())
                            pushVal(1);
                        else pushVal(-1);
                        pos++;
                        break;
                    }
                    case CMPNE: {
                        if (popVal() != popVal())
                            pushVal(1);
                        else pushVal(-1);
                        pos++;
                        break;
                    }
                    case CMPL: {
                        if (popVal() > popVal())
                            pushVal(1);
                        else pushVal(-1);
                        pos++;
                        break;
                    }
                    case CMPLE: {
                        if (popVal() >= popVal())
                            pushVal(1);
                        else pushVal(-1);
                        pos++;
                        break;
                    }
                    case CMPG: {
                        if (popVal() < popVal())
                            pushVal(1);
                        else pushVal(-1);
                        pos++;
                        break;
                    }
                    case CMPGE: {
                        if (popVal() <= popVal())
                            pushVal(1);
                        else pushVal(-1);
                        pos++;
                        break;
                    }
                }
            }
            else pushElm(entryList.get(pos++));
        }
    }

    private static int popVal(){
        String value = stack.pop().getValue();
        try{
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e){
            return varValues.get(value);
        }
    }

    private static int popPtrVal(){
        return stack.pop().getCmdPtr();
    }

    private static void pushVal(int val){
        PostfixEntry entry = new PostfixEntry();
        entry.setValue(String.valueOf(val));
        stack.push(entry);
    }

    private static void pushElm(PostfixEntry entry){
        stack.push(entry);
    }

    private static void setVarAndPop(int val){
        String varName = stack.peek().getValue();
        varValues.put(varName, val);
        stack.pop();
    }

    private static void printValues() {
        varValues.forEach((var, value) -> {
            System.out.println(var + " = " + value);
        });
    }
}
