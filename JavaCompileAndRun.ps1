param ($N = $(throw "N parameter is required."), $dt = $(throw "dt parameter is required."))

javac .\JavaSimulation\*.java
java javasimulation/Run $N $dt
write-host "Done!"