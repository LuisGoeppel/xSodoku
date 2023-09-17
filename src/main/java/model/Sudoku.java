package model;

import java.util.Random;

public class Sudoku {
    private int[] matrix;
    private boolean[] startNumber;

    private Random random;

    public Sudoku(String input) {
        matrix = new int[81];
        startNumber = new boolean[81];

        input = input.replaceAll("/", "");
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '-') {
                matrix[i] = 0;
                startNumber[i] = false;
            } else {
                int number = input.charAt(i) - '0';
                assert number >= 0 && number < 10;
                matrix[i] = number;
                startNumber[i] = true;
            }
        }
    }

    public Sudoku() {
        matrix = new int[81];
        startNumber = new boolean[81];
        random = new Random();

        for (int i = 0; i < 81; i++) {
            matrix[i] = 0;
            startNumber[i] = false;
        }

        int nStartNumbers = 12;
        do {
            int squareNumber = random.nextInt(81);
            int number = random.nextInt(9) + 1;
            if (insert(squareNumber, number, true)) {
                nStartNumbers --;
            }
        } while (nStartNumbers > 0);
    }

    public boolean insertNumber(int squareNumber, int number) {
        return insert(squareNumber, number, false);
    }

    public boolean insertStartNumber(int squareNumber, int number) {
        return insert(squareNumber, number, true);
    }

    private boolean insert(int squareNumber, int number, boolean isStartNumber) {
        if (!(squareNumber >= 0 && squareNumber < 81 && number > 0 && number <= 9)) {
            System.err.println("SquareNumber: " + squareNumber);
            System.err.println("Number: " + number);
            throw new IllegalArgumentException("Would ya please enter correct parameters?!");

        }

        int xCord = squareNumber % 9;
        int yCord = squareNumber / 9;

        if (matrix[squareNumber] != 0) {
            return false;
        }

        // Assert that the row doesn't already contain the number
        for (int i = 9 * yCord; i < 9 * (yCord + 1); i++) {
            if (matrix[i] == number) {
                return false;
            }
        }

        // Assert that the column doesn't already contain the number
        for (int i = xCord; i < 81; i += 9) {
            if (matrix[i] == number) {
                return false;
            }
        }

        // Assert that the box doesn't already contain the number
        for (int i = 0; i < 81; i++) {
            if (((i / 9) / 3) == (yCord / 3) && ((i % 9) / 3) == (xCord / 3) && matrix[i] == number) {
                return false;
            }
        }

        matrix[squareNumber] = number;
        if (isStartNumber) {
            startNumber[squareNumber] = true;
        }
        return true;
    }

    public boolean updateSudoku(String update) {
        int[] backup = matrix.clone();
        update = update.replaceAll("/", "");

        if (update.length() != 81) {
            return false;
        }

        for (int i = 0; i < update.length(); i++) {
            if (matrix[i] > 0) {
                if (update.charAt(i) - '0' != matrix[i]) {
                    return false;
                }
            } else {
                if (update.charAt(i) > '0') {
                    insert(i, update.charAt(i) - '0', false);
                }
            }
        }
        return true;
    }

    public boolean remove(int squareNumber) {
        if (startNumber[squareNumber]) {
            return false;
        }
        matrix[squareNumber] = 0;
        return true;
    }

    public String getAsString() {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < matrix.length; i++) {
            if (i > 0 && i % 9 == 0) {
                output.append("/");
            }
            output.append(matrix[i]);
        }
        return output.toString();
    }

    public int[] getMatrix() {
        return matrix;
    }

    public boolean[] getStartNumbers() {
        return startNumber;
    }
}
