package org.Non_Det_Eps_Automaton;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class NonDetEpsAutomaton {
    public static int amountOfStates;
    public static List<String> alphabet = new ArrayList<>();
    public static List<Integer> [][] transFunc;
    public static int startState;
    public static int [] endStates;

    public static void main(String[] args) {
        try {
            prepareData();
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }

        while(true) {
            System.out.print("Alphabet: ");
            for (String s : alphabet) {
                if (s.equals("eps"))
                    System.out.print("| " + s);
                else
                    System.out.print(s + " ");
            }


            System.out.println("\nEnter the input sequence: ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            System.out.println();
            List<Integer> startStateList = Arrays.asList(startState);
            startAutomate(startStateList, input.split(""), 0, new HashSet<>());
        }
    }

    public static void startAutomate(List<Integer> currentStateList, String [] inputSymbols, int inputIndex, Set<Integer> endStateList) {
        if (currentStateList.size() == 0){
            return;
        }

        for (int state : currentStateList) {
            if (transFunc[state][alphabet.indexOf("eps")].size() != 0) {
                startAutomate(transFunc[state][alphabet.indexOf("eps")], inputSymbols, inputIndex, endStateList);
            }
        }

        for (int state : currentStateList){
            if (inputIndex == inputSymbols.length){
                endStateList.addAll(currentStateList);
                return;
            }

            int symbolIndex = alphabet.indexOf(inputSymbols[inputIndex]);
            startAutomate(transFunc[state][symbolIndex], inputSymbols, inputIndex + 1, endStateList);
        }

        if (inputIndex != 0)
            return;

        boolean result = false;
        for (int i = 0; i < endStates.length; i++){
            if (endStateList.contains(endStates[i])){
                result = true;
                break;
            }
        }

        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/output.txt"));
            writer.write("Input sequence: ");
            for (String s : inputSymbols){
                writer.write(s);
            }
            writer.write("\nAchieved end states: ");
            for (int state : endStateList)
                writer.write(state + " ");
            writer.write("\n");

            if (result){
                writer.write("Input sequence accepted.\n");
            }
            else{
                writer.write("Input sequence rejected.\n");
            }


            writer.close();
        }
        catch (IOException e){

        }
    }

    public static void prepareData() throws IOException {
        Path path = Paths.get("src/main/resources/non_det_eps_automaton.txt");
        BufferedReader reader = Files.newBufferedReader(path);

        alphabet.addAll(Arrays.asList(reader.readLine().split(" ")));
        alphabet.add("eps");

        String[] params = reader.readLine().split(" ");
        amountOfStates = Integer.parseInt(params[0]);

        transFunc = new List[amountOfStates][alphabet.size()];
        for (int i=0; i < amountOfStates; i++){
            String[] stateLists = reader.readLine().split(" ");
            for (int j=0; j < alphabet.size(); j++){
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
        for (int i=0; i< len; i++){
            endStates[i] = Integer.parseInt(params[i+2]);
        }
    }
}
