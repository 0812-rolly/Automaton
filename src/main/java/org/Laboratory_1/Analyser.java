package org.Laboratory_1;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Analyser {
    public static int numOfStates;
    public static int [][] transFunc;
    public static int startState;
    public static int [] endStates;
    public static List<String> keywords = new ArrayList<>();
    public static Map<Integer, String> stateMap = new HashMap<>();
    public static List<Lexeme> lexemes = new ArrayList<>();

    private static void loadKeywords() throws IOException {
        Path path = Paths.get("src/main/resources/laboratory_1/keys.txt");
        BufferedReader reader = Files.newBufferedReader(path);

        while (reader.ready()){
            keywords.add(reader.readLine());
        }
    }

    private static void loadStateMap(){
        stateMap.put(0, "Start");
        stateMap.put(1, "Identifier");
        stateMap.put(2, "Constant");
        stateMap.put(3, "Arithmetic");
        stateMap.put(4, "LessComparison");
        stateMap.put(5, "Assignment");
        stateMap.put(6, "MoreOrEqualComparison");
        stateMap.put(7, "Equality");
        stateMap.put(8, "Final");
    }

    public static void main(String[] args) {
        try {
            loadKeywords();
            loadStateMap();
            prepareData();
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }

        System.out.println("\nВведите строку на вход автомата: ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        startAutomate(input + " ");

    }

    public static void startAutomate(String input){
        int currentState = startState;
        String [] inputArray = input.split("");
        StringBuilder lexeme = new StringBuilder();
        int lexemeIndex = 0;
        int movingIndex = 0;

        for (String symbol : inputArray){
            movingIndex++;
            int index = getTableIndex(symbol);
            if (index != 7)
                lexeme.append(symbol);

            int previousState = currentState;
            currentState = transFunc[currentState][index];

            if (currentState == 8){
                getLexemeInfo(previousState, lexeme.toString(), lexemeIndex);
                lexemeIndex = movingIndex;
                lexeme.setLength(0);
            }
        }

        boolean result = false;
        for (int i = 0; i < endStates.length; i++){
            if (currentState == endStates[i]){
                result = true;
                break;
            }
        }
        if (result){
            System.out.println();
            printLexemes();
        }
    }

    public static int getTableIndex(String symbol){
        if (Character.isDigit(symbol.charAt(0)))
            return 1;

        if (symbol.charAt(0) >= 65 && symbol.charAt(0) <= 122)
            return 0;

        return switch (symbol) {
            case "+" -> 2;
            case "-" -> 3;
            case "<" -> 4;
            case ">" -> 5;
            case "=" -> 6;
            default -> 7;
        };
    }

    public static void getLexemeInfo(int previousState, String lexeme, int position){
        String state = stateMap.get(previousState);
        TypeOfLexeme typeOfLexeme = null;
        ClassOfLexeme classOfLexeme = null;
        switch (state) {
            case "Identifier":
                if (keywords.contains(lexeme)) {
                    classOfLexeme = ClassOfLexeme.KEYWORD;
                    for (TypeOfLexeme lex : TypeOfLexeme.values()) {
                        if (lex.toString().toLowerCase(Locale.ROOT).equals(lexeme)) {
                            typeOfLexeme = lex;
                        }
                    }
                } else {
                    classOfLexeme = ClassOfLexeme.IDENTIFIER;
                    typeOfLexeme = TypeOfLexeme.UNDEFINED;
                }
                break;
            case "Constant":
                classOfLexeme = ClassOfLexeme.CONSTANT;
                typeOfLexeme = TypeOfLexeme.UNDEFINED;
                break;
            case "Arithmetic":
                classOfLexeme = ClassOfLexeme.SPECIAL_SYMBOL;
                typeOfLexeme = TypeOfLexeme.ARITHMETIC;
                break;
            case "LessComparison":
            case "MoreOrEqualComparison":
            case "Equality":
                classOfLexeme = ClassOfLexeme.SPECIAL_SYMBOL;
                typeOfLexeme = TypeOfLexeme.COMPARISON;
                break;
            case "Assignment":
                classOfLexeme = ClassOfLexeme.SPECIAL_SYMBOL;
                typeOfLexeme = TypeOfLexeme.ASSIGNMENT;
                break;
            default:
                return;
        }

        Lexeme lex = new Lexeme(classOfLexeme, typeOfLexeme, lexeme, position);
        lexemes.add(lex);
    }

    private static void printLexemes(){
        System.out.println("\n<-------- Ключевые слова: -------->\n");
        for (Lexeme lex : lexemes.stream()
                .filter(x -> x.getClassOfLexeme() == ClassOfLexeme.KEYWORD)
                .collect(Collectors.toList())){
            System.out.println(lex);
        }

        System.out.println("\n<-------- Идентификаторы: -------->\n");
        for (Lexeme lex : lexemes.stream()
                .filter(x -> x.getClassOfLexeme() == ClassOfLexeme.IDENTIFIER)
                .collect(Collectors.toList())){

            System.out.println(lex);
        }

        System.out.println("\n<-------- Константы: -------->\n");
        for (Lexeme lex : lexemes.stream()
                .filter(x -> x.getClassOfLexeme() == ClassOfLexeme.CONSTANT)
                .collect(Collectors.toList())){

            System.out.println(lex);
        }

        System.out.println("\n<-------- Специальные символы: -------->\n");
        for (Lexeme lex : lexemes.stream()
                .filter(x -> x.getClassOfLexeme() == ClassOfLexeme.SPECIAL_SYMBOL)
                .collect(Collectors.toList())){

            System.out.println(lex);
        }
    }

    public static void prepareData() throws IOException {
        numOfStates = 9;
        int alphabetLength = 8;
        startState = 0;
        endStates = new int [] {8};

        Path path = Paths.get("src/main/resources/laboratory_1/transTable.txt");
        BufferedReader reader = Files.newBufferedReader(path);

        transFunc = new int[numOfStates][8];
        for (int i=0; i < numOfStates; i++){
            String[] states = reader.readLine().split(" ");
            for (int j=0; j<alphabetLength; j++){
                transFunc[i][j] = Integer.parseInt(states[j]);
            }
        }
    }

}
