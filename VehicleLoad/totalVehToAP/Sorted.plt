set terminal pngcairo transparent enhanced font "arial,10" fontscale 1.0 size 600, 400 
set output 'sorted vehicle load over APs.png'
#set key fixed right top vertical Right noreverse enhanced autotitle box lt black linewidth 1.000 dashtype solid
set title "Sorted incomping traffic to the Munich selected area for one hour (3600 timestamp)"
set style fill solid
set ytics textcolor "red"
set ytics nomirror
#set xtics 50 nomirror
set tics out
set yrange [0:120000]
set xrange[0:467]
unset xtics
set xlabel "Access Point" textcolor lt -1
set ylabel "Load (number of vehicles)"
plot "SortedVehPerAP.dat" with lines notitle
set output