using BenchmarkTools

include("Simulation.jl")
include("Body.jl")

using .Simulation

# Defining the model space
moon = ModelSpace.Body(position = [1,0,0], velocity =[0,0.6,0])
earth = ModelSpace.Body(position = [-1,0,0], velocity = [0,-0.6,0])

bodies = [moon, earth]
simLength = 10000
dt = 0.01    

@btime Simulation.runSimulation(bodies, simLength, dt)

