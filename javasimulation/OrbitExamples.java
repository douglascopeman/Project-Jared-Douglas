package javasimulation;

import java.util.HashMap;

public class OrbitExamples {

    // Remember to amend HashMap at the end of the file when adding new examples

    public static Body[] circular = {
        new Body(new Vector(1, 0, 0), new Vector(0, 0.5, 0)), 
        new Body(new Vector(-1, 0, 0), new Vector(0, -0.5, 0))
    };

    public static Body[] elliptical = {
        new Body(new Vector(1, 0, 0), new Vector(0, 0.6, 0)), 
        new Body(new Vector(-1, 0, 0), new Vector(0, -0.6, 0))
    };

    public static Body[] longElliptical = {
        new Body(new Vector(1, 0, 0), new Vector(0, 0.1, 0)), 
        new Body(new Vector(-1, 0, 0), new Vector(0, -0.1, 0))
    };

    private static double v = Math.sqrt(5.0/4.0);
    public static Body[] eulerThree = {
        new Body(new Vector(1, 0, 0), new Vector(0, v, 0)),
        new Body(new Vector(), new Vector()),
        new Body(new Vector(-1, 0, 0), new Vector(0, -v, 0))
    };


    private static double b = 1.5197;
    public static Body[] lagrangeThree = {
        new Body(new Vector(1, 0, 0), new Vector(0, 0.5 * b, 0)),
        new Body(new Vector(-0.5, Math.sqrt(3) / 2, 0), new Vector(-Math.sqrt(3) / 4 * b, -0.25 * b, 0)),
        new Body(new Vector(-0.5, -Math.sqrt(3) / 2, 0), new Vector(Math.sqrt(3) / 4 * b, -0.25 * b, 0))
    };

    public static Body[] figureEight = {
        new Body(new Vector(0.97000436,-0.24308753,0), new Vector(0.46620368,0.43236573,0)),
        new Body(new Vector(), new Vector(-0.93240737,-0.86473146,0)),
        new Body(new Vector(-0.97000436,0.24308753,0), new Vector(0.46620368,0.43236573,0))
    };

    public static Body[] figureEightMoving = {
        new Body(new Vector(-0.97000436,0.24308753,0), new Vector(0.46620368,0.43236573,10)),
        new Body(new Vector(), new Vector(-0.93240737,-0.86473146,10)),
        new Body(new Vector(0.97000436,-0.24308753,0), new Vector(0.46620368,0.43236573,10))
    };




    // ============== Figure 8 orbits with different energy ========================
    public static Body[] figureEightEnergy10PercentMore = {
        new Body(new Vector(0.97000436,-0.24308753,0), new Vector(0.4903179,0.45472969,0)),
        new Body(new Vector(), new Vector(-0.98063579,-0.90945938,0)),
        new Body(new Vector(-0.97000436,0.24308753,0), new Vector(0.4903179 ,0.45472969,0))
    };
    public static Body[] figureEightEnergy10PercentLess = {
        new Body(new Vector(0.97000436,-0.24308753,0), new Vector(0.44077217,0.40878009,0)),
        new Body(new Vector(), new Vector(-0.88154434,-0.81756017,0)),
        new Body(new Vector(-0.97000436,0.24308753,0), new Vector(0.44077217 ,0.40878009,0))
    };
    public static Body[] figureEightEnergy20PercentLess = {
        new Body(new Vector(0.97000436,-0.24308753,0), new Vector(0.41378054,0.38374757,0)),
        new Body(new Vector(), new Vector(-0.82756109,-0.76749513,0)),
        new Body(new Vector(-0.97000436,0.24308753,0), new Vector(0.41378054 ,0.38374757,0))
    };
    public static Body[] figureEightEnergy20PercentMore = {
        new Body(new Vector(0.97000436,-0.24308753,0), new Vector(0.5133005,0.47604418,0)),
        new Body(new Vector(), new Vector(-1.02660101,-0.95208835,0)),
        new Body(new Vector(-0.97000436,0.24308753,0), new Vector(0.5133005 ,0.47604418,0))
    };



    public static HashMap<String, Body[]> orbitExampleMap = new HashMap<String, Body[]>() {{
        put("circular", OrbitExamples.circular);
        put("elliptical", elliptical);
        put("longElliptical", longElliptical);
        put("eulerThree", eulerThree);
        put("lagrangeThree", lagrangeThree);
        put("figureEight", figureEight);
        put("figureEightMoving", figureEightMoving);
        put("figureEightEnergy10PercentMore", figureEightEnergy10PercentMore);
        put("figureEightEnergy10PercentLess", figureEightEnergy10PercentLess);
        put("figureEightEnergy20PercentMore", figureEightEnergy20PercentMore);
        put("figureEightEnergy20PercentLess", figureEightEnergy20PercentLess);
    }};
}
