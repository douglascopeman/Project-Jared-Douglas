module Simulation

export runSimulation, calculateAccelerations

include("Integrators.jl")


using LinearAlgebra
using Combinatorics
using DelimitedFiles

G = 1

# function f()
#     print(a)
    
# end
    
"""
    accelerationCalc(spaceData)

Compute the acceleration between n bodies in the x,y and z axes. The output will be a nx3 array.
"""
function calculateAccelerations(bodies)
    n = length(bodies)
    acceleration = zeros(Float64, (n,3))
    for (i,body) in enumerate(bodies)
        acceleration[i,:] = sum(
            -G*otherBody.mass*(body.pos - otherBody.pos)/((norm(body.pos - otherBody.pos))^3)
            for otherBody in setdiff(bodies, [body]))
    end
    return acceleration
end

# """
#     Hamiltonian(spaceData::Vector{Body})

# The Hamiltonian is the total amount of energy in the system, gravitational plus kinetic.
# The function takes the spaceData and outputs both the value of the Hamiltonian, H, for all 
# timesteps and the change in the Hamiltonian value over time, ΔH
# """
# function Hamiltonian(spaceData::Vector{Body})
#     nBodies = length(spaceData)
#     Ω = collect(combinations(1:nBodies, 2))     # nBodies choose 2
#     hamiltonian = sum(                                                              # gravitational part of the hamiltonian
#         -G * spaceData[Ω[i][1]].mass * spaceData[Ω[i][2]].mass / (norm(spaceData[Ω[i][1]].pos - spaceData[Ω[i][2]].pos)) 
#         for i in length(Ω)) +
#             sum((1/2) * spaceData[i].mass * norm(                                   # kinetic part of the hamiltonian
#                 spaceData[i].vel)^2 for i in 1:nBodies)  
#     return hamiltonian
# end

"""
    simulation(spaceData::Vector{Body}, simLength::Int64)

Builds a 3 dimensional array filled with the 3 axes position data of every body for the length of the simulaiton
"""
function runSimulation(bodies, N, dt::Float64)
    nBodies = length(bodies)
    simulation = zeros(Float64, (N, 6, nBodies))  # Simulation length by axes by number of bodies

    for k=1:N
        accelerations = calculateAccelerations(bodies)     # Gives acceleration values at timestep n for all bodies and all axes
        bodies = Integrators.symplecticEuler(bodies, accelerations, dt) # Updates each instance of the class
        for (i,body) in enumerate(bodies)
            simulation[k,:,i] = [body.pos; body.vel]   # Fills relevent timestep and body in the array with the position data
        end
    end
    
    for body=1:nBodies
        path = raw"Outputs/output" * string(body) * raw".csv"
        writedlm(path, simulation[:,:,body], ',')   
    end
end


end