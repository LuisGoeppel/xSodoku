package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class SudokuGenerator {

    public static String getRandomSudoku() {

        String filePath = "src/main/resources/database/sudoku-10k.txt";

        try {
            ArrayList<String> lines = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            reader.close();

            Random rand = new Random();
            int randomIndex = rand.nextInt(lines.size());
            String randomLine = lines.get(randomIndex);

            String[] lineInformation = randomLine.split(",");
            String sudoku = lineInformation[1].replaceAll("\\.", "-");
            double difficulty = Double.parseDouble(lineInformation[4]);

            StringBuilder output = new StringBuilder();

            for (int i = 0; i < sudoku.length(); i++) {
                char c = sudoku.charAt(i);
                output.append(c);

                if ((i + 1) % 9 == 0 && i != sudoku.length() - 1) {
                    output.append('/');
                }
            }
            return output.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(SudokuGenerator.getRandomSudoku());
    }
}
