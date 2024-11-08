package javasimulation;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class SimulationIO {

    private enum CalculationType {
        POTENTIAL_ENERGY,
        KINETIC_ENERGY,
        CENTRE_OF_MASS,
        ANGULAR_MOMENTUM,
        LINEAR_MOMENTUM
    }

    public static void writeOptionsToFiles(HashMap<String, Boolean> options, Vector[] centreOfMass, double[] potentialEnergy, double[] kineticEnergy, Vector[] angularMomentum, Vector[] linearMomentum) {
        // Then write all optional calculations to files
        if (options.get("calculateCentreOfMass")) {
            writeCalculationToFile(CalculationType.CENTRE_OF_MASS, centreOfMass);
        }
        if (options.get("calculateEnergies")) {
            writeCalculationToFile(CalculationType.POTENTIAL_ENERGY, potentialEnergy);
            writeCalculationToFile(CalculationType.KINETIC_ENERGY, kineticEnergy);
        }
        if (options.get("calculateAngularMomentum")) {
            writeCalculationToFile(CalculationType.ANGULAR_MOMENTUM, angularMomentum);
        }
        if (options.get("calculateLinearMomentum")) {
            writeCalculationToFile(CalculationType.LINEAR_MOMENTUM, linearMomentum);
        }
    }

    public static void writeBodiesToFiles(double[][][] simulation, int N, int n) {
        for (int p = 0; p < n; p++) {
            try (FileWriter writer = new FileWriter("Outputs\\output" + p + ".csv")) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < N; i++) {
                    sb.append(simulation[i][0][p])
                    .append(",")
                    .append(simulation[i][1][p])
                    .append(",")
                    .append(simulation[i][2][p])
                    .append(",")
                    .append(simulation[i][3][p])
                    .append(",")
                    .append(simulation[i][4][p])
                    .append(",")
                    .append(simulation[i][5][p])
                    .append("\n");
                }
                writer.write(sb.toString());
            } catch (FileNotFoundException e) {
                System.err.println("Body " + p +  " file not found");
            } catch (IOException e) {
                System.err.println("Something went wrong writing to file: " + e.getMessage());
            }
        }
    }

    private static void writeCalculationToFile(CalculationType calculationType, double[] doubleQuantity) {
        String fileName = "";
        switch (calculationType) {
            case POTENTIAL_ENERGY:
                fileName = "Outputs\\potentialEnergy.csv";
                break;
            case KINETIC_ENERGY:
                fileName = "Outputs\\kineticEnergy.csv";
                break;
            default:
                break;
        }

        int N = doubleQuantity.length;

        try (FileWriter writer = new FileWriter(fileName)) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < N; i++) {
                sb.append(doubleQuantity[i]).append("\n");
            }        
            writer.write(sb.toString());
        } catch (FileNotFoundException e) {
            System.err.println("Calculation file not found");
        } catch (IOException e) {
            System.err.println("Something went wrong writing to file: " + e.getMessage());
        }
    }


    private static void writeCalculationToFile(CalculationType calculationType, Vector[] vectorQuantity) {
        String fileName = "";
        switch (calculationType) {
            case CENTRE_OF_MASS:
                fileName = "Outputs\\centreOfMass.csv";
                break;
            case ANGULAR_MOMENTUM:
                fileName = "Outputs\\angularMomentum.csv";
                break;
            case LINEAR_MOMENTUM:
                fileName = "Outputs\\linearMomentum.csv";
                break;
            default:
                break;
        }

        int N = vectorQuantity.length;

        try (FileWriter writer = new FileWriter(fileName)) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < N; i++) {
                sb.append(vectorQuantity[i].getX())
                .append(",")
                .append(vectorQuantity[i].getY())
                .append(",")
                .append(vectorQuantity[i].getZ())
                .append("\n");
            }
            writer.write(sb.toString());
        } catch (FileNotFoundException e) {
            System.err.println("Calculation file not found");
        } catch (IOException e) {
            System.err.println("Something went wrong writing to file: " + e.getMessage());
        }
    }

    public static void writeSettingsToFile(int N, double dt, int n, double G, boolean findOrbitLength, double orbitLength) {
        try(FileWriter writer = new FileWriter("Outputs\\simulationSettings.csv")){
            writer.append(N + "," + dt + "," + n + "," + G);
            if (findOrbitLength) {
                writer.append("," + orbitLength);
            }
            writer.append("\n");
        } catch (FileNotFoundException e) {
            System.err.println("Setting file not found");
        } catch (IOException e) {
            System.err.println("Something went wrong writing to file: " + e.getMessage());
        }
    }

    public static void writePerturbationSettingsToFile(int N, double dt, int n, double delta, int halfGridSize) {
        try(FileWriter writer = new FileWriter("Outputs\\perturbationSettings.csv")){
            writer.append(N + "," + dt + "," + n + "," + delta + "," + halfGridSize);
            writer.append("\n");
        } catch (FileNotFoundException e) {
            System.err.println("Setting file not found");
        } catch (IOException e) {
            System.err.println("Something went wrong writing to file: " + e.getMessage());
        }
    }

    public static void setupDirectories() {
        java.nio.file.Path outputPath = java.nio.file.Paths.get("Outputs");
        try {
            if (java.nio.file.Files.exists(outputPath)) {
            java.nio.file.Files.walk(outputPath)
                .sorted(java.util.Comparator.reverseOrder())
                .map(java.nio.file.Path::toFile)
                .forEach(java.io.File::delete);
            }
            java.nio.file.Files.createDirectories(outputPath);
        } catch (IOException e) {
            System.err.println("Failed to create or clear output directory: " + e.getMessage());
        }
    }

    public static void saveMatrix(double[][] matrix) {
        try (FileWriter writer = new FileWriter("Outputs\\perturbationMatrix.csv")) {
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length - 1; j++) {
                    writer.write(matrix[i][j] + ",");
                }
                writer.write(matrix[i][matrix[i].length-1] + "\n");
            }
            
        } catch (Exception e) {
            System.out.println("Error writing stop matrix to file");
        }
    }
}
