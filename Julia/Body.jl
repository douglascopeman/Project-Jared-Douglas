module ModelSpace
export Body, calculateAccelerations

using LinearAlgebra

# define the Body "structure"
@kwdef mutable struct Body
    position::Vector{Float64} = [0,0,0]
    velocity::Vector{Float64} = [0,0,0]
    mass::Float64 = 1
    acceleration::Vector{Float64} = [0,0,0]
    G = 1
end
    
"""
    accelerationCalc(spaceData)

Compute the acceleration between n bodies in the x,y and z axes. The output will be a nx3 array.
"""
function calculateAccelerations(bodies)
    n = length(bodies)
    for (i,body) in enumerate(bodies)
        body.acceleration = sum(
            -body.G*otherBody.mass*(body.position - otherBody.position)/((norm(body.position - otherBody.position))^3)
            for otherBody in setdiff(bodies, [body]))
    end
end
end