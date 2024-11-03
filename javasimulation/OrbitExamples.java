package javasimulation;

import java.util.HashMap;

public class OrbitExamples {

    // Remember to ammend HashMap at the end of the file when adding new examples

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

    public static HashMap<String, Body[]> orbitExampleMap = new HashMap<String, Body[]>() {{
        put("circular", OrbitExamples.circular);
        put("elliptical", elliptical);
        put("longElliptical", longElliptical);
        put("figureEight", figureEight);
        put("figureEightMoving", figureEightMoving);
    }};
}
