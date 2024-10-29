package javasimulation;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class Simulation {

    private final Body[] bodies;
    private final int n;
    private final int N;
    private final double dt;
    private final double G;
    private final IntegratorType integratorType;
    private final boolean useVariableTimestep;
    private double elapsedTime = 0;
    private Integrator integrator;
    private double[][][] simulation;


    private final boolean checkStopConditions;
    private double energyErrorBound = 1e-5;
    private double distanceBound = 20;
    private double timestepSizeBound = 1e-6;

    /**
     * @param bodies Array of Body objects
     * @param N Number of iterations
     * @param dt Time step
     * 
     */
    public Simulation(Body[] bodies, int N, double dt) {
        this.bodies = bodies;
        this.n = bodies.length;
        this.N = N;
        this.dt = dt;
        this.G = 1;
        this.integratorType = IntegratorType.SYMPLECTIC_EULER;
        this.checkStopConditions = false;
        this.useVariableTimestep = false;
        this.integrator = new Integrator(integratorType, useVariableTimestep);
    }

    public Simulation(Body[] bodies, int N, double dt, double G, IntegratorType integratorType, boolean useVariableTimestep, boolean checkStopConditions){
        this.bodies = bodies;
        this.n = bodies.length;
        this.N = N;
        this.dt = dt;
        this.G = G;
        this.integratorType = integratorType;
        this.checkStopConditions = checkStopConditions;
        this.useVariableTimestep = useVariableTimestep;
        this.integrator = new Integrator(integratorType, useVariableTimestep);
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

    public void run(){
        double[][][] simulation = new double[N][6][n];

        // Set up arrarys for optional calculations
        Vector[] centreOfMass = new Vector[N];
        double[] potentialEnergy = new double[N];
        double[] kineticEnergy = new double[N];
        Vector[] angularMomentum = new Vector[N];
        Vector[] linearMomentum = new Vector[N];

        // ----- Main Time Loop ----- \\
        for (int i = 0; i < this.N; i++) {
            // Record all optional calculations
            centreOfMass[i] = calculateCentreOfMass();
            potentialEnergy[i] = calculatePotentialEnergy();
            kineticEnergy[i] = calculateKineticEnergy();
            angularMomentum[i] = calculateAngularMomentum();
            linearMomentum[i] = calclateLinearMomentum();

            // Record the current timestep of the simulation
            for (int p = 0; p < this.n; p++) {
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
            double usedTimestep = integrator.Integrate(bodies, dt);
            elapsedTime += usedTimestep;

            // Check if the simulation should stop
            if (checkStopConditions && i % 10 == 1) {
                checkStopConditions(potentialEnergy, kineticEnergy, centreOfMass, i, usedTimestep, elapsedTime);
            }
        }

        // Write the simulation to a global variable for convenience
        this.simulation = simulation;
        writeSimulationToFiles();
    }

    private void checkStopConditions(double[] potentialEnergy, double[] kineticEnergy, Vector[] centreOfMass, int timestep, double usedTimestep, double elapsedTime) {

        // Check if the energy error is within the bound
        double totalEnergy = potentialEnergy[timestep] + kineticEnergy[timestep];
        double energyError = Math.abs((totalEnergy - (potentialEnergy[0] + kineticEnergy[0])) / (potentialEnergy[0] + kineticEnergy[0]));
        if (energyError < this.energyErrorBound) {
            System.out.println("Simulation terminated after exceeding energy error bound");
            System.out.println("Energy error bound: \t" + this.energyErrorBound);
            System.out.println("Energy error: \t" + energyError);
            System.out.println("Time reached: \t" + elapsedTime);
            throw new RuntimeException("Energy error bound exceeded");
        }

        // Check if the distance between the centre of mass and the origin is within the bound
        double distance = centreOfMass[timestep].norm();
        if (distance > this.distanceBound) {
            System.out.println("Simulation terminated after exceeding distance bound");
            System.out.println("Distance bound: \t" + this.distanceBound);
            System.out.println("Distance: \t" + distance);
            System.out.println("Time reached: \t" + elapsedTime);
            throw new RuntimeException("Distance bound exceeded");
        }

        // Check if the timestep size is within the bound
        if (useVariableTimestep) {
            if (usedTimestep < this.timestepSizeBound) {
                System.out.println("Simulation terminated after exceeding timestep size bound");
                System.out.println("Timestep size bound: \t" + this.timestepSizeBound);
                System.out.println("Timestep size: \t" + usedTimestep);
                System.out.println("Time reached: \t" + elapsedTime);
                throw new RuntimeException("Timestep size bound exceeded");
            }
        }
    }

    private void writeSimulationToFiles(){

        // Create the output directory if it doesn't exist, or clear it if it does
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

        // Start by writing the simulation settings to a file
        try(FileWriter writer = new FileWriter("JavaSimulation\\Outputs\\simulationSettings.csv")){
            writer.append(this.N + "," + this.dt + "," + this.n + "," + this.G + "\n");
        } catch (FileNotFoundException e) {
            System.err.println("Setting file not found");
        } catch (IOException e) {
            System.err.println("Something went wrong writing to file: " + e.getMessage());
        }

        // Then write each body to its own file
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

    // ---------- Simulation optional Calculations ---------- \\

    public double calculatePotentialEnergy(){
        double potentialEnergy = 0.0;
        for (int p = 0; p < this.n; p++) {
            potentialEnergy += this.bodies[p].getPotentialEnergy(this.bodies, this.G);
        }
        return potentialEnergy;
    }

    public double calculateKineticEnergy() {
        double kineticEnergy = 0.0;
        for (int p = 0; p < this.n; p++) {
            kineticEnergy += this.bodies[p].getKineticEnergy();
        }
            
        return kineticEnergy;
    }

    public double calculateTotalMass() {
        double totalMass = 0;
        for (int i = 0; i < this.n; i++) {
            totalMass += this.bodies[i].getMass();
        }
        return totalMass;

    }

    public Vector calculateCentreOfMass() {
        Vector centreOfMass = new Vector();
        for (int p = 0; p < this.n; p++) {
            centreOfMass = centreOfMass.add(Vector.multiply(this.bodies[p].getPosition(), this.bodies[p].getMass()));
        }
        centreOfMass.divide(this.calculateTotalMass());
        
        return centreOfMass;
    }

    public Vector calculateAngularMomentum() {
        Vector L = new Vector();
        for (int p = 0; p < this.n; p++) {
            Vector pos_vel_cross = Vector.cross(this.bodies[p].getPosition(), this.bodies[p].getVelocity());

            L.add(Vector.multiply(pos_vel_cross, this.bodies[p].getMass()));
        }
        return L;
    }

    public Vector calclateLinearMomentum() {
        Vector P = new Vector();
        for (int p = 0; p < this.n; p++) {
            P.add(Vector.multiply(this.bodies[p].getVelocity(), this.bodies[p].getMass()));
        }
        
        return P;
    }


}