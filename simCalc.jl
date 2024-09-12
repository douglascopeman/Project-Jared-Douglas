include("modelSpace.jl")
using LinearAlgebra
using BenchmarkTools
using Combinatorics
using .ModelSpace


module SimCalc

export simulation

"""
    accelerationCalc(spaceData)

Compute the acceleration between n bodies in the x,y and z axes. The output will be a nx3 array.
"""
function accelerationCalc(spaceData::Vector{Main.ModelSpace.Body})
    n = length(spaceData)
    acceleration = zeros(Float64, (n,3))
    # This is an inneficient way to do this, will have to fix this at somepoint
    for body=1:n
        r1 = [spaceData[body].x, spaceData[body].y, spaceData[body].z]
        acceleration[body,:] = sum(
            -G*spaceData[i].mass*(r1 - [spaceData[i].x, spaceData[i].y, spaceData[i].z])/((norm(r1 - [spaceData[i].x, spaceData[i].y, spaceData[i].z]))^3)
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
function Hamiltonian(spaceData::Vector{Main.ModelSpace.Body})
    nBodies = length(spaceData)
    Ω = collect(combinations(1:nBodies, 2))     # nBodies choose 2
    hamiltonian = sum(                                                              # gravitational part of the hamiltonian
        -G * spaceData[Ω[i][1]].mass * spaceData[Ω[i][2]].mass / (norm(
            [spaceData[Ω[i][1]].x, spaceData[Ω[i][1]].y, spaceData[Ω[i][1]].z] -
            [spaceData[Ω[i][2]].x, spaceData[Ω[i][2]].y, spaceData[Ω[i][2]].z])) 
            for i in length(Ω)) +
            sum((1/2) * spaceData[i].mass * norm(                                   # kinetic part of the hamiltonian
                [spaceData[i].dx, spaceData[i].dy, spaceData[i].dz])^2 for i in 1:nBodies)  
    return hamiltonian
end

"""
    simulation(spaceData::Vector{Body}, simLength::Int64)

Builds a 3 dimensional array filled with the 3 axes position data of every body for the length of the simulaiton
"""
function simulation(spaceData::Vector{Main.ModelSpace.Body}, simLength::Int64)
    nBodies = length(spaceData)
    simulation = zeros(Float64, (simLength, 3, nBodies))  # Simulation length by axes by number of bodies
    modelHamiltonian = zeros(Float64, simLength)
    for i=1:simLength
        symplecticEuler!(spaceData) # Updates each instance of the class
        for p=1:nBodies
            simulation[i,:,p] = [spaceData[p].x, spaceData[p].y, spaceData[p].z]    # Fills relevent timestep and body in the array with the position data
        end

        modelHamiltonian[i] = Hamiltonian(spaceData)    # Current velocity data used to calculate the current hamiltonian
    end
    return simulation, modelHamiltonian
end

end