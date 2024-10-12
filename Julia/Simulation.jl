module Simulation

export runSimulation

include("Integrators.jl")

using LinearAlgebra
using Combinatorics
using DelimitedFiles
using .Integrators: symplecticEuler, yoshida

G = 1

"""
    simulation(spaceData::Vector{Body}, simLength::Int64)

Builds a 3 dimensional array filled with the 3 axes position data of every body for the length of the simulaiton
"""
function runSimulation(bodies, N::Int64, dt::Float64, integrator = symplecticEuler)
    nBodies = length(bodies)
    simulation = zeros(Float64, (N, 6, nBodies))  # Simulation length by axes by number of bodies
    path = raw"Julia/Outputs/"

    for k=1:N
        bodies = integrator(bodies, dt) # Updates each instance of the class
        for (i,body) in enumerate(bodies)
            simulation[k,:,i] = [body.position; body.velocity]   # Fills relevent timestep and body in the array with the position data
        end
    end
    
    for body=1:nBodies
        pathBodies = path* raw"output" * string(body-1) * raw".csv"
        writedlm(pathBodies, simulation[:,:,body], ',')   
    end

    writedlm(path * raw"simulationSettings.csv", [N, dt, nBodies, G], ',')
end


end