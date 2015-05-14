#!/bin/sh

BASE=`dirname $0`
java -jar -Djava.library.path="$BASE/lib/" "$BASE/TrafficCoordination-Visualization.jar" &
