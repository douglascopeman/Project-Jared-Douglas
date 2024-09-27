module ModelSpace
export Body

# define the Body "structure"
Base.@kwdef mutable struct Body
    pos::Vector{Float64} = [0,0,0]

    vel::Vector{Float64} = [0,0,0]

    mass::Float64 = 5.972e24
end
    
end