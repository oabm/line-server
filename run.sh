#!/usr/bin/env bash
PROJECT_ROOT=$(dirname "$0")

GREEDY=""

if [[ "$#" -lt 1 ]]
then
  echo "Must provide filename to serve as first argument!"
  exit 1
fi

FILENAME=$1
shift

while [[ "$#" -gt 0 ]]
do
  case $1 in
    --greedy | -g)
    GREEDY=-DfileReaderClass=services.file.GreedyFileReader
    ;;
    *)
    echo "Unknown option: '$1'!"
    exit 1
  esac
  shift
done

${PROJECT_ROOT}/target/universal/stage/bin/lineserver -DfileName=${FILENAME} ${GREEDY}
