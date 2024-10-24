package javasimulation;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class Simulation {

    private final Body[] bodies;
    private final int n;
    private final int N;
    private final double dt;
    private final IntegratorType integratorType;

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
        this.integratorType = IntegratorType.SYMPLECTIC_EULER;
        
    }

    public Simulation(Body[] bodies, int N, double dt, IntegratorType integratorType){
        this.bodies = bodies;
        this.n = bodies.length;
        this.N = N;
        this.dt = dt;
        this.integratorType = integratorType;
    }

    public void run(){
        double[][][] simulation = new double[this.N][6][this.n];
        Integrator integrator = new Integrator(this.integratorType, false);
        for (int i = 0; i < this.N; i++) {
            integrator.Integrate(this.bodies, this.dt);
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
        }

        WriteToFiles(simulation);
    }

    private void WriteToFiles(double[][][] simulation){

        try(FileWriter writer = new FileWriter("JavaSimulation\\Outputs\\simulationSettings.csv")){
            writer.append(this.N + "," + this.dt + "," + this.n + ", 1\n");
        } catch (FileNotFoundException e) {
            System.err.println("Setting file not found");
        } catch (IOException e) {
            System.err.println("Something went wrong writing to file: " + e.getMessage());
        }

        for (int bodyNum = 0; bodyNum < this.n; bodyNum++) {
            try (FileWriter writer = new FileWriter("JavaSimulation\\Outputs\\output" + bodyNum + ".csv")) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < this.N; i++) {
                    sb.append(simulation[i][0][bodyNum])
                    .append(",")
                    .append(simulation[i][1][bodyNum])
                    .append(",")
                    .append(simulation[i][2][bodyNum])
                    .append(",")
                    .append(simulation[i][3][bodyNum])
                    .append(",")
                    .append(simulation[i][4][bodyNum])
                    .append(",")
                    .append(simulation[i][5][bodyNum])
                    .append("\n");
                }
                writer.write(sb.toString());
            } catch (FileNotFoundException e) {
                System.err.println("Body " + bodyNum +  " file not found");
            } catch (IOException e) {
                System.err.println("Something went wrong writing to file: " + e.getMessage());
            }
        }
    }

}