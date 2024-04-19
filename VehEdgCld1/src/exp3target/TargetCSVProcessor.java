package exp3target;
//6 * 19 =114
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


/**
 * @author znb_n
 * cpu utilization file processor for matrix generation corresponding to target
 */
public class TargetCSVProcessor {

    public static void main(String[] args) {
        String inputFilePath = "C:\\Users\\znb_n\\eclipse-workspace\\Test3-target-p20\\Target\\in\\0CPUUtilRespoOpt.csv";

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath))) {
            String header = reader.readLine(); // Read the header to get column names

            int matrixRows = 6;
            int matrixCols = 19;

            String line;
            int rowNumber = 1;

            // Loop through the data and create 6x20 matrices in separate CSV files for each row
            while ((line = reader.readLine()) != null) {
                String[] row = line.split(",");
                String[][] matrix = new String[matrixRows][matrixCols + 1];

                // Populate the first column with a series starting from 0
                for (int i = 0; i < matrixRows; i++) {
                    matrix[i][0] = String.valueOf(i);
                }

                // Populate the matrix with the data from the 4th column onwards
                int dataIndex = 3; // Start from the 4th column
                for (int i = 0; i < matrixRows; i++) {
                    for (int j = 1; j <= matrixCols; j++) {
                        matrix[i][j] = row[dataIndex++];
                    }
                }

                // Write the matrix to a new CSV file with a header including a series from 1 to 19
                String outputFilePath = "C:\\Users\\znb_n\\eclipse-workspace\\Test3-target-p20\\Target\\in\\_" + rowNumber + ".csv";
                writeMatrixToCSV(matrix, outputFilePath);

                rowNumber++;
            }

            System.out.println("Processing completed successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeMatrixToCSV(String[][] matrix, String outputFilePath) {
        try (FileWriter writer = new FileWriter(outputFilePath)) {
            // Write the new header with values from 1 to 19
            StringBuilder newHeader = new StringBuilder("Series");
            for (int i = 1; i <= 19; i++) {
                newHeader.append(",").append(i);
            }
            writer.write(newHeader.toString() + "\n");

            // Write each row of the matrix to the new CSV file
            for (String[] row : matrix) {
                writer.write(String.join(",", row) + "\n");
            }

            System.out.println("Matrix written to " + outputFilePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
