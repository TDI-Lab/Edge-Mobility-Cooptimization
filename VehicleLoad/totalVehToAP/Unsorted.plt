set terminal pngcairo transparent enhanced font "arial,10" fontscale 1.0 size 600, 400 
set output 'vehicle load over APs.png'
#set key fixed right top vertical Right noreverse enhanced autotitle box lt black linewidth 1.000 dashtype solid
set title "Incomping traffic to the Munich selected area for one hour (3600 timestamp)"
#set datafile sep','
#set boxwidth 0.5
set style fill solid
set ytics textcolor "red"
set ytics nomirror
set xtics 50 nomirror
set tics out
set yrange [0:120000]
set xrange [0:468]
set xlabel "Access Point id" textcolor lt -1
set ylabel "Load (number of vehicles)"
set xtics ("0" 0, "100" 100, "200" 200, "300" 300, "400" 400, "500" 500)
#plot "traffic-pattern.txt" using ($0/10.):($1*4) with lines
plot "VehPerAP.dat" with lines notitle
set output