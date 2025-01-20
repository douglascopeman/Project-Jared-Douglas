import Perturbation_plotter
import ThreeDimensionalPerturbationPlot


perturbation_plot = Perturbation_plotter.Perturbation_plotter("javasimulation\\Outputs")

#perturbation_plot.plot_time("timeMatrix")
#perturbation_plot.plot_stop_codes("stopCodeMatrix")
# perturbation_plot.count_stop_matrix("stopCodeMatrix")
perturbation_plot.plot_stability("stabilityMatrix")

# perturbation_plot.plot_stop_codes_gradient("timeMatrix", "stopCodeMatrix")
# perturbation_plot.plot_stop_codes_gradient("timeMatrix", "stopCodeMatrix", "stabilityMatrix")


# pertubation_3dplot = ThreeDimensionalPerturbationPlot.ThreeDimensionalPerturbationPlot("C:\\Users\\Douglas\\Downloads\\Outputs")
# pertubation_3dplot.animate()

print("Done!")