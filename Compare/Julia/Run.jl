using BenchmarkTools

include("Simulation.jl")
include("Body.jl")

using .Simulation
using Dates

# Defining the model space
body0 = ModelSpace.Body(position = [1,0,0], velocity =[0,0.6,0])
body1 = ModelSpace.Body(position = [-1,0,0], velocity = [0,-0.6,0])

bodies = [body0, body1]
simLength = 10000
dt = 0.2  

if length(ARGS) > 0
    simLength = parse(Int64, ARGS[1])
    dt = parse(Float64, ARGS[2])
end


startTime = now()
    Simulation.runSimulation(bodies, simLength, dt)
endTime = now()

elapsedTime = (endTime - startTime) 
println("\tTime: ", elapsedTime)

