param ($N = $(throw "N parameter is required for Trial All."), $dt = $(throw "dt parameter is required for Trial All."))

Write-Host $PSScriptRoot
Write-Host "Running Python Simulation"
# $stopwatch = [System.Diagnostics.Stopwatch]::StartNew()
    python $PSScriptRoot\Python\Run.py $N $dt
# $stopwatch.Stop()
# $elapsed = $stopwatch.Elapsed
write-host "Python Done!"
# write-host "Python Elapsed time: $($elapsed.Hours)h $($elapsed.minutes)m $($elapsed.seconds)s."

write-host ""
Write-Host "Running Java Simulation"
# $stopwatch = [System.Diagnostics.Stopwatch]::StartNew()
    & $PSScriptRoot\..\JavaCompileAndRun.ps1 figureEight $N $dt
# $stopwatch.Stop()
# $elapsed = $stopwatch.Elapsed
write-host "Java Done!"
# write-host "Java Elapsed time: $($elapsed.Hours)h $($elapsed.minutes)m $($elapsed.seconds)s."

Write-Host "Running Julia Simulation"
# $stopwatch = [System.Diagnostics.Stopwatch]::StartNew()
    julia $PSScriptRoot\Julia\Run.jl $N $dt
# $stopwatch.Stop()
# $elapsed = $stopwatch.Elapsed
write-host "Julia Done!"
# write-host "Julia Elapsed time: $($elapsed.minutes)m $($elapsed.seconds)s $($elapsed.miliseconds)s"


Write-Host "Running C++ Simulation"
# $stopwatch = [System.Diagnostics.Stopwatch]::StartNew()
    & $PSScriptRoot\CppCompileAndRun.ps1 $N $dt
# $stopwatch.Stop()
# $elapsed = $stopwatch.Elapsed
write-host "C++ Done!"
# write-host "C++ Elapsed time: $($elapsed.Hours)h $($elapsed.minutes)m $($elapsed.seconds)s."