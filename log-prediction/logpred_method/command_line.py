from sys import argv

import logpred_method


def main():
    model_name = argv[1]
    csv_path = argv[2]
    logpred_method.run(model_name=model_name, csv_path=csv_path)
