module Plotting

export animation, plotHamiltonian

using Plots
gr()

@userplot ModelPlot
@recipe function f(mp::ModelPlot)
    x, y, i = mp.args
    n = length(x) 
    inds = circshift(1:n, 1-i)
    seriesalpha --> [zeros(n-250); range(0, 1, length = 250)]
    aspect_ratio --> 1
    linewidth --> [ones(n-25); (range(1, 10, length = 25))]
    label --> false
    xlims --> (-7e8, 7e8)
    ylims --> (-7e8, 7e8)
    x[inds], y[inds]
end

function animation(model, simLength::Int64, modelHamiltonian)
    anim = @animate for i ∈ 1:simLength
        #print("\n", i)
        modelplot(model[:,1,1], model[:,2,1], i, c=:thermal)
        for p = 2:(size(model)[3])
            modelplot!(model[:,1,p], model[:,2,p], i, c=:thermal, markersize = 5)
        end
        #annotate!("time= $(rpad(round(i/3; digits=2),4,"0")) s")
        #annotate!(-3e8, 5.5e8, "Hamiltonian= $(rpad(round(modelHamiltonian[i]; digits=2),4,"0"))")
    end 
    
    gif(anim, "anim.gif", fps = 20)
end

function plotHamiltonian(modelHamiltonian)
    plot(modelHamiltonian)
end

end