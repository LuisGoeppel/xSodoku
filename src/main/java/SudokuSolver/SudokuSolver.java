package SudokuSolver;

import java.util.Arrays;

public class SudokuSolver {

    public static String solve(String input) {
        int[] matrix = new int[81];
        input = input.replaceAll("/", "");

        if (input.length() != 81) {
            throw new IllegalArgumentException("The input has the wrong length");
        }

        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '-') {
                matrix[i] = 0;
            } else {
                int number = input.charAt(i) - '0';
                if (number < 0 || number >= 10) {
                    throw new IllegalArgumentException("The input contains an illegal character");
                }
                matrix[i] = number;
            }
        }
        int[] solvedMatrix = solve(matrix);

        StringBuilder output = new StringBuilder();
        for (int i = 0; i < solvedMatrix.length; i++) {
            if (i % 9 == 0 && i != 0) {
                output.append("/");
            }
            output.append(solvedMatrix[i]);
        }

        return output.toString();
    }

    private static int[] solve(int[] matrix) {

        int[] rows = new int[9];
        int[] cols = new int[9];
        int[] boxes = new int[9];

        Arrays.fill(rows, 0);
        Arrays.fill(cols, 0);
        Arrays.fill(boxes, 0);

        for (int j = 0; j < 81; j++) {
            if (matrix[j] > 0) {
                int number = matrix[j] - 1;

                int rowNumber = j / 9;
                int colNumber = j % 9;
                int boxNumber = (j / 27) * 3 + ((j % 9) / 3);

                rows[rowNumber] = rows[rowNumber] | (1 << number);
                cols[colNumber] = cols[colNumber] | (1 << number);
                boxes[boxNumber] = boxes[boxNumber] | (1 << number);
            }
        }

        boolean progressMade;
        do {
            System.out.println("Round");
            progressMade = false;
            for (int j = 0; j < 81; j++) {
                if (matrix[j] == 0) {
                    int rowNumber = j / 9;
                    int colNumber = j % 9;
                    int boxNumber = (j / 27) * 3 + ((j % 9) / 3);

                    int possibilities = 511 & ~(rows[rowNumber] | cols[colNumber] | boxes[boxNumber]);

                    if (isPowerOfTwo(possibilities)) {
                        int number = getPowerOfTwo(possibilities);
                        matrix[j] = number + 1;
                        rows[rowNumber] = rows[rowNumber] | (1 << number);
                        cols[colNumber] = cols[colNumber] | (1 << number);
                        boxes[boxNumber] = boxes[boxNumber] | (1 << number);
                        progressMade = true;
                    }
                }
            }
        } while (progressMade);
        return matrix;
    }


    private static boolean isPowerOfTwo(int number) {
        return number > 0 && (number & (number - 1)) == 0;
    }

    private static int getPowerOfTwo(int number) {
        if (isPowerOfTwo(number)) {
            int power = 0;
            while ((number >>= 1) != 0) {
                power++;
            }
            return power;
        } else {
            throw new IllegalArgumentException("Number is not a power of two.");
        }
    }

    private static int clearBit(int num, int position) {
        int mask = ~(1 << position);
        return num & mask;
    }
}

/*




                if (isPowerOfTwo(possibleNumbers[j])) {
                    int number = getPowerOfTwo(possibleNumbers[j]);
                    matrix[j] = number;
                    updatedMatrix = true;
                } else {
                    int xCord = j % 9;
                    int yCord = j / 9;

                    // Eliminate all row numbers as possibilities
                    for (int k = 9 * yCord; k < 9 * (yCord + 1); k++) {
                        if (matrix[k] > 0) {
                            possibleNumbers[j] = clearBit(possibleNumbers[j], matrix[k] - 1);
                        }
                    }

                    // Eliminate all column numbers as possibilities
                    for (int k = xCord; k < 81; k += 9) {
                        if (matrix[k] > 0) {
                            possibleNumbers[j] = clearBit(possibleNumbers[j], matrix[k] - 1);
                        }
                    }

                    // Eliminate all box numbers as possibilities
                    for (int k = 0; k < 81; k++) {
                        if (((k / 9) / 3) == (yCord / 3) && ((k % 9) / 3) == (xCord / 3)) {
                            if (matrix[k] > 0) {
                                possibleNumbers[j] = clearBit(possibleNumbers[j], matrix[k] - 1);
                            }
                        }
                    }
 */