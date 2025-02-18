import Plotter



plotter = Plotter.Plotter("javasimulation\\Outputs", 
                          run_fast=True, 
                          plot_centre_of_mass=False, 
                          plot_energy=False, 
                          plot_energy_error=False, 
                          plot_angular_momentum_error = False, 
                          plot_linear_momentum_error=False, 
                          plot_3D=False,
                          x_label="Time",
                          save_extra_plots=False
                          )

plotter.plot(save=False)
# plotter.shape_space(save=False)
# plotter.plot_simulation_shape_space("javasimulation\\Outputs\\shapeSpaceMatrix.csv", save=False)
# plotter.count_orbits()


