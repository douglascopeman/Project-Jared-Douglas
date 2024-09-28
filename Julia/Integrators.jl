module Integrators

export symplecticEuler

"""
    symplecticEuler(spaceData::Vector{Body})

The symplectic euler numerical method, calculates the velocity at timestep n+1 using it along with the n position step
to calculate the position at n+1
"""
function symplecticEuler(bodies, acceleration, dt)    
    # Updates the entries in each isntance of the class
    for (i,body) in enumerate(bodies)
        body.vel += dt * acceleration[i,:]   # x-velocity 
        body.pos += dt * body.vel      # x-position

    end
    return bodies
end 

end