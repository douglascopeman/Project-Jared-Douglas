using Plots
using CalculusWithJulia

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
    n = size(spaceData)[1]
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



# function symplecticEuler!(spaceData)
#     p.t += p.dt
#     dx(t) = sin.(t)
#     dy(t) = cos.(t)
#     dz(t) = 0.2
#     p.x += p.dt * (dx(p.t) + (p.dt * dx'(p.t)))
#     p.y += p.dt * (dy(p.t) + (p.dt * dy'(p.t)))
#     p.z += p.dt * (dz(p.t) + (p.dt * dz'(p.t)))
# end

function symplecticEuler!(spaceData::Vector{Body})
    α = accelerationCalc(spaceData)
    
    for (i,p) in enumerate(spaceData)
        p.t += p.dt
        p.dx += p.dt * α[i,1]
        p.dy += p.dt * α[i,2]
        p.dz += p.dt * α[i,3]
        p.x += p.dt * p.dx
        p.y += p.dt * p.dy
        p.z += p.dt * p.dz
        print(α[i,1])
    end
end

# Defining the model space, currently working with 2 bodies

p1 = Body()
p2 = Body()
p3 = Body()

p1.x = -3.93e8
p1.dy = 1e3
p1.mass = 7.34767309e22
p2.dz = 0.00001
spaceData = [p1, p2]




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

# build an animated gif by pushing new points to the plot, saving every 10th frame
@gif for i=1:2000
    symplecticEuler!(spaceData)
    push!(plt, spaceData[1].x, spaceData[1].y, spaceData[1].z)
    push!(plt, spaceData[2].x, spaceData[2].y, spaceData[2].z)
end every 10

