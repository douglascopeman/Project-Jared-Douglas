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
                          save_plots=False
                          )

# plotter.plot()
plotter.shape_space()

# plotter.plot_simulation_shape_space("javasimulation\\Outputs\\shapeSpaceMatrix.csv")

