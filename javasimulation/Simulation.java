package javasimulation;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class Simulation {

    private final Body[] bodies;
    private final int n;
    private final int N;
    private final double dt;
    private final double G;
    private double elapsedTime = 0;
    private IntegratorFunction integratorFunction;
    private double[][][] simulation;

    private HashMap<String, Boolean> options = new HashMap<String, Boolean>();

    private double energyErrorBound = 1e-1;
    private double distanceBound = 20;
    private double timestepSizeBound = 1e-6;

    private Vector[] centreOfMass;
    private double[] potentialEnergy;
    private double[] kineticEnergy;
    private Vector[] angularMomentum;
    private Vector[] linearMomentum;
    private double orbitLength;

    public Simulation(Body[] bodies, int N, double dt, double G, IntegratorFunction integrator)
    {
        this.bodies = bodies;
        this.n = bodies.length;
        this.N = N;
        this.dt = dt;
        this.G = 1;

        options.put("checkStopConditions", false);
        options.put("useVariableTimestep", false);
        options.put("calculateCentreOfMass", false);
        options.put("calculateEnergies", false);
        options.put("calculateAngularMomentum", false);
        options.put("calculateLinearMomentum", false);
        options.put("findOrbitLength", false);

        centreOfMass = new Vector[N];
        potentialEnergy = new double[N];
        kineticEnergy = new double[N];
        angularMomentum = new Vector[N];
        linearMomentum = new Vector[N];

        this.integratorFunction = integrator;
    }

    public HashMap<String, Boolean> getOptions() {
        return options;
    }

    public void setOptions(HashMap<String, Boolean> options) {
        options.putAll(options);
    }

    public void setEnergyErrorBound(double energyErrorBound) {
        this.energyErrorBound = energyErrorBound;
    }

    public void setDistanceBound(double distanceBound) {
        this.distanceBound = distanceBound;
    }

    public void setTimestepSizeBound(double timestepSizeBound) {
        this.timestepSizeBound = timestepSizeBound;
    }

    private void doOptionalCalculations(int timestep) {
        if (options.get("calculateCentreOfMass")) {
            centreOfMass[timestep] = calculateCentreOfMass();
        }
        if (options.get("calculateEnergies")) {
            potentialEnergy[timestep] = calculatePotentialEnergy();
            kineticEnergy[timestep] = calculateKineticEnergy();
        }
        if (options.get("calculateAngularMomentum")) {
            angularMomentum[timestep] = calculateAngularMomentum();
        }
        if (options.get("calculateLinearMomentum")) {
            linearMomentum[timestep] = calclateLinearMomentum();
        }
    }

    public void run(){
        simulation = new double[N][6][n];

        // ----- Main Time Loop ----- \\
        for (int i = 0; i < N; i++) {
            // Record all optional calculations
            doOptionalCalculations(i);

            // Record the current timestep of the simulation
            for (int p = 0; p < n; p++) {
                Vector position = bodies[p].getPosition();
                Vector velocity = bodies[p].getVelocity();
                simulation[i][0][p] = position.getX();
                simulation[i][1][p] = position.getY();
                simulation[i][2][p] = position.getZ();
                simulation[i][3][p] = velocity.getX();
                simulation[i][4][p] = velocity.getY();
                simulation[i][5][p] = velocity.getZ();
            }

            // Then update the states of all bodies
            boolean useVariableTimestep = options.get("useVariableTimestep");
            double usedTimestepLength = integratorFunction.Integrate(bodies, dt, useVariableTimestep);
            elapsedTime += usedTimestepLength;

            // Check if the simulation should stop
            if (options.get("checkStopConditions")) {
                checkStopConditions(i, usedTimestepLength, elapsedTime);
            }

            if (options.get("findOrbitLength") && (i > 10) && (orbitLength != 0.0)) {
                findOrbitLength();
            }

        }

        writeSimulationToFiles();
    }

    // check my logic in this method... It might be wrong
    private void findOrbitLength() {
        boolean hasMadeFullOrbit = true;
        for (int p = 0; p < n; p++) {
            Vector diff = bodies[p].getPosition().subtract(bodies[p].getInitialPosition());
            double distance = diff.norm();
            if (distance > 0.1) {
                hasMadeFullOrbit = false;
                break;
            }
        }
        if (hasMadeFullOrbit) {
            orbitLength = elapsedTime;
        }
    }

    private void checkStopConditions(int timestep, double usedTimestepLength, double elapsedTime) {
        if (options.get("calculateEnergies")) {
            // Check if the energy error is within the bound
            double totalEnergy = potentialEnergy[timestep] + kineticEnergy[timestep];
            double energyError = Math.abs((totalEnergy - (potentialEnergy[0] + kineticEnergy[0])) / (potentialEnergy[0] + kineticEnergy[0]));
            if (energyError > energyErrorBound) {
                System.out.println("Simulation terminated after exceeding energy error bound");
                System.out.println("Energy error bound: \t" + energyErrorBound);
                System.out.println("Energy error: \t" + energyError);
                System.out.println("Time reached: \t" + elapsedTime);
                throw new RuntimeException("Energy error bound exceeded");
            }
        }

        if (options.get("calculateCentreOfMass")) {
            // Check if the distance between the centre of mass and the origin is within the bound
            double distance = centreOfMass[timestep].norm();
            if (distance > distanceBound) {
                System.out.println("Simulation terminated after exceeding distance bound");
                System.out.println("Distance bound: \t" + distanceBound);
                System.out.println("Distance: \t" + distance);
                System.out.println("Time reached: \t" + elapsedTime);
                throw new RuntimeException("Distance bound exceeded");
            }
        }

        // Check if the timestep size is within the bound
        if (options.get("useVariableTimestep")) {
            if (usedTimestepLength < timestepSizeBound) {
                System.out.println("Simulation terminated after exceeding timestep size bound");
                System.out.println("Timestep size bound: \t" + timestepSizeBound);
                System.out.println("Timestep size: \t" + usedTimestepLength);
                System.out.println("Time reached: \t" + elapsedTime);
                throw new RuntimeException("Timestep size bound exceeded");
            }
        }
    }

    private void setupDirectories() {
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

    private void writeSettingsToFile() {
        try(FileWriter writer = new FileWriter("JavaSimulation\\Outputs\\simulationSettings.csv")){
            writer.append(this.N + "," + this.dt + "," + this.n + "," + this.G);
            if (options.get("findOrbitLength")) {
                writer.append("," + orbitLength);
            }
            writer.append("\n");
        } catch (FileNotFoundException e) {
            System.err.println("Setting file not found");
        } catch (IOException e) {
            System.err.println("Something went wrong writing to file: " + e.getMessage());
        }
    }

    private enum CalculationType {
        POTENTIAL_ENERGY,
        KINETIC_ENERGY,
        CENTRE_OF_MASS,
        ANGULAR_MOMENTUM,
        LINEAR_MOMENTUM
    }

    private void writeCalculationToFile(CalculationType calculationType) {
        String fileName = "";
        switch (calculationType) {
            case POTENTIAL_ENERGY:
                fileName = "JavaSimulation\\Outputs\\potentialEnergy.csv";
                break;
            case KINETIC_ENERGY:
                fileName = "JavaSimulation\\Outputs\\kineticEnergy.csv";
                break;
            case CENTRE_OF_MASS:
                fileName = "JavaSimulation\\Outputs\\centreOfMass.csv";
                break;
            case ANGULAR_MOMENTUM:
                fileName = "JavaSimulation\\Outputs\\angularMomentum.csv";
                break;
            case LINEAR_MOMENTUM:
                fileName = "JavaSimulation\\Outputs\\linearMomentum.csv";
                break;
            default:
                break;
        }

        try (FileWriter writer = new FileWriter(fileName)) {
            StringBuilder sb = new StringBuilder();
            switch (calculationType) {
                case POTENTIAL_ENERGY:
                    for (int i = 0; i < this.N; i++) {
                        sb.append(potentialEnergy[i]).append("\n");
                    }
                    break;
                case KINETIC_ENERGY:
                    for (int i = 0; i < this.N; i++) {
                        sb.append(kineticEnergy[i]).append("\n");
                    }
                    break;
                case CENTRE_OF_MASS:
                    for (int i = 0; i < this.N; i++) {
                        sb.append(centreOfMass[i].getX())
                        .append(",")
                        .append(centreOfMass[i].getY())
                        .append(",")
                        .append(centreOfMass[i].getZ())
                        .append("\n");
                    }
                    break;
                case ANGULAR_MOMENTUM:
                    for (int i = 0; i < this.N; i++) {
                        sb.append(angularMomentum[i].getX())
                        .append(",")
                        .append(angularMomentum[i].getY())
                        .append(",")
                        .append(angularMomentum[i].getZ())
                        .append("\n");
                    }
                    break;
                case LINEAR_MOMENTUM:
                    for (int i = 0; i < this.N; i++) {
                        sb.append(linearMomentum[i].getX())
                        .append(",")
                        .append(linearMomentum[i].getY())
                        .append(",")
                        .append(linearMomentum[i].getZ())
                        .append("\n");
                    }
                    break;
                default:
                    break;
            }
            writer.write(sb.toString());
        } catch (FileNotFoundException e) {
            System.err.println("Calculation file not found");
        } catch (IOException e) {
            System.err.println("Something went wrong writing to file: " + e.getMessage());
        }
    }

    private void writeBodiesToFiles() {
        for (int p = 0; p < this.n; p++) {
            try (FileWriter writer = new FileWriter("JavaSimulation\\Outputs\\output" + p + ".csv")) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < this.N; i++) {
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

    private void writeSimulationToFiles() {

        // Create the output directory if it doesn't exist, or clear it if it does
        setupDirectories();

        // Start by writing the simulation settings to a file
        writeSettingsToFile();

        // Then write each body to its own file
        writeBodiesToFiles();

        // Then write all optional calculations to files
        if (options.get("calculateCentreOfMass")) {
            writeCalculationToFile(CalculationType.CENTRE_OF_MASS);
        }
        if (options.get("calculateEnergies")) {
            writeCalculationToFile(CalculationType.POTENTIAL_ENERGY);
            writeCalculationToFile(CalculationType.KINETIC_ENERGY);
        }
        if (options.get("calculateAngularMomentum")) {
            writeCalculationToFile(CalculationType.ANGULAR_MOMENTUM);
        }
        if (options.get("calculateLinearMomentum")) {
            writeCalculationToFile(CalculationType.LINEAR_MOMENTUM);
        }
    }

    // ---------- Simulation optional Calculations ---------- \\

    public double calculatePotentialEnergy(){
        double potentialEnergy = 0.0;
        for (int p = 0; p < n; p++) {
            potentialEnergy += bodies[p].getPotentialEnergy(bodies, G);
        }
        return potentialEnergy;
    }

    public double calculateKineticEnergy() {
        double kineticEnergy = 0.0;
        for (int p = 0; p < n; p++) {
            kineticEnergy += bodies[p].getKineticEnergy();
        }
            
        return kineticEnergy;
    }

    public double calculateTotalMass() {
        double totalMass = 0;
        for (int i = 0; i < n; i++) {
            totalMass += bodies[i].getMass();
        }
        return totalMass;

    }

    public Vector calculateCentreOfMass() {
        Vector centreOfMass = new Vector();
        for (int p = 0; p < n; p++) {
            centreOfMass = centreOfMass.add(Vector.multiply(bodies[p].getPosition(), bodies[p].getMass()));
        }
        centreOfMass.divide(calculateTotalMass());
        
        return centreOfMass;
    }

    public Vector calculateAngularMomentum() {
        Vector L = new Vector();
        for (int p = 0; p < n; p++) {
            L.add(bodies[p].calculateAngularMomentum());
        }
        return L;
    }

    public Vector calclateLinearMomentum() {
        Vector P = new Vector();
        for (int p = 0; p < n; p++) {
            P.add(bodies[p].calculateLinearMomentum());
        }
        
        return P;
    }

}