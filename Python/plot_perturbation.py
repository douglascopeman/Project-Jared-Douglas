import Perturbation_plotter
import ThreeDimensionalPerturbationPlot


perturbation_plot = Perturbation_plotter.Perturbation_plotter("Python\\Outputs")

#perturbation_plot.plot_time("timeMatrix")
#perturbation_plot.plot_stop_codes("stopCodeMatrix")
# perturbation_plot.count_stop_matrix("stopCodeMatrix")
perturbation_plot.plot_stability("stabilityMatrix", save=False, save_dbl_click=False)

# perturbation_plot.plot_stop_codes_gradient("timeMatrix", "stopCodeMatrix")
# perturbation_plot.plot_stop_codes_gradient("timeMatrix", "stopCodeMatrix", "stabilityMatrix")


# pertubation_3dplot = ThreeDimensionalPerturbationPlot.ThreeDimensionalPerturbationPlot("C:\\Users\\Douglas\\Downloads\\Outputs")
# pertubation_3dplot.animate(is_energy=True)

print("Done!")