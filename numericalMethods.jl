module NumericalMethods

export symplecticEuler


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


end