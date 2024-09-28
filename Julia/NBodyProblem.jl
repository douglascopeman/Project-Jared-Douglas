module NBodyProblem
export runSimulation, Body

include("Body.jl")
include("Integrators.jl")
include("Simulation.jl")
using .SimCalc

end