# param ($N = $(throw "N parameter is required."), $dt = $(throw "dt parameter is required."))

Clear-host
Set-Location CPlusPlus
g++ -o run *.cpp
.\run.exe
Set-Location ..
write-host "Done!"