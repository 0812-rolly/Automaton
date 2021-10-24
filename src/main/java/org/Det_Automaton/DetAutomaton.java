package org.Det_Automaton;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DetAutomaton {
    public static int amountOfStates;
    public static List<String> alphabet = new ArrayList<>();
    public static int [][] transactFunction;
    public static int startState;
    public static int [] endStates;

    public static void main(String[] args) {
        try {
            getData();
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
        System.out.print("Alphabet: ");
        for (String s : alphabet) {
            System.out.print(s + " ");
        }
        System.out.println("\nEnter the input sequence: ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        startingAutomate(input);
    }

    public static void startingAutomate(String input){
        int currentState = startState;
        String [] inputArray = input.split("");
        for (String symbol : inputArray){
            int index = alphabet.indexOf(symbol);
            currentState = transactFunction[currentState][index];
        }
        boolean result = false;
        for (int i = 0; i < endStates.length; i++){
            if (currentState == endStates[i]){
                result = true;
                break;
            }
        }
        Path outputPath = Paths.get("src/main/resources/outputFile.txt");
        if (result){
            try {
                Files.writeString(outputPath, "Input sequence accepted");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
            try {
                Files.writeString(outputPath, "Input sequence rejected");
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static void getData() throws IOException {

        Path inputPath = Paths.get("src/main/resources/inputFile.txt");
        BufferedReader reader = Files.newBufferedReader(inputPath);

        String alphabetString = reader.readLine();
        String[] fullAlphabet = alphabetString.split(" ");

        String[] params = reader.readLine().split(" ");
        amountOfStates = Integer.parseInt(params[0]);

        for (int i=0; i< fullAlphabet.length;i++){
            alphabet.add(fullAlphabet[i]);
        }

        transactFunction = new int[amountOfStates][fullAlphabet.length];
        for (int i = 0; i < amountOfStates; i++){
            String[] states = reader.readLine().split(" ");
            for (int j = 0; j < fullAlphabet.length; j++){
                transactFunction[i][j] = Integer.parseInt(states[j]);
            }
        }

        startState = Integer.parseInt(params[1]);
        int len = params.length - 2;
        endStates = new int[len];
        for (int i=0; i < len; i++){
            endStates[i] = Integer.parseInt(params[i+2]);
        }
    }
}
