package org.Laboratory_3;

import org.Laboratory_1.Analyser;
import org.Laboratory_1.ClassOfLexeme;
import org.Laboratory_1.Lexeme;
import org.Laboratory_1.TypeOfLexeme;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SemanticSyntaxAnalyser {
    public static List<Lexeme> lexemes = new ArrayList<>();
    public static List<PostfixEntry> entries = new ArrayList<>();
    public static int index;
    public static int conditionPos;

    public static void main(String[] args) {
        Analyser.main(new String[]{});

        lexemes = Analyser.lexemes
                .stream()
                .sorted(Comparator.comparingInt(Lexeme::getPosition))
                .collect(Collectors.toList());

        index = 0;
        analyse();

        printEntryList();
    }

    public static void analyse(){
        while(lexemes.get(index).getTypeOfLexeme() != TypeOfLexeme.NEXT){
            if (!isForStatement()){
                return;
            }
        }
    }

    public static boolean isForStatement(){
        int indFirst = entries.size();

        if (!isForToStatement()) return false;

        int indJmp = writeCmdPtr(-1);
        writeCmd(ECmd.JZ);

        if (!isStatement()) return false;

        if (index >= lexemes.size()){
            int position = lexemes.get(index-1).getPosition() + lexemes.get(index-1).getValue().length() + 1;
            printError("Ключевое слово next ожидалось в позиции " + position);
            return false;
        }

        if (lexemes.get(index).getTypeOfLexeme() != TypeOfLexeme.NEXT){
            printError("Ключевое слово next ожидалось в позиции " +lexemes.get(index).getPosition());
            return false;
        }

        writeCmdPtr(conditionPos);
        int indLast = writeCmd(ECmd.JMP);
        setCmdPtr(indJmp, indLast+1);

        return true;
    }

    public static boolean isForToStatement(){
        if (index >= lexemes.size() || lexemes.get(index).getTypeOfLexeme() != TypeOfLexeme.FOR){
            printError("Ключевое слово for ожидалось в позиции " +lexemes.get(index).getPosition());
            return false;
        }

        index++;

        int idPos = index;
        if (!isStatement()) return false;

        if (index >= lexemes.size() || lexemes.get(index).getTypeOfLexeme() != TypeOfLexeme.TO){
            printError("Ключевое слово to ожидалось в позиции " +lexemes.get(index).getPosition());
            return false;
        }

        writeVar(idPos);
        conditionPos = entries.size() - 1;

        index++;
        if (!isArithmExpr()) return false;

        writeCmd(ECmd.CMPL);

        return true;
    }

    public static boolean isCondition(){
        if (!isLogExpression()) return false;

        if (index >= lexemes.size())
            return true;

        while (lexemes.get(index) != null && lexemes.get(index).getTypeOfLexeme() == TypeOfLexeme.OR){
            index++;
            if (!isLogExpression()) return false;
            writeCmd(ECmd.OR);
        }
        return true;
    }

    public static boolean isLogExpression(){
        if (!isRelExpression()) return false;

        if (index >= lexemes.size())
            return true;

        while (lexemes.get(index) != null && lexemes.get(index).getTypeOfLexeme() == TypeOfLexeme.AND){
            index++;
            if (!isRelExpression())return false;
            writeCmd(ECmd.AND);
        }

        return true;
    }

    public static boolean isRelExpression(){
        if (!isOperand()) return false;

        if (index >= lexemes.size()){
            printError("Оператор сравнения ожидался в позиции " +lexemes.get(index).getPosition());
            return false;
        }

        if (lexemes.get(index).getTypeOfLexeme() == TypeOfLexeme.COMPARISON){
            ECmd cmd = null;
            String val = lexemes.get(index).getValue();
            if (val.equals("<")) cmd = ECmd.CMPL;
            else if (val.equals("<=")) cmd = ECmd.CMPLE;
            else if (val.equals("<>")) cmd = ECmd.CMPNE;
            else if (val.equals("==")) cmd = ECmd.CMPE;
            else if (val.equals(">")) cmd = ECmd.CMPG;
            else if (val.equals("=>")) cmd = ECmd.CMPGE;
            index++;
            if (!isOperand()) return false;
            writeCmd(cmd);
        }
        return true;
    }

    public static boolean isOperand(){
        if (index >= lexemes.size()){
            return false;
        }
        if (lexemes.get(index).getClassOfLexeme() != ClassOfLexeme.CONSTANT
                && lexemes.get(index).getClassOfLexeme() != ClassOfLexeme.IDENTIFIER){
            printError("Переменная или константа ожидалась в позиции " +lexemes.get(index).getPosition());
            return false;
        }

        if (lexemes.get(index).getClassOfLexeme() == ClassOfLexeme.IDENTIFIER)
            writeVar(index);
        else
            writeConst(index);
        index++;
        return true;
    }

    public static boolean isLogicalOp(){
        if (lexemes.get(index).getTypeOfLexeme() != TypeOfLexeme.AND
                && lexemes.get(index).getTypeOfLexeme() != TypeOfLexeme.OR){
            printError("Логическая операция ожидалась в позиции " +lexemes.get(index).getPosition());
            return false;
        }

        return true;
    }

    public static boolean isStatement(){
        if (lexemes.get(index).getClassOfLexeme() != ClassOfLexeme.IDENTIFIER){
            printError("Идентификатор ожидался в позиции " +lexemes.get(index).getPosition());
            return false;
        }
        writeVar(index);

        index++;
        if (index >= lexemes.size() || lexemes.get(index).getTypeOfLexeme() != TypeOfLexeme.ASSIGNMENT){
            printError("Присваивание ожидалось в позиции " +lexemes.get(index).getPosition());
            return false;
        }

        index++;
        if (!isArithmExpr()) return false;
        writeCmd(ECmd.SET);

        return true;
    }

    public static boolean isArithmExpr(){
        if (!isOperand()) return false;

        while (lexemes.get(index).getTypeOfLexeme() == TypeOfLexeme.ARITHMETIC){
            ECmd cmd = null;
            String val = lexemes.get(index).getValue();
            if (val.equals("+"))  cmd = ECmd.ADD;
            else if (val.equals("-")) cmd = ECmd.SUB;
            index++;
            if (!isOperand()) return false;
            writeCmd(cmd);
        }

        return true;
    }

    public static void printError(String message){
        System.out.println("<!> Ошибка: " + message + " <!>");
    }

    private static int writeCmd(ECmd cmd){
        PostfixEntry entry = new PostfixEntry();
        entry.setType(EEntryType.CMD);
        entry.setCmd(cmd);
        entries.add(entry);
        return entries.size()-1;
    }

    private static int writeVar(int index){
        PostfixEntry variable = new PostfixEntry();
        variable.setType(EEntryType.VAR);
        variable.setValue(lexemes.get(index).getValue());
        entries.add(variable);
        return entries.size()-1;
    }

    private static int writeConst(int index){
        PostfixEntry con = new PostfixEntry();
        con.setType(EEntryType.CONST);
        con.setValue(lexemes.get(index).getValue());
        entries.add(con);
        return entries.size()-1;
    }

    private static int writeCmdPtr(int ptr){
        PostfixEntry cmdPtr = new PostfixEntry();
        cmdPtr.setType(EEntryType.CMDPTR);
        cmdPtr.setCmdPtr(ptr);
        entries.add(cmdPtr);
        return entries.size()-1;
    }

    private static void setCmdPtr(int index, int ptr){
        entries.get(index).setCmdPtr(ptr);
    }

    private static void printEntryList(){
        System.out.println("ПОЛИЗ:");
        for (PostfixEntry entry : entries) {
            System.out.print(entry + " ");
        }
    }
}
