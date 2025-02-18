import Perturbation_plotter
import ThreeDimensionalPerturbationPlot


# perturbation_plot = Perturbation_plotter.Perturbation_plotter("python\\Outputs")
perturbation_plot = Perturbation_plotter.Perturbation_plotter("testing_perturbations")

#perturbation_plot.plot_time("timeMatrix")
#perturbation_plot.plot_stop_codes("stopCodeMatrix")
# perturbation_plot.count_stop_matrix("stopCodeMatrix")
# perturbation_plot.plot_stability("stabilityMatrix", save=False, save_dbl_click=True)

# perturbation_plot.plot_stop_codes_gradient("timeMatrix", "stopCodeMatrix")
perturbation_plot.plot_stop_codes_stab_gradient("timeMatrix", "stopCodeMatrix", "stabilityMatrix")


# pertubation_3dplot = ThreeDimensionalPerturbationPlot.ThreeDimensionalPerturbationPlot("C:\\Users\\Douglas\\OneDrive - University of Edinburgh\\Uni\\Project-Jared-Douglas\\PaperTrail\\Pertubations\\MathsServers\\400x400x400 Angular Momentum 353h")
# pertubation_3dplot.plot_slice([180,190, 200,210, 220,230], dxda=True)
# pertubation_3dplot.plot_slice([180,190, 200,210,220,230], dyda=True)


print("Done!")