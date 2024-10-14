using BenchmarkTools

include("Simulation.jl")
include("Body.jl")

using .Simulation

# Defining the model space
moon = ModelSpace.Body(position = [1,0,0], velocity =[0,0.5,0])
earth = ModelSpace.Body(position = [-1,0,0], velocity = [0,-0.5,0])

bodies = [moon, earth]
simLength = 10000
dt = 0.01  

if length(ARGS) > 0
    simLength = parse(Int64, ARGS[1])
    dt = parse(Float64, ARGS[2])
end

@btime Simulation.runSimulation(bodies, simLength, dt)

