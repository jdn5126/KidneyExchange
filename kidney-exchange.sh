#!/bin/bash

function usage
{
    echo "Usage: kidney-exchange.sh [-a <algorithm>] [-h] [-i] [-m <max surgeries>] [-n <number hospitals>] [-p <number pairs>] [-q] [-r <number rounds>] [-s <RNG seed>] [-t]"
    echo
    echo "Driver for Kidney Exchange program on PowerShell or PowerShell Core. Requires at least JRE 1.8."
    echo
    echo "    -a <algorithm>           Matching algorithm to use for kidney exchange. Choose either ILP or Greedy."
    echo "    -h                       Show this usage and exit."
    echo "    -i                       Disable incremental setting."
    echo "    -m <max surgeries>       Max surgeries that can be performed per hospital per time step."
    echo "    -n <number hospitals>    Number of hospitals participating in the exchange."
    echo "    -p <number pairs>        Initial number of pairs per hospital."
    echo "    -q                       Enable quiet mode."
    echo "    -r <number rounds>       Number of time steps to run the kidney exchange for."
    echo "    -s <RNG seed>            Set the RNG seed for the kidney exchange."
    echo "    -t                       Enable test output."
    echo
}

SEED=
HOSPITALS=
PAIRS=
SURGERIES=
ROUNDS=
ALGORITHM=
QUIET=
TEST=
DISABLE_INCREMENTAL=

while getopts "s:n:p:m:r:a:qtih" OPTION; do
    case "$OPTION" in
        a)
            ALGORITHM_VALUE=`echo "$OPTARG" | tr a-z A-Z`
            if [ "$ALGORITHM_VALUE" != "GREEDY" ] && [ "$ALGORITHM_VALUE" != "ILP" ]; then
                echo "Invalid algorithm $OPTARG given. Must be Greedy or ILP."
                exit 1
            fi
            ALGORITHM="-a $ALGORITHM_VALUE"
            ;;
        h)
            usage
            exit 0
            ;;
        i)
            DISABLE_INCREMENTAL="-i"
            ;;
        m)
            if [ "$OPTARG" -eq 0 ] || ! [[ "$OPTARG" =~ ^[0-9]+$ ]]; then
                echo "Invalid max number of surgeries: $OPTARG"
                exit 1
            fi
            SURGERIES="-m $OPTARG"
            ;;
        n)
            if [ "$OPTARG" -eq 0 ] || ! [[ "$OPTARG" =~ ^[0-9]+$ ]]; then
                echo "Invalid number of hospitals: $OPTARG"
                exit 1
            fi
            HOSPITALS="-n $OPTARG"
            ;;
        p)
            if [ "$OPTARG" -eq 0 ] || ! [[ "$OPTARG" =~ ^[0-9]+$ ]]; then
                echo "Invalid number of pairs: $OPTARG"
                exit 1
            fi
            PAIRS="-p $OPTARG"
            ;;
        q)
            QUIET="-q"
            ;;
        r)
            if [ "$OPTARG" -eq 0 ] || ! [[ "$OPTARG" =~ ^[0-9]+$ ]]; then
                echo "Invalid number of rounds $OPTARG"
                exit 1
            fi
            ROUNDS="-r $OPTARG"
            ;;
        s)
            if ! [[ $OPTARG =~ '^[0-9]+$' ]] ; then
                echo "Invalid seed $OPTARG"
                exit 1
            fi
            SEED="-s $OPTARG"
            ;;
        t)
            TEST="-t"
            ;;
        *)
            usage
            exit 1
            ;;
    esac
done

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
KIDNEY_JAR="$SCRIPT_DIR/out/artifacts/KidneyExchange_jar/KidneyExchange.jar"

if [ ! -f "$KIDNEY_JAR" ]; then
    echo "Missing Kidney Exchange JAR file $KIDNEY_JAR. Please build the artifact with the IntelliJ project first."
    exit 1
fi

java -jar $KIDNEY_JAR $ALGORITHM $DISABLE_INCREMENTAL $SURGERIES $HOSPITALS $PAIRS $QUIET $ROUNDS $SEED $TEST
