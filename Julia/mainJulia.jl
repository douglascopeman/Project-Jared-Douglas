include("src/NBodyProblem.jl")


# Defining the model space
moon = Body()
earth = Body()
p3 = Body()

moon.pos = [-3.93e8, 0, 0]
moon.vel = [0, 1e3, 0]
moon.mass = 7.34767309e22

p3.pos = [3.93e8, 0, 0]
p3.vel = [0, -1e3, 0]
p3.mass = 7.34767309e22

spaceData = [moon, earth, p3]
simLength = 1000000
@assert simLength > 250

dt = 20

@btime
    

model, modelHamiltonian = runSimulation(spaceData, simLength, dt)

animation(model, (simLength ÷ 1000), modelHamiltonian)

 plotHamiltonian(modelHamiltonian)

# h5write("/tmp/test1.h5", "model", model)

# @testset "NBodyProblem.jl" begin
#     NBodyProblem.simulation(1,2)
# end
