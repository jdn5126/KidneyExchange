#!/usr/bin/env python

import argparse
import itertools
import os
import random
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
    for algorithm in ("GREEDY", "ILP", "LOCAL_ILP"):
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

def graph_results( args, results_dir ):
    pass

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
