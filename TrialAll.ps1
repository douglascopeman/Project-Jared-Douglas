param ($N = $(throw "N parameter is required for Trial All."), $dt = $(throw "dt parameter is required for Trial All."))

Write-Host $PSScriptRoot
Write-Host "Running Python Simulation"
python $PSScriptRoot\Python\Run.py $N $dt
Write-Host "Done!"

Write-Host "Running Java Simulation"
& $PSScriptRoot\JavaCompileAndRun.ps1 $N $dt

Write-Host "Running Julia Simulation"
julia $PSScriptRoot\Julia\Run.jl $N $dt
Write-Host "Done!"

Write-Host "Running C++ Simulation"
& $PSScriptRoot\CppCompileAndRun.ps1 $N $dt