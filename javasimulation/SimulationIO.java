package javasimulation;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class SimulationIO {

    public static void setSimulationOptions(Simulation simulation, List<String> clOptions) {
        HashMap<String, Boolean> simOptions = simulation.getOptions();
        clOptions.replaceAll(String::strip);
        clOptions.replaceAll(s -> s.startsWith("--") ? s.substring(2) : s);

        for (String option : clOptions) {
            if (simOptions.containsKey(option)) {
                simOptions.replace(option, true);
            }
            else if (option.equals("integrator")) {
                int integratorFlagIndex = clOptions.indexOf("integrator");
                String integratorName = clOptions.get(integratorFlagIndex + 1);
                simulation.setIntegratorFunction(Integrators.integratorMap.get(integratorName));
            }
        }
        simulation.setOptions(simOptions);
    }

    public static void setDefaultSimulationOptions(HashMap<String, Boolean> options) {
        options.put("useVariableTimestep", false);
        options.put("checkStopConditions", false);
        options.put("calculateCentreOfMass", false);
        options.put("calculateEnergies", false);
        options.put("calculateAngularMomentum", false);
        options.put("calculateLinearMomentum", false);
        options.put("findOrbitLength", false);
        options.put("calculateShapeSpace", false);
        options.put("skipSaveToCSV", false);
    }

    public static void setPerturbationsSettings(Perturbations perturbations, List<String> clOptions) {
        HashMap<String, Boolean> perturbationsOptions = perturbations.getOptions();
        clOptions.replaceAll(String::strip);
        clOptions.replaceAll(s -> s.startsWith("--") ? s.substring(2) : s);
        for (String option : clOptions) {
            if (perturbationsOptions.containsKey(option)) {
                perturbationsOptions.replace(option, true);
            }
            else switch (option) {
                case "delta":
                    int deltaFlagIndex = clOptions.indexOf("delta");
                    double delta = Double.parseDouble(clOptions.get(deltaFlagIndex + 1));
                    perturbations.setDelta(delta);
                    break;
                case "halfGridSize":
                    int halfGridSizeFlagIndex = clOptions.indexOf("halfGridSize");
                    int halfGridSize = Integer.parseInt(clOptions.get(halfGridSizeFlagIndex + 1));
                    perturbations.setHalfGridSize(halfGridSize);
                    break;
                case "integrator":
                    int integratorFlagIndex = clOptions.indexOf("integrator");
                    String integratorName = clOptions.get(integratorFlagIndex + 1);
                    perturbations.setIntegratorFunction(Integrators.integratorMap.get(integratorName));
                    break;
                case "shiftEnergy":
                    int shiftEnergyFlagIndex = clOptions.indexOf("shiftEnergy");
                    double shiftEnergy = Double.parseDouble(clOptions.get(shiftEnergyFlagIndex + 1));
                    perturbations.shiftEnergy(shiftEnergy);
                    break;
                default:
            }
            // Second switch statement for options that are both in the Perturbation options key and have arguments that follow them
            switch (option) {
                case "perturbEnergy":
                    int perturbEnergyIndex = clOptions.indexOf("perturbEnergy");
                    int halfGridSizeEnergy = Integer.parseInt(clOptions.get(perturbEnergyIndex + 1));
                    double energyDelta = Double.parseDouble(clOptions.get(perturbEnergyIndex + 2));
                    perturbations.setHalfGridSizeEnergy(halfGridSizeEnergy);
                    perturbations.setEnergyDelta(energyDelta);
                    break;
                case "perturbAngularMomentum":
                    int angularMomentumIndex = clOptions.indexOf("perturbAngularMomentum");
                    int angularMomentumHalfGridSize = Integer.parseInt(clOptions.get(angularMomentumIndex + 1));
                    double angularMomentumDelta = Double.parseDouble(clOptions.get(angularMomentumIndex + 2));
                    perturbations.setAngularMomentumShift(angularMomentumHalfGridSize, angularMomentumDelta);
                    break;
                case "perturbSingular":
                    perturbationsOptions.replace("perturbPositions",true);
                case "perturbSingularAngular":
                    perturbationsOptions.replace("perturbPositions",true);
                default:
            }
        }
    }

    public static void setDefaultPerturbationsOptions(HashMap<String, Boolean> options) {
        // Set the options unique to perturbations
        options.put("perturbPositions", false);
        options.put("perturbVelocities", false);
        options.put("perturbEnergy", false);
        options.put("perturbAngularMomentum", false);

        // Set default options for each simulation, and override specific options
        setDefaultSimulationOptions(options);
        options.replace("checkStopConditions", true);
        options.replace("skipSaveToCSV", true);

    }

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
            try (FileWriter writer = new FileWriter("Outputs/output" + p + ".csv")) {
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
                fileName = "Outputs/potentialEnergy.csv";
                break;
            case KINETIC_ENERGY:
                fileName = "Outputs/kineticEnergy.csv";
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
                fileName = "Outputs/centreOfMass.csv";
                break;
            case ANGULAR_MOMENTUM:
                fileName = "Outputs/angularMomentum.csv";
                break;
            case LINEAR_MOMENTUM:
                fileName = "Outputs/linearMomentum.csv";
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
        try(FileWriter writer = new FileWriter("Outputs/simulationSettings.csv")){
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

    public static void writePerturbationSettingsToFile(int N, double delta, int halfGridSize) {
        try(FileWriter writer = new FileWriter("Outputs/perturbationSettings.csv")){
            writer.append(N + "," + delta + "," + halfGridSize);
            writer.append("\n");
        } catch (FileNotFoundException e) {
            System.err.println("Setting file not found");
        } catch (IOException e) {
            System.err.println("Something went wrong writing to file: " + e.getMessage());
        }
    }
    
    public static void write3dPerturbationSettingsToFile(int N, double deltaAxis1, double deltaAxis2, int halfGridSizeAxis1, int halfGridSizeAxis2) {
        try(FileWriter writer = new FileWriter("Outputs/3dperturbationSettings.csv")){
            writer.append(N + "," + deltaAxis1 + "," + deltaAxis2 + "," + halfGridSizeAxis1 + "," + halfGridSizeAxis2);
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

    public static void saveMatrix(String filename, int[][] matrix) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Outputs/"+ filename +".csv"))) {
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length - 1; j++) {
                    writer.write(matrix[i][j] + ",");
                }
                writer.write(matrix[i][matrix[i].length-1] + "\n");
            }
            writer.close();

        } catch (Exception e) {
            System.out.println("Error writing " + filename + " to file");
        } 
    }

    public static void saveMatrix(String filename, boolean[][] matrix) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Outputs/"+ filename +".csv"))) {
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length - 1; j++) {
                    writer.write(matrix[i][j] + ",");
                }
                writer.write(matrix[i][matrix[i].length-1] + "\n");
            }
            writer.close();

        } catch (Exception e) {
            System.out.println("Error writing " + filename + " to file");
        }
    }

    public static void saveMatrix(String filename, double[][] matrix) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Outputs/"+ filename +".csv"))) {
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length - 1; j++) {
                    writer.write(matrix[i][j] + ",");
                }
                writer.write(matrix[i][matrix[i].length-1] + "\n");
            }
            writer.close();
            
        } catch (Exception e) {
            System.out.println("Error writing " + filename + " to file");
        }
    }

    public static void saveMatrix(String filename, char[][] matrix) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Outputs/"+ filename +".csv"))) {
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length - 1; j++) {
                    writer.write(matrix[i][j] + ",");
                }
                writer.write(matrix[i][matrix[i].length-1] + "\n");
            }
            writer.close();
            
        } catch (Exception e) {
            System.out.println("Error writing " + filename + " to file");
        }
    }
}
