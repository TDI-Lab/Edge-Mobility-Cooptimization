set terminal pngcairo transparent enhanced font "arial,10" fontscale 1.0 size 600, 400 
set output 'unsorted-workload.png'
set title "Number of connected vehicles to each access point for Munich selected area"
set xrange[0:3600] 
set yrange[0:467]
set tics out
set tics nomirror
set palette rgb 33,13,10
set xlabel "Time (second)" textcolor lt -1
set ylabel "Access point id"
#set palette model CMY rgbformulae 7,5,15
set cbrange [0:68]
plot "VehPerAP.dat" with image
set output