module ModelSpace

export Body, spaceData, simLength, G

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
    
end