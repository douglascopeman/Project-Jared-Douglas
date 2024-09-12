include("NumericalMethods.jl")
include("modelSpace.jl")
include("simCalc.jl")

using Plots
gr()
using LinearAlgebra
using BenchmarkTools
using Combinatorics
using .NumericalMethods
using .ModelSpace


model, modelHamiltonian = simulation(spaceData, simLength)


# Plotting

@userplot ModelPlot
@recipe function f(mp::ModelPlot)
    x, y, i = mp.args
    n = length(x) 
    inds = circshift(1:n, 1-i)
    seriesalpha --> [zeros(n-250); range(0, 1, length = 250)]
    aspect_ratio --> 1
    linewidth --> [ones(n-25); (range(1, 10, length = 25))]
    label --> false
    xlims --> (-4e8, 4e8)
    ylims --> (-4e8, 4e8)
    x[inds], y[inds]
end

anim = @animate for i ∈ 1:5:simLength
    modelplot(model[:,1,1], model[:,2,1], i, c=:thermal)
    for p = 2:length(spaceData)
        modelplot!(model[:,1,p], model[:,2,p], i, c=:thermal, markersize = 5)
    end
    #annotate!("time= $(rpad(round(i/3; digits=2),4,"0")) s")
    annotate!(-3.5e8, 3.5e8, "Hamiltonian= $(rpad(round(modelHamiltonian[i]; digits=2),4,"0"))")
end

gif(anim, fps = 30)