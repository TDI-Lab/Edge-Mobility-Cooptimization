set terminal pngcairo transparent enhanced font "arial,10" fontscale 1.0 size 600, 400 
set output 'Travel-time.png'
#set key fixed right top vertical Right noreverse enhanced autotitle box lt black linewidth 1.000 dashtype solid
set title "Travel time of the vehicles in the Munich selected area"
#set datafile sep','
#set boxwidth 0.5
set style fill solid
set ytics textcolor "red"
set ytics nomirror
set xtics 50 nomirror
set tics out
set yrange [0:3600]
set xrange [0:5946]
set xlabel "Vehicle id (sorted based on the entrance time to the area)" textcolor lt -1
set ylabel "Vehicle entrance-exit (second)"
set xtics ("0" 0, "1000" 1000, "2000" 2000, "3000" 3000, "4000" 4000, "5000" 5000, "6000" 6000)
#plot "traffic-pattern.txt" using 0:1 with lines
plot "VehTravels.dat" using ($0 + 1):3 with lines notitle,
"VehTravels.dat" using ($0 + 1):2 with lines notitle,
set output