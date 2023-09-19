package SudokuSolver;

import java.util.Arrays;

public class SudokuSolver {

    private static int[] rows;
    private static int[] cols;
    private static int[] boxes;
    private static int[] matrix;

    public static String solve(String input) {
        matrix = new int[81];
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
        solve();
        return getMatrixAsString();
    }

    private static void solve() {
        long start = System.nanoTime();

        rows = new int[9];
        cols = new int[9];
        boxes = new int[9];

        Arrays.fill(rows, 0);
        Arrays.fill(cols, 0);
        Arrays.fill(boxes, 0);

        // Fill all row, column and box information
        for (int j = 0; j < 81; j++) {
            if (matrix[j] > 0) {
                int number = matrix[j] - 1;

                int rowNumber = getRowNumber(j);
                int colNumber = getColumnNumber(j);
                int boxNumber = getBoxNumber(j);

                rows[rowNumber] = rows[rowNumber] | (1 << number);
                cols[colNumber] = cols[colNumber] | (1 << number);
                boxes[boxNumber] = boxes[boxNumber] | (1 << number);
            }
        }

        String beforeRotation;
        String afterRotation;

        do {
            beforeRotation = getMatrixAsString();
            combineInformation();
            checkMissingNumbers();
            afterRotation = getMatrixAsString();
        } while (!beforeRotation.equals(afterRotation));

        long end = System.nanoTime();
        System.out.println("Solved in " + (((double)(end - start)) / 1_000_000_000.0) + " Seconds");
    }

    private static void combineInformation() {

        boolean progressMade;
        do {
            progressMade = false;
            for (int j = 0; j < 81; j++) {
                if (matrix[j] == 0) {
                    int rowNumber = getRowNumber(j);
                    int colNumber = getColumnNumber(j);
                    int boxNumber = getBoxNumber(j);

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
    }

    private static void checkMissingNumbers() {

        // Check for missing numbers in all rows
        for (int i = 0; i < 9; i++) {
            int nSetBits = getNSetBits(rows[i]);
            if (nSetBits >= 6 && nSetBits < 9) {
                for (int k = 0; k < 9; k++) {
                    if (!isBitSet(rows[i], k)) {
                        int possibleSquares = 0;
                        int possibleSquare = -1;
                        for (int m = 0; m < 9; m++) {
                            int squareNumber = 9 * i + m;
                            if (matrix[squareNumber] == 0) {
                                int columnNumber = getColumnNumber(squareNumber);
                                int boxNumber = getBoxNumber(squareNumber);
                                int mask = 1 << k;

                                if ((cols[columnNumber] & mask) == 0 && (boxes[boxNumber] & mask) == 0) {
                                    possibleSquare = squareNumber;
                                    possibleSquares++;
                                }
                            }
                        }
                        if (possibleSquares == 1) {
                            matrix[possibleSquare] = k + 1;
                            rows[getRowNumber(possibleSquare)] = rows[getRowNumber(possibleSquare)] | (1 << k);
                            cols[getColumnNumber(possibleSquare)] = cols[getColumnNumber(possibleSquare)] | (1 << k);
                            boxes[getBoxNumber(possibleSquare)] = boxes[getBoxNumber(possibleSquare)] | (1 << k);
                        }
                    }
                }
            }
        }

        // Check for missing numbers in all columns
        for (int i = 0; i < 9; i++) {
            int nSetBits = getNSetBits(cols[i]);
            if (nSetBits >= 6 && nSetBits < 9) {
                for (int k = 0; k < 9; k++) {
                    if (!isBitSet(cols[i], k)) {
                        int possibleSquares = 0;
                        int possibleSquare = -1;
                        for (int m = 0; m < 9; m++) {
                            int squareNumber = i + 9 * m;
                            if (matrix[squareNumber] == 0) {
                                int rowNumber = getRowNumber(squareNumber);
                                int boxNumber = getBoxNumber(squareNumber);
                                int mask = 1 << k;

                                if ((rows[rowNumber] & mask) == 0 && (boxes[boxNumber] & mask) == 0) {
                                    possibleSquare = squareNumber;
                                    possibleSquares++;
                                }
                            }
                        }
                        if (possibleSquares == 1) {
                            matrix[possibleSquare] = k + 1;
                            rows[getRowNumber(possibleSquare)] = rows[getRowNumber(possibleSquare)] | (1 << k);
                            cols[getColumnNumber(possibleSquare)] = cols[getColumnNumber(possibleSquare)] | (1 << k);
                            boxes[getBoxNumber(possibleSquare)] = boxes[getBoxNumber(possibleSquare)] | (1 << k);
                        }
                    }
                }
            }
        }

        // Check for missing numbers in all boxes
        for (int i = 0; i < 9; i++) {
            int nSetBits = getNSetBits(boxes[i]);
            if (nSetBits >= 4 && nSetBits < 9) {
                for (int k = 0; k < 9; k++) {
                    if (!isBitSet(boxes[i], k)) {
                        int possibleSquares = 0;
                        int possibleSquare = -1;
                        for (int m = 0; m < 9; m++) {
                            int squareNumber = (i / 3) * 27 + (m / 3) * 9 + (i % 3) * 3 + (m % 3);
                            if (matrix[squareNumber] == 0) {
                                int rowNumber = getRowNumber(squareNumber);
                                int colNumber = getColumnNumber(squareNumber);
                                int mask = 1 << k;

                                if ((rows[rowNumber] & mask) == 0 && (cols[colNumber] & mask) == 0) {
                                    possibleSquare = squareNumber;
                                    possibleSquares++;
                                }
                            }
                        }
                        if (possibleSquares == 1) {
                            matrix[possibleSquare] = k + 1;
                            rows[getRowNumber(possibleSquare)] = rows[getRowNumber(possibleSquare)] | (1 << k);
                            cols[getColumnNumber(possibleSquare)] = cols[getColumnNumber(possibleSquare)] | (1 << k);
                            boxes[getBoxNumber(possibleSquare)] = boxes[getBoxNumber(possibleSquare)] | (1 << k);
                        }
                    }
                }
            }
        }
    }

    private static int getNSetBits(int n) {
        int count = 0;
        while (n > 0) {
            count += n & 1;
            n >>= 1;
        }
        return count;
    }

    private static String getMatrixAsString() {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < matrix.length; i++) {
            if (i % 9 == 0 && i != 0) {
                output.append("/");
            }
            output.append(matrix[i]);
        }

        return output.toString();
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

    private static boolean isBitSet(int number, int pos) {
        int mask = 1 << pos;
        return (number & mask) > 0;
    }

    private static int clearBit(int num, int position) {
        int mask = ~(1 << position);
        return num & mask;
    }

    private static int getRowNumber(int squareNumber) {
        return squareNumber / 9;
    }

    private static int getColumnNumber(int squareNumber) {
        return squareNumber % 9;
    }

    private static int getBoxNumber(int squareNumber) {
        return (squareNumber / 27) * 3 + ((squareNumber % 9) / 3);
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