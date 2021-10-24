package org.Non_Det_Automaton;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class NonDetAutomaton {
    public static int amountOfStates;
    public static List<String> alphabet = new ArrayList<>();
    public static List<Integer> [][] transFunc;
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
        List<Integer> startStateList = Arrays.asList(startState);
        startAutomate(startStateList, input.split(""), 0, new ArrayList<>());
    }

    public static void startAutomate(List<Integer> currentStateList, String [] input, int inputIndex, List<Integer> endStateList){
        if (currentStateList.size() == 0){
            return;
        }

        if (inputIndex == input.length){
            endStateList.addAll(currentStateList);
            return;
        }

        for (int state : currentStateList){
            int symbolIndex = alphabet.indexOf(input[inputIndex]);
            startAutomate(transFunc[state][symbolIndex], input, inputIndex + 1, endStateList);
        }

        if (inputIndex != 0)
            return;

        boolean result = false;
        for (int i = 0; i < endStates.length; i++){
            int ind = endStateList.indexOf(endStates[i]);
            if (ind != -1){
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

        Path path = Paths.get("src/main/resources/non_det_automaton.txt");
        BufferedReader reader = Files.newBufferedReader(path);

        String alphabetString = reader.readLine();
        String[] fullAlphabet = alphabetString.split(" ");

        String[] params = reader.readLine().split(" ");
        amountOfStates = Integer.parseInt(params[0]);

        //алфавит
        for (int i=0; i< fullAlphabet.length;i++){
            alphabet.add(fullAlphabet[i]);
        }

        //матрица переходов
        transFunc = new List[amountOfStates][fullAlphabet.length];
        for (int i=0; i < amountOfStates; i++){
            String[] stateLists = reader.readLine().split(" ");
            for (int j=0; j < fullAlphabet.length; j++){
                if (stateLists[j].equals("_")) {
                    transFunc[i][j] = new ArrayList<>();
                    continue;
                }
                String [] stateArr = stateLists[j].split(",");
                List<Integer> stateList = new ArrayList<>();
                for (String s : stateArr)
                    stateList.add(Integer.valueOf(s));
                transFunc[i][j] = stateList;
            }
        }

        //начальное состояние
        startState = Integer.parseInt(params[1]);

        //множество конечных состояний
        int len = params.length - 2;
        endStates = new int[len];
        for (int i=0; i < len; i++){
            endStates[i] = Integer.parseInt(params[i+2]);
        }
    }
}