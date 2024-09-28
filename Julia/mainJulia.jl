using BenchmarkTools

include("Simulation.jl")
include("Body.jl")

using .Simulation

# Defining the model space
moon = ModelSpace.Body([1,0,0], [0,0.6,0],1)
earth = ModelSpace.Body([-1,0,0], [0,-0.6,0], 1)

bodies = [moon, earth]
simLength = 1000
dt = 0.1    


@benchmark Simulation.runSimulation(bodies, simLength, dt)

