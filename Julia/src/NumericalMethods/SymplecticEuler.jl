module SymplecticEuler

export symplecticEuler

"""
    symplecticEuler(spaceData::Vector{Body})

The symplectic euler numerical method, calculates the velocity at timestep n+1 using it along with the n position step
to calculate the position at n+1
"""
function symplecticEuler(spaceData, α, dt)    
    # Updates the entries in each isntance of the class
    for (i,p) in enumerate(spaceData)
        p.pos += dt * p.vel      # x-position
        p.vel += dt * α[i,:]   # x-velocity 

    end
end 

end