param ($N = $(throw "N parameter is required for Cpp."), $dt = $(throw "dt parameter is required for Cpp."))

# Clear-host
Set-Location CPlusPlus
g++ -mavx -o Run *.cpp
.\Run.exe $N $dt
Set-Location ..
write-host "Done!"