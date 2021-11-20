package org.Laboratory_2;

import org.Laboratory_1.Analyser;
import org.Laboratory_1.ClassOfLexeme;
import org.Laboratory_1.Lexeme;
import org.Laboratory_1.TypeOfLexeme;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SyntaxAnalyser {
    public static List<Lexeme> lexemes = new ArrayList<>();
    public static int index;

    public static void main(String[] args) {
        Analyser.main(new String[]{});

        lexemes = Analyser.lexemes
                .stream()
                .sorted(Comparator.comparingInt(Lexeme::getPosition))
                .collect(Collectors.toList());

        index = 0;
        analyse();
    }

    public static void analyse(){
        while(lexemes.get(index).getTypeOfLexeme() != TypeOfLexeme.NEXT){
            if (!isForStatement()){
                return;
            }
        }
    }

    public static boolean isForStatement(){
        if (index >= lexemes.size() || lexemes.get(index).getTypeOfLexeme() != TypeOfLexeme.FOR){
            printError("Ключевое слово for ожидалось в позиции " +lexemes.get(index).getPosition());
            return false;
        }
        index++;
        if (!isStatement()) return false;

        if (index >= lexemes.size() || lexemes.get(index).getTypeOfLexeme() != TypeOfLexeme.TO){
            printError("Ключевое слово to ожидалось в позиции " +lexemes.get(index).getPosition());
            return false;
        }

        index++;
        if (!isArithmExpr()) return false;

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

        return true;
    }

    public static boolean isCondition(){
        if (!isLogExpression()) return false;

        if (index >= lexemes.size())
            return true;

        while (lexemes.get(index) != null && lexemes.get(index).getTypeOfLexeme() == TypeOfLexeme.OR){
            index++;
            if (!isLogExpression()) return false;
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
        }

        return true;
    }

    public static boolean isRelExpression(){
        if (!isOperand()) return false;

        if (index >= lexemes.size() || lexemes.get(index).getTypeOfLexeme() != TypeOfLexeme.COMPARISON){
            printError("Оператор сравнения ожидался в позиции " + lexemes.get(index).getPosition());
            return false;
        }

        if (lexemes.get(index).getTypeOfLexeme() == TypeOfLexeme.COMPARISON){
            index++;
            if (!isOperand()) return false;
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

        index++;
        if (index >= lexemes.size() || lexemes.get(index).getTypeOfLexeme() != TypeOfLexeme.ASSIGNMENT){
            printError("Присваивание ожидалось в позиции " +lexemes.get(index).getPosition());
            return false;
        }

        index++;
        if (!isArithmExpr()) return false;

        return true;
    }

    public static boolean isArithmExpr(){
        if (!isOperand()) return false;

        while (lexemes.get(index).getTypeOfLexeme() == TypeOfLexeme.ARITHMETIC){
            index++;
            if (!isOperand()) return false;
            if(index >= lexemes.size()) break;
        }

        return true;
    }

    public static void printError(String message){
        System.out.println("<!> Ошибка: " + message + " <!>");
    }
}
