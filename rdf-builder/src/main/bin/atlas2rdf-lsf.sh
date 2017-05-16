#!/bin/bash

experimentdir=/nfs/public/ro/fg/atlas/experiments
output=/nfs/production3/linked-data/expressionatlas/13-02-2017
group="/expressionatlasrdf";

bgadd -L 5 $group;
bgmod -L 5 $group;

for f in `ls -d1 ${experimentdir}/E-* | xargs -n1 basename`
do
# echo "Processing $f"
 # do something on $f
 bsub -o output-lsf.txt -e error-lsf.txt -q research-rh7 -M 4000 -R "rusage[mem=4000]" -g $group ./atlas2rdf.sh -in $experimentdir -out $output -acc $f
done
