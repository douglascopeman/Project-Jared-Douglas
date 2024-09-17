using Plots
gr()
using BenchmarkTools
using LinearAlgebra
using Combinatorics


# Constants
G = 6.67430e-11

# define the Body "structure"
Base.@kwdef mutable struct Body
    dt::Float64 = 2000
    t::Float64 = 0.0
    x::Float64 = 0
    y::Float64 = 0
    z::Float64 = 0
    dx::Float64 = 0
    dy::Float64 = 0
    dz::Float64 = 0
    mass::Float64 = 5.972e24
end

"""
    symplecticEuler(spaceData::Vector{Body})

The symplectic euler numerical method, calculates the velocity at timestep n+1 using it along with the n position step
to calculate the position at n+1
"""
function symplecticEuler(spaceData, α)    
    # Updates the entries in each isntance of the class
    for (i,p) in enumerate(spaceData)
        p.t += p.dt             # Time update
        p.dx += p.dt * α[i,1]   # x-velocity 
        p.dy += p.dt * α[i,2]   # y-velocity
        p.dz += p.dt * α[i,3]   # z-velocity
        p.x += p.dt * p.dx      # x-position
        p.y += p.dt * p.dy      # y-position
        p.z += p.dt * p.dz      # z-position
    end
end

"""
    accelerationCalc(spaceData)

Compute the acceleration between n bodies in the x,y and z axes. The output will be a nx3 array.
"""
function accelerationCalc(spaceData)
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
function Hamiltonian(spaceData)
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
function simulation(spaceData, simLength)
    nBodies = length(spaceData)
    simulation = zeros(Float64, (simLength, 3, nBodies))  # Simulation length by axes by number of bodies
    modelHamiltonian = zeros(Float64, simLength)
    for i=1:simLength
        α = accelerationCalc(spaceData)     # Gives acceleration values at timestep n for all bodies and all axes
        symplecticEuler(spaceData, α) # Updates each instance of the class
        for p=1:nBodies
            simulation[i,:,p] = [spaceData[p].x, spaceData[p].y, spaceData[p].z]    # Fills relevent timestep and body in the array with the position data
        end

        modelHamiltonian[i] = Hamiltonian(spaceData)    # Current velocity data used to calculate the current hamiltonian
    end
    return simulation, modelHamiltonian
end



# Defining the model space
moon = Body()
earth = Body()
p3 = Body()

moon.x = -3.93e8
moon.dy = 1e3
moon.mass = 7.34767309e22
earth.dz = 0.00001
spaceData = [moon, earth]
simLength = 1200
@assert simLength > 250
    

model, modelHamiltonian = simulation(spaceData, simLength)


# Plotting

@userplot ModelPlot
@recipe function f(mp::ModelPlot)
    x, y, i = mp.args
    n = length(x) 
    inds = circshift(1:n, 1-i)
    seriesalpha --> [zeros(n-250); range(0, 1, length = 250)]
    aspect_ratio --> 1
    linewidth --> [ones(n-25); (range(1, 10, length = 25))]
    label --> false
    xlims --> (-4e8, 4e8)
    ylims --> (-4e8, 4e8)
    x[inds], y[inds]
end

anim = @animate for i ∈ 1:5:simLength
    modelplot(model[:,1,1], model[:,2,1], i, c=:thermal)
    for p = 2:length(spaceData)
        modelplot!(model[:,1,p], model[:,2,p], i, c=:thermal, markersize = 5)
    end
    #annotate!("time= $(rpad(round(i/3; digits=2),4,"0")) s")
    annotate!(-3.5e8, 3.5e8, "Hamiltonian= $(rpad(round(modelHamiltonian[i]; digits=2),4,"0"))")
end

gif(anim, fps = 30)