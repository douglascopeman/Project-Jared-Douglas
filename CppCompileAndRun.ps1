# param ($N = $(throw "N parameter is required."), $dt = $(throw "dt parameter is required."))

clear
cd CPlusPlus
g++ -o run *.cpp
.\run.exe
cd ..