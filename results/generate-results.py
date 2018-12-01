#!/usr/bin/env python

import argparse
import itertools
import json
import matplotlib.pyplot as plt
import numpy
import os
import pathlib
import random
import re
import subprocess

SCRIPT_DIR = os.path.dirname( os.path.abspath( __file__ ) )
KIDNEY_JAR = os.path.join( SCRIPT_DIR, "..\\out\\artifacts\\KidneyExchange_jar\\KidneyExchange.jar" )

def make_directory( directory_path ):
    try:
        os.mkdir( directory_path )
    except FileExistsError:
        pass

def random_seed( seed_for_seed ):
    random.seed( seed_for_seed )
    while True:
        yield random.randrange( 2 ** 32 - 1 )

def run_kidney_exchange( results_dir, seed, number_hospitals, max_surgeries, number_pairs, sample ):
    for algorithm in ("GREEDY", "LOCAL_ILP"):
        config_dir = os.path.join( results_dir, "algorithm_%s_surgeries_%d_pairs_%d" % (algorithm, max_surgeries, number_pairs) )
        make_directory( config_dir )
        results_file = os.path.join( config_dir, "results_sample_%d.json" % sample )
        print( "Running: %s" % results_file )
        subprocess.run( ["java", "-jar", KIDNEY_JAR,
            "-q", "-t", "--no-test-printing",
            "-a", algorithm,
            "-s", str(seed),
            "-n", str(number_hospitals),
            "-m", str(max_surgeries),
            "-p", str(number_pairs),
            "-o", results_file] )

def generate_results( args, results_dir ):
    make_directory( results_dir )

    seed_generator = random_seed( args.rng_seed )
    max_surgeries_range = range( args.max_surgeries_lower, args.max_surgeries_upper + 1 )
    number_pairs_range = range( args.pairs_lower, args.pairs_upper + 1 )
    samples_range = range( args.samples )

    for exchange_args in itertools.product( max_surgeries_range, number_pairs_range, samples_range ):
        run_kidney_exchange( results_dir, next( seed_generator ), args.hospitals, *exchange_args )

def pair_index( number_pairs, args ):
    return number_pairs - args.pairs_lower

def graph_results( args, results_dir ):
    pairs_domain = [p for p in range( args.pairs_lower, args.pairs_upper + 1 )]
    run_duration_range = {
        ("LOCAL_ILP", 3) : [None] * len( pairs_domain ),
        ("LOCAL_ILP", 4) : [None] * len( pairs_domain ),
        ("LOCAL_ILP", 5) : [None] * len( pairs_domain ),
        ("GREEDY", 3) : [None] * len( pairs_domain ),
        ("GREEDY", 4) : [None] * len( pairs_domain ),
        ("GREEDY", 5) : [None] * len( pairs_domain )
    }

    operation_rate_range = {
        ("LOCAL_ILP", 3) : [None] * len( pairs_domain ),
        ("LOCAL_ILP", 4) : [None] * len( pairs_domain ),
        ("LOCAL_ILP", 5) : [None] * len( pairs_domain ),
        ("GREEDY", 3) : [None] * len( pairs_domain ),
        ("GREEDY", 4) : [None] * len( pairs_domain ),
        ("GREEDY", 5) : [None] * len( pairs_domain )
    }

    results_dir_path = pathlib.Path( results_dir )
    test_run_pattern = re.compile( r'^algorithm_(?P<algorithm>[A-Z_]+)_surgeries_(?P<max_surgeries>\d+)_pairs_(?P<number_pairs>\d+)$' )
    for test_run_path in results_dir_path.iterdir():
        matches = test_run_pattern.match( test_run_path.name )
        algorithm, max_surgeries, number_pairs = matches.group( "algorithm", "max_surgeries", "number_pairs" )
        max_surgeries = int(max_surgeries)
        number_pairs = int(number_pairs)

        run_durations = []
        operation_rates = []

        for sample_path in test_run_path.glob( "*.json" ):
            with open( sample_path, "r" ) as sample_file:
                run_data = json.load( sample_file )
                run_durations.append( run_data['runDuration'] )
                operation_rates.append( run_data['operationRate'] )

        run_duration_range[(algorithm, max_surgeries)][pair_index( number_pairs, args )] = (numpy.mean( run_durations ) / 1e9)
        operation_rate_range[(algorithm, max_surgeries)][pair_index( number_pairs, args )] = numpy.mean( operation_rates )

    print( pairs_domain )
    print( run_duration_range )
    print( operation_rate_range )

    fig, ax = plt.subplots( figsize=(11,6) )
    for (algorithm, max_surgeries) in run_duration_range:
        ax.plot( pairs_domain, run_duration_range[(algorithm, max_surgeries)],
            label=("%s - %d Max Surgeries" % (algorithm, max_surgeries)) )
    ax.set_yscale( 'log' )
    ax.set_xlabel( "Initial Number Pairs per Hospital" )
    ax.set_ylabel( "Runtime (sec)" )
    ax.grid( True )
    ax.set_title( "Kidney Exchange Runtime (sec)" )
    ax.legend( loc='upper left', bbox_to_anchor=(1,1) )
    plt.subplots_adjust( right=0.7 )
    plt.show()

    fig, ax = plt.subplots( figsize=(11,6) )
    for (algorithm, max_surgeries) in run_duration_range:
        ax.plot( pairs_domain, operation_rate_range[(algorithm, max_surgeries)],
            label=("%s - %d Max Surgeries" % (algorithm, max_surgeries)) )
    ax.set_xlabel( "Initial Number Pairs per Hospital" )
    ax.set_ylabel( "Patient Pair Proportion" )
    ax.grid( True )
    ax.set_ylim( 0, 1 )
    ax.set_title( "Proportion of Patient Pairs Successfully Operated On" )
    ax.legend( loc='upper left', bbox_to_anchor=(1,1) )
    plt.subplots_adjust( right=0.7 )
    plt.show()

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument( "-m", "--max-surgeries-lower",
        type=int,
        default=3,
        help="Lower bound on max number of surgeries." )

    parser.add_argument( "-M", "--max-surgeries-upper",
        type=int,
        default=10,
        help="Upper bound on max number of surgeries." )

    parser.add_argument( "-s", "--samples",
        type=int,
        default=5,
        help="Number of runs to perform for each configuration. This is to generate an average." )

    parser.add_argument( "-p", "--pairs-lower",
        type=int,
        default=10,
        help="Lower bound on number of pairs to assign to each hospital." )

    parser.add_argument( "-P", "--pairs-upper",
        type=int,
        default=25,
        help="Upper bound on number of pairs to assign to each hospital." )

    parser.add_argument( "-n", "--hospitals",
        type=int,
        default=5,
        help="Number of hospitals in the exchange." )

    parser.add_argument( "-r", "--rounds",
        type=int,
        default=5,
        help="Number of hospitals in the exchange." )

    parser.add_argument( "-g", "--graph-only",
        action="store_true",
        help="Skip generation data and use existing data from previous run. Graph results only." )

    parser.add_argument( "-b", "--rng-seed",
        type=int,
        default=0,
        help="RNG seed to use when generating RNG seeds for the kidney exchange." )

    args = parser.parse_args()
    results_dir = os.path.join( SCRIPT_DIR, "results" )
    print( args )

    if not args.graph_only:
        generate_results( args, results_dir )

    graph_results( args, results_dir )
