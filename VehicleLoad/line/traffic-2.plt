set terminal pngcairo transparent enhanced font "arial,10" fontscale 1.0 size 600, 400 
set output 'vehicle load.png'
#set key fixed right top vertical Right noreverse enhanced autotitle box lt black linewidth 1.000 dashtype solid
set title "Incomping traffic to the Munich selected area"
#set datafile sep','
#set boxwidth 0.5
set style fill solid
set ytics textcolor "red"
set ytics nomirror
set xtics 50 nomirror
set tics out
set yrange [0:1500]
set xrange [0:3600]
set xlabel "Time (second)" textcolor lt -1
set ylabel "Load (number of vehicles)"
set xtics ("0" 0, "600" 600, "1200" 1200, "1800" 1800, "2400" 2400, "3000" 3000, "3600" 3600)
#plot "traffic-pattern.txt" using ($0/10.):($1*4) with lines
plot "VehPerTS.dat" with lines notitle
set output