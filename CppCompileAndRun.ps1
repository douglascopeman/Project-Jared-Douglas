param ($N = $(throw "N parameter is required for Cpp."), $dt = $(throw "dt parameter is required for Cpp."))

# Clear-host
Set-Location CPlusPlus
g++ -o run *.cpp
.\run.exe $N $dt
Set-Location ..
write-host "Done!"