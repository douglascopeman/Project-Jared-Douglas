module SimCalc

import ..SymplecticEuler
import ..ModelSpace

export runSimulation, Body

using LinearAlgebra
using Combinatorics
using .ModelSpace

G = 6.67430e-11

# function f()
#     print(a)
    
# end
    
"""
    accelerationCalc(::Vector{Body})

Compute the acceleration between n bodies in the x,y and z axes. The output will be a nx3 array.
"""
function accelerationCalc(spaceData::Vector{Body})
    n = length(spaceData)
    acceleration = zeros(Float64, (n,3))
    # This is an inneficient way to do this, will have to fix this at somepoint
    for body=1:n
        acceleration[body,:] = sum(
            -G*spaceData[i].mass*(spaceData[body].pos - spaceData[i].pos)/((norm(spaceData[body].pos - spaceData[i].pos))^3)
            for i in setdiff(1:n, [body]))
    end
    return acceleration
end

"""
    Hamiltonian(spaceData::Vector{Body})

The Hamiltonian is the total amount of energy in the system, gravitational plus kinetic.
The function takes the spaceData and outputs both the value of the Hamiltonian, H, for all 
timesteps and the change in the Hamiltonian value over time, ΔH
"""
function Hamiltonian(spaceData::Vector{Body})
    nBodies = length(spaceData)
    Ω = collect(combinations(1:nBodies, 2))     # nBodies choose 2
    hamiltonian = sum(                                                              # gravitational part of the hamiltonian
        -G * spaceData[Ω[i][1]].mass * spaceData[Ω[i][2]].mass / (norm(spaceData[Ω[i][1]].pos - spaceData[Ω[i][2]].pos)) 
        for i in length(Ω)) +
            sum((1/2) * spaceData[i].mass * norm(                                   # kinetic part of the hamiltonian
                spaceData[i].vel)^2 for i in 1:nBodies)  
    return hamiltonian
end

"""
    simulation(spaceData::Vector{Body}, simLength::Int64)

Builds a 3 dimensional array filled with the 3 axes position data of every body for the length of the simulaiton
"""
function runSimulation(spaceData::Vector{Body}, simLength::Int64, dt::Int64)
    nBodies = length(spaceData)
    simulation = zeros(Float64, ((simLength ÷ 1000), 6, nBodies))  # Simulation length by axes by number of bodies
    modelHamiltonian = zeros(Float64, simLength)
    for i=1:simLength
        α = accelerationCalc(spaceData)     # Gives acceleration values at timestep n for all bodies and all axes
        SymplecticEuler.symplecticEuler(spaceData, α, dt) # Updates each instance of the class
        if i % 1000 == 0 
            for p=1:nBodies
                simulation[(i ÷ 1000),:,p] = [spaceData[p].pos; spaceData[p].vel]   # Fills relevent timestep and body in the array with the position data
            end
        end
        modelHamiltonian[i] = Hamiltonian(spaceData)    # Current velocity data used to calculate the current hamiltonian
    end
    return simulation, modelHamiltonian
end


end