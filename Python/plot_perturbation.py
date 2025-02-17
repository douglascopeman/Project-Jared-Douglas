import Perturbation_plotter
import ThreeDimensionalPerturbationPlot


perturbation_plot = Perturbation_plotter.Perturbation_plotter("Python\\Outputs")

#perturbation_plot.plot_time("timeMatrix")
#perturbation_plot.plot_stop_codes("stopCodeMatrix")
# perturbation_plot.count_stop_matrix("stopCodeMatrix")
perturbation_plot.plot_stability("stabilityMatrix", save=False, save_dbl_click=True)

perturbation_plot.plot_stop_codes_gradient("timeMatrix", "stopCodeMatrix")
perturbation_plot.plot_stop_codes_gradient("timeMatrix", "stopCodeMatrix", "stabilityMatrix")


pertubation_3dplot = ThreeDimensionalPerturbationPlot.ThreeDimensionalPerturbationPlot("C:\\Users\\Douglas\\OneDrive - University of Edinburgh\\Uni\\Project-Jared-Douglas\\PaperTrail\\Pertubations\\MathsServers\\400x400x400 Angular Momentum 353h")
pertubation_3dplot.animate(is_energy=False)

print("Done!")