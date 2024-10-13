module Integrators

export symplecticEuler, yoshida

include("Body.jl")

using .ModelSpace: calculateAccelerations


"""
    symplecticEuler(spaceData::Vector{Body})

The symplectic euler numerical method, calculates the velocity at timestep n+1 using it along with the n position step
to calculate the position at n+1
"""
function symplecticEuler(bodies, dt)    
    # Updates the entries in each isntance of the class
    calculateAccelerations(bodies)
    for (i,body) in enumerate(bodies)
        body.velocity += dt * body.acceleration   # x-velocity 
        body.position += dt * body.velocity      # x-position

    end
    return bodies
end 

function yoshida(bodies,  dt)
    Cs = zeros(Float64, (4))
    Ds = zeros(Float64, (4))
    w0 = -(2^(1/3))/(2-(2^(1/3)))
    w1 = 1/(2-(2^(1/3)))
    Cs[1] = w1/2
    Cs[4] = w1/2
    Cs[2] = (w0+w1)/2
    Cs[3] = (w0+w1)/2
    Ds[1] = w1
    Ds[3] = w1
    Ds[2] = w0

    for i in range(1,4)
        for body in bodies 
            body.position += Cs[i]*dt*body.velocity
        end
        calculateAccelerations(bodies)
        for body in bodies
            body.velocity += Ds[i]*dt*body.acceleration
        end
    end
    return bodies
end

end 