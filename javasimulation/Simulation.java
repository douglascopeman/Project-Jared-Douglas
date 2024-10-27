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
    private double[][][] simulation;

    private boolean checkStopConditions = false;
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
    }

    public Simulation(Body[] bodies, int N, double dt, double G, IntegratorType integratorType, boolean checkStopConditions){
        this.bodies = bodies;
        this.n = bodies.length;
        this.N = N;
        this.dt = dt;
        this.G = G;
        this.integratorType = integratorType;
        this.checkStopConditions = checkStopConditions;
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
        double[][][] simulation = new double[this.N][6][this.n];
        Integrator integrator = new Integrator(this.integratorType, false);

        // Set up arrarys for optional calculations
        Vector[] centreOfMass = new Vector[this.N];
        double[] potentialEnergy = new double[this.N];
        double[] kineticEnergy = new double[this.N];
        Vector[] angularMomentum = new Vector[this.N];
        Vector[] linearMomentum = new Vector[this.N];

        // ----- Main Time Loop ----- \\
        for (int i = 0; i < this.N; i++) {
            // Record all optional calculations
            centreOfMass[i] = this.calculateCentreOfMass();
            potentialEnergy[i] = this.calculatePotentialEnergy();
            kineticEnergy[i] = this.calculateKineticEnergy();
            angularMomentum[i] = this.calculateAngularMomentum();
            linearMomentum[i] = this.calclateLinearMomentum();

            // Record the current timestep of the simulation
            for (int p = 0; p < this.n; p++) {
                Vector position = this.bodies[p].getPosition();
                Vector velocity = this.bodies[p].getVelocity();
                simulation[i][0][p] = position.getX();
                simulation[i][1][p] = position.getY();
                simulation[i][2][p] = position.getZ();
                simulation[i][3][p] = velocity.getX();
                simulation[i][4][p] = velocity.getY();
                simulation[i][5][p] = velocity.getZ();
            }

            // Check if the simulation should stop
            if (this.checkStopConditions && i % 10 == 0) {
                checkStopConditions(potentialEnergy, kineticEnergy, centreOfMass, i);
            }

            // Then update the states of all bodies
            integrator.Integrate(this.bodies, this.dt);
        }

        // Write the simulation to a global variable for convenience
        this.simulation = simulation;
        writeSimulationToFiles();
    }

    private void checkStopConditions(double[] potentialEnergy, double[] kineticEnergy, Vector[] centreOfMass, int timestep){

        // Check if the energy error is within the bound
        double totalEnergy = potentialEnergy[timestep] + kineticEnergy[timestep];
        double energyError = Math.abs((totalEnergy - (potentialEnergy[0] + kineticEnergy[0])) / (potentialEnergy[0] + kineticEnergy[0]));
        if (energyError < this.energyErrorBound) {
            System.out.println("Simulation terminated after exceeding energy error bound");
            System.out.println("Energy error bound: \t" + this.energyErrorBound);
            System.out.println("Energy error: \t" + energyError);
            System.out.println("Time reached: \t" + timestep * this.dt);
            throw new RuntimeException("Energy error bound exceeded");
        }

        // Check if the distance between the centre of mass and the origin is within the bound
        double distance = centreOfMass[timestep].norm();
        if (distance > this.distanceBound) {
            System.out.println("Simulation terminated after exceeding distance bound");
            System.out.println("Distance bound: \t" + this.distanceBound);
            System.out.println("Distance: \t" + distance);
            System.out.println("Time reached: \t" + timestep * this.dt);
            throw new RuntimeException("Distance bound exceeded");
        }

        // Check if the timestep size is within the bound
        // TODO: Implement this
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
            writer.append(this.N + "," + this.dt + "," + this.n + ", 1\n");
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