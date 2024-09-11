using Plots
gr()
using LinearAlgebra
using BenchmarkTools

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
    ForceCalc(spaceData)

Compute the acceleration between n bodies in the x,y and z axes. The output will be a nx3 array.
"""
function accelerationCalc(spaceData::Vector{Body})
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
    symplecticEuler!(spaceData::Vector{Body})

The symplectic euler numerical method, calculates the velocity at timestep n+1 using it along with the n position step
to calculate the position at n+1
"""
function symplecticEuler!(spaceData::Vector{Body})
    α = accelerationCalc(spaceData)     # Gives acceleration values at timestep n for all bodies and all axes
    
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
    simulation(spaceData::Vector{Body}, simLength::Int64)

Builds a 3 dimensional array filled with the 3 axes position data of every body for the length of the simulaiton
"""
function simulation(spaceData::Vector{Body}, simLength::Int64)
    nBodies = length(spaceData)
    simulation = zeros(Float64, (simLength, 3, nBodies))  # Simulation length by axes by number of bodies
    for i=1:simLength
        symplecticEuler!(spaceData) # Updates each instance of the class
        for p=1:nBodies
            simulation[i,:,p] = [spaceData[p].x, spaceData[p].y, spaceData[p].z]    # Fills relevent timestep and body in the array with the position data
        end
    end
    return simulation
end

# Defining the model space

moon = Body()
earth = Body()
p3 = Body()

moon.x = -3.93e8
moon.dy = 0.8e3
moon.mass = 7.34767309e22
earth.dz = 0.00001
spaceData = [moon, earth]
simLength = 1200

model = simulation(spaceData, simLength)

@userplot ModelPlot
@recipe function f(mp::ModelPlot)
    x, y, i = mp.args
    n = length(x)
    halfn = n÷2
    inds = circshift(1:n, 1-i)
    seriesalpha --> range(0, 1, length = n-halfn)
    aspect_ratio --> 1
    label --> false
    x[inds], y[inds]
end

anim = @animate for i ∈ 1:5:simLength
    modelplot(model[:,1,1], model[:,2,1], i, c=:thermal)
end

# initialize a 3D plot with 1 empty series
plt = plot3d(
    1,
    xlim = (-6e8, 6e8),
    ylim = (-6e8, 6e8),
    zlim = (0, 40),
    legend = false,
    marker = 5,
    linecolor = "white"
)



gif(anim, fps = 30)